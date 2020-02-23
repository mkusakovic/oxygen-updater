const val KOTLIN_VERSION = "1.3.61"

object BuildPlugins {
    private object Versions {
        const val BUILD_TOOLS_VERSION = "3.5.3"
        const val FABRIC_GRADLE_PLUGIN = "1.31.1"
        const val GOOGLE_SERVICES_PLUGIN = "4.3.3"
    }

    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:${Versions.BUILD_TOOLS_VERSION}"
    const val FABRIC_GRADLE_PLUGIN = "io.fabric.tools:gradle:${Versions.FABRIC_GRADLE_PLUGIN}"
    const val GOOGLE_SERVICES_PLUGIN = "com.google.gms:google-services:${Versions.GOOGLE_SERVICES_PLUGIN}"
    const val KOTLIN_GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"

    const val ANDROID_APPLICATION = "com.android.application"
    const val FABRIC = "io.fabric"
    const val GOOGLE_SERVICES = "com.google.gms.google-services"

    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_ANDROID_EXTENSIONS = "kotlin-android-extensions"
    const val KOTLIN_KAPT = "kotlin-kapt"
}

object AndroidSdk {
    const val MIN = 21
    const val COMPILE = 29
    const val TARGET = COMPILE
}

// AndroidX: https://developer.android.com/jetpack/androidx/releases
object AndroidXLibraries {
    private object Versions {
        const val APP_COMPAT = "1.1.0"
        const val BROWSER = "1.2.0"
        const val CONSTRAINT_LAYOUT = "2.0.0-beta4"
        const val KTX_CORE = "1.2.0"
        const val KTX_FRAGMENT = "1.2.1"
        const val KTX_LIFECYCLE = "2.2.0"
        const val KTX_PREFERENCE = "1.1.0"
        const val RECYCLER_VIEW = "1.1.0"
    }

    // https://developer.android.com/jetpack/androidx/releases/appcompat
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    // https://developer.android.com/jetpack/androidx/releases/browser
    const val BROWSER = "androidx.browser:browser:${Versions.BROWSER}"
    // https://developer.android.com/jetpack/androidx/releases/constraintlayout
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"

    // https://developer.android.com/jetpack/androidx/releases/core
    const val KTX_CORE = "androidx.core:core-ktx:${Versions.KTX_CORE}"
    // https://developer.android.com/jetpack/androidx/releases/fragment
    const val KTX_FRAGMENT = "androidx.fragment:fragment-ktx:${Versions.KTX_FRAGMENT}"
    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    const val KTX_LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.KTX_LIFECYCLE}"
    const val KTX_LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.KTX_LIFECYCLE}"
    // https://developer.android.com/jetpack/androidx/releases/preference
    const val KTX_PREFERENCE = "androidx.preference:preference-ktx:${Versions.KTX_PREFERENCE}"
}

object Libraries {
    private object Versions {
        const val A_FILE_CHOOSER = "1.0.1"
        const val CHAINFIRE_LIBSUPERUSER = "1.1.0.201907261845"
        const val CRASHLYTICS = "2.10.1"
        const val FACEBOOK_SHIMMER = "0.5.0"
        const val FIREBASE_ADS = "18.3.0"
        const val FIREBASE_MESSAGING = "20.1.0"
        const val GLIDE = "4.11.0"
        const val JACKSON_KOTLIN_MODULE = "2.10.2"
        const val JODA_CONVERT = "2.2.1"
        const val JODA_TIME = "2.10.5"
        const val KOIN = "2.0.1"
        const val KOIN_UNSTABLE = "2.1.0-beta-1"
        const val MATERIAL = "1.2.0-alpha05"
        const val OKHTTP_LOGGING_INTERCEPTOR = "4.3.1"
        const val PLAY_SERVICES_BASE = "17.1.0"
        const val PR_DOWNLOADER = "0.6.0"
        const val RETROFIT = "2.7.1"
    }

    const val KOTLIN_STDLIB_JDK8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$KOTLIN_VERSION"

    // Google Material Components for Android
    // https://github.com/material-components/material-components-android/releases
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"

    // Google Firebase
    // https://firebase.google.com/support/release-notes/android
    const val CRASHLYTICS = "com.crashlytics.sdk.android:crashlytics:${Versions.CRASHLYTICS}"
    const val FIREBASE_ADS = "com.google.firebase:firebase-ads:${Versions.FIREBASE_ADS}"
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging:${Versions.FIREBASE_MESSAGING}"
    const val PLAY_SERVICES_BASE = "com.google.android.gms:play-services-base:${Versions.PLAY_SERVICES_BASE}"

    // Koin for Android
    // https://doc.insert-koin.io/#/setup/index
    const val KOIN = "org.koin:koin-android:${Versions.KOIN}"
    const val KOIN_FRAGMENT = "org.koin:koin-androidx-fragment:${Versions.KOIN_UNSTABLE}"
    const val KOIN_SCOPE = "org.koin:koin-androidx-scope:${Versions.KOIN}"
    const val KOIN_VIEWMODEL = "org.koin:koin-androidx-viewmodel:${Versions.KOIN_UNSTABLE}"

    // https://github.com/square/retrofit/releases
    const val OKHTTP_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP_LOGGING_INTERCEPTOR}"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val RETROFIT_CONVERTER_JACKSON = "com.squareup.retrofit2:converter-jackson:${Versions.RETROFIT}"

    // Note: Before updating version, make sure this library"s kotlin-reflect dependency"s version is the same as kotlin-stdlib"s version above
    // Check https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin/<version>
    const val JACKSON_KOTLIN_MODULE = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_KOTLIN_MODULE}"

    // https://github.com/bumptech/glide/releases
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"

    // https://github.com/facebook/shimmer-android/releases
    const val FACEBOOK_SHIMMER = "com.facebook.shimmer:shimmer:${Versions.FACEBOOK_SHIMMER}"

    // https://www.joda.org/joda-convert/changes-report.html
    const val JODA_CONVERT = "org.joda:joda-convert:${Versions.JODA_CONVERT}"
    // https://www.joda.org/joda-time/changes-report.html
    const val JODA_TIME = "joda-time:joda-time:${Versions.JODA_TIME}"

    // https://github.com/MindorksOpenSource/PRDownloader/releases
    const val PR_DOWNLOADER = "com.mindorks.android:prdownloader:${Versions.PR_DOWNLOADER}"

    // https://github.com/Chainfire/libsuperuser/releases
    const val CHAINFIRE_LIBSUPERUSER = "eu.chainfire:libsuperuser:${Versions.CHAINFIRE_LIBSUPERUSER}"

    // https://github.com/iPaulPro/aFileChooser
    const val A_FILE_CHOOSER = "andhradroid.dev:aFilechooser:${Versions.A_FILE_CHOOSER}"
}

object TestLibraries {
    private object Versions {
        const val ANNOTATION = "1.1.0"
        const val ESPRESSO = "3.2.0"
        const val JUNIT_EXT = "1.1.1"
        const val JUNIT4 = "4.13"
        const val KOIN = "2.0.1"
        const val RULES = "1.2.0"
        const val RUNNER = "1.2.0"
    }

    const val JUNIT4 = "junit:junit:${Versions.JUNIT4}"
    // https://developer.android.com/jetpack/androidx/releases/test
    const val JUNIT_EXT = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"

    const val KOTLIN_TEST_JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:$KOTLIN_VERSION"

    const val KOIN_TEST = "org.koin:koin-test:${Versions.KOIN}"

    // https://developer.android.com/jetpack/androidx/releases/test
    const val ANNOTATION = "androidx.annotation:annotation:${Versions.ANNOTATION}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
    const val RULES = "androidx.test:rules:${Versions.RULES}"
    const val RUNNER = "androidx.test:runner:${Versions.RUNNER}"
}