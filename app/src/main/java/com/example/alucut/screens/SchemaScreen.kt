package com.example.alucut.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.example.alucut.data.BarCut
import com.example.alucut.data.CutItem
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
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("لا توجد نتيجة")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            result.typeSummaries.forEach { summary ->
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = colorForType(summary.type).copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${summary.type} — ${summary.barCount} عمود",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
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
fun SchemaBarCard(bar: BarCut, barLengthMm: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // العنوان
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "العمود ${bar.index}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colorForType(bar.type)
                )
                Text(
                    "الطول الكلي: ${barLengthMm / 10} سم",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }

            // مخطط العمود
            SchemaBar(bar, barLengthMm)

            // الأبعاد أسفل المخطط
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                bar.cuts.forEachIndexed { idx, cut ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(colorForType(cut.type))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "قطعة ${idx + 1}",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                        Text(
                            "%.1f سم".format(cut.lengthMm / 10.0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchemaBar(bar: BarCut, barLengthMm: Int) {
    val cuts = bar.cuts

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0A0A0A))
    ) {
        val totalWidth = size.width - 4f
        val y = 6f
        val barHeight = size.height - 12f
        var xOffset = 2f

        cuts.forEach { cut ->
            val cutWidth = (cut.lengthMm.toFloat() / barLengthMm.toFloat()) * totalWidth
            // القطعة
            drawRect(
                color = colorForType(cut.type),
                topLeft = Offset(xOffset, y),
                size = Size(cutWidth, barHeight)
            )
            // إطار أسود
            drawRect(
                color = Color.Black,
                topLeft = Offset(xOffset, y),
                size = Size(cutWidth, barHeight),
                style = Stroke(width = 2f)
            )
            // النص
            if (cutWidth > 60f) {
                // لا نستخدم النص هنا - Canvas لا يدعم العربي
            }
            xOffset += cutWidth
        }
        // الهدر
        if (bar.wasteMm > 0) {
            val wasteWidth = (bar.wasteMm.toFloat() / barLengthMm.toFloat()) * totalWidth
            drawRect(
                color = Color(0xFF2A2A2A),
                topLeft = Offset(xOffset, y),
                size = Size(wasteWidth, barHeight)
            )
            drawRect(
                color = Color(0xFF555555),
                topLeft = Offset(xOffset, y),
                size = Size(wasteWidth, barHeight),
                style = Stroke(width = 2f)
            )
        }
    }
}
