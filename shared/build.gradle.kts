import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}



// SQLDelight 2.0.2 has no wasmJs or js NPM artifact.
// Exclude it from all configurations that are used for NPM/WASM dependency resolution
// so Gradle does not try to resolve a non-existent wasmJs variant.
configurations.all {
    if (name.contains("wasmJs", ignoreCase = true) || name.contains("NpmAggregated", ignoreCase = true)) {
        exclude(group = "app.cash.sqldelight")
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    js {
        browser()
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        // Intermediate source set shared by all non-web targets (android, ios, jvm).
        // SQLDelight 2.0.2 does not publish a wasmJs/js artifact, so we must keep it
        // out of commonMain (which includes wasmJs and js).
        val nonWebMain by creating {
            dependsOn(commonMain.get())
        }

        androidMain.get().dependsOn(nonWebMain)
        iosMain.get().dependsOn(nonWebMain)
        jvmMain.get().dependsOn(nonWebMain)

        // Explicitly wire iOS targets since default hierarchy template is disabled
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosArm64Main.dependsOn(iosMain.get())
        iosSimulatorArm64Main.dependsOn(iosMain.get())

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.animation)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        nonWebMain.dependencies {
            // SQLDelight runtime + coroutines: Android, iOS, JVM only
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            implementation(libs.androidx.workmanager)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.kotlinx.coroutinesSwing)
        }

        // wasmJsMain and jsMain use InMemoryExamRepository via DI — no SQLDelight driver needed
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.exam.countdown.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("ExamDatabase") {
            packageName.set("com.exam.countdown.database")
        }
    }
    linkSqlite.set(false)
}