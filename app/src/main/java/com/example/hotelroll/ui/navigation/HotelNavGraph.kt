package com.example.hotelroll.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hotelroll.ui.roll.RollScreen
import com.example.hotelroll.ui.roll.RollViewModel
import com.example.hotelroll.ui.stay.StayDetailScreen

@Composable
fun HotelNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "roll",
        modifier = modifier
    ) {
        composable("roll") {
            RollScreen(
                onStayClick = { stayId, roomNumber, reservationName->
                    navController.navigate("stay/$stayId/$roomNumber/$reservationName")
                }
            )
        }

        composable(
            route = "stay/{stayId}/{roomNumber}/{reservationName}",
            arguments = listOf(
                navArgument("stayId") {
                    type = NavType.LongType
                },
                navArgument("roomNumber") {
                    type = NavType.StringType
                },
                navArgument("reservationName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            StayDetailScreen(
                stayId = backStackEntry.arguments!!.getLong("stayId"),
                    onBack = { navController.popBackStack()},
                roomNumber = backStackEntry.arguments!!.getString("roomNumber") ?: "Room",
                reservationName = backStackEntry.arguments!!.getString("reservationName") ?:
                ""
            )
        }
    }
}
