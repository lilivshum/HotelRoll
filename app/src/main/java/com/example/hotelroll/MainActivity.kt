package com.example.hotelroll

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import com.example.hotelroll.ui.navigation.HotelNavGraph
import com.example.hotelroll.ui.theme.HotelRollTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelroll.ui.resMenu.ReservationMenuScreen
import com.example.hotelroll.ui.resMenu.ReservationViewModel
import com.example.hotelroll.ui.resMenu.ReservationViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.hotelroll.ui.navigation.HotelRoute

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        // Slave devices pull the latest data from Drive each time the app comes to foreground.
        // Master devices push after every write — no pull needed.
        val isMaster = getSharedPreferences("hotelroll_settings", Context.MODE_PRIVATE)
            .getBoolean("is_master", false)
        if (!isMaster) {
            val app = applicationContext as HotelApplication
            if (GoogleSignIn.getLastSignedInAccount(app) != null) {
                lifecycleScope.launch {
                    app.seedJob.join()  // ensure local seed completes before overwriting with Drive data
                    app.syncService.pull()  // silent — errors surface via manual sync in Settings
                }
            } else {
                Toast.makeText(this, "Drive sync not set up — go to Settings to sign in", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotelRollTheme {
                val app = LocalContext.current.applicationContext as HotelApplication

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val navController = rememberNavController()

                val reservationViewModel: ReservationViewModel = viewModel(
                    factory = ReservationViewModelFactory(app.repository)
                )

                // for screen resize for tablet
                val windowSizeClass = calculateWindowSizeClass(this)
                val isTablet = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

                val onSettingsClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(HotelRoute.Settings.route)
                }

                if (isTablet) {
                    Surface(
                        modifier = Modifier
                            .width(320.dp)
                            .fillMaxHeight(),
                        tonalElevation = 2.dp
                    ) {
                        ReservationMenuScreen(
                            reservationViewModel,
                            onReservationClick = { resId ->
                                navController.navigate(HotelRoute.ReservationDetail.createRoute(resId))
                                scope.launch { drawerState.close() }
                            },
                            onAddReservationClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(HotelRoute.CreateRes.createRoute())
                                }
                            },
                            onSettingsClick = { onSettingsClick() }
                        )

                        HotelNavGraph(
                            onMenuClick = { },
                            navController = navController
                        )
                    }
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = MaterialTheme.colorScheme.surface
                            ) {
                                ReservationMenuScreen(
                                    viewModel = reservationViewModel,
                                    onReservationClick = { resId ->
                                        navController.navigate(HotelRoute.ReservationDetail.createRoute(resId))
                                        scope.launch { drawerState.close() }
                                    },
                                    onAddReservationClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(HotelRoute.CreateRes.createRoute())
                                        }
                                    },
                                    onSettingsClick = { onSettingsClick() }
                                )
                            }
                        }
                    ) {
                        HotelNavGraph(
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            },
                            navController = navController
                        )
                    }
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
