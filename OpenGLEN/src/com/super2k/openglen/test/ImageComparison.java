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

import com.super2k.openglen.utils.Log;

public abstract class ImageComparison {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Source image
     */
    protected int[] mSource;

    /**
     * Compare image
     */
    protected int[] mComparison;

    /**
     * Checks if source and comparison is the same with a difference limit for each channel (RGBA)
     * @param redDiff Max diff in red channel, if one pixel is above this value then return false.
     * @param greenDiff Max diff in green channel,
     * if one pixel is above this value then return false.
     * @param blueDiff Max diff in blue channel,
     * if one pixel is above this value then return false.
     * @param alphaDiff Max diff in alpha channel,
     * if one pixel is above this value then return false.
     * @return True if the source and comparison images are within diff limits.
     */
    public boolean isSame(int redDiff, int greenDiff, int blueDiff, int alphaDiff) {
        return isSame(mSource, mComparison, redDiff, greenDiff, blueDiff, alphaDiff);
    }

    /**
     * Internal method to check diff in image. Takes to int arrays as input parameters and
     * checks the difference between them. Returns true if diff is within limits.
     * @param source The source image array
     * @param target The comparison image array
     * @param redDiff Max diff in red channel, if one pixel is above this value then return false.
     * @param greenDiff Max diff in green channel,
     * if one pixel is above this value then return false.
     * @param blueDiff Max diff in blue channel,
     * if one pixel is above this value then return false.
     * @param alphaDiff Max diff in alpha channel,
     * if one pixel is above this value then return false.
     * @return True if the source and comparison images are within diff limits.
     */
    protected boolean isSame(int[] source, int[] target,
                          int redDiff, int greenDiff, int blueDiff, int alphaDiff) {

        //Iterate trough int array - if all values same then just pass.
        int count = source.length;
        for (int i = 0; i < count; i++) {
            if (source[i] != target[i]) {
                //Check what component and how much the diff was.
                int s = source[i];
                int c = target[i];
                int b = Math.abs(((s & 0x0ff) - (c & 0x0ff)));
                int g = Math.abs(((s & 0x0ff00)>>>8 - (c & 0x0ff00)>>>8));
                int r = Math.abs(((s & 0x0ff0000)>>>16 - (c & 0x0ff0000)>>>16));
                int a = Math.abs(((s & 0x0ff000000)>>>24 - (c & 0x0ff000000)>>>24));
                if (r > redDiff || g > greenDiff || b > blueDiff || a > alphaDiff) {
                    Log.d(TAG, "Diff found att index " + i + ", diff: " + r + ", " + g + ", " +
                          b + ", " + a + ", source: " + s + ", comparison: " + c);
                    return false;
                }
            }
        }

        return true;
    }


}
