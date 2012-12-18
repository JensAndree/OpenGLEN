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

/**
 * Listener for touch events
 * @author Richard Sahlin
 *
 */
public interface TouchListener {

    /**
     * Handle touch down event, return true if handled.
     * @param x
     * @param y
     * @return True if the touch down was handled.
     */
    public boolean touchDown(float x, float y);

    /**
     * Handle touch up event, return true if handled.
     * @param x
     * @param y
     * @return True if the touch up was handled.
     */
    public boolean touchUp(float x, float y);

    /**
     * Handle a touch move to the specified position (drag)
     * @param x
     * @param y
     * @return True if the touch move was handled.
     */
    public boolean touchMove(float x, float y);


    /**
     * Handle touch tap
     * @param x
     * @param y
     * @return
     */
    public boolean touchTap(float x, float y);


}
