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

package com.super2k.openglen.benchmark;

import java.io.IOException;
import java.util.Vector;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.nibbler.BaseCompatibilityRunner;
import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.nibbler.InputStreamResolver;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.test.PerformanceBenchmark;
import com.super2k.openglen.texture.Texture2D;
import com.super2k.openglen.texture.TextureUtils;
import com.super2k.openglen.utils.JavaUtils;
import com.super2k.openglen.utils.Log;
import com.super2k.openglen.utils.Logger;

/**
 * Class that has the common functions and variables needed to do bencmarking.
 * This is a helperclass that clients may extend to get access to common functions.
 * @author Richard Sahlin
 *
 */
public abstract class BaseBenchmarkProgram extends BaseCompatibilityRunner
                                           implements PerformanceBenchmark {

    /**
     * Tag used for general output that is same for all performance testcases.
     * Will output some debug info and the result of each performance test using this tag.
     */
    public final static String TAG = "SOMC_GL";

    /**
     * Property for setting the texture format used in a benchmark.
     * Note that not all benchmarks may read this property.
     * Valid values are the values in ConstantValues for LUMINANCE, LUMINANCE_ALPHA, RGB and
     * known compressed texture formats from CompressedTextureFormats.
     */
    public final static String PROPERTY_BENCHMARK_TEXTURE_FORMAT =
                            "com.super2k.openglen.benchmark";

    public final static String TEXTURE_FORMAT_LUMINACE = "LUMINANCE";
    public final static String TEXTURE_FORMAT_LUMINACE_ALPHA = "LUMINANCE_ALPHA";
    public final static String TEXTURE_FORMAT_RGB = "RGB";
    public final static String TEXTURE_FORMAT_RGBA_5551 = "RGB5551";

    /**
     * Defining the texture formats that can be used.
     */
    public final static String[] TEXTURE_FORMAT_NAME_TABLE = new String[] {
                                                                    TEXTURE_FORMAT_LUMINACE,
                                                                    TEXTURE_FORMAT_LUMINACE_ALPHA,
                                                                    TEXTURE_FORMAT_RGB,
                                                                    TEXTURE_FORMAT_RGBA_5551};
    /**
     * Values for texture formats.
     */
    public final static int[] TEXTURE_FORMAT_TABLE = new int[] {ConstantValues.LUMINANCE,
                                                                ConstantValues.LUMINANCE_ALPHA,
                                                                ConstantValues.RGB,
                                                                ConstantValues.RGB5_A1};

    /**
     * Number of millis to sleep after a memcleanup is performed.
     * This is sometimes needed to settle system after GC.
     * If not needed tests can change the value.
     */
    protected int mSleepAfterCleanup = 0;

    private boolean mInitialized = false; //set to true when initialized

    /**
     * Number of millis to sleep after test has settled, this is just before the benchmark
     * run starts.
     */
    protected int mSleepAfterSettled = 0;

    /**
     * The position for background, if depth test is not used then all objects
     * can be put at this position.
     */
    public final static int BACKGROUND_Z = -1;

    /**
     * Shading to be used by on objects, may not be used by some tests.
     */
    protected int mShading = Material.SHADING_COLORED;

    /**
     * Format to be used for bitmaps/textures, may not be used by some tests.
     * This is the format used when creating bitmaps that are used as texture sources.
     */
    protected int mBitmapFormat = BitmapHandler.FORMAT_ARGB8888;

    /**
     * If a specific texture format should be used, for instance force to LUMINANCE or
     * a compressed texture format.
     * Set to valid compressed texture extensions to enable using
     * compressed textures, then scalefactor will be disregarded.
     */
    protected int mTextureFormat;

    /**
     * Set if a specific texture format is set, eg a compressed texture or LUMINANCE etc.
     */
    protected Texture2D mTexture;


    /**
     * Background image
     */
    protected Object mBgBlit;


    /**
     * How long in number of frames that a performance run is.
     */
    protected int mPerformanceRunFrameCount = 250;

    /**
     * Delay between logging when a performance run is not started (mPerformanceRunFrameCount=0)
     */
    protected int mLogInterval = 150;

    /**
     * Number of times the frame threshold is checked before deciding that the
     * framerate has settled.
     */
    protected final int SETTLED_LIMIT = 1;

    /**
     * Number of frames in which the test can increase load if deltatime is below
     * threshold (for mFrameAverage number of frames)
     */
    protected int mMaxStabilizeFrames = 40;

    /**
     * Number of frames to base threshold on, a value of 3 will base frame threshold
     * on the average of 3 frames.
     * May be overridden by tests.
     */
    protected int mFrameAverage = 3;

    /**
     * Number of frames to skip before starting to measure stabilize framerate.
     * After increase load a number of frames may be needed to be skipped.
     */
    protected int mSkipFrames = 10;

    /**
     *  Increase load if ticks per frame is below this.
     * This is the number of milliseconds it takes to create and draw one frame.
     * A value of 20000 will settle at 50 fps.
     * Note that the average FPS for a performancerun will most likely be lower than this value
     * since the load will be increased if delta frametime is lower than the threshold.
     * This increase in load may be coarse, making the average FPS go down to a value lower
     * than the threshold.
     */
    protected int mFrametickThreshold = 17000; //

    /**
     * Reference to the profileinfo.
     */
    protected ProfileInfo mProfileInfo;

    /**
     * Start of performance measure.
     */
    protected long mMeasureStart;

    /**
     * Set to true to start counting frames.
     */
    protected boolean mCountFrames = true;

    /**
     * Count up when measuring performance run
     * This is the number of frames that has passed for
     * one performance run.
     */
    protected int mRunFramecount;
    protected int mFrameCount;
    protected int mStabilizeCount;
    /**
     * Flag to mark when fps has settled
     */
    protected int mSettled;
    /**
     * Set to true to increase load, if true the increaseLoad method will be called
     * when framerate is below threshold.
     */
    protected boolean mIncreaseLoad;

    protected int mTotalFrameTicks;

    protected int mMinDelta = 0; //Set this to force lower fps.

    private final Vector<PerformanceBenchmark.ProfileListener> mProfileListener
                  = new Vector<PerformanceBenchmark.ProfileListener>();

    /**
     * Default constructor for creating from classname.
     */
    public BaseBenchmarkProgram() {
        super();
    }

    /**
     * Creates a base class for programs that will do performance profiling/benchmarking.
     * @param bitmapFormat
     * @param performanceRun
     */
    public BaseBenchmarkProgram(int bitmapFormat, int performanceRun) {
        super();
        switch (bitmapFormat) {
            case BitmapHandler.FORMAT_ARGB8888:
            case BitmapHandler.FORMAT_ARGB4444:
            case BitmapHandler.FORMAT_RGB565:
                break;
            default:

                throw new IllegalArgumentException(
                        "Invalid value for bitmapFormat:" + bitmapFormat);
        }

        if (performanceRun < 0) {
            throw new IllegalArgumentException(
                    "Invalid value for performanceRun:" + performanceRun);
        }
        mBitmapFormat = bitmapFormat;
        mPerformanceRunFrameCount = performanceRun;
    }

    @Override
    public void setup(Renderer renderer, InputStreamResolver resolver, int width, int height) {
        //If second time this is called and mInitialized alredy is true then we must
        //add code to remove all already added objects in testcase.
        if (mInitialized == true) {
            throw new IllegalArgumentException("Initialized=true");
//            destroy();
        }

        super.setup(renderer, resolver, width, height);

        mCountFrames = true;
        mRunFramecount = 0;
        mFrameCount = 0;
        mStabilizeCount = 0;
        mSettled = 0;
        mIncreaseLoad = true;
        mTotalFrameTicks = 0;
        mProfileInfo = mRenderer.getProfileInfo();
        //Create per frame profilestorage for fps.
        mProfileInfo.createFrameStorage(mPerformanceRunFrameCount);
        //Make one first call to the profiler so we have a delta time next frame iteration.
        mProfileInfo.update(0, 0, 0, 0, 0);

        checkSystemProperties();
        //Always start with clearing the depth buffer.
        renderer.getGraphicsUtilities().clearBuffer(ConstantValues.DEPTH_BUFFER_BIT, 1, null, 0, 0);
        try {
            renderer.startRenderer();
        } catch (OpenGLENException glene) {
            throw new IllegalArgumentException(glene);
        }

    }

    /**
     * Sets the state to initialized.
     * Call this method from subclasses when all setup is done.
     */
    protected void initialize() {
        mInitialized = true;
    }

    /**
     * Returns the initialized status.
     * @return True if initialized, false otherwise.
     */
    protected boolean isInitialized() {
        return mInitialized;
    }

    /**
     * Check for config parameters sent by system property.
     */
    protected void checkSystemProperties() {

        //Check if a texture format is defined.
        String textureFormatStr = System.getProperty(PROPERTY_BENCHMARK_TEXTURE_FORMAT);
        if (textureFormatStr != null) {
            //If unknown texture format 0 will be returned - check luminance, rgb etc.
            mTextureFormat = TextureUtils.getCompression(textureFormatStr);
            if (mTextureFormat == TextureUtils.UNDEFINED_TEXTURE_FORMAT) {
                //Check for luminance, luminance_alpha and rgb formats
                mTextureFormat = getTextureFormat(textureFormatStr);
            }
        }
    }


    /**
     * Internal helper method.
     * Checks a string for texture format, LUMINANCE, LUMINANCE_ALPHA or RGB.
     * @return Value for texture format string.
     * @throws IllegalArgumentException If texture format is not one of
     * LUMINANCE, LUMINANCE_ALPHA or RGB
     */
    protected int getTextureFormat(String textureFormatStr) {

        for (int i = 0; i < TEXTURE_FORMAT_NAME_TABLE.length; i++) {
            if (textureFormatStr.equalsIgnoreCase(TEXTURE_FORMAT_NAME_TABLE[i])) {
                return TEXTURE_FORMAT_TABLE[i];
            }
        }
        throw new IllegalArgumentException("Invalid texture format: " + textureFormatStr);

    }

    /**
     * Internal method.
     * Returns a String for the specified texture format, LUMINANCE, LUMINANCE_ALPHA or RGB:
     * @param textureFormat
     * @return String for the texture format or null if not matching.
     */
    protected String getTextureFormatStr(int textureFormat) {

        for (int i = 0; i < TEXTURE_FORMAT_NAME_TABLE.length; i++) {
            if (textureFormat == TEXTURE_FORMAT_TABLE[i]) {
                return TEXTURE_FORMAT_NAME_TABLE[i];
            }
        }
        return null;
    }

    /**
     * Performs the profiling for this frame.
     * @param name Name of the caller, this is used for logging.
     * @return True if the test should continue, false if test has finished.
     */
    public boolean profileTest(String name) {

        if (mCountFrames) {
            mRunFramecount++;
            if (mPerformanceRunFrameCount > 0 && mRunFramecount > mPerformanceRunFrameCount) {
                mRunFramecount = 0;
                boolean hasMoreTests = nextTest(); //Start next test if there is any.
                if (!hasMoreTests) {
                    endTestRun();
                }
                return hasMoreTests;
            } else {
                //No performance run, output profileinfo
                if (Log.logProfileInfoInterval(TAG, mProfileInfo, mLogInterval,
                        Logger.LOGLEVEL_INFO)) {
                    //Time is in mikros and we want fillrate in M (1000000) pixels.
                    Log.logFillrate(TAG, (int)(((float)1 / mProfileInfo.getMinTicks()) * mBlitSize),
                            (int)(((float)1 / mProfileInfo.getMaxTicks()) * mBlitSize),
                            (int)(((float)1 / mProfileInfo.getAverageTicks()) * mBlitSize));
                    mProfileInfo.reset();
                }
            }
        }
        return true;
    }

    /**
     * Call this when you have finished running the testcase(s)
     * Subclasses can override this to change behavior.
     * Default behavior is to call finishTest in the superclass with
     * the profileinfo from the test.
     * This will in turn call any added OpenGLENTester.resultListener which
     * will propage the result back to listeners.
     */
    public void endTestRun() {
        finishTest(mProfileInfo);
    }

    /**
     * Dummy method to start next test, if test implementations want to
     * do more than one test in a run they can override this method.
     * Called by profileTest when next test should be executed.
     * Calls endTestRun to flag that this is the end of the test.
     * @return False to stop test.
     */
    public boolean nextTest() {
        outputTestResult(TAG);
        logFillrate(TAG);
        // Flag that we are finished with the tests.
        return false;

    }

    /**
     * Method to put more load on GPU and system, subclasses shall implement
     * this method to increase the complexity of the data rendered.
     * How this is done is up to implementations.
     * Note that the ProfileInfo may be reset before this method is called.
     * @throws OpenGLENException
     */
    public abstract void increaseLoad() throws OpenGLENException;


    /**
     * Measures the framerate, if a set number of frames has passed the frametime is
     * checked.
     * If average frametime is below a set value the method
     * increaseLoad() is called.
     * If framerate has settled nothing is done.
     * @throws OpenGLENException If there is an error calling increaseLoad()
     */
    public void stabilizeFramerate(int ticks) throws OpenGLENException{
        //First frame after start will be undefined, until we have framerate.
        if (ticks == 0) {
            return;
        }
        mTotalFrameTicks += ticks;
        mFrameCount++;
        if (mFrameCount < (mMaxStabilizeFrames + mSkipFrames) && mSettled < SETTLED_LIMIT) {
            //Fix to counter that some frames produced to quickly.
            if (mFrameCount > mSkipFrames) {
                mStabilizeCount++;
            }
            if (mStabilizeCount == mFrameAverage) {
                if ((mTotalFrameTicks/mStabilizeCount) < mFrametickThreshold) {
                    mFrameCount = 0;
                    if (mIncreaseLoad) {
                        increaseLoad();
                        JavaUtils.stabilizeFreeMemory();
                        try {
                            Thread.sleep(mSleepAfterCleanup);
                        }
                        catch (InterruptedException e) {
                            //cant do anything
                        }
                        mSettled = 0;
                        mProfileInfo.reset();
                    }
                }
                mStabilizeCount = 0;
                mTotalFrameTicks = 0;
            }

        } else {
            mTotalFrameTicks = 0;
            mFrameCount = 0;
            mStabilizeCount = 0;
            mSettled++;
            if (mSettled == SETTLED_LIMIT) {
                settled();
                JavaUtils.stabilizeFreeMemory();
                try {
                    Thread.sleep(mSleepAfterSettled);
                }
                catch (InterruptedException e) {
                    //cant do anything
                }
                mProfileInfo.reset();
                mCountFrames = true;
            }
        }
    }

    /**
     * Called when framerate has settled.
     * Log start of usecase and enable per frame profiling.
     * Implement in sublass to do expected behavior.
     */
    public void settled() {
        Log.d(TAG,  "Settled()");
        mIncreaseLoad = false;
        try {
            Thread.sleep(mSleepAfterSettled);
        } catch (InterruptedException e) {
            //Cant do anything
        }

        //Log that the framerate has settled.
        Log.logStartOfUsecase("Start of test");
        //Enable per frame profiling of framerate.
        mProfileInfo.setDrawSize(mBlitSize);
        mProfileInfo.setPerFrameProfiling(true);
    }

    /**
     * Sets the bitmap and texture format to be used for the test.
     * This can be used to select different Bitmap sources for textures, not relevant
     * if a compressed texture or specific texture format is used.
     * Valid values are
     * FORMAT_ARGB8888
     * FORMAT_RGB565
     * FORMAT_ARGB4444
     * @param bitmapFormat
     * @throws IllegalArgumentException if bitmapFormat is invalid.
     */
    @Override
    public void setBitmapFormat(int bitmapFormat) {
        switch (bitmapFormat) {
            case BitmapHandler.FORMAT_ARGB8888:
            case BitmapHandler.FORMAT_RGB565:
            case BitmapHandler.FORMAT_ARGB4444:
                break;
            default:
                throw new IllegalArgumentException("Invalid bitmapformat:" + bitmapFormat);
        }
        mBitmapFormat = bitmapFormat;
    }

    /**
     * Sets the number of frames for one performance run, 0 means run forever.
     * @param frames The number of frames to run the test for.
     * @throws IllegalArgumentException If frames is < 0
     */
    @Override
    public void setPerformanceRun(int frames) {
        if (frames < 0) {
            throw new IllegalArgumentException("Invalid number of frames:" + frames);
        }
        mPerformanceRunFrameCount = frames;
        Log.d(TAG, "Set performance run: " + frames);
    }

    @Override
    public void setTickTreshold(int mikros) {
        if (mikros < 0) {
            throw new IllegalArgumentException("Invalid parameter, threshold is < 0, " + mikros);
        }
        mFrametickThreshold = mikros;
        Log.d(TAG, "Set tick treshold: " + mikros);
    }


    /**
     * Create background image to fill screen.
     * @param background Name of background image
     * @scale Scale factor for screen, a value of 1 will create background
     * image with size of screen.
     * A larger value will scale the background image down.
     * @throws IOException
     */
    protected void createBackgroundImage(String background, int scale,
                                        float screenWidth, float screenHeight) throws IOException  {

        BitmapHandler bHandler = mRenderer.getBitmapHandler();
        Object b2 =  bHandler.createBitmap("greyscalewallpaper.png", mResolver);
        float oneByScale = (float) 1 / scale;
        //read bitmap
        mBgBlit = bHandler.createScaledBitmap(b2,(int)((screenWidth) * oneByScale),
                (int) ((screenHeight) * oneByScale), mBitmapFormat);


    }

    /**
     * Creates background object based on the specified image object.
     * @param background The background image object as created when
     * calling BitmapHandler.createImage() or an existing Texture2D object.
     * @param division Number of quads for the blit object, a value of 1 will
     * create one quad, a higher value will divide quad on x and y axis.
     * @param shading The material shading.
     * @param onscreenWidth Width of object onscreen.
     * @param onscreenHeight Height of object onscreen.
     * @param prepareTexture True to prepare texture, if enabled this will
     * send the texture to the GL.
     * @param useVBO True to use vertex buffer objects.
     * @return The GLBLitObject with the specified background image.
     * @throws OpenGLENException If there is a GL error preparing or using VBO.
     * @throws IllegalArgumentException if background object is null or not image or Texture2D
     *
     */
    public GLBlitObject createBackgroundObject(Object background, int division, int shading,
            int onscreenWidth, int onscreenHeight, boolean prepareTexture, boolean useVBO)
            throws OpenGLENException {

        BitmapHandler bHandler = mRenderer.getBitmapHandler();

        Texture2D texture = null;
        if (background instanceof Texture2D) {
            texture = (Texture2D)background;
        } else {
            texture = createTexture(background, mTextureFormat, bHandler);
        }

        GLBlitObject object = new GLBlitObject(0, 0, BACKGROUND_Z, onscreenWidth, onscreenHeight,
                new Texture2D[] { texture},
                GLBlitObject.ANCHOR_LEFT | GLBlitObject.ANCHOR_TOP, division);

        Material material = object.material;
        material.materialShading = shading;
        material.setBlendFunc(-1, -1);

        mRenderer.getTextureHandler().prepareMaterialTexture(0, material);
        if (mUseVBO) {
            mRenderer.getGraphicsUtilities().convertToVBO(object);
        }

        mBlitSize += mWidth * mHeight;
        return object;

    }

    /**
     * Sets the shading to be used for the benchmark
     * Note that not all benchmarks will read the value of
     * this field.
     * Valid values are:
     * Material.SHADING_UNLIT
     * Material.SHADING_COLORED
     * Material.SHADING_LIT
     * Material.SHADING_LAMBERT
     * Material.SHADING_PHONG
     * @param shading
     */
    public void setShading(int shading) {
        switch (shading) {
            case Material.SHADING_UNLIT:
            case Material.SHADING_COLORED:
            case Material.SHADING_LIT:
            case Material.SHADING_LAMBERT:
            case Material.SHADING_PHONG:
                mShading = shading;
                break;
            default:
                throw new IllegalArgumentException("Invalid material shading:" + shading);
        }
    }

    /**
     * Sends the result to the registered listeners, do this after completing
     * the test.
     * @param profile Profile result.
     */
    protected void finishTest(ProfileInfo profile) {
        int count = mProfileListener.size();
        Log.d(TAG, "Sending result to " + count + " listeners, with result: " + profile);
        for (int i = 0; i < count; i++) {
            mProfileListener.elementAt(i).result(profile);
        }
    }

    @Override
    public void addProfileListener(ProfileListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null.");
        }
        mProfileListener.add(listener);
        Log.d(TAG, "Added ProfileListener, total: " + mProfileListener.size());
    }

    @Override
    public void removeProfileListener(ProfileListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null.");
        }
        mProfileListener.remove(listener);
    }

    @Override
    public int getListenerCount() {
        return mProfileListener.size();
    }

    @Override
    public synchronized void destroy() {
        Log.d(TAG, "destroy()");
        super.destroy();
        /**
         * Relase the resource in this class.
         */
        if (mRenderer != null) {
            mRenderer.getBitmapHandler().recycle(mBgBlit);
            mRenderer.destroy();
        }
        mInitialized = false;

    }

    /**
     * Outputs the profileinfo to the log and the value of:
     * bypassDisplay, useVBO, bitmapFormat,frametick threshold, shading, textureformat
     * Testcases should override this method to output specific result.
     * @param name Tag to log result with.
     */
    public void outputTestResult(String name) {
        String textureFormat = TextureUtils.getCompressedName(mTextureFormat);
        //Check if textureformat is not compressed but one of LUMINANCE, LUMIANCE_ALPHA or RGB
        if (TextureUtils.getCompression(textureFormat) ==
            TextureUtils.UNDEFINED_TEXTURE_FORMAT) {
            textureFormat = getTextureFormatStr(mTextureFormat);
        }
        String shading = null;
        switch (mShading) {
            case Material.SHADING_UNLIT:
                shading = "UNLIT";
                break;
            case Material.SHADING_COLORED:
                shading = "COLORED_TEXTURE";
                break;
            case Material.SHADING_LIT:
                shading = "LIT_TEXTURE";
                break;
            case Material.SHADING_LAMBERT:
                shading = "LAMBERT";
                break;
            case Material.SHADING_PHONG:
                shading = "PHONG";
                break;

        }
        String bitmapFormat = "unknown";
        switch (mBitmapFormat) {
            case BitmapHandler.FORMAT_ARGB4444:
                bitmapFormat = "ARGB444";
                break;
            case BitmapHandler.FORMAT_ARGB8888:
                bitmapFormat = "ARGB8888";
                break;
            case BitmapHandler.FORMAT_RGB565:
                bitmapFormat = "RGB565";
                break;

        }

        Log.i(name, "Bypassdisplay=" + bypassDisplay +
                ", useVBO=" + mUseVBO + ", bitmapFormat=" + bitmapFormat +
                ", tickThreshold=" + mFrametickThreshold + ", shading=" + shading +
                ", textureformat=" + textureFormat + ", depthTest=" +
                mRenderer.getRenderSetting().getDepthFunc() + ", clear=" +
                mRenderer.getRenderSetting().getClearFunction() +
                ", size=" + mWidth + ", " + mHeight);
        Log.logProfileInfo(TAG, mProfileInfo, Logger.LOGLEVEL_INFO);
    }

    /**
     * Creates a Texture object for the specified bitmap, based on the textureFormat.
     * @param bitmap Bitmap object to create texture from.
     * @param textureFormat CompressedTextureUtils.UNCOMPRESSED_TEXTURE_FORMAT to use the format
     * from the Bitmap object
     * ConstantValues.RGB5_A1 5551 texture format.
     * @param bitmapHandler
     * @return Texture object with the specified texture format.
     * @throws IllegalArgumentException If textureformat is invalid, if bitmapHandler or bitmap
     * is null.
     */
    public Texture2D createTexture(Object bitmap, int textureFormat, BitmapHandler bitmapHandler)  {
        Texture2D result = null;
        if (bitmap == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter: null");
        }
        switch (mTextureFormat) {
            case ConstantValues.RGB5_A1:
                result = TextureUtils.create5551Texture(null, bitmap, 128, bitmapHandler);
                break;
            case ConstantValues.LUMINANCE_ALPHA:
                result = TextureUtils.createLuminanceAlphaTexture(null, bitmap, bitmapHandler);
                break;
            case ConstantValues.LUMINANCE:
                result = TextureUtils.createLuminanceTexture(null, bitmap, bitmapHandler);
                break;
            case TextureUtils.UNCOMPRESSED_TEXTURE_FORMAT:
                result = new Texture2D(bitmap, -1, bitmapHandler.getWidth(bitmap),
                                       bitmapHandler.getHeight(bitmap));
                break;

            default:
                throw new IllegalArgumentException("Unknown textureformat: " + textureFormat);
        }

        return result;
    }

    /**
     * Forces the smallest delta time between frames, if lower than this value, by sleeping
     * in the thread. The value is in milliseconds.
     * Set to 0 for no limit.
     * @param minDelta Minimum delta times between frames in milliseconds.
     */
    public void setMinDelta(int minDelta) {
        mMinDelta = minDelta;
    }

    @Override
    public int processFrame(Object object) {

        if (mMinDelta > 0) {
            int millis =  mProfileInfo.getFrameTicks() / 1000;
            if (mMinDelta > 0 && mMinDelta > millis) {
                try {
                    Thread.sleep(mMinDelta - millis);
                } catch (InterruptedException e) {
                    //Cant do anything.
                }
            }

        }
        return 0;
    }

}
