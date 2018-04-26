# State Machine

Abstract implementation to handle view states changes based on some key (represented by a **Int**)

##### Simple Usage

###### Setup
```kotlin
stateMachine.setup {
    onChangeState { stateKey -> } // Optional handler called when a state becomes active

    add(YOUR_INT_CONSTANT) {}     // to Add a state into the state machine
}
```

###### Change State
```kotlin
stateMachine.changeState(YOUR_INT_CONSTANT)
```

###### Save State

```kotlin
outState.putBundle("YOUR_STATE_KEY", stateMachine.saveInstanceState())
```

###### Restore State

```kotlin
stateMachine.restoreInstanceState(savedInstanceState.getBundle("YOUR_STATE_KEY"))

// OR

stateMachine.setup(restoreState = savedInstanceState.getBundle("YOUR_STATE_KEY")) { }
```

### ViewStateMachine

Implementation based on visibility changes on views.
To use this, all views must be already in the layout.

##### Simple Usage

```kotlin
add(YOUR_INT_CONSTANT) {

    onEnter {}   // Will be executed when this state becomes Active

    visibles()   // views to become visible
    invisibles() // views to become invisible
    gones()      // views to become gone

    onExit {}    // Will be executed when this state is leaving

}
```

### SceneStateMachine

Implementation based on Transition Scenes framework to change a layout content.
To use this, you must have a layout container to put a custom layout on it.

##### Usage

```kotlin
add(YOUR_INT_CONSTANT) {

    onEnter {}                        // Will be executed when this state becomes Active

    scene(layoutRes to containerView) // Receives a Pair with LayoutId and the container ViewGroup to inflate the layout on it
    transition()                      // Optional Transition to animate the scene change

    onExit {}                         // Will be executed when this state is leaving

}
```