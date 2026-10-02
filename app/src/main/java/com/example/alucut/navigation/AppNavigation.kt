package com.example.alucut.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alucut.data.*
import com.example.alucut.screens.*

@Composable
fun AppNavigation(templateRepo: TemplateRepository) {
    val navController = rememberNavController()

    var templates by remember { mutableStateOf(templateRepo.getTemplates()) }
    var currentTemplate by remember { mutableStateOf<Template?>(null) }
    var accumulatedItems by remember { mutableStateOf<List<InputItem>>(emptyList()) }
    var nextItemId by remember { mutableStateOf(1) }
    var currentResult by remember { mutableStateOf<CuttingResult?>(null) }

    fun refresh() { templates = templateRepo.getTemplates() }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                templates = templates,
                onTemplateSelected = { t ->
                    currentTemplate = t
                    navController.navigate("input")
                },
                onAddNew = {
                    currentTemplate = null
                    navController.navigate("edit_template/true")
                },
                onEditTemplate = { t ->
                    currentTemplate = t
                    navController.navigate("edit_template/false")
                }
            )
        }

        composable("input") {
            val t = currentTemplate
            if (t == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                InputScreen(
                    template = t,
                    items = accumulatedItems,
                    onBack = { navController.popBackStack() },
                    onAddItem = { item ->
                        accumulatedItems = accumulatedItems + item.copy(id = nextItemId++)
                    },
                    onRemoveItem = { id ->
                        accumulatedItems = accumulatedItems.filterNot { it.id == id }
                    },
                    onAddAnotherType = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onCalculate = {
                        if (accumulatedItems.isNotEmpty()) {
                            val reqs = Calculator.calculateAll(accumulatedItems, templates)
                            val firstT = templates.find { it.id == accumulatedItems.first().templateId }
                            val barLen = firstT?.barLengthCm ?: 600.0
                            val kerf = firstT?.kerfCm ?: 0.3
                            currentResult = CuttingOptimizer.optimize(reqs, barLen, kerf)
                            navController.navigate("result")
                        }
                    }
                )
            }
        }

        composable("result") {
            ResultScreen(
                result = currentResult,
                onBack = { navController.popBackStack() },
                onViewSchema = { navController.navigate("schema") }
            )
        }

        composable("schema") {
            SchemaScreen(
                result = currentResult,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_template/{isNew}",
            arguments = listOf(navArgument("isNew") { type = NavType.BoolType })
        ) { back ->
            val isNew = back.arguments?.getBoolean("isNew") ?: true
            TemplateEditorScreen(
                template = if (isNew) null else currentTemplate,
                isNew = isNew,
                onBack = { navController.popBackStack() },
                onSave = { t ->
                    if (isNew) templateRepo.addTemplate(t)
                    else templateRepo.updateTemplate(t)
                    refresh()
                    navController.popBackStack()
                }
            )
        }
    }
}
