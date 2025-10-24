package com.quirozsolucions.cortesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quirozsolucions.cortesapp.ui.AppRoot
import com.quirozsolucions.cortesapp.ui.theme.AppTheme
import androidx.compose.ui.platform.LocalConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = isSystemInDarkTheme()) {  // <- nombre correcto del parámetro
                val vm: OptimizerViewModel = viewModel()
                val cfg = LocalConfiguration.current
                val isWide = cfg.screenWidthDp >= 720
                Surface(Modifier.fillMaxSize()) {
                    AppRoot(vm, isWide)
                }
            }
        }
    }
}
