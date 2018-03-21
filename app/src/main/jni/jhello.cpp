//
// Created by Administrator on 2018/3/21.
//
#include <jni.h>
#include "hello.h"
extern "C"
{
JNIEXPORT jlong JNICALL Java_cn_krvision_mynavidemo_JniKit_createNativeObject
(JNIEnv * env, jobject obj){
    jlong result = 0;
    result = (jlong) new gaoqiong();

    return result;
}

JNIEXPORT jboolean JNICALL Java_cn_krvision_mynavidemo_JniKit_CheckSB
(JNIEnv * env, jobject obj,jlong thiz){

    return ((gaoqiong*)thiz)->CheckSB();
}
}