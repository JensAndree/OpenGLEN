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

/**
 * Interface for objects that can be pooled.
 * @author Richard Sahlin
 *
 */
public interface PooledObject {

    /**
     * If the key is this value then the object does not belong to any pool.
     */
    public final static int NOT_POOLED_OBJECT = -1;

    /**
     * The key to find object from pool by, normally this is the same for all objects
     * of same type/class
     * @param key The key to get object from pool by.
     */
    public void setKey(int key);

    /**
     * Returns the objects key for the pool, or -1 if this is not a pooled object.
     * @return The object key, or -1 if this is not a pooled object.
     */
    public int getKey();

    /**
     * Release the object, this means clearing any specific data.
     */
    public void releaseObject();

    /**
     * Called when the object is created for the pool.
     * @param obj Extra object, this is implementation specific.
     */
    public void createObject(Object obj);

    /**
     * Destroys the object, this is normally done when the pool is destroyed.
     * @param obj Extra object, this is implementation specific
     */
    public void destroyObject(Object obj);

}
