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
package com.super2k.openglen.j2se.utils;

import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Log;

/**
 * J2SE Implementation of GraphicsLibraryHandler
 * @author Richard Sahlin
 *
 */
public class J2SEGraphicsLibraryHandler extends GraphicsLibraryHandler {

    private final static String TAG = "J2SEGraphicLib";

    protected GL2ES2 mGles2;

    /**
     * Constructs a new J2SE graphics library handler for GLES2.
     * @param gles2
     * @throws IllegalArgumentException If gles2 is NULL.
     */
    public J2SEGraphicsLibraryHandler(GL2ES2 gles2) {
        if (gles2==null) {
            throw new IllegalArgumentException(GLES_NULL_STR);
        }
        mGles2 = gles2;
    }

    @Override
    public void setupGL(RenderSetting setting) {
        setRenderSetting(setting);
        setting.clearDirty();
        mGles2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void setRenderSetting(RenderSetting setting) {
        int flags = setting.getChangeFlag();
        if ((flags & RenderSetting.CHANGE_FLAG_CLEARCOLOR) != 0) {
            float[] clear = setting.getClearColor();
            mGles2.glClearColor(clear[0], clear[1], clear[2], clear[3]);

        }
        if ((flags & RenderSetting.CHANGE_FLAG_CULLFACE) != 0) {
            // Set GL values.
            if (setting.getCullFace()!=ConstantValues.NONE) {
                mGles2.glEnable(GL.GL_CULL_FACE);
                mGles2.glCullFace(setting.getCullFace());
            } else
                mGles2.glDisable(GL.GL_CULL_FACE);
        }
        if ((flags & RenderSetting.CHANGE_FLAG_DEPTH) != 0) {

            mGles2.glClearDepthf(setting.getClearDepth());
            mGles2.glDepthRangef(setting.getDepthRangeNear(), setting.getDepthRangeFar());

            if (setting.getDepthFunc() != ConstantValues.NONE) {
                mGles2.glEnable(GL.GL_DEPTH_TEST);
                mGles2.glDepthFunc(setting.getDepthFunc());
            } else {
                mGles2.glDisable(GL.GL_DEPTH_TEST);
            }
            mGles2.glDepthRangef(setting.getDepthRangeNear(), setting.getDepthRangeFar());
        }
        if ((flags & RenderSetting.CHANGE_FLAG_MULTISAMPLE) != 0) {
            if (setting.isMultisampling())
                mGles2.glEnable(GL.GL_MULTISAMPLE);
            else
                mGles2.glDisable(GL.GL_MULTISAMPLE);
        }

    }

    @Override
    public int checkError() {
        int error = mGles2.glGetError();
        if (error!=GL.GL_NO_ERROR) {
            Log.d(TAG, "GL Error: " + error);
        }
        return error;
    }

    @Override
    public void logConfig(Object egl, Object eglDisplay, Object config, int loglevel) {
        throw new IllegalArgumentException("not implemented");

    }

    @Override
    public int getUniformLocation(int program, String name) {
        return mGles2.glGetUniformLocation(program, name);
    }

    @Override
    public void genBuffers(int count, int[] names, int offset) {
        mGles2.glGenBuffers(count, names, offset);
    }

    @Override
    public void bindBuffer(int target, int buffer) {
        mGles2.glBindBuffer(target, buffer);
    }

    @Override
    protected int internalBufferData(int target, int size, Buffer data, int usage) {
        //Call should be OK unless out of memory.
        mGles2.glBufferData(target, size, data, usage);
        return mGles2.glGetError();

    }

    @Override
    public void deleteBuffers(int count, int[] names, int offset) {
        mGles2.glDeleteBuffers(count, names, offset);
    }

    @Override
    public int getEGLConfigAttrib(Object egl, Object eglDisplay, Object config, int attribute) {
        //EGL does not seem to be implemented in JOGAMP. Can not find the corresponding method.
        int[] attribs = new int[1];
        return attribs[0];
    }

    @Override
    public void clearError() {
        while (mGles2.glGetError()!=GL.GL_NO_ERROR);
    }

    @Override
    public void enable(int cap) {
        mGles2.glEnable(cap);
    }

    @Override
    public void disable(int cap) {
        mGles2.glDisable(cap);
    }

    @Override
    public void blendFunc(int sourceFactor, int destFactor) {
        mGles2.glBlendFunc(sourceFactor, destFactor);
    }

    @Override
    protected String internalGetString(int name) {
        return mGles2.glGetString(name);
    }

    @Override
    protected void internalGetInteger(int pname, int[] value, int offset) {
        mGles2.glGetIntegerv(pname, value, offset);
    }

    @Override
    protected void internalGetFloat(int pname, float[] value, int offset) {
        mGles2.glGetFloatv(pname, value, offset);
    }

    @Override
    protected void internalGetBoolean(int pname, boolean[] value, int offset) {
        byte[] bools = new byte[value.length];
        mGles2.glGetBooleanv(pname, bools, offset);
        for (int i = 0; i<bools.length; i++) {
            if (bools[i]>0) {
                value[i] = true;
            } else {
                value[i] = false;
            }
        }
    }

    @Override
    protected void internalClearBuffer(int flags) {
        mGles2.glClear(flags);
    }

    @Override
    protected void internalClearDepth(float depth) {
        mGles2.glClearDepth(depth);
    }

    @Override
    protected void internalClearColor(float[] colors, int offset) {
        mGles2.glClearColor(colors[offset++], colors[offset++], colors[offset++], colors[offset++]);
    }

    @Override
    protected void internalClearStencil(int stencil) {
        mGles2.glClearStencil(stencil);
    }

}
