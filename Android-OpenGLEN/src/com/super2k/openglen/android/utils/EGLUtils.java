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

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class EGLUtils {

    public final static String TAG = "EGLUtils";

/**
 * Calls getEGLConfigAttrib and returns the value for the specified attribute.
 * @param egl
 * @param eglDisplay
 * @param config
 * @param configAttrib
 * @return
 */
public static int getEGLConfigAttrib(EGL10 egl, EGLDisplay eglDisplay, EGLConfig config, int configAttrib)       {
    int[] attribs = new int[1];
    egl.eglGetConfigAttrib(eglDisplay, config, configAttrib, attribs);
    return attribs[0];
}

/**
 * Select the config that is what we asked for
 * @param egl
 * @param eglDisplay
 * @param configs
 * @param configSpec My configuration specification.
 * @param count
 * @return The exact matching EGLConfig or null
 */
public final static EGLConfig selectConfig(EGL10 egl,
                EGLDisplay eglDisplay,
                EGLConfig[] configs,
                int[] configSpec,
                int count)   {

    int[] result = new int[(configSpec.length-1)>>>1];

    int index;
    for (int i = 0; i < count; i++)     {
        index = 0;
        for (int attrib = 0;attrib < result.length;attrib++) {
            result[attrib] = getEGLConfigAttrib(egl, eglDisplay, configs[i], configSpec[index]);
            index += 2;
        }
        //Verify parameters
        index = 1;
        boolean match = true;
        for (int attrib = 0;attrib < result.length;attrib++) {
//          if (result[attrib] < configSpec[index] || result[attrib] > configSpec[index]) {
          if (result[attrib] < configSpec[index]) {
                match = false;
                break;
            }
            index += 2;
        }
        if (match) {
            return configs[i];
        }
    }

    return null;

}

public final static String getError(int error) {

    switch (error)  {
        case EGL10.EGL_BAD_ACCESS:
            return "EGL_BAD_ACCESS";
        case EGL10.EGL_BAD_ALLOC:
            return "EGL_BAD_ALLOC";
        case EGL10.EGL_BAD_ATTRIBUTE:
            return "EGL_BAD_ATTRIBUTE";
        case EGL10.EGL_BAD_CONFIG:
            return "EGL_BAD_CONFIG";
        case EGL10.EGL_BAD_CONTEXT:
            return "EGL_BAD_CONTEXT";
        case EGL10.EGL_BAD_CURRENT_SURFACE:
            return "EGL_BAD_CURRENT_SURFACE";
        case EGL10.EGL_BAD_DISPLAY:
            return "EGL_BAD_DISPLAY";
        case EGL10.EGL_BAD_MATCH:
            return "EGL_BAD_MATCH";
        case EGL10.EGL_BAD_NATIVE_PIXMAP:
            return "EGL_BAD_PIXMAP";
        case EGL10.EGL_BAD_NATIVE_WINDOW:
            return "EGL_BAD_NATIVE_WINDOW";
        case EGL10.EGL_BAD_PARAMETER:
            return "EGL_BAD_PARAMETER";
        case EGL10.EGL_BAD_SURFACE:
            return "EGL_BAD_SURFACE";
        case EGL10.EGL_NOT_INITIALIZED:
            return "EGL_NOT_INITIALIZED";
    }

    return "UNKNOWN (" + error + ")";

}


}
