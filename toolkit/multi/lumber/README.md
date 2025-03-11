# Lumber - A Lightweight Logging Library for Android and Kotlin Multiplatform (KMP)

Lumber is a flexible and straightforward logging library designed for Android and Kotlin Multiplatform (KMP), inspired by the excellent [Timber](https://github.com/JakeWharton/timber) library by Jake Wharton. This library allows you to log messages with various levels of priority, such as `Verbose`, `Debug`, `Info`, `Warn`, `Error`, and `Assert`.

## Example Usage:

```kotlin
// Plant a custom Oak (logging tree)
Lumber.plant(MyCustomOak())

// Log messages with different levels
Lumber.debug("Debug message")
Lumber.error(Throwable("Exception"), "An error occurred!")
```

## Features:
- **Multiplatform Support**: Works on Android and Kotlin Multiplatform (KMP) projects.
- **Flexible Logging Levels**: Logs at `Verbose`, `Debug`, `Info`, `Warn`, `Error`, and `Assert` levels.
- **Customizable**: Easily extend the logging functionality by creating your own custom "Oaks" (logging trees).
- **Thread-Safe**: Ensures logging operations are safe and efficient across threads.
- **Easy to Use**: Simple API that is easy to integrate into your project.

## Honorable Mention:
This library was created because the excellent [Timber](https://github.com/JakeWharton/timber) library, which inspired the design of Lumber, does not support Kotlin Multiplatform (KMP). While Timber is a fantastic logging solution for Android development, Lumber was designed to fill the gap for KMP projects, providing similar logging functionality across multiple platforms. Special thanks to Jake Wharton for creating such an awesome library!

## Installation

### Gradle
Add the following to your `build.gradle` file:

```gradle
dependencies {
    implementation "io.github.matheus-corregiari:lumber:<arch-toolkit-version>"
}
```

## Logging Levels

Lumber supports the following logging levels:

- **Verbose**: For detailed logging, typically used for tracing and debugging.
- **Debug**: For debugging information and messages.
- **Info**: For general informative messages.
- **Warn**: For warnings that are not necessarily errors but may require attention.
- **Error**: For logging errors and exceptions.
- **Assert**: For logging assert-level messages, typically used for critical failures.

## Logging Example

```kotlin
Lumber.verbose("This is a verbose log message.")
Lumber.debug("Debugging the app with some debug logs.")
Lumber.info("This is an informational log.")
Lumber.warn("This is a warning message!")
Lumber.error(message = "An error occurred!", error = Exception("Sample exception"))
Lumber.wtf(message = "Critical issue occurred!", error = Exception("Critical exception"))
```

## Creating Custom Oaks

To extend Lumber's functionality, you can create your own custom Oak (logging tree). Here's an example of a custom logging implementation:

```kotlin
class ConsoleOak : Lumber.Oak() {
    override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
        println("$level: [$tag] $message")
    }
}
```

## Planting Oaks

You can plant one or more Oaks into the logging system:

```kotlin
Lumber.plant(ConsoleOak(), FileOak()) // Plant custom Oaks
Lumber.debug("Debug message") // Logs to both ConsoleOak and FileOak
```

## Managing Logging Configuration

You can temporarily set a custom tag or quiet mode for the next log message:

```kotlin
Lumber.tag("MyActivity").debug("Debug message with custom tag")
Lumber.quiet(true).error("This error will be ignored in quiet mode.")
```

## Clearing Logs

You can uproot (remove) specific Oaks from the logging system or clear all Oaks:

```kotlin
Lumber.uproot(consoleOak) // Removes consoleOak
Lumber.uprootAll() // Removes all Oaks
```

## License

Lumber is licensed under the [APACHE License](../../../LICENSE.md).

---
