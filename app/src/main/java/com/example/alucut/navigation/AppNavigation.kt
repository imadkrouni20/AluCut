package com.example.alucut.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alucut.data.*
import com.example.alucut.screens.InputScreen
import com.example.alucut.screens.ResultScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var result by remember { mutableStateOf<CuttingResult?>(null) }

    NavHost(navController = navController, startDestination = "input") {
        composable("input") {
            InputScreen(
                onCalculate = { input, config ->
                    val reqs = WindowCalculator.calculateRequirements(input, config)
                    result = CuttingOptimizer.optimize(
                        reqs, config.barLengthCm, config.kerfCm
                    )
                    navController.navigate("result")
                }
            )
        }
        composable("result") {
            ResultScreen(result = result, onBack = { navController.popBackStack() })
        }
    }
}
