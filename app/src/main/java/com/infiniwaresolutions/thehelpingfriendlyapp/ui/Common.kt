package com.infiniwaresolutions.thehelpingfriendlyapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

//@Preview(showBackground = true)
@Composable
fun ShowCardSmall(title: String, content: String) {
    Card (
        Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(4.dp),
        content = {
            Text(
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                text = title
            )
            Text(modifier = Modifier.padding(4.dp),
                text = content)
        }
    )
}