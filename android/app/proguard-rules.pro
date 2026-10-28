# Keep JNI entry points when librime_session is linked later.
-keepclasseswithmembernames class * {
    native <methods>;
}
