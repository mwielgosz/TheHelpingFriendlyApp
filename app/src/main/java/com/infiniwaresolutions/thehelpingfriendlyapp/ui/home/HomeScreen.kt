package com.infiniwaresolutions.thehelpingfriendlyapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.ShowCardSmall

@Composable
fun HomeScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .width(intrinsicSize = IntrinsicSize.Max)
    ) {
        ShowCardSmall("Title", "This is where the contents of the show will go. Setlists, notes, etc.")
    }
}