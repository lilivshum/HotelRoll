package com.example.hotelroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.activity.viewModels
import com.example.hotelroll.ui.roll.RollScreen
import com.example.hotelroll.ui.theme.HotelRollTheme
import com.example.hotelroll.ui.roll.RollViewModel
import com.example.hotelroll.ui.roll.RollViewModelFactory

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
                    RollScreen(viewModel = viewModel)
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
