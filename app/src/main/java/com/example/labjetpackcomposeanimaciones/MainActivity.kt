package com.example.labjetpackcomposeanimaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labjetpackcomposeanimaciones.ui.theme.LabJetpackComposeAnimacionesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    MENU, BATTLE, VICTORY, GAME_OVER
}

// Posiciones relativas (0..1) y tamaño de las estrellas del fondo
private val stars = listOf(
    Triple(0.10f, 0.08f, 4f), Triple(0.45f, 0.05f, 3f), Triple(0.80f, 0.12f, 5f),
    Triple(0.25f, 0.22f, 3f), Triple(0.90f, 0.30f, 4f), Triple(0.05f, 0.40f, 5f),
    Triple(0.60f, 0.45f, 3f), Triple(0.35f, 0.60f, 4f), Triple(0.85f, 0.68f, 3f),
    Triple(0.15f, 0.78f, 5f), Triple(0.55f, 0.85f, 4f), Triple(0.92f, 0.92f, 3f),
    Triple(0.30f, 0.95f, 4f), Triple(0.70f, 0.20f, 3f), Triple(0.50f, 0.72f, 5f)
)

@Composable
fun GamePrototypeScreen() {
    val scope = rememberCoroutineScope()

    // ---------- ESTADO DEL JUEGO ----------
    var gameState by remember { mutableStateOf(GameState.MENU) }
    var isAttacking by remember { mutableStateOf(false) }
    var powerUpActive by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

    var enemyHp by remember { mutableStateOf(100) }
    var heroHp by remember { mutableStateOf(100) }
    var score by remember { mutableStateOf(0) }

    var enemyHit by remember { mutableStateOf(false) }
    var heroHit by remember { mutableStateOf(false) }
    var showDamage by remember { mutableStateOf(false) }
    var lastDamage by remember { mutableStateOf(0) }

    fun resetGame() {
        gameState = GameState.MENU
        isAttacking = false
        powerUpActive = false
        busy = false
        enemyHp = 100
        heroHp = 100
        score = 0
        enemyHit = false
        heroHit = false
        showDamage = false
    }

    fun attack() {
        if (busy) return
        busy = true
        scope.launch {
            val damage = if (powerUpActive) 25 else 10
            isAttacking = true                 // el héroe se lanza hacia el enemigo
            delay(300)
            lastDamage = damage
            enemyHp = (enemyHp - damage).coerceAtLeast(0)
            score += damage * 10
            enemyHit = true                    // el enemigo tiembla
            showDamage = true                  // sale el "-25"
            delay(300)
            isAttacking = false                // el héroe regresa
            delay(500)
            showDamage = false

            if (enemyHp <= 0) {
                delay(400)
                gameState = GameState.VICTORY
            } else {
                // contraataque del enemigo
                delay(300)
                heroHp = (heroHp - 12).coerceAtLeast(0)
                heroHit = true
                delay(500)
                if (heroHp <= 0) gameState = GameState.GAME_OVER
            }
            busy = false
        }
    }

    // ---------- ANIMACIONES ----------
    val infinite = rememberInfiniteTransition(label = "infinite")

    val bgShift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "bgShift"
    )
    val twinkle by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "twinkle"
    )
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulse"
    )
    val glow by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "glow"
    )
    val trophyRotation by infinite.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "trophy"
    )

    val heroOffset by animateDpAsState(
        targetValue = if (isAttacking) 80.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "HeroOffsetAnimation"
    )
    val enemyShake by animateDpAsState(
        targetValue = if (enemyHit) 14.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.15f, stiffness = 1500f),
        finishedListener = { enemyHit = false },
        label = "enemyShake"
    )
    val heroShake by animateDpAsState(
        targetValue = if (heroHit) (-14).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.15f, stiffness = 1500f),
        finishedListener = { heroHit = false },
        label = "heroShake"
    )
    val arenaColor by animateColorAsState(
        targetValue = if (powerUpActive) Color(0xFF7B1FA2) else Color(0xFF3F51B5),
        animationSpec = tween(durationMillis = 800),
        label = "ArenaColorAnimation"
    )
    val enemyHpAnim by animateFloatAsState(
        targetValue = enemyHp / 100f,
        animationSpec = tween(500),
        label = "enemyHpAnim"
    )
    val heroHpAnim by animateFloatAsState(
        targetValue = heroHp / 100f,
        animationSpec = tween(500),
        label = "heroHpAnim"
    )

    // ---------- UI ----------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E)),
                    start = Offset(0f, bgShift),
                    end = Offset(bgShift, 2000f)
                )
            )
    ) {
        // Estrellas parpadeando
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEachIndexed { index, (xf, yf, r) ->
                val alpha = if (index % 2 == 0) twinkle else (1.2f - twinkle).coerceIn(0.1f, 1f)
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = r,
                    center = Offset(size.width * xf, size.height * yf)
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎮 CYBER QUEST",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Cyan.copy(alpha = glow)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Marcador con números que "ruedan"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PUNTOS: ",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    AnimatedContent(
                        targetState = score,
                        transitionSpec = {
                            (slideInVertically { -it } + fadeIn()) togetherWith
                                    (slideOutVertically { it } + fadeOut())
                        },
                        label = "ScoreAnimation"
                    ) { value ->
                        Text(
                            text = value.toString(),
                            color = Color(0xFFFFEB3B),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedContent(
                    targetState = gameState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    },
                    label = "GameScreenTransition"
                ) { state ->
                    when (state) {
                        // ---------------- MENÚ ----------------
                        GameState.MENU -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xCC263238))
                                    .border(2.dp, Color.Cyan.copy(alpha = glow), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "🤖", fontSize = 56.sp, modifier = Modifier.offset(y = floatY.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "¡Bienvenido Héroe!",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Derrota al enemigo antes de que acabe contigo",
                                        color = Color(0xFFB0BEC5),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { gameState = GameState.BATTLE },
                                        modifier = Modifier.scale(pulse),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Iniciar Partida", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }

                        // ---------------- BATALLA ----------------
                        GameState.BATTLE -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(arenaColor)
                                    .border(
                                        2.dp,
                                        if (powerUpActive) Color(0xFFE040FB) else Color.Cyan.copy(alpha = glow),
                                        RoundedCornerShape(20.dp)
                                    ),
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
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Barras de vida
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("HÉROE  $heroHp", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                            LinearProgressIndicator(
                                                progress = { heroHpAnim },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(10.dp)
                                                    .clip(RoundedCornerShape(5.dp)),
                                                color = if (heroHpAnim > 0.3f) Color(0xFF00E676) else Color(0xFFFF1744),
                                                trackColor = Color(0x33FFFFFF)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("ENEMIGO  $enemyHp", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                            LinearProgressIndicator(
                                                progress = { enemyHpAnim },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(10.dp)
                                                    .clip(RoundedCornerShape(5.dp)),
                                                color = if (enemyHpAnim > 0.3f) Color(0xFF00E676) else Color(0xFFFF1744),
                                                trackColor = Color(0x33FFFFFF)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Héroe vs Enemigo
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .offset(x = heroOffset + heroShake, y = floatY.dp)
                                                .size(70.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFEB3B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "🤖", fontSize = 32.sp)
                                        }

                                        Box(contentAlignment = Alignment.TopCenter) {
                                            Box(
                                                modifier = Modifier
                                                    .offset(x = enemyShake, y = (-floatY).dp)
                                                    .size(70.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFD32F2F)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "👾", fontSize = 32.sp)
                                            }
                                            // Número de daño flotante
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = showDamage,
                                                enter = fadeIn() + slideInVertically { it / 2 },
                                                exit = fadeOut() + slideOutVertically { -it }
                                            ) {
                                                Text(
                                                    text = "-$lastDamage",
                                                    color = Color(0xFFFF5252),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 22.sp,
                                                    modifier = Modifier.offset(y = (-26).dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

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

                        // ---------------- VICTORIA ----------------
                        GameState.VICTORY -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF4CAF50))
                                    .border(2.dp, Color.White.copy(alpha = glow), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "🏆",
                                        fontSize = 64.sp,
                                        modifier = Modifier.rotate(trophyRotation)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "¡VICTORIA ÉPICA!",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Puntaje final: $score",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { resetGame() },
                                        modifier = Modifier.scale(pulse),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Jugar de Nuevo", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // ---------------- GAME OVER ----------------
                        GameState.GAME_OVER -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF8E0000))
                                    .border(2.dp, Color(0xFFFF1744).copy(alpha = glow), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "💀",
                                        fontSize = 64.sp,
                                        modifier = Modifier.offset(y = floatY.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "GAME OVER",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Puntaje final: $score",
                                        color = Color(0xFFFFCDD2),
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { resetGame() },
                                        modifier = Modifier.scale(pulse),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Reintentar", color = Color(0xFF8E0000), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Controles (solo durante la batalla)
                AnimatedVisibility(
                    visible = gameState == GameState.BATTLE,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { attack() },
                            enabled = !busy,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚔️ Atacar", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = { powerUpActive = !powerUpActive },
                            enabled = !busy,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ Power-Up", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}