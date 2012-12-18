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


/**
 * Class that holds the runtime render settings, this is depth test, culling, cull-face, dither
 * and other options related to geometry/rasterization.
 * @author Richard Sahlin
 *
 */
public class RenderSetting {

    protected final static String INVALID_CULLFACE_STR = "Invalid cullFace:";
    protected final static String INVALID_CLEARFLAG_STR = "Invalid clearFlag:";
    protected final static String INVALID_DEPTHFUNC_STR = "Invalid depthFunc:";
    protected final static String INVALID_CLEARCOLOR_STR = "Invalid clear color array.";

    public final static int DEFAULT_DEPTHFUNC = ConstantValues.LEQUAL;
    public final static float DEFAULT_DEPTHRANGE_NEAR = 0.000001f;
    public final static float DEFAULT_DEPTHRANGE_FAR = 1f;
    public final static float DEFAULT_CLEARDEPTH = DEFAULT_DEPTHRANGE_FAR;
    public final static int DEFAULT_CULLFACE = ConstantValues.CULL_BACK;
    public final static int DEFAULT_CLEARFLAG = ConstantValues.COLOR_BUFFER_BIT |
            ConstantValues.DEPTH_BUFFER_BIT;
    public final static boolean DEFAULT_MULTISAMPLING = false;

    public final static int CHANGE_FLAG_ALL = -1; //Flag that all values should be updated
    public final static int CHANGE_FLAG_NONE = 0; //NO values should be updated.
    public final static int CHANGE_FLAG_CLEARCOLOR = 1; //Clearcolor has changed.
    public final static int CHANGE_FLAG_DEPTH = 2; //Depth related functions has changed.
    public final static int CHANGE_FLAG_CULLFACE = 4; //Cullface has changed
    public final static int CHANGE_FLAG_MULTISAMPLE = 8; //Multisample has changed

    /**
     * Set to true when a value has changed.
     */
    private boolean mIsDirty = true;

    /**
     * What parameters have changed, used for some settings.
     */
    private int mChangeFlag = CHANGE_FLAG_ALL;

    /**
     * Should multisampling be enabled, default is true - must also be supported in the surface (EGL)
     */
    protected boolean mEnableMultisampling = DEFAULT_MULTISAMPLING;

    /**
     * Depth func used if depth test is enabled, default is less or equal.
     */
    protected int mDepthFunc = DEFAULT_DEPTHFUNC;

    /**
     * Near value for depthrange
     */
    protected float mDepthRangeNear = DEFAULT_DEPTHRANGE_NEAR;

    /**
     * Far value for depthrange.
     */
    protected float mDepthRangeFar = DEFAULT_DEPTHRANGE_FAR;

    /**
     * Depth clear value.
     */
    protected float mClearDepth = DEFAULT_CLEARDEPTH;

    /**
     * If depth test is enabled display is cleared to this color at beginFrame()
     */
    protected float[] mClearColor = new float[] {0, 0, 0, 1};

    /**
     * If culling is enabled, what faces should be culled.
     *
     */
    protected int  mCullFace = DEFAULT_CULLFACE;

    /**
     * This value is read in Renderer.beginFrame() and used to decide if buffer should be cleared
     * Defaults to clearing depth and color-buffer.
     */
    protected int mClearFlags = DEFAULT_CLEARFLAG;

    /**
     * Constructs a new RenderSetting with default values.
     * Depth function is LEQUAL.
     * Cull flag is CULL_BACK
     * Clear flags are COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT
     *
     */
    public RenderSetting()   {

    }

    /**
     * Creates a new RenderSettings class with the specified values, other values will default.
     * @param clearFlags Set to what buffers to clear between frames, values are ored together
     * ConstantValues.DEPTH_BUFFER_BIT | ConstantValues.STENCIL_BUFFER_BIT |
     * ConstantValues.COLOR_BUFFER_BIT
     * Set to ConstantValues.NONE to disable clearing.
     * @param cullFace What faces to cull, set to ConstantValues.NONE to disable culling.
     * One of:
     * ConstantValues.CULL_BACK, ConstantValues.CULL_FRONT,
     * ConstantValues.CULL_FRONT_AND_BACK or ConstantValues.NONE
     * @param depthTest Set to true to enable depth test.
     * If true depth test is set to LEQUAL, if other depth test function is requred use another constuctor.
     * @param multisampling Set to true to enable multisampling, must also be configured in EGL.
     * @throws IllegalArgumentException If clearFlags or cullFace is not valid.
     */
    public RenderSetting(int clearFlags, int cullFace, boolean depthTest, boolean multisampling)   {
        setClearFunction(clearFlags);
        setCullFace(cullFace);
        mEnableMultisampling = multisampling;
        if (depthTest){
            mDepthFunc = ConstantValues.LEQUAL;
        }
        else   {
            mDepthFunc = ConstantValues.NONE;
        }

    }

    /**
     * Enables or disables multisampling, to be enabled the surface must also support multisampling.
     * @param enableMultisampling
     */
    public void enableMultisampling(boolean enableMultisampling)    {
        mEnableMultisampling = enableMultisampling;
        mIsDirty = true;
    }


    /**
     * Returns true if settings needs to be updated, false if settings has not been updated.
     * @return True if settings have changed.
     */
    public boolean isDirty()      {
        return mIsDirty;
    }

    /**
     * Returns flags for what settings have changed, this is used for some settings such
     * as the clearcolor to enable faster changing of some settings.
     * @return Change flag, CHANGE_FLAG_ALL or a number of CHANGE_FLAG_XX values ored together.
     */
    public int getChangeFlag() {
        return mChangeFlag;
    }

    /**
     * Clears the dirty flag, should be called after settings has been updated.
     */
    public void clearDirty() {
        mIsDirty = false;
        mChangeFlag = CHANGE_FLAG_NONE;
    }

    /**
     * State of the flag indicating if multisampling should be enabled or not.
     * For multisampling to work a surface with multisample buffer must be configured.
     * @return True to enable multisampling.
     */
    public boolean isMultisampling()    {
        return mEnableMultisampling;
    }

    /**
     * Set the depth function value, set NONE to disable depth test.
     * @param the Depth function.
     * Valid values from ConstantValues are:
     * NONE, NEVER, LESS, EQUAL, LEQUAL, GREATER, GEQUAL, ALWAYS
     * @throws IllegalArgumentException If depthFunc is not valid
     */
    public void setDepthFunc(int depthFunc) {
        switch (depthFunc) {
            case ConstantValues.NONE:
            case ConstantValues.NEVER:
            case ConstantValues.LESS:
            case ConstantValues.EQUAL:
            case ConstantValues.LEQUAL:
            case ConstantValues.GREATER:
            case ConstantValues.GEQUAL:
            case ConstantValues.ALWAYS:
                break;
            default:
            throw new IllegalArgumentException(INVALID_DEPTHFUNC_STR);
        }
        mChangeFlag |= CHANGE_FLAG_DEPTH;
        mDepthFunc = depthFunc;
        mIsDirty = true;
    }
    /**
     * Return the depth function value, this value controls what pixels
     * pass the depth test. NONE to disable depth test.
     * Valid values are: NONE, NEVER, LESS, EQUAL, LEQUAL, GREATER, GEQUAL, ALWAYS
     * @return Depth function.
     */
    public int getDepthFunc()    {
        return mDepthFunc;
    }

    /**
     * Returns the flags that contol what buffers should be cleared between frames.
     * ConstantValues.NONE for no clearing of buffers
     * ConstantValues.DEPTH_BUFFER_BIT to clear depth buffer between frames.
     * ConstantValues.COLOR_BUFFER_BIT to clear color buffer between frames.
     * @return Flags to control what buffer should be cleared between frames.
     */
    public int getClearFunction()  {
        return mClearFlags;
    }

    /**
     * Set the clearDepth value
     * @param clearDepth
     */
    public void setClearDepth(float clearDepth)    {
        this.mClearDepth = clearDepth;
        mIsDirty = true;
        mChangeFlag |= CHANGE_FLAG_DEPTH;
    }

    /**
     * Return the clear depth value.
     * @return Clear depth.
     */
    public float getClearDepth()    {
        return mClearDepth;
    }

    /**
     * Set the value used for culling, set to NONE to disable culling.
     * Valid values are: NONE, FRONT, BACK, FRONT_AND_BACK
     * @param cullFace What faces to cull, set to ConstantValues.NONE to disable culling.
     * ConstantValues.CULL_BACK, ConstantValues.CULL_FRONT,
     * ConstantValues.CULL_FRONT_AND_BACK or ConstantValues.NONE
     * @throws IllegalArgumentException If cullface is illegal value.
     */
    public void setCullFace(int cullFace)    {
        if (cullFace != ConstantValues.CULL_BACK &&
                cullFace != ConstantValues.CULL_FRONT &&
                cullFace != ConstantValues.CULL_FRONT_AND_BACK &&
                cullFace != ConstantValues.NONE)    {
            throw new IllegalArgumentException(INVALID_CULLFACE_STR + cullFace);
        }

        this.mCullFace = cullFace;
        mIsDirty = true;
        mChangeFlag |= CHANGE_FLAG_CULLFACE;
    }

    /**
     * Return the cull mode.
     * @return Either of NONE, FRONT, BACK, FRONT_AND_BACK
     */
    public int getCullFace(){
        return mCullFace;
    }


    /**
     * Set the farthest value for the depthrange.
     * @param far
     */
    public void setDepthRangeFar(float far)    {
        mDepthRangeFar = far;
        mIsDirty = true;
        mChangeFlag |= CHANGE_FLAG_DEPTH;
    }

    /**
     * Return the farthest value for the depthrange, as set by
     * setDepthRangeFar
     * @return The far depth value.
     */
    public float getDepthRangeFar()    {
        return mDepthRangeFar;
    }

    /**
     * Set the nearest value for the depthrange.
     * @param near
     */
    public void setDepthRangeNear(float near)    {
        mDepthRangeNear = near;
        mIsDirty = true;
        mChangeFlag |= CHANGE_FLAG_DEPTH;
    }

    /**
     * Return the near depth value as set by setDepthRangeNear
     * @return The near depth value.
     */
    public float getDepthRangeNear()    {
        return mDepthRangeNear;
    }

    /**
     * Returns the clear color component, red, green, blue, alpha in an array. Red at offset 0.
     * @return Clear color components.
     */
    public float[] getClearColor()    {
        return mClearColor;
    }

    /**
     * Sets the color to clear color buffer to when COLOR_BUFFER_BIT is enabled in setClearFunction
     * Array must contain at least 4 values.
     * @param clearColor Array containing RGBA values for the clear color, red at index 0,
     * alpha at index 3.
     * @throws IllegalArgumentException If clearColor is null or does not contain at least 4 values.
     */
    public void setClearColor(float[] clearColor)  {
        if (clearColor == null || clearColor.length< 4) {
            throw new IllegalArgumentException(INVALID_CLEARCOLOR_STR);
        }
        mClearColor = clearColor;
        /**
         * If already dirty then don't update flag, otherwise other flags will be lost.
         */
        if (!mIsDirty) {
            mIsDirty = true;
            mChangeFlag |= CHANGE_FLAG_CLEARCOLOR;
        }
    }

    /**
     * Set the flags for the clear function, this is called in beginFrame.
     * @param clearFlags The flags for clear function in beginFrame() method of Renderer.
     * ConstantValues.DEPTH_BUFFER_BIT, ConstantValues.STENCIL_BUFFER_BIT,
     * ConstantValues.COLOR_BUFFER_BIT or ConstantValues.NONE
     * @throws IllegalArgumentException If clearFlags is invalid.
     */
    public void setClearFunction(int clearFlags)  {
        if ((clearFlags ^ (clearFlags & (ConstantValues.DEPTH_BUFFER_BIT |
                ConstantValues.STENCIL_BUFFER_BIT |
                ConstantValues.COLOR_BUFFER_BIT))) != 0) {
            throw new IllegalArgumentException(INVALID_CLEARFLAG_STR);
        }
        mClearFlags |= clearFlags;
        mIsDirty = true;

    }

}
