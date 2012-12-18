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
package com.super2k.openglen.android;


import java.nio.IntBuffer;

import android.opengl.GLES20;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.android.program.AndroidProgramHandler;
import com.super2k.openglen.android.texture.AndroidTextureHandler;
import com.super2k.openglen.android.utils.AndroidBitmapHandler;
import com.super2k.openglen.android.utils.AndroidGraphicsLibraryHandler;
import com.super2k.openglen.core.GLESBaseRenderer;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;


/**
 * Class implementing an OpenGL ES 2.0 based renderer using the
 * @link com.super2k.openglen.Renderer
 * @author Richard Sahlin
 *
 */
public class GLES20Renderer extends GLESBaseRenderer implements Renderer {

    private final String TAG = getClass().getSimpleName();

    private final int[] mTexUnits = new int[] {GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1, GLES20.GL_TEXTURE2,
            GLES20.GL_TEXTURE3, GLES20.GL_TEXTURE4, GLES20.GL_TEXTURE5,
            GLES20.GL_TEXTURE6, GLES20.GL_TEXTURE7, GLES20.GL_TEXTURE8 };
    /**
     * Creates a new GLES20 renderer
     * @param renderSetting The renderer settings. May be null to create a default setting.
     */
    public GLES20Renderer(RenderSetting renderSetting)   {
        super(renderSetting);
    }


    @Override
    public void beginFrame() {
        super.beginFrame();
        int flags = mRenderSetting.getClearFunction();
        if (flags != ConstantValues.NONE)
            GLES20.glClear(flags);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void finish() {

        GLES20.glFinish();

    }

    @Override
    public void flush() {

        GLES20.glFlush();

    }


    @Override
    public synchronized void initRenderer() throws OpenGLENException{
        super.initRenderer();

    }


    @Override
    protected void createTextureHandler() {
        //Will throw exception if mGraphicsUtilities is null
        mTextureHandler = new AndroidTextureHandler(mGraphicsUtilities);
    }


    @Override
    protected void createGraphicsLibraryUtilities() {
        mGraphicsUtilities = new AndroidGraphicsLibraryHandler();

    }

    @Override
    protected void createProgramHandler() {
        //Will throw exception if mGraphicsUtilities is null
        mProgramHandler = new AndroidProgramHandler(mGraphicsUtilities);
    }

    @Override
    protected void createBitmapHandler()    {
        mBitmapHandler = new AndroidBitmapHandler();
    }



    @Override
    public void setViewPort(int x, int y, int width, int height) {
        super.setViewPort(x, y, width, height);
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    protected void enableVertexAttributes(int count, int[] attributeArrays, int offset) {

        for (int i = offset; i < count; i++) {
            GLES20.glEnableVertexAttribArray(attributeArrays[i]);
        }

    }


    @Override
    protected void renderGLBlitOBject(int program,int[] uniformLocations, GLBlitObject blit) {

        int vcount = blit.getVertexCount();
        int indexCount = blit.getIndexCount();
        int stride = blit.mArrayByteStride;
        if (blit.arrayVBOName != -1) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, blit.arrayVBOName);
            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, stride, 0);
            GLES20.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, stride, 3 * 4);
            GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, stride,6 * 4);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, blit.elementVBOName);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
            //            mGraphicsUtilities.checkError();

            //update profiling
            mVBOvertexCount += vcount;
            mVBOIndexCount += indexCount;
        }
        else    {
            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, stride,
                    blit.arrayBuffer.position(0));
            GLES20.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, stride,
                    blit.arrayBuffer.position(3));
            GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, stride,
                    blit.arrayBuffer.position(6));
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT,
                    blit.indices.position(0));
            //update profiling
            mVertexCount += vcount;
            mIndexCount += indexCount;
        }

    }


    @Override
    protected void renderGLParticleArray(int program, int[] uniformLocations,
            GLParticleArray particleArray) {

        int vcount = particleArray.getActiveParticles();
        int stride = particleArray.mArrayByteStride;
        if (particleArray.arrayVBOName != -1) {

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, particleArray.arrayVBOName);
            GLES20.glBufferData(ConstantValues.ARRAY_BUFFER,
                    vcount * GLParticleArray.PARTICLE_FLOAT_COUNT * 4,
                    particleArray.arrayBuffer.position(0), ConstantValues.DYNAMIC_DRAW);
            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, stride,
                    GLParticleArray.POSITION);
            GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, stride,
                    GLParticleArray.COLOR * 4);
            GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false, stride,
                    GLParticleArray.VELOCITY * 4);
            GLES20.glVertexAttribPointer(3, 4, GLES20.GL_FLOAT, false, stride,
                    GLParticleArray.PACKED_DATA * 4);
            GLES20.glVertexAttribPointer(4, 4, GLES20.GL_FLOAT, false, stride,
                    GLParticleArray.COLOR_ADD * 4);
            GLES20.glDrawArrays(GLES20.GL_POINTS,0, vcount);
            mGraphicsUtilities.checkError();
            //update profiling
            mVBOvertexCount += vcount;
        }
        else    {
            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false,
                            stride, particleArray.arrayBuffer.position(GLParticleArray.POSITION));
            GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false,
                    stride, particleArray.arrayBuffer.position(GLParticleArray.COLOR));
            GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false,
                    stride, particleArray.arrayBuffer.position(GLParticleArray.VELOCITY));
            GLES20.glVertexAttribPointer(3, 4, GLES20.GL_FLOAT, false,
                    stride, particleArray.arrayBuffer.position(GLParticleArray.PACKED_DATA));
            GLES20.glVertexAttribPointer(4, 4, GLES20.GL_FLOAT, false,
                    stride, particleArray.arrayBuffer.position(GLParticleArray.COLOR_ADD));
            GLES20.glDrawArrays(GLES20.GL_POINTS,0, vcount);
            mGraphicsUtilities.checkError();
            //update profiling
            mVertexCount += vcount;
        }

    }


    @Override
    protected void internalReadPixels(int x, int y, int width, int height,
            int format, int type, IntBuffer buffer) {
        GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 4);
        GLES20.glReadPixels(x, y, width, height, format, type, buffer);
    }

}
