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
package com.super2k.openglen.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.texture.Texture2D;
import com.super2k.openglen.utils.JavaUtils;
import com.super2k.openglen.utils.Log;
import com.super2k.openglen.utils.Matrix;

/**
 * An array of particles that can be moved and rendered on the GPU.
 * The behavior of vertex array particles does not have
 * any way of altering the velocity in runtime, ie once
 * the particle is emitted it will keep its velocity.
 * Gravity affects position but does not alter velocity.
 * This type cannot (easily) collide with geometry, bounds  or other particles.
 * @author Richard Sahlin
 *
 */
public class GLParticleArray extends GLObject {

    private final String TAG = getClass().getSimpleName();

    protected final static String INVALID_PARAMETER_STR = "Invalid parameter";

    /**
     * ****************************************************************
     * Let type values correspond to the program that will be used.
     * Look in ParticleProgramCollenction and see that the shader
     * program names correspond to the type values.
     * ****************************************************************
     */

    /**
     * Particle that has color speed, direction and size.
     * color add value (RGBA).
     * All other values are disregarded.
     * This will map to program 0.
     */
    public final static int TYPE_SIMPLE_UNLIT_PARTICLE = 0;

    /**
     * Particle that has the same properties as the simple unlit particle
     * plus the following:
     * Gravity factor in x,y, z and time.
     * Perspective division based on a set size.
     * This will map to program 1
     */
    public final static int TYPE_UNLIT_PARTICLE = 1;

    /**
     * Same as unlit particle but color is taken from
     * texture based on particles screen position.
     * Texture is stored in Material.
     */
    public final static int TYPE_IMAGE_PARTICLE = 2;

    /**
     * Unlit particle that has separate position, velocity and color for each particle.
     * Use this when all particles are released at the same time may have individual position,
     * color and size. All other values are shared.
     * All particles are affected by gravity and have color add.
     */
    public final static int TYPE_POSITION_PARTICLE = 3;

    protected final static int PARTICLE_TYPE_COUNT = TYPE_POSITION_PARTICLE + 1;
    /**
     * **********************************************************
     * PARTICLE OFFSETS
     * Defines for variable positions within the particle array.
     * These values are the offsets within each particles memspace.
     * **********************************************************
     */

    /**
     * The particle start position, X,Y,Z
     * X at offset 0
     */
    public final static int POSITION = 0;    //X, Y, Z
    /**
     * The particle XYZ Velocity
     * Velocity is made up of direction (XYZ) and speed.
     */
    public final static int VELOCITY = 3;    //X, Y, Z + speed
    /**
     * The particle RGBA color
     */
    public final static int COLOR = 7;  //RGBA

    /**
     * Per particle size
     */
    public final static int SIZE = 11;        //1 float
    public final static int PACKED_DATA = 11; //pack size, time, intensity into data.

    /**
     * Particle start time, subtracted from global time
     * to get particle lifetime.
     */
    public final static int TIME = 12;        //1 float

    /**
     * Particle intensity.
     */
    public final static int INTENSITY = 13;   //1 float

    /**
     * Particle RGBA colorcycle, multiplied by lifetime and added to color
     */
    public final static int COLOR_ADD = 14;  //RGBA


    /**
     * Number of float values that is used for each particle
     */
    public final static int PARTICLE_FLOAT_COUNT = COLOR_ADD + 4;

    /*
     * ***************************************************
     * END PARTICLE OFFSETS
     * ***************************************************
     */
    private final int GRAVITY_VALUE_COUNT = 3;
    /**
     * ******************************************************
     * Packed data uniforms in the data_uniform array
     * in the vertex shader
     * ******************************************************
     */
    /**
     * Modelview matrix, this is applied before particles movement.
     */
    public float[] modelViewMatrix = new float[16];

    /**
     * The type of particles.
     */
    protected int mParticleType;
    /**
     * Number of vertices, ie number of particles.
     */
    protected int mVertexCount;

    /**
     * Number of active (rendered) particles
     */
    protected int mActiveParticles;

    /**
     * Byte stride for vertices, this is used to align array
     * data in memory.
     */
    public int mArrayByteStride;

    /**
     * Index into data uniform for gravity, gravity is stored as 3 values - x,y,z.
     *  use this index when storing data
     * in the data_uniform
     */
    public final static int GRAVITY_UNIFORM_INDEX = 0;

    /**
     * Index into data uniform for some data, use this index when storing data
     * in the data_uniform
     * time
     * size
     */
    public final static int DATA_UNIFORM_INDEX = 4;

    /**
     * Index into data uniform for color add, use this index when storing data
     * in the data_uniform
     */
    public final static int COLOR_ADD_UNIFORM_INDEX = 8;

    /**
     * //Time offset, this is the main timeline
     */
    public final static int TIME_UNIFORM = DATA_UNIFORM_INDEX;
    /**
     * Size offset, added to all particles, regardless of Z.
     */
    public final static int SIZE_UNIFORM = DATA_UNIFORM_INDEX + 1;

    /**
     * Store float data in vec4
     */
    public float[] data_uniform = new float[4 * 3];

    /**
     * 1 / texturesize used for texture lookups when using TYPE_IMAGE_PARTICLES
     */
    public float[] mOneBySize = new float[2];

    /**
     * Initializes the particle array for the specified number of particles.
     * After this method is called the appropriate storage is allocated, though no values are setup.
     * @param particleCount  Number of particles, the same number of particles are always rendered.
     * @param type Type of particle
     * Currently TYPE_SIMPLE_UNLIT_PARTICLE or TYPE_UNLIT_PARTICLE
     * @throws IllegalArgumentException If particleCount is negative or type is invalid.
     */
    public void init(int particleCount, int type) {
        if (particleCount < 0 ) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        Log.d(TAG, "Particlecount: " + particleCount);
        setParticleType(type);
        setActiveParticles(particleCount);
        int tries = 0;
        while (arrayBuffer == null) {
            try {
                arrayBuffer = ByteBuffer.allocateDirect(
                        particleCount * 4 * PARTICLE_FLOAT_COUNT).order(ByteOrder.nativeOrder()).
                                                                        asFloatBuffer();
                mArrayByteStride = PARTICLE_FLOAT_COUNT * 4;    //Byte stride for array.
            }
            catch (OutOfMemoryError e) {
                tries++;
                if (tries > 1) {
                    throw e;
                }
                Log.d(TAG, "Could not allocate memory for nio.FloatBuffer, retrying once");
                JavaUtils.stabilizeFreeMemory();
            }
        }

        mVertexCount = particleCount;
        material = new Material();
        material.materialShading = Material.SHADING_UNLIT;
        Matrix.setIdentityM(modelViewMatrix, 0);
        Log.d(TAG, "Created particle array for " + particleCount + " particles.");

    }

    /**
     * Initialized the particle array with TYPE_IMAG_PARTICLE
     * and the specified texture.
     * Note that the platform must have support for at least one
     * texture in the vertex shader.
     * @param particleCount
     * @param image
     * @throws IllegalArgumentException If image is null.
     */
    public void init(int particleCount, Texture2D image) {
        init(particleCount, TYPE_IMAGE_PARTICLE);
        if (image == null) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        setParticleImage(image);
    }

    /**
     * Returns the maximum number of  particles(vertices)
     * This is the max number of particles that this array can have active.
     * @return Max number of particles (vertices)
     */
    public int getMaxParticleCount() {
        return mVertexCount;
    }

    /**
     * Sets the amount of particles that should be rendered.
     * Active particles start at offset 0.
     * @param particles Number of particles to be active, ie be rendered.
     */
    public void setActiveParticles(int particles) {
        mActiveParticles = particles;
    }

    /**
     * Returns the number of active particles, ie how many particles that are rendered.
     * @return Number of active (rendered) particles.
     */
    public int getActiveParticles() {
        return mActiveParticles;
    }

    /**
     * Returns the type of particles.
     * @return TYPE_SIMPLE_UNLIT_PARTICLE, TYPE_UNLIT_PARTICLE, TYPE_IMAGE_PARTICLE
     * or TYPE_POSITION_PARTICLE
     */
    public int getParticleType() {
        return mParticleType;
    }

    /**
     * Sets the particle type
     * @param type The new particle type, must be one of
     * TYPE_SIMPLE_UNLIT_PARTICLE
     * TYPE_IMAGE_PARTICLE
     * TYPE_UNLIT_PARTICLE
     * @throws IllegalArgumentException If type < 0 or not one of the valid types.
     */
    public void setParticleType(int type) {
        if (type < 0 || type > PARTICLE_TYPE_COUNT - 1) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        mParticleType = type;
    }
    /**
     * Sets an image to be used as lookup for particle colors.
     * Type must be set to TYPE_IMAGE_PARTICLE otherwise an exception is thrown.
     * When particles are rendered they will take color from corresponding
     * position from texture.
     * @param particleImage
     * @throws IllegalArgumentException if particleImage is null or type is not
     * TYPE_IMAGE_PARTICLE
     */
    public void setParticleImage(Texture2D particleImage) {
        if (particleImage == null || mParticleType != TYPE_IMAGE_PARTICLE) {
            throw new IllegalArgumentException(INVALID_PARAMETER_STR);
        }
        material.texture = new Texture2D[1];
        material.texture[0] = particleImage;
        mOneBySize[0] = (float)1/particleImage.getWidth();
        mOneBySize[1] = (float)1/particleImage.getHeight();

    }

    /**
     * Set the gravity x,y,z.
     * @param x
     * @param y
     * @param z
     */
    public void setGravity(float x, float y, float z) {
        data_uniform[GRAVITY_UNIFORM_INDEX] = x;
        data_uniform[GRAVITY_UNIFORM_INDEX + 1] = y;
        data_uniform[GRAVITY_UNIFORM_INDEX + 2] = z;
    }

    /**
     * Set gravity x,y,z and time from an array of values.
     * @param gravity Gravity values.
     * @param index Index into array where values are read.
     * @throws IllegalArgumentException If gravity is null or does not contain 4 values at
     * index.
     */
    public void setGravity(float[] gravity, int index) {
        if (gravity == null || gravity.length < index + GRAVITY_VALUE_COUNT) {
            throw new IllegalArgumentException("Invalid array");
        }
        System.arraycopy(gravity, index, data_uniform, GRAVITY_UNIFORM_INDEX, GRAVITY_VALUE_COUNT);
    }

    @Override
    public void releaseObject() {
        mActiveParticles = 0;
    }

    @Override
    public void createObject(Object obj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroyObject(Object obj) {
        // TODO Auto-generated method stub

    }


}
