pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TirupatiPos"

include(
    ":app",
    ":core",
    ":domain",
    ":data",
    ":feature-auth",
    ":feature-dashboard",
    ":feature-products",
    ":feature-billing",
    ":feature-inventory",
    ":feature-customers",
    ":feature-suppliers",
    ":feature-reports",
    ":feature-settings"
)
