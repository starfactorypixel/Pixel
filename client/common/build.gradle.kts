plugins {
    id("ru.starfactory.convention.preset.client-feature")
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":core:apps"))
                api(project(":core:compose"))
                api(project(":core:coroutines"))
                api(project(":core:decompose"))
                api(project(":core:di"))
                api(project(":core:key-value-storage"))
                api(project(":core:navigation"))
                api(project(":core:uikit"))

                api(project(":feature:apps"))
                api(project(":feature:dashboard-screen"))
                api(project(":feature:main-screen:impl"))
                api(project(":feature:theming"))
            }
        }
    }
}