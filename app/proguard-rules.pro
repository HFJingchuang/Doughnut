# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Sdk/tools/proguard/proguard-android.txt
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

-dontshrink    #不压缩输入的类文件
-dontoptimize    #不优化输入的类文件
-dontskipnonpubliclibraryclasses

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {                                           # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {                                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {                                           # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {                        # 保持自定义控件类不被混淆
   public void *(android.view.View);
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {                                                       # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {                                   # 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {                                           # eventbus
    java.lang.String TAG;
}

#引入各个jar包
-library libs/gson-2.3.1.jar(!META-INF/MANIFEST.MF)

-library libs/universal-image-loader-1.9.3.jar(!META-INF/MANIFEST.MF)


-dontwarn com.xiaomi.**
-keep class com.xiaomi.**{*;}

-dontwarn com.w3c.dom.**
-keep class com.w3c.dom.**{*;}

-dontwarn org.w3c.dom.**
-keep class org.w3c.dom.**{*;}

-dontwarn com.google.appengine.**
-keep class com.google.appengine.**{*;}


-dontwarn sun.misc.**
-keep class sun.misc.**{*;}

-dontwarn java.util.concurrent.**
-keep class java.util.concurrent.**{*;}
-keep class java.util.TimerTask.**{*;}

-dontwarn android.support.v4.**
-keep class android.support.v4.**{*;}
-keep interface android.support.v4.**{*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragemnt

-keep class com.fasterxml.jackson.**{*;}
-dontwarn com.fasterxml.jackson.**

-keep public class [doughnut].R$*{
    public static final int *;
}

# # -------------------------------------------
# #  ############### volley混淆  ###############
# # -------------------------------------------
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }


-keep class **$Properties

## ----------------------------------
##   ########## Gson混淆    ##########
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
## ----------------------------------
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }


-keep class doughnut.base.** { *; }

-keepattributes *JavascriptInterface*

-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**

-keepattributes *Annotation*

-keep class org.android.** {*;}

-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

 -keepclassmembers class ** {
     @org.greenrobot.eventbus.Subscribe <methods>;
 }
 -keep enum org.greenrobot.eventbus.ThreadMode { *; }

 # Only required if you use AsyncExecutor
 -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
     <init>(java.lang.Throwable);
 }

-keep class com.googlecode.openbeans.** { *; }
-dontwarn com.googlecode.openbeans.**
-keep class org.apache.harmony.** { *; }
-dontwarn org.apache.harmony.**
-keep class org.bouncycastle.jce.** { *; }
-dontwarn org.bouncycastle.jce.**
-keep class org.spongycastle.jce.** { *; }
-dontwarn org.spongycastle.jce.**
-keep class org.spongycastle.x509.** { *; }
-dontwarn org.spongycastle.x509.**
-keep class org.bouncycastle.x509.** { *; }
-dontwarn org.bouncycastle.x509.**
-keep class net.i2p.crypto.x509.** { *; }
-dontwarn net.i2p.crypto.**
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.**

#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.android.jtblk.** { *;}

-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

-keepclassmembers class com.doughnut.web.JsNativeBridge{ *; }

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-keep class m.framework.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
-dontwarn **.R$*