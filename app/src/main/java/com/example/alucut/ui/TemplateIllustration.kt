package com.example.alucut.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.alucut.data.TemplateType

@Composable
fun TemplateIllustration(
    type: TemplateType,
    modifier: Modifier = Modifier
) {
    val frameColor = Color(0xFF4FC3F7)
    val glassColor = Color(0xFF1A3A52)
    val handleColor = Color(0xFFB0BEC5)

    Box(
        modifier = modifier
            .background(Color(0xFF0A0A0A))
            .padding(40.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val frame = 12.dp.toPx()

            when (type) {
                TemplateType.SLIDING_WINDOW -> {
                    drawRect(color = glassColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h))
                    drawRect(color = frameColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h),
                        style = Stroke(width = frame))
                    drawLine(color = frameColor,
                        start = Offset(w / 2, 0f), end = Offset(w / 2, h),
                        strokeWidth = frame * 0.7f)
                    drawLine(color = Color.White.copy(alpha = 0.15f),
                        start = Offset(w * 0.1f, h * 0.1f),
                        end = Offset(w * 0.35f, h * 0.4f),
                        strokeWidth = 6.dp.toPx())
                    drawLine(color = Color.White.copy(alpha = 0.15f),
                        start = Offset(w * 0.6f, h * 0.1f),
                        end = Offset(w * 0.85f, h * 0.4f),
                        strokeWidth = 6.dp.toPx())
                }
                TemplateType.SINGLE_DOOR -> {
                    drawRect(color = glassColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h))
                    drawRect(color = frameColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h),
                        style = Stroke(width = frame))
                    val panelTop = h * 0.12f
                    val panelBottom = h * 0.75f
                    val panelLeft = w * 0.15f
                    val panelRight = w * 0.85f
                    drawRect(color = frameColor,
                        topLeft = Offset(panelLeft, panelTop),
                        size = Size(panelRight - panelLeft, panelBottom - panelTop),
                        style = Stroke(width = frame * 0.5f))
                    val handleX = w * 0.88f
                    val handleY = h * 0.5f
                    drawCircle(color = handleColor,
                        radius = 14.dp.toPx(),
                        center = Offset(handleX, handleY))
                    drawLine(color = handleColor,
                        start = Offset(handleX - 30.dp.toPx(), handleY),
                        end = Offset(handleX - 8.dp.toPx(), handleY),
                        strokeWidth = 6.dp.toPx())
                }
                TemplateType.DOUBLE_DOOR_WINDOW -> {
                    drawRect(color = glassColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h))
                    drawRect(color = frameColor,
                        topLeft = Offset(0f, 0f), size = Size(w, h),
                        style = Stroke(width = frame))
                    drawLine(color = frameColor,
                        start = Offset(w / 3, 0f), end = Offset(w / 3, h),
                        strokeWidth = frame * 0.6f)
                    drawLine(color = frameColor,
                        start = Offset(2 * w / 3, 0f), end = Offset(2 * w / 3, h),
                        strokeWidth = frame * 0.6f)
                    val ps = frame * 0.4f
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.05f, h * 0.08f),
                        size = Size(w * 0.25f, h * 0.5f),
                        style = Stroke(width = ps))
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.70f, h * 0.08f),
                        size = Size(w * 0.25f, h * 0.5f),
                        style = Stroke(width = ps))
                }
            }
        }
    }
}
