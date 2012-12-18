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
package com.super2k.openglen;


/**
 * OpenGLEN throwable when an error occurs, usually in the graphics implementation.
 * Many errors are handled by throwing an IllegalArgumentException,
 * this is not enough to handle all cases.
 * For instance when an error occurs while doing graphics processing, or calls,
 * an exception is needed to relay the reason that may not be an underlying exception
 * but rather an error code or state.
 * Another instance is when a method fails to return a value,
 * this exception can then be used with String.
 * @author Richard Sahlin
 *
 */
public class OpenGLENException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -8418052001522103763L;
    /**
     * Error code, reason why the exception was thrown.
     */
    protected int   mErrorCode = ConstantValues.NONE;

    /**
     * Constructs an OpenGLENException with the specified errorCode as reason.
     * Use this for instance when a call to the underlying graphics implementation fails
     * and this error needs to be realayed using an exception.
     * @param errorCode
     */
    public OpenGLENException(int errorCode){
        super();
        mErrorCode = errorCode;
    }

    /**
     * Constructs an OpenGLENException with cause and error code.
     * @param cause String describing the cause.
     * @param errorCode Error code relevant for the cause.
     */
    public OpenGLENException(String cause, int errorCode) {
        super(cause);
        mErrorCode = errorCode;

    }
    /**
     * Constructs an OpenGLENException with the specified cause.
     * @param cause
     */
    public OpenGLENException(String cause){
        super(cause);
    }

    /**
     * Returns the error code that is the reason for this exception.
     * @return The error code, a value from ConstantValues
     */
    public int getErrorCode()   {
        return mErrorCode;
    }

    /**
     * Returns the exception as a String.
     * @return
     */
    @Override
    public String toString()    {
        return getMessage() + " Error code:" + mErrorCode;
    }

}
