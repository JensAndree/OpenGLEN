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

import android.os.Bundle;

import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.android.OpenGLENActivity;
import com.super2k.openglen.benchmark.BaseBenchmarkProgram;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.nibbler.CompatibilityRunner;
import com.super2k.openglen.test.PerformanceBenchmark;
import com.super2k.openglen.test.PerformanceBenchmark.ProfileListener;
import com.super2k.openglen.texture.TextureUtils;
import com.super2k.openglen.utils.Log;
/**
 * Base class for Android OpenGLEN Benchmark Activity.
 * Class has common data and functions needed for an activity that does
 * benchmarking using OpenGLEN and PET.
 * The runner class must implement the PerformanceBenchmark interface.
 * @author Richard Sahlin
 *
 */
public class BaseBenchmarkActivity extends OpenGLENActivity implements ProfileListener {

    /**
     * Parameter to change number of quads for each layer. A value of 1 will
     * create layers with 1 quad each, larger values will divide the quad on
     * both x and y axis. A value of 10 will produce 100 quads for a layer.
     * Valid values are 1 - MAX_VERTEX_FACTOR
     * Set int value in Bundle with key
     * VERTEX_FACTOR_STR
     *
     */
    public final static String VERTEX_FACTOR_KEY = "divisor";

    public final static int MAX_VERTEX_FACTOR = 1000;

    /**
     * Limits the fps, this means that the BaseBenchmarkProgram will sleep to force a lower
     * fps if needed
     */
    public final static String MAX_FPS_KEY = "maxfps";

    /**
     * Parameter for setting shading, this controls the material shading
     * for the layers on screen.
     * Set int value in Bundle with key SHADING_STR
     * unlit - Just a texture lookup
     * colored - texel multiplied by ARGB color (texture2D * Vec4)
     * lit - Simple lighting, light fallof according to normal (texture2D * float * Vec4)
     * lambert - Lambert model with diffuse and ambient light
     * (texture2D * float + (float * Vec4) * Vec4 + Vec4
     * phong - Per pixel phong, recalculated reflection vector per pixel.
     * (dot, normalize, pow, Vec4 * Vec4 + Lambert)
     *
     */
    public final static String SHADING_KEY = "shading";

    /**
     * The benchmark program base, extended by the implemented performance test.
     */
    CompatibilityRunner mProgram;

    /**
     * The class implementing the PerformanceBenchmark interface.
     */
    PerformanceBenchmark mBenchmark;

    /**
     * Index into the SHADING tables so that the String definitions are usable
     * from other classes. Use when setting SHADING_KEY into Bundle to control shading
     * eg SHADING_TABLE[UNLIT_SHADINGX]
     */
    public final static int SHADING_UNLIT = 0;
    public final static int SHADING_COLOR = 1;
    public final static int SHADING_LIT = 2;
    public final static int SHADING_LAMBERT = 3;
    public final static int SHADING_PHONG = 4;

    /**
     * The defined values for the shading, set in the Bundle as String property.
     */
    public final static String[] SHADING_TABLE = new String[] {
            "unlit",
            "colored",
            "lit",
            "lambert",
            "phong"};

    protected final static int[] SHADING_VALUE_TABLE = new int[] {Material.SHADING_UNLIT,
            Material.SHADING_COLORED,
            Material.SHADING_LIT,
            Material.SHADING_LAMBERT,
            Material.SHADING_PHONG};

    /**
     * Index into BITMAP_FORMAT_STR_TABLE so that the String definitions are usable
     * from other classes. Used when setting BITMAP_FORMAT_KEY into Bundle to control bitmap format.
     * eg
     */
    public final static int BITMAP_RGBA8888 = 0;
    public final static int BITMAP_RGB565 = 2;
    public final static int BITMAP_RGBA4444 = 3;

    /**
     * Property for setting the bitmap format used in benchmark.
     * Note that not all benchmarks will use this.
     * Valid values are defined in the BITMAP_FORMAT_VALUE_TABLE
     */
    public final static String BITMAP_KEY = "bitmapformat";

    protected final static int[] BITMAP_VALUE_TABLE= new int[] {
            BitmapHandler.FORMAT_ARGB8888,
            BitmapHandler.FORMAT_ARGB8888,
            BitmapHandler.FORMAT_RGB565,
            BitmapHandler.FORMAT_ARGB4444,
            BitmapHandler.FORMAT_ARGB4444 };

    /**
     * The defined bitmap format String value, set in the Bundle as String property.
     */
    public final static String[] BITMAP_TABLE = new String[] {
            "RGBA8888",
            "AGBA8888",
            "RGB565",
            "ARGB4444",
            "RGBA4444" };

    /**
     * This variable controls the number of frames in a performance run.
     * After the benchmark has settled this number of frames is counted and
     * the benchmark is finished.
     */
    public final static String RUN_LENGTH_KEY = "runlength";

    /**
     * This is the tick treshold for detecting if more load should be put on the test.
     * The figure is number of MIKRO (not milli) seconds elapsed since last frame.
     * eg, a value of 20000 will settle framerate lower than 50 fps, ie as long as
     * fps is higher than 50 the test will put more load on. The last load increase may push
     * the fps quite a way below 50 fps depending on how the test behaves.
     * It is recommended to keep this value at around 18000.
     */
    public final static String TICKS_TRESHOLD_KEY = "tickTreshold";


    protected int eglDepthBits = -1;

    protected int mShading = -1;
    protected int mBitmapFormat = BitmapHandler.FORMAT_ARGB8888;
    protected int mTextureFormat = TextureUtils.UNCOMPRESSED_TEXTURE_FORMAT;
    protected int mDivision = 1;
    /**
     * Set this value to force a max fps, will sleep if delta time is lower than this.
     * ie a value of 30 will force delta times between frames to be at least 30.
     */
    protected int mMinDelta = 0;

    /**
     * Number of frames for a performance run
     */
    protected int mRunLength = 0;

    /**
     * Ticks threshold for increasing load, set to benchmark if defined in bundle.
     */
    protected int mTicksThreshold = -1;

    /**
     * This is the main startingpoint for the activity, must use empty constructor.
     */
    public BaseBenchmarkActivity() {
    }

    /**
     * Sets the benchmark properties in a Bundle, call this method before the activity
     * is started.
     * @param bundle The Bundle to set the properties in
     * @param shading The shading str as defined by SHADING_TABLE_STR or null if no shading
     * @param bitmapFormat The bitmap format str as defined by BITMAP_STR_TABLE,
     * or null if no bitmap format.
     * @param textureFormat The texture format to use, or null if not required.
     * @param ticksTreshold Mikrosecond limit for when test increases load, if deltatime between
     * frames is lower than this value then increaseLoad() is called. A value of 20000 will
     * settle framerate BELOW 50 fps. Normally use around 18000.
     * @param testRunLength Number of frames to run benchmark test for, after framerate
     * has settled. Set to 0 for infinite number of frames.
     */
    public final static void setBenchmarkProperties(Bundle bundle,
            String shading, String bitmapFormat,
            String textureFormat,
            int ticksThreshold, int testRunLenth) {
        if (shading != null) {
            bundle.putString(SHADING_KEY, shading);
        }
        if (bitmapFormat != null) {
            bundle.putString(BITMAP_KEY, bitmapFormat);
        }
        bundle.putString(TICKS_TRESHOLD_KEY, Integer.toString(ticksThreshold));
        bundle.putString(RUN_LENGTH_KEY, Integer.toString(testRunLenth));

        //Must clear value if not defined otherwise old settings will stay.
        if (textureFormat != null) {
            System.setProperty(BaseBenchmarkProgram.PROPERTY_BENCHMARK_TEXTURE_FORMAT,
                    textureFormat);
        } else {
            System.setProperty(BaseBenchmarkProgram.PROPERTY_BENCHMARK_TEXTURE_FORMAT,
                    TextureUtils.UNCOMPRESSED);
        }


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        Bundle extra = getIntent().getExtras();
        if (extra != null) {

            //Note that the division variable may not be read by all benchmarks.
            String division = extra.getString(VERTEX_FACTOR_KEY);
            if (division != null) {
                Log.d(TAG, "divisor=" + division);
                mDivision = Integer.parseInt(division);
                if (mDivision < 1 || mDivision > MAX_VERTEX_FACTOR) {
                    throw new IllegalArgumentException("Invalid vertex factor value " + mDivision);
                }
            }

            //Read the bundle for parameters and call super.
            String shadingStr = extra.getString(SHADING_KEY);
            if (shadingStr != null) {
                Log.d(TAG, "Shading:" + shadingStr);
                for (int i = 0; i < SHADING_TABLE.length; i++) {
                    if (shadingStr.equalsIgnoreCase(SHADING_TABLE[i])) {
                        mShading = SHADING_VALUE_TABLE[i];
                        Log.d(TAG, "mShading=" + mShading);
                        break;
                    }
                }
                if (mShading == -1) {
                    throw new IllegalArgumentException("Invalid shading:" + shadingStr);
                }
            }

            String maxfps = extra.getString(MAX_FPS_KEY);
            if (maxfps != null) {
                mMinDelta = 1000 / Integer.parseInt(maxfps);
            }

            String formatStr = extra.getString(BITMAP_KEY);
            if (formatStr != null) {
                mBitmapFormat = -1;
                for (int i = 0; i < BITMAP_TABLE.length; i++) {
                    if (formatStr.equalsIgnoreCase(BITMAP_TABLE[i])) {
                        mBitmapFormat = BITMAP_VALUE_TABLE[i];
                        break;
                    }
                }
                if (mBitmapFormat == -1) {
                    throw new IllegalArgumentException("Invalid " + BITMAP_KEY + ": " +
                                                       formatStr);
                }
            }

            String runLengthStr = extra.getString(RUN_LENGTH_KEY);
            if (runLengthStr != null) {
                mRunLength = Integer.parseInt(runLengthStr);

                if (mRunLength <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid benchmark runlength value:" + mRunLength);
                }
            }

            String thresholdStr = extra.getString(TICKS_TRESHOLD_KEY);
            if (thresholdStr != null) {
                mTicksThreshold = Integer.parseInt(thresholdStr);
            }

        }
        //This will set the OpenGLEN view and setOpenGLENView will be called.
        //Do this last
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setOpenGLENView()   {
        super.setOpenGLENView();
        /**
         * Runner is  now instantiated
         */
        mProgram = mOpenGLENThread.getRunner();
        /**
         * If runner implements PerformanceBenchmark set the bitmap format.
         */
        if (mProgram instanceof PerformanceBenchmark) {
            mBenchmark = ((PerformanceBenchmark)mProgram);
            mBenchmark.setBitmapFormat(mBitmapFormat);
            mBenchmark.setPerformanceRun(mRunLength);
            if (mTicksThreshold != -1) {
                mBenchmark.setTickTreshold(mTicksThreshold);
            }
            mBenchmark.addProfileListener(this);
        } else {
            throw new IllegalArgumentException("Runner class must implement PerformanceBenchmark");
        }

    }

    @Override
    public void onStop()       {
        super.onStop();

    }

    /**
     * Returns the class implementing the testcase.
     * Do not call before the onCreate method.
     * @return The class implementing the testcase.
     * @throws IllegalStateException If called before onCreate method has finished.
     */
    public PerformanceBenchmark getPerformanceBenchmark() {
        if (mBenchmark == null) {
            throw new IllegalStateException("Illegal state, PerformanceBenchmark is null.");
        }
        return mBenchmark;
    }

    @Override
    public void result(ProfileInfo profile) {
        //This will be called of the benchmark program implements the PerformanceBenchmark
        StringBuffer result = new StringBuffer();
        int[] frameTicks = profile.getFrameTicksBuffer();
        int column = 0;
        for (int i = 0; i < frameTicks.length; i++) {
            result.append(frameTicks[i]);
            column++;
            if (i < frameTicks.length-1) {
                result.append(", ");
            }
            if (column > 8) {
                column = 0;
                result.append("\n");
            }
        }
        Log.d(TAG, result.toString());
        //If this is the only listener then terminate the activity.
        if (mBenchmark.getListenerCount() == 1) {
            Log.d(TAG, "Only this class is listening on profile result, remove and finish()");
            mBenchmark.removeProfileListener(this);
            finish();
        }
    }

}
