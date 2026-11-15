plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // <--- ADICIONA ESTA LINHA AQUI
}

android {
    namespace = "com.example.meucontrole"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.meucontrole"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // AndroidX & Material UI
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Segurança & Biometria
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Firebase BoM (Gerencia todas as versões do Firebase)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")

    // Room Database (Para persistência local dos gastos)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Google ML Kit para Leitura Real de Cupom Fiscal (OCR) 📸
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Gráficos MPAndroidChart (Gráfico de Pizza) 📊
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}