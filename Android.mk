LOCAL_PATH := $(call my-dir)

#$(shell mkdir $(TARGET_OUT)/media/wallpapers/)
#$(shell mkdir $(TARGET_OUT)/media/wallpapers/original -p)
#$(shell mkdir $(TARGET_OUT)/media/wallpapers/thumbnail -p)
#$(shell cp -rf ./vendorshly/wallpapers/original/* $(TARGET_OUT)/media/wallpapers/original/)
#$(shell cp -rf ./vendorshly/wallpapers/thumbnail/* $(TARGET_OUT)/media/wallpapers/thumbnail/)

include $(CLEAR_VARS)
LOCAL_MODULE := ShlyMirror
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := ShlyMirror.apk
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_PATH := $(TARGET_OUT)/app
include $(BUILD_PREBUILT)
