plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "dam.pmdm.tarea6_gutierrezruiz_francisco"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "dam.pmdm.tarea6_gutierrezruiz_francisco"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

secrets {
    // Especifica un nombre diferente para el archivo que contiene las claves secretas porque el plugin usa por defecto "local.properties"
    propertiesFileName = "secrets.properties"

    // El archivo de propiedades que contiene valores secretos por defecto. Este archivo puede subirse al control de versiones.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configura qué claves deben ser ignoradas por el plugin usando expresiones regulares.
    ignoreList.add("keyToIgnore") // Ignora la clave "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignora todas las claves que coincidan con la expresión regular "sdk.*"
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)
}