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

package com.super2k.openglen.utils;

import junit.framework.TestCase;

import com.super2k.openglen.ConstantValues;
/**
 * Test the ConfigurationParameters util class.
 * @author Richard Sahlin
 *
 */
public class FConfigurationParameters extends TestCase {

    private final static String BOOLEAN_PROPERTY_NAME = "booleanproperty";
    private final static String INT_PROPERTY_NAME = "intproperty";
    private final static String INT_PROPERTY_VALUE = "1267";
    private final static String INT_PROPERTY_VALUE_INVALID = "abc123";
    private final static String[] WINDOWFORMAT_STR_VALUE = {
        "RGBA_4444",
        "RGB_565",
        "RGB_888",
        "RGBA_5551",
        "RGBA_8888",
        "RGBX_8888"};
    private final static int[]  WINDOWFORMAT_INT_VALUE = {
        ConstantValues.PIXELFORMAT_RGBA_4444,
        ConstantValues.PIXELFORMAT_RGB_565,
        ConstantValues.PIXELFORMAT_RGB_888,
        ConstantValues.PIXELFORMAT_RGBA_5551,
        ConstantValues.PIXELFORMAT_RGBA_8888,
        ConstantValues.PIXELFORMAT_RGBX_8888};
    private final static String WINDOWFORMAT_INVALID_STR_VALUE = "abcd";

    private final static String FAIL_SHOULD_THROW_ILLEGAL = "Should throw IllegalArgumentException";

    /**
     * Test that the function to get boolean property works.
     */
    public void testGetBooleanProperty()    {

        //Make sure -1 when property not set.
        assertEquals(-1, ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME));

        System.setProperty(BOOLEAN_PROPERTY_NAME, "true");
        assertEquals(1, ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME));

        //Make sure works with some uppercase
        System.setProperty(BOOLEAN_PROPERTY_NAME, "TruE");
        assertEquals(1, ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME));

        System.setProperty(BOOLEAN_PROPERTY_NAME, "false");
        assertEquals(0, ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME));

        //Make sure works with some uppercase
        System.setProperty(BOOLEAN_PROPERTY_NAME, "fAlSe");
        assertEquals(0, ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME));


        try {
            System.setProperty(BOOLEAN_PROPERTY_NAME, "fel");
            ConfigurationParameters.getBooleanProperty(BOOLEAN_PROPERTY_NAME);
            //Fail - should throw exception
            fail(FAIL_SHOULD_THROW_ILLEGAL);
        }
        catch (IllegalArgumentException iae) {
            //Pass
            assertTrue(true);
        }

    }

    /**
     * Test that the function to get int property works.
     */
    public void testGetIntProperty() {

        assertEquals(-1, ConfigurationParameters.getIntProperty(INT_PROPERTY_NAME));

        System.setProperty(INT_PROPERTY_NAME, INT_PROPERTY_VALUE);
        assertEquals(Integer.parseInt(INT_PROPERTY_VALUE),ConfigurationParameters.getIntProperty(INT_PROPERTY_NAME));

        try {
            System.setProperty(INT_PROPERTY_NAME, INT_PROPERTY_VALUE_INVALID);
            ConfigurationParameters.getIntProperty(INT_PROPERTY_NAME);
            //Fail - should throw exception
            fail(FAIL_SHOULD_THROW_ILLEGAL);
        }
        catch (IllegalArgumentException iae) {
            //Pass
            assertTrue(true);
        }

    }

    /**
     * Test that the function to get windowformat property works.
     */
    public void testWindowFormatProperty() {

        assertEquals(-1, ConfigurationParameters.getWindowFormatProperty());

        for (int i = 0; i < WINDOWFORMAT_STR_VALUE.length; i++) {
            System.setProperty(ConfigurationParameters.PROPERTY_WINDOW_FORMAT,WINDOWFORMAT_STR_VALUE[i]);
            assertEquals(WINDOWFORMAT_INT_VALUE[i], ConfigurationParameters.getWindowFormatProperty());

        }

        try {
            System.setProperty(ConfigurationParameters.PROPERTY_WINDOW_FORMAT, WINDOWFORMAT_INVALID_STR_VALUE);
            ConfigurationParameters.getWindowFormatProperty();
            //Fail - should throw exception
            fail(FAIL_SHOULD_THROW_ILLEGAL);
        }
        catch (IllegalArgumentException iae) {
            //Pass
            assertTrue(true);

        }
    }

}
