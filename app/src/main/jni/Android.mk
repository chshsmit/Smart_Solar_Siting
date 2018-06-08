LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
#opencv
OPENCVROOT:= /Users/Sam/Desktop/116/OpenCV-android-sdk/sdk/native/jni
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include /Users/Sam/Desktop/116/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
LOCAL_SRC_FILES := solarsitingucsc_smartsolarsiting_Controller_NativePanorama.cpp
LOCAL_LDLIBS += -llog
LOCAL_MODULE := MyLib
include $(BUILD_SHARED_LIBRARY)