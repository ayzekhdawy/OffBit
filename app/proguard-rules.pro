# Add project specific ProGuard rules here.
# You can control the set of rules that apply to your project by
# editing the proguard-rules.pro file in the app directory.

# By default, the flags in this file are appended to flags specified
# in /Users/username/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# WebRTC classes must not be obfuscated
-keep class org.webrtc.** { *; }

# NanoHTTPD classes must not be obfuscated
-keep class fi.iki.elonen.** { *; }

# Gson uses reflection, so we need to keep its classes
-keep class com.google.gson.** { *; }
-keep class com.offbit.offbit.model.** { *; }

# Keep all Model classes
-keep class com.offbit.offbit.model.** { *; }

# Keep all Network classes
-keep class com.offbit.offbit.network.** { *; }

# Keep all WebRTC classes
-keep class com.offbit.offbit.webrtc.** { *; }

# Keep all Manager classes
-keep class com.offbit.offbit.manager.** { *; }

# Keep all Service classes
-keep class com.offbit.offbit.service.** { *; }

# Keep all Receiver classes
-keep class com.offbit.offbit.receiver.** { *; }

# Keep all Utils classes
-keep class com.offbit.offbit.utils.** { *; }

# Keep all Activity classes
-keep class com.offbit.offbit.**Activity { *; }

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclasseswithmembernames class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep JSON classes
-keep class org.json.** { *; }

# Keep SQLite classes
-keep class android.database.sqlite.** { *; }

# Keep annotation classes
-keepattributes *Annotation*

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep getters and setters
-keepclassmembers class * {
    public void set*(***);
    public *** get*();
}

# Keep callback methods
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
