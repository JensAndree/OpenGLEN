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
 * Abstract base class for light definition.
 * @author Richard Sahlin
 *
 */
public abstract class Light {

    /**
     * Denotes a directional light, has position, color and direction.
     * Light is considered to be a single ray of light.
     */
    public final static int DIRECTIONAL = 0x0a000;
    /**
     * Point light that radiates equally in all directions.
     * Can have light fallof.
     */
    public final static int POINT = 0x0a001;
    /**
     * Spot (directed) light
     */
    public final static int SPOT = 0x0a002;

    /**
     * Color of the light - RGBA, Red at index 0.
     */
    public float[]    color = new float[4];

    /**
     * Position of light
     */
    public float[]     position = new float[4];

    /**
     * Type of light.
     */
    public int type = DIRECTIONAL;

    /**
     * Sets the light color RGBA.
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public void setColor(float r, float g, float b, float a)   {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    /**
     * Sets the light color RGB.
     * @param r
     * @param g
     * @param b
     */
    public void setColor(float r, float g, float b)   {
        color[0] = r;
        color[1] = g;
        color[2] = b;
    }

    /**
     * Sets the position of the light.
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

}
