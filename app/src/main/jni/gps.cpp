//
// Created by Administrator on 2018/3/21.
//

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

//extern "C"
//{

//}

 jdouble Java_cn_krvision_mynavidemo_JniKit3_add(JNIEnv* env, jobject thiz,jdouble a,jdouble b){
        return a+b;
    }