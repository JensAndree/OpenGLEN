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

import com.super2k.openglen.Renderer;

/**
 * Interface to help testing of OpenGLEN rendering.
 * The normal scenario would be an Nibbler application that
 * is setup to test on multiple platforms (Android/J2SE),this
 * needs to be invoked from the testclass and result needs
 * to be checked.
 * This interface contains methods to help with this.
 * @author Richard Sahlin
 *
 */
public interface OpenGLENTester {

    /**
     * Class holding resultbuffer, width and height of rendered area.
     * @author Richard Sahlin
     *
     */
    public class ResultBuffer {
        public int[] mBuffer; //Buffer pixel data
        public int mWidth; //width of buffer in pixels, one int per pixel.
        public int mHeight; //height of buffer in pixels.

        public ResultBuffer(int[] buffer, int width, int height) {
            mBuffer = buffer;
            mWidth = width;
            mHeight = height;
        }
    }

    /**
     * Returns the Renderer
     * @return The renderer
     * @throws IllegalArgumentException If renderer is not initialized (null)
     */
    public Renderer getRenderer();

    /**
     * Fetches the resultbuffer, this shall be implemented
     * by the testcase so that the result can be checked.
     * Note that not all testcases needs to check the output-buffer.
     * @return The buffer with the result pixels
     * @throws IllegalArgumentException If called before the testcase has
     * finished.
     */
    public ResultBuffer getResultBuffer();

    /**
     * Adds a listener for the result.
     * @param callback The result listener.
     */
    public void addResultListener(ResultListener callback);

    /**
     * Interface for reporting when a result buffer is available.
     * This is for instance used when testing the visual output
     * of the renderer.
     * @author Richard Sahlin
     *
     */
    public interface ResultListener {
        /**
         * Called when the testcase has finished to report the result.
         * @param result The buffer result of the testcase containing the rendered output.
         */
        public void result(ResultBuffer result);
    }


}
