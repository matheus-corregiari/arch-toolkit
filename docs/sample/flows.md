# Demonstrated Flows

## GitHub List

The GitHub feature exercises shared networking, repository boundaries,
observable request state, and reusable Compose UI across targets.

## Settings

The settings feature exercises the `StorageProvider` contract. Persistent
targets receive `DataStoreProvider`; web receives `MemoryStoreProvider`.

## Request Orchestration

Repository requests use Splinter to expose loading, data, and error states to
the shared UI. This demonstrates orchestration in an application boundary
rather than isolated API calls.

## What the Sample Does Not Prove

The sample does not replace module tests, API compatibility checks, or
publication validation. It is intentionally a separate CI concern because its
UI, network, and compiler-plugin dependencies have a larger compatibility
surface.
