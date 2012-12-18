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

package com.super2k.openglen.objects;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.ObjectFactory;
import com.super2k.openglen.ObjectFactoryManager;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.texture.Texture2D;
import com.super2k.openglen.texture.TextureHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;

/**
 * Factory methods for creating objects that can be rendered using the OpenGLEN
 * renderer.
 * Use the ObjectFactoryManager to get an instance of an ObjectFactory.
 *@see ObjectFactoryManager
 *
 * @author Richard Sahlin
 *
 */
public class GLENObjectFactory implements ObjectFactory {

    /**
     * The texturehandler used by the factory.
     */
    protected TextureHandler mTextureHandler;

    /**
     * The bitmaphandler used by the factory.
     */
    protected BitmapHandler mBitmapHandler;

    /**
     * Graphics utilities.
     */
    protected GraphicsLibraryHandler mGraphicsUtils;

    /**
     * List of created textures by this object factory. Textures prepared by the
     * factory may get a texture name and texture buffer, these will be deleted
     * from GL when calling destroy() by traversing this list.
     */
    protected Vector<Texture2D> mTextures = new Vector<Texture2D>();

    protected Hashtable<Integer, LinkedList<GLBlitObject>> mPool =
            new Hashtable<Integer, LinkedList<GLBlitObject>>();

    protected Hashtable<Integer, LinkedList<GLParticleArray>> mParticlePool =
            new Hashtable<Integer, LinkedList<GLParticleArray>>();

    /**
     * Temp storage
     */
    protected LinkedList<GLBlitObject> mTempList;
    protected LinkedList<GLParticleArray> mTempParticleList;
    protected GLBlitObject mTempBlit;
    protected GLParticleArray mTempParticle;

    /**
     * Creates an objectfactory.
     *
     * @param renderer The renderer to create objects for.
     * @throws IllegalArgumentException If renderer is null
     */
    public GLENObjectFactory(Renderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("renderer is null");
        }
        mTextureHandler = renderer.getTextureHandler();
        mBitmapHandler = renderer.getBitmapHandler();
        mGraphicsUtils = renderer.getGraphicsUtilities();
    }

    @Override
    public void destroy() {
        int count = mTextures.size();
        Texture2D tex;
        int[] texname = new int[1];
        for (int i = 0; i<count; i++) {
            tex = mTextures.elementAt(i);
            texname[0] = tex.getTextureName();
            mTextureHandler.deleteTextures(1, texname, 0);
        }
    }

    @Override
    public GLBlitObject createBlitObject(Object bitmap, float xpos, float ypos, float zpos,
            float width, float height, int anchor, int shading, boolean useAlpha)
            throws OpenGLENException {

        int w = mBitmapHandler.getWidth(bitmap);
        int h = mBitmapHandler.getHeight(bitmap);
        Texture2D tex = new Texture2D(bitmap, -1, w, h);
        return createBlitObject(tex, xpos, ypos, zpos, width, height, anchor, shading, useAlpha);
    }

    @Override
    public GLBlitObject createBlitObject(Texture2D texture, float xpos, float ypos, float zpos,
            float width, float height, int anchor, int shading,
            boolean useAlpha) throws OpenGLENException {

        GLBlitObject blit = new GLBlitObject(xpos, ypos, zpos, width, height,
                new Texture2D[] {texture}, anchor, 1);
        if (useAlpha) {
            blit.material.setBlendFunc(ConstantValues.SRC_ALPHA,
                                       ConstantValues.ONE_MINUS_SRC_ALPHA);
        }
        blit.setShading(shading);
        mTextureHandler.prepareTexture(0, texture);
        mGraphicsUtils.convertToVBO(blit);
        mTextures.add(texture);
        return blit;
    }

    @Override
    public GLBlitObject createObject(String classname, Material material, int anchor, float xpos,
            float ypos, float zpos, int width, int height) throws OpenGLENException {

        if (width == -1) {
            if (material.texture != null) {
                width = material.texture[0].getWidth();
            } else {
                throw new OpenGLENException("Cannot read width, no texture.");
            }
        }
        if (height == -1) {
            if (material.texture != null) {
                height = material.texture[0].getHeight();
            } else {
                throw new OpenGLENException("Cannot read height, no texture.");
            }
        }
        GLBlitObject blitObject = createBlitObject(classname);
        blitObject.create(1, 1, 1);
        blitObject.set(xpos,  ypos, zpos, width, height, anchor, material);
        mTextureHandler.prepareMaterialTexture(0, blitObject.material);
        return blitObject;

    }

/*
    @Override
    public GLBlitObject createObject(String classname, Material material,
            Texture2D texture, int anchor, float xpos, float ypos, float zpos,
            int width, int height) throws IOException, OpenGLENException {
        if (material == null || texture == null || classname == null) {
            throw new IllegalArgumentException("Illegal parameter, null");
        }
        if (width == -1) {
            width = texture.getWidth();
        }
        if (height == -1) {
            height = texture.getHeight();
        }
        GLBlitObject blitObject = createBlitObject(classname);
        blitObject.init(width, height, material, texture, anchor, 1);
        blitObject.set(xpos, ypos, -1);

        return blitObject;

    }
*/
    /**
     * Creates a new GLBLitObject instance of the specified name.
     * @param classname Classname to instantiate, must be GLBlitObject subclass.
     * @return New instance of the specified class.
     */
    private GLBlitObject createBlitObject(String classname) {

        try {
            return (GLBlitObject) Class.forName(classname).newInstance();

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public void createBlitObjectPool(String className, int type, int count, int shading,
            int divisor, float xRepeat, float yRepeat) throws OpenGLENException {
        if (className == null || count <= 0) {
            throw new IllegalArgumentException("Illegal parameter: " + className + ", " +
                    ", " + count);
        }
        //Check if objects already exist
        Integer integer = Integer.valueOf(type);
        LinkedList<GLBlitObject> blitPool = mPool.get(integer);
        if (blitPool != null) {
            throw new OpenGLENException("Already created pool with key: " + integer.intValue());
        }
        blitPool = new LinkedList<GLBlitObject>();
        for (int i = 0; i < count; i++) {
            GLBlitObject blit = createBlitObject(className);
            blit.create(divisor, xRepeat, yRepeat);
            blit.key = type;
            blitPool.add(blit);
        }
        mPool.put(integer, blitPool);
    }

    @Override
    public void createParticlesPool(String className, int key, int count,
            int particleType, int particleCount, int srcBlend, int dstBlend, boolean useVBO)
                    throws OpenGLENException {
        if (className == null || count <= 0) {
            throw new IllegalArgumentException("Illegal parameter: " + className + ", " +
                    ", " + count);
        }
        //Check if objects already exist
        Integer integer = Integer.valueOf(key);
        LinkedList<GLParticleArray> particlePool = mParticlePool.get(integer);
        if (particlePool != null) {
            throw new OpenGLENException("Already created particle pool with key: " +
                    integer.intValue());
        }
        particlePool = new LinkedList<GLParticleArray>();
        for (int i = 0; i < count; i++) {
            GLParticleArray particle = createParticleArray(particleCount, particleType,
                    srcBlend, dstBlend, useVBO);
            particle.type = key;
            particle.key = key;
            particlePool.add(particle);
        }
        mParticlePool.put(key, particlePool);
    }


    @Override
    public GLBlitObject getObject(int type) {
        mTempList = mPool.get(Integer.valueOf(type));
        return mTempList.removeLast();
    }

    @Override
    public void releaseObject(GLBlitObject object) {
        if (object.key == -1) {
            //Object is not pooled.
            return;
        }
        mTempList = mPool.get(Integer.valueOf(object.key));
        mTempList.add(object);
        object.releaseObject();
    }

    @Override
    public GLParticleArray createParticleArray(int count, int type,
            int srcBlend, int dstBlend, boolean useVBO) throws OpenGLENException {
        GLParticleArray particles = new GLParticleArray();
        particles.init(count, type);
        particles.material.setBlendFunc(srcBlend, dstBlend);
        if (useVBO) {
            mGraphicsUtils.convertToVBO(particles);
        }
        return particles;
    }

    @Override
    public GLParticleArray getParticle(int key) {
        mTempParticleList = mParticlePool.get(key);
        return mTempParticleList.removeLast();

    }

    @Override
    public void releaseParticle(GLParticleArray object) {
        if (object.key == -1) {
            //Object is not pooled.
            return;
        }
        mTempParticleList = mParticlePool.get(object.key);
        mTempParticleList.add(object);
        object.releaseObject();
    }

}
