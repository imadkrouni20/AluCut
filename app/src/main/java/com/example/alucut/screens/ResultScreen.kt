package com.example.alucut.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: CuttingResult?,
    onBack: () -> Unit,
    onViewSchema: () -> Unit
) {
    val context = LocalContext.current

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
        },
        bottomBar = {
            if (result != null && result.bars.isNotEmpty()) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            icon = Icons.Default.Print,
                            label = "طباعة",
                            onClick = { printResult(context, result) }
                        )
                        ActionButton(
                            icon = Icons.Default.PictureAsPdf,
                            label = "PDF",
                            onClick = { exportToPdf(context, result) }
                        )
                        ActionButton(
                            icon = Icons.Default.SquareFoot,
                            label = "Schéma",
                            onClick = onViewSchema
                        )
                    }
                }
            }
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
            item { SummaryCard(result) }
            item { TypeSummariesCard(result.typeSummaries) }
            item { RequirementsCard(result) }

            result.typeSummaries.forEach { summary ->
                item {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = colorForType(summary.type).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(12.dp)
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
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ═══════════════════════════════════════════
// دوال الطباعة والتصدير
// ═══════════════════════════════════════════

private fun buildTextLines(result: CuttingResult): List<String> {
    val lines = mutableListOf<String>()
    lines.add("ALUMINIUM CUTTING RESULT")
    lines.add("========================")
    lines.add("")
    lines.add("Total bars: ${result.totalBars}")
    lines.add("Total used: %.1f cm".format(result.totalUsedMm / 10.0))
    lines.add("Total waste: %.1f cm".format(result.totalWasteMm / 10.0))
    lines.add("Waste percent: %.2f %%".format(result.wastePercent))
    lines.add("")
    lines.add("--- By Type ---")
    result.typeSummaries.forEach { s ->
        lines.add("${s.type}: ${s.barCount} bars, waste ${s.wasteMm / 10.0} cm")
    }
    lines.add("")
    lines.add("--- Cutting Plan ---")
    result.bars.forEach { bar ->
        lines.add("")
        lines.add("Bar ${bar.index} (${bar.type}) - ${bar.cuts.size} pieces")
        bar.cuts.forEach { cut ->
            lines.add("  - ${cut.type}: %.1f cm".format(cut.lengthMm / 10.0))
        }
        lines.add("  Used: %.1f cm | Waste: %.1f cm".format(
            bar.usedMm / 10.0, bar.wasteMm / 10.0))
    }
    return lines
}

fun printResult(context: Context, result: CuttingResult) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "AluCut_Result"

    printManager.print(jobName, object : PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }
            val lines = buildTextLines(result)
            val pages = (lines.size / 45) + 1
            val info = PrintDocumentInfo.Builder("alucut_result.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
            callback.onLayoutFinished(info, true)
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback
        ) {
            try {
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint().apply {
                    textSize = 11f
                    color = AndroidColor.BLACK
                }
                val lines = buildTextLines(result)
                var y = 50f
                lines.forEach { line ->
                    if (y > 800f) return@forEach
                    canvas.drawText(line, 40f, y, paint)
                    y += 18f
                }
                document.finishPage(page)
                document.writeTo(FileOutputStream(destination.fileDescriptor))
                document.close()
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
            }
        }
    }, null)
}

fun exportToPdf(context: Context, result: CuttingResult) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply {
        textSize = 11f
        color = AndroidColor.BLACK
    }
    val lines = buildTextLines(result)
    var y = 50f
    lines.forEach { line ->
        if (y > 800f) return@forEach
        canvas.drawText(line, 40f, y, paint)
        y += 18f
    }
    document.finishPage(page)

    val file = File(context.getExternalFilesDir(null), "alucut_result.pdf")
    try {
        document.writeTo(FileOutputStream(file))
        Toast.makeText(
            context,
            "PDF saved: ${file.absolutePath}",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        document.close()
    }
}

// ═══════════════════════════════════════════
// مكونات العرض (كما هي)
// ═══════════════════════════════════════════

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
            RowLine("الطول المستغل", "%.1f م".format(r.totalUsedMm / 1000.0))
            RowLine("إجمالي الهدر", "%.1f م".format(r.totalWasteMm / 1000.0))
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
                        Text("${s.barCount} عمود", fontSize = 13.sp,
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
                    Text("${req.type}:", fontSize = 13.sp,
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
