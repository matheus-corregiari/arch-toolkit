# Running the Sample

## Enable Sample Projects

Every command must include:

```bash
-PincludeSamples=true
```

## Android

Open the project in Android Studio, select the Android sample configuration, or
build it from the command line:

```bash
./gradlew -PincludeSamples=true :sample:target:android:assembleDebug
```

Install and launch the generated application through Android Studio or `adb`.

## Desktop

```bash
./gradlew -PincludeSamples=true :sample:target:desktop:run
```

## Web

The active browser target is WasmJS:

```bash
./gradlew -PincludeSamples=true :sample:target:web:wasmJsBrowserDevelopmentRun
```

The JavaScript browser target is currently disabled in the web target build
script.

## Assemble All Samples

```bash
./gradlew -PincludeSamples=true ciSamples
```

Check [known limitations](limitations.md) when a sample build fails while the
library build remains green.
