LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
APP_PLATFORM := android-8

LOCAL_MODULE    := LazencaS
LOCAL_SRC_FILES := lazencaS/detect/Engine.cpp

LOCAL_CFLAGS    := -Wall -Wextra -Wreorder -Ijni/lazencaS
LOCAL_LDLIBS 	:= -llog -landroid -lz

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

APP_PLATFORM := android-8

LOCAL_MODULE    := LoadEngine
LOCAL_SRC_FILES := lazencaS/LoadEngine.cpp

LOCAL_CFLAGS    := -fvisibility=hidden -Ijni/lazencaS
LOCAL_LDLIBS := -llog -landroid -lz

include $(BUILD_SHARED_LIBRARY)


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
#ifeq ($(TARGET_ARCH_ABI),x86)
#else
#LOCAL_ARM_MODE := arm 
#endif
#
#LOCAL_DEX_PREOPT := true
#include $(BUILD_SHARED_LIBRARY)
#
#include $(CLEAR_VARS)
#LOCAL_MODULE    := sample03_1
#LOCAL_SRC_FILES := ./sample03/sample03_1.cpp
#
#LOCAL_CFLAGS    := -Wall -Wextra -Wreorder
#LOCAL_LDLIBS	+= -llog -ldl -lz
#
#include $(BUILD_SHARED_LIBRARY)



#include $(CLEAR_VARS)
#LOCAL_MODULE    := hooking
#LOCAL_SRC_FILES := ./hooking/MainGotHooking.cpp ./util/util.cpp ./hooking/elf_file.cpp ./hooking/got_hook.cpp ./hooking/plt_hooking.cpp
#
#LOCAL_CFLAGS    := -Wall -Wextra -Wreorder -Wunused-parameter
#LOCAL_CFLAGS	+= -DANDROID_ARM_LINKER
#LOCAL_CFLAGS	+= -Ijni/util/ -Ijni/hooking
#
#LOCAL_LDLIBS	+= -Ljni/hooking/lib/armeabi
#LOCAL_LDLIBS	+= -llog -ldl -lz
#
#LOCAL_STATIC_LIBRARIES := elf_module
#
#include $(BUILD_SHARED_LIBRARY)
#
#include $(CLEAR_VARS)
#LOCAL_MODULE    := sample
#LOCAL_SRC_FILES := ./hooking/sample.cpp
#
#LOCAL_LDLIBS	+= -llog -ldl -lz
#include $(BUILD_SHARED_LIBRARY)