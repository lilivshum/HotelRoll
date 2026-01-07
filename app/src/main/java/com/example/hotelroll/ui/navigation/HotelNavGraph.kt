package com.example.hotelroll.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hotelroll.ui.res.ReservationDetailScreen
import com.example.hotelroll.ui.res.ReservationDetailViewModel
import com.example.hotelroll.ui.res.ReservationDetailViewModelFactory
import com.example.hotelroll.ui.roll.RollScreen
import com.example.hotelroll.ui.roll.RollViewModel
import com.example.hotelroll.ui.stay.StayDetailScreen

@Composable
fun HotelNavGraph(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = HotelRoute.Roll.route,
        modifier = modifier
    ) {
        composable(HotelRoute.Roll.route) {
            RollScreen(
                onStayClick = { stayId, roomNumber, reservationName->
                    navController.navigate(HotelRoute.StayDetail.createRoute(stayId, roomNumber, reservationName))
                },
                onMenuClick = onMenuClick
            )
        }

        // Stay Detail Screen

        composable(
            route = HotelRoute.StayDetail.route,
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

        // Reservation Detail
        composable(
            route = HotelRoute.ReservationDetail.route,
            arguments = listOf(
                navArgument("reservationId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val reservationId =
                backStackEntry.arguments!!.getLong("reservationId")

            ReservationDetailScreen(
                reservationId,
                onBackClick = { navController.popBackStack()},
                onStayClick = { stayId, roomNumber, reservationName->
                    navController.navigate(HotelRoute.StayDetail.createRoute(stayId, roomNumber, reservationName))
                },
            )
        }
    }
}
