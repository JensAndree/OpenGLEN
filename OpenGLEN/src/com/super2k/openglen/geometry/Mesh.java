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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.super2k.openglen.ConstantValues;

/**
 * Class for mesh definition,
 *
 * @author Richard Sahlin
 */
public class Mesh  {

    /**
     * All vertices for this mesh.
     */
    public Buffer vertices;

    /**
     * UV texture coordinates.
     */
    public Buffer textureCoordinates;

    /**
     * Per vertex normals.
     */
    public Buffer normals;

    /**
     * Element list - must at least contain one elementlist (even if drawArrays
     * is to be used, mode and material is taken from elementlist)
     */
    public ElementList[] elementList;

    public int vertexType = -1;

    public int normalType = -1;

    public int texCoordType = -1;

    /**
     * Runtime variables - not exported.
     */
    private int arrayBufferID = -1; // Buffer id, -1 not used.

    private int elementBufferID = -1; // Buffer id, -1 not used.

    public int normalOffset;

    public int texCoordOffset;

    /**
     * Default constructor The buffers must be created before using the Mesh
     * class.
     */
    public Mesh() {
        super();
    }

    /**
     * Create a mesh that can be rendered (using drawArrays), note that in order
     * for drawArrays to use the buffers SHALL have the same size.
     * It is OK to specify 0 for texCoordCount, if so then texturing for this mesh will be
     * disabled. vertexCount number of elements are drawn during rendering.
     *
     * @param vertexCount Number of vertices
     * @param normalCount Number of normals - may not be less than vertexCount,
     *            unless it is 0
     * @param texCoordCount
     * @param mode How arrays are drawn, eg ConstantValues.LINES,
     *            ConstantValues.TRIANGLES etc.
     * @param type Type of buffers, ConstantValues.FLOAT, ConstantValues.SHORT
     *            etc.
     * @throws IllegalArgumentException if normalCount is less than vertexCount.
     *             If texCoordCount > 0 & < vertexCount/3*2
     */
    public Mesh(int vertexCount, int normalCount, int texCoordCount, int mode, int type) {
        if (normalCount < vertexCount && normalCount > 0)
            throw new IllegalArgumentException();
        if ((texCoordCount > 0) && (texCoordCount < (vertexCount / 3 * 2)))
            throw new IllegalArgumentException();

        setupArrays(vertexCount, normalCount, texCoordCount, type);

        elementList = new ElementList[1];
        elementList[0] = new ElementList((short)0, mode);

    }

    /**
     * Allocate the arrays for the correct type of storage, using this method
     * all arrays are same type.
     *
     * @param vertexCount
     * @param normalCount
     * @param texCoordCount
     * @param type
     */
    private void setupArrays(int vertexCount, int normalCount, int texCoordCount, int type) {

        if (type == ConstantValues.FLOAT) {
            vertices = ByteBuffer.allocateDirect(vertexCount * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer();

            if (normalCount > 0)
                normals = ByteBuffer.allocateDirect(normalCount * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            if (texCoordCount > 0)
                textureCoordinates = ByteBuffer.allocateDirect(texCoordCount * 4).order(
                        ByteOrder.nativeOrder()).asFloatBuffer();

        }

        vertexType = type;
        normalType = type;
        texCoordType = type;

    }

    /**
     * Create vertex array and store with data. The result array type depends on
     * the type parameter, possible values are: ConstantValues.FLOAT,
     * ConstantValues.FIXED, ConstantValues.BYTE and ConstantValues.SHORT The
     * caller shall make sure that there the right number of vertices, normals
     * and texturecoordinates are present.
     *
     * @param sourceVertices
     * @param index Index into the vertices array where reading starts.
     * @param count Number of values to store
     * @param type Type of array for the vertices. ConstantValues.FLOAT,
     *            ConstantValues.FIXED, ConstantValues.BYTE or
     *            ConstantValues.SHORT
     */
    public void createVertexArray(float[] sourceVertices, int index, int count, int type) {
        switch (type) {
            case ConstantValues.FLOAT:
                vertices = ByteBuffer.allocateDirect(count * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
                ((FloatBuffer)vertices).put(sourceVertices, index, count);
                vertexType = type;
                break;

        }

    }

    /**
     * Create normal array and store with data. The result array type depends on
     * the type parameter, possible values are: ConstantValues.FLOAT,
     * ConstantValues.FIXED, ConstantValues.BYTE and ConstantValues.SHORT The
     * caller shall make sure that there the right number of vertices, normals
     * and texturecoordinates are present.
     *
     * @param sourceNormals
     * @param index Index into the normals array where reading starts.
     * @param count Number of values to store
     * @param type Type of array for the normals. ConstantValues.FLOAT,
     *            ConstantValues.FIXED, ConstantValues.BYTE or
     *            ConstantValues.SHORT
     */
    public void createNormalArray(float[] sourceNormals, int index, int count, int type) {
        switch (type) {
            case ConstantValues.FLOAT:
                normals = ByteBuffer.allocateDirect(count * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
                ((FloatBuffer)normals).put(sourceNormals, index, count);
                normalType = type;
                break;

        }

    }

    /**
     * Create texturecoordinate array and store with data. The result array type
     * depends on the type parameter, possible values are: ConstantValues.FLOAT,
     * ConstantValues.FIXED, ConstantValues.BYTE and ConstantValues.SHORT The
     * caller shall make sure that there the right number of vertices, normals
     * and texturecoordinates are present.
     *
     * @param sourceTexCoord
     * @param index Index into the texturecoordinate array where reading starts.
     * @param count Number of values to store
     * @param type Type of array for the texturecoordinates.
     *            ConstantValues.FLOAT, ConstantValues.FIXED,
     *            ConstantValues.BYTE or ConstantValues.SHORT
     */
    public void createTexCoordArray(float[] sourceTexCoord, int index, int count, int type) {
        switch (type) {
            case ConstantValues.FLOAT:
                textureCoordinates = ByteBuffer.allocateDirect(count * 4).order(
                        ByteOrder.nativeOrder()).asFloatBuffer();
                ((FloatBuffer)textureCoordinates).put(sourceTexCoord, index, count);
                texCoordType = type;
                break;

        }

    }

    /**
     * Set the buffer ids for arrays and elements, if indices are not used
     * elementBufferID should be -1 -1 means buffer not used, renderer will use
     * call to set pointer instead. User needs to call the apropriate methods to
     * glBufferData, glBufferSubData to set the data.
     *
     * @param arrayBufferID The ID of the array buffer.
     * @param elementBufferID ID of the element buffer (if used)
     */
    public void setBufferID(int arrayBufferID, int elementBufferID) {
        this.arrayBufferID = arrayBufferID;
        this.elementBufferID = elementBufferID;
    }

    /**
     * Fetch the array bufferID for this mesh, -1 if vbos not used.
     *
     * @return
     */
    public int getArrayBufferID() {
        return arrayBufferID;
    }

    /**
     * Return the elementbuffer ID for this mesh, -1 means elements not used (in
     * which case ElementList in mesh must NOT have indexBuffer)
     *
     * @return Element buffer ID or -1 if not used.
     */
    public int getElementBufferID() {
        return elementBufferID;
    }




}
