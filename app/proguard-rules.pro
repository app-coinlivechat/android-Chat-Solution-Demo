# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Signature,Exceptions,*Annotation*,
                InnerClasses,PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable


-keepclassmembers class com.coinlive.chat.Coinlive {*;}

-keep class com.coinlive.chat.firebase.CoinliveChat {*;}
-keep class com.coinlive.chat.firebase.model.** {*;}
-keep interface com.coinlive.chat.firebase.listener.** {*;}
-keep class com.coinlive.chat.firebase.service.CoinliveAuthentication {*;}

-keep class com.coinlive.chat.exception.** {*;}

-keep class com.coinlive.chat.api.model.** {*;}
-keep class com.coinlive.chat.api.CoinliveRestApi {*;}
-keep class com.coinlive.chat.api.ResponseCallback {*;}


# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *



