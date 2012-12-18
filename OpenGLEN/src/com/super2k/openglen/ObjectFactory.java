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

package com.super2k.openglen;

import java.io.IOException;

import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;
import com.super2k.openglen.texture.Texture2D;

/**
 * ObjectFactory for objects that can be rendered using OpenGLEN renderer. Use
 * the ObjectFactoryManager to get an instance of this interface.
 *
 * @author Richard Sahlin
 *
 */
public interface ObjectFactory {

    /**
     * Creates a renderable object with a flat 2D surface from a image object with specified
     * anchor point and world width/height parameter.
     * The texture will be prepared when this method returns, the object is ready to be rendered.
     * This will load the image and create a new texture object and store in the material.
     * @param classname Classname, must be instanceof GLBLitObject
     * @param filename
     * @param resolver
     * @param shading
     * @param textureFormat
     * @param anchor
     * @param xpos
     * @param ypos
     * @param width
     * @param height
     * @param useAlpha
     * @return
     * @throws OpenGLENException
     * @throws IOException
     */
/*
    public GLBlitObject createObject(String classname, String filename,
            InputStreamResolver resolver, int shading, int textureFormat, int anchor, float xpos,
            float ypos, int width, int height, boolean useAlpha)
            throws IOException, OpenGLENException;
*/

    /**
     * Creates a renderable object with a flat 2D surface from a bitmap object with specified
     * anchor point and world width/height parameter.
     * The texture will be prepared when this method returns, the object is ready to be rendered.
     * This will create a new texture object for the image and store in the material, only use when
     * not time critical.
     * @param classname Classname, must be instanceof GLBLitObject
     * @param image Bitmap object to use for object.
     * @param anchor
     * @param xpos
     * @param ypos
     * @param width
     * @param height
     * @return
     * @throws OpenGLENException
     * @throws IOException
     */
/*
    public GLBlitObject createObject(String classname, Object image, Material material,int anchor,
            float xpos, float ypos, float zpos, int width, int height)
            throws IOException, OpenGLENException;
*/
    /**
     * Creates a renderable object with a flat 2D surface from a material object with specified
     * anchor point and world width/height parameter.
     * The texture will be prepared when this method returns, the object is ready to be rendered.
     * The material will be copied into the blit object.
     * @param classname Classname, must be instanceof GLBLitObject
     * @param image Image to attach to material, will overwrite any existing textures.
     * @param material Material to take data from.
     * @param anchor
     * @param xpos
     * @param ypos
     * @param zpos
     * @param width Width of object, or -1 to take width from first texture.
     * @param height Height of object, or -1 to take height from first texture.
     * @return
     * @throws OpenGLENException
     * @throws IOException
     */
    public GLBlitObject createObject(String classname, Material material,
            int anchor, float xpos, float ypos, float zpos, int width, int height)
            throws IOException, OpenGLENException;

    /**
     * Creates a renderable object with a flat 2D surface from a material and texture object
     * with specified anchor point and world width/height parameter.
     * The texture will be prepared when this method returns, the object is ready to be rendered.
     * The material will be copied into the blit object,
     * the texture will be copied into the material
     * @param classname Classname, must be instanceof GLBLitObject
     * @param image Image to attach to material, will overwrite any existing textures.
     * @param material Material to take data from.
     * @param texture The texture to use, this will be copied into the material.
     * @param anchor
     * @param xpos
     * @param ypos
     * @param zpos
     * @param width Width of object, or -1 to take width from first texture.
     * @param height Height of object, or -1 to take height from first texture.
     * @return
     * @throws OpenGLENException
     * @throws IOException
     */
/*
    public GLBlitObject createObject(String classname, Material material, Texture2D texture,
            int anchor, float xpos, float ypos, float zpos, int width, int height)
            throws IOException, OpenGLENException;
*/
    /**
     * Creates a renderable object with a flat 2D surface from a texture2D and Material.
     * @param classname
     * @param texture
     * @param material
     * @param anchor
     * @param xpos
     * @param ypos
     * @param zpos
     * @param width
     * @param height
     * @return
     */
//    public GLBlitObject createObject(String classname, Texture2D texture, Material material,
//            int anchor, float xpos, float ypos, float zpos, int width, int height)
//            throws OpenGLENException;

    /**
     * Creates a renderable object with a flat 2D surface, specified
     * anchor point and world width/height parameter.
     * The texture will be prepared when this method returns,
     * the object is ready to be rendered.
     *
     * @param bitmap The bitmap to use as texture source, on Android this must
     *            be a Bitmap object. Use the BitmapHandler to create an image.
     *            To create an object based on a jpg/png/etc use other create
     *            method.
     * @param xpos X position
     * @param ypos Y position
     * @param zpos Z position
     * @param width Width on screen, actual size depends on transform and projection matrix.
     * -1 to use width of bitmap
     * @param height Height on screen, actual size depends on transform and projection matrix.
     * -1 to use height of bitmap.
     * @param anchor Anchor point.
     * @param shading The object shading. One of Material.SHADING_XXX values.
     * @param useAlpha True to enable alpha blending, sourceblend will be SRC_ALPHA,
     * destinationblend will be ONE_MINUS_SRC_ALPHA if turned on.
     * @return Object that can be renderered
     * @throws IllegalArgumentException If bitmap is null or wrong object type.
     * @throws OpenGLENException If the texture could not be prepared.
     */
    public GLBlitObject createBlitObject(Object bitmap,
                                        float xpos,
                                        float ypos,
                                        float zpos,
                                        float width,
                                        float height,
                                        int anchor,
                                        int shading,
                                        boolean useAlpha) throws OpenGLENException;

    /**
     * Creates a renderable object with a flat 2D surface from a texture object with specified
     * anchor point and world width/height parameter.
     * The texture will be prepared when this method returns, the object is ready to be rendered.
     *
     * @param texture The texture to be used, can be compressed texture.
     * @param xpos X position
     * @param ypos Y position
     * @param zpos Z position
     * @param width Width on screen, actual size depends on transform and projection matrix.
     * @param height Height on screen, actual size depends on transform and projection matrix.
     * @param anchor Anchor point.
     * @param shading Material shading, one of Material.SHADING_XXX
     * @param useAlpha True to enable alpha blending, sourceblend will be SRC_ALPHA,
     * destinationblend will be ONE_MINUS_SRC_ALPHA if turned on.
     * @return Object that can be renderered
     * @throws IllegalArgumentException If bitmap is null or wrong object type.
     * @throws OpenGLENException If the texture could not be prepared.
     */
    public GLBlitObject createBlitObject(Texture2D texture, float xpos, float ypos, float zpos,
            float width, float height, int anchor, int shading, boolean useAlpha)
            throws OpenGLENException;

    /**
     * Creates a particle array with the specified number of particles of the specified type.
     * Use the renderer to draw particles.
     * @param particles Number of particles to allocate.
     * @param type Type of particles, must be one of GLParticleArray.TYPE_XXX
     * @param srcBlend The source blend operation, ConstantValues.NONE for no alpha
     * @param dstBlend The destination blend operation, ConstantValues.NONE for no alpha
     * @param useVBO Set to true to convert array to vertex buffer object, this will usually
     * not lead to performance gain since array buffers have to be updated each frame.
     * @return The particle array that can be rendered
     */
    public GLParticleArray createParticleArray(int particles, int type, int srcBlend,
            int dstBlend, boolean useVBO) throws OpenGLENException;

    /**
     * Creates a pool with the specified number of objects, the className must be instance
     * of GLBlitObject.
     * Storage for 1 quad is allocated, indices, texture coordinates and  normals (front facing)
     * are setup.
     * @param className Object class to create, must be instance of GLBLitObject. Objects can
     * be fetched using the key value.
     * @param type Key used to find object types (of classname)
     * @param count Number of objects to create
     * @param shading The shading to use
     * @param divisor Number of divisions of quad for vertices, 1 equals to one quad, 5 means
     * the quad is divided 5 times in each of x and y.
     * @param xRepeat Number of times to repeat texture in x, normally 1.
     * @param yRepeat Number of times to repeat texture in y, normally 1.
     * @throws OpenGLENException If the classname cannot be created or if an entry with the
     * specified type already exists.
     * @throws IllegalArgumentException If classname is null, or if count <= 0
     */
    public void createBlitObjectPool(String className, int type, int count, int shading,
            int divisor, float xRepeat, float yRepeat) throws
            OpenGLENException;

    /**
     * Creates a pool with a number of particle arrays, the className must be instance of
     * GLParticleArray.
     * @param className
     * @param key
     * @param count Number of particle objects to create
     * @param particleType Type of particle, defined in GLParticleArray
     * @param particleCount Number of particles in each particle object
     * @param srcBlend The source blend operation, ConstantValues.NONE for no alpha
     * @param dstBlend The destination blend operation, ConstantValues.NONE for no alpha
     * @param useVBO Set to true to convert array to vertex buffer object, this will usually
     * not lead to performance gain since array buffers have to be updated each frame.
     * @throws OpenGLENException
     */
    public void createParticlesPool(String className, int key, int count, int particleType,
            int particleCount, int srcBlend, int dstBlend, boolean useVBO) throws OpenGLENException;

    /**
     * Returns an object of the specified type, object must be created with a call to
     * createObjectPool
     * The object must be released back to the pool with a call to releaseObject.
     * @param type The type of object to return.
     * @return
     */
    public GLBlitObject getObject(int type);

    /**
     * Returns a particle array created with the specified key.
     * @param key Key used when particle was created.
     * @return
     */
    public GLParticleArray getParticle(int key);

    /**
     * Release an object back to a pool, this must be called for all objects
     * that are fetched with a call to getObject()
     * @param object
     */
    public void releaseObject(GLBlitObject object);

    /**
     * Release the particle array object back to a pool, this must be called for all particle
     * objects that are fetched with a call to getParticle()
     * @param object
     */
    public void releaseParticle(GLParticleArray object);

    /**
     * Destroy the objectfactory and release all object names and buffers
     * allocated by this factory. Call this method before the GL context is
     * released.
     */
    public void destroy();

}
