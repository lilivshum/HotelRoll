package com.example.hotelroll.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hotelroll.HotelApplication
import com.example.hotelroll.ui.createStay.CreateStayScreen
import com.example.hotelroll.ui.createStay.CreateStayViewModel
import com.example.hotelroll.ui.createStay.CreateStayViewModelFactory
import com.example.hotelroll.ui.res.ReservationDetailScreen
import com.example.hotelroll.ui.roll.RollScreen
import com.example.hotelroll.ui.stay.StayDetailScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.ui.createRes.CreateResScreen
import com.example.hotelroll.ui.stay.StayDetailViewModel
import com.example.hotelroll.ui.stay.StayDetailViewModelFactory
import java.time.LocalDate

// created a stay mode that determines how the stay is being viewed
enum class StayMode{
    CREATE,
    EDIT
}

@Composable
fun HotelNavGraph(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    navController: NavHostController
) {
    val app = LocalContext.current.applicationContext as HotelApplication

    NavHost(
        navController = navController,
        startDestination = HotelRoute.Roll.route,
        modifier = modifier
    ) {
        composable(HotelRoute.Roll.route) {
            RollScreen(
                // for a confirmed stay only (should changed the name to onConfirmedStayClick)
                onStayClick = { stayId, roomNumber, reservationName->
                    navController.navigate(
                        HotelRoute.StayDetail.createRoute(stayId, roomNumber, reservationName))
                },
                onMenuClick = onMenuClick,

                onEmptyClick = {roomId, roomNumber, date, mode, stayId->
                    navController.navigate(
                        HotelRoute.CreateStay.createRoute(roomId, roomNumber, date, mode, stayId)
                    )
                }
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


            val stayId = backStackEntry.arguments!!.getLong("stayId")

            val viewModel: StayDetailViewModel = viewModel(
                factory = StayDetailViewModelFactory(
                    repository = app.repository,
                    stayId = stayId
                )
            )

            StayDetailScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp()},
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
                onBackClick = { navController.navigateUp()},
                onStayClick = { stayId, roomNumber, reservationName->
                    navController.navigate(HotelRoute.StayDetail.createRoute(stayId, roomNumber, reservationName))
                },
            )
        }

        // create stay navigation route
        composable(
            route = HotelRoute.CreateStay.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.LongType },
                navArgument("roomNumber") {
                    type = NavType.StringType
                },
                navArgument("date") {
                    type = NavType.StringType
                },
                navArgument("mode"){
                    type = NavType.StringType
                },
                navArgument("stayId"){
                    type = NavType.StringType
                    nullable = true
                }

            )
        ) { backStackEntry ->

            val viewModel: CreateStayViewModel = viewModel(
                backStackEntry,
                factory = CreateStayViewModelFactory(app.repository)
            )

            CreateStayScreen(
                onSaved = { navController.popBackStack() },
                onCancel = {navController.popBackStack() }
            )
        }

        // create Res navigation pipeline
        composable(
            route = HotelRoute.CreateRes.route
        ) {
            CreateResScreen(
                onBack = {navController.navigateUp()},
                onSave = {navController.popBackStack()},
                onCancel = {navController.popBackStack()}
            )
        }


    }
}
