package com.example.labjetpackcomposeanimaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labjetpackcomposeanimaciones.ui.theme.LabJetpackComposeAnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabJetpackComposeAnimacionesTheme {
                GamePrototypeScreen()
            }
        }
    }
}

enum class GameState {
    MENU, BATTLE, VICTORY
}

@Composable
fun GamePrototypeScreen() {
    var gameState by remember { mutableStateOf(GameState.MENU) }
    var isAttacking by remember { mutableStateOf(false) }
    var powerUpActive by remember { mutableStateOf(false) }

    val heroOffset by animateDpAsState(
        targetValue = if (isAttacking) 90.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "HeroOffsetAnimation"
    )

    val arenaColor by animateColorAsState(
        targetValue = if (powerUpActive) Color(0xFF7B1FA2) else Color(0xFF3F51B5),
        animationSpec = tween(durationMillis = 800),
        label = "ArenaColorAnimation"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎮 RPG Prototype: Cyber Quest",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = gameState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "GameScreenTransition"
            ) { state ->
                when (state) {
                    GameState.MENU -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF263238)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "¡Bienvenido Héroe!",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Prepárate para la batalla animada",
                                    color = Color(0xFFB0BEC5),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { gameState = GameState.BATTLE },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Iniciar Partida", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                    GameState.BATTLE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(arenaColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚔️ ZONA DE COMBATE ⚔️",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(28.dp))

                                Box(
                                    modifier = Modifier
                                        .offset(x = heroOffset)
                                        .size(75.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEB3B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🤖", fontSize = 32.sp)
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                AnimatedVisibility(
                                    visible = powerUpActive,
                                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                                    exit = fadeOut()
                                ) {
                                    Text(
                                        text = "⚡ ¡Super Poder Activado! (+Daño)",
                                        color = Color(0xFF00E676),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    GameState.VICTORY -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "🏆 ¡VICTORIA ÉPICA! 🏆",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = {
                                        gameState = GameState.MENU
                                        powerUpActive = false
                                        isAttacking = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Jugar de Nuevo", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (gameState == GameState.BATTLE) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { isAttacking = !isAttacking },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Atacar", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    Button(
                        onClick = { powerUpActive = !powerUpActive },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Power-Up", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    Button(
                        onClick = { gameState = GameState.VICTORY },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ganar", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }
}