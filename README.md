# Coroutines

Coroutine:
----------

A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.

coroutines help to solve two primary problems:

1) Manage long-running tasks that might otherwise block the main thread and cause your app to freeze.

Long-running operations should run outside of the main thread.

suspend fun fetchDocs() {                             // Dispatchers.Main
    val result = get("https://developer.android.com") // Dispatchers.IO for `get`
    show(result)                                      // Dispatchers.Main
}

suspend fun get(url: String) = withContext(Dispatchers.IO) { /* ... */ }

//Suspend
suspend pauses the execution of the current coroutine, saving all local variables.

//Resume
resume continues execution of a suspended coroutine from the place where it was suspended.

You can call suspend functions only from other suspend functions or by using a coroutine builder such as launch to start a new coroutine.

Kotlin uses a stack frame to manage which function is running along with any local variables. When suspending a coroutine, the current stack frame is copied and saved for later. When resuming, the stack frame is copied back from where it was saved, and the function starts running again.

2) Providing main-safety, or safely calling network or disk operations from the main thread.

Kotlin coroutines use dispatchers to determine which threads are used for coroutine execution.
Coroutines can suspend themselves, and the dispatcher is responsible for resuming them.

Dispatchers.Main - Use this dispatcher to run a coroutine on the main Android thread. This should be used only for interacting with the UI and performing quick work.

Dispatchers.IO - This dispatcher is optimized to perform disk or network I/O outside of the main thread.

Dispatchers.Default - This dispatcher is optimized to perform CPU-intensive work outside of the main thread. Example use cases include sorting a list and parsing JSON.

withContext():
--------------

So a good practice is to use withContext to make sure every function is safe to be called on any Dispatcher including Main — that way the caller never has to think about what thread will be needed to execute the function.

suspend fun fetchDocs() {                      // Dispatchers.Main
    val result = get("developer.android.com")  // Dispatchers.Main
    show(result)                               // Dispatchers.Main
}

suspend fun get(url: String) =                 // Dispatchers.Main
    withContext(Dispatchers.IO) {              // Dispatchers.IO (main-safety block)
        /* perform network IO here */          // Dispatchers.IO (main-safety block)
    }                                          // Dispatchers.Main
}

CoroutineScope:
---------------

A CoroutineScope keeps track of all your coroutines, and it can cancel all of the coroutines started in it.

A CoroutineScope manages one or more related coroutines.
CoroutineScope stops coroutine execution when a user leaves a content area within your app.

LifeCycleScope:

This lets you avoid leaking memory or doing extra work for activities or fragments that are no longer relevant to the user.

lifecycleScope.launch {
    withContext(Dispatchers.IO) {
        for (i in 1..15){
            println("output $i")
            Thread.sleep(1000)
        }
    }
    val response = RetrofitHandler(NetworkConnectionInterceptor(this@LifeCycleScopeActivity)).ping()
    println(response.body())
}

ViewModelScope:
---------------

Using Jetpack components, they fit naturally in a ViewModel. Because a ViewModel isn't destroyed during configuration changes (such as screen rotation), you don't have to worry about your coroutines getting canceled or restarted.

class MyViewModel : ViewModel() {

    fun launchDataLoad() {
        viewModelScope.launch {
            sortList()
            // Modify UI
        }
    }

    /**
    * Heavy operation that cannot be done in the Main Thread
    */
    suspend fun sortList() = withContext(Dispatchers.Default) {
        // Heavy work
    }
}

CustomScope:
------------

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    fun loadData() = launch {
        // code
    }
}

GlobalScope:
------------

Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are not cancelled prematurely.

Application code usually should use application-defined CoroutineScope, using async or launch on the instance of GlobalScope is highly discouraged.

GlobalScope.launch {
    // code
}


Start a coroutine:
------------------

launch:
-------

Launch is a bridge from regular functions into coroutines.
starts a new coroutine and doesn't return the result to the caller.
Any work that is considered "fire and forget" can be started using launch.
It runs the coroutine synchronously in the scope.
Typically, you should launch a new coroutine from a regular function, as a regular function cannot call await.

fun onDocsNeeded() {
    viewModelScope.launch {    // Dispatchers.Main
        fetchDocs()            // Dispatchers.Main (suspend function call)
    }
}

async:
------
starts a new coroutine and allows you to return a result with a suspend function called await.
It runs the coroutine asynchronously in the scope.
Use async only when inside another coroutine or when inside a suspend function and performing parallel decomposition.

suspend fun fetchTwoDocs() =
    coroutineScope {
        val deferredOne = async { fetchDoc(1) }
        val deferredTwo = async { fetchDoc(2) }
        deferredOne.await()
        deferredTwo.await()
    }

suspend fun fetchTwoDocs() =        // called on any Dispatcher (any thread, possibly Main)
    coroutineScope {
        val deferreds = listOf(     // fetch two docs at the same time
            async { fetchDoc(1) },  // async returns a result for the first doc
            async { fetchDoc(2) }   // async returns a result for the second doc
        )
        deferreds.awaitAll()        // use awaitAll to wait for both network requests
    }


runBlocking:
------------

Runs a new coroutine and blocks the current thread interruptibly until its completion. This function should not be used from a coroutine. It is designed to bridge regular blocking code to libraries that are written in suspending style, to be used in main functions and in tests.

runBlocking(Dispatchers.IO) {
    for (i in 1..15){
        println("output $i")
        Thread.sleep(1000)
    }
}

Keeping track of coroutines:
----------------------------

A leaked coroutine can waste memory, CPU, disk, or even launch a network request that’s not needed.

To help avoid leaking coroutines, Kotlin introduced structured concurrency. Structured concurrency is a combination of language features and best practices that, when followed, help you keep track of all work running in coroutines.

On Android, we can use structured concurrency to do three things:
1) Cancel work when it is no longer needed.
2) Keep track of work while it’s running.
3) Signal errors when a coroutine fails.


Kotlin Coroutines patterns & anti-patterns:
-------------------------------------------

1) Wrap async calls with coroutineScope or use SupervisorJob to handle exceptions:
----------------------------------------------------------------------------------

 If async block may throw exception don’t rely on wrapping it with try/catch block.

 val job: Job = Job()
 val scope = CoroutineScope(Dispatchers.Default + job)
 // may throw Exception
 fun doWork(): Deferred<String> = scope.async { ... }   // (1)
 fun loadData() = scope.launch {
     try {
         doWork().await()                               // (2)
     } catch (e: Exception) { ... }
 }

 In the example above doWork function launches new coroutine (1) which may throw an unhandled exception. If you try to wrap doWork with try/catch block (2) it will still crash.
 This happens because the failure of any of the job’s children leads to an immediate failure of its parent.

 SupervisorJob:
 --------------

 One way how you can avoid the crash is by using SupervisorJob.
 A failure or cancellation of a child does not cause the supervisor job to fail and does not affect its other children.

 val job = SupervisorJob()                               // (1)
 val scope = CoroutineScope(Dispatchers.Default + job)

 // may throw Exception
 fun doWork(): Deferred<String> = scope.async { ... }

 fun loadData() = scope.launch {
     try {
         doWork().await()
     } catch (e: Exception) { ... }
 }

 wrapping async with coroutineScope:
 -----------------------------------

 So the code below will still crash your application because async is launched in the scope of parent coroutine

 val job = SupervisorJob()
 val scope = CoroutineScope(Dispatchers.Default + job)

 fun loadData() = scope.launch {
     try {
         async {                                         // (1)
             // may throw Exception
         }.await()
     } catch (e: Exception) { ... }
 }

 Now when the exception occurs inside async it will cancel all other coroutines created in this scope, without touching outer scope.

 val job = SupervisorJob()
 val scope = CoroutineScope(Dispatchers.Default + job)

 // may throw Exception
 suspend fun doWork(): String = coroutineScope {     // (1)
     async { ... }.await()
 }

 fun loadData() = scope.launch {                       // (2)
     try {
         doWork()
     } catch (e: Exception) { ... }
 }

2) Prefer the Main dispatcher for root coroutine:
-------------------------------------------------

If you need to do a background work and update UI inside your root coroutine, don’t launch it with non-Main dispatcher.

val scope = CoroutineScope(Dispatchers.Default)          // (1)

fun login() = scope.launch {
    withContext(Dispatcher.Main) { view.showLoading() }  // (2)
    networkClient.login(...)
    withContext(Dispatcher.Main) { view.hideLoading() }  // (2)
}

In the example above we launch root coroutine using a scope with Default dispatcher. With this approach, every time when we need to touch user interface we have to switch context.

In most cases, it’s preferable to create your scope with the Main dispatcher which results in simpler code and less explicit context switching.

val scope = CoroutineScope(Dispatchers.Main)

fun login() = scope.launch {
    view.showLoading()
    withContext(Dispatcher.IO) { networkClient.login(...) }
    view.hideLoading()
}

3) Avoid usage of unnecessary async/await:
------------------------------------------

If you are using async function followed by immediate await you should stop doing this.

launch {
    val data = async(Dispatchers.Default) { /* code */ }.await()
}

If you want to switch coroutine context and immediately suspend parent coroutine withContext is a preferable way to do that.

launch {
    val data = withContext(Dispatchers.Default) { /* code */ }
}

4) Avoid cancelling scope job:
------------------------------

If you need to cancel coroutine, don’t cancel scope job in the first place.

class WorkManager {
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    fun doWork1() {
        scope.launch { /* do work */ }
    }

    fun cancelAllWork() {
        job.cancel()
    }
}

fun main() {
    val workManager = WorkManager()

    workManager.doWork1()
    workManager.cancelAllWork()
    workManager.doWork1() // (1)
}

The issue with the above code is that when we cancel job we put it into the completed state. Coroutines launched in a scope of the completed job will not be executed

When you want to cancel all coroutines of a specific scope, you can use cancelChildren function. Also, it’s a good practice to provide the possibility to cancel individual jobs.

class WorkManager {
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    fun doWork1(): Job = scope.launch { /* do work */ } // (2)

    fun cancelAllWork() {
        scope.coroutineContext.cancelChildren()         // (1)
    }
}
fun main() {
    val workManager = WorkManager()

    workManager.doWork1()
    workManager.cancelAllWork()
    workManager.doWork1()
}

5) Avoid writing suspend function with an implicit dispatcher:
--------------------------------------------------------------

Don’t write suspend function which relies on execution from specific coroutine dispatcher.

suspend fun login(): Result {
    view.showLoading()

    val result = withContext(Dispatcher.IO) {
        someBlockingCall()
    }
    view.hideLoading()

    return result
}

In the example above login function is a suspend function which will crash if you execute it from coroutine which uses non-Main dispatcher.

launch(Dispatcher.Main) {     // (1) no crash
    val loginResult = login()
    ...
}

launch(Dispatcher.Default) {  // (2) cause crash
    val loginResult = login()
    ...
}

CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
Design your suspend function in a way that it can be executed from any coroutine dispatcher.

suspend fun login(): Result = withContext(Dispatcher.Main) {
    view.showLoading()

    val result = withContext(Dispatcher.IO) {
        someBlockingCall()
    }

    view.hideLoading()
	return result
}

Now we can call our login function from any dispatcher.

launch(Dispatcher.Main) {     // (1) no crash
    val loginResult = login()
    ...
}

launch(Dispatcher.Default) {  // (2) no crash ether
    val loginResult = login()
    ...
}

6) Avoid usage of global scope:
-------------------------------

If you are using GlobalScope everywhere in your Android application you should stop doing this.

GlobalScope.launch {
    // code
}

Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are not cancelled prematurely.
Application code usually should use application-defined CoroutineScope, using async or launch on the instance of GlobalScope is highly discouraged.
In Android coroutine can be easily scoped to Activity, Fragment, View or ViewModel lifecycle.

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    fun loadData() = launch {
        // code
    }
}
