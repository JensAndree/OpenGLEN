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

package com.super2k.openglen.nibbler;

import java.util.Vector;

import com.super2k.openglen.EGLRenderer;
import com.super2k.openglen.ObjectFactory;
import com.super2k.openglen.ObjectFactoryManager;
import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.animation.Animation3D;
import com.super2k.openglen.objects.GLBlitObject;
import com.super2k.openglen.utils.ConfigurationParameters;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Log;

/**
 * Base class for CompatibilityRunner implementation,
 * this holds the most common functions and fields that are used by compatibility runners.
 * @author Richard Sahlin
 *
 */
public abstract class BaseCompatibilityRunner implements CompatibilityRunner {

    public String TAG = this.getClass().getSimpleName();

    /**
     * List with all current GLBlitObjects
     */
    protected Vector<GLBlitObject> mList = new Vector<GLBlitObject>();

    /**
     * Width and height of the display
     */
    protected int mWidth, mHeight;

    protected float[] mPerspectiveMatrix = new float[16];

    /**
     * Renderer used for screen renderering.
     */
    protected Renderer mRenderer;

    /**
     * If an EGL renderer is used it is set here.
     */
    protected EGLRenderer mEglRenderer;

    /**
     * Resolves streams in a platform dependant manner.
     */
    protected InputStreamResolver mResolver;

    protected boolean bypassDisplay = false;

    /**
     * Set to true to allocate array data in VBO.
     */
    protected boolean mUseVBO = true;

    /**
     * Onscreen size of blits,
     * this value is only valid when an orthogonal projection
     * is used.
     */
    protected int mBlitSize = 0;

    /**
     * Factory for objects, initialized in setup method.
     */
    protected ObjectFactory mFactory;


    @Override
    public void setup(Renderer renderer, InputStreamResolver resolver, int width, int height) {

        Log.d(TAG, "setup(width,height) " + width + ", " + height);
        //Fetch render configuration parameters from System property
        ConfigurationParameters.getRenderConfiguration(renderer.getRenderSetting());
        checkConfiguration();

        if (renderer == null || resolver == null) {
            throw new IllegalArgumentException("Called with NULL parameter");
        }

        mWidth = width;
        mHeight = height;
        mRenderer = renderer;
        //Check if we are using an EGL renderer.
        if (renderer instanceof EGLRenderer) {
            mEglRenderer = (EGLRenderer)renderer;
        }
        mResolver = resolver;
        mFactory = ObjectFactoryManager.createObjectFactory(null, renderer);
        mRenderer.setViewPort(0,0, mWidth, mHeight);

    }

    /**
     * Output fillrate.
     */
    public void logFillrate(String name) {

        ProfileInfo profile = mRenderer.getProfileInfo();
        float average = (profile.getTotalTicks() / profile.getFramecount());
        float blitKsize = mBlitSize * 0.001f;
        if (mBlitSize > 0) {
            Log.logFillrate(name, (int)(blitKsize * ((float)1000 / profile.getMinTicks())),
                    (int)(blitKsize * ((float)1000/profile.getMaxTicks())),
                    (int)(blitKsize * (1000/average)));
        }
    }

    /**
     * Animate all objects in the list, time is in seconds.
     * @param list
     * @param time
     */
    public void animateObjects(Vector<GLBlitObject> list, float time) {
        int size = list.size();
        GLBlitObject blit;
        for (int i = 0; i < size; i++) {
            blit = list.elementAt(i);
            Animation3D anim;
            for (int loop = 0; loop < blit.anim.length; loop++) {
                anim = blit.anim[loop];
                if (anim!=null) {
                    anim.animate(time);
                }
            }
        }

    }

    @Override
    public synchronized void destroy() {
        /**
         * Called when the thread has exited
         * - release all common GL objects in the list.
         */
        if (mList != null) {
            GraphicsLibraryHandler.releaseGLBuffers(mList, mRenderer);
        }
    }

    /**
     * Check configuration parameters set as System property.
     * This is an internal method that should be called by setup()
     */
    private void checkConfiguration() {

        String str = ConfigurationParameters.getUseVBO();
        if (str.equalsIgnoreCase("true")) {
            mUseVBO = true;
        } else {
            if (str.equalsIgnoreCase("false"))
            mUseVBO = false;
        }

    }

    /**
     * Helper class to set a property for this class when running OpenGLEN.
     * Use when starting OpenGLEN programatically and you need to set a specific property.
     * The properties will be read when setup() is called and stored in members in the class.
     * @param useVBO True to flag for usage of VBO, false to not use VBOs.
     */
    public static void setProperties(boolean useVBO) {
        ConfigurationParameters.setUseVBO(useVBO);
    }

}
