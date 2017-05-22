APP_PLATFORM := android-19

APP_CPPFLAGS += -fexceptions
APP_CPPFLAGS += -Wformat

#APP_CFLAGS += -fno-strict-aliasing

#APP_STL := stlport_static
#APP_STL := gnustl_shared
APP_STL := gnustl_static

NDK_TOOLCHAIN_VERSION := clang
#APP_CXXFLAGS := -Wall -Werror -fpermissive

#APP_ABI := armeabi x86
APP_ABI := armeabi
APP_ABI += x86
APP_ABI += armeabi-v7a
#APP_ABI := all