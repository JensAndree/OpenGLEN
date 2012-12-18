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

import com.super2k.openglen.nibbler.InputStreamResolver;

/**
 * Interface for background rendering.
 * The background renderer should preferably be rendererd in the same pass as the foreground
 * for optimal performance. (The background renderer can itself be multi-pass to texture)
 * The backgroundrenderer is called before anything is rendered, the framebuffer contents is
 * undefined before calling. After this call returns the framebuffer should have valid
 * contents covering the screen area (unless a scissor region is set)
 * @author Richard Sahlin
 *
 */
public interface BackgroundRenderer {

    /**
     * Setup the backgroundrenderer, initialize any data needed to render.
     * When this call returns the backgroundrenderer should be ready to render pixels.
     * @param renderer The renderer to be used, use to get BitmapHandler etc.
     * @param resolver Resolver to load resources etc.
     * @param factory Object factory for creating GLEN Objects that can be rendered
     * @param width Width of display
     * @param height Height of display
     */
    public void setupBackground(Renderer renderer,InputStreamResolver resolver,
                                ObjectFactory factory, int width, int height);

    /**
     * Renders the background, after this call returns the display region should have
     * valid framebuffer contents (after the underlying graphics subsystem has finished rendering)
     * NOTE! Do NOT call heavy graphics functions such as finish()!
     * Just make sure the proper rendercalls are passed on to the system.
     * Rendering will take place together with foreground rendering.
     * @param renderer The renderer to use
     */
    public void renderBackground(Renderer renderer);

    /**
     * Called when the application is finishing, release all resources.
     * @param renderer The renderer used, do not destroy renderer.
     */
    public void destroyBackground(Renderer renderer);

}
