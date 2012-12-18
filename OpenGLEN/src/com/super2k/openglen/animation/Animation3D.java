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

import com.super2k.openglen.objects.PooledObject;


/**
 * Base class for animations.
 * @author Richard Sahlin
 *
 */
public abstract class Animation3D implements PooledObject {

    /**
     * Value to specify that animation should loop forever.
     */
    public final static int LOOP_INFINITE = -1;
    /**
     * Value to specify that animation should not loop.
     */
    public final static int LOOP_DISABLED = 0;

    /**
     * Value to specify that animation should loop once.
     * For other loop counts use the number of times the animation should loop,
     * eg 4 to loop 4 times.
     */
    public final static int LOOP_ONCE = 1;

    /**
     * User defined animation type. Is preserved when exporting animations.
     */
    protected int mAnimationType;

    /**
     * The key for pooled animation, or NOT_POOLED_OBJECT if not pooled.
     */
    protected int mKey = NOT_POOLED_OBJECT;

    /**
     * Keypoints in the time domain
     */
    protected float[] mInput;

    /**
     * Keypoints in the target domain.
     */
    protected float[] mOutput;

    /**
     * The target array, this is normally a reference.
     */
    protected float[] mTarget;

    protected int mOutputIndex = 0;
    protected int mOutputStride = 0;
    protected int mOutputStartIndex = 0;

    /**
     * Max number of times an animation shall loop.
     * If <= 0 then this value limits number of times the animation can loop.
     * When this loop count is reached the animation shall not animate further
     * and report true when isFinished is called.
     */
    protected int mLoopCount = LOOP_INFINITE;
    /**
     * Controlls if animation values are absolute or added to existing values.
     * Set to true to make values be added to existing values.
     */
    protected boolean mRelative = false;

    /**
     * Array that may be used to copy the contents of the target
     */
    protected float[] mResetArray;

    /**
     * --------------------------------
     * Runtime variables.
     * --------------------------------
     */

    protected float mCurrentTime = 0;
    protected int mInputIndex = 0;
    protected boolean mFinished = false;
    protected int mCurrentLoop = 0;

    protected boolean mTempLoop;
    protected float mTempMaxtime;

    /**
     * Return the time in seconds when this animation starts.
     * @return
     */
    public float getAnimationStart()
    {
        return mInput[0];
    }

    /**
     * Return the time in seconds when this animation ends.
     * @return
     */
    public float getAnimationEnd()
    {
        return mInput[mInput.length-1];
    }

    /**
     * Return true of the animation is finished.
     * Note that the animation will not be considered as finished while loop is true.
     * @return True if the animation has finished (ie loop is not true and animation has reached end)
     */
    public boolean isFinished()
    {
        return mFinished;
    }

    /**
     * Checks wether the animation has looped or not, returns number of times the animation
     * has looped.
     * This value is reset when calling resetAnimation()
     * @return Number of times the animation has looped, 0 or more.
     */
    public int getCurrentLoop() {
        return mCurrentLoop;
    }

    /**
     * This resets the animation, resets the internal current time value.
     * The animate() method can be called after this to produce the animation from start.
     */
    public void resetAnimation()
    {
        mCurrentTime = 0;
        mInputIndex = 0;
        mFinished = false;
        mCurrentLoop = 0;
    }

    /**
     * Set the time of the animation,
     * this will perform a resetAnimation followed by a call to animate() with the specified time.
     * @param time
     */
    public void setTime(float time)
    {
        resetAnimation();
        animate(time);

    }

    /**
     * Set the way values are considered when writing back,
     * if relative set to true then animation values are added to existing transform values.
     * If false the values from the animation is set in the transform.
     * @param relative
     */
    public void setRelative(boolean relative)
    {
        this.mRelative = relative;
    }



    /**
     * Return the type of animation,
     * this is something that is understood by the client. The animation type is simply a way for clients
     * to mark tag animations with a specific type that can later be retreived.
     * The animation type is preserved when exporting the animation.
     * @return The type of this animation or -1 if not set.
     */
    public int getAnimationType()
    {
        return mAnimationType;
    }

    /**
     * Set the (user defined) animation type for this animation.
     * This is client specific data that can be used to group animations together.
     * The animation type is saved when exporting the animation.
     * @param type
     */
    public void setAnimationType(int type)
    {
        mAnimationType = type;
    }

    /**
     * Sets the max number of times the animation may loop, if looping is enabled.
     * @param count LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     */
    public void setLoopCount(int count) {
        mLoopCount = count;
    }

    /**
     * Fetches the max number of times this animation shall loop.
     * @return LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     */
    public int getLoopCount() {
        return mLoopCount;
    }

    /**
     * Calculate the currentTime and currentIndex values.
     * @param timeDelta
     * @return True if animation reached end AND looped,
     * otherwise false. Will not return true if looping is disabled.
     */
    boolean calcCurrentIndex(float timeDelta)
    {
        mTempLoop = false;
        mCurrentTime += timeDelta;
        mTempMaxtime = mInput[mInput.length-1];

        if (mCurrentTime >= mTempMaxtime)
        {
            //Reached end of animation
            if (mLoopCount == LOOP_DISABLED) {
                mFinished = true;
                mInputIndex = mInput.length-2;
                mCurrentTime = mTempMaxtime;
                return false;
            } else {
                //Looping is enabled increase loopcount
                mCurrentLoop++;
                if (mLoopCount != LOOP_INFINITE) {
                    if (mCurrentLoop >= mLoopCount) {
                        //End of animation.
                        mFinished = true;
                        mInputIndex = mInput.length-2;
                        mCurrentTime = mTempMaxtime;
                    }
                }
            }

            while (mCurrentTime > mTempMaxtime )
                mCurrentTime -= mTempMaxtime;

            mInputIndex = 0;

            //Find current index.
            while (mCurrentTime > mInput[mInputIndex + 1])
                mInputIndex++;
            mTempLoop = true;
        }

        while (mCurrentTime > mInput[mInputIndex + 1])
            mInputIndex++;

        mOutputIndex = mInputIndex * mOutputStride + mOutputStartIndex;

        return mTempLoop;
    }

    /**
     * Restore target values. This is normally done at end of animation if reset flag is set.
     */
    public void restoreTarget() {
        System.arraycopy(mResetArray, 0, mTarget, 0, mResetArray.length);
    }

    /**
     * Saves the target values, may not work on all animations.
     */
    public void saveTarget() {
        if (mResetArray == null || mResetArray.length != mTarget.length) {
            mResetArray = new float[mTarget.length];
        }
        System.arraycopy(mTarget,  0, mResetArray, 0, mTarget.length);
    }

    public void reset() {
        mCurrentTime = 0;
        mInputIndex = 0;
        mFinished = false;
        mOutputIndex = 0;
        mOutputStride = 0;
        mOutputStartIndex = 0;
    }

    /**
     * Animate the target axis, current time will be updated with the timeDelta.
     * @param timeDelta The time delta from last call to this method (or to resetAnimation()).
     * @return True if this animation looped, ie reached end and restarted, false otherwise.
     */
    public abstract boolean animate(float timeDelta);

    /**
     * Returns true if this is a relative animation,
     * if relative then values are added/subtracted from previous values.
     * @return True if animation is relative, false for absolute values.
     */
    public boolean isRelative() {
        return mRelative;
    }

    /**
     * Rotates the animation on the Z axis, note that not all animations support rotation.
     * Returns false if rotation is not supported
     * @param angle Rotation on Z axis
     * @return True if animation supports rotation on Z axis, otherwise false.
     */
    public abstract boolean rotateZ(float angle);

    @Override
    public void setKey(int key) {
        mKey = key;
    }

    @Override
    public int getKey() {
        return mKey;
    }

    @Override
    public void releaseObject() {
        mTarget = null;
        mResetArray = null;
    }

    @Override
    public void createObject(Object obj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroyObject(Object obj) {
        // TODO Auto-generated method stub

    }

}
