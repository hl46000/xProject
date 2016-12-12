LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := sample00
LOCAL_SRC_FILES := sample00.cpp jstr2str.cpp

LOCAL_CFLAGS    := -Wall -Wextra
LOCAL_LDLIBS	+= -llog -ldl -lz -lGLESv2
 
LOCAL_DEX_PREOPT := true

include $(BUILD_SHARED_LIBRARY)
