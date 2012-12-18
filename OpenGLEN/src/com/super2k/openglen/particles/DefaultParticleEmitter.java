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

package com.super2k.openglen.particles;

import java.util.Random;

import com.super2k.openglen.animation.LinearAnimation;
import com.super2k.openglen.objects.GLParticleArray;
import com.super2k.openglen.utils.Log;

/**
 * Default implementation of a particle emitter, implements runnable
 * Call the start() method to start the thread that will call
 * updateColors()
 * This class will have it's own thread that animates colorvalues at a specified interval.
 * @author Richard Sahlin
 *
 */
public abstract class DefaultParticleEmitter implements Runnable, RandomizedParticleEmitter {

    private final String TAG = getClass().getSimpleName();
    protected final static String INVALID_PARAMETER_STR = "Invalid parameter.";

    protected Random mRand = new Random(0761442435);

    protected boolean mRunning = false;
    protected boolean mDestroy = false;
    protected Thread mThread;

    protected int mThreadSleep = 50; //call update colors 20 times/s
    protected long mPreviousTime;
    protected long mCurrentTime;
    protected int mTicks;

    protected int mColorIndex = 0;
    protected int mColorTimer = 0;

    /**
     * Table containing setupvalues, MUST contain STATE_MAX_COUNT arrays.
     * pos, XYZ
     * random scale XYZ
     * velocity XYZ
     * emitrate one float
     */
    protected float[][] mSetupValues;
    protected final static int SETUP_VALUES_POS = 0; //3 values
    protected final static int SETUP_VALUES_RANDOM = 3; //3 values
    protected final static int SETUP_VALUES_VELOCITY = 6; //4 values
    protected final static int SETUP_VALUES_RANDOM_VELOCITY = 10; //4 values
    protected final static int SETUP_VALUES_EMITRATE = 14; //1 value
    protected final static int SETUP_VALUES_EMIT_COLORADD = 15; //4 values


    /**
     * Perspective size factor used when emitting particles, aligned
     * with depth in shader
     */
    protected float mEmitParticlePerspectiveSize = 1;

    /**
     * This array is put into particles when they are emitted.
     * It controlls how the particle moves, changes color etc.
     * NOTE! Values are overwritten by separate values before
     * a particle is emitted.
     * This array is simply for transfer to attribute array.
     * @see GLParticleArray
     */
    protected float[] mParticleData = new float[] {
                    0, 0, 0,    //position
                    0, 0, 0, 0, //velocity - NOTE! XYZ Normalized vector + speed in 4th component.
                    0, 0, 0, 1f,//color
                    0, 0, 0, 0, //color add
                    0, 0, 0, 0};//drag, size, intensity, time


    protected int mState;

    /**
     * Factor to base random value for new particle x, y , z pos.
     */
    protected float[] mParticleRandomScale = new float[3];
    /**
     * New particles x,y,z position.
     */
    protected float[] mParticlePos = new float[3];

    /**
     * Speed of new particles.
     */
    protected float[] mParticleVelocity = new float[4];

    /**
     * Factor to base random values for new particles speed.
     */
    protected float[] mParticleRandomVelocity = new float[4];

    /**
     * The color of emited (new) particles.
     */
    protected float[] mEmitParticleColor = new float[4];

    /**
     * Animation to cycle particle emit color.
     */
    protected LinearAnimation mEmitParticleColorAnim;
    protected float mPreviousParticleTime = 0;

    /**
     */
    public DefaultParticleEmitter() {
    }

    /**
     * Starts the thread.
     * If thread already exists this method throws an exception.
     * @throws IllegalArgumentException If thread exists, ie start() has been called before
     * and not exited properly.
     */
    public synchronized void start() {

        if (mThread == null) {
            mThread = new Thread(this);
            mRunning = true;
            mThread.start();
        } else {
            throw new IllegalArgumentException("Thread already exists, must terminate properly.");
        }

    }

    /**
     * Return the state of the thread, if true then it is running (and using the particlegenerator).
     * @return True if the thread is running
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Signal to the thread to exit if it has started, otherwise this does nothing.
     */
    public void destroy() {
        Log.v(TAG, "destroy()");
        mDestroy = true;
    }

    /**
     * Setup the variables needed.
     * Called when the thread is started.
     */
    public abstract void setup();

    @Override
    public void run() {

        try {
            Log.d(TAG, "Started thread to updated emitter.");
            setup();
            mCurrentTime = System.currentTimeMillis();
            mTicks = 0;
            while (mRunning && !mDestroy) {

                try {
                    Thread.sleep(mThreadSleep);
                } catch (InterruptedException ie) {
                    //Don't do anything. Cant recover from interrupted exception.
                }

                mPreviousTime = mCurrentTime;
                mCurrentTime = System.currentTimeMillis();
                mTicks = (int)(mCurrentTime-mPreviousTime);
                updateColors(mTicks);

            }

        }
        finally {
            Log.d(TAG, "Exiting thread.");
            //Stopped running release all resources.
            mRunning = false;
//            mParticleGenerator.destroy();
        }
    }

    /**
     * Update the color timers and color.
     * This method is called by the run method of this thread
     * after it is started by a call to start()
     * The rate of when this is done can be controlled by setting the
     * thread sleep.
     * @param ticks Number of millis that has elapsed since last call.
     */
    public abstract void updateColors(int ticks);

    /**
     * Sets the time delay between each loop in the run method.
     * @param millis Number of millis to delay. A value of 100 will loop
     * the thread 10 times a second.
     * @throws IllegalArgumentException If millis is negative.
     */
    public void setThreadRate(int millis) {
        if (millis < 0) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR+millis);
        }
        mThreadSleep = millis;
    }

    /**
     * Setup the variables needed for the specified state.
     * @param state
     * @throws IllegalArgumentException If state is <= 0 or larger than max state count.
     */
    public abstract void setupState(int state);

    /**
     * Update the state variable to use next state
     *
     */
    public abstract void nextState();

    /**
     * Return the state that the thread is in.
     * @return The state
     */
    public int getState() {
        return mState;
    }

    /**
     * Fetch 4 float values from the source array, scale according to divisor and return new array,.
     * @param source Source array with 4 values.
     * @param divisor source values are divided by this.
     * @return An array containing 4 scaled values.
     * @throw IllegalArgumentException If source is null or does not contain 4 values.
     */
    protected float[] getColor(float[] source, float divisor) {
        if (source == null || source.length<4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        return new float[] { source[0]/divisor, source[1]/divisor,
                source[2]/divisor, source[3]/divisor };
    }

    @Override
    public int emitParticles(GLParticleArray particles,
                            int count,
                            int offset,
                            int maxOffset,
                            float timeOffset) {
        float time = timeOffset-mPreviousParticleTime;
        mPreviousParticleTime = timeOffset;
        if (mEmitParticleColorAnim != null) {
            mEmitParticleColorAnim.animate(time);
        }
        particles.arrayBuffer.position(offset * GLParticleArray.PARTICLE_FLOAT_COUNT);
        mParticleData[GLParticleArray.TIME] = timeOffset;
        float r;
        mParticleData[GLParticleArray.INTENSITY] = 1f;
        for (int i = 0; i < count; i++) {
            //With a distance of 500 and a speed of 100 the particles have a lifetime of 5 seconds.
            r = (mRand.nextFloat() - 0.5f) * (mParticleRandomScale[0])+mParticlePos[0];
            mParticleData[GLParticleArray.POSITION] = r;
            r = (mRand.nextFloat() - 0.5f) * (mParticleRandomScale[1])+mParticlePos[1];
            mParticleData[GLParticleArray.POSITION+1] = r;
            r = (mRand.nextFloat()-0.5f) * (mParticleRandomScale[2])+mParticlePos[2];
            mParticleData[GLParticleArray.POSITION+2] = r;
            mParticleData[GLParticleArray.SIZE] = mEmitParticlePerspectiveSize *
            (mRand.nextFloat() + 1.2f);
            System.arraycopy(mEmitParticleColor, 0, mParticleData, GLParticleArray.COLOR, 4);
            mParticleData[GLParticleArray.VELOCITY] = (mRand.nextFloat()-0.5f)
                    * mParticleRandomVelocity[0] + mParticleVelocity[0];
            mParticleData[GLParticleArray.VELOCITY+1] = (mRand.nextFloat()-0.5f)
                    * mParticleRandomVelocity[1] + mParticleVelocity[1];
            mParticleData[GLParticleArray.VELOCITY+2] = (mRand.nextFloat()-0.5f)
                    * mParticleRandomVelocity[2] + mParticleVelocity[2];
            mParticleData[GLParticleArray.VELOCITY + 3] = mParticleVelocity[3]
                    +(mRand.nextFloat()-0.5f) * mParticleRandomVelocity[3];
            //Take alpha from mParticleData array
            particles.arrayBuffer.put(mParticleData);
            offset++;
            if (offset >= maxOffset) {
                offset = 0;
                particles.arrayBuffer.position(0);
            }
        }

        return count;
    }

    @Override
    public void setEmitColor(float[] emitColor, int index) {
        if (emitColor==null || emitColor.length < index+4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mEmitParticleColor[0] = emitColor[index++];
        mEmitParticleColor[1] = emitColor[index++];
        mEmitParticleColor[2] = emitColor[index++];
        mEmitParticleColor[3] = emitColor[index++];
    }

    @Override
    public void setEmitColorCycle(float[] endColor, float startTime, float endTime, int loop) {
        if (mEmitParticleColorAnim == null) {
            mEmitParticleColorAnim = new LinearAnimation();
        }
        mEmitParticleColorAnim.setup(mEmitParticleColor, mEmitParticleColor,
                endColor, startTime, endTime, loop);
    }

    @Override
    public void setRandomPositionScale(float[] randomScale, int offset) {
        if (randomScale == null || randomScale.length < 3) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mParticleRandomScale[0] = randomScale[offset++];
        mParticleRandomScale[1] = randomScale[offset++];
        mParticleRandomScale[2] = randomScale[offset++];
    }

    /**
     * Sets the new particle start position, xyz.
     * This value will be the base for the start position of
     * all new particles.
     * @param pos Array with x,y,z base position for new particles.
     * @param offset Offset into float array where values are read.
     * @throws IllegalArgumentException pos is null or does not
     * contain at least 3 values.
     */
    @Override
    public void setPosition(float[] pos, int offset) {
        if (pos == null || pos.length < 3) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mParticlePos[0] = pos[offset++];
        mParticlePos[1] = pos[offset++];
        mParticlePos[2] = pos[offset++];
    }

    @Override
    public float[] getPosition() {
        return mParticlePos;
    }

    @Override
    public void setVelocity(float[] particleVelocity, int offset) {
        if (particleVelocity == null || particleVelocity.length < offset+4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(particleVelocity, offset, mParticleVelocity, 0, 4);

    }

    @Override
    public void setDirection(float[] particleDirection, int offset) {
        if (particleDirection == null || particleDirection.length < offset+3) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(particleDirection, offset, mParticleVelocity, 0, 3);
    }

    @Override
    public void setSpeed(float speed) {
        mParticleVelocity[3] = speed;
    }

    @Override
    public void setRandomVelocity(float[] randomVelocity, int offset) {
        if (randomVelocity == null || randomVelocity.length < offset+4) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(randomVelocity, offset, mParticleRandomVelocity, 0, 4);

    }

    @Override
    public void setRandomDirection(float[] randomDirection, int offset) {
        if (randomDirection == null || randomDirection.length < offset+3) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(randomDirection, 0, mParticleRandomVelocity, 0, 3);
    }

    @Override
    public void setEmitColorDelta(float[] colorDelta, int offset) {
        if (colorDelta == null || colorDelta.length < 4 + offset) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        System.arraycopy(colorDelta, offset, mParticleData, GLParticleArray.COLOR_ADD, 4);

    }

}
