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

package com.super2k.openglen.android.texture;

import java.nio.Buffer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.texture.TextureHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;


/**
 * Android texture handler, implementation of Android specific texture functions.
 * @author Richard Sahlin
 *
 */
public class AndroidTextureHandler extends TextureHandler {


    /**
     * Constructs a new AndroidTextureHandler using the specified graphicslibrary.
     * @param graphicsLibrary
     * @throws IllegalArgumentException If graphicsLibrary is NULl.
     */
    public AndroidTextureHandler(GraphicsLibraryHandler graphicsLibrary) {
        super(graphicsLibrary);
    }

    @Override
    protected void internalGenerateTextureNames(int count, int[] names, int offset) {
        GLES20.glGenTextures(count, names, offset);
    }

    @Override
    protected int internalTexImage2D(int level, int width, int height,
            int format, int type, Object pixels) {
        if (pixels == null) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,level, format,
                    width, height, 0, format, type, null);
        } else if (pixels instanceof Bitmap) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,level, (Bitmap)pixels, 0);
        } else if (pixels instanceof Buffer) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,level, format,
                    width, height, 0, format, type, (Buffer) pixels);
        }
        else {
            throw new IllegalArgumentException(INVALID_PIXEL_OBJECT_STR);
        }
        return GLES20.glGetError();

    }

    @Override
    protected int internalBindTexture2D(int target, int texture) {
        GLES20.glBindTexture(target, texture);
        return GLES20.glGetError();
    }

    @Override
    protected void internalActiveTexture(int texture) {
        GLES20.glActiveTexture(texture);
    }

    @Override
    protected void internalGenFrameBuffers(int count, int[] array, int offset) {
        GLES20.glGenFramebuffers(count,  array, offset);
    }

    @Override
    protected int internalBindFrameBuffer(int frameBuffer) {
        GLES20.glBindFramebuffer(ConstantValues.FRAMEBUFFER, frameBuffer);
        return GLES20.glGetError();
    }

    @Override
    protected int internalFrameBufferTexture2D(int attachment, int textarget, int texture,
            int level) {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                                        attachment, textarget, texture, level);
        return GLES20.glGetError();

    }

    @Override
    protected int internalCheckFrameBufferStatus() {
        return GLES20.glCheckFramebufferStatus(ConstantValues.FRAMEBUFFER);
    }

    @Override
    public int texParameter2D(int pname, int param) {
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, pname, param);
        return GLES20.glGetError();
    }

    @Override
    protected int internalGenerateMipMap(int target) {
        GLES20.glGenerateMipmap(target);
        return GLES20.glGetError();
    }

    @Override
    protected int internalPixelstore(int pname, int param) {
        GLES20.glPixelStorei(pname, param);
        return GLES20.glGetError();
    }

    @Override
    protected int internalCompressedTexImage2D(int level, int internalFormat,
                                        int width, int height, int size, Buffer data) {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glCompressedTexImage2D(GLES20.GL_TEXTURE_2D,
                                    level, internalFormat, width, height,0 , size, data);
        return GLES20.glGetError();

    }

    @Override
    protected int internalFrameBufferRenderbuffer(int attachement, int renderbuffer) {
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
                                         attachement, GLES20.GL_RENDERBUFFER, renderbuffer);
        return GLES20.glGetError();
    }

    @Override
    protected void internalGenRenderbuffers(int count, int[] array, int offset) {
        GLES20.glGenRenderbuffers(count, array, offset);

    }

    @Override
    protected int internalBindRenderBuffer(int renderBuffer) {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer);
        return GLES20.glGetError();
    }

    @Override
    protected int internalRenderBufferStorage(int internalformat, int width, int height) {
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, internalformat, width, height);
        return GLES20.glGetError();
    }


    @Override
    protected void internalDeleteTextures(int count, int[] names, int offset) {
        GLES20.glDeleteTextures(count, names, offset);
    }


}
