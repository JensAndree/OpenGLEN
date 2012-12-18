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
 * User generated event for the compatibilityrunner.
 * This is just a carrier for a custom event that can be passed.
 * @author Richard Sahlin
 *
 */
public class UserEvent {

    /**
     * The type of event - user specified
     */
    public int type;
    /**
     * user data
     */
    public Object data;

    public final static int TYPE_KEY_DOWN = 0x0AE00;
    public final static int TYPE_KEY_UP = 0x0AE01;


    /**
     * UserEvent constructor with type and object.
     * @param type
     * @param data
     */
    public UserEvent(int type, Object data){
        this.type = type;
        this.data = data;
    }

}