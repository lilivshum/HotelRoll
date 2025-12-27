package com.example.hotelroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.viewModels
import com.example.hotelroll.ui.roll.RoomScreen
import com.example.hotelroll.ui.theme.HotelRollTheme
import com.example.hotelroll.ui.room.RollViewModel
import com.example.hotelroll.ui.room.RollViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: RollViewModel by viewModels{
        RollViewModelFactory(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotelRollTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoomScreen(viewModel = viewModel)
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
