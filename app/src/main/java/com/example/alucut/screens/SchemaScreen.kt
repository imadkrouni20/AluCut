package com.example.alucut.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.BarCut
import com.example.alucut.data.CutTypes
import com.example.alucut.data.CuttingResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemaScreen(
    result: CuttingResult?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schéma", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (result == null || result.bars.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("لا توجد نتيجة") }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LegendCard() }

            result.typeSummaries.forEach { summary ->
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = colorForType(summary.type).copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(10.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileCrossSection(summary.type, Modifier.size(36.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${summary.type} — ${summary.barCount} عمود",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                items(result.bars.filter { it.type == summary.type }) { bar ->
                    SchemaBarCard(bar, result.barLengthMm)
                }
            }
        }
    }
}

@Composable
fun LegendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("دليل المقاطع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            val types = listOf(
                CutTypes.CADRE, CutTypes.CADRE_OUVRANT,
                CutTypes.Z, CutTypes.T,
                CutTypes.PORT_ROULETTES, CutTypes.PORT_VERREAUX,
                CutTypes.CROUCHEMENT
            )
            types.chunked(2).forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { type ->
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileCrossSection(type, Modifier.size(30.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                type,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SchemaBarCard(bar: BarCut, barLengthMm: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // رأس البطاقة
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileCrossSection(bar.type, Modifier.size(26.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "العمود ${bar.index}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorForType(bar.type)
                    )
                }
                Text(
                    "الطول: ${barLengthMm / 10} سم",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }

            // ═══ المخطط البصري للعمود مع الأبعاد ═══
            Row(
                Modifier.fillMaxWidth().height(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A0A0A))
            ) {
                bar.cuts.forEach { cut ->
                    val w = cut.lengthMm.toFloat() / barLengthMm.toFloat()
                    Box(
                        Modifier
                            .weight(w)
                            .fillMaxHeight()
                            .padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(colorForType(cut.type).copy(alpha = 0.55f))
                            .border(1.dp, colorForType(cut.type), RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "%.0f".format(cut.lengthMm / 10.0),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "سم",
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                if (bar.wasteMm > 0) {
                    val w = bar.wasteMm.toFloat() / barLengthMm.toFloat()
                    Box(
                        Modifier
                            .weight(w)
                            .fillMaxHeight()
                            .padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2A2A2A))
                            .border(1.dp, Color(0xFF555555), RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "%.0f".format(bar.wasteMm / 10.0),
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text("هدر", fontSize = 7.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // ═══ قائمة القطع مع المقاطع الجانبية ═══
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                bar.cuts.forEachIndexed { idx, cut ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileCrossSection(cut.type, Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "قطعة ${idx + 1}",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "%.1f سم".format(cut.lengthMm / 10.0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * رسم المقطع الجانبي لكل نوع بروفيل
 */
@Composable
fun ProfileCrossSection(type: String, modifier: Modifier = Modifier) {
    val color = colorForType(type)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = 3.dp.toPx()
        val innerW = w - 2 * pad
        val innerH = h - 2 * pad
        val stroke = 2f.dp.toPx()
        val thin = 1.2f.dp.toPx()

        when (type) {
            CutTypes.CADRE -> {
                // إطار مربع أجوف
                drawRect(
                    color = color.copy(alpha = 0.25f),
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, innerH)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, innerH),
                    style = Stroke(width = stroke)
                )
                // تجويف داخلي
                drawRect(
                    color = Color(0xFF0A0A0A),
                    topLeft = Offset(pad + stroke * 1.5f, pad + stroke * 1.5f),
                    size = Size(innerW - stroke * 3f, innerH - stroke * 3f)
                )
            }

            CutTypes.CADRE_OUVRANT -> {
                // إطار مع فتحة على الجانب
                drawRect(
                    color = color.copy(alpha = 0.25f),
                    topLeft = Offset(pad, pad),
                    size = Size(innerW * 0.7f, innerH)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(pad, pad),
                    size = Size(innerW * 0.7f, innerH),
                    style = Stroke(width = stroke)
                )
                // جزء مفتوح
                drawRect(
                    color = color.copy(alpha = 0.15f),
                    topLeft = Offset(pad + innerW * 0.7f, pad),
                    size = Size(innerW * 0.3f, innerH)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(pad + innerW * 0.7f, pad),
                    size = Size(innerW * 0.3f, innerH),
                    style = Stroke(width = thin)
                )
            }

            CutTypes.Z -> {
                // شكل Z
                val path = Path().apply {
                    moveTo(pad, pad)
                    lineTo(w - pad, pad)
                    lineTo(w - pad, h * 0.35f)
                    lineTo(pad, h * 0.35f)
                    lineTo(pad, h * 0.65f)
                    lineTo(w - pad, h * 0.65f)
                    lineTo(w - pad, h - pad)
                    lineTo(pad, h - pad)
                    close()
                }
                drawPath(path, color.copy(alpha = 0.3f))
                drawPath(path, color, style = Stroke(width = thin))
            }

            CutTypes.T -> {
                // شكل T
                drawRect(
                    color = color.copy(alpha = 0.3f),
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, h * 0.25f)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, h * 0.25f),
                    style = Stroke(width = thin)
                )
                drawRect(
                    color = color.copy(alpha = 0.3f),
                    topLeft = Offset(w * 0.35f, pad + h * 0.25f),
                    size = Size(w * 0.3f, h * 0.75f - 2 * pad)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.35f, pad + h * 0.25f),
                    size = Size(w * 0.3f, h * 0.75f - 2 * pad),
                    style = Stroke(width = thin)
                )
            }

            CutTypes.PORT_ROULETTES -> {
                // شكل H (مسار مع عمودين)
                drawRect(
                    color = color.copy(alpha = 0.25f),
                    topLeft = Offset(pad + w * 0.15f, pad),
                    size = Size(w * 0.7f, innerH)
                )
                drawLine(
                    color, Offset(pad + w * 0.15f, pad),
                    Offset(pad + w * 0.15f, h - pad), stroke
                )
                drawLine(
                    color, Offset(w - pad - w * 0.15f, pad),
                    Offset(w - pad - w * 0.15f, h - pad), stroke
                )
                drawLine(
                    color, Offset(pad + w * 0.15f, h / 2),
                    Offset(w - pad - w * 0.15f, h / 2), thin
                )
            }

            CutTypes.PORT_VERREAUX -> {
                // شكل U مع فتحة زجاج
                val path = Path().apply {
                    moveTo(pad, pad)
                    lineTo(pad, h - pad)
                    lineTo(w - pad, h - pad)
                    lineTo(w - pad, pad)
                }
                drawPath(path, color, style = Stroke(width = stroke))
                // فتحة زجاج في المنتصف
                drawRect(
                    color = color.copy(alpha = 0.2f),
                    topLeft = Offset(w * 0.35f, pad + stroke),
                    size = Size(w * 0.3f, h - 2 * pad - 2 * stroke)
                )
                drawLine(
                    color, Offset(w * 0.5f, pad),
                    Offset(w * 0.5f, h * 0.5f), thin
                )
            }

            CutTypes.CROUCHEMENT -> {
                // شكل C
                val path = Path().apply {
                    moveTo(w - pad, pad)
                    lineTo(pad, pad)
                    lineTo(pad, h - pad)
                    lineTo(w - pad, h - pad)
                }
                drawPath(path, color, style = Stroke(width = stroke))
                drawRect(
                    color = color.copy(alpha = 0.2f),
                    topLeft = Offset(pad + stroke, pad + stroke),
                    size = Size(innerW - 2 * stroke, innerH - 2 * stroke)
                )
            }

            else -> {
                drawRect(
                    color = color.copy(alpha = 0.25f),
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, innerH)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(pad, pad),
                    size = Size(innerW, innerH),
                    style = Stroke(width = stroke)
                )
            }
        }
    }
}
