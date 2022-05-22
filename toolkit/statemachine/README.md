[ ![Download](https://api.bintray.com/packages/methe/arch-toolkit/statemachine/images/download.svg) ](https://bintray.com/methe/arch-toolkit/statemachine/_latestVersion)
[![CircleCI](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master.svg?style=svg)](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master)

# State Machine

Abstract implementation to handle view states changes based on some key (represented by a **Int**)

### Usage

#### Add into your project

###### Module -> build.gradle

```groovy
implementation "br.com.arch.toolkit:statemachine:$latest_version"
```

or

```groovy
api "br.com.arch.toolkit:statemachine:$latest_version"
```

### How to use

##### Setup

###### Kotlin
```kotlin
stateMachine.setup {
    config { ... } // Optional configuration for initial setup

    state(YOUR_INT_CONSTANT) { // to Add a state into the state machine
        onEnter { ... }   // Will be executed when this state becomes Active

        ...

        onExit { ... }    // Will be executed when this state is leaving
    }
}
```

###### Java
```java
final Config config = stateMachine.getConfig();

...

final State state = stateMachine.newStateInstance();
state.onEnter(() -> {}); // Will be executed when this state becomes Active

...

state.onExit(() -> {});  // Will be executed when this state is leaving

stateMachine
    .addState(YOUR_INT_CONSTANT, state) // Add the configured state
    .restoreInstanceState(bundle);      // Optional restore state
    .start();                           // In java you need to start the machine after the setup
```

##### Change State

```java
// Simple change state
stateMachine.changeState(YOUR_INT_CONSTANT);

// To force the state change even when it is the current state
stateMachine.changeState(YOUR_INT_CONSTANT, true);

// A custom onChangeState implementation [See the Config section]
stateMachine.changeState(YOUR_INT_CONSTANT, stateKey -> { ... });

// Full
stateMachine.changeState(YOUR_INT_CONSTANT, true, stateKey -> { ... });
```

##### Save and Restore State

###### Save

```kotlin
outState.putBundle("YOUR_STATE_KEY", stateMachine.saveInstanceState())
```

###### Restore

```kotlin
stateMachine.restoreInstanceState(savedInstanceState.getBundle("YOUR_STATE_KEY"))
```

##### Config

###### Kotlin
```kotlin
stateMachine.config {
    initialState = INITIAL_STATE_KEY
    setOnChangeState { stateKey -> ... } // Handler called whenever state becomes active
}
```

###### Java
```java
final Config config = stateMachine.getConfig();

config.setInitialState(INITIAL_STATE_KEY);
config.setOnChangeState(stateKey -> { ... }); // Handler called whenever state becomes active
```


### ViewStateMachine

Implementation based on visibility changes on views.
To use this, all views must be already in the layout.

##### Simple Usage

###### Kotlin
```kotlin
stateMachine.state(YOUR_INT_CONSTANT) {

    // Visibility
    visibles()   // views to become visible
    invisibles() // views to become invisible
    gones()      // views to become gone

    // Enable
    enables()    // views to become enable
    disables()   // views to become disable

}
```

###### Java
```java
final State state = stateMachine.newStateInstance();

state
    // Visibility
    .visibles()   // views to become visible
    .invisibles() // views to become invisible
    .gones()      // views to become gone

    // Enable
    .enables()    // views to become enable
    .disables();  // views to become disable

stateMachine.addState(YOUR_INT_CONSTANT, state);
```

### SceneStateMachine

Implementation based on Transition Scenes framework to change a layout content.
To use this, you must have a layout container to put a custom layout on it.

##### Usage

###### Kotlin
```kotlin
stateMachine.state(YOUR_INT_CONSTANT) {

    scene(layoutRes to containerView) // Receives a Pair with LayoutId and the container ViewGroup to inflate the layout on it
    transition()                      // Optional Transition to animate the scene change

}
```

###### Java
```java
final State state = stateMachine.newStateInstance();

state
    .scene(layoutRes to containerView) // Receives a Pair with LayoutId and the container ViewGroup to inflate the layout on it
    .transition();                     // Optional Transition to animate the scene change

stateMachine.addState(YOUR_INT_CONSTANT, state);
```

##### AVOID CRASHES AND LEAKS

If your Statemachine instance persists configuration changes, or view lifecycle. Don't forget to call the 'shutdown' method

```kotlin
stateMachine.shutdown()
```

This will erase all current references and states of the machine. =)

##### PROGUARD RULES

If you are not using Kotlin(and you should), you need to add this into your proguard-rules.pro file. Because if you are not using Kotlin, the shrinker will become angry with you, telling that some classes cannot be found.

```
# State Machine (only if you don't use Kotlin)
-dontwarn kotlin.jvm.internal.Intrinsics
-dontwarn kotlin.Pair
-dontwarn kotlin.Metadata
-dontwarn kotlin.jvm.functions.Function1
-dontwarn kotlin.Unit
```
