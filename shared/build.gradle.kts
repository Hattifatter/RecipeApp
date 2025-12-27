import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization) // ✅ важно для @Serializable
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    iosArm64()
    iosSimulatorArm64()

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)

            // ✅ Ktor client (MPP)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)

            // ✅ ВАЖНО: берём MPP, а не -jvm
            implementation(libs.ktor.serialization.kotlinx.json.mpp)
        }

        androidMain.dependencies {
            // ✅ engine для Android (иначе будет "Failed to find HTTP client engine...")
            implementation(libs.ktor.client.android)
        }

        // Если iosMain у тебя есть (обычно есть) — так:
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        // Если вдруг iosMain НЕ создаётся у тебя, скажи — дам вариант для iosArm64Main/iosSimulatorArm64Main.

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "ru.recipeapp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
