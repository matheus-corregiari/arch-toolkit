[ ![Download](https://api.bintray.com/packages/methe/arch-toolkit/statemachine/images/download.svg) ](https://bintray.com/methe/arch-toolkit/statemachine/_latestVersion)

# State Machine

Abstract implementation to handle view states changes based on some key (represented by a **Int**)

### Usage

#### Add into your project

###### build.gradle

First you need to add Kotlin

```groovy

// Root project build.gradle
dependencies {
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
}

// Module build.gradle
apply plugin: 'kotlin-android'

dependencies{
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
}
```

Then add the StateMachine

```groovy
implementation "br.com.arch.toolkit:statemachine:$latest_version"
```

or

```groovy
api "br.com.arch.toolkit:statemachine:$latest_version"
```

#### How to use

###### Setup
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

###### Change State
```kotlin
// Simple change state
stateMachine.changeState(YOUR_INT_CONSTANT)

// To force the state change even when it is the current state
stateMachine.changeState(YOUR_INT_CONSTANT, forceChange = true)

// A custom onChangeState implementation [See the Config section]
stateMachine.changeState(YOUR_INT_CONSTANT, onChangeState = { stateKey -> ... })
```

###### Save State

```kotlin
outState.putBundle("YOUR_STATE_KEY", stateMachine.saveInstanceState())
```

###### Restore State

```kotlin
stateMachine.restoreInstanceState(savedInstanceState.getBundle("YOUR_STATE_KEY"))
```

###### Config
```kotlin
stateMachine.config {
    initialState = INITIAL_STATE_KEY
    onChangeState = { stateKey -> ... } // Handler called whenever state becomes active
}
```


### ViewStateMachine

Implementation based on visibility changes on views.
To use this, all views must be already in the layout.

##### Simple Usage

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

### SceneStateMachine

Implementation based on Transition Scenes framework to change a layout content.
To use this, you must have a layout container to put a custom layout on it.

##### Usage

```kotlin
stateMachine.state(YOUR_INT_CONSTANT) {

    scene(layoutRes to containerView) // Receives a Pair with LayoutId and the container ViewGroup to inflate the layout on it
    transition()                      // Optional Transition to animate the scene change

}
```