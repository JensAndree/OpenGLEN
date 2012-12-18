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

/**
 * Interface for classes that handle the screen rendering, this is usually done
 * via a graphics library such as OpenGL or Direct3D (but is implementation specific)
 * This internface is for a renderer that has EGL,
 * this shall be used when the application wants to have full control of EGL
 * On Android this is clients that wants to use a SurfaceView and do not want to use
 * the GLSurfaceView provided by the Android platform.
 *
 * The underlying graphics library is abstracted from the caller of this API, some functions are
 * very similar to GL functions, eg setViewPort() but it is the intention that clients shall not need
 * any specific GL knowledge in order to use this API.
 * All graphics library functions are handled in the renderer implementations.
 * @see com.super2k.openglen.android.GLES20Renderer
 * @author Richard Sahlin
 */
public interface EGLRenderer extends Renderer {

    /**
     * Setup the native (window) system - on some implementations this may not be necessary.
     * If the source is an Android GLSurfaceView this method shall NOT be implemented.
     * In case of powersave events this method may be called again to re-enable rendering.
     * NOTE! This method shall be called from the same thread that will call the other EGL/GL related methods.
     * @param eglDisplay The EGL display to create EGL on, or null for the default display.
     * @return The EGL configuration
     * @throws OpenGLENException If no configuration is set, or configuration is invalid.
     * This normally means that the EGL cannot match the specified configuration.
     */
    Object createEGL(Object display) throws OpenGLENException;

    /**
     * Wait for rendering to complete, issue a swapbuffer command, normally eglSwapBuffers
     * @return The status of the swapbuffer operation. True if buffers where swapped. False will be returned if
     * the
     * @note This method can be used together with flush()/glFinish() when more controll of the rendering is needed.
     * If EGL has not been created (eg if a GLSUrfaceView is used) this method will do nothing.
     */
    boolean swapBuffers();

    /**
     * Release the EGL, destroy the EGL context and display.
     * After this method is called it is possible to call createEGL() create a new instance.
     */
    void releaseEGL();


    /**
     * Returns the EGL configuration object, on Android this is javax.microedition.khronos.egl.EGLConfig;
     * @return
     */
    Object getEGLConfig();

}
