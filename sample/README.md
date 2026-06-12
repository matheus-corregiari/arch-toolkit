# Arch Toolkit Sample

Compose Multiplatform sample for Android, desktop, and WasmJS.

```bash
./gradlew -PincludeSamples=true :sample:target:android:assembleDebug
./gradlew -PincludeSamples=true :sample:target:desktop:run
./gradlew -PincludeSamples=true :sample:target:web:wasmJsBrowserDevelopmentRun
```

See the [sample guide](https://matheus-corregiari.github.io/arch-toolkit/sample/)
for architecture, demonstrated flows, and known dependency limitations.
