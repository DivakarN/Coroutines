package com.sysaxiom.mvvmbasics.ui.navigationview.relation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sysaxiom.coroutines.R
import com.sysaxiom.coroutines.viewmodelscope.relation.RelationViewModel
import com.sysaxiom.coroutines.viewmodelscope.relation.RelationViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RelationFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: RelationViewModelFactory by instance()

    private lateinit var viewModel: RelationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_relation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(RelationViewModel::class.java)

    }

}
