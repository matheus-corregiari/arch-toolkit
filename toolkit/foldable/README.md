[ ![Download](https://api.bintray.com/packages/methe/arch-toolkit/livedata/images/download.svg) ](https://bintray.com/methe/arch-toolkit/livedata/_latestVersion)
[![CircleCI](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master.svg?style=svg)](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master)

# Foldable

Some extensions to help developing a foldable compatible application and a FoldableActivity for simple cases

### Usage

#### Add into your project

###### build.gradle

First you need to add the AndroidX Window component

```groovy
implementation "androidx.window:window:$versions.androidx.window"
```

Then add the Foldable implementation from arch-toolkit

```groovy
implementation "io.github.matheus-corregiari:foldable:$versions.arch.foldable"
```

#### How to use

##### FoldableActivity

###### Horizontal (divide when on landscape mode)

```kotlin

class FoldableSampleActivity : FoldableActivity() {

    override val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.HORIZONTAL
    override val topViewId: Int = R.layout.activity_foldable_top_sample
    override val bottomViewId: Int = R.layout.activity_foldable_bottom_sample
}

```

###### Vertical (divide when on portrait mode)

```kotlin

class FoldableSampleActivity : FoldableActivity() {

    override val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL
    override val startViewId: Int = R.layout.activity_foldable_start_sample
    override val endViewId: Int = R.layout.activity_foldable_end_sample
}

```

##### Extensions

###### handleFoldableStateChange (Useful when no other behaviour other than the default one is desired)

```Kotlin

class FoldableSampleActivity : AppCompatActivity(R.layout.activity_foldable_sample) {

    private val root: ViewGroup by viewProvider(R.id.root_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleFoldableStateChange(
            layout = root,
            reactiveGuideId = R.id.fold_guideline,
            orientation = FoldingFeature.Orientation.VERTICAL
        )
    }
}

```

###### observeFoldableStateChanges (Useful you want a complete custom behaviour for your activity)

```Kotlin

class FoldableSampleActivity :
    AppCompatActivity(R.layout.activity_foldable_sample),
    OnFoldableStateChangeListener {

    private val root: ViewGroup by viewProvider(R.id.root_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeFoldableStateChanges(
            layout = root,
            orientation = FoldingFeature.Orientation.VERTICAL,
            listener = this
        )
    }

    override fun onChangeState(isFolded: Boolean) {
        //Called whenever there is ANY state changes (called if state or rotation changes)
    }

    override fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation) {
        //Called whenever the foldable device is fully open (called if state or rotation changes)
    }

    override fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation) {
        //Called whenever the foldable device is half open (called if state or rotation changes)
    }

    override fun onClosed() {
        //Called whenever the foldable device is closed (called if state or rotation changes)
    }

    override fun onWrongOrientation() {
        //Called whenever the foldable device rotates and the orientation is the chosen one
    }
}

```

###### onNewLayoutInfo (Useful you want a complete control over the observer)

```Kotlin

class FoldableSampleActivity :
    AppCompatActivity(R.layout.activity_foldable_sample),
    OnFoldableStateChangeListener {

    private val root: ViewGroup by viewProvider(R.id.root_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onNewLayoutInfo { info: WindowLayoutInfo ->
            //Called whenever there is a rotation or state change
        }
    }
}

```
