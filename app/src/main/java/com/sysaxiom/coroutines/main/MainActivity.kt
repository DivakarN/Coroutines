package com.sysaxiom.coroutines.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sysaxiom.coroutines.R
import com.sysaxiom.coroutines.customscope.CustomScopeActivity
import com.sysaxiom.coroutines.globalscope.GlobalScopeActivity
import com.sysaxiom.coroutines.lifecyclescope.LifeCycleScopeActivity
import com.sysaxiom.coroutines.viewmodelscope.ViewModelScopeActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        life_cycle_scope.setOnClickListener {
            Intent(this, LifeCycleScopeActivity::class.java).also {
                this.startActivity(it)
            }
        }

        view_model_scope.setOnClickListener {
            Intent(this, ViewModelScopeActivity::class.java).also {
                this.startActivity(it)
            }
        }

        custom_scope.setOnClickListener {
            Intent(this, CustomScopeActivity::class.java).also {
                this.startActivity(it)
            }
        }

        global_scope.setOnClickListener {
            Intent(this, GlobalScopeActivity::class.java).also {
                this.startActivity(it)
            }
        }

    }
}
