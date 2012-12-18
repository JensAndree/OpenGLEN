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

package com.super2k.openglen.lighting;

/**
 * Implementation of a Directional light, all lightrays are considered to be planar.
 * Light has position, direction and color of light.
 * @author Richard Sahlin
 *
 */
public class DirectionalLight extends Light {

    public float[] direction = new float[4];

    /**
     * Default constructor.
     */
    public DirectionalLight() {
        super();
    }

    /**
     * Constructs a Directional light, all lightrays are considered to be planar.
     * Light is directed at origin (0, 0, 0) from the specified position.
     * Alpha is set to 1.
     * @param x X point light is traveling from
     * @param y Y point light is traveling from
     * @param z Z point light is traveling from
     * @param red Red factor of the light color
     * @param green Green factor of the light color
     * @param blue Blue factor of the light color
     */
    public DirectionalLight(float x, float y, float z, float red, float green, float blue) {
        super();
        setup(x, y, x, red, green, blue, 1f);

    }

    /**
     * Constructs a Directional light, all lightrays are considered to be planar.
     * Light is directed at origin (0, 0, 0) from the specified position.
     * @param x X point light is traveling from
     * @param y Y point light is traveling from
     * @param z Z point light is traveling from
     * @param red Red factor of the light color
     * @param green Green factor of the light color
     * @param blue Blue factor of the light color
     * @param alpha Alpha factor for the light.
     */
    public DirectionalLight(float x, float y, float z, float red, float green, float blue,
            float alpha) {
        super();
        setup(x, y, x, red, green, blue, alpha);

    }

    /**
     * Initializes the light with the specified values.
     * @param x X point light is traveling from
     * @param y Y point light is traveling from
     * @param z Z point light is traveling from
     * @param red Red factor of the light color
     * @param green Green factor of the light color
     * @param blue Blue factor of the light color
     * @param alpha Alpha factor for the light.
     */
    private final void setup(float x, float y, float z, float red, float green, float blue,
            float alpha) {
        setPositionDirection(x, y, z);
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;

    }

    /**
     * Sets the direction to be towards 0, 0, 0 from the current position.
     * Use this method of the position of the light has been updated.
     */
    public void setDirection() {
        setDirection(-color[0], -color[1], -color[2]);
    }

    /**
     * Sets the direction of the light, this will not change the position just direction.
     * Direction will be normalized.
     * @param x X axis direction of light
     * @param y Y axis direction of light
     * @param z Z axis direction of light
     */
    public void setDirection(float x, float y, float z) {
        float len = (float)Math.sqrt(
                (x * x)
                + (y * y)
                + (z * z));

        direction[0] = x / len;
        direction[1] = y / len;
        direction[2] = z / len;

    }

    /**
     * Sets the position and direction of the light.
     * Light is travelling towards 0,0,0 from the light position.
     * @param x X position of light
     * @param y Y position of light
     * @param z Z position of light
     */
    public void setPositionDirection(float x, float y, float z) {
        setDirection(-x, -y, -z);
        position[0] = x;
        position[1] = y;
        position[2] = z;

    }


}
