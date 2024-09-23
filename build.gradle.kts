
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.kapt") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}
