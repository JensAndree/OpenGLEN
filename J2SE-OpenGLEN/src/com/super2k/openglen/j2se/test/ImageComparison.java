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

package com.super2k.openglen.j2se.test;

import java.awt.image.BufferedImage;

/**
 * Compare the difference between 2 images.
 * Threshold values for diff limit per component can be set.
 * @author Richard Sahlin
 *
 */
public class ImageComparison {

    BufferedImage mSource;
    int[] mComparison;

    /**
     * Creates an image comparison for the source image and comparison int array.
     * @param source
     * @param comparison
     * @throws IllegalArgumentException If source or comparison is null or does not match in size.
     */
    public ImageComparison(BufferedImage source, int[] comparison) {
        if (source == null || comparison == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }
        if (source.getWidth() * source.getHeight() != comparison.length) {
            throw new IllegalArgumentException("Sizes of source and comparison does not match.");
        }
        mSource = source;
        mComparison = comparison;
    }

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

        int[] source = new int[mSource.getWidth() * mSource.getHeight()];
        mSource.getRGB(0, 0, mSource.getWidth(), mSource.getHeight(),
                    source, 0, mSource.getWidth());

        //Iterate trough int array - if all values same then just pass.
        int count = source.length;
        for (int i = 0; i < count; i++) {
            if (source[i] != mComparison[i]) {
                //Check what component and how much the diff was.
                int s = source[i];
                int c = mComparison[i];
                int r = ((s & 0x0ff) - (c & 0x0ff));
                int g = ((s & 0x0ff00) - (c & 0x0ff00)) >>> 8;
                int b = ((s & 0x0ff0000) - (c & 0x0ff0000)) >>> 16;
                int a = ((s & 0x0ff000000) - (c & 0x0ff000000)) >>> 24;
                if (r > redDiff || g > greenDiff || b > blueDiff || a > alphaDiff) {
                    return false;
                }
            }
        }

        return true;
    }

}
