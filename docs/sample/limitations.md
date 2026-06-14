# Known Sample Limitations

## Ktorfit and Kotlin Compiler Compatibility

The sample currently uses Ktorfit `2.7.3` with Kotlin `2.4.0`. Ktorfit code
generation can fail during sample compilation with a compiler-plugin
`ClassCastException`. Library modules do not use Ktorfit and are validated
independently.

Until that compatibility is resolved, treat `ciSamples` as a separate signal
and inspect whether the failure occurs in generated networking code before
attributing it to Arch Toolkit.

## Web Persistence

Web targets intentionally use `MemoryStoreProvider`. Settings are lost when the
page process is restarted.

## Browser Targets

WasmJS is active. The plain JavaScript browser target is present as commented
build configuration and is not part of the current run path.

## External Services

GitHub API availability, rate limits, and network failures can affect runtime
behavior even when the sample compiles correctly.
