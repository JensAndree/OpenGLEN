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
package com.super2k.openglen.android.test;

import com.super2k.openglen.android.OpenGLENActivity;
import com.super2k.openglen.test.OpenGLENTester;

/**
 * Base class for OpenGLEN Test Activity
 * This class provides common functions needed to write Android instrumentation runners using
 * an Activity.
 * The Activity under test shall extend this class. The OpenGLEN implementation class must
 * extend OpenGLENTester
 * @author Richard Sahlin
 *
 */
public class BaseTestActivity extends OpenGLENActivity {

    public OpenGLENTester getTester() {
        if (mOpenGLENThread != null) {
            return (OpenGLENTester) mOpenGLENThread.getRunner();
        }
        throw new IllegalArgumentException("OpenGLENThread is null");
    }

}
