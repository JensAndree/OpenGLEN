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
package com.super2k.openglen.animation;

/**
 * Sinusbased animation with support for animating the
 * amplitude and speed of the sinus
 * @author Richard Sahlin
 *
 */
public class SinoidAnimation extends SinusAnimation {

    /**
     * Used for picking new random amplitude and animating to them.
     */
    protected float[] mAmplitudeValues;
    protected float[] mTimeFactorValues;
    protected float[] mAmplitudeTarget;
    protected float[] mTimeFactorTarget;
    protected LinearAnimation mAmplitudeAnim;
    protected LinearAnimation mTimeFactorAnim;

    /**
     * Creates a sine animation, setup to animate amplitude and timefactor (speed)
     * The target values may have different sine speed and amplitude.
     * Target array may not contain less values than start-values.
     * setTimeFactorAnimation and setAmplitudeAnimation must be called before animation is used.
     * @param target The target for the animation,
     * must have at least the same length as start-values.
     * @param amplitude Array with start amplitude values, must be same size as timeFactor
     * @param timeFactor Array with start timeFactor values, must be same size as amplitude
     * @throws IllegalArgumentException If target, amplitude or timefactor is null
     * or does not contain the correct number of values.
     */
    public SinoidAnimation(float[] target,
                           float[] amplitude,
                           float[] timeFactor) {
        if (amplitude == null || timeFactor == null ||
            timeFactor.length != amplitude.length) {
            throw new IllegalArgumentException("Invalid parameter, null or too few values.");
        }
        int numberOfInValues = amplitude.length;
        super.init(numberOfInValues);
        mTarget = target;
        mTimeFactor = timeFactor;
        mAmplitude = amplitude;
        mStartTime = new float[numberOfInValues]; //Creates array with 0 starttime.
        mSineAdd = new float[numberOfInValues];   //Creates array with 0 offsets
        mTimeFactorTarget = new float[numberOfInValues];
        mTimeFactorValues = new float[numberOfInValues];
        mAmplitudeTarget = new float[numberOfInValues];
        mAmplitudeValues = new float[numberOfInValues];
        mAmplitudeAnim = new LinearAnimation();
        mTimeFactorAnim = new LinearAnimation();

    }

    @Override
    public boolean animate(float timeDelta) {
        mAmplitudeAnim.animate(timeDelta);
        mTimeFactorAnim.animate(timeDelta);
        return super.animate(timeDelta);

    }

    /**
     * Returns true if the timefactor animation has finished.
     * Will not return true if loop is enabled for timefactor animation.
     * @return True if timefactor animation has finished, false otherwise.
     */
    public boolean isTimeFactorFinished() {
        return mTimeFactorAnim.isFinished();
    }

    /**
     * Returns true if the amplitude animation has finished.
     * Will not return true if loop is enabled for amplitude animation.
     * @return True if amplitude animation has finished, false otherwise.
     */
    public boolean isAmplitudeFinished() {
        return mAmplitudeAnim.isFinished();
    }

    /**
     * Sets a new animation target for the time factor, the previous target
     * values will be copied to start values.
     * Call this when new values for timefactor animation are needed, changing the timefactor
     * will alter the speed of the sinuswave.
     * Remember to call setTimeFactor before calling this method to properly
     * setup the timeFactor start value.
     * @param targetTimeFactor Float array with target values for each axis used.
     * @param endTime End/Target time for animation in seconds.
     * @param loop LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     * @throws IllegalArgumentException If targetTimeFactor is null or does not contain
     * the same number of values as used as target when setting up this animation.
     *
     */
    public void setTimeFactorAnimation(float[] targetTimeFactor, float endTime, int loop) {
        if (targetTimeFactor == null || targetTimeFactor.length != mTimeFactorTarget.length) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        //Copy previous target to start values.
        System.arraycopy(mTimeFactorTarget,0, mTimeFactorValues, 0, mTimeFactorTarget.length);
        mTimeFactorAnim.setup(mTimeFactorTarget,
                                            mTimeFactorValues,
                                            targetTimeFactor,
                                            0, endTime, loop);
    }

    /**
     * Sets a new animation target for the amplitude, the previous target
     * values will be copied to start values.
     * Call this when new values for amplitude animation are needed, changing the ampplitude
     * will alter the height of the sinuswave. ie alter the height of the target values
     * specified when creating this animation.
     * Remember to call setAmplitude before calling this method to properly
     * setup the amplitude start.
     * @param amplitudeTarget Array with amplitude target values.
     * @param endTime End/target time for this animation in seconds.
     * @param loop LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     * @throws IllegalArgumentException If amplitudeTarget is null or does not contain
     * the same number of values as used as target when setting up this animation.
     */
    public void setAmplitudeAnimation(float[] amplitudeTarget, float endTime, int loop) {
        if (amplitudeTarget == null || amplitudeTarget.length != mTimeFactorTarget.length) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        System.arraycopy(mAmplitudeTarget,0, mAmplitudeValues, 0, mAmplitudeTarget.length);
        mAmplitudeAnim.setup(mAmplitudeTarget,
                            mAmplitudeValues,
                            amplitudeTarget,
                            0, endTime, loop);

    }

}
