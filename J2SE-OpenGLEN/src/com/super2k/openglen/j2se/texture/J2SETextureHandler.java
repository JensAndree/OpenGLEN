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
package com.super2k.openglen.j2se.texture;


import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.texture.TextureHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;

/**
 * J2SE implementation of texture handler
 * @author Richard Sahlin
 *
 */
public class J2SETextureHandler extends TextureHandler {

    protected GL2ES2 mGles2;

    /**
     * Constructs a new J2SE texture handler.
     * @param Gles2
     * @param graphicsLibrary The graphics library to use with the texturehandler.
     * @throws IllegalArgumentException if gles2 or graphicsLibrary is NULL
     */
    public J2SETextureHandler(GL2ES2 gles2, GraphicsLibraryHandler graphicsLibrary)  {
        super(graphicsLibrary);
        if (gles2 == null)  {
            throw new IllegalArgumentException(GLES_NULL_STR);
        }
        mGles2 = gles2;

    }


    @Override
    public void internalGenerateTextureNames(int count, int[] names, int offset) {
        mGles2.glGenTextures(count, names, offset);
    }

    @Override
    protected int internalTexImage2D(int level, int width, int height,
            int format, int type, Object pixels) {

        if (pixels == null) {
            mGles2.glTexImage2D(GL.GL_TEXTURE_2D, level, format,
                    width , height, 0, format, type, null);
        }else if (pixels instanceof BufferedImage) {
            BufferedImage bimg = (BufferedImage) pixels;
            int w = bimg.getWidth();
            int h = bimg.getHeight();
            WritableRaster writeRaster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    w, h, 4, null);
            ComponentColorModel componentModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                new int[] {8,8,8,8},true, false, ComponentColorModel.TRANSLUCENT,
                                                DataBuffer.TYPE_BYTE);
            BufferedImage source = new BufferedImage(componentModel, writeRaster, false, null);
            source.getGraphics().drawImage(bimg,0, 0, null);

            DataBufferByte RGBAPixels = (DataBufferByte) writeRaster.getDataBuffer();

            ByteBuffer buff = ByteBuffer.wrap(RGBAPixels.getData());
            mGles2.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA,
                                w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buff.rewind());
        } else if (pixels instanceof Buffer) {
            mGles2.glTexImage2D(GL.GL_TEXTURE_2D,level, format,
                    width, height, 0, format, type, (Buffer) pixels);

        } else {
            throw new IllegalArgumentException(INVALID_PIXEL_OBJECT_STR);
        }
        return mGles2.glGetError();

    }


    @Override
    protected int internalBindTexture2D(int target, int texture) {
        mGles2.glBindTexture(target, texture);
        return mGles2.glGetError();
    }


    @Override
    protected void internalActiveTexture(int texture) {
        mGles2.glActiveTexture(texture);

    }
    @Override
    protected void internalGenFrameBuffers(int count, int[] array, int offset) {
        mGles2.glGenFramebuffers(count,  array, offset);
    }


    @Override
    protected int internalBindFrameBuffer(int frameBuffer) {
        mGles2.glBindFramebuffer(ConstantValues.FRAMEBUFFER, frameBuffer);
        return mGles2.glGetError();
    }


    @Override
    protected int internalFrameBufferTexture2D(int attachment,
                                            int textarget,
                                            int texture,
                                            int level) {
        mGles2.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, attachment,
                                        textarget, texture, level);
        return mGles2.glGetError();

    }


    @Override
    protected int internalCheckFrameBufferStatus() {
        return mGles2.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
    }


    @Override
    public int texParameter2D(int pname, int param) {
        mGles2.glTexParameterf(GL.GL_TEXTURE_2D, pname, param);
        return mGles2.glGetError();
    }


    @Override
    protected int internalGenerateMipMap(int target) {
        mGles2.glGenerateMipmap(target);
        return mGles2.glGetError();
    }


    @Override
    protected int internalPixelstore(int pname, int param) {
        mGles2.glPixelStorei(pname, param);
        return mGles2.glGetError();
    }


    @Override
    protected int internalCompressedTexImage2D(int level, int internalFormat,
                                        int width, int height, int size, Buffer data) {
        mGles2.glCompressedTexImage2D(GL.GL_TEXTURE_2D,
                                    level, internalFormat, width, height,size, 0, data);
        return mGles2.glGetError();

    }


    @Override
    protected int internalFrameBufferRenderbuffer(int attachement, int renderbuffer) {
        mGles2.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
                                        attachement,
                                        GL.GL_RENDERBUFFER, renderbuffer);
        return mGles2.glGetError();

    }


    @Override
    protected void internalGenRenderbuffers(int count, int[] array, int offset) {
        mGles2.glGenRenderbuffers(count, array, offset);

    }


    @Override
    protected int internalBindRenderBuffer(int renderBuffer) {
        mGles2.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderBuffer);
        return mGles2.glGetError();
    }


    @Override
    protected int internalRenderBufferStorage(int internalformat, int width, int height) {
        mGles2.glRenderbufferStorage(GL.GL_RENDERBUFFER, internalformat, width, height);
        return mGles2.glGetError();
    }


    @Override
    protected void internalDeleteTextures(int count, int[] names, int offset) {
        mGles2.glDeleteTextures(count, names, offset);
    }

}
