package com.sysaxiom.coroutines.viewmodelscope.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sysaxiom.coroutines.R
import com.sysaxiom.mvvmbasics.ui.navigationview.terms.TermsViewModel
import com.sysaxiom.mvvmbasics.ui.navigationview.terms.TermsViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TermsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: TermsViewModelFactory by instance()

    private lateinit var viewModel: TermsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory).get(TermsViewModel::class.java)

    }
}
