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
package com.super2k.openglen.j2se;


import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;

import com.jogamp.newt.opengl.GLWindow;
import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.EGLRenderer;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.j2se.program.J2SEProgramHandler;
import com.super2k.openglen.j2se.texture.J2SETextureHandler;
import com.super2k.openglen.j2se.utils.J2SEBitmapHandler;
import com.super2k.openglen.j2se.utils.J2SEGraphicsLibraryHandler;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.objects.GLParticleArray;


/**
 * An implementation of a JOGAMP GLES20 renderer.
 * JOGAMP does not use EGL, but this class implements the EGLRenderer interface to
 * get the swapbuffer behavior. Instead of EGL the GLWindow is used to set capabilities, this
 * is done in the OpenGLENWindow class.
 * @author Richard Sahlin
 *
 */
public class JOGLGLES20Renderer extends JOGLRenderer implements EGLRenderer{


    private final static String INVALID_CONTEXT_STR = "GLContext is NULL";
    private final static String INVALID_GLDRAWABLE_STR = "GLDrawable is NULL";

    /**
     * Minimum number of millis for each frame.
     * Will sleep if current frame is created faster than this.
     */
    protected int mMinFrameMikros = 13000;

    GL2ES2    mGles2;
    GLDrawable mGlDrawable;
    GLContext mContext;



    /**
     * Creates a new GLCanvas with default configuration
     * @param width The width of the render area.
     * @paranm height The height of the render area.
     * @param glContext The GLContext.
     * @param glDrawable The drawable where rendering is done.
     * @param renderConfig The render configuration.
     * @throws IllegalArgumentException If width or height <= 0, glDrawable or glContext is null.
     */
    public JOGLGLES20Renderer(int width, int height,GLContext glContext, GLDrawable glDrawable, RenderSetting renderConfig) {
        super(renderConfig);
        if (glContext == null) {
            throw new IllegalArgumentException(INVALID_CONTEXT_STR);
        }
        if (glDrawable == null) {
            throw new IllegalArgumentException(INVALID_GLDRAWABLE_STR);
        }
        mContext = glContext;
        mGlDrawable = glDrawable;
    }


    @Override
    public void beginFrame() {
        super.beginFrame();

        //There is a fix for Jogamp/OpenGL after swapbuffer to clear screen
        //regardless of alpha. Jogamp seems to copy all rendering to one
        //buffer, whereas ES doesn't keep any buffer after swapbuffer.
        //Clearing accum buffer does not seem to help.

        int flags = mRenderSetting.getClearFunction();
        if (flags != ConstantValues.NONE)
            mGles2.glClear(flags);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void flush() {
        mGles2.glFlush();
    }

    @Override
    public synchronized void initRenderer() throws OpenGLENException{
        mContext.makeCurrent();
        mGles2 = mContext.getGL().getGL2ES2();
        super.initRenderer();
        //JOGL specific init
        mGles2.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE_ARB);
        mGles2.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
    }

    @Override
    public void finish() {
        mGles2.glFinish();
    }


    @Override
    protected void createTextureHandler() {
        //Will throw exception if mGles2 or mGraphicsUtilities is null
        mTextureHandler = new J2SETextureHandler(mGles2, mGraphicsUtilities);
    }


    @Override
    protected void createGraphicsLibraryUtilities() {
        //Will throw exception if mGles2 is null
        mGraphicsUtilities = new J2SEGraphicsLibraryHandler(mGles2);

    }

    @Override
    protected void createProgramHandler() {
        //Will throw exception if mGles2 or mGraphicsUtilities is null
        mProgramHandler = new J2SEProgramHandler(mGles2, mGraphicsUtilities);

    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        super.setViewPort(x, y, width, height);
        mGles2.glViewport(x, y, width, height);
    }

    @Override
    protected void createBitmapHandler() {
        mBitmapHandler = new J2SEBitmapHandler();

    }

    @Override
    protected void enableVertexAttributes(int count, int[] attributeArrays, int offset) {
        for (int i = offset; i < count; i++) {
            mGles2.glEnableVertexAttribArray(attributeArrays[i]);
        }

    }


    @Override
    protected void renderGLBlitOBject(int program, int[] uniformLocations, GLBlitObject blit) {

        int vcount = blit.getVertexCount();
        int indexCount = blit.getIndexCount();
        int stride = blit.mArrayByteStride;
        if (blit.arrayVBOName != -1) {
            mGles2.glBindBuffer(GL.GL_ARRAY_BUFFER, blit.arrayVBOName);
            mGles2.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, 0);
            mGles2.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, 3 * 4);
            mGles2.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, stride,6 * 4);
            mGles2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, blit.elementVBOName);
            mGles2.glDrawElements(GL.GL_TRIANGLES, indexCount, GL.GL_UNSIGNED_SHORT, 0);
            mGraphicsUtilities.checkError();

            //update profiling
            mVBOvertexCount += vcount;
            mVBOIndexCount += indexCount;
        }
        else    {
            mGles2.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, blit.arrayBuffer.position(0));
            mGles2.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, blit.arrayBuffer.position(3));
            mGles2.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, stride, blit.arrayBuffer.position(6));
            mGles2.glDrawElements(GL.GL_TRIANGLES, indexCount, GL.GL_UNSIGNED_SHORT,
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
            mGles2.glBindBuffer(GL.GL_ARRAY_BUFFER, particleArray.arrayVBOName);
            mGles2.glBufferData(ConstantValues.ARRAY_BUFFER,
                    vcount * GLParticleArray.PARTICLE_FLOAT_COUNT * 4,
                    particleArray.arrayBuffer.position(0), ConstantValues.DYNAMIC_DRAW);
            mGles2.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, GLParticleArray.POSITION);
            mGles2.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, stride, GLParticleArray.COLOR * 4);
            mGles2.glVertexAttribPointer(2, 4, GL.GL_FLOAT, false, stride, GLParticleArray.VELOCITY * 4);
            mGles2.glVertexAttribPointer(3, 4, GL.GL_FLOAT, false, stride, GLParticleArray.PACKED_DATA * 4);
            mGles2.glVertexAttribPointer(4, 4, GL.GL_FLOAT, false, stride, GLParticleArray.COLOR_ADD * 4);
            mGraphicsUtilities.checkError();
            mGles2.glDrawArrays(GL.GL_POINTS, 0, vcount);
            mGraphicsUtilities.checkError();
            //update profiling
            mVBOvertexCount += vcount;
        }
        else    {
            mGles2.glVertexAttribPointer(0, 3,
                    GL.GL_FLOAT, false, stride, particleArray.arrayBuffer.position(GLParticleArray.POSITION));
            mGles2.glVertexAttribPointer(1, 4,
                    GL.GL_FLOAT, false, stride, particleArray.arrayBuffer.position(GLParticleArray.COLOR));
            mGles2.glVertexAttribPointer(2, 4,
                    GL.GL_FLOAT, false, stride, particleArray.arrayBuffer.position(GLParticleArray.VELOCITY));
            mGles2.glVertexAttribPointer(3, 4,
                    GL.GL_FLOAT, false, stride, particleArray.arrayBuffer.position(GLParticleArray.PACKED_DATA));
            mGles2.glVertexAttribPointer(4, 4,
                    GL.GL_FLOAT, false, stride, particleArray.arrayBuffer.position(GLParticleArray.COLOR_ADD));
            mGles2.glDrawArrays(GL.GL_POINTS, 0, vcount);
            //update profiling
            mVertexCount += vcount;
        }

    }


    @Override
    public Object createEGL(Object display) throws OpenGLENException {

        /**
         * EGL is not use in JOGAMP but we create a GLDrawable that can do swapbuffers.
         */
        if (display instanceof GLWindow) {
            GLDrawable glDrawable = ((GLWindow)display).getFactory().createGLDrawable(((GLWindow)display).getNativeSurface());
            mContext = glDrawable.createContext(null);
        }
        else {
            throw new IllegalArgumentException("Invalid display object:" + display);
        }
        return null;
    }


    @Override
    public boolean swapBuffers() {

        long prev = mProfileInfo.getPreviousTime();
        long current;
        if (prev != 0) {
            while ((int)(((current = System.nanoTime()) - prev) / 1000) < mMinFrameMikros) {
                try {
                    int millis = (int)((current-prev)/1000000);
                    int nanos = (int)((current-prev) - millis * 1000000);
//                    Thread.sleep((int)((current-prev + 500000)/1000000));
                    Thread.sleep(millis, nanos);
                }
                catch (InterruptedException e) {

                }
            }
        }

        mGlDrawable.swapBuffers();
        //Check if clear color has alpha then first clear background with 0 alpha.

        float[] clearCol = mRenderSetting.getClearColor();
        if (clearCol[3] < 1) {
            mGles2.glClearColor(0, 0, 0, 1);
            mGles2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            mGles2.glFinish();
            mGles2.glClearColor(clearCol[0], clearCol[1], clearCol[2], clearCol[3]);
        }

        return true;
    }


    @Override
    public void releaseEGL() {
        /**
         * Nothing to do on AWT. JOGAMP does not have an EGL implementation.
         */


    }


    @Override
    public Object getEGLConfig() {
        /**
         * Nothing to do on AWT. JOGAMP does not have an EGL implementation.
         */
        return null;
    }


    @Override
    protected void internalReadPixels(int x, int y, int width, int height,
            int format, int type, IntBuffer buffer) {
        mGles2.glPixelStorei(GL.GL_PACK_ALIGNMENT, 4);
        mGles2.glReadPixels(x,  y, width, height, format, type, buffer);
    }


}
