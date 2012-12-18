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
 * Definition of constant values, as much as possible use same constant values as GL.
 * @author Richard Sahlin
 *
 */
public interface ConstantValues {

    public static final int PIXELFORMAT_RGBA_4444 = 7;
    public static final int PIXELFORMAT_RGBA_5551 = 6;
    public static final int PIXELFORMAT_RGBA_8888 = 1;
    public static final int PIXELFORMAT_RGBX_8888 = 2;
    public static final int PIXELFORMAT_RGB_565 = 4;
    public static final int PIXELFORMAT_RGB_888 = 3;


    public final static int NONE = 0;                   //Disable a function.
    public final static int NO_ERROR = 0;


    /* ErrorCode */
    public final static int INVALID_ENUM =                  0x0500;
    public final static int INVALID_VALUE =                 0x0501;
    public final static int INVALID_OPERATION =             0x0502;
    public final static int OUT_OF_MEMORY =                 0x0505;


    public final static int TRUE = 1;
    public final static int FALSE = 0;

    public final static int UNSIGNED_INT_8_8_8_8 = 32821;      //From GL
    public final static int UNSIGNED_INT_5_6_5 = 33635;        //From GL
    public final static int UNSIGNED_SHORT_5_6_5 = 33635;      //From GL
    public final static int UNSIGNED_BYTE = 5121;              //From GL
    public final static int UNSIGNED_SHORT_4_4_4_4 = 0x8033;
    public final static int UNSIGNED_SHORT_5_5_5_1 = 0x8034;

    /* PixelFormat */
    public final static int DEPTH_COMPONENT = 0x1902;
    public final static int ALPHA = 0x1906;
    public final static int RGB = 0x1907;
    public final static int RGBA = 0x1908;
    public final static int LUMINANCE = 0x1909;
    public final static int LUMINANCE_ALPHA = 0x190A;

    /* Framebuffer Object. */
    public final static int FRAMEBUFFER = 0x8D40;
    public final static int RENDERBUFFER = 0x8D41;

    public final static int RGBA4 = 0x8056;
    public final static int RGB5_A1 = 0x8057;
    public final static int RGB565 = 0x8D62;
    public final static int DEPTH_COMPONENT16 = 0x81A5;
    public final static int STENCIL_INDEX = 0x1901;
    public final static int STENCIL_INDEX8 = 0x8D48;


    public final static int RENDERBUFFER_WIDTH = 0x8D42;
    public final static int RENDERBUFFER_HEIGHT = 0x8D43;
    public final static int RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;
    public final static int RENDERBUFFER_RED_SIZE = 0x8D50;
    public final static int RENDERBUFFER_GREEN_SIZE = 0x8D51;
    public final static int RENDERBUFFER_BLUE_SIZE = 0x8D52;
    public final static int RENDERBUFFER_ALPHA_SIZE = 0x8D53;
    public final static int RENDERBUFFER_DEPTH_SIZE = 0x8D54;
    public final static int RENDERBUFFER_STENCIL_SIZE = 0x8D55;

    public final static int FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 0x8CD0;
    public final static int FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 0x8CD1;
    public final static int FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 0x8CD2;
    public final static int FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;

    public final static int NUM_COMPRESSED_TEXTURE_FORMATS  = 0x86A2;
    public final static int COMPRESSED_TEXTURE_FORMATS = 0x86A3;

    public final static int COLOR_ATTACHMENT0 = 0x8CE0;
    public final static int DEPTH_ATTACHMENT = 0x8D00;
    public final static int STENCIL_ATTACHMENT = 0x8D20;

    public final static int FRAMEBUFFER_COMPLETE = 0x8CD5;
    public final static int FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
    public final static int FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
    public final static int FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;
    public final static int FRAMEBUFFER_UNSUPPORTED = 0x8CDD;

    public final static int FRAMEBUFFER_BINDING = 0x8CA6;
    public final static int RENDERBUFFER_BINDING = 0x8CA7;
    public final static int MAX_RENDERBUFFER_SIZE = 0x84E8;

    public final static int INVALID_FRAMEBUFFER_OPERATION  = 0x0506;

    /* StringName */
    public final static int VENDOR = 0x1F00;
    public final static int RENDERER = 0x1F01;
    public final static int VERSION = 0x1F02;
    public final static int EXTENSIONS = 0x1F03;



    /* TextureMagFilter */
    public final static int NEAREST = 0x2600;
    public final static int LINEAR = 0x2601;

    /* TextureMinFilter */
    /*      GL_NEAREST */
    /*      GL_LINEAR */
    public final static int NEAREST_MIPMAP_NEAREST = 0x2700;
    public final static int LINEAR_MIPMAP_NEAREST = 0x2701;
    public final static int NEAREST_MIPMAP_LINEAR = 0x2702;
    public final static int LINEAR_MIPMAP_LINEAR = 0x2703;

    /* TextureParameterName */
    public final static int TEXTURE_MAG_FILTER = 0x2800;
    public final static int TEXTURE_MIN_FILTER = 0x2801;
    public final static int TEXTURE_WRAP_S = 0x2802;
    public final static int TEXTURE_WRAP_T = 0x2803;

    /* TextureTarget */
    /*      GL_TEXTURE_2D */
    public final static int TEXTURE = 0x1702;

    public final static int TEXTURE_CUBE_MAP = 0x8513;
    public final static int TEXTURE_BINDING_CUBE_MAP = 0x8514;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;
    public final static int TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;
    public final static int TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A;
    public final static int MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C;

    public final static int TEXTURE_EXTERNAL_OES = 0x8D65;


    /* TextureUnit */
    public final static int TEXTURE0 = 0x84C0;
    public final static int TEXTURE1 = 0x84C1;
    public final static int TEXTURE2 = 0x84C2;
    public final static int TEXTURE3 = 0x84C3;
    public final static int TEXTURE4 = 0x84C4;
    public final static int TEXTURE5 = 0x84C5;
    public final static int TEXTURE6 = 0x84C6;
    public final static int TEXTURE7 = 0x84C7;
    public final static int TEXTURE8 = 0x84C8;
    public final static int TEXTURE9 = 0x84C9;
    public final static int TEXTURE10 = 0x84CA;
    public final static int TEXTURE11 = 0x84CB;
    public final static int TEXTURE12 = 0x84CC;
    public final static int TEXTURE13 = 0x84CD;
    public final static int TEXTURE14 = 0x84CE;
    public final static int TEXTURE15 = 0x84CF;
    public final static int TEXTURE16 = 0x84D0;
    public final static int TEXTURE17 = 0x84D1;
    public final static int TEXTURE18 = 0x84D2;
    public final static int TEXTURE19 = 0x84D3;
    public final static int TEXTURE20 = 0x84D4;
    public final static int TEXTURE21 = 0x84D5;
    public final static int TEXTURE22 = 0x84D6;
    public final static int TEXTURE23 = 0x84D7;
    public final static int TEXTURE24 = 0x84D8;
    public final static int TEXTURE25 = 0x84D9;
    public final static int TEXTURE26 = 0x84DA;
    public final static int TEXTURE27 = 0x84DB;
    public final static int TEXTURE28 = 0x84DC;
    public final static int TEXTURE29 = 0x84DD;
    public final static int TEXTURE30 = 0x84DE;
    public final static int TEXTURE31 = 0x84DF;
    public final static int ACTIVE_TEXTURE = 0x84E0;


    /* TextureWrapMode */
    public final static int REPEAT = 0x2901;
    public final static int CLAMP_TO_EDGE = 0x812F;
    public final static int MIRRORED_REPEAT = 0x8370;


    /* EnableCap */
    public final static int TEXTURE_2D = 0x0DE1;
    public final static int CULL_FACE = 0x0B44;
    public final static int BLEND = 0x0BE2;
    public final static int DITHER = 0x0BD0;
    public final static int STENCIL_TEST = 0x0B90;
    public final static int DEPTH_TEST = 0x0B71;
    public final static int SCISSOR_TEST = 0x0C11;
    public final static int POLYGON_OFFSET_FILL = 0x8037;
    public final static int SAMPLE_ALPHA_TO_COVERAGE = 0x809E;
    public final static int SAMPLE_COVERAGE = 0x80A0;


    /* ClearBufferMask */
    public final static int COLOR_BUFFER_BIT = 16384;  //from GL
    public final static int STENCIL_BUFFER_BIT = 0x00000400; //from GL
    public final static int DEPTH_BUFFER_BIT = 256;    //from GL

    public final static int CULL_FRONT = 1028;         //from FRONT
    public final static int CULL_BACK = 1029;           //from BACK
    public final static int CULL_FRONT_AND_BACK = 1032; //from FRONT_AND_BACK -
                                                        //only points and lines drawn.

    public final static int NEVER = 512;                //from NEVER;
    public final static int LESS = 513;                 //from LESS;
    public final static int EQUAL = 514;                //from EQUAL;
    public final static int LEQUAL = 515;               //from LEQUAL;
    public final static int GREATER = 516;              //from GREATER;
    public final static int NOTEQUAL = 517;
    public final static int GEQUAL = 518;               //from GEQUAL;
    public final static int ALWAYS = 519;               //from ALWAYS


    public final static int FLAT = 7424;                //From FLAT
    public final static int SMOOTH = 7425;              //From SMOOTH

    public final static int POINTS = 0;                 //From POINTS
    public final static int LINE_STRIP = 3;             //From LINE_STRIP
    public final static int LINE_LOOP = 2;              //From LINE_LOOP
    public final static int LINES = 1;                  //From LINES
    public final static int TRIANGLE_STRIP = 5;         //From TRIANGLE_STRIP
    public final static int TRIANGLE_FAN = 6;           //From TRIANGLE_FAN
    public final static int TRIANGLES = 6;              //From TRIANGLES

    public final static int X_AXIS_INDEX = 0;           //Index in array for X axis.
    public final static int Y_AXIS_INDEX = 1;           //Index in array for Y axis.
    public final static int Z_AXIS_INDEX = 2;           //Index in array for Z axis.

    public final static int USE_X_AXIS = 0x10;          //Value that can be ored to specify axis use
    public final static int USE_Y_AXIS = 0x8;           //Value that can be ored to specify axis use
    public final static int USE_Z_AXIS = 0x4;           //Value that can be ored to specify axis use


    public final static int FIXED = 5132;               //From FIXED
    public final static int FLOAT = 5126;               //From FLOAT
    public final static int BYTE =  5120;               //From BYTE
    public final static int SHORT = 5122;               //From SHORT
    public final static int HALF_FLOAT_OES = 0x8D61;


    public final static int ZERO = 0;                   //From ZERO
    public final static int ONE = 1;                    //From ONE


    public final static int SRC_COLOR = 768;            //From SRC_COLOR
    public final static int ONE_MINUS_SRC_COLOR = 769;  //From ONE_MINUS_SRC_COLOR (1, 1, 1, 1) -
                                                        //(Rs/kR, Gs/kG, Bs/kB, As/kA)
    public final static int DST_COLOR  = 774;           //From DST_COLOR (Rd/kR, Gd/kG,
                                                        //Bd/kB, Ad/kA )
    public final static int ONE_MINUS_DST_COLOR = 775;  //From ONE_MINUS_DST_COLOR  (1, 1, 1, 1) -
                                                        //(Rd/kR, Gd/kG, Bd/kB, Ad/kA)
    public final static int SRC_ALPHA = 770;            //From SRC_ALPHA (As/kA,
                                                        //As/kA, As/kA, As/kA )
    public final static int ONE_MINUS_SRC_ALPHA = 771;  //From ONE_MINUS_SRC_ALPHA  (1, 1, 1, 1) -
                                                        //(As/kA, As/kA, As/kA, As/kA)
    public final static int DST_ALPHA = 772;            //From DST_ALPHA (Ad/kA, Ad/kA,
                                                        //Ad/kA, Ad/kA )
    public final static int ONE_MINUS_DST_ALPHA = 773;  //FromGL_ONE_MINUS_DST_ALPHA (1, 1, 1, 1) -
                                                        //(Ad/kA, Ad/kA, Ad/kA, Ad/kA)
    public final static int SRC_ALPHA_SATURATE = 776;   //From SRC_ALPHA_SATURATE (i, i, i, 1)

    /* Separate Blend Functions */
    public final static int BLEND_DST_RGB = 0x80C8;
    public final static int BLEND_SRC_RGB = 0x80C9;
    public final static int BLEND_DST_ALPHA = 0x80CA;
    public final static int BLEND_SRC_ALPHA = 0x80CB;
    public final static int CONSTANT_COLOR = 0x8001;
    public final static int ONE_MINUS_CONSTANT_COLOR = 0x8002;
    public final static int CONSTANT_ALPHA = 0x8003;
    public final static int ONE_MINUS_CONSTANT_ALPHA = 0x8004;
    public final static int BLEND_COLOR = 0x8005;


    /* GetPName */
    //    public final static int LINE_WIDTH = 0x0B21;
    public final static int ALIASED_POINT_SIZE_RANGE = 0x846D;
    public final static int ALIASED_LINE_WIDTH_RANGE = 0x846E;
    //    public final static int CULL_FACE_MODE = 0x0B45;
    //    public final static int FRONT_FACE = 0x0B46;
    public final static int DEPTH_RANGE = 0x0B70;
    //    public final static int DEPTH_WRITEMASK = 0x0B72;
    //    public final static int DEPTH_CLEAR_VALUE = 0x0B73;
    //    public final static int DEPTH_FUNC = 0x0B74;
    //    public final static int STENCIL_CLEAR_VALUE = 0x0B91;
    //    public final static int STENCIL_FUNC = 0x0B92;
    //    public final static int STENCIL_FAIL = 0x0B94;
    //    public final static int STENCIL_PASS_DEPTH_FAIL = 0x0B95;
    //    public final static int STENCIL_PASS_DEPTH_PASS = 0x0B96;
    //    public final static int STENCIL_REF = 0x0B97;
    //    public final static int STENCIL_VALUE_MASK = 0x0B93;
    //    public final static int STENCIL_WRITEMASK = 0x0B98;
    //    public final static int STENCIL_BACK_FUNC = 0x8800;
    //    public final static int STENCIL_BACK_FAIL = 0x8801;
    //    public final static int STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;
    //    public final static int STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;
    //    public final static int STENCIL_BACK_REF = 0x8CA3;
    //    public final static int STENCIL_BACK_VALUE_MASK = 0x8CA4;
    //    public final static int STENCIL_BACK_WRITEMASK = 0x8CA5;
    //    public final static int VIEWPORT = 0x0BA2;
    public final static int SCISSOR_BOX = 0x0C10;
    /*      SCISSOR_TEST */
    public final static int COLOR_CLEAR_VALUE = 0x0C22;
    public final static int COLOR_WRITEMASK = 0x0C23;
    public final static int UNPACK_ALIGNMENT = 0x0CF5;
    public final static int PACK_ALIGNMENT = 0x0D05;
    public final static int MAX_TEXTURE_SIZE = 0x0D33;
    public final static int MAX_VIEWPORT_DIMS = 0x0D3A;
    public final static int SUBPIXEL_BITS = 0x0D50;
    public final static int RED_BITS = 0x0D52;
    public final static int GREEN_BITS = 0x0D53;
    public final static int BLUE_BITS = 0x0D54;
    public final static int ALPHA_BITS = 0x0D55;
    public final static int DEPTH_BITS = 0x0D56;
    public final static int STENCIL_BITS = 0x0D57;
    public final static int POLYGON_OFFSET_UNITS = 0x2A00;
    /*      POLYGON_OFFSET_FILL */
    public final static int POLYGON_OFFSET_FACTOR = 0x8038;
    public final static int TEXTURE_BINDING_2D = 0x8069;
    public final static int SAMPLE_BUFFERS = 0x80A8;
    public final static int SAMPLES = 0x80A9;
    public final static int SAMPLE_COVERAGE_VALUE = 0x80AA;
    public final static int SAMPLE_COVERAGE_INVERT = 0x80AB;



    /* Shaders */
    public final static int FRAGMENT_SHADER=35632;
    public final static int VERTEX_SHADER=35633;
    public final static int MAX_VERTEX_ATTRIBS =              0x8869;
    public final static int MAX_VERTEX_UNIFORM_VECTORS =      0x8DFB;
    public final static int MAX_VARYING_VECTORS =             0x8DFC;
    public final static int MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
    public final static int MAX_VERTEX_TEXTURE_IMAGE_UNITS =  0x8B4C;
    public final static int MAX_TEXTURE_IMAGE_UNITS =         0x8872;
    public final static int MAX_FRAGMENT_UNIFORM_VECTORS =    0x8DFD;
    public final static int SHADER_TYPE =                     0x8B4F;
    public final static int DELETE_STATUS =                   0x8B80;
    public final static int LINK_STATUS =                     0x8B82;
    public final static int VALIDATE_STATUS =                  0x8B83;
    public final static int ATTACHED_SHADERS =                 0x8B85;
    public final static int ACTIVE_UNIFORMS =                 0x8B86;
    public final static int ACTIVE_UNIFORM_MAX_LENGTH =       0x8B87;
    public final static int ACTIVE_ATTRIBUTES =               0x8B89;
    public final static int ACTIVE_ATTRIBUTE_MAX_LENGTH =     0x8B8A;
    public final static int SHADING_LANGUAGE_VERSION =        0x8B8C;
    public final static int CURRENT_PROGRAM =                 0x8B8D;

    /* Shader Binary */
    public final static int SHADER_BINARY_FORMATS = 0x8DF8;
    public final static int NUM_SHADER_BINARY_FORMATS = 0x8DF9;

    public final static int COMPILE_STATUS = 0x8B81;
    public final static int INFO_LOG_LENGTH = 0x8B84;
    public final static int SHADER_SOURCE_LENGTH = 0x8B88;
    public final static int SHADER_COMPILER = 0x8DFA;

    /* Buffer Objects */
    public final static int ARRAY_BUFFER =                  0x8892;
    public final static int ELEMENT_ARRAY_BUFFER =          0x8893;
    public final static int ARRAY_BUFFER_BINDING =          0x8894;
    public final static int ELEMENT_ARRAY_BUFFER_BINDING =  0x8895;

    public final static int STREAM_DRAW =                   0x88E0;
    public final static int STATIC_DRAW =                   0x88E4;
    public final static int DYNAMIC_DRAW =                  0x88E8;

    public final static int BUFFER_SIZE =                   0x8764;
    public final static int BUFFER_USAGE =                  0x8765;

    public final static int CURRENT_VERTEX_ATTRIB =         0x8626;


}
