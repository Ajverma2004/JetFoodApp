import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajverma.jetfoodapp.ui.theme.JetFoodAppTheme

@Composable
fun FoodProgressIndicator() {
    val infiniteTransition = rememberInfiniteTransition()

    // Utensil rotation animation
    val utensilRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    // Meatball bounce animation
    val meatballOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.0f at 0 with LinearEasing
                1.0f at 600 with FastOutSlowInEasing
                0.0f at 1200 with LinearEasing
            }
        )
    )

    Canvas(modifier = Modifier.size(150.dp)) {
        val center = size.center
        val strokeWidth = 8.dp.toPx()
        val meatballRadius = 16.dp.toPx()
        val bounceRange = 40.dp.toPx()

        // Draw rotating utensils
        drawRotatingUtensils(center, utensilRotation, strokeWidth)

        // Draw bouncing meatball
        drawBouncingMeatball(center, meatballOffset, bounceRange, meatballRadius)
    }
}

private fun DrawScope.drawRotatingUtensils(
    center: Offset,
    degrees: Float,
    strokeWidth: Float
) {
    // Fork
    rotate(degrees = degrees, pivot = center) {
        drawLine(
            color = Color(0xFF6D4C41),
            start = center.copy(x = center.x - 40.dp.toPx()),
            end = center.copy(x = center.x + 40.dp.toPx()),
            strokeWidth = strokeWidth
        )
        repeat(4) { i ->
            val spacing = 8.dp.toPx()
            drawLine(
                color = Color(0xFF6D4C41),
                start = center.copy(
                    x = center.x - 30.dp.toPx() + i * spacing,
                    y = center.y - 30.dp.toPx()
                ),
                end = center.copy(
                    x = center.x - 30.dp.toPx() + i * spacing,
                    y = center.y + 30.dp.toPx()
                ),
                strokeWidth = strokeWidth / 2
            )
        }
    }

    // Knife (rotates in opposite direction)
    rotate(degrees = -degrees, pivot = center) {
        drawLine(
            color = Color(0xFF757575),
            start = center.copy(y = center.y - 40.dp.toPx()),
            end = center.copy(y = center.y + 40.dp.toPx()),
            strokeWidth = strokeWidth
        )
        drawPath(
            path = Path().apply {
                moveTo(center.x, center.y - 30.dp.toPx())
                lineTo(center.x + 20.dp.toPx(), center.y - 10.dp.toPx())
                lineTo(center.x - 20.dp.toPx(), center.y - 10.dp.toPx())
                close()
            },
            color = Color(0xFF757575)
        )
    }
}

private fun DrawScope.drawBouncingMeatball(
    center: Offset,
    progress: Float,
    bounceRange: Float,
    radius: Float
) {
    val yOffset = (1 - progress) * bounceRange
    drawCircle(
        color = Color(0xFFD32F2F),
        center = center.copy(y = center.y - yOffset),
        radius = radius,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodProgressIndicator() {
    JetFoodAppTheme {

    FoodProgressIndicator()
    }
}