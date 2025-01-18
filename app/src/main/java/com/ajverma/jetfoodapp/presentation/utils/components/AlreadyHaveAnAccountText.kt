package com.ajverma.jetfoodapp.presentation.utils.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlreadyHaveAnAccountText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = buildAnnotatedString {
            append("Already have an account? ")
            pushStringAnnotation(tag = text, annotation = text)
            withStyle(style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
            ) {
                append(text)
            }
            pop()
        },
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, color = Color.White),
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clickable {
                onClick()
            }
    )
}