package com.sysaxiom.coroutines.customscope

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sysaxiom.coroutines.R
import com.sysaxiom.coroutines.util.NetworkConnectionInterceptor
import com.sysaxiom.coroutines.util.RetrofitHandler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomScopeActivity : AppCompatActivity(), CoroutineScope {

    var job : Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scope)

        pingServer()
    }

    private fun pingServer() {

        launch {
            async(Dispatchers.IO) {
                for (i in 1..15){
                    println("output $i")
                    Thread.sleep(1000)
                }
            }
            runBlocking(Dispatchers.IO) {
                for (i in 1..15){
                    println("output $i")
                    Thread.sleep(1000)
                }
            }
            val response = RetrofitHandler(NetworkConnectionInterceptor(this@CustomScopeActivity)).ping()
            println(response.body())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]!!.cancel()
    }

}
