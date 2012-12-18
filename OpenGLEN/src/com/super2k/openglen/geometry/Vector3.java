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

package com.super2k.openglen.geometry;

import com.super2k.openglen.ConstantValues;

/**
 * A float Vector with x y z stored in an array.
 *
 * @author Richard Sahlin
 */
public class Vector3 {
    /**
     * The float Vector values.
     */
    public float[] values = new float[3];

    /**
     * Default constructor.
     */
    public Vector3() {
    }

    /**
     * Negate the values and return.
     * @return
     */
    public Vector3 negate() {
        values[0] = -values[0];
        values[1] = -values[1];
        values[2] = -values[2];
        return this;
    }

    /**
     * Constructor with 3 values for x, y and z.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3(float x, float y, float z) {
        set(x, y, z);
    }

    /**
     * Set this Vector to be the halfwayvector of this + other Vector, result is normalized.
     * @param other
     */
    public void halfway(Vector3 other)  {
        add(other);
        values[0] = values[0]/2;
        values[1] = values[1]/2;
        values[2] = values[2]/2;
        this.normalize();
    }

    /**
     * Construct a Vector from the specified origin and endpoint.
     * @param origin
     * @param endpoint
     */
    public Vector3(float[] origin, float[] endpoint) {
        set(origin, endpoint);
    }

    /**
     * Constructor with array of float as parameter.
     * @param values
     * @throws IllegalArgumentException If values is null or
     * there is not enough values in the values array, must be at least 3
     */
    public Vector3(float[] values) {
        set(values, 0);
    }

    /**
     * Constructor with array and index as parameter, copy the values from the
     * specified index to a Vector3.
     * @param values Array containing values.
     * @param index Index into the values array where values are read.
     * @throws IllegalArgumentException If values is null or
     * there is not enough values in the values array, must be at least index + 3
     */
    public Vector3(float[] values, int index) {
        set(values, index);

    }

    /**
     * Constructor with Vector3 as source.
     * @param source
     * @throws IllegalArgumentException If source is null
     */
    public Vector3(Vector3 source) {
        if (source == null) {
            throw new IllegalArgumentException("Source vector is null");
        }
        set(source);
    }

    /**
     * Copy the values from the specified float array.
     * @param setValues
     * @param index
     * @throws IllegalArgumentException If values is null or does not contain index +
     * 3 values.
     */
    public void set(float[] setValues, int index) {
        if (setValues == null || setValues.length < index + 3) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        System.arraycopy(setValues, index, values, 0, 3);
    }

    /**
     * Copy the contents of the source Vector into this Vector.
     * @param source
     * @throws IllegalArgumentException If source is null.
     */
    public Vector3 set(Vector3 source) {
        if (source == null) {
            throw new IllegalArgumentException("Source vector is null");
        }
        System.arraycopy(source.values, 0, values, 0, 3);
        return this;
    }

    /**
     * Set this Vector to be the distance from origin to endpoint, origin and
     * enpoint must have (at least) 3 values each.
     * @param origin
     * @param endpoint
     * @throws IllegalArgumentException if origin or endpoint is null
     */
    public void set(float[] origin, float[] endpoint) {
        if (origin == null || endpoint == null) {
            throw new IllegalArgumentException("Invalid parameter - null");
        }
        values[0] = endpoint[0] - origin[0];
        values[1] = endpoint[1] - origin[1];
        values[2] = endpoint[2] - origin[2];
    }

    /**
     * Set the values of this Vector
     *
     * @param x
     * @param y
     * @param z
     */
    public void set(float x, float y, float z) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
    }

    /**
     * Set Vector to 0
     */
    public void clear() {
        values[ConstantValues.X_AXIS_INDEX] = 0;
        values[ConstantValues.Y_AXIS_INDEX] = 0;
        values[ConstantValues.Z_AXIS_INDEX] = 0;
    }

    /**
     * Normalize to unit length |a| = sqrt( (x*x) + (y*y) + (z*z) ) x = ax / |a|
     * y = ay / |a| z = az / |a|
     */
    public Vector3 normalize() {
        float len = (float)Math
                .sqrt((values[ConstantValues.X_AXIS_INDEX] * values[ConstantValues.X_AXIS_INDEX])
                        + (values[ConstantValues.Y_AXIS_INDEX] * values[ConstantValues.Y_AXIS_INDEX])
                        + (values[ConstantValues.Z_AXIS_INDEX] * values[ConstantValues.Z_AXIS_INDEX]));
        values[ConstantValues.X_AXIS_INDEX] = values[ConstantValues.X_AXIS_INDEX] / len;
        values[ConstantValues.Y_AXIS_INDEX] = values[ConstantValues.Y_AXIS_INDEX] / len;
        values[ConstantValues.Z_AXIS_INDEX] = values[ConstantValues.Z_AXIS_INDEX] / len;

        return this;
    }

    /**
     * Calculate the length of the Vector. To get the normalized unit length
     * call normalize()
     * @return The length of the vector.
     */
    public float length() {
        return (float)Math
                .sqrt((values[ConstantValues.X_AXIS_INDEX] * values[ConstantValues.X_AXIS_INDEX])
                        + (values[ConstantValues.Y_AXIS_INDEX] * values[ConstantValues.Y_AXIS_INDEX])
                        + (values[ConstantValues.Z_AXIS_INDEX] * values[ConstantValues.Z_AXIS_INDEX]));

    }

    /**
     * Calculate the dot product between this Vertex and an array with 3 float
     * values (x,y,z). The angle between the directions of the two vectors. If
     * the angle is greater than 90 between the two vectors the dot product will
     * be negative (facing away)
     * @param vector2
     * @return
     */
    public float dot(float[] vertex2) {
        return dot(vertex2, 0);
    }

    /**
     * Calculate the dot product between this Vertex and 3 float values in the
     * array (x,y,z). The angle between the directions of the two vectors. If
     * the angle is greater than 90 between the two vectors the dot product will
     * be negative (facing away)
     * @param vertex2 Normalized vector.
     * @param index
     * @return The dot product
     */
    public float dot(float[] vertex2, int index) {
        // Calculate the dot product between this vector and vector2
        // Remember that this solution only works for normalized vectors
        // Otherwise ( u * v) / (|u| * |v|) should be used.
        // (the dot product divided by the sum of the magnitudes)

        return values[ConstantValues.X_AXIS_INDEX] * vertex2[index++]
                + values[ConstantValues.Y_AXIS_INDEX] * vertex2[index++]
                + values[ConstantValues.Z_AXIS_INDEX] * vertex2[index++];
    }


    /**
     * Calculate the cross product (direction of the plane defined by this
     * vector and v2). The result will be the surface 'normal' (not normalized),
     * stored in this vector.
     * @param values2
     */
    public void cross(float[] values2) {

        // Calculate the cross product of this and values2.
        float tmpX = values[1] * values2[2] - values[2] * values2[1];
        float tmpY = values[2] * values2[0] - values[0] * values2[2];
        float tmpZ = values[0] * values2[1] - values[1] * values2[0];

        values[0] = tmpX;
        values[1] = tmpY;
        values[2] = tmpZ;
    }

    /**
     * Create the cross product based on the plane defined by
     * (endpoint1-origin), (endpoint2-origin)
     * @param origin
     * @param endpoint1
     * @param endpoint2
     */
    public void cross(float[] origin, float[] endpoint1, float[] endpoint2) {
        values[0] = (endpoint1[1] - origin[1]) * (endpoint2[2] - origin[2])
                - (endpoint1[2] - origin[2]) * (endpoint2[1] - origin[1]);
        values[1] = (endpoint1[2] - origin[2]) * (endpoint2[0] - origin[0])
                - (endpoint1[0] - origin[0]) * (endpoint2[2] - origin[2]);
        values[2] = (endpoint1[0] - origin[0]) * (endpoint2[1] - origin[1])
                - (endpoint1[1] - origin[1]) * (endpoint2[0] - origin[0]);
    }

    /**
     * Add the specified Vector to this Vector.
     *
     * @param add The Vector to add.
     */
    public Vector3 add(Vector3 add) {
        values[0] += add.values[0];
        values[1] += add.values[1];
        values[2] += add.values[2];
        return this;
    }

    /**
     * Add the specified values to this Vector.
     * @param x The x value to add.
     * @param y The y value to add
     * @param z The z value to add
     */
    public void add(float x, float y, float z) {
        values[0] += x;
        values[1] += y;
        values[2] += z;
    }

    /**
     * Adds data from an array to this vector.
     * @param data
     * @param index
     * @throws IllegalArgumentException If data is null or does not contain index + 3 values.
     */
    public void add(float[] data, int index) {
        if (data == null || data.length < index + 3) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        values[0] += data[index++];
        values[1] += data[index++];
        values[2] += data[index++];
    }

    /**
     * Subtract the specified Vector from this Vector.
     *
     * @param sub The Vector to subtract.
     */
    public void sub(Vector3 sub) {
        values[0] -= sub.values[0];
        values[1] -= sub.values[1];
        values[2] -= sub.values[2];
    }

    /**
     * Subtract the specified values from this Vector.
     * @param x The x value to subtract.
     * @param y The y value to subtract
     * @param z The z value to subtract
     */
    public void sub(float x, float y, float z) {
        values[0] -= x;
        values[1] -= y;
        values[2] -= z;
    }

    /**
     * Subtract the specified values from this Vector.
     * @param data The values to subtract
     */
    public void sub(float[] data, int index) {
        values[0] -= data[index++];
        values[1] -= data[index++];
        values[2] -= data[index++];
    }

    /**
     * Multiply this Vector with a scalar.
     * @param scalar The scalar to multiply with
     */
    public void mult(float scalar) {
        values[0] *= scalar;
        values[1] *= scalar;
        values[2] *= scalar;
    }

    /**
     * Multiply this Vector with another vector.
     * The elements are multiplied
     * element by element.
     * @param mul Array to multiply vector with, 3 values.
     */
    public void mult(float[] mul) {
        values[0] *= mul[0];
        values[1] *= mul[1];
        values[2] *= mul[2];
    }

    /**
     * Rotate the Vector on the x axis.
     * @param angle
     * @param destination The destination vector
     */
    public void rotateXAxis(float angle, float[] destination) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float tempY = (values[1] * cos) - (values[2] * sin);
        destination[2] = (values[1] * sin) + (values[2] * cos);
        destination[1] = tempY;
    }

    /**
     * Rotate the Vector on the y axis.
     * @param angle
     * @param destination The destination vector
     */
    public void rotateYAxis(float angle, float[] destination) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float tempZ = (values[2] * cos) - (values[0] * sin);
        destination[0] = (values[2] * sin) + (values[0] * cos);
        destination[2] = tempZ;
    }

    /**
     * Rotates a Vector on the z axis.
     * @param angle
     * @param vector
     */
    public static void rotateZAxis(float[] vector, float angle) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float tempX = (vector[0] * cos) - (vector[1] * sin);
        vector[1] = (vector[0] * sin) + (vector[1] * cos);
        vector[0] = tempX;


    }

    /**
     * Rotate the Vector on the z axis.
     * @param angle
     * @param destination The destination vector
     */
    public void rotateZAxis(float angle, float[] destination) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        float tempX = (values[0] * cos) - (values[1] * sin);
        destination[1] = (values[0] * sin) + (values[1] * cos);
        destination[0] = tempX;
    }

    /**
     * Scale the vector by the specified factor.
     * @param factor
     */
    public void scale(float factor) {
        values[0] = values[0] * factor;
        values[1] = values[1] * factor;
        values[2] = values[2] * factor;
    }

}
