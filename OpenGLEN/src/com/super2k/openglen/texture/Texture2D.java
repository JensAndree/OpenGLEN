/* Copyright 2012 Richard Sahlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licensesimport java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.super2k.openglen.ConstantValues;
ributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.super2k.openglen.texture;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.super2k.openglen.ConstantValues;

/**
 * Texture2D class
 * Storage of data needed for a texture.
 * @author Richard Sahlin
 *
 */
public class Texture2D {

    protected final static String TEXTURE_DATA_NULL_STR = "Texture data is NULL";
    protected final static String INVALID_FORMAT_STR = "Invalid format: ";
    protected final static String INVALID_TYPE_STR = "Invalid type: ";
    protected final static String INVALID_TEXTURE_SIZE_STR = "Invalid texture size: ";
    protected final static String INVALID_TEXTURE_PARAM_STR = "Invalid texture parameter: ";

    protected final static int DEFAULT_MAG_FILTER = ConstantValues.LINEAR;
    protected final static int DEFAULT_MIN_FILTER = ConstantValues.LINEAR;
    protected final static int DEFAULT_WRAP_S = ConstantValues.CLAMP_TO_EDGE;
    protected final static int DEFAULT_WRAP_T = ConstantValues.CLAMP_TO_EDGE;

    /**
     * Pixel data.
     * For a compressed texture this must be java.nio.Buffer
     */
    Object mData = null;

    /**
     * Width in pixels (texels) of the texture.
     */
    int mWidth;

    /**
     * Height in pixels (texels) of the texture.
     */
    int mHeight;

    /**
     * The format of the texture, same as GL texture format.
     * Normally RGB or RGBA, for a compressed texture this will be the compressed format
     * eg.ETC1_RGB8_OES
     * @NOTE For compressed textures to work the texture data object must be java.nio.Buffer
     */
    int mFormat;

    /**
     * The datatype of the texture, same as GL texture type.
     * Normally UNSIGNED_BYTE or UNSIGNED_SHORT_5_6_5
     */
    int mType;

    /**
     * Number of bits per pixel
     */
    int mBpp;

    /**
     * The texture name (ID)
     */
    int mTextureName = -1;

    /**
     * The target for this texture, normally ConstantValues.TEXTURE_2D
     */
    int mTextureTarget = ConstantValues.TEXTURE_2D;

    /**
     * The texture unit that this texture shall use.
     */
    int mTextureUnit;

    /**
     * Texture parameters for each texture unit.
     * MAG_FILTER, MIN_FILTER, WRAP_S, WRAP_T
     */
    int[] mTexParameters = new int[] { DEFAULT_MAG_FILTER,
            DEFAULT_MIN_FILTER,
            DEFAULT_WRAP_S,
            DEFAULT_WRAP_T };

    /**
     * The currently set texture parameters, can be updated by
     * renderer to only set parameters when changed.
     */
    int[] mCurrentTexParameters = new int[4];

    /**
     * Whether the texture content has changed and needs to be refreshed in graphics subsystem.
     */
    private boolean mDirty = false;

    /**
     * Default constructor for pooling of texture objects.
     * Users must make sure to call a setup method before texture objects are used.
     */
    public Texture2D() {
        super();
    }

    /**
     * Constructs a Texture2D with only texture object name.
     * No allocations will be made, texture does not need to be
     * prepared.
     * @param texture
     * @param format The format of the texture from ConstantValues
     * ALPHA, RGB or RGBA, LUMINANCE or LUMINANCE_ALPHA
     * @param type The datatype of the texture
     * UNSIGNED_BYTE, UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_4444, UNSIGNED_SHORT_5551
     * @param width Target width of texture
     * @param height Target height of texture
     * @throws IllegalArgumentException if format or type is invalid or
     * if texture is negative, or if size is <= 0.
     * No checking is done to see if texture is
     * valid texture object name (since that requires the underlying platform)
     */
    public Texture2D(int texture, int format, int type, int width, int height) {
        setup(texture, format, type, width, height, false);
        mDirty = true;
    }

    /**
     * Create a new texture object based on the specified textureData.
     * The textureData object is platform dependent and is the pixel data.
     * On Android this is a Bitmap
     * Texture name can be -1, it will then be created as texture is prepared.
     * When texture is prepared the bitmap data will be sent to GL.
     * @param textureData The texture pixel data. Bitmap on Android.
     * @param texName Texture name or -1.
     * @param width Width of the texture
     * @param height Height of the texture
     * @throws IllegalArgumentException if textureData is null.
     */
    public Texture2D(Object textureData, int texName, int width, int height) {
        if (textureData==null) {
            throw new IllegalArgumentException(TEXTURE_DATA_NULL_STR);
        }
        //Set format to RGBA and unsigned byte - this is not used on Android
        //if Bitmap is the source.
        setup(texName, ConstantValues.RGBA, ConstantValues.UNSIGNED_BYTE, width, height, false);
        mData = textureData;
        mDirty = true; //set to true since a texture was passed
    }

    /**
     * Create a Texture2D with the specified properties.
     * Optional if a java.nio.Buffer is created to hold the pixels.
     * @param width Target width of texture
     * @param height Target height of texture
     * @param format The format of the texture from ConstantValues
     * ALPHA, RGB or RGBA, LUMINANCE or LUMINANCE_ALPHA
     * @param type The datatype of the texture
     * UNSIGNED_BYTE, UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_4444, UNSIGNED_SHORT_5551
     * @param allocateBuffer Set to true to allocate a java.nio.Buffer
     * to hold the pixels.
     * @throws IllegalArgumentException If type or format is invalid, or
     *             incompatible eg format=RGBA with type=UNSIGNED_5_6_5
     */
    public Texture2D(int width, int height, int format,
            int type, boolean allocateBuffer) {
        setup(-1, format, type, width, height, allocateBuffer);
    }

    /**
     * Create a Texture2D with the specified properties.
     * Optional if a java.nio.Buffer is created to hold the pixels.
     * @param width Target width of texture
     * @param height Target height of texture
     * @param format The format of the texture from ConstantValues
     * ALPHA, RGB or RGBA, LUMINANCE or LUMINANCE_ALPHA
     * @param type The datatype of the texture
     * UNSIGNED_BYTE, UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_4444, UNSIGNED_SHORT_5551
     * @param textureName The texturename to ascociate this texture with.
     * Must be a valid texture object name.
     * @param allocateBuffer Set to true to allocate a java.nio.Buffer
     * to hold the pixels.
     * @throws IllegalArgumentException If type or format is invalid, or
     *             incompatible eg format=RGBA with type=UNSIGNED_5_6_5
     */
    public Texture2D(int width, int height, int format,
            int type, int textureName, boolean allocateBuffer) {
        setup(textureName, format, type, width, height, allocateBuffer);

    }

    /**
     * Setup this texture to use a texture name and texture unit
     * with the specified format, type, width and height
     * The textureunit to use is decided when the material is prepared.
     * Material can have several textures.
     * @param texture The texture object name
     * @param format Texture format
     * One of ConstantValues.ALPHA, RGB or RGBA, LUMINANCE or LUMINANCE_ALPHA
     * @param type Texture type
     * One of ConstantValues.UNSIGNED_SHORT_4_4_4_4
     * UNSIGNED_BYTE,UNSIGNED_SHORT_5_6_5,UNSIGNED_SHORT_5_5_5_1
     * @param width Target width of texture
     * @param height Target height of texture
     * @param allocateBuffer True to allocate buffer for the texture.
     * @throws IllegalArgumentException If format or type is invalid, width or height <= 0
     */
    protected void setup(int texture, int format, int type, int width, int height,
                        boolean allocateBuffer) {
        validateFormatAndType(format, type);
        setup(width, height, format, type, allocateBuffer);
        mTextureName = texture;
    }

    /**
     * Checks that format is a valid value and that type is valid.
     * @param format
     * @param type
     * @throws IllegalArgumentException If format is not one of:
     * ConstantValues.ALPHA, RGB or RGBA, LUMINANCE or LUMINANCE_ALPHA
     * or type not one of:
     * ConstantValues.UNSIGNED_SHORT_4_4_4_4
     * UNSIGNED_BYTE,UNSIGNED_SHORT_5_6_5,UNSIGNED_SHORT_5_5_5_1
     * TODO: Also check that the compination format, type fits together.
     * Ie not Format RGB and type UNSIGNED_SHORT_5_6_5 etc.
     */
    private final void validateFormatAndType(int format, int type) {
        if (format!=ConstantValues.RGB &&
                format!=ConstantValues.RGBA &&
                format!=ConstantValues.LUMINANCE &&
                format!=ConstantValues.LUMINANCE_ALPHA &&
                format!=ConstantValues.ALPHA) {
            throw new IllegalArgumentException(INVALID_FORMAT_STR + format);
        }
        if (type!=ConstantValues.UNSIGNED_BYTE &&
                type!=ConstantValues.UNSIGNED_SHORT_4_4_4_4 &&
                type!=ConstantValues.UNSIGNED_SHORT_5_5_5_1 &&
                type!=ConstantValues.UNSIGNED_SHORT_5_6_5) {
            throw new IllegalArgumentException(INVALID_TYPE_STR + type);
        }

    }

    /**
     * Sets this texture to be a texture of the specified format, for instance a compressed
     * texture or LUMINANCE.
     * Any previously allocated buffer is released.
     * @param data The buffer containing the compressed data,
     * this buffer is read when the texture is prepared.
     * @param width Width of texture in pixels.
     * @param height Height of texture in pixels.
     * @param textureFormat textureformat - any of CompressedTextureFormat values.
     * eg CompressedTextureFormat.ETC1_RGB8_OES
     * or any valid texture format.
     * @param type Disregarded for compressed texture formats, Specifies the data type of the
     * pixel data. The following symbolic values are accepted:
     * UNSIGNED_BYTE, UNSIGNED_SHORT_5_6_5,
     * UNSIGNED_SHORT_4_4_4_4, and UNSIGNED_SHORT_5_5_5_1
     * @throws IllegalArgumentException If data is NULL
     */
    public void setup(Buffer data, int width, int height, int textureFormat, int type) {
        if (data == null) {
            throw new IllegalArgumentException(TEXTURE_DATA_NULL_STR);
        }
        setup(width, height, textureFormat, type, false);
        this.mTextureName = -1;
        this.mData = data;

    }

    /**
     * Internal method to setup the Texture
     * @param width Width in pixels
     * @param height Height in pixels
     * @param format Format, from ConstantValues.RGB etc.
     * @param type Datatype of texture, from ConstantValues.UNSIGNED_BYTE etc.
     * Not used for compressed texture.
     * @param allocateByteBuffer True to allocate ByteBuffer,
     * which can then be fetched with getTextureData.
     * @throws IllegalArgumentException If width or height is <= 0
     */
    private void setup(int width, int height, int format, int type, boolean allocateByteBuffer) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(INVALID_TEXTURE_SIZE_STR);
        }
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mType = type;
        mBpp = 24;
        boolean checkType = true;

        switch (format) {
            case ConstantValues.RGB:
                if (type == ConstantValues.UNSIGNED_BYTE)
                    mBpp = 24;
                break;
            case ConstantValues.RGBA:
                if (type == ConstantValues.UNSIGNED_BYTE)
                    mBpp = 32;
                if (type == ConstantValues.UNSIGNED_SHORT_5_6_5)
                    throw new IllegalArgumentException();

                break;
            case ConstantValues.RGB5_A1:
            case ConstantValues.RGB565:
                mBpp = 16;
                break;
            case ConstantValues.LUMINANCE:
            case ConstantValues.LUMINANCE_ALPHA:
                if (type == ConstantValues.UNSIGNED_BYTE) {
                    if (format == ConstantValues.LUMINANCE) {
                        mBpp = 8;
                    } else {
                        mBpp = 16;
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Texture format LUMINANCE must have type UNSIGNED_BYTE.");
                }
                break;
            case CompressedTextureFormat.ETC1_RGB8_OES:
            case CompressedTextureFormat.ATC_RGB_AMD:
            case CompressedTextureFormat.S3TC_DXT1_RGB:
            case CompressedTextureFormat.S3TC_DXT1_RGBA:
                mBpp = 4;
                checkType = false;
                break;
            case CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD:
            case CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD:
                mBpp = 8;
                checkType = false;
                break;
            case CompressedTextureFormat.GL_PALETTE8_RGB8_OES:
                mBpp = 8;
                checkType = false;
                break;
            default:
                throw new IllegalArgumentException("Not implemented compressed texture format: " +
                        format);
        }

        //Dont care about type if compressed texture.
        if (checkType) {
            switch (type) {
                case ConstantValues.UNSIGNED_SHORT_5_6_5:
                    mBpp = 16;
                    break;
                case ConstantValues.UNSIGNED_SHORT_4_4_4_4:
                    mBpp = 16;
                    break;
                case ConstantValues.UNSIGNED_SHORT_5_5_5_1:
                    mBpp = 16;
                    break;
                case ConstantValues.UNSIGNED_BYTE:
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (allocateByteBuffer) {
            mData = ByteBuffer.allocateDirect((width*height*mBpp) >>> 3).order(ByteOrder.nativeOrder());
        } else {
            mData = null;
        }
    }

    /**
     * The width of the texture in pixels.
     *
     * @return The width of the texture.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * The height of the texture in pixels.
     *
     * @return The height of the texture
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Set the texture name (ID) for this texture. This will not bind the
     * texture to that ID it will simply make it possible to easily fetch the
     * name together with this texture.
     *
     * @param textureName
     * @param textureUnit The texture unit number to use.
     * @return The previous texture name.
     */
    public int setTextureName(int textureName, int textureUnit) {

        mTextureUnit = textureUnit;
        int old = this.mTextureName;
        this.mTextureName = textureName;
        return old;
    }

    /**
     * Sets the texture target, normally TEXTURE_2D but can allow other values.
     * @param target
     */
    public void setTextureTarget(int target) {
        mTextureTarget = target;
    }

    /**
     * Sets the texture format and type.
     * @param format The texture format, one of
     * ConstantValues.ALPHA
     * ConstantValues.LUMINANCE
     * ConstantValues.LUMINANCE_ALPHA
     * ConstantValues.RGB
     * ConstantValues.RGBA
     * Or a compressed texture format.
     * @param type Texture type, one of
     * ConstantValues.UNSIGNED_BYTE
     * ConstantValues.UNSIGNED_SHORT_5_6_5
     * ConstantValues.UNSIGNED_SHORT_5_5_5_1
     * ConstantValues.UNSIGNED_SHORT_4_4_4_4
     * @throws IllegalArgumentException If format or type is invalid.
     */
    public void setFormatAndType(int format, int type) {
        if (!TextureUtils.validateTextureFormat(format)) {
            throw new IllegalArgumentException("Illegal texture format: " + format);
        }
        if (!TextureUtils.validateTextureType(type)) {
            throw new IllegalArgumentException("Illegal texture type: " + type);
        }
        mFormat = format;
        mType = type;
    }

    /**
     * Return the texture name (ID)
     *
     * @return The texture name (ID) or -1 if none specified.
     */
    public int getTextureName() {
        return mTextureName;
    }

    /**
     * Return the texture unit to use for this texture.
     * @return
     */
    public int getTextureUnit() {
        return mTextureUnit;
    }

    /**
     * Returns the texture target, normally TEXTURE_2D
     * @return The texture target.
     */
    public int getTarget() {
        return mTextureTarget;
    }

    /**
     * The format of the texture, same as GL texture format. Normally GL_RGB or
     * GL_RGBA
     *
     * @return The format of the texture (same as GL texture format)
     */
    public int getFormat() {
        return mFormat;
    }

    /**
     * The data type of the texture, same as GL texture type.
     * UNSIGNED_BYTE, UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_4444, UNSIGNED_SHORT_5551
     *
     * @return The data type of the texture (same as GL texture type)
     */
    public int getType() {
        return mType;
    }

    /**
     * Return the texture pixel data.
     * This can be a java.nio.ByteBuffer or a platform specific object (Bitmap)
     * @return ByteBuffer containing the pixel data.
     */
    public Object getTextureData() {
        return mData;
    }

    /**
     * Sets the texture wrap mode
     * Valid values are
     * CLAMP_TO_EDGE, MIRRORED_REPEAT, or REPEAT
     * @param wrapS
     * @param wrapT
     * @throws IllegalArgumentException If texture wrap modes are not valid.
     */
    public void setTextureWrap(int wrapS, int wrapT) {
        if ((wrapS != ConstantValues.CLAMP_TO_EDGE && wrapS != ConstantValues.MIRRORED_REPEAT &&
                wrapS!=ConstantValues.REPEAT) || (wrapT != ConstantValues.CLAMP_TO_EDGE &&
                wrapT!=ConstantValues.MIRRORED_REPEAT && wrapT != ConstantValues.REPEAT)) {
            throw new IllegalArgumentException(INVALID_TEXTURE_PARAM_STR + wrapS + ", " + wrapT);
        }
        mTexParameters[2] = wrapS;
        mTexParameters[3] = wrapT;
    }

    /**
     * Sets the texture mag and min filter
     * @param magFilter
     * @param minFilter
     */
    public void setTextureFilter(int magFilter, int minFilter) {
        mTexParameters[0] = magFilter;
        mTexParameters[1] = minFilter;
    }

    /**
     * Sets the size of this texture.
     * @param width Width of texture in pixels.
     * @param height Height of texture in pixels.
     */
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * Sets the texture data, the texture will be flagged as needing update.
     * @param data The data to set as this texture.
     */
    public void setData(Object data) {
        mData = data;
        mDirty = true;
    }

    /**
     * Check if the texture (data) has been touched and needs to be fed to GL again.
     * Is cleared after upload to GL.
     * @return True if the texture data needs to be uploaded to GL.
     */
    public boolean isDirty() {
        return mDirty;
    }

    /**
     *
     */
    public void clearDirty() {
        mDirty = false;
    }

    /**
     * Return an array holding the texture parameters.
     * MAG_FILTER, MIN_FILTER, WRAP_S, WRAP_T
     * @return Array holding texture pname values.
     */
    public int[] getTexParams() {
        return mTexParameters;
    }

    /**
     * The current texture parameter values.
     * This can be used by a renderer implementation to
     * keep track of when to set parameters.
     * @return
     */
    public int[] getCurrentTexParams() {
        return mCurrentTexParameters;
    }

    public interface CompressedTextureFormat {

        /**
         * Use this texture compression format whenever possible
         * - it has the most widespread support!
         * Check that gl extension GL_OES_compressed_ETC1_RGB8_texture is supported before using.
         * If alpha channel is needed try to use ETC2
         */
        public final static int ETC1_RGB8_OES = 0x8D64;

        /**
         * This format compresses blocks of source texels down to 4 bits per texel.
         * Assuming 8-bit component source texels, this represents a 8:1 compression
         * ratio.  This is the best format to use when no alpha channel is needed.
         */
        public final static int ATC_RGB_AMD = 0x8C92;

        /**
         * AMD/ATI (Qualcomm) proprietary texture compression format.
         * Good for textures with gradient alpha
         * Use if the extension GL_AMD_compressed_ATC_texture is supported
         */
        public final static int ATC_RGBA_INTERPOLATED_ALPHA_AMD = 0x87EE;

        /**
         * AMD/ATI (Qualcomm) proprietary texture compression format.
         * Good for textures with sharp alpha
         * Use if the extension GL_AMD_compressed_ATC_texture is supported
         */
        public final static int ATC_RGBA_EXPLICIT_ALPHA_AMD = 0x8C93;

        /**
         * DXT1 EXT_texture_compression_dxt1 compression,RGB
         * Use if the extension GL_EXT_texture_compression_dxt1 is supported
         * Usually on Nvidia platforms
         */
        public final static int S3TC_DXT1_RGB = 0x83F0;


        /**
         * DXT1 EXT_texture_compression_dxt1 compression, RGBA. Minimal alpha information (1 bit)
         * If alpha is greater than 0.5 alpha in texture is 1, if alpha in source is less than 0.5
         * alpha in texture is 0.
         * Use if the extension GL_EXT_texture_compression_dxt1 is supported
         * Usually on Nvidia platforms
         */
        public final static int S3TC_DXT1_RGBA = 0x83F1;

        /* GL_OES_compressed_paletted_texture */
        public final static int GL_PALETTE4_RGB8_OES     = 0x8B90;
        public final static int GL_PALETTE4_RGBA8_OES    = 0x8B91;
        public final static int GL_PALETTE4_R5_G6_B5_OES = 0x8B92;
        public final static int GL_PALETTE4_RGBA4_OES    = 0x8B93;
        public final static int GL_PALETTE4_RGB5_A1_OES  = 0x8B94;
        public final static int GL_PALETTE8_RGB8_OES     = 0x8B95;
        public final static int GL_PALETTE8_RGBA8_OES    = 0x8B96;
        public final static int GL_PALETTE8_R5_G6_B5_OES = 0x8B97;
        public final static int GL_PALETTE8_RGBA4_OES    = 0x8B98;
        public final static int GL_PALETTE8_RGB5_A1_OES  = 0x8B99;

    }

}
