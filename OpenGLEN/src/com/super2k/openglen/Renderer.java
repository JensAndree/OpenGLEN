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

import java.util.Vector;

import com.super2k.openglen.lighting.Light;
import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;
import com.super2k.openglen.program.ProgramHandler;
import com.super2k.openglen.texture.TextureHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;

/**
 * Interface for classes that handle the screen rendering, this is usually done
 * via a graphics library such as OpenGL or Direct3D (but is implementation
 * specific) The underlying graphics library is abstracted from the caller of
 * this API, some functions are very similar to GL functions, eg setViewPort()
 * but it is the intention that clients shall not need any specific GL knowledge
 * in order to use this API. All graphics library functions are handled in the
 * renderer implementations.
 *
 * @see com.super2k.android.GLES20Renderer
 * @author Richard Sahlin
 */
public interface Renderer {

    /**
     * The first state, renderer created.
     */
    final static int STATE_CREATED = 0;

    /**
     * State when renderer has been initialized.
     */
    final static int STATE_INITIALIZED = 1;

    /**
     * State when renderer is started, this means that rendering can take place.
     */
    final static int STATE_STARTED = 2;

    /**
     * Init the renderer using the specified configuration (normally set in the
     * constructor) This shall only allocate internal fields that needs a valid
     * GL context Do not load programs, that shall be done in startRenderer when
     * caller has had time to set inputstreamresolver.
     *
     * @throws IllegalArgumentException If configuration is not set or is
     *             invalid
     * @throws OpenGLENException If there is an error initializing the renderer.
     */
    public void initRenderer() throws OpenGLENException;

    /**
     * Starts the renderer, loading any shader programs needed and settin up GL.
     * When this method returns the renderer is ready to begin rendering.
     * Rendering is controlled by calling. beginFrame(...) renderNode(..) - one
     * or more render calls The renderer is initialized with one lightsource by
     * default. In case of pause events this method may be called again to
     * re-enable rendering.
     *
     * @throws OpenGLENException If shaders cannot be created/compiled or linked.
     * @throws IlletalStateException If renderer is not initialized.
     */
    public void startRenderer() throws OpenGLENException;

    /**
     * Sets the projection to orthogonal. This will affect all render calls made
     * after this call.
     *
     * @param left The left coordinate for the clipping plane
     * @param right The right coordinate for the clipping plane
     * @param bottom The bottom coordinate for the clipping plane
     * @param top The top coordinate for the clippping plane
     * @param near Distance to near clipping plane
     * @param far Distance to far clipping plane.
     */
    public void setOrthogonalProjection(float left, float right, float bottom, float top,
            float near, float far);

    /**
     * Marks the beginning of a new frame.
     * @throws IllegalStateException If renderer is not started, this is done by calling
     * startRenderer()
     *
     */
    public void beginFrame();

    /**
     * Marks the end of a frame, this means that all rendering is complete.
     * Usually call this method after frame has been updated to screen and all rendering
     * calls have finished.
     */
    public void endFrame();

    /**
     * Issues a call to the underlying graphics context that rendering commands
     * shall be flushed. This will normally be a glFlush(); Is intended to be
     * used when full control of the renderloop is needed.
     */
    public void flush();

    /**
     * Issues a call to the graphics context that it should wait for processing
     * (in this context) to finish. When this method returns all processing on
     * that context has finished.
     */
    public void finish();

    /**
     * Reads the contents of the colorbuffer and stores in an int array. Red,
     * green, blue and alpha values are fetched. Note that this method is very
     * expensive, performance wise. It is thought to be used for debugging and
     * verification purposes.
     *
     * @param x X position of area to read.
     * @param y Y position of area to read.
     * @param width Width of area to read.
     * @param height Height of area to read.
     * @param format ConstantValues.RGB, ConstantValues.RGBA or ConstantValues.ALPHA
     * @return
     * @throws IllegalArgumentException if format is not RGB,RGBA or ALPHA, width or height is
     * negative.
     */
    public int[] readPixels(int x, int y, int width, int height, int format);

    /**
     * Destroys the renderer and release all resources, after this is called all
     * methods will fail. Use this when the renderer shall not be used anymore.
     */
    public void destroy();

    /**
     * Renders a list containing (sorted) GLBLitObjects. The renderer will
     * traverse the list from beginning to end and send each of the objects to
     * GL (in order). If alpha is used transparent objects must come in correct
     * order.
     *
     * @param objectList
     */
    public void renderGLBlitObjects(Vector<GLBlitObject> objectList);

    /**
     * Renders a list containing (sorted) GLParticleArray object. The renderer
     * will traverse the list from beginning to end and send each of the
     * particle arrays to GL.
     *
     * @param objectList
     */
    public void renderGLParticleArray(Vector<GLParticleArray> objectList);

    /**
     * Sets the perspective matrix - this matrix will be concatenated with
     * object matrixes before an object is displayed. The source matrix is
     * copied into the perspective matrix at the time of the call. When this
     * method returns the new matrix is effective in the perspective operations
     * being done.
     *
     * @param matrix The perspective or orthogonal matrix.
     */
    public void setPerspectiveMatrix(float[] matrix);

    /**
     * Sets the position of the specified light, lightnumber must be within a
     * valid range. This can be queried by the getMaxLights() method - if
     * lightNumber >= getMaxLights nothing is done. The renderer shall store a
     * reference to the light passed to this method. This means that clients can
     * manipulate the light after this method is called and the result will be
     * read by the renderer.
     *
     * @param lightNumber Numbering starts at O, up to getMaxLights() -1
     * @param light The light to set (reference)
     */
    public void setLight(int lightNumber, Light light);

    /**
     * Return the max number of (simultaneous) lights that the renderer
     * supports.
     *
     * @return Max number of lights that are allowed.
     */
    public int getMaxLights();

    /**
     * Returns the texture handler used with this renderer.
     *
     * @return The TextureHandler
     */
    public TextureHandler getTextureHandler();

    /**
     * Returns the ProgramCollection for the specified blit type This can be
     * used to for instance change where shaders are fetched from.
     *
     * @param classname The GLObject instance to fetch program collection for,
     *            must be valid implementation of GLObject, GLBlitObject or
     *            GLParticleArray.
     * @return The ProgramCollection that will be used for the specified object
     *         type.
     * @throws IllegalArgumentException If no ProgramCollection could be found
     *             for the specified object type.
     */
    // public ProgramCollection getProgramCollection(String classname);

    /**
     * Returns the platform specific implementation of graphics library
     * utilities to be used with this renderer.
     *
     * @return The platform specific implementation of the graphics library
     *         utilities.
     */
    public GraphicsLibraryHandler getGraphicsUtilities();

    /**
     * Returns the platform specific implementation for handling bitmaps. This
     * is utility functions to help loading and managing bitmaps in a platform
     * independent way.
     *
     * @return The BitmapHandler platform specific implementation. TODO: Move
     *         bitmap handler to a better place - should not be with renderer
     */
    public BitmapHandler getBitmapHandler();

    /**
     * Returns the platform specific program handler {@link ProgramHandler}
     * This can be used when more controll over shaders is needed.
     * @return The program handler.
     */
    public ProgramHandler getProgramHandler();

    /**
     * Sets the viewport of the renderer, these values will be used to calculate
     * the window coordinates. By default it is set to 0, 0, width, height.
     *
     * @param x Left side of the viewport rectangle, in pixels
     * @param y Lower side of the viewport rectangle, in pixels.
     * @param width Width of the viewport, in pixels
     * @param height Height of the viewport, in pixels
     */
    public void setViewPort(int x, int y, int width, int height);

    /**
     * Rotate the scene by angle a (in degrees) around the axis (x, y, z) Note
     * that all rotation will be replaced by a call to perspective/ortho or
     * setPerspectiveMatrix
     *
     * @param angle Angle of rotation, in degrees
     * @param xaxis Scale factor x axis
     * @param yaxis Scale factor y axis
     * @param zaxis Scale factor z axis
     */
    public void rotateScene(float angle, float xaxis, float yaxis, float zaxis);

    /**
     * Returns the settings for this renderer, this controls backface culling,
     * depthtest etc. Change the values of the settings while rendering and the
     * new settings will be used when beginFrame() is called.
     *
     * @return The rendersettings
     */
    public RenderSetting getRenderSetting();

    /**
     * Returns profile information. This information is updated each frame, in
     * the object that is returned here, ie a reference can be kept and does not
     * need to be fetched each frame.
     *
     * @return Object containing profile information
     */
    public ProfileInfo getProfileInfo();

}
