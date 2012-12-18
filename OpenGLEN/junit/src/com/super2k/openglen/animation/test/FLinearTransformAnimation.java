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

package com.super2k.openglen.animation.test;

import junit.framework.TestCase;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.animation.LinearTransformAnimation;


/**
 *
 * @author Richard Sahlin
 *
 */
public class FLinearTransformAnimation extends TestCase {

    private final static String FAIL_NO_EXCEPTION = "Invalid value, should throw exception.";

    private final static float[] TIMEKEY_ARRAY = {0f, 1f, 2f, 3f};
    private final static float[] TARGETKEY_ARRAY = {0f, 1f, 10f, 100f};
    private final static float[] TARGET_ARRAY_ONEAXIS = new float[3];

    private final static int[] TEST_AXIS_TABLE = {
        ConstantValues.USE_X_AXIS, ConstantValues.X_AXIS_INDEX,
        ConstantValues.USE_Y_AXIS, ConstantValues.Y_AXIS_INDEX,
        ConstantValues.USE_Z_AXIS, ConstantValues.Z_AXIS_INDEX};

    /**
     * Test the constructors
     */
    public void testConstructor()
    {
        LinearTransformAnimation trans = new LinearTransformAnimation(TARGET_ARRAY_ONEAXIS,
                ConstantValues.USE_X_AXIS,
                TIMEKEY_ARRAY,
                TARGETKEY_ARRAY);

        assertEquals(TIMEKEY_ARRAY[0], trans.getAnimationStart());
        assertEquals(TIMEKEY_ARRAY[TIMEKEY_ARRAY.length-1], trans.getAnimationEnd());

        //Exception if illegal use axis.
        try {
            LinearTransformAnimation trans1 = new LinearTransformAnimation(TARGET_ARRAY_ONEAXIS,
                    0,
                    TIMEKEY_ARRAY,
                    TARGETKEY_ARRAY);
            //Fail - should throw exception
            fail(FAIL_NO_EXCEPTION);

        }
        catch (IllegalArgumentException iae) {
            //Pass
            assertTrue(true);
        }


    }

    /**
     * Test translate animation on one axis.
     */
    public void testTranslateOneAxis() {

        int tableIndex = 0;
        int axis;
        int index;

        while (tableIndex  < TEST_AXIS_TABLE.length) {
            axis = TEST_AXIS_TABLE[tableIndex++];
            index = TEST_AXIS_TABLE[tableIndex++];
            LinearTransformAnimation trans = new LinearTransformAnimation(TARGET_ARRAY_ONEAXIS,
                    axis,
                    TIMEKEY_ARRAY,
                    TARGETKEY_ARRAY);

            //Animate to half of index 1
            trans.animate(TIMEKEY_ARRAY[1]/2);
            assertEquals(TARGETKEY_ARRAY[1]/2, TARGET_ARRAY_ONEAXIS[index]);

            //Animate to index 1
            trans.animate(TIMEKEY_ARRAY[1]/2);
            assertEquals(TARGETKEY_ARRAY[1], TARGET_ARRAY_ONEAXIS[index]);

            //Animate to last index
            trans.animate(TIMEKEY_ARRAY[TIMEKEY_ARRAY.length-1] - TIMEKEY_ARRAY[1]);
            assertEquals(TARGETKEY_ARRAY[TIMEKEY_ARRAY.length-1], TARGET_ARRAY_ONEAXIS[index]);
        }

    }


}
