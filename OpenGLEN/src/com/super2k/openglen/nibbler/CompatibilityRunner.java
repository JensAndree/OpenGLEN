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

import com.super2k.openglen.Renderer;


/**
 * Interface for a program that can be executed from Android and from standard Java
 * This is the generic behavior that is shared across platforms.
 * The CompatibilityRunner can be started, stopped and destroyed.
 * It is driven by calls to the processFrame() calls -
 * this shall perform one iteration of the main loop.
 * @author Richard Sahlin
 *
 */
public interface CompatibilityRunner extends TouchListener {


    /**
     * Setup with the renderer and inputstreamresolver to use.
     * Note that the Renderer shall NOT be started before calling this method.
     * Implementations must call the startRenderer() method of the renderer.
     * This is so that the runner may change settings and call the startRenderer method,
     * for instance changing the source path to shaders.
     * @param renderer
     * @param resolver The resolver to open inputstreams (for resources)
     * @param width Width
     * @param height Height
     * @throws IllegalArgumentException If renderer or resolver is NULL or if startRenderer() of
     * renderer failed.
     */
    void setup(Renderer renderer, InputStreamResolver resolver, int width, int height);


    /**
     * Process one frame, typically called each time in the main loop.
     * @param object Optional data
     * @return -1 to stop calling this method and terminate calling thread.
     */
    int processFrame(Object object);


    /**
     * Destroy this runner and all its resources.
     * This will exit the thread calling this runner.
     */
    void destroy();


    /**
     * Handle a custom event, what this shall do is implementation specific.
     * @param event The event object.
     */
    public void userEvent(UserEvent event);

}
