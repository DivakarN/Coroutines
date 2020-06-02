package com.sysaxiom.coroutines.viewmodelscope.relation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sysaxiom.coroutines.util.PingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RelationViewModel(
    private val repository: PingRepository
) : ViewModel() {

    init {
        pingServer()
    }

    private fun pingServer() {
        viewModelScope.launch {
            viewModelScope.async(Dispatchers.IO) {
                for (i in 1..15){
                    println("output $i")
                    Thread.sleep(1000)
                }
            }
            val response = repository.ping()
            println(response)
        }
    }

}