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

import com.super2k.openglen.objects.GLParticleArray;

/**
 * Interface for a class that emits particles.
 * @author Richard Sahlin
 *
 */
public interface ParticleEmitter {

    /**
     * Emits a number of particles to the GLParticleArray with the specified timeOffset.
     * Movement of the particles shall be handled by the caller, ie updating the time in the
     * GLParticleArray.
     * The implementation may add more or less particles if desired.
     * @see GLParticleArray To see what data should be written for particles.
     * @param particles Array to add particles to, add particles as data values into
     * arrayBuffer.
     * @param count Number of particles to add, as specified by setParticleEmitRate and
     * the number of milliseconds that has passed since last time this was called.
     * @param offset Offset into particles array, counted in number of particles.
     * Set the buffer position to offset * GLParticleArray.PARTICLE_FLOAT_COUNT
     * @param maxOffset Wrap-round when this offset is reached, ie when offset == maxOffset
     * wrap round and set offset to 0 (before writing data to buffer)
     * @param timeOffset Time offset for particles to add, particle lifetime will be calculated
     * current time - timeOffset.
     * @return The offset to next free particle, ie count up with number of particles released.
     * Note: Return next free index counted in number of particles, ie offset + count.
     * Does not have to check for wrap around.
     * @throws IllegalArgumentException if particles or color is null or color contains less than 4 values.
     * @see GLParticleArray
     */
    public int emitParticles(GLParticleArray particles, int count, int offset, int maxOffset,
            float timeOffset);


    /**
     * Sets the particle direction and speed, it is encouraged to use
     * normalized values for the direction, but not mandatory.
     * This will affect all particles emitted after this call has returned,
     * it will not change the current movement for particles.
     * @param particleVelocity Direction is XYZ in first 3 values of array.
     * Value 4 is speed. [direction X, direction Y, direction Z, speed]
     * @param offset Offset into array where values are.
     * @throws IllegalArgumentException If particleVelocity is null
     * or does not contain 4 values at offset.
     */
    public void setVelocity(float[] particleVelocity, int offset);

    /**
     * Sets the particle direction, while leaving the speed unchanged.
     * It is encouraged to use normalized values for the direciton.
     * This will affect particles emitted after this call has returned.
     * It will not change current particels.
     * @param particleDirection Normalized direction, XYZ.
     * @param offset Offset into array where values are read.
     */
    public void setDirection(float[] particleDirection, int offset);

    /**
     * Sets the particle speed.
     * This will affect all new emitted particles after a call to this method.
     * It will not affect already released particles.
     * @param speed
     */
    public void setSpeed(float speed);

    /**
     * Sets the particle emit color, this is the color that new emited particle will have.
     * Per particle property.
     * @param emitColor array with 4 values, RGBA - Red at index, Green at index + 1,
     * Blue at index + 2 and alpha at index + 3.
     * Please not that for alpha to work correctly with depth the particles
     * needs to be sorted. Sorting is currently not supported.
     * Either use alpha in a projection without depth,when sort order does not matter, or
     * with a spread of particles so you know they will not overlap.
     * @param index Index into array where values are read.
     * @throws IllegalArgumentException If emitColor is null or does not contain 4 values at index.
     */
    public void setEmitColor(float[] emitColor, int index);

    /**
     * Sets a color cycle for the emit color of particles.
     * The colorcycle will start at the current emit particle color and and at endColor.
     * This will not cycle the colors of the particles after emitted, only the color
     * that the particle has when it is emitted. Using this function will override the
     * emitColor.
     * @param endColor End color of the cycle.
     * @param startTime Start time, normally 0.
     * @param endTime End time of cycle.
     * @param loop LOOP_INFINITE to loop forever, LOOP_DISABLED to disable looping, or
     * number of times animation shall loop.
     */
    public void setEmitColorCycle(float[] endColor, float startTime, float endTime, int loop);


    /**
     * Sets the colordelta per timeunit for particles, this is a per particle
     * property that is loaded when particle is emitted.
     * Particles emitted after this method is called will be affected.
     * @param colorDelta Color (RGBA) delta values per time unit, ie to
     * have a color go from 0 to 1 in one second, set value to 1
     * @param offset Offset into array.
     * @throws IllegalArgumentException If colorDelta is null or does not contain 4 values
     * at offset.
     */
    public void setEmitColorDelta(float[] colorDelta, int offset);

    /**
     * Sets the new particle start position, xyz.
     * This value will be the base for the start position of
     * all new particles.
     * @param pos Array with x,y,z base position for new particles.
     * @param offset Offset into float array where values are read.
     * @throws IllegalArgumentException pos is null or does not
     * contain at least 3 values.
     */
    public void setPosition(float[] pos, int offset);

    /**
     * Returns a reference to the array holding the position of the emitter.
     * @return Reference to array holding the position (X,Y,Z) any updates
     * to this array will affect the emitter position.
     * This can be used to drive an animation.
     */
    public float[] getPosition();


}
