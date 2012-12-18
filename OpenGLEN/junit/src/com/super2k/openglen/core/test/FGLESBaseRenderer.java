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

package com.super2k.openglen.core.test;

import java.nio.IntBuffer;

import junit.framework.TestCase;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.core.GLESBaseRenderer;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;

/**
 * Test that the base implementation of renderer works
 * @author Richard Sahlin
 *
 */
public class FGLESBaseRenderer extends TestCase {

    private final static int TEST_WIDTH = 50;
    private final static int TEST_HEIGHT = 50;

    private final static int TEST_CLEAR_FLAG = ConstantValues.STENCIL_BUFFER_BIT;
    private final static int TEST_CULL_FLAG = ConstantValues.CULL_FRONT_AND_BACK;
    private final static boolean TEST_ENABLE_DEPTHTEST = false;
    private final static boolean TEST_ENABLE_MULTISAMPLING = true;
    /**
     * Make sure constructors work
     */
    public void testConstructor()   {

        RenderSetting settings = new RenderSetting(TEST_CLEAR_FLAG,
                TEST_CULL_FLAG,
                TEST_ENABLE_DEPTHTEST,
                TEST_ENABLE_MULTISAMPLING);

        GLESBaseRenderImpl render = new GLESBaseRenderImpl(settings);
        assertEquals(TEST_CLEAR_FLAG, render.getRenderSetting().getClearFunction());
        assertEquals(TEST_CULL_FLAG, render.getRenderSetting().getCullFace());
        assertEquals(TEST_ENABLE_MULTISAMPLING, render.getRenderSetting().isMultisampling());

        assertNotNull(render.getProfileInfo());
        assertNotNull(render.getRenderSetting());

    }

    /**
     * Empty implementation to test base implementation.
     * @author Richard Sahlin
     *
     */
    class GLESBaseRenderImpl extends GLESBaseRenderer {

        public GLESBaseRenderImpl(RenderSetting renderSetting) {
            super(renderSetting);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void flush() {
            // TODO Auto-generated method stub

        }

        @Override
        public void finish() {
            // TODO Auto-generated method stub

        }

        @Override
        public void destroy() {
            // TODO Auto-generated method stub

        }

        @Override
        public void setViewPort(int x, int y, int width, int height) {
            // TODO Auto-generated method stub

        }

        @Override
        protected void createTextureHandler() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void createGraphicsLibraryUtilities() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void createProgramHandler() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void createBitmapHandler() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void renderGLBlitOBject(int program, int[] uniformLocation,
                GLBlitObject blit) {
            // TODO Auto-generated method stub

        }

        @Override
        protected void enableVertexAttributes(int count, int[] attributeArrays, int offset) {
            // TODO Auto-generated method stub

        }

        @Override
        protected void renderGLParticleArray(int program, int[] uniformLocation,
                GLParticleArray particleArray) {
            // TODO Auto-generated method stub

        }

        @Override
        protected void internalReadPixels(int x, int y, int width, int height, int format,
                int type, IntBuffer buffer) {
            // TODO Auto-generated method stub

        }

    }

}
