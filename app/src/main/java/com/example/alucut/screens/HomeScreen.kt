package com.example.alucut.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SensorDoor
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.R
import com.example.alucut.data.Template
import com.example.alucut.data.TemplateType

@Composable
fun HomeScreen(
    templates: List<Template>,
    onTemplateSelected: (Template) -> Unit,
    onAddNew: () -> Unit,
    onEditTemplate: (Template) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        // خلفية صورة
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // طبقة بيج شبه شفافة
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E6D3).copy(alpha = 0.75f))
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(14.dp, 30.dp, 14.dp, 14.dp)
        ) {
            items(templates, key = { it.id }) { template ->
                HomeTemplateCard(
                    template = template,
                    onClick = { onTemplateSelected(template) },
                    onEdit = { onEditTemplate(template) }
                )
            }
            item {
                AddNewCard(onClick = onAddNew)
            }
        }
    }
}

@Composable
fun HomeTemplateCard(
    template: Template,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCCFFF8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = iconFor(template.type),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = Color(0xFF5D4037)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = template.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            // زر التعديل
            Box(
                Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "تعديل",
                        tint = Color(0xFF8D6E63),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99D7CCC8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "إضافة نوع",
                modifier = Modifier.size(52.dp),
                tint = Color(0xFF4E342E)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "إضافة نوع جديد",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                textAlign = TextAlign.Center
            )
        }
    }
}

fun iconFor(type: TemplateType): ImageVector = when (type) {
    TemplateType.SINGLE_DOOR -> Icons.Default.DoorFront
    TemplateType.DOUBLE_DOOR -> Icons.Default.SensorDoor
    TemplateType.SINGLE_WINDOW -> Icons.Default.Window
    TemplateType.DOUBLE_WINDOW -> Icons.Default.Window
    TemplateType.SLIDING_WINDOW -> Icons.Default.Window
    TemplateType.CUSTOM -> Icons.Default.Add
}
