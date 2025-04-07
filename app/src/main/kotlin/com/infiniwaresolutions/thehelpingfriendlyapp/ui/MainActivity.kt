package com.infiniwaresolutions.thehelpingfriendlyapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.main.MainScreen
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.TheHelpingFriendlyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TheHelpingFriendlyAppTheme {
                MainScreen()
            }
        }
    }
}
