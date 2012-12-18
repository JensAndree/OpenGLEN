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
 * Class encapsulating result for particle position and color data.
 * @author Richard Sahlin
 *
 */
public class ParticleBitmapData {

    /**
     * Array with position data.
     */
    public float[] position;

    /**
     * Array with color.
     */
    public float[] color;

    /**
     * Default constructor, does not create any arrays.
     */
    public ParticleBitmapData() {

    }

    /**
     * Creates a new container for particle positions and color.
     * Will allocate count number of X,Y positions and count number of
     * R,G,B color values.
     * This means that the position array will be 2*count, color array 3*count.
     * @param count Number of particles to allocate storage for.
     */
    public ParticleBitmapData(int count) {
        position = new float[count*2];
        color = new float[count*3];
    }

}
