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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.nibbler.InputStreamResolver;
import com.super2k.openglen.texture.Texture2D.CompressedTextureFormat;

/**
 * Class to help with DirectDraw surface fileformat.
 * @author Richard Sahlin
 *
 */
public class TextureUtils {

    protected static String TAG = TextureUtils.class.getSimpleName();

    /**
     * For not defined or unknown texture format.
     */
    public final static int UNDEFINED_TEXTURE_FORMAT = -1;

    public final static int UNCOMPRESSED_TEXTURE_FORMAT = 0;

    public final static String ETC_COMPRESSION = "ETC";
    public final static String ATC_RGB_COMPRESSION = "ATC_RGB";
    public final static String ATC_RGBA_E_COMPRESSION = "ATC_RGBA_E";
    public final static String ATC_RGBA_I_COMPRESSION = "ATC_RGBA_I";
    public final static String DXT1_RGB_COMPRESSION = "DXT1_RGB";
    public final static String DXT1_RGBA_COMPRESSION = "DXT1_RGBA";
    public final static String UNCOMPRESSED = "UNCOMPRESSED";
    public final static String UNKNOWN = "unknown";

    public final static String[] FOURCC_TABLE = new String[]
            {"ATC ", "ATCI", "ATCA", "ETC ","DXT1"};
    public final  static int[] FOURCC_FORMAT_TABLE = new int[] {CompressedTextureFormat.ATC_RGB_AMD,
        CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD,
        CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD, CompressedTextureFormat.ETC1_RGB8_OES,
        CompressedTextureFormat.S3TC_DXT1_RGB};

    /**
     * Filename endings to use for compressed textures, append this string lookedup up by
     * using the texture format, to the filename to be load texture.
     * This table must match with the compresseion format and name tables below.
     */
    protected final static String[] COMPRESSED_TEXTURE_FILENAMES = new String[]{
            "",
            "_etc_rgb.DDS",
            "_atc_rgb.DDS",
            "_atc_rgba_e.DDS",
            "_atc_rgba_i.DDS",
            "_dxt1_rgb.DDS",
            "_dxt1_rgba.DDS"};

    /**
     * Table with allowed compression formats, note that the platform must support the
     * necessary compression format.
     * Must match with the compression name and texture filename tables.
     */
    public final static int[] COMPRESSION_FORMAT_TABLE = new int[]{
            UNCOMPRESSED_TEXTURE_FORMAT,
            CompressedTextureFormat.ETC1_RGB8_OES,
            CompressedTextureFormat.ATC_RGB_AMD,
            CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD,
            CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD,
            CompressedTextureFormat.S3TC_DXT1_RGB,
            CompressedTextureFormat.S3TC_DXT1_RGBA
            };

    /**
     * Lookup a compression name string, if string matches the corresponding value can be fetched
     * for compression value.
     * Must match with compression format and filename tables.
     */
    public final static String[] COMPRESSION_FORMAT_NAME_TABLE = new String[] {
            UNCOMPRESSED,
            ETC_COMPRESSION,
            ATC_RGB_COMPRESSION,
            ATC_RGBA_E_COMPRESSION,
            ATC_RGBA_I_COMPRESSION,
            DXT1_RGB_COMPRESSION,
            DXT1_RGBA_COMPRESSION};

    /**
     * Creates a Texture2D object from the specified filename and compressed texture type.
     * @param resolver
     * @param filename
     * @param compressedFormat
     * @return
     * @throws IOException
     */
    public static Texture2D createCompressedTexture(InputStreamResolver resolver, String filename,
            int compressedFormat) throws IOException {

        BufferedInputStream bin = new BufferedInputStream(resolver.openInputStream(filename));

        //read width and height, first 4 bytes are identifier 'DDS '
        byte[] header = new byte[124 + 4];
        int offset = 0;
        int left = header.length;
        int read = 0;
        while ((read = bin.read(header, offset, header.length-offset)) < left) {
            left -= read;
        }

        int flags = getInt(header, 80);
        String fourCC = new String(header, 84, 4);
        int format = getFourCC(fourCC);

        int rgbBitCount = getInt(header, 88);
        int rMask = getInt(header, 92);
        int gMask = getInt(header, 96);
        int bMask = getInt(header, 100);
        int aMask = getInt(header, 104);

//        if (format != compressedFormat) {
//            throw new IllegalArgumentException("Formats not matching, format=" + format);
//        }

        int height = getInt(header, 12);
        int width = getInt(header, 16);

        int size = 0;
        switch (compressedFormat) {
            case Texture2D.CompressedTextureFormat.ETC1_RGB8_OES:
            case Texture2D.CompressedTextureFormat.ATC_RGB_AMD:
            case Texture2D.CompressedTextureFormat.S3TC_DXT1_RGB:
            case Texture2D.CompressedTextureFormat.S3TC_DXT1_RGBA:
                //ETC 4 bits per pixel, ATC RGB 4 bits per pixel.
                //DXT1 RGB,RGBA 4 bits per pixel
                size = ( (((width+3)>>>2)<<2) * (((height+3)>>>2)<<2)) >>>1;
                break;
                case Texture2D.CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD:
                case Texture2D.CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD:
                //8 bits per pixel.
                size = width * height;
                break;
            default:
                throw new IllegalArgumentException("Unknown compressed texture format: " +
                                                    compressedFormat);

        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());

        byte[] data = new byte[1024];
        read = 0;
        left = size;
        while (left > 0) {
            if ((read = bin.read(data)) > 0) {
                if (left < read) {
                    read = left;
                    left = 0;
                }
                left -= read;
                buffer.put(data, 0, read);
            } else {
                Log.d(TAG, "Reached end of input texture");
                throw new RuntimeException("Reached end of input texture.");
            }
        }

        Texture2D tex = new Texture2D();
        tex.setup(buffer, width, height, compressedFormat, -1);


        return tex;
    }

    /**
     * Returns the compressed texture format for a DDS fourcc string.
     * @param fourcc The DDS fourcc string, eg "DXT1", "ETC "
     * @return The compressed texture format, or -1 if invalid String.
     * @throws IllegalArgumentException If fourcc is null
     */
    public static int getFourCC(String fourcc) {
        if (fourcc == null) {
            throw new IllegalArgumentException("Fourcc is null.");
        }

        for (int i = 0; i < FOURCC_TABLE.length; i++) {
            if (fourcc.equalsIgnoreCase(FOURCC_TABLE[i])) {
                return FOURCC_FORMAT_TABLE[i];
            }
        }
        return -1;
    }

    /**
     * Reads 4 bytes at the specified position and returns an int.
     * @param array
     * @param index
     * @return Int composed from 4 byte values at the specified index.
     */
    private static int getInt(byte[] array, int index) {
        return (array[index++]&255) + ((array[index++]&255)<<8) + ((array[index++]&255)<<16) +
        ((array[index++]&255)<<24);
    }

    /**
     * Utility method to get name string of compressed texture.
     * Allowed compressed texture formats are defined in COMPRESSION_FORMAT_TABLE
     * @param compressedFormat Compressed texture format, from CompressedTextureFormat
     * @return String representing the compressed texture format, or UNCOMPRESSED_TEXTURE_FORMAT if
     * compressedFormat is 0. Unknown if there is no known format matching.
     */
    public final static String getCompressedName(int compressedFormat) {
        if (compressedFormat == UNCOMPRESSED_TEXTURE_FORMAT) {
            return UNCOMPRESSED;
        }
        String result = UNKNOWN;

        for (int i = 0; i < COMPRESSION_FORMAT_TABLE.length; i++) {
            if (COMPRESSION_FORMAT_TABLE[i] == compressedFormat) {
                return COMPRESSION_FORMAT_NAME_TABLE[i];
            }
        }
        return result;
    }

    /**
     * Fetch the value for a compression.
     * @param compression The compressed format, eg ETC, ATC_RGBA_E, ATC_RGB
     * @return The value defined for the compression or UNDEFINED_TEXTURE_FORMAT if not matching
     * any known formats.
     */
    public final static int getCompression(String compression) {

        for (int i = 0; i < COMPRESSION_FORMAT_NAME_TABLE.length; i++) {
            if (compression.equalsIgnoreCase(COMPRESSION_FORMAT_NAME_TABLE[i])) {
                return COMPRESSION_FORMAT_TABLE[i];
            }
        }
        return UNDEFINED_TEXTURE_FORMAT;
    }

    /**
     * Utility method to return filename ending for compressed texture format.
     * eg. etc1 compressed format will return "_etc_rgb.DDS", see COMPRESSED_TEXTURE_FILENAMES
     * @param compressedFormat Compressed texture format from CompressedTextureFormat
     * @return String with filename ending apropriate for the specified format, "" if format is 0
     * and null if no matching format was found.
     */
    public final static String getCompressedFileName(int compressedFormat) {
        if (compressedFormat == UNCOMPRESSED_TEXTURE_FORMAT) {
            return "";
        }
        for (int i = 0; i < COMPRESSION_FORMAT_TABLE.length; i++) {
            if (COMPRESSION_FORMAT_TABLE[i] == compressedFormat) {
                return COMPRESSED_TEXTURE_FILENAMES[i];
            }
        }
        return null;

    }

    /**
     * Checks if the specified texture format is known.
     * @param format The texture format, can be compressed texture format.
     * @return true if the format is known and valid, false otherwise.
     */
    public static boolean validateTextureFormat(int format) {
        switch (format) {
            case ConstantValues.LUMINANCE:
            case ConstantValues.LUMINANCE_ALPHA:
            case ConstantValues.ALPHA:
            case ConstantValues.RGB:
            case ConstantValues.RGBA:
            case CompressedTextureFormat.ETC1_RGB8_OES:
            case CompressedTextureFormat.ATC_RGB_AMD:
            case CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD:
            case CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD:
            case CompressedTextureFormat.S3TC_DXT1_RGB:
            case CompressedTextureFormat.S3TC_DXT1_RGBA:
            return true;
        }
        return false;
    }

    /**
     * Checks if the specified texture type is valid.
     * @param type The textue type, must be one of:
     * ConstantValues.UNSIGNED_BYTE
     * ConstantValues.UNSIGNED_SHORT_5_6_5
     * ConstantValues.UNSIGNED_SHORT_5_5_5_1
     * ConstantValues.UNSIGNED_SHORT_4_4_4_4
     * @return True if type is one of the allowed texture types.
     */
    public static boolean validateTextureType(int type) {
        switch (type) {
            case ConstantValues.UNSIGNED_BYTE:
            case ConstantValues.UNSIGNED_SHORT_5_6_5:
            case ConstantValues.UNSIGNED_SHORT_5_5_5_1:
            case ConstantValues.UNSIGNED_SHORT_4_4_4_4:
            return true;
        }
        return false;
    }


    /**
     * Creates a luminance texture object from the specified bitmap.
     * Resulting texture will have 8 bits luminance (greyscale) data for each pixel and
     * no alpha.
     * Note that this method has some processing overhead, if time critical consider
     * using converted texture that is saved to filesystem.
     * @param texture Texture object to store texture in or null to create new.
     * @param image Image to convert to greyscale texture.
     * @param bitmapHandler Bitmaphandler.
     * @return Texture object containing the image as a greyscale texture.
     * @throws IllegalArgumentException If image is null or not bitmap object, or if bitmapHandler
     * is null.
     */
    public static Texture2D createLuminanceTexture(Texture2D texture, Object image,
                                                   BitmapHandler bitmapHandler) {
        if (image == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }
        if (texture == null) {
            texture = new Texture2D();
        }
        int[] pixels = bitmapHandler.getBitmapArray(image);
        //Get images greyscale values.
        int size = pixels.length;
        byte[] bytedata = new byte[size];
        ByteBuffer luminance = ByteBuffer.allocateDirect(size).
                           order(ByteOrder.nativeOrder());
        int p;
        for (int i = 0; i < size;i++) {
            p = pixels[i];
            bytedata[i] = (byte) (((p&255) + ((p>>>8)&255) + ((p>>>16)&255))/3);
        }
        luminance.put(bytedata);
        texture.setup(luminance,
                bitmapHandler.getWidth(image),
                bitmapHandler.getHeight(image),
                ConstantValues.LUMINANCE, ConstantValues.UNSIGNED_BYTE);
        return texture;
    }

    /**
     * Creates a RGB texture object from the specified bitmap.
     * Resulting texture will have 8 bits of red, green and blue for each pixel, 24 bpp.
     * Note that this method has some processing overhead, if time critical consider
     * using converted texture that is saved to filesystem.
     * @param texture Texture object to store texture in or null to create new.
     * @param image Texture source image.
     * @param bitmapHandler Bitmaphandler.
     * @return Texture object containing the image as a RGB 24bpp texture.
     * @throws IllegalArgumentException If image is null or not bitmap object, or if bitmapHandler
     * is null.
     */
    public static Texture2D createRGBTexture(Texture2D texture, Object image,
                                             BitmapHandler bitmapHandler) {

        if (image == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }

        if (texture == null) {
            texture = new Texture2D();
        }
        int[] pixels = bitmapHandler.getBitmapArray(image);
        //Get images greyscale values.
        int size = pixels.length;
        byte[] bytedata = new byte[size * 3];
        ByteBuffer rgb = ByteBuffer.allocateDirect(size * 3).
                           order(ByteOrder.nativeOrder());
        int index = 0;
        int value;
        for (int i = 0; i < size;i++) {
            value = pixels[i];
            bytedata[index++] = (byte) (value & 255);
            bytedata[index++] = (byte) ((value>>>8) & 255);
            bytedata[index++] = (byte) ((value>>>16) & 255);
        }
        rgb.put(bytedata);
        texture.setup(rgb,
                bitmapHandler.getWidth(image),
                bitmapHandler.getHeight(image),
                ConstantValues.RGB, ConstantValues.UNSIGNED_BYTE);
        return texture;


    }

    /**
     *
     * @param texture Texture object to store texture in or null to create new.
     * @param image Texture source image.
     * @param alphaTreshold Threshold for alpha value, if alpha is above this value then resulting
     * alpha is 1, otherwise 0.
     * @param bitmapHandler Bitmaphandler.
     * @return Texture object containing the image as a 16 bit 5551 texture.
     * @throws IllegalArgumentException If image is null or not bitmap object, or if bitmapHandler
     * is null.
     */
    public static Texture2D create5551Texture(Texture2D texture, Object image, int alphaTreshold,
            BitmapHandler bitmapHandler) {

        if (image == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }

        if (texture == null) {
            texture = new Texture2D();
        }
        int[] pixels = bitmapHandler.getBitmapArray(image);
        //Get images greyscale values.
        int size = pixels.length;
        byte[] bytedata = new byte[size * 2];
        ByteBuffer rgb = ByteBuffer.allocateDirect(size * 2).
                           order(ByteOrder.nativeOrder());
        int index = 0;
        int value;
        int pixel;
        for (int i = 0; i < size;i++) {
            value = pixels[i];
            pixel =  (((value & 0xf8)>>>3) | ((value & 0x0f800)>>>6) |
                     ((value & 0x0f80000)>>>9))<<1;
            //Check for alpha
            if (((value & 0x0ff000000)>>>24) > alphaTreshold) {
                pixel += 1;
            }
            bytedata[index++] = (byte) ((pixel));
            bytedata[index++] = (byte) ((pixel>>>8));
        }
        rgb.put(bytedata);
        texture.setup(rgb,
                bitmapHandler.getWidth(image),
                bitmapHandler.getHeight(image),
                ConstantValues.RGBA, ConstantValues.UNSIGNED_SHORT_5_5_5_1);
        return texture;
    }

    /**
     * Creates a luminance + alpha texture object from the specified bitmap.
     * Resulting texture will have 8 bits luminance (greyscale) + 8 bits of alpha for each pixel.
     * Note that this method has some processing overhead, if time critical consider
     * using converted texture that is saved to filesystem.
     * @param texture Texture object to store texture in or null to create new.
     * @param image Image to convert to greyscale + alpha texture.
     * @param bitmapHandler Bitmaphandler.
     * @return Texture object containing the image as a greyscale + alpha texture.
     * @throws IllegalArgumentException If image is null or not bitmap object, or if bitmapHandler
     * is null.
     */
    public static Texture2D createLuminanceAlphaTexture(Texture2D texture, Object image,
            BitmapHandler bitmapHandler) {
        if (image == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }

        if (texture == null) {
            texture = new Texture2D();
        }
        int[] pixels = bitmapHandler.getBitmapArray(image);
        //Get images greyscale values.
        int size = pixels.length;
        byte[] bytedata = new byte[size<<1];
        ByteBuffer luminance = ByteBuffer.allocateDirect(size<<1).
                           order(ByteOrder.nativeOrder());
        int index = 0;
        int value;
        for (int i = 0; i < size;i++) {
            value = pixels[i];
            bytedata[index++] = (byte) (value & 255);
            bytedata[index++] = (byte) ((value>>>24) & 255);
        }
        luminance.put(bytedata);
        texture.setup(luminance,
                bitmapHandler.getWidth(image),
                bitmapHandler.getHeight(image),
                ConstantValues.LUMINANCE_ALPHA, ConstantValues.UNSIGNED_BYTE);
        return texture;
    }


    /**
     * Creates a Texture object for the specified bitmap, based on the textureFormat.
     * @param source Source object to create texture from, if a compressed texture is used this
     * must be a java.nio.Buffer, otherwise it shall be the platform Bitmap object.
     * @param inputFormat The format of the source data, if
     * UNCOMPRESSED_TEXTURE_FORMAT then the source data must be a loaded image.
     * Allowed compressed texture formats:
     * CompressedTextureFormat.ETC1_RGB8_OES,
     * CompressedTextureFormat.ATC_RGB_AMD,
     * CompressedTextureFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD,
     * CompressedTextureFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD,
     * CompressedTextureFormat.S3TC_DXT1_RGB,
     * CompressedTextureFormat.S3TC_DXT1_RGBA
     * If a compressed texture is used then format must be set to the same value as inputFormat.
     * @param width Width of texture in pixels, if inputFormat is uncompressed this value
     * shall be -1
     * @param height Height of texture in pixels, if inputFormat is uncompressed this value
     * shall be -1
     * @param bitmapHandler
     * @param texture Texture object with defined parameters for the texture.
     * If inputFormat is UNCOMPRESSED_TEXTURE_FORMAT then the format and type is taken
     * from the texture. If a compressed input texture is used then the format and type
     * is discarded.
     * @throws IllegalArgumentException If inputFormat or format is invalid,
     * if inputFormat is compressed texture format AND format is not same value,
     * if bitmapHandler or source is null.
     */
    public void createTexture(Object source, int inputFormat, int width, int height,
            BitmapHandler bitmapHandler, Texture2D texture)  {
        if (source == null || bitmapHandler == null) {
            throw new IllegalArgumentException("Invalid parameter: null");
        }
        int format = texture.getFormat();
        if (inputFormat == UNCOMPRESSED_TEXTURE_FORMAT) {
            //Texture is not compressed.
            switch (format) {
                case ConstantValues.RGBA:
                    switch (texture.getType()) {
                        case ConstantValues.UNSIGNED_SHORT_5_5_5_1:
                        TextureUtils.create5551Texture(texture, source, 128, bitmapHandler);
                        break;
                        case ConstantValues.UNSIGNED_BYTE:
                            if (width == -1) {
                                width = bitmapHandler.getWidth(source);
                            }
                            if (height == -1) {
                                height = bitmapHandler.getHeight(source);
                            }
                            //If format and type is not defined then same as Bitmap format
                            //shall be used.
                            texture.setData(source);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "No support for uncompressed format and type: " +
                                    texture.getType());
                    }
                case ConstantValues.RGB:
                    switch (texture.getType()) {
                    case ConstantValues.UNSIGNED_BYTE:
                    TextureUtils.createRGBTexture(texture, source, bitmapHandler);
                    break;
                    default:
                        throw new IllegalArgumentException(
                                "No support for uncompressed format and type: " +
                                texture.getType());
                }

                case ConstantValues.LUMINANCE_ALPHA:
                    TextureUtils.createLuminanceAlphaTexture(texture, source, bitmapHandler);
                    break;
                case ConstantValues.LUMINANCE:
                    TextureUtils.createLuminanceTexture(texture, source, bitmapHandler);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown format: " + format);
            }
        } else {
            if (format != inputFormat) {
                throw new IllegalArgumentException("Format does not match compressed format: " +
                        format);
            }
            texture.setup((Buffer) source, width, height, inputFormat, -1);
        }

    }



}
