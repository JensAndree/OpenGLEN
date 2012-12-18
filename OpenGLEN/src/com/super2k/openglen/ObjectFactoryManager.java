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

import com.super2k.openglen.objects.GLENObjectFactory;

/**
 * This class will create the ObjectFactory needed by clients.
 *
 * @author Richard Sahlin
 *
 */
public class ObjectFactoryManager {

    /**
     * Creates an instance of an ObjectFactory that can be used to create
     * OpenGLEN objects.
     * @param config Configuration parameters. Currently ignored, may be null.
     * @param renderer The Renderer to create objects for.
     * @return An instance of the ObjectFactory ready to create OpenGLEN
     * objects.'
     * @throws IllegalArgumentException If renderer is null
     */
    public static ObjectFactory createObjectFactory(String config,
                                                    Renderer renderer) {
        if (renderer==null) {
            throw new IllegalArgumentException("Renderer may not be null.");
        }
        // Always create default factory.
        return new GLENObjectFactory(renderer);
    }

}
