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

import junit.framework.TestCase;

import com.super2k.openglen.OpenGLENException;


/**
 * Functional tests for OpenGLENException
 * @author Richard Sahlin
 *
 */
public class FOpenGLENException extends TestCase    {

    public final static int TEST_ERROR_CODE = 1;

    public final static String TEST_ERROR_STRING = "cause";

    /**
     * Tests the constructors of class under test.
     */
    public void testConstructors()    {

        OpenGLENException glen = new OpenGLENException(TEST_ERROR_CODE);
        assertNotNull(glen);

        OpenGLENException glen2 = new OpenGLENException("cause");
        assertNotNull(glen2);

        OpenGLENException glen3 = new OpenGLENException("cause", TEST_ERROR_CODE);
        assertNotNull(glen3);

    }

    /**
     * Test that the error code can be set and read.
     */
    public void testErrorCode() {

        OpenGLENException glen = new OpenGLENException(TEST_ERROR_CODE);
        assertEquals(TEST_ERROR_CODE, glen.getErrorCode());

    }

    /**
     * Test that the reason string can be set and read.
     */
    public void testReasonString()  {

        OpenGLENException glen = new OpenGLENException(TEST_ERROR_STRING);
        assertEquals(TEST_ERROR_STRING, glen.getMessage());

    }

    /**
     * Test that both the reason and error code can be set and read.
     */
    public void testErrorAndReason()    {

        OpenGLENException glen = new OpenGLENException(TEST_ERROR_STRING, TEST_ERROR_CODE);
        assertEquals(TEST_ERROR_CODE, glen.getErrorCode());
        assertEquals(TEST_ERROR_STRING, glen.getMessage());


    }

}
