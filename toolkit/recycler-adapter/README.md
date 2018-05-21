# Recycler Adapter

Basic implementation of a simple Recycler Adapter using Custom Views

### Usage

#### Add into your project

###### build.gradle

First you need to add Kotlin and Recycler View

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

```groovy
implementation "com.android.support:recyclerview-v7:$versions.androidSupport"
```

Then add the Recycler Adapter

```groovy
implementation "br.com.arch.toolkit:recycler-adapter:$latest_version"
```

or

```groovy
api "br.com.arch.toolkit:recycler-adapter:$latest_version"
```

#### How to use

##### Simple Usage

###### Creating a Item

```kotlin
class YoutCustomViewClass : View, ViewBinder<YourModel>{

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
    val adapter = SimpleAdapter(::YoutCustomViewClass) // Simple like that =)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Your code here

        recycler.adapter = adapter
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

    // Optional
    override fun <T> bindHolder(holder: BaseViewHolder, model: T, onItemClick: ((T) -> Unit)?) {
        super.bindHolder(holder, model, onItemClick)

        // If you want a custom implementation to bind your view
    }

}
```

###### Click Listeners

```kotlin
val adapter = SimpleAdapter(::YoutCustomViewClass)

adapter.withListener { model -> } // Default click listener
adapter.withListener(VIEW_TYPE) { model -> } // For specific view types
```