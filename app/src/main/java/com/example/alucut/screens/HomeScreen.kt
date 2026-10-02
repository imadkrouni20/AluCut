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
import androidx.compose.material.icons.filled.SensorDoor
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alucut.R
import com.example.alucut.data.TemplateType

data class HomeOption(
    val type: TemplateType,
    val title: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    onTypeSelected: (TemplateType) -> Unit
) {
    val options = listOf(
        HomeOption(TemplateType.SINGLE_DOOR, "باب بدفة واحدة", Icons.Default.DoorFront),
        HomeOption(TemplateType.DOUBLE_DOOR, "باب بدفتين", Icons.Default.SensorDoor),
        HomeOption(TemplateType.SINGLE_WINDOW, "نافذة بدفة واحدة", Icons.Default.Window),
        HomeOption(TemplateType.DOUBLE_WINDOW, "نافذة بدفتين", Icons.Default.Window),
        HomeOption(TemplateType.SLIDING_WINDOW, "نافذة بدفتين\nمنزلقتين", Icons.Default.Window),
        HomeOption(TemplateType.CUSTOM, "أشكال أخرى", Icons.Default.Add)
    )

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
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
        ) {
            items(options) { opt ->
                HomeCard(opt) { onTypeSelected(opt.type) }
            }
        }
    }
}

@Composable
fun HomeCard(opt: HomeOption, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCFFF8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = opt.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF5D4037)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = opt.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
        }
    }
}
