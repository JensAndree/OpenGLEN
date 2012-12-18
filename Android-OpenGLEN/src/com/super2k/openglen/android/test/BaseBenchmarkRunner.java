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

package com.super2k.openglen.android.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.android.OpenGLENActivity;
import com.super2k.openglen.test.PerformanceBenchmark;
import com.super2k.openglen.utils.ConfigurationParameters;

/**
 * Base class for benchmark runners, implements ProfileListener to get result from test.
 * Use the waitForResult() method to wait for result from benchmark.
 *
 * @author Richard
 *
 * @param <T> Class under test.
 */
public class BaseBenchmarkRunner<T extends Activity>
             extends ActivityInstrumentationTestCase2
             implements PerformanceBenchmark.ProfileListener{

    public BaseBenchmarkRunner(Class <T> activity) {
        super(activity);
    }

    protected final String TAG = getClass().getSimpleName();

    protected volatile BaseBenchmarkActivity mActivity;
    protected volatile ProfileInfo mProfileInfo;
    protected Instrumentation mInstrumentation;
    protected String mWindowFormat = BaseBenchmarkActivity.WINDOWFORMAT_RGBX_8888;
    //default is to disable clear function.
    protected String mClearFunc = ConfigurationParameters.CLEAR_FUNCTION_DISABLE;
    //default is to disable depth test.
    protected String mDepthTest = ConfigurationParameters.DEPTH_FUNCTION_DISABLE;

    public void onCreate(Bundle extra) {
        String str = extra.getString("hidenavigationbar");
        Log.d(TAG, str);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
    }

    @Override
    public void result(ProfileInfo profile) {
        Log.d(TAG, "Got profileinfo.");
        mProfileInfo = profile;
        mActivity.getPerformanceBenchmark().removeProfileListener(mActivity);
        mActivity.finish();
        mActivity = null;
    }

    /**
     * Adds this class as profilelistener and waits until a result is received.
     */
    protected void waitForResult() {

        SystemClock.sleep(500);
        while (!mActivity.hasStarted()) {
            Log.d(TAG, "OpenGLEN not started yet, waiting 500 ms");
            SystemClock.sleep(500);
        }
        //Has started, now we can set callbacklistener.
        mActivity.getPerformanceBenchmark().addProfileListener(this);
        Log.d(TAG, "Added ProfileListener, waiting for result");
        while (mActivity != null) {
            try {
                Thread.sleep(20);
            }
            catch (InterruptedException e) {
                //Cant do anything
            }
        }
        mInstrumentation.waitForIdleSync();

    }

    /**
     * Measures the delta between min and max value and asserts that the delta percentage
     * is lower than the limit. Ie limit < ((delta / minvalue) * 100)
     * @param values Values to calculate min, max and delta from.
     * @param index Index into array where reading of values start.
     * @param conunt Number of values to include.
     * @param limit Limit in percent, ie a value of 10 will make sure that the delta (max-min)
     * is lower than 10 percent of the min value.
     * @throws IllegalArgumentException if values is null or there is not count indices at
     * index.
     */
    protected void assertWithinLimit(float[] values, int index, int count, float limit) {
        if (values == null) {
            throw new IllegalArgumentException("Null array");
        }
        if (values.length < count + index) {
            throw new IllegalArgumentException("Not enough values at index: " +
                                                index + ", " + count);
        }
        float max = 0;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < count;i++) {
            if (values[i] > max) {
                max = values[i];
            } else {
                if (values[i] < min) {
                    min = values[i];
                }
            }
        }
        float delta = max - min;
        float diff = ((delta * 100) / min);
        assertTrue("Precision between tests failure, difference: " + diff + "%", diff < limit);

    }

    /**
     * Start a test with benchmark parameters, internal method for conveniance.
     * EGL will be set to 16 bit depth, 888 rgb.
     * Depth test and clear will be taken from member variables.
     *
     * @param bundle The bundle to put properties in
     * @param ticksThreshold Ticks threshold for framerate
     * @param framecount Number of frames to run test for,
     * @param shading Shading string
     * @param textureFormat Specified texture format (luminance, luminance_alpha, rgb or RGB5_A1) or
     * compressed texture format. If textureFormat is set then bitmapFormat is disregarded.
     * @param orientation Orientation
     * @param packageStr The package of class under test
     * @param instrClass Class under test
     */
    protected void startTest(Bundle bundle, int ticksThreshold, int framecount,
                             String shading, String textureFormat,
                             String orientation,
                             String packageStr, Class<T> instrClass) {

        BaseBenchmarkActivity.setBenchmarkProperties(bundle,
                                                     shading, null, textureFormat,
                                                     ticksThreshold,
                                                     framecount);
        OpenGLENActivity.setEGLProperties(bundle, 16, 8, 8, 8, 0);
        OpenGLENActivity.setActivityProperties(bundle ,orientation, mWindowFormat, true);
        ConfigurationParameters.setOpenGLENProperties(mClearFunc, mDepthTest, false);
        mActivity = (BaseBenchmarkActivity)launchActivity(packageStr, instrClass, bundle);

    }
    /**
     * Start a test with benchmark parameters, internal method for conveniance.
     * EGL will be set to 16 bit depth, 888 rgb.
     * Depth test and clear will be disabled and multisampling disabled. Usage of VBO can be
     * turned on or off.
     *
     * @param bundle The bundle to put properties in
     * @param ticksThreshold Ticks threshold for framerate
     * @param framecount Number of frames to run test for,
     * @param shading Shading string
     * @param bitmapFormat Bitmap format
     * @param orientation Orientation
     * @param useVBO True to use vertex buffer objects, false otherwise
     * @param packageStr The package of class under test
     * @param instrClass Class under test
     */
    protected void startTest(Bundle bundle, int ticksThreshold, int framecount,
                             String shading, String bitmapFormat, String orientation,
                             boolean useVBO, String packageStr, Class<T> instrClass) {

        BaseBenchmarkActivity.setBenchmarkProperties(bundle,
                                                     shading, bitmapFormat, null,
                                                     ticksThreshold,
                                                     framecount);
        OpenGLENActivity.setEGLProperties(bundle, 16, 8, 8, 8, 0);
        OpenGLENActivity.setActivityProperties(bundle ,orientation, mWindowFormat, true);
        ConfigurationParameters.setUseVBO(useVBO);
        ConfigurationParameters.setOpenGLENProperties(mClearFunc, mDepthTest, false);
        mActivity = (BaseBenchmarkActivity)launchActivity(packageStr, instrClass, bundle);

    }


    /**
     * Start a test with benchmark parameters, internal method for conveniance.
     * Depth test and clear will be disabled and multisampling disabled. Objects will use VBO
     *
     * @param bundle The bundle to put properties in
     * @param depthbits Number of depth buffer bits
     * @param redbits Number of red bits
     * @param greenbits Number of green bits
     * @param bluebits Number of blue bits
     * @param ticksThreshold Ticks threshold for framerate
     * @param framecount Number of frames to run test for,
     * @param shading Shading string
     * @param bitmapFormat Bitmap format
     * @param orientation Orientation
     * @param packageStr The package of class under test
     * @param instrClass Class under test
     */
    protected void startTest(Bundle bundle, int depthbits, int redbits, int greenbits, int bluebits,
            int ticksThreshold, int framecount,
            String shading, String bitmapFormat, String orientation,
            String packageStr, Class<T> instrClass) {

        BaseBenchmarkActivity.setBenchmarkProperties(bundle,
                                            shading, bitmapFormat, null,
                                            ticksThreshold,
                                            framecount);
        OpenGLENActivity.setEGLProperties(bundle, depthbits, redbits, greenbits, bluebits, 0);
        OpenGLENActivity.setActivityProperties(bundle ,orientation, mWindowFormat, true);
        ConfigurationParameters.setOpenGLENProperties(mClearFunc, mDepthTest, false);
        mActivity = (BaseBenchmarkActivity)launchActivity(packageStr, instrClass, bundle);

    }

}
