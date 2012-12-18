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
 * Creates a Sinus based animation for the specified target.
 * @author Richard Sahlin
 *
 */
public class SinusAnimation extends Animation3D {

    protected float[] mAmplitude;   //Amplitude for each value,
                                    //this is multiplied by the sine value.
    protected float[] mStartTime;   //Start time for each value
    protected float[] mEndTime;     //End time for each value
    protected float[] mTimeFactor;  //Time factor for each value, this is a speed factor -
                                    //smaller values than 1 will make it go slower,
                                    //larger values gives faster animation.
    protected float[] mTime;        //Time values for each destination after timefactor adjust
                                    //goes from mStartTime to mStartTime + (1/timefactor)
    protected float[] mSineAdd;     //Values added after sinus function,
                                    //before amplitude multiplication.
                                    //Eg a value of 0.5, will offset the curve + 0.5 * amplitude.
    protected final static float TWOPI = (float) Math.PI *2;
    @Override
    public boolean animate(float timeDelta) {
        mCurrentTime += timeDelta;

        int len = mStartTime.length;
        for (int i = 0; i < len; i++) {
            float endTime = TWOPI;
            if (mEndTime != null) {
                endTime = mEndTime[i];
            }
            //Go through each destination
            mTime[i] += timeDelta * mTimeFactor[i];
            if (mTime[i] >= endTime) {
                mTime[i] -= endTime;
            }
            if (mTime[i] >= mStartTime[i]) {
                float offset = 0;
                if (mSineAdd != null) {
                    offset = mSineAdd[i];
                }
                float value = mTime[i] - mStartTime[i];
                float sin = (float) Math.sin(value);
                mTarget[i] = (sin + offset) * mAmplitude[i];
            }
        }
        return false;
    }

    /**
     * Creates the time and starttime arrays.
     * @param inValues Number of in-values.
     */
    protected void init(int inValues) {
        mTime = new float[inValues];
        mStartTime = new float[inValues];
    }

    /**
     * Default constructor, used by subclasses.
     */
    protected SinusAnimation() {
    }

    /**
     * Creates a sine animation, animation will loop at time 2 * PI radians.
     * The target values may have different sine offsets, speed and amplitude.
     * @param target The target for the animation, must have at least the same length as in values.
     * @param startTime Start time, idle time before sinewave. Affected by timeFactor.
     * @param amplitude The amplitude of output values
     * @param timeFactor Time factor, value of 1 is normal, lower values slower.
     * @param sineOffset Offset factor for sine.
     */
    public SinusAnimation(float[] target,
            float[] startTime,
            float[] amplitude,
            float[] timeFactor,
            float[] sineOffset) {
        setup(target, startTime, null, amplitude, timeFactor, sineOffset);
    }

    /**
     * Creates a sine animation, animation will loop at time 2 * PI radians.
     * The target values may have different sine offsets, speed and amplitude.
     * @param target The target for the animation, must have at least the same length as in values.
     * @param startTime Start time, idle time before sinewave. Affected by timeFactor.
     * @param amplitude The amplitude of output values
     * @param timeFactor Time factor, value of 1 is normal, lower values slower.
     * @param sineOffset Offset factor for sine.
     */
    public SinusAnimation(float[] target,
            float[] startTime,
            float[] endTime,
            float[] amplitude,
            float[] timeFactor,
            float[] sineOffset) {
        setup(target, startTime, endTime, amplitude, timeFactor, sineOffset);
    }

    /**
     * Initializes the animation.
     * @param target
     * @param startTime
     * @param endTime Endtime of animation, may be null. If so 2 * PI is used for one full sinewave.
     * @param amplitude
     * @param timeFactor
     * @param sineOffset
     */
    protected void setup(float[] target,
                        float[] startTime,
                        float[] endTime,
                        float[] amplitude,
                        float[] timeFactor,
                        float[] sineOffset) {
        if (startTime == null || amplitude == null || timeFactor == null || sineOffset == null ||
                startTime.length != amplitude.length || startTime.length != timeFactor.length ||
                startTime.length != sineOffset.length) {
            throw new IllegalArgumentException("Invalid parameter, null or sizes do not match.");
        }
        mTarget = target;
        int l = startTime.length;
        init(l);
        mAmplitude = amplitude;
        mTimeFactor = timeFactor;
        mSineAdd = sineOffset;
        mEndTime = endTime;
        System.arraycopy(startTime, 0, mStartTime, 0, l);


    }

    /**
     * Sets the timefactor for the specified index, this will affect the time factor
     * for target index-
     * @param index Target index to change time factor for.
     * @param factor Time factor
     * @throws IllegalArgumentException If index is outside time factor array.
     * Size of time factor array is decided when animation is created.
     */
    public void setTimeFactor(int index, float factor) {
        if (index > 0 || index >= mTimeFactor.length) {
            throw new IllegalArgumentException("Index out of range: " + index);
        }
        mTimeFactor[index] = factor;
    }

    /**
     * Sets the amplitude for the specified index, this will affect the amplitude
     * for target index.
     * @param index Target index to change amplitude for.
     * @param amplitude Amplitude value
     * @throws IllegalArgumentException If index is outside amplitude array.
     * Size of amplitude array is decided when animation is created.
     */
    public void setAmplitude(int index, float amplitude) {
        if (index < 0 || index >= mAmplitude.length) {
            throw new IllegalArgumentException("Index out of range: " + index);
        }
        mAmplitude[index] = amplitude;
    }

    @Override
    public boolean rotateZ(float angle) {
        // TODO Auto-generated method stub
        return false;
    }

}
