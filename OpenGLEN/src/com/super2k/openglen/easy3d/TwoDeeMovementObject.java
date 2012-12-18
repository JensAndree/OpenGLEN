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
 * Class for handling simple 2D movement, move speed and bounds can be setup.
 * Movement will automatically switch direction when reaching bounds.
 * @author Richard Sahlin
 *
 */
public class TwoDeeMovementObject extends MovementObject {

    public TwoDeeMovementObject(GLBlitObject blit,
            float xDelta, float yDelta, float xMoveDelta, float yMoveDelta,
            int left, int top, int right, int bottom)   {

        this.blit = blit;
        moveDelta[0] = xMoveDelta;
        moveDelta[1] = yMoveDelta;
        delta[0] = xDelta;
        delta[1] = yDelta;
        this.bounds = new float[4];
        this.bounds[0] = left;
        this.bounds[1] = right;
        this.bounds[2] = top;
        this.bounds[3] = bottom;
    }

    @Override
    public int updateMovement(int ticks)      {

        int result = 0;
        float[] position = blit.position;
        if (ticks > 100) {
            ticks = 100;
        }
        position[0] += delta[0] * ticks;
        position[1] += delta[1] * ticks;

        if (position[0] > bounds[1])      {
            //Reverse direction.
            delta[0] = -delta[0];
            position[0] = bounds[1] - (position[0] - bounds[1]);
            result |= REVERSE_X_MOVEMENT;
        } else {
            if (position[0] < bounds[0])       {
                //Reverse direction.
                delta[0] = -delta[0];
                position[0] = bounds[0] + (bounds[0] - position[0]);
                result |= REVERSE_X_MOVEMENT;
            }
        }
        if (position[1] > bounds[3])      {
            //Reverse direction.
            delta[1] = -delta[1];
            float dist = position[1] - bounds[3];
            position[1] = bounds[3];
            delta[1] += dist / 1000;
            result |= REVERSE_Y_MOVEMENT;
        } else {
            if (position[1] < bounds[2])       {
                //Reverse direction.
                delta[1] = -delta[1];
                position[1] = bounds[2] + (bounds[2] - position[1]);
                result |= REVERSE_Y_MOVEMENT;
            }
            else
                delta[1] += moveDelta[1] * ticks;
        }
        delta[0] += moveDelta[0] * ticks;
        return result;
    }

}
