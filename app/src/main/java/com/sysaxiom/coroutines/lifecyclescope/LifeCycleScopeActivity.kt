package com.sysaxiom.coroutines.lifecyclescope

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sysaxiom.coroutines.R
import com.sysaxiom.coroutines.util.NetworkConnectionInterceptor
import com.sysaxiom.coroutines.util.RetrofitHandler
import kotlinx.coroutines.*

class LifeCycleScopeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycle_scope)

        pingServer()
    }

    private fun pingServer() {
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
    }

}
