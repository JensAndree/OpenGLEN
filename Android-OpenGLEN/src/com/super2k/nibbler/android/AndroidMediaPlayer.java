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

package com.super2k.nibbler.android;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.super2k.openglen.nibbler.InputStreamResolver;
import com.super2k.openglen.nibbler.NibblerMediaPlayer;

/**
 * Implementation of NibblerMediaPlayer for the Android platform.
 * @author Richard Sahlin
 *
 */
public class AndroidMediaPlayer implements NibblerMediaPlayer {

    protected MediaPlayer mPlayer;

    public AndroidMediaPlayer() {}

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void release() {
        mPlayer.release();
    }

    @Override
    public void open(String name, InputStreamResolver resolver)
            throws IOException {
        if (name == null || resolver == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        mPlayer = new MediaPlayer();
        AssetManager am = ((AndroidStreamResolver)resolver).getAssetManager();
        AssetFileDescriptor afd = am.openFd(name);
        mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
    }

    @Override
    public void prepare() throws IOException{
        mPlayer.prepare();
    }

    @Override
    public void play() {
        mPlayer.start();
    }

}
