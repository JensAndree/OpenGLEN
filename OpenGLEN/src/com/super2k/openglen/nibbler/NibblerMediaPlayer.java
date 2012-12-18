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

import java.io.IOException;

/**
 * Interface for a media player.
 * Get an instance of the MediaPlayer by calling the MediaFactory
 * @author Richard Sahlin
 *
 */
public interface NibblerMediaPlayer {


    /**
     * Open the resource
     * @param name Name of file to open
     * @param resolver The inputstreamresolver to open the stream.
     * @throws IOException If there is an exception opening the stream.
     * @throws IllegalArgumentException If any of the parameters is null
     * or if resource type is invalid.
     */
    public void open(String name, InputStreamResolver resolver) throws IOException;

    /**
     * Prepares the media to be played.
     */
    public void prepare() throws IOException;

    /**
     * Starts playing the current media, if media is paused it is resumed.
     */
    public void play();

    /**
     * Stops playback.
     */
    public void stop();

    /**
     * Pause playback.
     */
    public void pause();

    /**
     * Call when the media will not be used anymore to free resources.
     * Too many active MediaPlayers may result in exception.
     */
    public void release();

}
