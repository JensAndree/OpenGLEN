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

import com.super2k.openglen.utils.Log;

/**
 * Factory method for creating media player.
 * @author Richard Sahlin
 *
 */
public class MediaFactory {

    private static final String TAG = MediaFactory.class.getClass().getSimpleName();

    public static NibblerMediaPlayer createPlayer() {

        try {

            String jre = System.getProperty("java.vendor");
            if (jre.equalsIgnoreCase("the android project")) {
                Log.d(TAG, "Creating MediaPlayer for Android");
                NibblerMediaPlayer player =
                        (NibblerMediaPlayer)Class.forName(
                                "com.super2k.nibbler.android.AndroidMediaPlayer")
                                .newInstance();
                return player;
            } else {
                Log.d(TAG, "Creating MediaPlayer for J2SE");
                return (NibblerMediaPlayer)Class.forName(
                        "com.super2k.nibbler.j2se.J2SEMediaPlayer")
                        .newInstance();
            }

        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
    }
}
