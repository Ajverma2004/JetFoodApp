package com.ajverma.jetfoodapp.presentation.screens.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SignInTextWithLine(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    text: String,
    lineWidth: Dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(Modifier.width(lineWidth))
        Text(
            text,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(Modifier.width(lineWidth))
    }
}