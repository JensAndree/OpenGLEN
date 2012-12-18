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

import com.super2k.openglen.geometry.Vector3;


/**
 * Animation that is based on 2D velocity, direction + speed.
 * Speed is number of pixels to move per second, use normalized direction.
 * @author Richard Sahlin
 *
 */
public class VelocityAnimation extends Animation3D {

    public final static int X_OFFSET = 0;
    public final static int Y_OFFSET = 1;
    public final static int Z_OFFSET = 2;
    public final static int SPEED_OFFSET = 3;

    /**
     * Direction x,y + speed.
     * Speed is number of pixels to move per second.
     */
    protected float[] mVelocity = new float[4];


    @Override
    public boolean animate(float timeDelta) {
        float speed = mVelocity[SPEED_OFFSET] * timeDelta;
        mTarget[X_OFFSET] += mVelocity[X_OFFSET] * speed;
        mTarget[Y_OFFSET] += mVelocity[Y_OFFSET] * speed;
        return false;
    }

    /**
     * Rotates the direction.
     * @param radians
     */
    public void rotate(float radians) {

    }

    /**
     * Sets up the animation to use the specified velocity.
     * @param target
     * @param velocity
     */
    public void setup(float[] target, float[] velocity) {
        if (target == null || velocity == null || velocity.length < mVelocity.length) {
            throw new IllegalArgumentException("Illegal value");
        }
        System.arraycopy(velocity,  0, mVelocity, 0, mVelocity.length);
        mTarget = target;
    }

    @Override
    public boolean rotateZ(float angle) {
        Vector3.rotateZAxis(mVelocity, angle);
        return true;
    }

}
