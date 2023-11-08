[Download](https://search.maven.org/artifact/io.github.matheus-corregiari/delegate)
[![CircleCI](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master.svg?style=svg)](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master)

# Delegate

Some custom implementations for delegated properties

### Usage

#### Add into your project

###### build.gradle

First you need to add Kotlin and the androidx core components

```groovy
// Module build.gradle
apply plugin: 'kotlin-android'

dependencies{
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versions.kotlin"
    implementation "androidx.annotation:annotation:$versions.androidx.annotation"
    implementation "androidx.appcompat:appcompat:$versions.androidx.appcompat"
    implementation "androidx.recyclerview:recyclerview:$versions.androidx.recyclerview"
}
```

Then add the Delegate dependency

```groovy
implementation "io.github.matheus-corregiari:delegate:$latest_version"
```
or
```groovy
api "io.github.matheus-corregiari:delegate:$latest_version"
```

#### How to use

##### ViewProvider

```kotlin
class YourView : AppCompatActivity() {
    
    // Required Views (throws exception when not found)
    private val view: View by viewProvider(R.id.view_id)
    // or
    private val view: View by viewProvider(R.id.view_id, parentId= R.id.parent_view_id)

    // Optional Views accept only nullable references \o/
    private val view: View? by optionalViewProvider(R.id.view_id)
    // or
    private val view: View? by optionalViewProvider(R.id.view_id, parentId= R.id.parent_view_id)
    
}

// Also works with
class YourView : Fragment()
class YourView : View()
class YourView : RecyclerView.ViewHolder()
```

###### BIG NOTE: like another strategies to use some view binding, you need to use the references below the layout inflate... otherwise your app will crash

- AppCompatActivity -> after the inflate
- Fragment -> It's recommend to call your view on any method after onCreateView and before
  onDestroyView ... also after the inflate
- View -> after the inflate
- RecyclerView.ViewHolder -> after the inflate

##### ExtraProvider

```kotlin
class YourView : AppCompatActivity() {
    
    // Required Views (throws exception when not found)
    private val extra: String? by extraProvider("EXTRA_KEY")
    
    // keepState FALSE means that the delegate will call getExtra every time! TRUE means that the delegate will lazily keeps a reference extracted from getExtra
    private val extra: String? by extraProvider("EXTRA_KEY", keepState=true)

    // default value to make your extra type non null \o/
    private val extra: String by extraProvider("EXTRA_KEY", keepState=true, default="default value")

    // is you want to execute a block to build the default value
    private val extra: String by extraProvider("EXTRA_KEY", keepState=true) { "default value" }

    // You can also set this variables with var but it only works with delegates with keepState attribute set to TRUE (default value)
    private var extra: String? by extraProvider("EXTRA_KEY")
    
}

// Also works with
class YourView : Fragment()
```