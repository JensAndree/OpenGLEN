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

package com.super2k.openglen.nibbler;

import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.Renderer;

/**
 * GLThread class that can run GL context.
 * This is made to run an implementation of the @link com.super2k.openglen.Renderer
 * @author Richard Sahlin
 *
 */
public class OpenGLENRunner implements Runnable {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Property to set the runnerclass, the runnerclass must be an instance of
     * CompatibilityRunner
     */
    public final static String PROPERTY_RUNNER_CLASS = "runnerClass";

    private final static String INVALID_PARAMETER_STR = "Cannot create class, parameter is NULL:";
    private static final long serialVersionUID = 1329455531863293970L;

    Renderer mRenderer;
    int mWidth;
    int mHeight;
    RenderSetting mRenderConfig;
    boolean mRunning = false;
    Thread mThread;
    CompatibilityRunner mRunner;
    InputStreamResolver mResolver;
    int mMinMikros = 10000; //If fps is above 100 it is capped.

    /**
     * Constructs a new OpenGLENRunner using the specified renderer
     * and starting the specified CompatibilityRunner
     * After the constructor is called the OpenGLENRunner is running.
     * @param renderer The renderer.
     * @param resolver Used to load assets
     * @param runnerClass The CompatibilityRunner class to create and drive using this thread.
     * @param width Width of display area in pixels.
     * @param height Height of display area in pixels.
     * @throws IllegalArgumentException If renderer, resourcePath or runnerClass is null or
     * if the runnerclass cannot be created.
     * @throws OpenGLENException If the renderer cannot be initialized
     */
    public OpenGLENRunner(Renderer renderer,InputStreamResolver resolver,
            CompatibilityRunner runnerClass,
            int width, int height) throws OpenGLENException {
        super();
        if (renderer == null || resolver == null || runnerClass == null) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR +
                    renderer + "," + resolver + "," + runnerClass);
        }
        mResolver = resolver;
        mRenderer = renderer;
        mRunner = runnerClass;
        mWidth = width;
        mHeight = height;
        start();
    }

    /**
     * Stops the thread running, when thread exits the renderer will be destroyed.
     */
    public void destroy() {
        mRunning = false;
    }

    /**
     * Creates a new Thread if one does not already exists.
     * The runnerclass is created and the thread is then started, driving the runner.
     * Note that the renderer must be initialized from the same thread doing the
     * GL calls, ie the thread started here.
     * @throws IllegalArgumentException If the runner class cannot be created.
     */
    public void start() {


        if (mThread == null) {
            mThread = new Thread(this);
        }
        mThread.start();

    }

    @Override
    public void run() {

        try {
            /*
             * Initializes the renderer, but does not start the renderer - that shall be done
             * by clients.
             */
            mRenderer.initRenderer();

            mRunning = true;
            mRunner.setup(mRenderer, mResolver, mWidth, mHeight);

            int ticks;
            while (mRunning) {

                if (mRunner.processFrame(null) == -1) {
                    destroy();
                    return;
                }

                ticks = mRenderer.getProfileInfo().getFrameTicks();
                if (ticks < mMinMikros) {
                    try {
                        Thread.sleep((mMinMikros - ticks) / 1000);
                    } catch (InterruptedException ie) {

                    }
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ie) {}
                    Thread.yield();
                }
            }
        } catch (OpenGLENException glene) {
            //Cant recover.
            throw new IllegalArgumentException(glene);
        } finally {
            mRunner.destroy();
            mRunner = null;
            if (mRenderer != null) {
                mRenderer.destroy();
                mRenderer = null;
            }

        }

    }

    /**
     * Returns the CompatibilityRunner that this thread is driving.
     * @return The instance of CompatibilityRunner that this thread is
     * driving.
     */
    public CompatibilityRunner getRunner() {
        return mRunner;
    }

}
