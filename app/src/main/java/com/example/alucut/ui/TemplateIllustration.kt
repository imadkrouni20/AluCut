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
fun TemplateIllustration(type: TemplateType, modifier: Modifier = Modifier) {
    val frameColor = Color(0xFF4FC3F7)
    val glassColor = Color(0xFF1A3A52)
    val handleColor = Color(0xFFB0BEC5)

    Box(modifier = modifier.background(Color(0xFF0A0A0A)).padding(30.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val frame = 10.dp.toPx()

            fun drawGlass() { drawRect(glassColor, Offset(0f, 0f), Size(w, h)) }
            fun drawFrame() { drawRect(frameColor, Offset(0f, 0f), Size(w, h), style = Stroke(frame)) }
            fun drawVerticalDivider(x: Float) {
                drawLine(frameColor, Offset(x, 0f), Offset(x, h), frame * 0.7f)
            }
            fun drawHandle(x: Float, y: Float) {
                drawCircle(handleColor, 10.dp.toPx(), Offset(x, y))
                drawLine(handleColor, Offset(x - 20.dp.toPx(), y), Offset(x - 6.dp.toPx(), y), 4.dp.toPx())
            }

            when (type) {
                TemplateType.FIXED_WINDOW -> { drawGlass(); drawFrame() }
                TemplateType.SINGLE_DOOR, TemplateType.SINGLE_WINDOW,
                TemplateType.AWNING_WINDOW, TemplateType.HOPPER_WINDOW -> {
                    drawGlass(); drawFrame()
                    drawRect(frameColor, Offset(w * 0.12f, h * 0.12f),
                        Size(w * 0.76f, h * 0.76f), style = Stroke(frame * 0.4f))
                    drawHandle(w * 0.85f, h * 0.5f)
                }
                TemplateType.DOUBLE_DOOR, TemplateType.DOUBLE_WINDOW -> {
                    drawGlass(); drawFrame()
                    drawVerticalDivider(w / 2)
                    drawRect(frameColor, Offset(w * 0.06f, h * 0.12f),
                        Size(w * 0.4f, h * 0.76f), style = Stroke(frame * 0.4f))
                    drawRect(frameColor, Offset(w * 0.54f, h * 0.12f),
                        Size(w * 0.4f, h * 0.76f), style = Stroke(frame * 0.4f))
                    drawHandle(w * 0.45f, h * 0.5f)
                    drawHandle(w * 0.55f, h * 0.5f)
                }
                TemplateType.TRIPLE_WINDOW -> {
                    drawGlass(); drawFrame()
                    drawVerticalDivider(w / 3); drawVerticalDivider(2 * w / 3)
                    for (i in 0..2) {
                        drawRect(frameColor, Offset(w * (0.05f + 0.32f * i), h * 0.1f),
                            Size(w * 0.26f, h * 0.8f), style = Stroke(frame * 0.4f))
                    }
                }
                TemplateType.SLIDING_DOOR, TemplateType.SLIDING_WINDOW_2 -> {
                    drawGlass(); drawFrame()
                    drawRect(frameColor, Offset(w * 0.05f, h * 0.06f),
                        Size(w * 0.55f, h * 0.88f), style = Stroke(frame * 0.5f))
                    drawRect(frameColor, Offset(w * 0.4f, h * 0.06f),
                        Size(w * 0.55f, h * 0.88f), style = Stroke(frame * 0.5f))
                    drawHandle(w * 0.58f, h * 0.5f); drawHandle(w * 0.42f, h * 0.5f)
                }
                TemplateType.SLIDING_WINDOW_3 -> {
                    drawGlass(); drawFrame()
                    for (i in 0..2) {
                        drawRect(frameColor, Offset(w * (0.03f + 0.32f * i), h * 0.06f),
                            Size(w * 0.28f, h * 0.88f), style = Stroke(frame * 0.4f))
                    }
                }
                TemplateType.SLIDING_WINDOW_4 -> {
                    drawGlass(); drawFrame()
                    for (i in 0..3) {
                        drawRect(frameColor, Offset(w * (0.03f + 0.24f * i), h * 0.06f),
                            Size(w * 0.2f, h * 0.88f), style = Stroke(frame * 0.35f))
                    }
                }
                TemplateType.VERTICAL_SLIDER -> {
                    drawGlass(); drawFrame()
                    drawLine(frameColor, Offset(0f, h / 2), Offset(w, h / 2), frame * 0.6f)
                    drawCircle(handleColor, 8.dp.toPx(), Offset(w / 2, h * 0.35f))
                }
                TemplateType.FOLDING_DOOR -> {
                    drawGlass(); drawFrame()
                    val path = Path().apply {
                        moveTo(w * 0.1f, h * 0.1f); lineTo(w * 0.3f, h * 0.5f); lineTo(w * 0.1f, h * 0.9f)
                        moveTo(w * 0.5f, h * 0.1f); lineTo(w * 0.7f, h * 0.5f); lineTo(w * 0.5f, h * 0.9f)
                    }
                    drawPath(path, frameColor, style = Stroke(frame * 0.5f))
                }
                TemplateType.ARCH_WINDOW -> {
                    drawGlass()
                    val path = Path().apply {
                        moveTo(0f, h); lineTo(0f, h * 0.3f)
                        quadraticBezierTo(w / 2, -h * 0.1f, w, h * 0.3f)
                        lineTo(w, h); close()
                    }
                    drawPath(path, frameColor, style = Stroke(frame))
                }
                TemplateType.WINDOW_DOOR, TemplateType.DOOR_SIDELIGHT -> {
                    drawGlass(); drawFrame()
                    drawVerticalDivider(w * 0.6f)
                    drawRect(frameColor, Offset(w * 0.08f, h * 0.12f),
                        Size(w * 0.44f, h * 0.76f), style = Stroke(frame * 0.4f))
                    drawHandle(w * 0.55f, h * 0.5f)
                }
                TemplateType.DOOR_TRANSOM -> {
                    drawGlass(); drawFrame()
                    drawLine(frameColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), frame * 0.6f)
                    drawHandle(w * 0.85f, h * 0.6f)
                }
                TemplateType.BALCONY -> {
                    drawGlass(); drawFrame()
                    drawVerticalDivider(w * 0.3f); drawVerticalDivider(w * 0.7f)
                    drawHandle(w * 0.35f, h * 0.5f); drawHandle(w * 0.65f, h * 0.5f)
                }
                TemplateType.GRILLE -> {
                    drawGlass()
                    for (i in 0..6) {
                        drawLine(frameColor, Offset(w * i / 6f, 0f), Offset(w * i / 6f, h), 3.dp.toPx())
                        drawLine(frameColor, Offset(0f, h * i / 6f), Offset(w, h * i / 6f), 3.dp.toPx())
                    }
                }
                TemplateType.ROLLER_SHUTTER -> {
                    drawGlass()
                    for (i in 0..15) {
                        val y = h * i / 15f
                        drawLine(frameColor, Offset(0f, y), Offset(w, y), 2.dp.toPx())
                    }
                    drawFrame()
                }
                TemplateType.BLIND -> {
                    drawGlass(); drawFrame()
                    for (i in 0..10) {
                        val y = h * (0.1f + i * 0.08f)
                        drawLine(frameColor, Offset(w * 0.1f, y), Offset(w * 0.9f, y), 2.dp.toPx())
                    }
                }
                TemplateType.CUSTOM -> {
                    val path = Path().apply {
                        val cx = w / 2; val cy = h / 2
                        val r1 = w * 0.4f; val r2 = w * 0.2f
                        for (i in 0 until 10) {
                            val angle = Math.PI * i / 5 - Math.PI / 2
                            val r = if (i % 2 == 0) r1 else r2
                            val x = cx + (r * Math.cos(angle)).toFloat()
                            val y = cy + (r * Math.sin(angle)).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(path, frameColor.copy(alpha = 0.3f))
                    drawPath(path, frameColor, style = Stroke(2.dp.toPx()))
                }
            }
        }
    }
}
