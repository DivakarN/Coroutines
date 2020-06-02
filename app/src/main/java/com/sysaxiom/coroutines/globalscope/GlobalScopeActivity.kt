package com.sysaxiom.coroutines.globalscope

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sysaxiom.coroutines.R
import com.sysaxiom.coroutines.util.NetworkConnectionInterceptor
import com.sysaxiom.coroutines.util.RetrofitHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class GlobalScopeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_scope)

        pingServer()
    }

    private fun pingServer() {
        GlobalScope.launch {
            GlobalScope.async(Dispatchers.IO) {
                for (i in 1..15){
                    println("output $i")
                    Thread.sleep(1000)
                }
            }
            val response = RetrofitHandler(NetworkConnectionInterceptor(this@GlobalScopeActivity)).ping()
            println(response.body())
        }
    }

}
