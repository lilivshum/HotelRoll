package com.example.hotelroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hotelroll.ui.navigation.HotelNavGraph
import com.example.hotelroll.ui.theme.HotelRollTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotelRollTheme {
                HotelNavGraph()
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
