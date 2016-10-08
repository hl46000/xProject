LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := atm_v3
LOCAL_SRC_FILES := atm_v3.cpp

LOCAL_LDLIBS	+= -llog -ldl -lz

include $(BUILD_SHARED_LIBRARY)
