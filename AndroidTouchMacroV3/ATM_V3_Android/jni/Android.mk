LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PRELINK_MODULE := false

LOCAL_MODULE    := atm_v3
LOCAL_SRC_FILES := atm_v3.cpp com_cust_android_screencap_ScreenCap.cpp

LOCAL_LDLIBS	+= -llog -ldl -lz
LOCAL_SHARED_LIBRARIES := \
    libcutils \
    libutils \
	libbinder \
	libskia \
	libui\
	libsurfaceflinger_client

LOCAL_C_INCLUDES += \
        external/skia/include/core \
        external/skia/include/effects \
        external/skia/include/images \
        external/skia/src/ports \
        external/skia/include/utils

LOCAL_MODULE_TAGS := eng

include $(BUILD_SHARED_LIBRARY)
