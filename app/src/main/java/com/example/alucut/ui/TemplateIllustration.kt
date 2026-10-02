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
import androidx.compose.ui.graphics.Path
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

    Box(modifier = modifier.background(Color(0xFF0A0A0A)).padding(30.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val frame = 10.dp.toPx()

            fun drawFrame() {
                drawRect(color = frameColor, topLeft = Offset(0f, 0f),
                    size = Size(w, h), style = Stroke(width = frame))
            }

            fun drawGlass() {
                drawRect(color = glassColor, topLeft = Offset(0f, 0f),
                    size = Size(w, h))
            }

            fun drawHandle(x: Float, y: Float) {
                drawCircle(color = handleColor, radius = 12.dp.toPx(),
                    center = Offset(x, y))
                drawLine(color = handleColor,
                    start = Offset(x - 25.dp.toPx(), y),
                    end = Offset(x - 6.dp.toPx(), y),
                    strokeWidth = 5.dp.toPx())
            }

            when (type) {
                TemplateType.SINGLE_DOOR,
                TemplateType.SINGLE_WINDOW -> {
                    drawGlass(); drawFrame()
                    // إطار زجاجي داخلي
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.15f, h * 0.12f),
                        size = Size(w * 0.7f, h * 0.6f),
                        style = Stroke(width = frame * 0.4f))
                    drawHandle(w * 0.85f, h * 0.5f)
                }
                TemplateType.DOUBLE_DOOR,
                TemplateType.DOUBLE_WINDOW -> {
                    drawGlass(); drawFrame()
                    // فاصل
                    drawLine(color = frameColor,
                        start = Offset(w / 2, 0f), end = Offset(w / 2, h),
                        strokeWidth = frame * 0.7f)
                    // إطاران داخليان
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.06f, h * 0.12f),
                        size = Size(w * 0.38f, h * 0.6f),
                        style = Stroke(width = frame * 0.4f))
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.56f, h * 0.12f),
                        size = Size(w * 0.38f, h * 0.6f),
                        style = Stroke(width = frame * 0.4f))
                    drawHandle(w * 0.45f, h * 0.5f)
                    drawHandle(w * 0.55f, h * 0.5f)
                }
                TemplateType.SLIDING_WINDOW -> {
                    drawGlass(); drawFrame()
                    // دفتان منزلقتان متراكبتان
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.05f, h * 0.05f),
                        size = Size(w * 0.55f, h * 0.9f),
                        style = Stroke(width = frame * 0.5f))
                    drawRect(color = frameColor,
                        topLeft = Offset(w * 0.4f, h * 0.05f),
                        size = Size(w * 0.55f, h * 0.9f),
                        style = Stroke(width = frame * 0.5f))
                    drawHandle(w * 0.6f, h * 0.5f)
                    drawHandle(w * 0.4f, h * 0.5f)
                }
                TemplateType.CUSTOM -> {
                    // نجمة / شكل مخصص
                    val path = Path().apply {
                        moveTo(w / 2, h * 0.05f)
                        lineTo(w * 0.6f, h * 0.4f)
                        lineTo(w * 0.95f, h * 0.4f)
                        lineTo(w * 0.68f, h * 0.6f)
                        lineTo(w * 0.8f, h * 0.95f)
                        lineTo(w / 2, h * 0.72f)
                        lineTo(w * 0.2f, h * 0.95f)
                        lineTo(w * 0.32f, h * 0.6f)
                        lineTo(w * 0.05f, h * 0.4f)
                        lineTo(w * 0.4f, h * 0.4f)
                        close()
                    }
                    drawPath(path, frameColor.copy(alpha = 0.3f))
                    drawPath(path, frameColor, style = Stroke(width = frame * 0.5f))
                }
            }
        }
    }
}
