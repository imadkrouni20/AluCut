package com.example.alucut.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.data.Template
import com.example.alucut.data.TemplateType
import com.example.alucut.ui.FloatingBackButton

@Composable
fun TemplatesScreen(
    currentType: TemplateType,
    allTemplates: List<Template>,
    onBack: () -> Unit,
    onTemplateSelected: (Template) -> Unit,
    onEdit: (Template) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Template) -> Unit
) {
    val filtered = allTemplates.filter { it.type == currentType }
    var toDelete by remember { mutableStateOf<Template?>(null) }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 70.dp, bottom = 90.dp, start = 14.dp, end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center) {
                        Text("لا توجد قوالب. اضغط + للإضافة.",
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            items(filtered, key = { it.id }) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onTemplateSelected(template) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(template.name, fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${template.params.size} إعداد",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onEdit(template) }) {
                            Icon(Icons.Default.Edit, "تعديل",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { toDelete = template }) {
                            Icon(Icons.Default.Delete, "حذف",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        FloatingBackButton(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, "إضافة")
        }
    }

    toDelete?.let { t ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("حذف \"${t.name}\"؟") },
            confirmButton = {
                TextButton(onClick = { onDelete(t); toDelete = null }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("إلغاء") }
            }
        )
    }
}
