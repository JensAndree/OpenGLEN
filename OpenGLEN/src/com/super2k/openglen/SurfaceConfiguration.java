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
 * Class that specifies the surface configuration, bitdepth, zbufferm samples and other
 * surface specific configurations.
 * Use with the @link Renderer
 * @author Richard Sahlin
 */
public class SurfaceConfiguration {

    public final static int DEFAULT_RED_BITS = 8;
    public final static int DEFAULT_GREEN_BITS = 8;
    public final static int DEFAULT_BLUE_BITS = 8;
    public final static int DEFAULT_ALPHA_BITS = 8;
    public final static int DEFAULT_DEPTH_BITS = 16;
    public final static int DEFAULT_SAMPLES = 0;

    protected final static String INVALID_SAMPLES_STRING = "Invalid samples value";

    /**
     * The display red bit (colour) depth
     */
    protected int           mRedBits = DEFAULT_RED_BITS;
    /**
     * The display green bit (colour) depth
     */
    protected int           mGreenBits = DEFAULT_GREEN_BITS;
    /**
     * The display blue bit (colour) depth
     */
    protected int           mBlueBits = DEFAULT_BLUE_BITS;

    /**
     * Number of bits to use for alpha.
     */
    protected int           mAlphaBits = DEFAULT_ALPHA_BITS;

    /**
     * Number of bits to use for the depth buffer.
     */
    protected int           mDepthBits = DEFAULT_DEPTH_BITS;

    /**
     * Number of samples to require in SampleBuffers.
     */
    protected int           mSamples = DEFAULT_SAMPLES;

    /**
     * Constructs a SurfaceConfiguration that can used when
     * selecting EGL configuration.
     * All parameters are set to default values.
     */
    public SurfaceConfiguration() {
    }

    /**
     *
     * @param redBits Number of red bits
     * @param greenBits Number of green bits
     * @param blueBits Number of blue bits
     * @param alphaBits Number of alpha bits
     * @param depthBits Number of depth bits
     * @param samples The number of sampels for each pixel.
     */
    public SurfaceConfiguration(int redBits, int greenBits, int blueBits, int alphaBits, int depthBits, int samples) {

        this.mRedBits = redBits;
        this.mGreenBits = greenBits;
        this.mBlueBits = blueBits;
        this.mAlphaBits = alphaBits;
        this.mDepthBits = depthBits;
        this.mSamples = samples;

    }

    /**
     * Return the number of bits to use for red.
     * @return Number of bits to use for red.
     */
    public int getRedBits() {
        return mRedBits;
    }
    /**
     * Return the number of bits to use for green.
     * @return Number of bits to use for green.
     */
    public int getGreenBits() {
        return mGreenBits;
    }
    /**
     * Return the number of bits to use for blue.
     * @return Number of bits to use for blue.
     */
    public int getBlueBits() {
        return mBlueBits;
    }

    /**
     * Return the number of bits to use for alpha.
     * @return Number of alpha bits.
     */
    public int getAlphaBits() {
        return mAlphaBits;
    }

    /**
     * Return the number of bits to use for depth buffer.
     *
     * @return Number of bits to use in depth buffer.
     */
    public int getDepthBits() {
        return mDepthBits;
    }

    /**
     * Return the number of samples required for this configuration.
     * @return The number of samples required for this configuration.
     */
    public int getSamples() {
        return mSamples;
    }

    /**
     * Sets the wanted number of samples for the EGL buffer.
     * @param samples The number of samples
     * @throws IllegalArgumentException If samples is negative
     */
    public void setSamples(int samples) {
        if (samples < 0)    {
            throw new IllegalArgumentException(INVALID_SAMPLES_STRING);
        }
        mSamples = samples;
    }

    /**
     * Sets the number of wanted redbits, at least this value - may get a config with more.
     * @param redbits
     */
    public void setRedBits(int redbits){
        mRedBits = redbits;
    }
    /**
     * Sets the number of wanted greenbits, at least this value - may get a config with more.
     * @param greenbits
     */
    public void setGreenBits(int greenbits){
        mGreenBits = greenbits;
    }
    /**
     * Sets the number of wanted bluebits, at least this value - may get a config with more.
     * @param bluebits
     */
    public void setBlueBits(int bluebits){
        mBlueBits = bluebits;
    }
    /**
     * Sets the number of wanted alphabits, at least this value - may get a config with more.
     * @param alphabits
     */
    public void setAlphaBits(int alphabits){
        mAlphaBits = alphabits;
    }
    /**
     * Sets the number of wanted depthbits, at least this value - may get a config with more.
     * @param depthbits
     */
    public void setDepthBits(int depthbits){
        mDepthBits = depthbits;
    }

}
