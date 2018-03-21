#include <jni.h>

JNIEXPORT jint JNICALL Java_cn_krvision_mynavidemo_JniKit_helloFromC
  (JNIEnv * env, jobject thiz,jdouble a,jdouble b){

	return a+b;

}


