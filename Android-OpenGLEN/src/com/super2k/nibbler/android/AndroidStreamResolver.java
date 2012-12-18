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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

import com.super2k.openglen.nibbler.InputStreamResolver;

/**
 * Android InputStreamResolver, used for instance when loading resources and shaders.
 * @author Richard Sahlin
 *
 */
public class AndroidStreamResolver implements InputStreamResolver {

    private final AssetManager mAm;

    /**
     * Constructs a new AndroidStreamResolver using the specified Assetmanager,
     * subsequent calls to openInputStream will use this AssetManager to open inputstreams.
     *
     * @param am The AssetManager
     * @throws IllegalArgumentException if am is null.
     */
    public AndroidStreamResolver(AssetManager am) {
        if (am == null)
            throw new IllegalArgumentException("AssetManager is NULL");
        this.mAm = am;
    }

    @Override
    public InputStream openInputStream(String stream) throws IOException {

        //Assuming mAm.open will throw runtime exception if stream is NULL
        InputStream result = mAm.open(stream, AssetManager.ACCESS_STREAMING);

        if (result == null) {
            throw new IOException("Could not open inputstream");
        }
        return result;

    }

    @Override
    public FileDescriptor openFileDescriptor(String file) throws IOException {
        return mAm.openFd(file).getFileDescriptor();
    }

    /**
     * Returns the AssetManager that this inputstreamresolver was initialized with.
     * @return
     */
    public AssetManager getAssetManager() {
        return mAm;
    }


}
