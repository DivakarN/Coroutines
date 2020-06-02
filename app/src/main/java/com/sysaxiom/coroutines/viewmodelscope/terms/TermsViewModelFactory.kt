package com.sysaxiom.mvvmbasics.ui.navigationview.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sysaxiom.coroutines.util.PingRepository

@Suppress("UNCHECKED_CAST")
class TermsViewModelFactory(
    private val repository: PingRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TermsViewModel(repository) as T
    }
}