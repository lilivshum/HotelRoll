package com.example.hotelroll.ui.utilities

import com.example.hotelroll.data.model.Stay

// bundles a stay with its reservation name for display purposes
data class StayWithResName(
    val stay: Stay,
    val resName: String
)
