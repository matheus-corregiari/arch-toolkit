[Download](https://search.maven.org/artifact/io.github.matheus-corregiari/livedata)
[![CircleCI](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master.svg?style=svg)](https://circleci.com/gh/matheus-corregiari/arch-toolkit/tree/master)

# Livedata

Some custom implementations of LiveData and some extensions

### Usage

#### Add into your project

###### build.gradle

First you need to add the Google Architecture components

```groovy
implementation "android.arch.lifecycle:livedata:$versions.arch"
```

Then add the Livedata implementation from arch-toolkit

```groovy
implementation "io.github.matheus-corregiari:livedata:$latest_version"
```

#### How to use

##### ResponseLiveData

```kotlin

class MyViewModel(private val repository: MyRepository): ViewModel() {
    
    // Use MutableResponseLiveData when you want to change the value
    private val _liveData: MutableResponseLiveData<Movies> = MutableResponseLiveData()
    
    val liveData: ResponseLiveData<Movies>
        get() = _liveData
    
    fun loadMovies() {
        try {
            // call postLoading to post loading true
            _liveData.postLoading()
            // call postData to post data
            _liveData.postData(repository.loadMovies())
        } catch (e: Exception) {
            // call postError to post error
            _liveData.postError(e)
        }
    }

    fun loadMoviesAsyncOperation() : ResponseLiveData<Movies> {
        // When you don't want to implement the code on the function above you can use the function makeAsyncOperation
        // She has two implementations, one using a WorkerThread and another using Coroutine

        return makeAsyncOperation {
            repository.loadMovies()
        }
    }

    fun loadMoviesAsyncOperationErrorTransformer() : ResponseLiveData<Movies> {
        // You can also pass a errorTransformer to makeAsyncOperation
        return makeAsyncOperation(errorTransformer = {}) {
            repository.loadMovies()
        }
    }
}

class MyActivity: AppCompatActivity() {

    private val viewModel: MyViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadMovies()
        viewModel.liveData.observe(this) {
            status { 
                // Called when status change value
                // The status can be:
                // DataResultStatus.LOADING
                // DataResultStatus.ERROR
                // DataResultStatus.SUCCESS
            }
            success {
                // Called when status is DataResultStatus.SUCCESS
            }
            data {
                // Called when has data
            }
            showLoading {
                // called only when loading is true
            }
            loading {
                // called when loading change value
            }
            error { e ->
                // called when error change value
            }
            
            // When withData is false, the function only will be called when data is null
            // This is useful because you might not want to show loading or error state when you already have data
            showLoading(withData = false) {
                
            }
            loading(withData = false) {

            }
            error(withData = false) { e ->

            }
            
            // When single is true, the function only will be executed one time
            status(single = true) { }
            success(single = true) { }
            data(single = true) { }
            showLoading(single = true) { }
            loading(single = true) { }
            error(single = true) { e -> }
        }
    }
}

```

##### Extensions and Transformers

###### Extensions

```Kotlin

val liveData: LiveData<Movies>

// Observes a LiveData<T> with non null values
liveData.observe(this@Activity) {
    // Will be called on every non null data
}

// Observes a LiveData<T> with non null values only one time
liveData.observeSingle(this@Activity) {
    // Will be called one time with a non null data
}

// Observes a LiveData<T> until a condition be true
liveData.observeUntil(this@Activity) {
    // Will be called on every data changes until it returns true
}

// Because it is a LiveData extension, 
// you can use with any custom implementation of LiveData, including ResponseLiveData

```

###### Transformers

```Kotlin

val liveData: LiveData<Movies>

// Transforms a LiveData<T> into a LiveData<R>
liveData.map {
    // here you map T to R
}

// Transforms a LiveData<List<T>> into a LiveData<List<R>>
liveData.mapList {
    // here you map List<T> to List<R>
}

val responseLiveData: ResponseLiveData<Movies>

// Transforms a ResponseLiveData<List<T>> into a ResponseLiveData<List<R>>
 responseLiveData.mapList(transformAsync = true) { // Map runs asynchronously when transformAsync is true
    // here you map List<T> to List<R>
}

```

##### SwapResponseLiveData

```Kotlin

// A custom implementation of ResponseLiveData responsible for replicate a value from another ResponseLiveData

val swapResponseLiveData: SwapResponseLiveData<Movies> = SwapResponseLiveData()

// Changes the actual DataSource
swapResponseLiveData.swapSource(otherResponseLiveData)

// You can discard the value after loading when discardAfterLoading is true
swapResponseLiveData.swapSource(otherResponseLiveData, discardAfterLoading = true)


val moviesResponseLiveData: ResponseLiveData<MoviesResponse>

// You can also transform the data
swapResponseLiveData.swapSource(moviesResponseLiveData, dataTransformer = { movies: MoviesResponse ->
    // Receives the data of the source and changes it to the value of this SwapResponseLiveData
})

// Others arguments you can pass to swapSource
swapResponseLiveData.swapSource(
    moviesResponseLiveData, 
    // Indicate swapSource will execute synchronously or asynchronously
    async = true,
    dataTransformer = { movies: MoviesResponse -> },
    // Receives the error of the source and change to another Throwable value
    errorTransformer = { throwable: Throwable -> },
    // Receives the error of the source and changes it to the value of this SwapResponseLiveData
    onErrorReturn = { throwable: Throwable -> }
)

// Remove the source
swapResponseLiveData.clearSource()

// Returns true if does not have data source or if the status is equal to DataResultStatus.ERROR
swapResponseLiveData.needsRefresh()

```
