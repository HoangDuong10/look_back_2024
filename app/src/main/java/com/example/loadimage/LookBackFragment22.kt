package com.example.loadimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson

class LookBackFragment22 : androidx.fragment.app.Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val navController = rememberNavController()
            val fakeData = FakeData(
                order = "12345",
                topNhaBan = "Top 100",
                doanhthu = "100,000,000",
                thang = "6",
                name = "John Doe",
                slKhachHang = "150",
                topYeuThich = "Top 100",
                khachHang = "500",
                danhGiaKH = "22",
                danhGiaCuaBan = "12",
                soLanSD = "20"
            )
            var navigationData = LookBackDataNavigation(
                LookBackConstants.TOTAL_STEPS,
                LookBackConstants.CURRENT_STEP_DEFAULT,
                data = fakeData
            )
            NavHost(navController = navController, startDestination = "screen1") {
                composable(LookBackNavigation.Screen1.route) {
                    Screen1(
                        modifier = Modifier,
                        nextScreen = {
                            val jsonData = Gson().toJson(navigationData)
                            navController.navigate("${LookBackNavigation.Screen2.route}/$jsonData")
                        },
                        dataNavigation = navigationData
                    )
                }
                composable(
                    "${LookBackNavigation.Screen2.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
                    Screen2(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen3.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen1.route}")
                        },
                        data = reminderData
                    )

                }

                composable(
                    "${LookBackNavigation.Screen3.route}/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("data") ?: ""
                    val reminderData = Gson().fromJson(json, LookBackDataNavigation::class.java)
                    Screen3(
                        nextScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen4.route}/$jsonData")
                        },
                        previousScreen = {
                            val jsonData = Gson().toJson(reminderData)
                            navController.navigate("${LookBackNavigation.Screen2.route}/$jsonData")
                        },
                        data = reminderData
                    )

                }
            }
        }
    }
}