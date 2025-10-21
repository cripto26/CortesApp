package com.quirozsolucions.cortesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quirozsolucions.cortesapp.ui.AppRoot
import com.quirozsolucions.cortesapp.ui.theme.CortesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CortesTheme {
                val vm: OptimizerViewModel = viewModel()
                // Alternativa por si el IDE insiste:
                // val vm = androidx.lifecycle.viewmodel.compose.viewModel<OptimizerViewModel>()
                AppRoot(vm)
            }
        }
    }
}
