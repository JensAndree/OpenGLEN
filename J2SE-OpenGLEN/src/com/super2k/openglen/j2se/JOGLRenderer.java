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
package com.super2k.openglen.j2se;

import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.Renderer;
import com.super2k.openglen.core.GLESBaseRenderer;

/**
 * Base class for a JOGLRenderer implementation of com.sonyericsson.graphics.openglen.Renderer
 * This implementation is based on a GLCanvas that is contained in this class.
 * @author Richard Sahlin
 *
 */
public abstract class JOGLRenderer extends GLESBaseRenderer  implements Renderer {

    /**
     * Constructs a new JOGLRenderer using the specified OpenGLENThread and rendersettings.
     * @param width Width of rendering area.
     * @param height Height of rendering area.
     * @param renderSetting The render settings.
     */
    public JOGLRenderer(RenderSetting renderSetting) {
        super(renderSetting);

    }


    @Override
    public void destroy() {
        //On AWT this method shall be called when GLCanvas is released
        //        glDrawable.setRealized(true);

    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }


    @Override
    public synchronized void initRenderer() throws OpenGLENException {
        super.initRenderer();
        mInitialized = true;

    }


}
