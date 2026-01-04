package com.example.hotelroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hotelroll.ui.navigation.HotelNavGraph
import com.example.hotelroll.ui.theme.HotelRollTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ModalNavigationDrawer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.ui.resMenu.ReservationMenuScreen
import com.example.hotelroll.ui.resMenu.ReservationViewModel
import com.example.hotelroll.ui.resMenu.ReservationViewModelFactory
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotelRollTheme {
                val app = LocalContext.current.applicationContext as HotelApplication

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val reservationViewModel: ReservationViewModel = viewModel(
                    factory = ReservationViewModelFactory(app.repository)
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.surface
                        ) {
                            ReservationMenuScreen(
                                reservations = reservationViewModel.reservations.value,
                                onReservationClick = {
                                    // navigation later
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    HotelNavGraph(
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            }

        }
    }
}

/*
@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HotelRollTheme {
        HomeScreen("Android")
    }
}*/
