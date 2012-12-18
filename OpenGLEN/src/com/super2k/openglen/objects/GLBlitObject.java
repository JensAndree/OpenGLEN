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
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.super2k.openglen.animation.Animation3D;
import com.super2k.openglen.geometry.Material;
import com.super2k.openglen.texture.Texture2D;

/**
 * Each GLBlitObject holds the data needed to render a bitmap (image) to screen.
 * @author Richard Sahlin
 *
 */
public class GLBlitObject extends GLObject {

    private final static String ILLEGAL_ANCHOR_VALUE = "Illegal anchor value:";
    private final static String ILLEGAL_VALUE = "Illegal value:";

    public final static float[] FRONT_FACING_NORMALS = new float[] {0f, 0f, 1f};

    /**
     * X anchor will be at left side
     */
    public final static int ANCHOR_LEFT = 1;
    /**
     * X anchor will be center (middle)
     */
    public final static int ANCHOR_CENTER_X = 2;
    /**
     * X anchor will be at right side
     */
    public final static int ANCHOR_RIGHT = 4;
    /**
     * Y anchor will be at top
     */
    public final static int ANCHOR_TOP = 0x10;
    /**
     * Y anchor will be center (middle)
     */
    public final static int ANCHOR_CENTER_Y = 0x20;
    /**
     * Y anchor will be at bottom
     */
    public final static int ANCHOR_BOTTOM = 0x40;

    /**
     * Position on screen - if z is not used set to -1.
     * All z values are positive going into the screen. Highest value is at the back.
     */
    public float[] position = new float[] { 0, 0, 0, 1 };

    /**
     * Scaling of object in x,y and z.
     * X is at index 0.
     */
    public float[] scale = new float[] { 1, 1, 1, 1 };

    /**
     * Rotation of object in x, y and z.
     * X is at index 0.
     */
    public float[] rotation = new float[] { 0, 0, 0, 0 };

    /**
     * Width of blit.
     */
    public float width;
    /**
     * Height of blit.
     */
    public float height;

    public ShortBuffer indices;

    protected int mVertexCount; //Number of vertices
    protected int mIndiceCount; //Number of indices for triangle list.
    private float[] mTemp2Float = new float[2];
    private short[] mTempIndices = new short[6];
    /**
     * Byte stride for vertices, this is used to align array
     * data in memory.
     */
    public int mArrayByteStride;

    public int elementVBOName = -1;

    /**
     * Limit value for u, normally 1
     */
    float mUMax = 1;

    /**
     * Limit value for v, normally 1
     */
    float mVMax = 1;

    /**
     * Linear transform animtion, room for one animation for translate, rotate and scale.
     * Animations are setup with a float[] as target.
     */
    public Animation3D[] anim = new Animation3D[3];

    /**
     * Default constructor
     */
    public GLBlitObject() {
        super();
    }

    /**
     * Create a new GLBlit object with the specified position, bitmap and anchor.
     * Blit object will use the specified width and height on screen.
     * @param x
     * @param y
     * @param z
     * @param width Width of blit
     * @param height Height of blit, if negative it means object will be flipped in Y.
     * @param textures Array containing the texture object to use for this material.
     * Note that texture objects are not copied, instead a reference is kept.
     * @param division Number of squares to subdivide the blit into.
     * A value of 2 will divide x and y into 2 planes - resulting in 4 squares.
     * @param anchor
     */
/*
    public GLBlitObject(float x, float y, float z, float width, float height, Texture2D[] textures,
            int anchor, int division) {
        create(division);

//        set(x, y, z, width, height, anchor, mat)
        set(x, y, z);
    }
*/
    /**
     * Creates a new object with the specified position, size and material.
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     * @param material The material, it is copied into the object
     * @param anchor
     * @param division
     */
    public GLBlitObject(float x, float y, float z, float width, float height, Material material,
            int anchor, int division) {
        create(division, 1, 1);
        set(x, y, z, width, height, anchor, material);
//        init(width, height, material, null, anchor, division);
//        set(x, y, z);
    }

    /**
     * Create a new GLBlit object using the specified position and bitmap. Anchor is left & top.
     * The onscreen size is specified.
     * @param x
     * @param y
     * @param width Width
     * @param height Height
     * @param textures Array containing the texture object to use for this material.
     * Note that texture objects are not copied, instead a reference is kept.
     */
/*
    public GLBlitObject(float x, float y, float width, float height, Texture2D[] textures) {
//        init(width, height, textures, (ANCHOR_LEFT|ANCHOR_TOP), 1);
        set(x, y, 0);

    }
*/
    /**
     * Create a new GLBlit object using the specified position and bitmap.Anchor is left & top.
     * The onscreen size is specified.
     * @param x
     * @param y
     * @param z
     * @param width Width of blit
     * @param height Height of blit
     * @param textures Array containing the texture object to use for this material.
     * Note that texture objects are not copied, instead a reference is kept.
     */
/*
    public GLBlitObject(float x, float y, float z, float width, float height,
            Texture2D[] textures) {
        init(width, height, textures, (ANCHOR_LEFT|ANCHOR_TOP), 1);
        set(x, y, z);

    }
*/
    /**
     * Create a new GLBlit object using the specified position and bitmap.
     * The onscreen size and anchor is specified.
     * The material will get the following properties:
     * Ambient 0, 0, 0, 0
     * Diffuse 1, 1, 1, 1
     * Specular 0.5, 0.5, 0.5, 1
     * power 10
     *
     * @param x
     * @param y
     * @param z
     * @param width Width of blit
     * @param height Height of blit
     * @param textures Array containing the texture object to use for this material.
     * @param anchor The anchor position
     * @param division Number of times to divide the quad in x and y
     * eg ANCHOR_CENTER_Y | ANCHOR_CENTER_X for middle anchor
     * ANCHOR_LEFT | ANCHOR_TOP for upper left anchor.
     * Note that texture objects are not copied, instead a reference is kept.
     */
    public GLBlitObject(float x, float y, float z,
            float width, float height, Texture2D[] textures, int anchor, int division) {
        create(division, 1, 1);
        set(x, y, z, width, height, anchor, null);
        material.texture = textures;

        material.materialShading = Material.SHADING_UNLIT;
        material.setAmbient(0, 0, 0, 0);
        material.setDiffuse(1, 1, 1, 1);
        material.setSpecular(0.5f, 0.5f, 0.5f, 1);
        material.power = 10;


    }

    /**
     * Creates a new GLBlit object using the specified position and bitmap
     * The onscreen size and anchor is specified.
     * @param x
     * @param y
     * @param z
     * @param width Width of blit
     * @param height Height of blit
     * @param textures Array containing the texture object to use for this material.
     * @param anchor The anchor position
     * @param xBlur How much to blur in x, 1 or more.
     * Bigger values will increase blur (read texels further away in x)
     * @param yBlur How much to blur in y, 1 or more.
     * Bigger values will increase blur (read texels further away in y)
     * @param weight How much weight to put on center pixel.
     * Bigger values will dim out effect of surrounding pixels, default is 1.7
     * eg ANCHOR_CENTER_Y | ANCHOR_CENTER_X for middle anchor
     * ANCHOR_LEFT | ANCHOR_TOP for upper left anchor.
     * Note that texture objects are not copied, instead a reference is kept.
     */
/*
    public GLBlitObject(float x, float y, float z, float width, float height,
            Texture2D[] textures, int anchor, float xBlur, float yBlur, float weight) {
        create(1);
        set(x, y, z, width, height, anchor, null);
        material.texture = textures;
        material.xBlurFactor = xBlur;
        material.yBlurFactor = yBlur;
        material.weightFactor = weight;
    }
*/

    /**
     * Initialize buffers to be used and material color to default.
     * @param width Width of blit on screen in pixels (if orthogonal projection)
     * @param height Height of blit on screen in pixels (if orthogonal projection)
     * @param textures Array containing the texture object to use for this material.
     * Note that textures are not copied, a reference to the textures is stored.
     * @param anchor Anchor for the blit.
     * @param division Number of subdivisions for geometry
     * @throws IllegalArgumentException If anchor is not valid,
     * subdivision <= 0 or width is negative.
     */
/*
    public void init(float width, float height, Texture2D[] textures, int anchor,
            int division) {
        init(width, height, null, null, anchor, division);

        material.materialShading = Material.SHADING_UNLIT;
        material.setAmbient(0, 0, 0, 0);
        material.setDiffuse(1, 1, 1, 1);
        material.setSpecular(0.5f, 0.5f, 0.5f, 1);
        material.power = 10;
        material.texture = textures;

    }
*/
    /**
     * Initializes the buffers to be used.
     * @param width Width of blit on screen in pixels (if orthogonal projection)
     * @param height Height of blit on screen in pixels (if orthogonal projection)
     * @param material The material to use, this is copied into this object. If null then
     * a default material is used.
     * @param texture Texture to set in material, copied into material. May be null
     * @param anchor Anchor for the blit.
     * @param division Number of subdivisions for geometry
     * @throws IllegalArgumentException If anchor is not valid,
     * subdivision <= 0 or width is negative.
     */
/*
    public void init(float width, float height, Material material, Texture2D texture, int anchor,
            int subdivision) {

        if ((anchor & (ANCHOR_BOTTOM | ANCHOR_CENTER_X | ANCHOR_CENTER_Y | ANCHOR_LEFT |
                ANCHOR_RIGHT | ANCHOR_TOP )) == 0) {
            throw new IllegalArgumentException(ILLEGAL_ANCHOR_VALUE + anchor);
        }
        if (width <= 0 || height == 0 ||subdivision <= 0) {
            throw new IllegalArgumentException(ILLEGAL_VALUE);
        }

        mVertexCount = subdivision * subdivision * 4; //Total vertexcount for one quad.
        mIndiceCount = (mVertexCount>>>1) * 3; //Each quad is 4 vertices,
                                               //gives number of triangles * 3
        arrayBuffer = ByteBuffer.allocateDirect((mVertexCount * 3 * 4) +
                (mVertexCount * 3 * 4)+
                (mVertexCount * 2 * 4)).order(ByteOrder.nativeOrder()).asFloatBuffer();
        indices = ByteBuffer.allocateDirect(mIndiceCount*2).order(ByteOrder.nativeOrder()).
                asShortBuffer();

        setupIndices(subdivision);
        setupNormals(subdivision, arrayBuffer, 3, 8);
        setupTexCoords(subdivision, arrayBuffer, 6, 8);

        this.width = width;
        this.height = Math.abs(height);

        setVertices(width, height, anchor, subdivision, arrayBuffer, 0, 8);
        mArrayByteStride = 8*4; //Byte stride for array.
        if (material != null) {
            setMaterial(material);
        }
        if (texture != null) {
            if (this.material.texture != null && this.material.texture.length > 0) {
                throw new IllegalArgumentException("Not implemented");
            }
            this.material.texture = new Texture2D[] {texture};
        }

    }
*/
    /**
     * Creates material, arraybuffers and index buffer storage,
     * setups indices, normals and texture coordinates
     * @param division
     * @param xRepeat Number of times texture will repeat in x (this is the u max value)
     * @param yRepeat Number of times texture will repeat in y (this is the v max value)
     */
    public void create(int division, float xRepeat, float yRepeat) {

        material = new Material();
        mVertexCount = division * division * 4; //Total vertexcount for one quad.
        mIndiceCount = (mVertexCount>>>1) * 3; //Each quad is 4 vertices,
                                               //gives number of triangles * 3
        arrayBuffer = ByteBuffer.allocateDirect((mVertexCount * 3 * 4) +
                (mVertexCount * 3 * 4)+
                (mVertexCount * 2 * 4)).order(ByteOrder.nativeOrder()).asFloatBuffer();
        indices = ByteBuffer.allocateDirect(mIndiceCount * 2).order(ByteOrder.nativeOrder()).
                asShortBuffer();

        setupIndices(division);
        setupNormals(division, arrayBuffer, 3, 8);
        setupTexCoords(division, xRepeat, yRepeat, arrayBuffer, 6, 8);
    }

    /**
     * Setup triangle indexes for a Quad.
     * @param subdivision Number of times the quad should be subdivided in x and y
     */
    protected void setupIndices(int subdivision) {

        short index = 0; //Count up after each quad.

        indices.rewind();
        for (int y = 0; y < subdivision; y++) {

            for (int x = 0; x < subdivision; x++) {
                mTempIndices[0] = index;
                mTempIndices[1] = (short)(index+3);
                mTempIndices[2] = (short)(index+1);
                mTempIndices[3] = (short)(index+0);
                mTempIndices[4] = (short)(index+2);
                mTempIndices[5] = (short)(index+3);
                indices.put(mTempIndices);
                index += 4;
            }
        }

    }

    /**
     * Setup normals for frontfacing ortho blit, only needed for
     * objects that will be lit
     * The stride value will be set in this object to be used when rendering.
     * @subdivision Number of times the quad is split (in x and y)
     * @param normals Normals are put here.
     * @param Offset into normal array.
     * @param stride Number of items between each set of normal coordinates,
     * 0 for packed array. 3 for an array with 3 float positions inbetween each set of normals.
     */
    protected void setupNormals(int subdivision, FloatBuffer normals, int offset, int stride) {

        int size = 4 * subdivision * subdivision;
        for (int i = 0; i < size; i++) {
            normals.position(offset);
            normals.put(FRONT_FACING_NORMALS);
            offset += stride;
        }

    }

    /**
     * Setup texture coordinates for a normal frontfacing ortho blit.
     * The stride value will be set in this object to be used when rendering.
     * @param texCoordinates The FloatBuffer where coordinates are stored.
     * @param division Number of times each quad is split (in x and y)
     * @param uMax max X value for coordinates, the range is 0 - uMax
     * @param vMax max Y value for coordinates, the range is 0 - vMax
     * @param offset Offset into texCoordinate array
     * @param stride Number of items between each set of normal coordinates,
     * 0 for packed array. 3 for an array with 3 float positions inbetween each set of normals.
     */
    protected void setupTexCoords(int division, float uMax, float vMax,
            FloatBuffer texCoordinates, int offset, int stride) {

        float xpos = 0;
        float ypos = vMax;
        float ysub = vMax / division;
        float xadd = uMax / division;
        mUMax = uMax;
        mVMax = vMax;
        for (int y = 0; y < division; y++) {
            for (int x = 0; x < division; x++) {
                mTemp2Float[0] = xpos;
                mTemp2Float[1] = ypos;
                texCoordinates.position(offset);
                texCoordinates.put(mTemp2Float);
                offset += stride;

                mTemp2Float[0] = xpos;
                mTemp2Float[1] = ypos - ysub;
                texCoordinates.position(offset);
                texCoordinates.put(mTemp2Float);
                offset += stride;

                mTemp2Float[0] = xpos + xadd;
                mTemp2Float[1] = ypos;
                texCoordinates.position(offset);
                texCoordinates.put(mTemp2Float);
                offset += stride;

                mTemp2Float[0] = xpos + xadd;
                mTemp2Float[1] = ypos - ysub;
                texCoordinates.position(offset);
                texCoordinates.put(mTemp2Float);
                offset += stride;

                xpos += xadd;
            }
            xpos = 0;
            ypos -= ysub;
        }

    }

    /**
     * Sets the position in world coordinates.
     * Where this object will be shown depends on viewport, transform matrix and projection.
     * @param x World x coordinate
     * @param y World y coordinate
     * @param z Workd z coordinate.
     */
    public void set(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;

    }

    /**
     * Sets the x, y and z position. The width and height of the blit and the material to
     * use.
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     * @param anchor
     * @param mat The material Or null to not set a new material.
     */
    public void set(float x, float y, float z, float width, float height, int anchor,
            Material mat) {
        this.width = width;
        this.height = Math.abs(height);

        setVertices(width, height, anchor, (int) Math.sqrt(mVertexCount>>>2), arrayBuffer, 0, 8);
        mArrayByteStride = 8 * 4; //Byte stride for array.
        if (mat != null) {
            setMaterial(mat);
        }
        set(x, y, z);
        renderFlag = true;
    }

    /**
    /**
     * Sets the x, y and z position. The width and height of the blit and the material to
     * use.
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     * @param anchor
     * @param mat
     * @param texture
     * @throws IllegalArgumentException If texture is null
     */
    public void set(float x, float y, float z, float width, float height, int anchor,
            Material mat, Texture2D texture) {
        if (texture == null) {
            throw new IllegalArgumentException("Texture is null.");
        }
        set(x, y, z, width, height, anchor, mat);
        material.texture = new Texture2D[] {texture};
    }

    /**
     * Sets the material. The material is copied into this object.
     * @param material Material to set.
     */
    public void setMaterial(Material material) {
        this.material.set(material);
    }

    /**
     * Set the specular power, this controlls the 'shininess' of phong shaded objects.
     * Higher values for a more defined smaller highlight.
     * @param specularPower
     */
    public void setSpecularPower(float specularPower) {
        material.power = specularPower;
    }

    /**
     * Set the vertex positions (for GLES), vertex can be interleaved or packed in the array.
     * The stride value will be set in this object to be used when rendering.
     * @param width Width of blit
     * @param height Height of blit
     * @param anchor Anchor value
     * @param division Number of subdivisions for the blit object.
     * @param vertices FloatBuffer where vertices are put.
     * @param offset Offset into FloatBuffer where vertices start.
     * @param stride Number of positions between each set of vertice data.
     * Set to 0 for packed array, 3 for an array with 3 float values inbetween each set of vertices.
     */
    public void setVertices(float width, float height, int anchor, int division,
            FloatBuffer vertices, int offset, int stride) {

        float xpos = 0;
        float ypos = height; //Texture (t) coordinates go from 1 to 0 on y axis
        if ((anchor & ANCHOR_CENTER_X)!= 0) {
            xpos = -(width / 2);
        }
        else if ((anchor & ANCHOR_RIGHT)!= 0) {
            xpos = -width;
        }

        if ((anchor & ANCHOR_CENTER_Y)!= 0) {
            ypos = (height / 2);
        }
        else if ((anchor & ANCHOR_BOTTOM)!= 0) {
            ypos = 0;
        }

        float xAdd = width/division;
        float ySub = height/division;
        float xCopy = xpos;
        for (int y = 0; y < division; y++) {
            for (int x = 0; x < division; x++) {
                vertices.position(offset);
                offset += stride;
                vertices.put(xpos);
                vertices.put(ypos);
                vertices.put(0);

                vertices.position(offset);
                offset += stride;
                vertices.put(xpos);
                vertices.put(ypos-ySub);
                vertices.put(0);

                vertices.position(offset);
                offset += stride;
                vertices.put(xpos+xAdd);
                vertices.put(ypos);
                vertices.put(0);

                vertices.position(offset);
                offset += stride;
                vertices.put(xpos+xAdd);
                vertices.put(ypos-ySub);
                vertices.put(0);

                xpos += xAdd;
            }
            xpos = xCopy;
            ypos -= ySub;
        }

    }

    /**
     * Return the number of vertices.
     * @return
     */
    public int getVertexCount() {
        return mVertexCount;
    }

    /**
     * Return the number of triangle indexes, used when outputting to GL as trianglelist.
     * @return
     */
    public int getIndexCount() {
        return mIndiceCount;
    }

    /**
     * Return buffer containing element indices (triangle list).
     * @return Buffer containing triangle indexes.
     */
    public ShortBuffer getElementBuffer() {
        return indices;
    }

    /**
     * Set VBO name for elementbuffer, this buffer contains the triangle indexes.
     * VBO buffer shall contain the same data as the indices buffer (in this class)
     * @param name
     */
    public void setElementVBOName(int name) {
        elementVBOName = name;
    }

    /**
     * Release all objects/buffers/names attached to this object.
     */
    @Override
    public void destroy() {
        super.destroy();
        scale = null;
        rotation = null;
        position = null;
        arrayBuffer = null;
        indices = null;
        anim = null;
    }

    /**
     * Returns the U max (destination) value
     * @return
     */
    public float getU() {
        return mUMax;
    }

    /**
     * Returns the U max (destination) value
     * @return
     */
    public float getV() {
        return mVMax;
    }

    @Override
    public void releaseObject() {
        rotation[0] = 0;
        rotation[1] = 0;
        rotation[2] = 0;
        scale[0] = 1;
        scale[1] = 1;
        scale[2] = 1;
    }

    @Override
    public void createObject(Object obj) {
        // TODO Auto-generated method stub
    }

    @Override
    public void destroyObject(Object obj) {
        destroy();
    }

}
