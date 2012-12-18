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

import com.super2k.openglen.ConstantValues;


/**
 * Linear transform animation
 * This class can animate several parts of the transform, eg x rotation + y rotation + z transform.
 * @author Richard Sahlin
 *
 */
public class LinearTransformAnimation extends Animation3D {

    private final static String ILLEGAL_USE_AXIS = "Illegal axis value:";

    int         mAxisInUse = 0;
    int         mNrOfAxis = 0;
    float[]     mTransform = new float[3]; //Anim translation stored here locally.

    /**
     * Constructor for dataimporter, create an empty animation.
     */
    public LinearTransformAnimation()
    {
        super();
    }

    /**
     * Swaps the key values so that the animations endpoint will be the startingpoint.
     */
    public void reverse() {
        //Go through mOutput and change triplets order.
        int length = mOutput.length;
        float[] newArray = new float[length];
        int index1 = 0;
        int index2 = length -3;
        while (index1 < length) {
            System.arraycopy(mOutput, index1, newArray, index2, 3);
            index1 += 3;
            index2 -= 3;
        }
        mOutput = newArray;
        mCurrentTime = mInput[mInput.length-1] - mCurrentTime;
    }
    /**
     * Create a linear transform animation with rotation on all axes, from start to stop.
     * There will be 2 timeKeys and targetKeys in this animation,
     * the timeKeys will start at startTime and end at stopTime
     * @param target
     * @param start
     * @param stop
     * @param startTime
     * @param stopTime
     */
    public LinearTransformAnimation(float[] target, float[] start, float[] end,
            float startTime, float endTime)
    {
        //Use all axes
        mNrOfAxis = 3;
        mOutputStride = mNrOfAxis;
        this.mTarget = target;
        mAxisInUse = ConstantValues.USE_X_AXIS |
                     ConstantValues.USE_Y_AXIS |
                     ConstantValues.USE_Z_AXIS;
        mInput = new float[2];
        mOutput = new float[2 * 3];
        mInput[0] = startTime;
        mInput[1] = endTime;
        mOutput[0] = start[0];
        mOutput[1] = start[1];
        mOutput[2] = start[2];

        mOutput[3] = end[0];
        mOutput[4] = end[1];
        mOutput[5] = end[2];

        mOutputStartIndex = 0;


    }

    /**
     * Create a linear transform animation.
     * @param target The target array for values.
     * @param axisInUse Which axis to use,
     * ConstantValues.USE_X_AXIS |
     * ConstantValues.USE_Y_AXIS |
     * ConstantValues.USE_Z_AXIS
     * @param timeKeys Input values.
     * @param targetKeys Output values, stride is set to same value as nr of axes in use.
     * @throws IllegalArgumentException If axisInUse is not one of
     * ConstantValues.USE_X_AXIS
     * ConstantValues.USE_Y_AXIS
     * ConstantValues.USE_Z_AXIS
     * @TODO Make sure there are enough values in all arays - if not throw IllegalArgumentException
     */
    public LinearTransformAnimation(float[] target,int axisInUse,
                                    float[] timeKeys, float[] targetKeys)
    {
        //Make sure axisInUse is valid.
        if ((axisInUse & (ConstantValues.USE_X_AXIS |
                ConstantValues.USE_Y_AXIS |
                ConstantValues.USE_Z_AXIS)) == 0) {
            throw new IllegalArgumentException(ILLEGAL_USE_AXIS + axisInUse);
        }
        if ((axisInUse & ConstantValues.USE_X_AXIS) == ConstantValues.USE_X_AXIS)
            mNrOfAxis++;
        if ((axisInUse & ConstantValues.USE_Y_AXIS) == ConstantValues.USE_Y_AXIS)
            mNrOfAxis++;
        if ((axisInUse & ConstantValues.USE_Z_AXIS) == ConstantValues.USE_Z_AXIS)
            mNrOfAxis++;

        mOutputStride = mNrOfAxis;
        this.mAxisInUse = axisInUse;
        this.mTarget = target;
        mInput = timeKeys;
        mOutput = targetKeys;
        mOutputStartIndex = 0;

    }


    @Override
    public void resetAnimation()
    {
        super.resetAnimation();
    }

    @Override
    public boolean animate(float timeDelta) {

        boolean looped = calcCurrentIndex(timeDelta);

        int sampleIndex = mOutputIndex * mNrOfAxis; //TODO: Check should be done at create so
                                                    //this cant overflow

        float delta = mCurrentTime - mInput[mInputIndex ];
        float factor = delta / (mInput[mInputIndex + 1] - mInput[mInputIndex]);

        mTransform[0] = mTarget[0];
        mTransform[1] = mTarget[1];
        mTransform[2] = mTarget[2];

        if ((mAxisInUse & ConstantValues.USE_X_AXIS) == ConstantValues.USE_X_AXIS )
        {
            if (!mRelative)
                mTransform[0] = mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));
            else
                mTransform[0] += mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));
            sampleIndex++;
        }
        if ((mAxisInUse & ConstantValues.USE_Y_AXIS) == ConstantValues.USE_Y_AXIS )
        {
            if (!mRelative)
                mTransform[1] = mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));
            else
                mTransform[1] += mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));

            sampleIndex++;
        }
        if ((mAxisInUse & ConstantValues.USE_Z_AXIS) == ConstantValues.USE_Z_AXIS )
        {
            if (!mRelative)
                mTransform[2] = mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));
            else
                mTransform[2] += mOutput[sampleIndex] +
                (factor * (mOutput[sampleIndex + mOutputStride] - mOutput[sampleIndex]));

        }

        //Set translation in target.
        mTarget[0] = mTransform[0];
        mTarget[1] = mTransform[1];
        mTarget[2] = mTransform[2];
        return looped;
    }


    private float[] cubic(float factor, float value)
    {
        float[] result = new float[4];
        for (int outer = 3; outer > 0; outer--)
            for (int inner = 0; inner < outer; inner++)
            {
                //         result[inner] = (1-factor)*result[inner] + factor*result[inner+1];
            }
        return result;
    }

    @Override
    public boolean rotateZ(float angle) {
        // TODO Auto-generated method stub
        return false;
    }


}
