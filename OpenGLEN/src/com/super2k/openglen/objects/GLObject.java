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

import java.nio.FloatBuffer;

import com.super2k.openglen.geometry.Material;

/**
 * Base GL Object that can be rendered using OpenGL
 * Different GLObject implementations can have different shaders,
 * this is handled by the implementation of the Renderer interface.
 * @author Richard Sahlin
 *
 */
public abstract class GLObject implements PooledObject {

    /**
     * Type of object, user defined.
     */
    public int type;

    /**
     * User defined identifier for the object, this is can be used by clients to identify
     * objects.
     */
    public int userId;

    /**
     * Key of object for pool, if NOT_POOLED_OBJECT then not pooled.
     */
    public int key = NOT_POOLED_OBJECT;

    /**
     * Buffer containing array data - what data is stored here is object specific.
     */
    public FloatBuffer arrayBuffer;

    /**
     * Set if object should use VBO for arrays.
     */
    public int arrayVBOName = -1;

    /**
     * The material definition for this object.
     * This is the lighting and textures appearance.
     * Use the setMaterial method to modify this value.
     */
    public Material material;

    /**
     * Flag to controll if object is rendered or not.
     * Can be used to easily toggle object rendering on or off
     * Implementations of the Renderer interface shall check this flag
     * and only render objects that have it set to true.
     */
    public boolean renderFlag = true;

    /**
     * Sets the shading to be used for this object, note that not all object types may read
     * this value.
     * It is used by GLBlitObject
     * One of
     * SHADING_UNLIT - just texture
     * SHADING_COLORED - texture + color
     * SHADING_LIT - texture + diffuse lighting
     * SHADING_LAMBERT - texture + diffuse lighting and ambient
     * SHADING_PHONG - texture + specular lighting
     * @param shading
     * @throws IllegalArgumentException If shading is not one of the above values.
     */
    public void setShading(int shading) {
        if (shading!=Material.SHADING_UNLIT && shading != Material.SHADING_LAMBERT &&
                shading != Material.SHADING_PHONG&& shading != Material.SHADING_LIT &&
                shading != Material.SHADING_COLORED)
            throw new IllegalArgumentException("Illegal shading value: " + shading);
        material.materialShading = shading;
    }

    /**
     * Return the FloatBuffer containing vertices, normals and texcoords.
     * @return Buffer containing vertices, normals and texcoords.
     */
    public FloatBuffer getArrayBuffer() {
        return arrayBuffer;
    }

    /**
     * Set VBO name for arraybuffer, this buffer contains vertices, normals and texcoords.
     * VBO buffer shall contain same data as arraybuffer (in this class)
     * It is the responsibility of the caller to create and destroy VBO names etc.
     * @param name VBO ID of arraybuffer or -1 if none.
     */
    public void setArrayVBOName(int name) {
        arrayVBOName = name;
    }

    /**
     * Releases all resources for this object.
     * Callers must make sure the object ref is set to null
     * after calling this method.
     */
    public void destroy() {
        material = null;
        arrayBuffer = null;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void releaseObject() {
        renderFlag = false; //Make sure object is not rendered
    }

}
