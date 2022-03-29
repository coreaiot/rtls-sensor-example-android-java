#include <jni.h>
#include "sensor.h"

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_rtls_1sensor_1example_1android_1java_MainActivity_coreaiot_1generate_1manufacturer_1specific_1data(
        JNIEnv *env, jobject thiz, jshort id, jbyte alarm, jbyte battery, jbyte advertise_mode,
        jbyte tx_power_level, jbyte channel) {
    uint8_t *buff = coreaiot_generate_manufacturer_specific_data(
    id, alarm, battery, advertise_mode,
            tx_power_level, channel);
    jbyteArray arr = env->NewByteArray(COREAIOT_MANUFACTURER_SPECIFIC_DATA_LENGTH);
    env->SetByteArrayRegion(
            arr, 0, COREAIOT_MANUFACTURER_SPECIFIC_DATA_LENGTH, (jbyte*)buff);
    return arr;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_rtls_1sensor_1example_1android_1java_MainActivity_coreaiot_1get_1manufacturer_1id(
        JNIEnv *env, jobject thiz) {
    return COREAIOT_MANUFACTURER_ID;
}