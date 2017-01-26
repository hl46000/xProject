LOCAL_PATH := $(call my-dir)

####################################################################################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := AppSealingExternalExecutor
LOCAL_SRC_FILES := AppSealingExternalExecutor.cpp util/maps_reader.cpp

LOCAL_CFLAGS	+= -I./ -I../
LOCAL_LDLIBS	+= -llog -ldl -lz

include $(BUILD_SHARED_LIBRARY)
####################################################################################################################
#include $(CLEAR_VARS)
#
#LOCAL_MODULE    := attacher
#LOCAL_SRC_FILES := attacher.cpp
#
#LOCAL_LDLIBS	+= -llog -ldl -lz
#
#include $(BUILD_EXECUTABLE)
####################################################################################################################
#include $(CLEAR_VARS)
#
#LOCAL_MODULE    := attach
#LOCAL_SRC_FILES := attach/attach.cpp
#
#LOCAL_LDLIBS	+= -llog -ldl -lz
#
#include $(BUILD_SHARED_LIBRARY)