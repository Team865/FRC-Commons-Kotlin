# FRC-Commons-Kotlin

A Kotlin library for common FRC math and control flow

[![Build](https://travis-ci.org/Team865/FRC-Commons-Kotlin.svg?branch=master)](https://travis-ci.org/Team865/FRC-Commons-Kotlin)
[![Release](https://jitpack.io/v/Team865/FRC-Commons-Kotlin.svg)](https://jitpack.io/#Team865/FRC-Commons-Kotlin)

### Usage

#### Robot Project (Vendor File)

Copy this code into `Commons.json` in the `vendordeps` folder of a robot project:

```json
{
    "fileName": "Commons.json",
    "name": "FRC-Commons-Kotlin",
    "version": "2019.8.0",
    "uuid": "ab676553-b602-441f-a38d-f1296eff6538",
    "mavenUrls": [
        "https://jitpack.io"
    ],
    "jsonUrl": "",
    "javaDependencies": [
        {
            "groupId": "com.github.Team865",
            "artifactId": "FRC-Commons-Kotlin",
            "version": "2019.8.0"
        }
    ],
    "jniDependencies": [],
    "cppDependencies": []
}
```

#### Non-Robot Gradle Project

Use the following in the `build.gradle.kts` file

```kotlin
repositories {
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    implementation(group="com.github.Team865", name="FRC-Commons-Kotlin", version="2019.8.0")
}
```