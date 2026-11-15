plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    // Adiciona esta linha para o Firebase funcionar corretamente:
    id("com.google.gms.google-services") version "4.4.2" apply false
}