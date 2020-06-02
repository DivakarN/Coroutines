package com.sysaxiom.coroutines.viewmodelscope.relation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sysaxiom.coroutines.util.PingRepository

@Suppress("UNCHECKED_CAST")
class RelationViewModelFactory(
    private val repository: PingRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelationViewModel(repository) as T
    }
}