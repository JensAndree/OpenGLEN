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

package com.super2k.nibbler.j2se;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.super2k.openglen.nibbler.InputStreamResolver;

public class J2SEInputStreamResolver implements InputStreamResolver {

    protected String mPath;

    /**
     * Constructs a new J2SE inputstream resolver based on the specified filepath.
     * @param path
     * @throws IllegalArgumentException If path is NULL
     */
    public J2SEInputStreamResolver(String path) {
        if (path == null)
            throw new IllegalArgumentException("Path is NULL");
        mPath = path;
        //Make sure path ends with path delimiter IF a path is set
        if (mPath.length() > 0 && !mPath.endsWith(File.pathSeparator))
            mPath = mPath + File.separatorChar;

    }

    @Override
    public InputStream openInputStream(String stream) throws IOException {
        return new FileInputStream(mPath + stream);
    }

    @Override
    public FileDescriptor openFileDescriptor(String file) throws IOException {
        return new FileInputStream(mPath + file).getFD();
    }

}
