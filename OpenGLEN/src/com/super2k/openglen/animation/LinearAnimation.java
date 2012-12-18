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
 * Class that handles linear animation.
 * The target is a float array of desired size.
 * @author Richard Sahlin
 *
 */
public class LinearAnimation extends Animation3D {

    protected final static String INVALID_PARAMETER_STR = "Invalid parameter.";

    protected float[] mTempArray;
    float mTempFactor;
    int mTempIndex;

    /**
     * Default empty animation.
     */
    public LinearAnimation() {

    }

    /**
     * Create a transform animation.
     * There will be 2 timeKeys and targetKeys in this animation,
     * the timeKeys will start at startTime and end at stopTime
     * @param target
     * @param start
     * @param stop
     * @param startTime
     * @param stopTime
     * @param loop Should the animation loop or not, LOOP_INFINITE to loop forever,
     * LOOP_DISABLED to disable looping, or number of times animation shall loop.
     */
    public LinearAnimation(float[] target, float[] start, float[] end,
            float startTime, float endTime, int loop)    {
        setup(target, start, end, startTime, endTime, loop);
    }


    /**
     * Initializes a transform animation.
     * There will be 2 timeKeys and targetKeys in this animation,
     * the timeKeys will start at startTime and end at stopTime
     * @param target
     * @param start
     * @param stop
     * @param startTime
     * @param stopTime
     * @param loop Should the animation loop or notLOOP_INFINITE to loop forever,
     * LOOP_DISABLED to disable looping, or number of times animation shall loop.
     */
    public void setup(float[] target, float[] start, float[] end,
            float startTime, float endTime, int loop) {
        if (target == null || start == null || end == null || start.length != end.length ||
                target.length < start.length) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        reset();

        mLoopCount = loop;
        mOutputStride = start.length;
        mTempArray = new float[mOutputStride];
        mTarget = target;
        mInput = new float[2];
        mOutput = new float[2 * mOutputStride];
        mInput[0] = startTime;
        mInput[1] = endTime;

        //Set output.
        System.arraycopy(start, 0, mOutput, 0, mOutputStride);
        System.arraycopy(end, 0, mOutput, mOutputStride, mOutputStride);


    }


    @Override
    public boolean animate(float timeDelta) {

        if (timeDelta == 0) {
            return false;
        }

         mTempLoop = calcCurrentIndex(timeDelta);

        mTempIndex = mOutputIndex * mOutputStride; //TODO: Check should be done at create so this
                                                   //cant overflow

        mTempFactor = (mCurrentTime - mInput[mInputIndex ]) /
                      (mInput[mInputIndex + 1] - mInput[mInputIndex]);

        System.arraycopy(mTarget, 0, mTempArray, 0, mOutputStride);

        if (!mRelative) {
            for (int i = 0; i < mOutputStride; i++) {
                mTempArray[i] = mOutput[mTempIndex] +
                (mTempFactor * (mOutput[mTempIndex + mOutputStride] - mOutput[mTempIndex]));
                mTempIndex++;
            }
        }
        else {
            for (int i = 0; i < mOutputStride; i++) {
                mTempArray[i] += mOutput[mTempIndex] +
                (mTempFactor * (mOutput[mTempIndex + mOutputStride] - mOutput[mTempIndex]));
                mTempIndex++;
            }
        }

        //Set translation in target.
        System.arraycopy(mTempArray, 0, mTarget, 0, mOutputStride);
        return mTempLoop;
    }

    @Override
    public boolean rotateZ(float angle) {
        // TODO Auto-generated method stub
        return false;
    }


}
