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

package com.super2k.openglen;

import java.util.StringTokenizer;

import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Log;

/**
 * Holds data for the Renderer capabilities. This is for instance extensions available
 * and max values for implementation variables.
 * @author Richard Sahlin
 *
 */
public class RendererInfo {

    private final String TAG = getClass().getSimpleName();
    private final static String ARGUMENT_NULL_STR = "Argument is NULL";
    protected String mExtensions = ""; //A whitespace delimetered string with available extensions.

    protected String mVendor;    //Vendor name
    protected String mRenderer;  //Name of underlying renderer, normally the HW name.
    protected String mVersion;   //Version of the underlying renderer.

    protected int mCombinedTextures; //combined texture unit count.
    protected int mFragmentTextures; //fragment shader texture units
    protected int mVertexTextures;  //vertex shader texture units.

    protected int mMaxFragUniformVectors;
    protected int mMaxVertUniformVectors;
    protected int mMaxRenderbufferSize;
    protected int mMaxTextureSize;
    protected int mMaxVaryingVectors;
    protected int mMaxVertexAttribs;


    /**
     * Constructs a new RendererInfo reading info from the specific graphics library handler.
     * @param glHandler
     */
    public RendererInfo(GraphicsLibraryHandler glHandler) {
        try {
            mRenderer = glHandler.getString(ConstantValues.RENDERER);
            mVersion = glHandler.getString(ConstantValues.VERSION);
            mVendor = glHandler.getString(ConstantValues.VENDOR);
            mExtensions = glHandler.getString(ConstantValues.EXTENSIONS);

            mCombinedTextures = getInteger(glHandler, ConstantValues.MAX_COMBINED_TEXTURE_IMAGE_UNITS);
            mFragmentTextures = getInteger(glHandler, ConstantValues.MAX_TEXTURE_IMAGE_UNITS);
            mVertexTextures = getInteger(glHandler, ConstantValues.MAX_VERTEX_TEXTURE_IMAGE_UNITS);
            mMaxFragUniformVectors = getInteger(glHandler, ConstantValues.MAX_FRAGMENT_UNIFORM_VECTORS);
            mMaxVertUniformVectors = getInteger(glHandler, ConstantValues.MAX_VERTEX_UNIFORM_VECTORS);
            mMaxRenderbufferSize = getInteger(glHandler, ConstantValues.MAX_RENDERBUFFER_SIZE);
            mMaxTextureSize = getInteger(glHandler, ConstantValues.MAX_TEXTURE_SIZE);
            mMaxVaryingVectors = getInteger(glHandler, ConstantValues.MAX_VARYING_VECTORS);
            mMaxVertexAttribs = getInteger(glHandler, ConstantValues.MAX_VERTEX_ATTRIBS);

        }
        catch (IllegalArgumentException iae) {
            //Could not get some values - doesn't matter.
            Log.d(TAG, "Could not get string: " + iae.toString());
        }
    }

    /**
     * Default constructor, with vendor name, renderer, version and extensions.
     * @param vendor The name of the underlying GL vendor.
     * @param renderer The name of the underlying GL renderer, normally HW name.
     * @param version Version of the underlying GL renderer.
     * @param extensions Supported extensions, whitespace delimitered list.
     * @throws IllegalArgumentException If any of the parameters are null.
     */
    public RendererInfo(String vendor, String renderer, String version, String extensions) {
        if (vendor == null || renderer == null || version == null || extensions == null) {
            throw new IllegalArgumentException(ARGUMENT_NULL_STR);
        }
        mVendor = vendor;
        mRenderer = renderer;
        mVersion = version;
        mExtensions = extensions;
    }

    /**
     * Calls the getInteger method in the glHandler and returns one int (the first)
     * @param glHandler
     * @param pname
     * @return One int value from the specified pname value.
     * @throws IllegalArgumentException If pname is not valid.
     */
    private int getInteger(GraphicsLibraryHandler glHandler, int pname) {
        int[] res = glHandler.getInteger(pname);
        return res[0];
    }

    /**
     * Returns the vendor name of the underlying GL renderer.
     * @return Vendor name.
     */
    public String getVendor() {
        return mVendor;
    }



    /**
     * Returns the renderer name of the underlying GL renderer, normally the HW name.
     * @return Renderer name.
     */
    public String getRenderer() {
        return mRenderer;
    }

    /**
     * Returns the version of the underlying GL renderer.
     * @return Renderer version.
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * Returns the supported extensions of the underlying renderer.
     * @return Supported extensions, whitespace delimitered.
     */
    public String getExtensions() {
        return mExtensions;
    }

    /**
     * Checks if the specified extension is available on this platform.
     * @param extension
     * @return True if the extension is available otherwise false.
     */
    public boolean hasExtension(String extension) {
        StringTokenizer st = new StringTokenizer(mExtensions);
        while (st.hasMoreTokens()) {
            if (st.nextToken().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the extensions, with each extension separated by a newline.
     * @return String with each extension on a separate line.
     */
    public String getExtensionsLine() {
        String result = "";
        StringTokenizer st = new StringTokenizer(mExtensions);
        while (st.hasMoreTokens()) {
            result += st.nextToken() + "\n";
        }
        return result;
    }

    /**
     * Number of supported combined texture units, units accessible both in vertex and fragment shader.
     * @return Number of texture unit accessible in vertex and fragment shader.
     * If a shader is accessible from both it counts as 2.
     */
    public int getCombinedTextureUnits() {
        return mCombinedTextures;
    }

    /**
     * Returns the number of supported texture units in the fragment shader.
     * @return Number of textur units in the fragment shader.
     */
    public int getFragmentTextureUnits() {
        return mFragmentTextures;
    }

    /**
     * Returns the number of supported texture units in the vertex shader.
     * @return Number of texture units in the vertex shader.
     */
    public int getVertexTextureUnits() {
        return mVertexTextures;
    }

    /**
     * Returns the max number of fragment shader vector uniforms.
     * @return Max number of fragment shader uniform vectors.
     */
    public int getMaxFragmentUniforms() {
        return mMaxFragUniformVectors;
    }

    /**
     * Returns the max number of vertex shader vector uniforms.
     * @return Max number of vertex shader uniform vectors.
     */
    public int getMaxVertexUniforms() {
        return mMaxVertUniformVectors;
    }

    /**
     * The value indicates the largest renderbuffer width and height
     * that the can be handled.
     * @return Max renderbuffer width and height.
     */
    public int getMaxRenderbufferSize() {
        return mMaxRenderbufferSize;
    }

    /**
     * The max size of a texture, in pixels.
     * @return Max size of texture, size is for both width and height value.
     */
    public int getMaxTextureSize() {
        return mMaxTextureSize;
    }

    /**
     * Returns the max number of varying vectors that are available in the shaders.
     * @return Max number of varying vectors.
     */
    public int getMaxVaryingVectors() {
        return mMaxVaryingVectors;
    }

    /**
     * Returns the max number of vertex attributes available to a vertex shader.
     * @return Max number of 4 component vertex attributes in a vertex shader.
     */
    public int getMaxVertexAttribs() {
        return mMaxVertexAttribs;
    }

}

