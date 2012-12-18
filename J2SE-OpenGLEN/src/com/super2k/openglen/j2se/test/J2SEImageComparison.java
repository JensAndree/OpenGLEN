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

import com.super2k.openglen.test.ImageComparison;

/**
 * Compare the difference between 2 images using java.awt
 * Threshold values for diff limit per component can be set.
 * @author Richard Sahlin
 *
 */
public class J2SEImageComparison extends ImageComparison {

    /**
     * Creates an image comparison for the source image and comparison int array.
     * @param source
     * @param comparison
     * @throws IllegalArgumentException If source or comparison is null or does not match in size.
     */
    public J2SEImageComparison(BufferedImage source, int[] comparison) {
        if (source == null || comparison == null) {
            throw new IllegalArgumentException("Invalid parameter, null");
        }
        if (source.getWidth() * source.getHeight() != comparison.length) {
            throw new IllegalArgumentException("Sizes of source and comparison does not match.");
        }

        mSource = new int[source.getWidth() * source.getHeight()];
        source.getRGB(0, 0, source.getWidth(), source.getHeight(),
                    mSource, 0, source.getWidth());
        mComparison = comparison;
    }

}
