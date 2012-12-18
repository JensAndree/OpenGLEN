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

package com.super2k.openglen.android.test;

import android.graphics.Bitmap;

import com.super2k.openglen.test.ImageComparison;

public class AndroidImageComparison extends ImageComparison {

    /**
     * Creates an image comparison for the source image and comparison int array.
     * @param source Bitmap holding the source image.
     * @param comparison Array with comparison image.
     * @throws IllegalArgumentException If source or comparison is null or does not match in size.
     */
    public AndroidImageComparison(Bitmap source, int[] comparison) {
        if (source == null || comparison == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }
        if (source.getWidth() * source.getHeight() != comparison.length) {
            throw new IllegalArgumentException("Sizes of source and comparison does not match.");
        }
        int w = source.getWidth();
        int h = source.getHeight();
        mSource = new int[w * h];
        source.getPixels(mSource, 0, w, 0, 0, w, h);
        mComparison = comparison;
    }

}
