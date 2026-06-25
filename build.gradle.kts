// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt)
}

detekt {
    config.setFrom("$rootDir/detekt.yml")
    source.setFrom("$rootDir/app/src/main/java")
    buildUponDefaultConfig = true
}

subprojects {
    afterEvaluate {
        tasks.matching { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }.configureEach {
            dependsOn(rootProject.tasks.named("detekt"))
        }
    }
}