package com.example.loadimage

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

sealed class ReminderNavigation(
    val route: String
) {
    object GReminder1 : ReminderNavigation("reminder11")
    object GReminder2 : ReminderNavigation("reminder22")
    object GReminder3 : ReminderNavigation("reminder33")
}
fun NavGraphBuilder.reminderGraph(
    navController: NavController,
    data: String? = null
) {
    navigation(
        startDestination = "${ReminderNavigation.GReminder1.route}/{data}",
        route = "reminder"
    ) {
        composable(
            "${ReminderNavigation.GReminder1.route}/{data}",
            arguments = listOf(navArgument("data") { type = NavType.StringType })
        ) {
            var navigationData = ReminderDataNavigation(
                ReminderConstants.TOTAL_STEPS,
                ReminderConstants.CURRENT_STEP_DEFAULT,
            )
//            navController.currentBackStackEntry?.arguments?.getString("data")?.let {
//                navigationData = Gson().fromJsonData(it)
//            }
//            if (navigationData.reminderData == null) {
//                data?.let {
//                    val reminder: ReminderData = Gson().fromJsonData(data)
//                    navigationData.reminderData = reminder
//                }
//            }
//            MultiLinearDeterminateIndicator(
//                modifier = Modifier,
//                nextScreen = {},
//                dataNavigation =
//            )
        }
    }
}