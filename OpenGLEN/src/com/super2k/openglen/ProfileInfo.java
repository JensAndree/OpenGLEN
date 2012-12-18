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


package com.super2k.openglen;

import android.util.Log;


/**
 * Class containing profileinformation for benchmarking.
 * @author Richard Sahlin
 *
 */
public class ProfileInfo {

    protected final static String LOG_FILLRATE_STR = "Fillrate average/max/min: ";
    protected final static String LOG_VERTEXRATE_STR = "(non VBO/VBO) Vertexcount/s: ";
    protected final static String LOG_INDICERATE_STR = "(non VBO/VBO) Indices/s: ";
    protected final static String LOG_DRAWCALLS_STR = "Drawcalls: ";
    protected final static String LOG_COMPLEXITY_STR = "Complexity: ";
    protected final static String LOG_FRAMECOUNT_STR = "Frames: ";
    protected final static String LOG_AVERAGE_TICKS_STR = "Average ticks: ";
    protected final static String LOG_AVERAGE_FPS_STR = "FPS: ";

    protected int mMaxTicks;
    protected int mMinTicks;
    protected int mTotalTicks;
    protected int mFrameCount;
    protected int mVertexCount;
    protected int mIndexCount;
    protected int mVBOVertexCount;
    protected int mVBOIndexCount;
    protected int mDrawCalls;
    protected int mLogFrequency = 5000000; //Log info every 5 seconds by default.
    protected int mFrameTicks;

    protected int mDrawSize; //Size of output to screen on a per frame basis.
    protected long mCurrentTime = 0;
    protected long mPrevTime;

    /**
     * Array to store the frametime for each frame.
     */
    protected int[] mFrameTicksBuffer;

    /**
     * Index for frameticks and frametimestamp buffer.
     */
    protected int mBufferIndex = 0;

    /**
     * Array to store the timestamp for each frame.
     * Counted at beginning of frame.
     */
    protected long[] mFrameTimeStampBuffer;

    /**
     * If enabled framerate will be stored per frame.
     */
    protected boolean mEnablePerFrameProfiling = false;

    /**
     * Complexity of rendering, can be used to count number of objects/layers drawn.
     * This variable is application specific.
     */
    protected int mComplexity;

    /**
     * Resets values used for profiling - note that complexity is not reset since this
     * is considered to be set once.
     * To clear complexity call setComplexity(0);
     */
    public void reset() {
        mMaxTicks = 0;
        mMinTicks = 10000000;
        mTotalTicks = 0;
        mFrameCount = 0;
        mVertexCount = 0;
        mIndexCount = 0;
        mVBOVertexCount = 0;
        mVBOIndexCount = 0;
        mDrawCalls = 0;
        mFrameTicks = 0;
        mCurrentTime = 0;
    }

    /**
     * Enables per frame storing of fps.
     * @param flag True to enable storage of per frame fps, false to turn off.
     * Call createFrameStorage() before enabling this.
     */
    public void setPerFrameProfiling(boolean flag) {
        mEnablePerFrameProfiling = flag;
    }

    /**
     * Increases the complexity count, this variable is application specific.
     * Can be used to keep track of number of objects/layers etc.
     * @param count  Number to increase complexity with.
     * @return Total complexity after increase
     */
    public int increaseComplexity(int count) {
        mComplexity += count;
        return mComplexity;
    }

    /**
     * Set the complexity value, this variable is application specific.
     * @param count
     */
    public void setComplexity(int count) {
        mComplexity = count;
    }

    /**
     * Returns the complexity count, this is an application specific variable that can be used
     * to count number of objects/layers etc.
     * @return
     */
    public int getComplexity() {
        return mComplexity;
    }

    /**
     * Creates storage to store frameticks, timestamp and draw size for a specific number of frames.
     * Must be called in order for per frame profiling to work.
     * @param frames Number of frames to create storage for.
     * @throws IllegalArgumentException If frames is negative
     */
    public void createFrameStorage(int frames) {
        if (frames < 0) {
            throw new IllegalArgumentException("Invalid value, size of storage must be positive."
                                                + frames);
        }
        mFrameTicksBuffer = new int[frames];
        mFrameTimeStampBuffer = new long[frames];
    }

    /**
     * Returns the buffer holding the mikrosecond times for each frame during profiling OR
     * null if per frame profiling is not enabled.
     * @return Array containing per frame times in mikroseconds or null if not enabled.
     * @see #createFrameStorage(int)
     * @see #setPerFrameProfiling(boolean)
     */
    public int[] getFrameTicksBuffer() {
        return mFrameTicksBuffer;
    }

    /**
     * Returns the number of per frame fps values that are entered in the arrays.
     * @return Number of per frame fps values recorded.
     */
    public int getPerFrameCount() {
        return mBufferIndex;
    }

    /**
     * Returns buffer holdong the timestamps for the duration of each frame (ticks) OR
     * null if per frame profiling is not enabled.
     * @return Array holding timestamps or null if not enabled.
     * @see #createFrameStorage(int)
     * @see #setPerFrameProfiling(boolean)
     */
    public long[] getFrameTimestampBuffer() {
        return mFrameTimeStampBuffer;
    }

    /**
     * Sets the estimated size of one frame in number of pixels.
     * If set this value is used to calculate fillrate over time.
     * @param pixels Number of pixels drawn in one frame.
     */
    public void setDrawSize(int pixels) {
        mDrawSize = pixels;
    }

    /**
     * Returns the number of pixels in one frame.
     * @return Size of one frame in pixels, as set by setDrawSize()
     */
    public int getDrawSize() {
        return mDrawSize;
    }
    /**
     * Update the profile info, update timers and framecount.
     * This is normally called by the renderer. If you want to get a time value for the first
     * frame rendered you need to call this method before starting your render loop.
     * @param vertices The number of vertices processed this frame
     * @param indices The number of indices processed this frame.
     * @param VBOvertices The number of VBO vertices processed this frame.
     * @param VBOIndices The number of VBO indices processed this frame.
     * @param drawCalls The number of drawcalls
     */
    public void update(int vertices, int indices, int VBOvertices, int VBOIndices, int drawCalls) {

        //Update performance counting.
        mPrevTime = mCurrentTime;
        mCurrentTime = System.nanoTime();
        //We must skip first frame since we cannot get a delta time.
        if (mPrevTime != 0) {
            if (mCurrentTime < mPrevTime) {
                //Check for wrap of currenttime.
                mFrameTicks = (int) (0x7fffffff - mPrevTime + mCurrentTime) + 1;
                Log.d("ProfileInfo", "NanoTime wrapped: current,previous, ticks: " +
                        mCurrentTime + ", " + mPrevTime + ", " + mFrameTicks);
            } else {
                mFrameTicks = (int) ((mCurrentTime - mPrevTime)/1000);
            }

            mFrameCount ++;
            mVertexCount+= vertices;
            mIndexCount += indices;
            mVBOVertexCount+= VBOvertices;
            mVBOIndexCount+= VBOIndices;
            mDrawCalls += drawCalls;

            if (mFrameTicks > mMaxTicks)
                mMaxTicks = mFrameTicks;
            if (mFrameTicks < mMinTicks)
                mMinTicks = mFrameTicks;
            mTotalTicks += mFrameTicks;
            if (mEnablePerFrameProfiling &&
                    mFrameTicksBuffer != null &&
                    mBufferIndex < mFrameTicksBuffer.length) {
                mFrameTicksBuffer[mBufferIndex] = mFrameTicks;
                mFrameTimeStampBuffer[mBufferIndex++] = mCurrentTime;
            }
        }

    }

    /**
     * Return the total number of ticks since profiling started.
     * @return
     */
    public int getTotalTicks()  {
        return mTotalTicks;

    }
    /**
     * Return the number of frames since profiling started.
     * @return The number of frames passed since profile started.
     */
    public int getFramecount()      {
        return mFrameCount;
    }


    /**
     * Returns the time (in mikroseconds) between the last frame and this.
     * Time measurements are done in endFrame(), this method will return the delta between
     * two calls to endFrame()
     * Clients can call this method each frame to get time information to base movement
     * and animations on. Please note that the frame tick value will be 0 until endFrame()
     * has been called.
     * @return The time in mikroseconds between the last 2 endFrame() calls.
     */
    public int getFrameTicks()  {
        return mFrameTicks;
    }

    /**
     * Returns the time, in nanoseconds, of the previous frame.
     * Previous frame is switched when update is called.
     * @return The time in nanos of the previous frame.
     */
    public long getPreviousTime() {
        return mPrevTime;
    }

    /**
     * Returns the time, in nanoseconds, of the current frame.
     * Current frame is updated when update() is called.
     * @return The time in nanos of the current frame.
     */
    public long getCurrentTime() {
        return mCurrentTime;
    }

    /**
     * Return the max number of ticks (mikroseconds) in a frame.
     * @return Max ticks a frame took to produce (slowest frame)
     */
    public int getMaxTicks()    {
        return mMaxTicks;
    }

    /**
     * Return the minimum number of ticks (mikroseconds) a frame took.
     * @return Min ticks for a frame (fastest frame)
     */
    public int getMinTicks()        {
        return mMinTicks;
    }

    /**
     * Return the number of vertices sent to GL.
     * @return
     */
    public int getVertexCount() {
        return mVertexCount;
    }

    /**
     * Return the number of VBO vertices sent to GL.
     * @return
     */
    public int getVBOVertexCount()  {
        return mVBOVertexCount;
    }


    /**
     * Return number of indices sent to GL.
     * @return
     */
    public int getIndexCount()  {
        return mIndexCount;
    }

    /**
     * Return number of VBO indexes sent to GL.
     * @return
     */
    public int getVBOIndexCount()   {
        return mVBOIndexCount;
    }

    /**
     * Returns the number of drawcalls done, this is either drawArrays or drawElements
     * @return
     */
    public int getDrawCalls() {
        return mDrawCalls;
    }

    /**
     * Return the average ticks for each frame.
     * @return Average ticks per frame.
     */
    public int getAverageTicks()        {
        return (mTotalTicks / mFrameCount);
    }

    /**
     * Returns an array with the String to output for the profile info.
     * @param info
     * @return A string with info from this class.
     */
    public final String[] getProfileInforStr()       {

        String[] result = new String[3];
        float average = (getTotalTicks() / getFramecount());
        result[0] = LOG_AVERAGE_FPS_STR + 1000000 / getAverageTicks();
        result[1] = LOG_DRAWCALLS_STR + getDrawCalls() + ", " + LOG_COMPLEXITY_STR +
                getComplexity() + ", " + LOG_FRAMECOUNT_STR + getFramecount() +
                ", " + LOG_AVERAGE_TICKS_STR + average + " (" + getMaxTicks() + " / "
                + getMinTicks() + ")";
        int vCount = getVertexCount();
        int VBOvCount = getVBOVertexCount();
        int iCount = getIndexCount();
        int VBOiCount = getVBOIndexCount();
        float sec = (float) 1000000 / getTotalTicks();
        result[2] = LOG_VERTEXRATE_STR + (int) (vCount * sec) + "/" + (int) (VBOvCount * sec) +
                "," + LOG_INDICERATE_STR +  (int) (iCount * sec) + "/" + (int) (VBOiCount * sec);
        return result;
    }


}
