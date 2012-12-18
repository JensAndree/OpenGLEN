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

package com.super2k.openglen.material.test;

import junit.framework.TestCase;

import com.super2k.openglen.geometry.Material;

/**
 * Functional tests for the Material class.
 * @author Richard Sahlin
 *
 */
public class FMaterial extends TestCase {

    private final static float FLOAT_VAL_1 = 124.1f;
    private final static float FLOAT_VAL_2 = 456.56f;
    private final static float FLOAT_VAL_3 = 3.14f;
    private final static float FLOAT_VAL_4 = 4579.334f;

    /**
     * Test the constructors.
     */
    public void testCreate()    {

        Material mat = new Material();
        assertNotNull(mat);
    }

    /**
     * Test methods to set diffuse color.
     */
    public void testSetDiffuse()    {

        Material mat = new Material();
        mat.setDiffuse(FLOAT_VAL_1, FLOAT_VAL_2, FLOAT_VAL_3, FLOAT_VAL_4);
        assertEquals(FLOAT_VAL_1,mat.diffuse[0]);
        assertEquals(FLOAT_VAL_2,mat.diffuse[1]);
        assertEquals(FLOAT_VAL_3,mat.diffuse[2]);
        assertEquals(FLOAT_VAL_4,mat.diffuse[3]);

    }


}
