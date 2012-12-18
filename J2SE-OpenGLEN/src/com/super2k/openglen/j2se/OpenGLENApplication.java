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
package com.super2k.openglen.j2se;

import java.util.StringTokenizer;

import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.SurfaceConfiguration;
import com.super2k.openglen.nibbler.CompatibilityRunner;
import com.super2k.openglen.nibbler.OpenGLENRunner;
import com.super2k.openglen.utils.ConfigurationParameters;
import com.super2k.openglen.utils.Log;

/**
 * Base class for a J2SE OpenGLEN application.
 * Call the startOpenGLEN to create a window and start OpenGLEN.
 * @author Richard Sahlin
 *
 */
public class OpenGLENApplication {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Property to set the resource path.
     */
    public final static String PROPERTY_RESOURCE_PATH = "resourcePath";
    /**
     * Property to set the screen (window) size
     */
    public final static String PROPERTY_SCREEN_SIZE = "screensize";
    /**
     * Default window width on J2SE
     */
    protected int mWindowWidth = 1980;
    /**
     * Default window height on J2SE
     */
    protected int mWindowHeight = 1080;

    protected String mRunnerClass = null;
    protected String mResourcePath = null;
    protected OpenGLENWindow mGlenWindow;

    public OpenGLENApplication() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        OpenGLENApplication myApp = new OpenGLENApplication();
        myApp.startOpenGLEN(args);

    }

    /**
     * Set the window width and height, will be overridden by Java arguments passed to the main
     * method.
     * This method must be called before calling startOpenGLEN otherwise size change will not
     * be reflected in window size.
     * @param width
     * @param height
     */
    public void setWindowSize(int width, int height) {
        mWindowWidth = width;
        mWindowHeight = height;
    }

    /**
     * Constructs a new OpenGLENApplication using the specified shader source.
     * A window will be created and a renderer will be attached to it.
     * An OpenGLENThread will be created and started.
     * @param shaderSource Path to where shaders are
     * @param resourcePath Path to resources.
     * @param runnerClass CompatibilityRunner implementation that will be called in the OpenGLENThread
     */
    public void startOpenGLEN(String[] args) {

        try {

            checkProperties(args);

            if (mRunnerClass == null) {
                Log.d(TAG, "No runner class set, nothing to start. Set argument 'runnerClass'");
                throw new IllegalArgumentException("No runner class is set");
            }
            if (mResourcePath == null) {
                Log.d(TAG, "No resource path set. Set argument '" + PROPERTY_RESOURCE_PATH + "'");
                throw new IllegalArgumentException("No resource path is set");
            }

            try {

                Class<?> rClass = Class.forName(mRunnerClass);
                createWindow(mWindowWidth, mWindowHeight, (CompatibilityRunner)rClass.newInstance(),
                        mResourcePath);

            } catch (ClassNotFoundException cnfe) {
                Log.d(TAG, "Could not create class " + mRunnerClass);
                throw new IllegalArgumentException(cnfe);
            } catch (InstantiationException ie) {
                Log.d(TAG, "Could not instantiate class " + mRunnerClass);
                throw new IllegalArgumentException(ie);
            } catch (IllegalAccessException ia) {
                Log.d(TAG, "Could not access class " + mRunnerClass);
                throw new IllegalArgumentException(ia);
            }

        } catch (OpenGLENException glene) {
            //Cannot recover.
            throw new RuntimeException(glene);

        }
    }

    /**
     * Check for system properties and commandline properteis to set runnerclass,
     * resourcepath and windowsize.
     * @param args
     */
    public void checkProperties(String[] args) {

        //Check system properties.
        String runnerClass = System.getProperty(OpenGLENRunner.PROPERTY_RUNNER_CLASS);
        String resourcePath = System.getProperty(PROPERTY_RESOURCE_PATH);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {

                if (args[i].toLowerCase().startsWith(
                        OpenGLENRunner.PROPERTY_RUNNER_CLASS.toLowerCase() + "=")) {
                    mRunnerClass = args[i].substring(12);
                } else if (args[i].toLowerCase().startsWith(
                        PROPERTY_RESOURCE_PATH.toLowerCase() + "=")) {
                    resourcePath = args[i].substring(13);
                } else if (args[i].toLowerCase().startsWith("windowsize=")
                        || args[i].toLowerCase().startsWith("screensize=")) {
                    int index = args[i].indexOf(',');
                    if (index == -1) {
                        throw new IllegalArgumentException(
                                "windowsize parameter should have 'width,height'");
                    }
                    mWindowWidth = Integer.parseInt(args[i].substring(11, index));
                    mWindowHeight = Integer.parseInt(args[i].substring(index + 1));
                }

            }
        }
        //Fetch screensize parameter from systemproperty if set.
        String screensize = System.getProperty(PROPERTY_SCREEN_SIZE);
        if (screensize != null) {
            StringTokenizer st = new StringTokenizer(screensize, ",");
            mWindowWidth = Integer.parseInt(st.nextToken());
            mWindowHeight = Integer.parseInt(st.nextToken());
            if (mWindowWidth <= 0 || mWindowHeight <= 0) {
                throw new IllegalArgumentException("Width and height must be > 0");
            }
        }
        if (runnerClass != null) {
            mRunnerClass = runnerClass;
        }
        if (resourcePath != null) {
            mResourcePath = resourcePath;
        }


    }

    /**
     * Creates the window that can be used to attach a Renderer to.
     * @param width
     * @param height
     * @param runner Runner to attach to thread, this is the implementation that will drive
     * updates in the window.
     * @param resourcePath The path to where resources are loaded from.
     * @throws OpenGLENException If renderer cannot be started.
     * @throws IllegalArgumentException If resourcePath or runner is null
     */
    protected void createWindow(int width, int height, CompatibilityRunner runner,
            String resourcePath) throws OpenGLENException {
        if (resourcePath == null || runner == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        SurfaceConfiguration surfaceConfig = new SurfaceConfiguration();
        RenderSetting renderSetting = new RenderSetting();
        ConfigurationParameters.getSurfaceConfiguration(surfaceConfig);
        ConfigurationParameters.getRenderConfiguration(renderSetting);
        mGlenWindow = new OpenGLENWindow(surfaceConfig, renderSetting, width, height, resourcePath,
                          runner);

    }

    /**
     * Returns the compatibilityrunner that is used.
     * @return The CompatibilityRunner
     */
    public CompatibilityRunner getCompatibilityRunner() {
        return mGlenWindow.mGlThread.getRunner();
    }

}
