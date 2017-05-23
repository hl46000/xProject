LOCAL_PATH := $(call my-dir)
STATIC_LOCAL_PATH := $(LOCAL_PATH)
 
include $(CLEAR_VARS)

LOCAL_MODULE    := MainSimple
LOCAL_SRC_FILES := MainSimple.cpp \
					./util/log.cpp \
					./smc/smc_module.cpp \
					./util/util.cpp \
					./util/file_observer.cpp \
					./util/device_info.cpp \
					./zlib/Unzipper.cpp \
					./zlib/zlib_helper.cpp \
					./zlib/minizip/unzip.c \
					./zlib/minizip/ioapi.c \
					./util/apk_hash_generator.cpp
					
LOCAL_CFLAGS	+= -DOS_ANDROID -DFILE_OFFSET_BITS=64 -DLARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64 -D_LARGEFILE64_SOURCE -Wno-psabi -O1
LOCAL_CFLAGS	+= -g -DBOOST_PP_VARIADICS=1 -DIOAPI_NO_64 -s -DUNITY_PLUGIN_BUILD
LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/util -I$(STATIC_LOCAL_PATH)/smc -I$(STATIC_LOCAL_PATH)/openssl/include
LOCAL_CFLAGS	+= -fvisibility=hidden

LOCAL_STATIC_LIBRARIES := libcrypto-static

LOCAL_LDLIBS	+= -llog -lz -latomic
#include $(BUILD_SHARED_LIBRARY)

##############################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := ash
LOCAL_SRC_FILES := AntiSpeedHack/AntiSpeedHackMain.cpp AntiSpeedHack/game_process_main.cpp AntiSpeedHack/child_process_main.cpp
LOCAL_CFLAGS	+= -DOS_ANDROID -DFILE_OFFSET_BITS=64 -DLARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64 -D_LARGEFILE64_SOURCE -Wno-psabi -O1
#LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/util -I$(STATIC_LOCAL_PATH)/smc
LOCAL_CFLAGS	+= -fvisibility=hidden

LOCAL_CFLAGS	+= -DANDROID_ARM_LINKER

LOCAL_LDLIBS	+= -llog -lz -latomic
include $(BUILD_SHARED_LIBRARY)


##############################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := MainHooking
LOCAL_SRC_FILES := MainHooking.cpp ./util/log.cpp ./hooking/hooking.cpp
LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/util
#LOCAL_CFLAGS	+= -fvisibility=hidden

LOCAL_CFLAGS	+= -DANDROID_ARM_LINKER

LOCAL_LDLIBS	+= -llog
#include $(BUILD_SHARED_LIBRARY)

##############################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := MainInjection
LOCAL_SRC_FILES := MainInjection.cpp ./util/log.cpp ./util/util.cpp ./util/simple_http.cpp ./injection/injectionPLT.cpp 
LOCAL_SRC_FILES += ./injection/injection_detecter.cpp ./util/maps_reader.cpp
LOCAL_SRC_FILES += ./injection/injection_detecter_not_read_maps.cpp

LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/util
#LOCAL_CFLAGS	+= -fvisibility=hidden

LOCAL_CFLAGS	+= -DANDROID_ARM_LINKER
LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/openssl/include -I$(STATIC_LOCAL_PATH)/curl/include 
LOCAL_STATIC_LIBRARIES := libcurl libssl-static libcrypto-static 

LOCAL_LDLIBS	+= -llog -lz -latomic
#include $(BUILD_SHARED_LIBRARY)


##############################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := probe
LOCAL_SRC_FILES := ./probe/probe_main.cpp ./probe/probe.cpp ./probe/probe_lua.cpp
LOCAL_SRC_FILES += ./util/log.cpp ./util/maps_reader.cpp ./util/simple_http.cpp ./util/status_reader.cpp ./smc/smc_module.cpp
LOCAL_CFLAGS	+= -I$(STATIC_LOCAL_PATH)/openssl/include -I$(STATIC_LOCAL_PATH)/curl/include -I$(STATIC_LOCAL_PATH)/lua -I$(STATIC_LOCAL_PATH)/smc
#LOCAL_CFLAGS	+= -fvisibility=hidden 
LOCAL_LDLIBS	+= -llog -lz -latomic
LOCAL_STATIC_LIBRARIES := liblua libcurl libssl-static libcrypto-static 

#include $(BUILD_EXECUTABLE)



##############################################################
#include $(CLEAR_VARS)
#
#LOCAL_MODULE    := test_tool
#LOCAL_SRC_FILES := test_tool.cpp probe_log.cpp
#LOCAL_CFLAGS	+= -fvisibility=hidden 
#LOCAL_LDLIBS	+= -llog -lz -latomic
# 
#include $(BUILD_EXECUTABLE)

##############################################################
#include $(STATIC_LOCAL_PATH)/lua/Android.mk
#include $(STATIC_LOCAL_PATH)/openssl/Android.mk
#include $(STATIC_LOCAL_PATH)/curl/Android.mk

