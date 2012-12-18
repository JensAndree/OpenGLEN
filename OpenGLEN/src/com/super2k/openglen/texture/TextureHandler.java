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
package com.super2k.openglen.texture;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.texture.Texture2D.CompressedTextureFormat;
import com.super2k.openglen.utils.GraphicsLibraryHandler;


/**
 * Class that handles adding and removal of textures in the renderer.
 * @author Richard Sahlin
 *
 */
public abstract class TextureHandler {

    protected final static String SET_TEXTURE_ERROR_STR = "Error setting texture for unit:";
    protected final static String ERROR_STR = " Error:";
    protected final static String TEXTURE_NULL_STR = "Texture is null";
    protected final static String MATERIAL_NULL_STR = "Material is null";
    protected final static String TEXTURE_UNIT_ERR_STR = "Texture unit out of range:";
    protected final static String TEXTURE_BUFFER_ERROR = "Could not create texture buffer:";
    protected final static String GRAPHICS_LIBRARY_NULL_STR = "GraphicsLibraryHandler is null";
    protected final static String GLES_NULL_STR = "GLES is null";
    protected final static String INVALID_PARAMETER_STR = "Invalid parameter";
    protected final static String BIND_TEXTURE_ERR_STR = "Could not bind texture:";
    protected final static String ACTIVE_TEXTURE_ERR_STR = "Could not activate texture unit:";
    protected final static String BIND_FRAMEBUFFER_ERR_STR = "Could not bind framebuffer:";
    protected final static String BIND_RENDERBUFFER_ERR_STR = "Could not bind renderbuffer:";
    protected final static String FRAMEBUFFER_ATTACH_ERR_STR =
                                                        "Could not set framebuffer attachement:";
    protected final static String GENERATE_MIPMAP_ERR_STR =
                                                        "Could not generate mipmaps for target:";
    protected final static String INVALID_PIXEL_OBJECT_STR = "Pixels is invalid object type";

    protected final static int PIXELSTORE_UNPACK_ALIGNMENT = 1;

    /**
     * Graphics Library Handler
     */
    protected GraphicsLibraryHandler mGraphicsLibrary;

    /**
     * Constructs a new texturehandler with specified GraphicsLibraryHandler
     * @param graphicsLibrary
     * @throws IllegalArgumentException If graphicsLibrary is NULL
     */
    public TextureHandler(GraphicsLibraryHandler graphicsLibrary)   {
        if (graphicsLibrary == null)    {
            throw new IllegalArgumentException(GRAPHICS_LIBRARY_NULL_STR);
        }
        mGraphicsLibrary = graphicsLibrary;
    }

    /**
     * Prepares the texture to be used in rendering, if texture needs to be loaded it is done now.
     * If texture name does not exist it is created,
     * texture name is set in the Texture2D object if sucessfully prepared.
     * After this method has completed succesfully it is possible to bind the texture,
     * using the texture name in the texture object.
     * @param textureUnit The texture unit to set the texture to, 0 up to
     * max number of texture units.
     * @param tex The Texture2D to prepare to be used.
     * @throws IllegalArguementException if tex is NULL
     * @throws OpenGLENException If the texture could not be prepared,
     * because texture could not be set or new texture name could not be generated.
     */
    public void prepareTexture(int textureUnit, Texture2D tex) throws OpenGLENException{
        if (tex == null){
            throw new IllegalArgumentException(TEXTURE_NULL_STR);
        }
        //Check for texture name.
        if (tex.mTextureName > 0)  {
            tex.mTextureUnit = textureUnit;
            setTexture2D(textureUnit, tex.getTarget(), tex, tex.mTextureName);
        }
        int[] texNames = new int[1];
        try {
            generateTextureNames(1, texNames, 0);
            setTexture2D(textureUnit, tex.getTarget(), tex, texNames[0]);
            tex.setTextureName(texNames[0], textureUnit);
            tex.clearDirty();
        }
        catch (OpenGLENException glen)    {
            //Error - release texture name and return.
            deleteTextures(1, texNames, 0);
            throw glen;
        }

    }

    /**
     * Prepares the textures needed for the material.
     * @param material
     * @param textureUnit The offset to the first texture unit to use.
     * @throws OpenGLENException If the texture(s) could not be prepared for rendering.
     * @throws IllegalArgumentException If textureUnit is invalid or material is NULL.
     */
    public void prepareMaterialTexture(int textureUnit, Material material) throws OpenGLENException{
        if (material == null)   {
            throw new IllegalArgumentException(MATERIAL_NULL_STR);
        }
        if (textureUnit < 0)    {
            //TODO:Check for high values out of range, max number
            //of texture units shall be defined in shader?
            throw new IllegalArgumentException(TEXTURE_UNIT_ERR_STR + textureUnit);
        }
        Texture2D[] textures = material.texture;
        int count = material.texture.length;
        for (int i = 0; i < count; i++)  {
            prepareTexture(textureUnit + i, textures[i]);
        }
    }

    /**
     * Generates a texture buffer (for the currently bound texture and texture unit)
     * An empty buffer of the specified size, format and type will be created.
     * @see glTexImage2D
     * @param level Mipmap level, must be 0 or above.
     * @param width Width in texels
     * @param height Height in texels
     * @param format The format of the texture, one of:
     * ConstantValues.ALPHA, ConstantValues.RGB, ConstantValues.RGBA,
     * ConstantValues.LUMINANCE, or ConstantValues.LUMINANCE_ALPHA
     * @param type The data type of the texture, one of
     * ConstantValues.UNSIGNED_BYTE, ConstantValues.UNSIGNED_SHORT_5_6_5,
     * ConstantValues.UNSIGNED_SHORT_4_4_4_4,
     * ConstantValues.UNSIGNED_SHORT_5_5_5_1
     * @throws IllegalArguementException if any of the parameters are invalid.
     */
    public void generateTextureBuffer(int level,
            int width, int height,
            int format,int type) {
        int result = internalTexImage2D(level, width, height, format, type, null);
        if (result != ConstantValues.NO_ERROR)  {
            throw new IllegalArgumentException(TEXTURE_BUFFER_ERROR + result);
        }
    }

    /**
     * Binds a framebuffer object.
     * @see Khronos glbindframebuffer
     * @param frameBuffer The framebuffer object to bind.
     * @throws IllegalArgumentException If frameBuffer cannot be bound.
     */
    public void bindFrameBuffer(int frameBuffer)        {
        mGraphicsLibrary.clearError();
        int result = internalBindFrameBuffer(frameBuffer);
        if (result != ConstantValues.NO_ERROR)  {
            throw new IllegalArgumentException(BIND_FRAMEBUFFER_ERR_STR + frameBuffer);
        }

    }

    /**
     * Binds a renderbuffer object.
     * @see Khronos glBindRenderbuffer
     * @param renderBuffer The renderbuffer object to bind.
     * @throws IllegalArgumentException If renderBuffer cannot be bound.
     */
    public void bindRenderBuffer(int renderBuffer) {
        mGraphicsLibrary.clearError();
        int result = internalBindRenderBuffer(renderBuffer);
        if (result != ConstantValues.NO_ERROR)  {
            throw new IllegalArgumentException(BIND_RENDERBUFFER_ERR_STR + renderBuffer);
        }

    }

    /**
     * Specifies the attachements for the currently bound framebuffer.
     * @see Khronos glBindFrameBufferTexture2D
     * @param attachment COLOR_ATTACHEMENT, STENCIL_ATTACHEMENT or DEPTH_ATTACHEMENT
     * @param textarget The texture target.
     * @param texture The texture object name
     * @param level
     * @throws IllegalArgumentException If there is an error, invalid parameters.
     */
    public void frameBufferTexture2D(int attachment, int textarget,
            int texture, int level) {
        mGraphicsLibrary.clearError();
        int result = internalFrameBufferTexture2D(attachment, textarget, texture, level);
        if (result != ConstantValues.NO_ERROR) {
            throw new IllegalArgumentException(FRAMEBUFFER_ATTACH_ERR_STR + result);
        }
    }

    /**
     * Attaches a renderbuffer object to a framebuffer object
     * @see Khronos glBindFrameRenderbuffer
     * @param attachment COLOR_ATTACHEMENT, STENCIL_ATTACHEMENT or DEPTH_ATTACHEMENT
     * @param renderbuffer Renderbuffer object name
     */
    public void frameBufferRenderBuffer(int attachement, int renderbuffer) {
        mGraphicsLibrary.clearError();
        int result = internalFrameBufferRenderbuffer(attachement, renderbuffer);
        if (result != ConstantValues.NO_ERROR) {
            throw new IllegalArgumentException(FRAMEBUFFER_ATTACH_ERR_STR + result);
        }

    }

    /**
     * Sets the texture2D parameter for the currently bound texture on the
     * active texture unit.
     * @see glTexParameter
     * @param pname Texture parameter to set
     * TEXTURE_MIN_FILTER, TEXTURE_MAG_FILTER,
     * TEXTURE_WRAP_S, TEXTURE_WRAP_T.
     * @param param Value
     * @return Result code, ConstantValues.NO_ERROR or INVALID_ENUM.
     */
    public abstract int texParameter2D(int pname, int param);

    /**
     * Generates mipmaps for texture bound to the specified target,
     * TEXTURE_2D or TEXTURE_CUBE_MAP
     * All mipmaps level up to 1*1 will ge replaced, level 0 will be
     * unchanged.
     * @param target The texture target TEXTURE_2D or TEXTURE_CUBE_MAP
     * @throws IllegalArgumentException If target is not one of
     * ConstantValues.TEXTURE_2D or ConstantValues.TEXTURE_CUBE_MAP
     *
     */
    public void generateMipMap(int target) throws OpenGLENException{
        if (target != ConstantValues.TEXTURE_2D && target != ConstantValues.TEXTURE_CUBE_MAP)   {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR + target);
        }
        mGraphicsLibrary.clearError();
        int result = internalGenerateMipMap(target);
        if (result != ConstantValues.NO_ERROR){
            throw new OpenGLENException(GENERATE_MIPMAP_ERR_STR + target +",", result);
        }
    }

    /**
     * Internal wrapper method for bindFrameBufferTexture2D, make sure no pending error.
     * @param attachement The buffer attachement point COLOR, DEPTH or STENCIL
     * @param textarget Texture target
     * @param texture Texture object name
     * @param level Mip level
     * @return Result, NO_ERROR or errorcode
     */
    protected abstract int internalFrameBufferTexture2D(int attachment, int textarget,
            int texture, int level);

    /**
     * internal wrappermethod for frameBufferRenderbuffer
     * @param attachement The buffer attachement point COLOR, DEPTH or STENCIL
     * @param renderbuffer
     * @return Result, NO_ERROR or errorcode
     */
    protected abstract int internalFrameBufferRenderbuffer(int attachement, int renderbuffer);

    /**
     * Internal wrapper method for generateMipMaps, make sure no
     * pending error and check that target is valid.
     * @param target TEXTURE_2D or TEXTURE_CUBE_MAP
     * @return Result ConstantValues.NO_ERROR or errorcode.
     */
    protected abstract int internalGenerateMipMap(int target);

    /**
     * Internal wrapper method for bindFrameBuffer, make sure error cleared
     * before calling and check result.
     * @param frameBuffer
     * @return resultcode, NO_ERROR or errorcode
     */
    protected abstract int internalBindFrameBuffer(int frameBuffer);

    /**
     * internal wrapper method for bindRenderbuffer, make sure error cleared before calling
     * and check result after.
     * @param renderBuffer
     * @return resultcode, NO_ERROR or errorcode
     */
    protected abstract int internalBindRenderBuffer(int renderBuffer);

    /**
     * Internal wrapper method for renderbufferstorage, make sure error cleared before calling
     * and check result.
     * @param internalformat The renderbuffer format, one of
     * RGBA4,
     * RGB565,
     * RGB5_A1,
     * DEPTH_COMPONENT16, or
     * STENCIL_INDEX8.
     * @param width The width of the renderbuffer in pixels
     * @param height The height of the renderbuffer in pixels.
     * @return
     */
    protected abstract int internalRenderBufferStorage(int internalformat, int width, int height);

    /**
     * Internal wrapper method for texImage2D,
     * will create a buffer for the texture as specified.
     * Make sure no pending error before calling this method.
     * @param level
     * @param width
     * @param height
     * @param format
     * @param type
     * @param pixels The pixeldata to use or null to allocate new buffer.
     * On android this can be a bitmap or java.nio.buffer
     * On J2SE this must be an BufferedImage
     * @return result, NO_ERROR or errorcode.
     * @throws IllegalArgumentException If pixels is not null and wrong object.
     * TODO: Spec does not say anything regarding out of memory error,
     * what happens when fails due to out of memory.
     *
     */
    protected abstract int internalTexImage2D(int level, int width, int height, int format,
                                              int type, Object pixels);

    /**
     * Internal wrapper method, check parameters before calling.
     * Deletes the specified textures.
     * @param count The number of textures to delete
     * @param names List of texture names.
     * @param offset Offset into names array.
     */
    protected abstract void internalDeleteTextures(int count, int[] names, int offset);

    /**
     * Internal wrapper method for compressed tex image.
     * Make sure no pending error before calling this method.
     * @param level Mip level
     * @param internalformat The compressed format.
     * @param width Width of texture in pixels
     * @param height Height of texture in pixels.
     * @param size Number of bytes of data, ie size of compressed texture in bytes.
     * @param data The compressed data.
     * @return result, NO_ERROR or errorcode.
     */
    protected abstract int internalCompressedTexImage2D(int level, int internalformat,
            int width, int height, int size, Buffer data);

    /**
     * Internal wrapper method for bindTexture.
     * Make sure no pending error before calling this method.
     * @param target Specifies the target of the active texture unit to which the texture is bound.
     * @param texture Texture object to bind.
     * @return resultcode, NO_ERROR or errorcode
     */
    protected abstract int internalBindTexture2D(int target, int texture);

    /**
     * Sets the pixelstore alignment, make sure no pending error before
     * calling this method.
     * @param pname Either PACK_ALIGNMENT or UNPACK_ALILGNMENT
     * @param param Alignment for each row in memory, 1, 2, 4 or 8
     * @return The resultcode, NO_ERROR or errorcode.
     */
    protected abstract int internalPixelstore(int pname, int param);

    /**
     * Internal wrapper method for activeTexture.
     * @param texture Texture unit to make active.
     */
    protected abstract void internalActiveTexture(int texture);

    /**
     * Internal wrapper method for checkFrameBufferStatus
     * @return The completeness of the framebuffer, NO_ERROR or errorcode.
     */
    protected abstract int internalCheckFrameBufferStatus();

    /**
     * Binds a texture to TEXTURE_2D, the texture is bound to the currently
     * active texture unit.
     * @param target Specifies the target of the active texture unit to which the texture is bound
     * @param texture The texture object to bind.
     * @throws IllegalArgumentException If texture is not a valid texture object.
     * In which case the texture is not bound.
     */
    public void bindTexture2D(int target, int texture)  {
        mGraphicsLibrary.clearError();
        int result = internalBindTexture2D(target, texture);
        if (result != ConstantValues.NO_ERROR) {
            throw new IllegalArgumentException(BIND_TEXTURE_ERR_STR + texture +"," +
                                               INVALID_PARAMETER_STR);
        }
    }

    /**
     * Sets the active texture unit, valid values are TEXTURE_0 up to MAX_TEXTURE_UNITS-1
     * @param texture
     * @throws IllegalArgumentException If texture is not in range for a texture unit.
     */
    public void activeTexture(int texture)  {
        mGraphicsLibrary.clearError();
        internalActiveTexture(texture);
        int result = mGraphicsLibrary.checkError();
        if (result != ConstantValues.NO_ERROR) {
            throw new IllegalArgumentException(ACTIVE_TEXTURE_ERR_STR + texture + "," +
                                               INVALID_PARAMETER_STR);
        }
    }

    /**
     * Generates the specified number of framebuffer object names.
     * There is no guarantee that the names form a contiguous set of integers
     * however, it is guaranteed that none of the returned names was in use
     * immediately before the call to glGenFramebuffers.
     * Framebuffer object names returned by a call to glGenFramebuffers are not returned by
     * subsequent calls, unless they are first deleted with deleteFramebuffers.
     * No framebuffer objects are associated with the returned
     * framebuffer object names until they are first bound by calling bindFramebuffer.
     * @param count Number of buffer names to generate.
     * @param array Array to hold generated names
     * @param offset Offset into array where names are stored.
     * @throws IllegalArgumentException If count or offset is negative, array is NULL
     * or count+offset > array.length
     * @return Array in which the generated framebuffer object names are stored.
     */
    public int[] genFrameBuffers(int count, int[] array, int offset) {
        if (count < 0 || offset < 0 || array == null || (array.length < count + offset)) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        internalGenFrameBuffers(count, array, offset);
        return array;
    }

    /**
     * Generates the specified number of renderbuffer object names.
     * There is no guarantee that the names form a contiguous set of integers
     * however, it is guaranteed that none of the returned names was in use
     * immediately before the call.
     * @see Khronos glGenRenderbuffers
     * @param count Number of buffer names to generate.
     * @param array Array to hold generated names
     * @param offset Offset into array where names are stored.
     * @return Array with the generated renderbuffer object names.
     * @throws IllegalArgumentException If count or offset is negative, array is NULL
     * or count+offset > array.length
     */
    public int[] genRenderBuffers(int count, int[] array, int offset) {
        if (count < 0 || offset < 0 || array == null || (array.length < count + offset)) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        internalGenRenderbuffers(count, array, offset);
        return array;
    }

    /**
     * creates and initializes a renderbuffer object's data store
     * @param internalformat Specifies the color-renderable, depth-renderable, or
     * stencil-renderable format of the renderbuffer.
     * Must be one of the following symbolic constants:
     * RGBA4,
     * RGB565,
     * RGB5_A1,
     * DEPTH_COMPONENT16, or
     * STENCIL_INDEX8.
     * @param width Width of renderbuffer in pixels
     * @param height Height of renderbuffer in pixels
     * @throws IllegalArgumentException if width or height is <= 0, if internalformat or
     * reserved renderbuffer object 0 is bound.
     */
    public void renderBufferStorage(int internalformat, int width, int height) {
        mGraphicsLibrary.clearError();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        int result = internalRenderBufferStorage(internalformat, width, height);
        if (result != ConstantValues.NO_ERROR) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR + ", " + result);
        }
    }


    /**
     * Creates framebuffer object names. Internal method.
     * Count, array and offset must be errorchecked before calling this method.
     * @param count Number of framebuffer names to generate.
     * @param array Array to store names.
     * @param offset Offset in array where names are stored.
     */
    protected abstract void internalGenFrameBuffers(int count, int[] array, int offset);

    /**
     * Internal wrapper method that creates renderbuffer object names.
     * @param count Number of renderbuffer object names to generate
     * @param array Array to hold generated object names
     * @param offset Offset into array
     */
    protected abstract void internalGenRenderbuffers(int count, int[] array, int offset);


    /**
     * Internal wrapper method for glGenTextures.
     * Parameters must be checked before calling this method.
     * @param count
     * @param names
     * @param offset
     */
    protected abstract void internalGenerateTextureNames(int count, int[] names, int offset);

    /**
     * Generates texture names (IDs)
     * @param count Number of texture names to create.
     * @param names Array holding names
     * @param offset Offset into array for names.
     * @throws IllegalArgumentException If count or offset is negative, names.length < count+offset
     */
    public void generateTextureNames(int count, int[] names, int offset) {
        if (count < 0 || offset < 0 || names == null || names.length < count+offset) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        internalGenerateTextureNames(count, names, offset);
    }


    /**
     * Deletes the texture names specified.
     * If texture is currently bound the binding will be broken.
     * @param count Number of textures to delete
     * @param textures The texture names to delete
     * @param offset Offset into textures
     * @throws IllegalArgumentException If count is negative,
     * or there is not enough data at textures + offset.
     */
    public void deleteTextures(int count, int[] textures, int offset) {
        if (count < 0 || textures == null || textures.length < count + offset) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        internalDeleteTextures(count, textures, offset);
    }

    /**
     * Returns the status of the currently bound framebuffer.
     * @return Status of currently bound framebuffer, ConstantValues.FRAMEBUFFE_COMPLETE
     * ConstantValues.FRAMEBUFFER_INCOMPLETE_ATTACHMENT
     * ConstantValues.FRAMEBUFFER_INCOMPLETE_DIMENSIONS
     * ConstantValues.FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT
     * ConstantValues.FRAMEBUFFER_UNSUPPORTED
     */
    public int checkFrameBufferStatus() {
        //Make sure no pending error.
        mGraphicsLibrary.clearError();
        return internalCheckFrameBufferStatus();
    }

    /**
     * Sets the texture to GL using the specified texture name,
     * note that the texture name and texture unit must be valid.
     * After this call is succesfull it is possible to bind the texture to GL, when this method
     * returns the texture will be bound to the specified texture unit.
     * @param textureUnit The textureunit to set texture to (active texture), 0 up to
     * max texture units.
     * @param target Specifies the target of the active texture unit to which the texture is bound
     * @param texture The texture to be set to GL
     * @param texName The texture name to use.
     * @return Texture name
     * @throws OpenGLENException If the texture could not be set, it means that the
     * texture cannot be used.
     */

    public void setTexture2D(int activeTexture, int target, Texture2D texture, int texName )
            throws OpenGLENException {
        //Clear any previous error.
        mGraphicsLibrary.clearError();
        Object textureData = texture.getTextureData();

        switch (activeTexture)  {
            case 0:
                internalActiveTexture(ConstantValues.TEXTURE0);
                break;
            case 1:
                internalActiveTexture(ConstantValues.TEXTURE1);
                break;

            default:
                throw new IllegalArgumentException(TEXTURE_UNIT_ERR_STR + ", " + activeTexture);
        }
        int result = internalBindTexture2D(target, texName);
        if (result != ConstantValues.NO_ERROR) {
            throw new OpenGLENException("Could not bind texture " + texName, result);
        }
        internalPixelstore(ConstantValues.UNPACK_ALIGNMENT, PIXELSTORE_UNPACK_ALIGNMENT);
        if (!(textureData instanceof ByteBuffer))  {
            //Format could be Android bitmap or J2SE IntBuffer
            result = internalTexImage2D(0, 0, 0, texture.getFormat(), texture.getType(),
                                        textureData);
            if (result != ConstantValues.NO_ERROR) {
                throw new IllegalArgumentException(SET_TEXTURE_ERROR_STR + ", " + activeTexture);
            }
        }
        else {

            int w = texture.getWidth();
            int h = texture.getHeight();
            int format = texture.getFormat();
            switch (format) {

                case CompressedTextureFormat.ETC1_RGB8_OES:
                case CompressedTextureFormat.ATC_RGB_AMD:
                case CompressedTextureFormat.S3TC_DXT1_RGB:
                case CompressedTextureFormat.S3TC_DXT1_RGBA:
                    //ETC, AMD RGB and DXT1 textures are padded to even 4 rows and collumns.
                    //It is made up of blocks of 4*4 pixels.
                    int size = ((((w+3)>>>2)<<2) * (((h+3)>>>2)<<2)) >>>1;
                    result = internalCompressedTexImage2D(0,
                            format,
                            w, h, size, ((Buffer) textureData).position(0));
                    break;
                    case    CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD:
                    case    CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD:
                        result = internalCompressedTexImage2D(0,
                                format,
                                w, h, w*h, ((Buffer) textureData).position(0));
                        break;


                    default:
                        result = internalTexImage2D(0,
                                w, h, texture.getFormat(),
                                texture.getType(), ((Buffer)textureData).position(0));
            }

            if (result != ConstantValues.NO_ERROR) {
                throw new OpenGLENException(SET_TEXTURE_ERROR_STR + texName + "," + activeTexture +
                        ERROR_STR + "result= " + result);
            }
        }

    }

}
