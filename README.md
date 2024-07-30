# Video player for Android

### Build Application

VLC-Android build relies on gradle build modes :

- `Release` & `Debug` will get LibVLC and Medialibrary from Bintray, and build application source code only.
- `SignedRelease` also, but it will allow you to sign application apk with a local keystore.
- `Dev` will build build LibVLC, Medialibrary, and then build the application with these binaries. (via build scripts only)

Setup the build environment:
Set `$ANDROID_SDK` to point to your Android SDK directory
`export ANDROID_SDK=/path/to/android-sdk`

Set `$ANDROID_NDK` to point to your Android NDK directory
`export ANDROID_NDK=/path/to/android-ndk`

Then, you are ready to build!