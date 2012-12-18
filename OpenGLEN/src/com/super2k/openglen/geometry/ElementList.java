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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This class defines the data needed to draw elements (triangles, fans, lines
 * etc)
 *
 * @author Richard Sahlin
 */

public class ElementList  {

    /**
     * The colourmode, this defines how colours are set for the list.
     */
    public byte colourMode;

    /**
     * Indexbuffer with indices for the specified arrays. If not specified then
     * drawArrays will be used. This is used for drawElements
     */
    public ShortBuffer indexBuffer = null;

    /**
     * The material for the elements in this list., this is used for ambient,
     * diffuse and specular colour. If the colourmode is COLOURMODE_POLYLIST
     * this material colour is used.
     */
    public Material material;

    /**
     * Number of elements to render for this trianglelist-must not exceed the
     * buffer.
     */
    public int count;

    /**
     * Drawmode for the list. TRIANGLES, TRIANGLE_FAN etc.
     */
    public int mode;

    /**
     * Runtime variables, not exported
     */

    public int offset; // Buffer offset, used with vbos

    /**
     * Empty constructor, needed by Externalizable interface. Do not use this
     * constructor!
     */
    public ElementList() {
    }

    /**
     * Create a new TriangleList with the specified number of indices.
     *
     * @param count Number of elements to draw (and size of indexbuffer)
     * @param mode The drawmode
     */
    public ElementList(int count, int mode) {

        this.mode = mode;
        this.count = count;
        if (count > 0)
            indexBuffer = ByteBuffer.allocateDirect(count * 2).order(ByteOrder.nativeOrder())
            .asShortBuffer();
        material = new Material();
    }

    /**
     * Copy the contents of the source to the destination. Source and
     * destination MUST have equal size.
     *
     * @param source
     * @param destination
     */
    private void copy(ShortBuffer source, ShortBuffer destination) {
        if (source.hasArray())
            destination.put(source.array());
        else {
            int loop = source.capacity();
            while (loop > 0) {
                destination.put(source.get());
            }
        }

    }

    /**
     * Constructor that copies the source to this class. This class will be a
     * new instance (no references)
     *
     * @param source
     */
    public ElementList(ElementList source) {

        colourMode = source.colourMode;
        mode = source.mode;
        material = new Material(source.material);

        if (source.indexBuffer != null) {
            indexBuffer = ByteBuffer.allocateDirect(source.indexBuffer.capacity() * 2).order(
                    ByteOrder.nativeOrder()).asShortBuffer();
            source.indexBuffer.rewind();
            copy(source.indexBuffer, indexBuffer);

        }

    }

    /**
     * Create a new ElementList based on the specified indexBuffer and mode
     *
     * @param indexBuffer
     */
    public ElementList(ShortBuffer indexBuffer, int mode) {
        this.indexBuffer = indexBuffer;
        this.mode = mode;
        this.count = indexBuffer.capacity();
    }


}
