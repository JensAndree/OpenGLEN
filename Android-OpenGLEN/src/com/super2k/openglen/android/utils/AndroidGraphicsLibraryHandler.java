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
package com.super2k.openglen.android.utils;

import java.nio.Buffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.util.Log;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Logger;
import com.super2k.openglen.utils.LoggerFactory;

public class AndroidGraphicsLibraryHandler extends GraphicsLibraryHandler {

    public final String TAG = getClass().getSimpleName();

    private final static Logger logger = LoggerFactory.createLogger();

    @Override
    public void setupGL(RenderSetting setting) {
        setRenderSetting(setting);
        setting.clearDirty();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void setRenderSetting(RenderSetting setting) {

        int flags = setting.getChangeFlag();
        if ((flags & RenderSetting.CHANGE_FLAG_CLEARCOLOR) != 0) {
            float[] clear = setting.getClearColor();
            GLES20.glClearColor(clear[0], clear[1], clear[2], clear[3]);

        }
        if ((flags & RenderSetting.CHANGE_FLAG_DEPTH) != 0) {

            GLES20.glClearDepthf(setting.getClearDepth());
            GLES20.glDepthRangef(setting.getDepthRangeNear(), setting.getDepthRangeFar());

        }
        if ((flags & RenderSetting.CHANGE_FLAG_CULLFACE) != 0) {
            // Set GL values.
            if (setting.getCullFace()!=ConstantValues.NONE) {
                GLES20.glEnable(GL10.GL_CULL_FACE);
                GLES20.glCullFace(setting.getCullFace());
            } else
                GLES20.glDisable(GL10.GL_CULL_FACE);
        }
        if ((flags & RenderSetting.CHANGE_FLAG_DEPTH) != 0) {
            if (setting.getDepthFunc()!=ConstantValues.NONE) {
                GLES20.glEnable(GL10.GL_DEPTH_TEST);
                GLES20.glDepthFunc(setting.getDepthFunc());
            } else
                GLES20.glDisable(GL10.GL_DEPTH_TEST);
            GLES20.glDepthRangef(setting.getDepthRangeNear(), setting.getDepthRangeFar());
        }
        if ((flags & RenderSetting.CHANGE_FLAG_MULTISAMPLE) != 0) {
            if (setting.isMultisampling())
                GLES20.glEnable(GL10.GL_MULTISAMPLE);
            else
                GLES20.glDisable(GL10.GL_MULTISAMPLE);
        }

    }

    @Override
    public int checkError() {

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR)  {
            Log.d(TAG,"GL Error: " + error);
        }
        return error;
    }

    @Override
    public void logConfig(Object eglObject, Object eglDisplayObject,
                          Object configObject, int loglevel) {

        EGL10 egl = (EGL10) eglObject;
        EGLDisplay eglDisplay = (EGLDisplay) eglDisplayObject;
        EGLConfig config = (EGLConfig) configObject;
        logger.logMessage(TAG, "EGL config RGBA and depth:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_RED_SIZE) + "," +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_GREEN_SIZE) + "," +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_BLUE_SIZE) + "," +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_ALPHA_SIZE) + ", depth " +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_DEPTH_SIZE), loglevel);
        int transparentType = getEGLConfigAttrib(egl, eglDisplay, config,
                                                EGL10.EGL_TRANSPARENT_TYPE);
        if (transparentType == EGL10.EGL_TRANSPARENT_RGB)   {
            com.super2k.openglen.utils.Log.d(TAG, "EGL Transparent RGB:" +
                    getEGLConfigAttrib(egl, eglDisplay, config,
                                       EGL10.EGL_TRANSPARENT_RED_VALUE) + "," +
                    getEGLConfigAttrib(egl, eglDisplay, config,
                                       EGL10.EGL_TRANSPARENT_GREEN_VALUE) + "," +
                    getEGLConfigAttrib(egl, eglDisplay, config,
                                       EGL10.EGL_TRANSPARENT_BLUE_VALUE));
        }
        else
            com.super2k.openglen.utils.Log.d(TAG, "EGL No transparent RGB");
        //#define EGL_VG_COLORSPACE_LINEAR_BIT    0x0020  /* EGL_SURFACE_TYPE mask bits */
        //#define EGL_VG_ALPHA_FORMAT_PRE_BIT 0x0040  /* EGL_SURFACE_TYPE mask bits */
        //#define EGL_MULTISAMPLE_RESOLVE_BOX_BIT 0x0200  /* EGL_SURFACE_TYPE mask bits */
        //#define EGL_SWAP_BEHAVIOR_PRESERVED_BIT 0x0400  /* EGL_SURFACE_TYPE mask bits */

        int surfaceType = getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_SURFACE_TYPE);
        if ((surfaceType & EGL10.EGL_PBUFFER_BIT) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: PBUFFER", loglevel);
        }
        if ((surfaceType & EGL10.EGL_WINDOW_BIT) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: WINDOW", loglevel);
        }
        if ((surfaceType & EGL10.EGL_PIXMAP_BIT) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: PIXMAP", loglevel);
        }
        if ((surfaceType & 0x020) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: VG_COLORSPACE_LINEAR", loglevel);
        }
        if ((surfaceType & 0x040) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: VG_ALPHA_FORMAT_PRE", loglevel);
        }
        if ((surfaceType & 0x200) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: MULTISAMPLE_RESOLVE_BOX", loglevel);
        }
        if ((surfaceType & 0x400) != 0) {
            logger.logMessage(TAG, "EGL SurfaceType: SWAP_BEHAVIOR_PRESERVED_BIT", loglevel);
        }
        logger.logMessage(TAG, "EGL MAX Pbuffer pixels:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_MAX_PBUFFER_PIXELS),
                loglevel);
        logger.logMessage(TAG, "EGL Samplebuffers:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_SAMPLE_BUFFERS) +
                ", samples:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_SAMPLES),
                loglevel);
        logger.logMessage(TAG, "EGL Stencil bits:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_STENCIL_SIZE),
                loglevel);
        logger.logMessage(TAG, "EGL Level:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_LEVEL) +
                ", native visual ID:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_NATIVE_VISUAL_ID) +
                ", native type:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_NATIVE_VISUAL_TYPE)
                + ", native renderable:" +
                getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_NATIVE_RENDERABLE), loglevel);

        int configCaveat = getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_CONFIG_CAVEAT);
        if (configCaveat == EGL10.EGL_NONE)
            logger.logMessage(TAG, "EGL config caveat NONE", loglevel);
        if (configCaveat == EGL10.EGL_SLOW_CONFIG)
            logger.logMessage(TAG, "EGL config caveat SLOW_CONFIG", loglevel);
        if (configCaveat == EGL10.EGL_NON_CONFORMANT_CONFIG)
            logger.logMessage(TAG, "EGL config caveat NON_CONFORMANT", loglevel);


    }


    @Override
    public int getUniformLocation(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    @Override
    public void genBuffers(int count, int[] names, int offset) {
        GLES20.glGenBuffers(count, names, offset);
    }

    @Override
    public void bindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
    }
    @Override
    public int internalBufferData(int target, int size, Buffer data, int usage) {

        //Call should be OK unless out of memory.
        GLES20.glBufferData(target, size, data, usage);
        return GLES20.glGetError();

    }

    @Override
    public void deleteBuffers(int count, int[] names, int offset) {
        clearError();
        GLES20.glDeleteBuffers(count, names, offset);
        int error;
        if ((error = GLES20.glGetError()) != GL10.GL_NO_ERROR) {
            Log.d(TAG, "GL error calling glDeleteBuffers: " + error);
        }
    }

    @Override
    public int getEGLConfigAttrib(Object egl, Object eglDisplay, Object config, int attribute) {
        if (egl == null || eglDisplay == null || config == null)    {
            throw new IllegalArgumentException("Parameters may not be NULL");
        }
        int[] attribs = new int[1];
        ((EGL10)egl).eglGetConfigAttrib((EGLDisplay)eglDisplay, (EGLConfig)config, attribute, attribs);
        return attribs[0];
    }

    @Override
    public void clearError() {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);

    }

    @Override
    public void enable(int cap) {
        GLES20.glEnable(cap);

    }

    @Override
    public void disable(int cap) {
        GLES20.glDisable(cap);
    }

    @Override
    public void blendFunc(int sourceFactor, int destFactor) {
        GLES20.glBlendFunc(sourceFactor, destFactor);

    }

    @Override
    protected String internalGetString(int name) {
        return GLES20.glGetString(name);
    }

    @Override
    protected void internalGetInteger(int pname, int[] value,int offset) {
        GLES20.glGetIntegerv(pname, value, offset);
    }

    @Override
    protected void internalGetFloat(int pname, float[] value, int offset) {
        GLES20.glGetFloatv(pname, value, offset);
    }

    @Override
    protected void internalGetBoolean(int pname, boolean[] value, int offset) {
        GLES20.glGetBooleanv(pname, value, offset);
    }

    @Override
    protected void internalClearBuffer(int flags) {
        GLES20.glClear(flags);
    }

    @Override
    protected void internalClearDepth(float depth) {
        GLES20.glClearDepthf(depth);
    }

    @Override
    protected void internalClearColor(float[] colors, int offset) {
        GLES20.glClearColor(colors[offset++],colors[offset++],colors[offset++],colors[offset++]);
    }

    @Override
    protected void internalClearStencil(int stencil) {
        GLES20.glClearStencil(stencil);
    }


}
