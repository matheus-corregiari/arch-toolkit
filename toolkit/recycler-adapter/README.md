[Download](https://search.maven.org/artifact/io.github.matheus-corregiari/recycler-adapter)
[![CircleCI](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master.svg?style=svg)](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master)

# Recycler Adapter

Basic implementation of a simple Recycler Adapter using Custom Views

### Usage

#### Add into your project

###### build.gradle

First you need to add Kotlin and Recycler View

```groovy

// Root project build.gradle
dependencies {
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlin"
}

// Module build.gradle
apply plugin: 'kotlin-android'

dependencies{
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versions.kotlin"
}
```

```groovy
implementation "androidx.recyclerview:recyclerview:$versions.androidx.recyclerview"
```

Then add the Recycler Adapter

```groovy
implementation "io.github.matheus-corregiari:recycler-adapter:$latest_version"
```

or

```groovy
api "io.github.matheus-corregiari:recycler-adapter:$latest_version"
```

#### How to use

##### Simple Usage

###### Creating a Item

```kotlin
class YourCustomViewClass : View, ViewBinder<YourModel>{

    // Your custom View stuff

    fun bind(model: YourModel){
        // Bind your View Here \o/
    }

}
```

###### For single ItemView adapter, use SimpleAdapter

```kotlin
class YourActivity : Activity {

    val recyclerView: RecyclerView
    val adapter = SimpleAdapter(::YourCustomViewClass) // Simple like that =)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Your code here

        recyclerView.adapter = adapter
        adapter.setList(someList)
    }

}
```

###### For a sticky Header and a single ItemView adapter, use SimpleStickyAdapter

```kotlin
class YourStickyModel : StickyHeaderModel {
    override var isSticky = false
}

class YourActivity : Activity {

    val recyclerView: RecyclerView
    val adapter = SimpleStickyAdapter(::YourCustomViewClass, ::YourCustomHeaderClass) // Simple like that =)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Your code here

        recyclerView.adapter = adapter
        recycler.layoutManager = StickyHeadersLinearLayoutManager<SimpleStickyAdapter<YourStickyModel, YourCustomViewClass, YourCustomHeaderClass>>(this)
        adapter.setList(someList)
    }

}
```

###### Implementing a Custom Adapter

```kotlin
class CustomAdapter : BaseRecyclerAdapter<MODEL>() {

    override fun viewCreator(context: Context, viewType: Int): ViewBinder<*> {
        // Returns a new instance of a custom View that implements ViewBinder =D
    }

    // Optional
    override fun getItemViewType(position: Int): Int {
        // Your logic for custom itemView
    }

}
```

###### Click Listeners

```kotlin
val adapter = SimpleAdapter(::YourCustomViewClass)

adapter.withListener { model -> } // Default click listener
adapter.withListener(VIEW_TYPE) { model -> } // For specific view types
```
