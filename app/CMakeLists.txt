## For more information about using CMake with Android Studio, read the
## documentation: https://d.android.com/studio/projects/add-native-code.html
#
## Sets the minimum version of CMake required to build the native library.
#
#cmake_minimum_required(VERSION 3.4.1)
#
## Creates and names a library, sets it as either STATIC
## or SHARED, and provides the relative paths to its source code.
## You can define multiple libraries, and CMake builds them for you.
## Gradle automatically packages shared libraries with your APK.
#
#add_definitions(-std=c++11)
#
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
##设置生成的so动态库最后输出的路径
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
#
#include_directories(
#    src/main/cpp/include
#    )
#
#aux_source_directory(
#    src/main/cpp SRC_FILE
#    )
#
#
#add_library( # Sets the name of the library.
#             YuvUtils
#
#             # Sets the library as a shared library.
#             SHARED
#
#             # Provides a relative path to your source file(s).
#             ${SRC_FILE}
#             # src/main/cpp/native-lib.cpp
#              )
#
#add_library(
#             netsdk
#
#             STATIC  # 静态库
#
#             IMPORTED
#             )
#
#set_target_properties( # Specifies the target library.
#                       netsdk
#
#                       # Specifies the parameter you want to define.
#                       PROPERTIES IMPORTED_LOCATION    # 本地文件导入
#
#                       # Provides the path to the library you want to import.
#                      ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jni/libnetsdk.a ) # .a文件目录
#
#
## Searches for a specified prebuilt library and stores the path as a
## variable. Because CMake includes system libraries in the search path by
## default, you only need to specify the name of the public NDK library
## you want to add. CMake verifies that the library exists before
## completing its build.
#
#find_library( # Sets the name of the path variable.
#              log-lib
#
#              # Specifies the name of the NDK library that
#              # you want CMake to locate.
#              log )
#
## Specifies libraries CMake should link to your target library. You
## can link multiple libraries, such as libraries you define in this
## build script, prebuilt third-party libraries, or system libraries.
#
#target_link_libraries( # Specifies the target library.
#                       YuvUtils
#
#                       netsdk
#
#                       # Links the target library to the log library
#                       # included in the NDK.
#                       ${log-lib} )