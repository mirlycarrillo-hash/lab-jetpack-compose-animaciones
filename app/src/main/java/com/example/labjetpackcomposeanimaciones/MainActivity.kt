package com.example.labjetpackcomposeanimaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labjetpackcomposeanimaciones.ui.theme.LabJetpackComposeAnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabJetpackComposeAnimacionesTheme {
                AnimateSizeAndPositionScreen()
            }
        }
    }
}

@Composable
fun AnimateSizeAndPositionScreen() {
    // Estado para alternar entre dos posiciones y tamaños
    var isExpanded by remember { mutableStateOf(false) }

    val animatedSize by animateDpAsState(
        targetValue = if (isExpanded) 240.dp else 120.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "SizeAnimation"
    )

    val animatedOffsetY by animateDpAsState(
        targetValue = if (isExpanded) (-60).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "OffsetAnimation"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(animatedSize)
                    .offset(y = animatedOffsetY)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFF9800)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isExpanded) "Expandido" else "Normal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { isExpanded = !isExpanded },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF673AB7)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isExpanded) "Restaurar Posición" else "Mover y Escalar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}