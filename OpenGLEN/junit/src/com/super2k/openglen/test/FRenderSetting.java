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

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.RenderSetting;

/**
 * Functional tests for RenderSetting.
 * Test constructors and that values can be set and get.
 * @author Richard Sahlin
 *
 */
public class FRenderSetting extends TestCase {

    protected final static int TEST_DEPTH_FUNC = ConstantValues.NEVER;
    protected final static int TEST_CLEAR_FLAG = ConstantValues.STENCIL_BUFFER_BIT;
    protected final static int TEST_CULLFACE = ConstantValues.CULL_FRONT_AND_BACK;
    protected final static float TEST_DEPTH = 0.4562345f;
    protected final static String FAILED_NO_EXCEPTION = "Should throw exception";
    protected final static int TEST_INVALID_VALUE = -512; //Test for invalid value
    /**
     * Test that it is possible to construct class.
     */
    public void testDefaultConstructor()    {

        RenderSetting rs = new RenderSetting();

        //Make sure fields are set to default.
        assertEquals(RenderSetting.DEFAULT_CLEARDEPTH, rs.getClearDepth());
        assertEquals(RenderSetting.DEFAULT_CULLFACE, rs.getCullFace());
        assertEquals(RenderSetting.DEFAULT_CLEARFLAG, rs.getClearFunction());
        assertEquals(RenderSetting.DEFAULT_DEPTHFUNC, rs.getDepthFunc());
        assertEquals(RenderSetting.DEFAULT_DEPTHRANGE_FAR, rs.getDepthRangeFar());
        assertEquals(RenderSetting.DEFAULT_DEPTHRANGE_NEAR, rs.getDepthRangeNear());
        assertEquals(RenderSetting.DEFAULT_MULTISAMPLING, rs.isMultisampling());

    }

    /**
     * Test constructor(s)
     */
    public void testConstructor()   {
        RenderSetting rs = new RenderSetting(TEST_CLEAR_FLAG, TEST_CULLFACE, false, false);

        assertEquals(TEST_CLEAR_FLAG, rs.getClearFunction());
        assertEquals(TEST_CULLFACE, rs.getCullFace());
        assertEquals(ConstantValues.NONE, rs.getDepthFunc());
        assertEquals(false, rs.isMultisampling());
    }

    /**
     * Test setting and getting of cleardepth
     */
    public void testClearDepth()    {
        RenderSetting rs = new RenderSetting();

        rs.setClearDepth(0f);
        assertEquals(0f, rs.getClearDepth());

        rs.setClearDepth(TEST_DEPTH);
        assertEquals(TEST_DEPTH, rs.getClearDepth());
    }

    /**
     * Test setting and getting of clear func
     */
    public void testClearFunc()     {
        RenderSetting rs = new RenderSetting();

        rs.setClearFunction(ConstantValues.NONE);
        assertEquals(ConstantValues.NONE, rs.getClearFunction());

        rs.setClearFunction(TEST_CLEAR_FLAG);
        assertEquals(TEST_CLEAR_FLAG, rs.getClearFunction());

    }
    /**
     * Test setting of invalid value.
     */
    public void testClearFuncInvalidValue() {
        RenderSetting rs = new RenderSetting();

        try {
            //Set value outside valid flags
            rs.setClearFunction(TEST_INVALID_VALUE);

            //Fail if no exception.
            assertTrue(FAILED_NO_EXCEPTION, false);
        }
        catch (IllegalArgumentException iae)    {
            //Correct
        }

    }

    /**
     * Test setting and getting of depth range near.
     */
    public void testDepthRangeNear()    {
        RenderSetting rs = new RenderSetting();

        rs.setDepthRangeNear(0f);
        assertEquals(0f, rs.getDepthRangeNear());

        rs.setDepthRangeNear(TEST_DEPTH);
        assertEquals(TEST_DEPTH, rs.getDepthRangeNear());
    }

    /**
     * Test setting and getting of depth range far
     */
    public void setDepthRangeFar()  {
        RenderSetting rs = new RenderSetting();

        rs.setDepthRangeNear(0f);
        assertEquals(0f, rs.getDepthRangeNear());

        rs.setDepthRangeFar(TEST_DEPTH);
        assertEquals(TEST_DEPTH, rs.getDepthRangeFar());
    }

    /**
     * Test setting and getting of cullface.
     */
    public void testCullFace()    {
        RenderSetting rs = new RenderSetting();

        rs.setCullFace(ConstantValues.NONE);
        assertEquals(ConstantValues.NONE, rs.getCullFace());

        rs.setCullFace(TEST_CULLFACE);
        assertEquals(TEST_CULLFACE, rs.getCullFace());
    }

    /**
     * Test exception for cullface when invalid value is set.
     */
    public void testCullFaceInvalidValue()  {
        RenderSetting rs = new RenderSetting();

        //Make sure exception if invald value
        try {
            rs.setCullFace(TEST_INVALID_VALUE);
            //Fail
            assertTrue(FAILED_NO_EXCEPTION, false);
        }
        catch (IllegalArgumentException iae){
            //Pass
            assertTrue(true);
        }


    }

    /**
     * Test setting and getting of depth function
     */
    public void testDepthFunc()     {
        RenderSetting rs = new RenderSetting();

        rs.setDepthFunc(ConstantValues.NONE);
        assertEquals(ConstantValues.NONE, rs.getDepthFunc());

        rs.setDepthFunc(TEST_DEPTH_FUNC);
        assertEquals(TEST_DEPTH_FUNC, rs.getDepthFunc());
    }

    /**
     * Test setting and getting of multisampling.
     */
    public void testMultisampling() {
        RenderSetting rs = new RenderSetting();

        rs.enableMultisampling(false);
        assertFalse(rs.isMultisampling());
        rs.enableMultisampling(true);
        assertTrue(rs.isMultisampling());

    }

}
