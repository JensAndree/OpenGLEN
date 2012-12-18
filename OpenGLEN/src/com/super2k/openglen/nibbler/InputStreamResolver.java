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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to open an inputstream, on Android this goes through the AssetManager.
 * @author Richard Sahlin
 *
 */
public interface InputStreamResolver {

    /**
     * Opens a stream in a platform dependant manner.
     * @param stream
     * @return The opened InputStream
     * @throws IOException If no stream with the specified name could be openend.
     * @throws IllegalArgumentException if stream is null
     */
    InputStream openInputStream(String stream) throws IOException;

    /**
     * Open a file and returns the FileDescriptor
     * @param file
     * @return The FileDescriptor for the file.
     * @throws IOException
     */
    FileDescriptor openFileDescriptor(String file) throws IOException;

}
