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

package com.super2k.openglen.easy3d;

import com.super2k.openglen.objects.GLBlitObject;

/**
 * Class holding movement data for a GLBlitObject.
 * Movement objects can have a bounding Rectangle.
 * @author Richard Sahlin
 *
 */
public abstract class MovementObject {

    public GLBlitObject blit;

    public float[] delta = new float[3];

    public float[] moveDelta = new float[3];

    public float[] bounds;

    /**
     * Returned by update movement if x movement is reversed
     */
    public final static int REVERSE_X_MOVEMENT = 1;
    /**
     * Returned by update movement if y movement is reversed
     */
    public final static int REVERSE_Y_MOVEMENT = 2;
    /**
     * Returned by update movement if z movement is reversed
     */
    public final static int REVERSE_Z_MOVEMENT = 4;

    /**
     * Update the movement values in the blit object.
     * @param ticks
     * @return 0 if no change of direction, bits set for each axis that has changed.
     * REVERSE_X_MOVEMENT
     * REVERSE_Y_MOVEMENT
     * REVERSE_Z_MOVEMENT
     */
    public abstract int updateMovement(int ticks);

    /**
     * Releases any resources this object has.
     */
    public void destroy() {
        blit = null;
        delta = null;
        moveDelta = null;
        bounds = null;
    }

}
