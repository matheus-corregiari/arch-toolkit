# API Reference

Dokka generates a separate HTML reference for every published module. The API
reference is built before MkDocs and copied into the same published site.

- [Storage Core](storage-core/html/index.html)
- [Storage Memory](storage-memory/html/index.html)
- [Storage DataStore](storage-datastore/html/index.html)
- [State Handle](state-handle/html/index.html)
- [Splinter](splinter/html/index.html)

Generated HTML is not committed. Run `./gradlew ciDocs` before a local strict
MkDocs build.
