OpenGLEN
========

An OpenGL (ES) enabler for multiple platforms.

OpenGLEN currently has support for 2 different platforms.

* Android
There is an Android abstraction in Android-OpenGLEN containing the necessary classes to connect OpenGLEN to an Android (surface)view.
Lowest API level is set to Gingerbread but it should be possible to get it running on older versions if vertex buffer objects are not used.

* Java platforms with JOGAMP
There is an abstraction built on JOGAMP in J2SE-OpenGLEN, this contains the necessary classes to connect OpenGLEN to the jogamp window system.
Tested on Windows and Ubuntu, there is an issue on Ubuntu if particles is used. 

Clone the repository and setup import as 3 projects into Eclipse (OpenGLEN, J2SE-OpenGLEN and Android-OpenGLEN).
You will need to link J2SE-OpenGLEN to JOGAMP (I did it by adding the JOGAMP jar file to project properties, make sure to link platform files)
Android project should be marked as a library.
 