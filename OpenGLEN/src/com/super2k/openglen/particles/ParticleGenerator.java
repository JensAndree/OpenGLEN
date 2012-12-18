/* Copyright 2012 Richard Sahlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.super2k.openglen.particles;

import java.util.Random;
import java.util.Vector;

import android.util.Log;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.animation.LinearAnimation;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;
import com.super2k.openglen.texture.Texture2D;
import com.super2k.openglen.texture.TextureHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Matrix;

/**
 * Class that emits and renders particles.
 * This class holds both renderer and particleemitter.
 * Call renderParticles() method and particles will be renderer
 * and the emitter will be called to emit new particels.
 * Use functions in this class to control color, position, direction
 * etc of emitted particles.
 * @author Richard Sahlin
 *
 */
public class ParticleGenerator {

    private final String TAG = getClass().getSimpleName();
    private final static String INVALID_PARAMETER_STR = "Invalid parameter.";

    /**
     * ParticleEmitter to handle emitting of new particles, called periodically to release
     * the correct number of particles per second.
     */
    protected ParticleEmitter mParticleEmitter;

    /**
     * The renderer for this particle generator.
     */
    protected Renderer mRenderer;

    /**
     * Width of renderer area
     */
    protected int mWidth;

    /**
     * Height of renderer area
     */
    protected int mHeight;

    /**
     * Number of particles to be released, if emitter does not release
     * the requested number the remaining particles are left for next pass.
     */
    protected int mEmitCount;

    /**
     * Max number of particles to release per millisecond, default is -1 ie no check is made.
     */
    protected int mMaxEmitRate = -1;

    /**
     * Number of particles, this is the max number of active particles.
     */
    protected int mParticleCount;

    /**
     * Animation to cycle background color.
     */
    private LinearAnimation mBGCycleAnim;
    private float[] mBgColor = new float[] { 0, 0, 0, 0 }; //Target bg color.

    /**
     * Number of particles released per millisecond.
     */
    protected float mParticleEmitRate = 0; //Emit particles per millis.
    /**
     * Default particle speed, Units per second.
     */
    public final static float DEFAULT_PARTICLE_SPEED = 100;
    /**
     * Default z , Negative values are into the screen.
     */
    public final static float DEFAULT_PARTICLE_START_Z = -500;

    /**
     * Default particle global size.
     */
    public final static float DEFAULT_PARTICLE_SIZE = 1;

    /**
     * Default (global) particle intensity
     */
    public final static float DEFAULT_PARTICLE_INTENSITY = 1;

    private final Random rand = new Random(0761442435);

    protected GLParticleArray mParticleArray;
    protected Vector<GLParticleArray> mList = new Vector<GLParticleArray>();
    protected Vector<GLBlitObject> mFBOList = new Vector<GLBlitObject>();
    private int mParticleOffset = 0;
    private final float[] mFBOMatrix = new float[16];
    private final float[] mScreenMatrix = new float[16];
    private final float mPictureZ = -0.01f;

    /**
     * *******************************************
     * Objects for handling offscreen rendering.
     * *******************************************
     */
    /**
     * Number of framebuffer objects.
     */
    private final int mFBOCount = 2;
    /**
     * The framebuffer objects
     */
    protected int[] mFrameBufferObjects = new int[mFBOCount];
    /**
     * Texturenames for the framebuffer objects
     */
    protected int[] mTextureObjects = new int[mFBOCount];

    protected int[] mFBOShading = new int[] { Material.SHADING_UNLIT, Material.SHADING_UNLIT };
    /**
     * Blitobjects to display the framebuffer textures.
     */
    protected GLBlitObject[] mFBOBlits = new GLBlitObject[mTextureObjects.length];
    /**
     * Width and height of the framebuffers, set when the framebuffers are created.
     * Do not change without re-creating the framebuffers.
     */
    protected int mFBOWidth, mFBOHeight;

    /**
     * Format to use for framebuffer objects.
     */
    protected int mFBOFormat = ConstantValues.RGBA;

    /**
     * Type to use for framebuffer objects.
     */
    protected int mFBOType = ConstantValues.UNSIGNED_BYTE;

    /**
     * Flag to toggle use of offscreen rendering.
     */
    protected boolean mUseFBO = false;

    /**
     * The texturehandle to be used.
     */
    protected TextureHandler mThandler;

    /**
     * The graphicslibrary handler to be used.
     */
    protected GraphicsLibraryHandler mGLHandler;

    /**
     * Perspective matrix
     */
    protected float[] mPerspectiveMatrix = new float[16];

    /**
     * Sets up the particles and renderbuffers needed.
     * @param renderer The renderer to render the particles.
     * @param particles Number of particles to create storage for.
     * @param particleType One of the types in {@linkGLParticleArray}
     * @param FBOWidth Width of offscreen rendering
     * @param FBOHeight Height of offscreen rendering
     * @param width Width of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param height Height of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param useVBO True to use VBOs
     * @throw IllegalArgumentException If the framebuffer width
     * or height is negative, if renderer is null
     */
    public ParticleGenerator(Renderer renderer, int particles, int particleType, int FBOWidth,
                            int FBOHeight, int width, int height, boolean useVBO) {

        setupParticleGenerator(renderer, particles, particleType,
                               FBOWidth, FBOHeight, width, height, useVBO);

    }

    /**
     * Creates a particlegenerator and renderbuffers, if needed.
     * The particleImage parameter is used as an image for particle
     * colors. As particles move across the screen their color will
     * change according to the texture color at the corresponding position.
     * @param renderer
     * @param particles
     * @param particleImage
     * @param FBOWidth
     * @param FBOHeight
     * @param width Width of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param height Height of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param useVBO
     */
    public ParticleGenerator(Renderer renderer, int particles, Texture2D particleImage,
                            int FBOWidth, int FBOHeight, int width, int height, boolean useVBO) {

        setupParticleGenerator(renderer, particles, GLParticleArray.TYPE_IMAGE_PARTICLE,
                                FBOWidth, FBOHeight, width, height, useVBO);
        mParticleArray.setParticleImage(particleImage);

    }

    /**
     * Sets the particle type to TYPE_IMAGE_PARTICLE and uses
     * the specified texture as a lookup for particle color on screen.
     * @param particleImage The image to lookup particle colors from.
     * @throws IllegalArgumentException If particleImage is null
     */
    public void setParticleImage(Texture2D particleImage) {
        mParticleArray.setParticleType(GLParticleArray.TYPE_IMAGE_PARTICLE);
        mParticleArray.setParticleImage(particleImage);
    }

    /**
     * Sets the particle emitter
     * @param emitter
     * @throws IllegalArgumentException If emitter is null
     */
    public void setEmitter(ParticleEmitter emitter) {
        if (emitter==null) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mParticleEmitter = emitter;
    }

    /**
     * Sets up the particles and renderbuffers needed.
     * This method must be called before processFrame is called.
     *
     * @param particles Number of particles to create storage for.
     * @param particleType Type of particle, must be one of the
     * types defined in {@linkGLParticleArray}
     * @param FBOWidth Width of offscreen rendering, or zero for no offscreen buffer
     * @param FBOHeight Height of offscreen rendering, or zero for no offscreen buffer
     * @param width Width of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param height Height of render area in pixels, frustum setup from this and particles
     * emitted based on this.
     * @param useVBO True to use VBOs
     * @throw IllegalArgumentException If emitter or renderer is null.
     * If the framebuffer width or height is negative.
     * If particleType is invalid.
     */
    public void setupParticleGenerator(Renderer renderer,
            int particles,
            int particleType,
            int FBOWidth, int FBOHeight, int width, int height,
            boolean useVBO) {

        if (FBOWidth <0 || FBOHeight < 0 || renderer==null) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        switch (particleType) {
            case GLParticleArray.TYPE_IMAGE_PARTICLE:
            case GLParticleArray.TYPE_UNLIT_PARTICLE:
            case GLParticleArray.TYPE_SIMPLE_UNLIT_PARTICLE:
                break;

            default:
                throw new IllegalArgumentException("Invalid particle type: " + particleType);
        }
        mRenderer = renderer;
        mThandler = mRenderer.getTextureHandler();
        mParticleCount = particles;
        mGLHandler = mRenderer.getGraphicsUtilities();
        //Handle clearing of buffers here since it depends on FBO.
//        mRenderer.getRenderSetting().setClearFunction(ConstantValues.NONE);
//        mRenderer.getRenderSetting().setCullFace(ConstantValues.NONE);

        mFBOWidth = FBOWidth;
        mFBOHeight = FBOHeight;

        mWidth = width;
        mHeight = height;
        //Setup perspectivematrix
        float top = (float)Math.tan(45*Math.PI/360)*0.1f;
        Matrix.frustumM(mPerspectiveMatrix, 0,
                ((float)mWidth/mHeight)*(-top),
                ((float)mWidth/mHeight)*top,
                top, -top, 0.1f, 1000f);

        createFBOs(FBOWidth, FBOHeight, useVBO);

        try {
            initParticles(particles, particleType, useVBO);
        } catch (OpenGLENException glene) {
            //Failed convert to VBO.
            throw new IllegalArgumentException(glene);
        }

    }

    /**
     * Creates fbo objects with the specified width and height.
     * This method creates blit objects to use when rendering offscreen
     * mFBOBlits[0] is offscreen buffer with size of FBOs
     * mFBOBlits[1] is offscreen buffer with same size as screen.
     *
     * @param FBOWidth Width of fbo in pixels
     * @param FBOHeight Height of fbo in pixels
     * @param useVBO True to use VBO for offscreen buffers (vertex storage)
     * @throws IllegaArgumentException If Framebuffer object could not be created or convert to
     * VBO failed.
     */
    private void createFBOs(int FBOWidth, int FBOHeight, boolean useVBO) {
        if (FBOWidth > 0 && FBOHeight > 0) {
            /**
             * Setup FBO stuff
             */
            //Create framebuffer object names
            mThandler.genFrameBuffers(mTextureObjects.length, mFrameBufferObjects, 0);
            mThandler.generateTextureNames(mTextureObjects.length, mTextureObjects, 0);
            mThandler.activeTexture(ConstantValues.TEXTURE0);
            for (int i = 0; i<mFBOCount; i++) {
                mThandler.bindTexture2D(ConstantValues.TEXTURE_2D, mTextureObjects[i]);
                mThandler.generateTextureBuffer(0, mFBOWidth, mFBOHeight,
                        ConstantValues.RGBA,
                        ConstantValues.UNSIGNED_BYTE);
                mThandler.bindFrameBuffer(mFrameBufferObjects[i]);
                mThandler.frameBufferTexture2D(ConstantValues.COLOR_ATTACHMENT0,
                        ConstantValues.TEXTURE_2D,
                        mTextureObjects[i], 0);
                int result = mThandler.checkFrameBufferStatus();

                if (result!=ConstantValues.FRAMEBUFFER_COMPLETE) {
                    throw new IllegalArgumentException("Framebuffer status not OK: "+result);
                }

                /**
                 * Create the blitobjects to draw the offscreen buffers.
                 */
                if (i==0) {
                    mFBOBlits[i] = createOffscreenBlitObject(mFBOShading[i], mTextureObjects[i],
                                                            mFBOWidth, mFBOHeight);
                } else {
                    mFBOBlits[i] = createOffscreenBlitObject(mFBOShading[i], mTextureObjects[i],
                                                             mWidth, mHeight);
                }
                try {
                    if (useVBO) {
                        mRenderer.getGraphicsUtilities().convertToVBO(mFBOBlits[i]);
                    }
                } catch (OpenGLENException glene) {
                    //We can continue without VBOs but we don't want to - running without VBOs may
                    //affect performance.
                    throw new IllegalArgumentException(glene);
                }
                mFBOList.add(mFBOBlits[i]);

            }
            mFBOBlits[0].material.setBlendFunc(ConstantValues.ONE, ConstantValues.SRC_ALPHA);
            //Set the fbo matrix here
            Matrix.orthoM(mFBOMatrix, 0, 0, FBOWidth, FBOHeight, 0, -1f, 1000);
        }
        //Check if FBOs where created then set back framebuffer.
        if (mFBOCount > 0) {
            mThandler.bindFrameBuffer(0);
        }
    }

    /**
     * Init the particle generator with the specified number of particles.
     * This method must be called before any particles are emited.
     * @param particles Number of particles to allocate, this is the number
     * of particles that may/will be visible.
     * @param type Type of particles to create, TYPE_SIMPLE or TYPE_COLLIDING
     * @param useVBO True to convert arrays to VBO.
     * @throws IllegalArgumentException If type is invalid.
     * @throws OpenGLENException If particle array could not be converted to VBO.
     */
    public void initParticles(int particles, int type, boolean useVBO) throws OpenGLENException {

        mParticleArray = new GLParticleArray();
        mParticleArray.init(particles, type);
        mParticleArray.material.setDiffuse(0, 0, 0, 0);
        mParticleArray.material.setBlendFunc(ConstantValues.SRC_ALPHA,
                                             ConstantValues.ONE_MINUS_SRC_ALPHA);
        mParticleArray.data_uniform[GLParticleArray.SIZE_UNIFORM] = DEFAULT_PARTICLE_SIZE;
//        mParticleArray.data_uniform[GLParticleArray.INTENSITY_UNIFORM] = DEFAULT_PARTICLE_INTENSITY;

        float[] data = new float[GLParticleArray.PARTICLE_FLOAT_COUNT];
        data[GLParticleArray.POSITION] = -1000;
        data[GLParticleArray.POSITION+1] = -2000;
        data[GLParticleArray.POSITION+2] = 500;
        data[GLParticleArray.SIZE] = 0;

        mParticleArray.arrayBuffer.position(0);
        for (int i = 0; i < particles; i++) {
            mParticleArray.arrayBuffer.put(data);
        }

        mList.add(mParticleArray);
        if (useVBO) {
            mRenderer.getGraphicsUtilities().convertToVBO(mParticleArray);
        }

    }

    private GLBlitObject createOffscreenBlitObject(int shading, int textureName, int destWidth,
                                                   int destHeight) {

        GLBlitObject result;
        Texture2D tex = new Texture2D(textureName, mFBOFormat, mFBOType, mFBOWidth, mFBOHeight);
        result = new GLBlitObject(0, destHeight, mPictureZ, destWidth, -destHeight,
                new Texture2D[] { tex }, GLBlitObject.ANCHOR_LEFT | GLBlitObject.ANCHOR_TOP, 1);
        result.material.materialShading = shading;

        return result;
    }

    /**
     * Adds a number of particles to the array with the specified timeOffset.
     * @param particles Array to add particles to
     * @param count Number of particles to add
     * @param offset Offset into particles array (counted in particles)
     * @param timeOffset Time offset for particles to add.
     * @return New offset into particles array, checked for wrapround (MAX_PARTICLES)
     */
    public int addSnowflakes(GLParticleArray particles, int count, int offset, float timeOffset) {

        int pos = offset*GLParticleArray.PARTICLE_FLOAT_COUNT;
        float[] data = new float[] { 0, 0, 0, 1,
                0, 0, 0,
                1, 0, timeOffset };
        particles.arrayBuffer.position(pos);
        float r;
        int maxOffset = particles.getActiveParticles();
        for (int i = 0; i<count; i++) {
            data[GLParticleArray.POSITION] = rand.nextFloat()*mWidth;
            //            data[GLParticleArray.POSITION + 1] = rand.nextFloat() * mHeight;
            r = rand.nextFloat();
            data[GLParticleArray.VELOCITY+1] = -r*0.2f-0.03f;
            data[GLParticleArray.SIZE] = 1f;
            particles.arrayBuffer.put(data);
            offset++;
            if (offset>=maxOffset) {
                offset = 0;
                pos = 0;
                particles.arrayBuffer.position(0);
            }
        }
        return offset;
    }

    /**
     * Renders one frame
     */
    public void renderParticles() {

        /**
         * FBO can be toggled on or off so must set proper setting
         * both for enabled and disabled.
         */
        if (mUseFBO) {
            //Render to texture.
            mRenderer.setViewPort(0, 0, mFBOWidth, mFBOHeight);
            mThandler.bindFrameBuffer(mFrameBufferObjects[0]);

        } else {
            mRenderer.setViewPort(0, 0, mWidth, mHeight);
            mThandler.bindFrameBuffer(0);
        }
        mGLHandler.clearBuffer(ConstantValues.COLOR_BUFFER_BIT, 0, mBgColor, 0, 0);
        mRenderer.setPerspectiveMatrix(mPerspectiveMatrix);
        mRenderer.renderGLParticleArray(mList);

        if (mUseFBO) {

            /**
             * Render to FBO 2
             * Turn off depth test and render.
             */
            mGLHandler.disable(ConstantValues.DEPTH_TEST);
            mRenderer.setPerspectiveMatrix(mFBOMatrix);
            mFBOBlits[0].renderFlag = true;
            mFBOBlits[1].renderFlag = false;
            mThandler.bindFrameBuffer(mFrameBufferObjects[1]);
            mThandler.bindTexture2D(ConstantValues.TEXTURE_2D, mTextureObjects[0]);
            mRenderer.renderGLBlitObjects(mFBOList);
            /**
             * Render texture to display.
             */
            mFBOBlits[0].renderFlag = false;
            mFBOBlits[1].renderFlag = true;
            mRenderer.setPerspectiveMatrix(mScreenMatrix);
            mRenderer.setViewPort(0, 0, mWidth, mHeight);
            mThandler.bindFrameBuffer(0);
            mThandler.bindTexture2D(ConstantValues.TEXTURE_2D, mTextureObjects[1]);
            mRenderer.renderGLBlitObjects(mFBOList);
            /**
             * Set back depth test, if depthfunc is not NONE.
             */
            int deptThest = mRenderer.getRenderSetting().getDepthFunc();
            if (deptThest!=ConstantValues.NONE) {
                mGLHandler.enable(ConstantValues.DEPTH_TEST);
            }
        }

        int ticks = mRenderer.getProfileInfo().getFrameTicks();
        int millis = ticks/1000;
        if (millis>100) {
            millis = 100;
        }
        float time = (float)millis/1000;
        if (mBGCycleAnim!=null) {
            mBGCycleAnim.animate(time);
        }
        //Will be set next time frame.
        mRenderer.getRenderSetting().setClearColor(mBgColor);

        //Check if any particles should be emitted.
        if (mParticleEmitter != null) {

            mParticleArray.data_uniform[GLParticleArray.TIME_UNIFORM] += time;
            mEmitCount += (int)(mParticleEmitRate * millis);
            int release = mEmitCount;
            int max = mMaxEmitRate * millis;
            if (mMaxEmitRate >= 0 && release > max) {
                release = max;
            }
            release = mParticleEmitter.emitParticles(mParticleArray,
                    release,
                    mParticleOffset,
                    mParticleArray.getActiveParticles(),
                    mParticleArray.data_uniform[GLParticleArray.TIME_UNIFORM]);
            mEmitCount -= release;
            /**
             * Update mParticleOffset.
             */
            mParticleOffset += release;
            if (mParticleOffset>=mParticleArray.getActiveParticles()) {
                mParticleOffset -= mParticleArray.getActiveParticles();
            }

        }

    }

    /**
     * Sets the array holding the background color, this will replace the reference to the
     * background color. Any changes to the array will be visible.
     * If colorcycling is ongoing or started after a call to this method then the
     * background color will be changed by the cycling.
     * @param backgroundColor The new background color array.
     * @throws IllegalArgumentException If backgroundColor is null or does not contain
     * at least 4 values.
     */
    public void setBackgroundColor(float[] backgroundColor) {
        if (backgroundColor==null||backgroundColor.length<4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mBgColor = backgroundColor;
    }

    /**
     * Returns the array holding background color. If clear screen is turned on
     * in the rendersettings this is the color the background will be cleared
     * to.
     * The returned array is holding the values that are written to background,
     * any changes to the array will be reflected.
     * @return The array holding the background colors, 4 values. R,G,B,A
     */
    public float[] getBackgroundColor() {
        return mBgColor;
    }

    /**
     * Set the background color to cycle, from the current background color to the endColor.
     * @param endColor The end color
     * @param startTime Start time (usually 0)
     * @param endTime End time End time of animation.
     * @param loop LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     */
    public void setBackgroundColorCycle(float[] endColor, float startTime, float endTime,
            int loop) {

        if (mBGCycleAnim==null) {
            mBGCycleAnim = new LinearAnimation(mBgColor, mBgColor, endColor, startTime, endTime,
                    loop);
        } else {
            mBGCycleAnim.setup(mBgColor, mBgColor, endColor, startTime, endTime, loop);
        }
    }

    /**
     * Stops background color cycle animation.
     */
    public void stopBackgroundColorCycle() {
        mBGCycleAnim = null;
    }

    /**
     * Sets the emit rate for particles.
     * @param particlesPerMillis
     * @throws IllegalArgumentException If particlesPerMillis is negative.
     */
    public void setParticleEmitRate(float particlesPerMillis) {
        if (particlesPerMillis < 0) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR+", "+particlesPerMillis);
        }
        mParticleEmitRate = particlesPerMillis;
    }

    /**
     * Sets the max number of particles to be released per millisecond.
     * Set to -1 to disable checking of max emit count.
     * This value is used if the emitter does not release all requested particles, these
     * are saved for next frame.
     * @param maxEmitPerFrame Max number of particles to release per millisecond.
     * or -1 to disable checking.
     */
    public void setMaxEmitRate(int maxEmitRate) {
        mMaxEmitRate = maxEmitRate;
    }

    /**
     * Sets the number of active particles,
     * this is the number of particles that are rendered.
     * @param particles Number of particles to render, > 0 and
     * less than the max number of particles.
     * @throws IllegalArgumentException If particles < 0 or greater
     * than the max number of particles
     */
    public void setActiveParticles(int particles) {
        if (particles < 0 || particles > mParticleCount) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR + particles);
        }
        mParticleArray.setActiveParticles(particles);
    }

    /**
     * Returns the number of active (processed) particles.
     * This is the number of particles that may be on screen, the actual number of visible particles
     * may be lower since some may be offscreen.
     * @return Number of active particles.
     */
    public int getActiveParticles() {
        return mParticleArray.getActiveParticles();
    }

    /**
     * Returns the max number of particles that can be rendered.
     * This is the number of particles that are allocated in the particle array.
     * @return Max number of particles.
     */
    public int getMaxParticles() {
        return mParticleCount;
    }

    /**
     * Set the gravity values for the particles
     * @param gravity
     * @param offset
     * @throw new IllegalArgumentException If gravity is NULL or
     * there is not 4 values at gravity + offset.
     */
    public void setGravity(float[] gravity, int offset) {
        if (gravity==null||gravity.length<offset+4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(gravity, offset, mParticleArray.data_uniform,
                GLParticleArray.GRAVITY_UNIFORM_INDEX, 4);
    }

    /**
     * Sets the global particle intensity. This will affect all present and new particles.
     * The intensity is a value that the color will be multiplied with when particle is rendered.
     * Normally a value between 0 and 1.
     * @param intensity
     */
//    public void setParticleGlobalIntensity(float intensity) {
//        mParticleArray.data_uniform[GLParticleArray.INTENSITY_UNIFORM] = intensity;
//    }

    /**
     * Sets the global particle size, this is the minimum size of all particles
     * that are rendered.
     * @param size Size in pixels, to be added to particle size.
     */
    public void setParticleGlobalSize(float size) {
        mParticleArray.data_uniform[GLParticleArray.SIZE_UNIFORM] = size;
    }

    /**
     * Sets the blur factor for the specified fbo, the fbo must be set
     * to a blur material shader and fbo must be enabled.
     * @param fbo The fbo number 0 or 1.
     * @param xBlurFactor How much to step in x when reading texels, 1 or less.
     * Smaller values produce more blur.
     * @param yBlurFactor How much to step in y when reading texels, 1 or less.
     * Smaller values produce more blur.
     * @param weight Weight of the centerpixel, a value of 1 will weight this pixel same
     * as surrounding, a higher value will put more weight on the center.
     * @throws IllegalArgumentException If fbo is not a valid number, 0 or 1.
     */
    public void setFBOBlurFactor(int fbo, float xBlurFactor, float yBlurFactor, float weight) {
        if (fbo<0||fbo>1) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR+", "+fbo);
        }
        mFBOBlits[fbo].material.xBlurFactor = xBlurFactor;
        mFBOBlits[fbo].material.yBlurFactor = yBlurFactor;
        mFBOBlits[fbo].material.weightFactor = weight;

    }

    /**
     * Sets the shading for the specified FBO.
     * @param fbo The fbo number 0 or 1.
     * @param shading The material shading to use. Material.SHADING_XXX
     * @throws IllegalArgumentException If fbo is not a valid number, 0 or 1
     */
    public void setFBOShading(int fbo, int shading) {
        if (fbo<0||fbo>1) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR+", "+fbo);
        }
        mFBOBlits[fbo].material.materialShading = shading;

    }

    /**
     * Sets the type of particle.
     * @param type Must be one of the specified particle types.
     * TYPE_SIMPLE_UNLIT_PARTICLE
     * TYPE_IMAGE_PARTICLE
     * TYPE_UNLIT_PARTICLE
     * @throws IllegalArgumentException If type is not a valid particle type
     */
    public void setParticleType(int type) {
        mParticleArray.setParticleType(type);
    }

    /**
     * Enables or disables rendering to FBOs, if enabled there will be a 2 pass offscreen render.
     *
     * @param enabled
     */
    public void setUseFBO(boolean enabled) {
        if (mFBOWidth>0&&mFBOHeight>0)
            mUseFBO = enabled;
    }

    /**
     * Returns the renderer used by the particle generator.
     * @return The renderer.
     */
    public Renderer getRenderer() {
        return mRenderer;
    }

    /**
     * Tears down all resources used by the ParticleGenerator
     * after this call returns it is not possible to use
     * the ParticleGenerator.
     * Call this method when you are exiting and will not use
     * the generator anymore.
     */
    public void destroy() {
        Log.d(TAG, "destroy()");
        if (mParticleArray != null) {
            if (mParticleArray.arrayVBOName > 0) {
                int[] vboname = new int[] { mParticleArray.arrayVBOName };
                mRenderer.getGraphicsUtilities().deleteVBOBuffer(vboname, 1, 0,
                                                                 ConstantValues.ARRAY_BUFFER);
                if (mParticleArray.material.texture != null) {
                    Texture2D[] tex = mParticleArray.material.texture;
                    for (int loop = 0; loop < tex.length; loop++) {
                        int[] name = new int[] { tex[loop].getTextureName() };
                        mRenderer.getGraphicsUtilities().deleteBuffers(1, name, 0);
                    }
                }
            }
            mParticleArray.destroy();
            mParticleArray = null;
        }
        if (mRenderer!=null) {
            mRenderer.destroy();
        } else {
            Log.d(TAG, "ParticleArray is null - cannot release.");
        }

    }
}
