LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
#LOCAL_MODULE    := sample01
#LOCAL_SRC_FILES := sample01.cpp
#
#LOCAL_CFLAGS    := -Wall -Wextra -Wreorder
#LOCAL_LDLIBS	+= -llog -ldl -lz
# 
#LOCAL_DEX_PREOPT := true
#include $(BUILD_SHARED_LIBRARY)
#
#
#include $(CLEAR_VARS)
#LOCAL_MODULE    := sample00
#LOCAL_SRC_FILES := sample00.cpp jstr2str.cpp maps_reader.cpp
#
#LOCAL_CFLAGS    := -Wall -Wextra -Wreorder
#LOCAL_LDLIBS	+= -llog -ldl -lz
# 
#LOCAL_DEX_PREOPT := true
#include $(BUILD_SHARED_LIBRARY)


#include $(CLEAR_VARS)
#LOCAL_MODULE    := sample03
#LOCAL_SRC_FILES := ./sample03/sample03.cpp ./sample03/AppSealingSecurity_Unreal.cpp
#
#LOCAL_CFLAGS    := -Wall -Wextra -Wreorder
#LOCAL_LDLIBS	+= -llog -ldl -lz
#
#LOCAL_DEX_PREOPT := true
#include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := hooking
LOCAL_SRC_FILES := ./hooking/MainHooking.cpp ./hooking/hooking.cpp ./util/util.cpp

LOCAL_CFLAGS    := -Wall -Wextra -Wreorder -Wunused-parameter
LOCAL_CFLAGS	+= -DANDROID_ARM_LINKER -I./util
LOCAL_LDLIBS	+= -llog -ldl -lz

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := sample
LOCAL_SRC_FILES := ./hooking/sample.cpp

LOCAL_LDLIBS	+= -llog -ldl -lz

include $(BUILD_SHARED_LIBRARY)