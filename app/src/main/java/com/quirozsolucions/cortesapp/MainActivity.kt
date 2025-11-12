package com.quirozsolucions.cortesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quirozsolucions.cortesapp.ui.HomeScreen
import com.quirozsolucions.cortesapp.ui.ResultScreen
import com.quirozsolucions.cortesapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = isSystemInDarkTheme()) {
                val vm: OptimizerViewModel = viewModel()
                val nav = rememberNavController()

                Surface(modifier = Modifier) {
                    NavHost(navController = nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                vm = vm,
                                onOptimize = {
                                    vm.optimizeAll()
                                    nav.navigate("result")
                                }
                            )
                        }
                        composable(
                            route = "result"
                        ) { ResultScreen(vm = vm, onBack = { nav.popBackStack() }) }
                    }
                }
            }
        }
    }
}
