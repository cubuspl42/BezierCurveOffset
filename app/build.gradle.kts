plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
            implementation(libs.batik.anim)
            implementation(libs.batik.svg.dom)
            implementation(libs.batik.css)
            implementation(libs.commons.math3)
        }

        jvmTest.dependencies {
        }
    }
}
