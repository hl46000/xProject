APP_STL := gnustl_static
APP_ABI := armeabi 
#APP_ABI += x86
#APP_ABI += armeabi-v7a 
#APP_ABI += arm64-v8a

#NDK_TOOLCHAIN_VERSION := clang

APP_CPPFLAGS += -std=c++11
APP_CPPFLAGS += -fexceptions -fpermissive -Wunused-parameter
#APP_CPPFLAGS += -fno-integrated-as
#APP_CPPFLAGS += -frtti
#APP_CPPFLAGS += -marm -fno-omit-frame-pointer
#APP_CPPFLAGS += -mthumb
#APP_CFLAGS 	 += -marm -fno-omit-frame-pointer
#APP_CFLAGS 	 += -fno-integrated-as
