# Sample Application

The sample is a Compose Multiplatform GitHub browser used to exercise Arch
Toolkit in a realistic modular application. It is a validation client, not a
starter template or a compatibility promise for every dependency it uses.

The sample demonstrates:

- shared feature and design-system modules
- persistent storage on Android, JVM, and Apple-oriented shared code
- in-memory storage on web
- Splinter request orchestration
- multiplatform dependency injection and networking

Sample projects are excluded from normal Gradle configuration. Enable them with
`-PincludeSamples=true`.

- [Architecture](architecture.md)
- [Run each target](running.md)
- [Demonstrated flows](flows.md)
- [Known limitations](limitations.md)
