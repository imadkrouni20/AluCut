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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(result: CuttingResult?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("النتيجة", fontWeight = FontWeight.Bold) },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1️⃣ الملخص الكلي
            item { SummaryCard(result) }

            // 2️⃣ ملخص حسب كل نوع
            item { TypeSummariesCard(result.typeSummaries) }

            // 3️⃣ القطع المطلوبة
            item { RequirementsCard(result) }

            // 4️⃣ تفصيل الأعمدة لكل نوع
            result.typeSummaries.forEach { summary ->
                item {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = colorForType(summary.type).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(colorForType(summary.type))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "${summary.type} — ${summary.barCount} عمود",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                items(result.bars.filter { it.type == summary.type }) { bar ->
                    BarCard(bar, result.barLengthMm)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(r: CuttingResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("الملخص الكلي", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            RowLine("إجمالي الأعمدة", "${r.totalBars}")
            RowLine("الطول المستغل",
                "%.1f م".format(r.totalUsedMm / 1000.0))
            RowLine("إجمالي الهدر",
                "%.1f م".format(r.totalWasteMm / 1000.0))
            RowLine("نسبة الهدر", "%.2f %%".format(r.wastePercent))
        }
    }
}

@Composable
fun TypeSummariesCard(summaries: List<TypeSummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("حسب النوع", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            summaries.forEach { s ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(10.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(colorForType(s.type))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(s.type, fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text("${s.barCount} عمود",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "هدر: %.1f سم (%.1f%%)".format(s.wasteMm / 10.0, s.wastePercent),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RowLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun RequirementsCard(r: CuttingResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("القطع المطلوبة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            r.requirements.forEach { req ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${req.type}:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("%.1f × %d".format(req.lengthCm, req.quantity),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun BarCard(bar: BarCut, barLengthMm: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("العمود ${bar.index}",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = colorForType(bar.type))
                Text("${bar.cuts.size} قطعة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            BarVisualization(bar.cuts, barLengthMm, bar.wasteMm)

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                bar.cuts.forEach { cut ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• ${cut.type}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("%.1f سم".format(cut.lengthMm / 10.0),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("مستخدم: %.1f سم".format(bar.usedMm / 10.0),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("هدر: %.1f سم".format(bar.wasteMm / 10.0),
                    fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    color = if (bar.wasteMm < 50) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun colorForType(type: String): Color = when (type) {
    CutTypes.CADRE -> Color(0xFF4FC3F7)
    CutTypes.CADRE_OUVRANT -> Color(0xFF29B6F6)
    CutTypes.PORT_ROULETTES -> Color(0xFF81C784)
    CutTypes.PORT_VERREAUX -> Color(0xFFFFB74D)
    CutTypes.CROUCHEMENT -> Color(0xFFBA68C8)
    CutTypes.Z -> Color(0xFF4DB6AC)
    CutTypes.T -> Color(0xFFE57373)
    else -> Color(0xFF9E9E9E)
}

@Composable
fun BarVisualization(cuts: List<CutItem>, barLengthMm: Int, wasteMm: Int) {
    Row(
        Modifier.fillMaxWidth().height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        cuts.forEach { cut ->
            val w = cut.lengthMm.toFloat() / barLengthMm.toFloat()
            Box(
                Modifier.weight(w).fillMaxHeight().padding(1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colorForType(cut.type)),
                contentAlignment = Alignment.Center
            ) {
                if (cut.lengthMm > 800) {
                    Text("%.0f".format(cut.lengthMm / 10.0),
                        fontSize = 9.sp, fontWeight = FontWeight.Bold,
                        color = Color.Black)
                }
            }
        }
        if (wasteMm > 0) {
            val w = wasteMm.toFloat() / barLengthMm.toFloat()
            Box(
                Modifier.weight(w).fillMaxHeight().padding(1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF3A3A3A))
            )
        }
    }
}
