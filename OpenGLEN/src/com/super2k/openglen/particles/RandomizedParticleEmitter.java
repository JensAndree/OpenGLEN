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


/**
 * Emitter that has random parts for the emitted particles.
 * @author Richard Sahlin
 *
 */
public interface RandomizedParticleEmitter extends ParticleEmitter {

    /**
     * Sets the particle random direction, this will affect all new emitted particles
     * after a call to this method.
     * It will not change current particles.
     * @param randomDirection Random part of direction, ie
     * rand(-0.5f -> 0.5f) * factor is added to the direction of the particle
     * @param offset Offset into array where values are read.
     */
    public void setRandomDirection(float[] randomDirection, int offset);

    /**
     * Set the factor that the random position for new particles is scaled by.
     * @param xposScale New particles random position is scaled by this
     * The random value is added to particle base position when emitted,
     * random value is -0.5 to 0.5 * randomScale.
     * @param offset Offset into float array where X,Y,Z values are read.
     * @throws IllegalArgumentException If randomScale is null or does not
     * contain at least 3 values at offset.
     */
    public void setRandomPositionScale(float[] randomScale, int offset);

    /**
     * Sets the particle random velocity, ie both random direction and speed.
     * This will affect all new emitted particles after a call to this method.
     * It will not change the current movement for particles.
     * @param randomVelocity Direction is XYZ in first 3 values of array.
     * Value 4 is speed. [direction X, direction Y, direction Z, speed]
     * rand(-0.5f -> 0.5f) * factor is added to the direction of the particle
     * @param offset Offset into array where values are read.
     * @throws IllegalArgumentException If randomVelocity is null
     * or does not contain 4 values at offset.
     */
    public void setRandomVelocity(float[] randomVelocity, int offset);

}
