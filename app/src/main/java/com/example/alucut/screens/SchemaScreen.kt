package com.example.alucut.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.BarCut
import com.example.alucut.data.CutTypes
import com.example.alucut.data.CuttingResult
import com.example.alucut.ui.FloatingBackButton

@Composable
fun SchemaScreen(
    result: CuttingResult?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (result == null || result.bars.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد نتيجة")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 70.dp, bottom = 90.dp, start = 14.dp, end = 14.dp),
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
                                Text("${summary.type} — ${summary.barCount} عمود",
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                    items(result.bars.filter { it.type == summary.type }) { bar ->
                        SchemaBarCard(bar, result.barLengthMm)
                    }
                }
            }

            // Bottom bar بأزرار الطباعة
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 6.dp
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalIconButton(
                            onClick = { printResult(context, result) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Print, "طباعة")
                        }
                        Text("طباعة", fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalIconButton(
                            onClick = { saveToDocuments(context, result) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, "PDF")
                        }
                        Text("PDF", fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))
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
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { type ->
                        Row(Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically) {
                            ProfileCrossSection(type, Modifier.size(30.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(type, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileCrossSection(bar.type, Modifier.size(26.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("العمود ${bar.index}",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = colorForType(bar.type))
                }
                Text("الطول: ${barLengthMm / 10} سم",
                    fontSize = 11.sp, color = Color.LightGray)
            }

            Row(
                Modifier.fillMaxWidth().height(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A0A0A))
            ) {
                bar.cuts.forEach { cut ->
                    val w = cut.lengthMm.toFloat() / barLengthMm.toFloat()
                    Box(
                        Modifier.weight(w).fillMaxHeight().padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(colorForType(cut.type).copy(alpha = 0.55f))
                            .border(1.dp, colorForType(cut.type), RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("%.0f".format(cut.lengthMm / 10.0),
                                fontSize = 12.sp, color = Color.White,
                                fontWeight = FontWeight.Bold)
                            Text("سم", fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }
                if (bar.wasteMm > 0) {
                    val w = bar.wasteMm.toFloat() / barLengthMm.toFloat()
                    Box(
                        Modifier.weight(w).fillMaxHeight().padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2A2A2A))
                            .border(1.dp, Color(0xFF555555), RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("%.0f".format(bar.wasteMm / 10.0),
                                fontSize = 10.sp, color = Color.Gray,
                                fontWeight = FontWeight.Bold)
                            Text("هدر", fontSize = 7.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                bar.cuts.forEachIndexed { idx, cut ->
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        ProfileCrossSection(cut.type, Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("قطعة ${idx + 1}", fontSize = 11.sp,
                            color = Color.LightGray, modifier = Modifier.weight(1f))
                        Text("%.1f سم".format(cut.lengthMm / 10.0),
                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }
            }
        }
    }
}

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
                drawRect(color.copy(alpha = 0.25f), Offset(pad, pad), Size(innerW, innerH))
                drawRect(color, Offset(pad, pad), Size(innerW, innerH), style = Stroke(stroke))
                drawRect(Color(0xFF0A0A0A),
                    Offset(pad + stroke * 1.5f, pad + stroke * 1.5f),
                    Size(innerW - stroke * 3f, innerH - stroke * 3f))
            }
            CutTypes.CADRE_OUVRANT -> {
                drawRect(color.copy(alpha = 0.25f), Offset(pad, pad), Size(innerW * 0.7f, innerH))
                drawRect(color, Offset(pad, pad), Size(innerW * 0.7f, innerH), style = Stroke(stroke))
                drawRect(color.copy(alpha = 0.15f), Offset(pad + innerW * 0.7f, pad),
                    Size(innerW * 0.3f, innerH))
                drawRect(color, Offset(pad + innerW * 0.7f, pad),
                    Size(innerW * 0.3f, innerH), style = Stroke(thin))
            }
            CutTypes.Z -> {
                val path = Path().apply {
                    moveTo(pad, pad); lineTo(w - pad, pad)
                    lineTo(w - pad, h * 0.35f); lineTo(pad, h * 0.35f)
                    lineTo(pad, h * 0.65f); lineTo(w - pad, h * 0.65f)
                    lineTo(w - pad, h - pad); lineTo(pad, h - pad); close()
                }
                drawPath(path, color.copy(alpha = 0.3f))
                drawPath(path, color, style = Stroke(thin))
            }
            CutTypes.T -> {
                drawRect(color.copy(alpha = 0.3f), Offset(pad, pad), Size(innerW, h * 0.25f))
                drawRect(color, Offset(pad, pad), Size(innerW, h * 0.25f), style = Stroke(thin))
                drawRect(color.copy(alpha = 0.3f), Offset(w * 0.35f, pad + h * 0.25f),
                    Size(w * 0.3f, h * 0.75f - 2 * pad))
                drawRect(color, Offset(w * 0.35f, pad + h * 0.25f),
                    Size(w * 0.3f, h * 0.75f - 2 * pad), style = Stroke(thin))
            }
            CutTypes.PORT_ROULETTES -> {
                drawRect(color.copy(alpha = 0.25f), Offset(pad + w * 0.15f, pad),
                    Size(w * 0.7f, innerH))
                drawLine(color, Offset(pad + w * 0.15f, pad),
                    Offset(pad + w * 0.15f, h - pad), stroke)
                drawLine(color, Offset(w - pad - w * 0.15f, pad),
                    Offset(w - pad - w * 0.15f, h - pad), stroke)
                drawLine(color, Offset(pad + w * 0.15f, h / 2),
                    Offset(w - pad - w * 0.15f, h / 2), thin)
            }
            CutTypes.PORT_VERREAUX -> {
                val path = Path().apply {
                    moveTo(pad, pad); lineTo(pad, h - pad)
                    lineTo(w - pad, h - pad); lineTo(w - pad, pad)
                }
                drawPath(path, color, style = Stroke(stroke))
                drawRect(color.copy(alpha = 0.2f), Offset(w * 0.35f, pad + stroke),
                    Size(w * 0.3f, h - 2 * pad - 2 * stroke))
                drawLine(color, Offset(w * 0.5f, pad),
                    Offset(w * 0.5f, h * 0.5f), thin)
            }
            CutTypes.CROUCHEMENT -> {
                val path = Path().apply {
                    moveTo(w - pad, pad); lineTo(pad, pad)
                    lineTo(pad, h - pad); lineTo(w - pad, h - pad)
                }
                drawPath(path, color, style = Stroke(stroke))
                drawRect(color.copy(alpha = 0.2f),
                    Offset(pad + stroke, pad + stroke),
                    Size(innerW - 2 * stroke, innerH - 2 * stroke))
            }
            else -> {
                drawRect(color.copy(alpha = 0.25f), Offset(pad, pad), Size(innerW, innerH))
                drawRect(color, Offset(pad, pad), Size(innerW, innerH), style = Stroke(stroke))
            }
        }
    }
}
