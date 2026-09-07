package com.apyar.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.apyar.app.ApyarApp
import com.apyar.app.presentation.navigation.ApyarNavGraph
import com.apyar.app.presentation.theme.ApyarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as ApyarApp).container

        setContent {
            ApyarTheme {
                val navController = rememberNavController()
                ApyarNavGraph(
                    navController = navController,
                    container = appContainer
                )
            }
        }
    }
}
