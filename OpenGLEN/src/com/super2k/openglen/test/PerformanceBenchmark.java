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
package com.super2k.openglen.test;

import com.super2k.openglen.ProfileInfo;

/**
 * Interface for a performance benchmark that can report result
 * back when finished.
 * @author Richard Sahlin
 *
 */
public interface PerformanceBenchmark {

    /**
     * Interface for listening to profile result when a performance testrun has
     * completed.
     * @author Richard Sahlin
     *
     */
    public interface ProfileListener {
        /**
         * Callback to recieve profile data from the benchmark/testcase.
         * @param profile
         */
        public void result(ProfileInfo profile);
    }

    /**
     * Sets the bitmap and texture format to be used for the test.
     * Valid values are
     * FORMAT_ARGB8888
     * FORMAT_RGB565
     * FORMAT_ARGB4444
     * @param bitmapFormat
     * @throws IllegalArgumentException if bitmapFormat is invalid.
     */
    public void setBitmapFormat(int bitmapFormat);
    /**
     * Sets the number of frames for one performance run.
     * @param frames The number of frames to run the test for.
     * @throws IllegalArgumentException If frames is <= 0
     */
    public void setPerformanceRun(int frames);

    /**
     * Sets the threshold for load increase of tests in mikroseconds.
     * If the deltatime between frames is below this value then more load is put by the test.
     * @param mikros
     * @throws IllegalArgumentException If mikros is negative.
     */
    public void setTickTreshold(int mikros);

    /**
     * Adds a listener to recieve callback when performance benchmark has
     * finished.
     * @param listener The callback reciever.
     * @throws IllegalArgumentException If listener is null.
     */
    public void addProfileListener(ProfileListener listener);

    /**
     * Removes the listener from the callback provider. The listener will not get
     * performance updates.
     * @param listener The listener to remove.
     * @throws IllegalArgumentException If listener is null.
     */
    public void removeProfileListener(ProfileListener listener);

    /**
     * Returns the number of listeners currently attached.
     * @return Number of current listeners.
     */
    public int getListenerCount();

}
