package com.sysaxiom.coroutines.viewmodelscope

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.sysaxiom.coroutines.R
import kotlinx.android.synthetic.main.activity_view_model_scope.*

class ViewModelScopeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model_scope)

        val navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupWithNavController(bottom_nav_view, navController)
    }
}
