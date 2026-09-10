package com.mayosal.pacmangame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.atan2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PocketMaze() }
    }
}

@Composable
private fun PocketMaze() {
    var player by remember { mutableStateOf(Offset(4f, 7f)) }
    var direction by remember { mutableFloatStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }
    var pellets by remember {
        mutableStateOf((1..9).flatMap { x -> (1..13).map { y -> Offset(x.toFloat(), y.toFloat()) } }.toSet())
    }
    val walls = remember {
        setOf(
            Offset(3f,3f), Offset(4f,3f), Offset(5f,3f), Offset(7f,3f),
            Offset(4f,6f), Offset(5f,6f), Offset(6f,6f), Offset(7f,6f),
            Offset(3f,10f), Offset(4f,10f), Offset(5f,10f), Offset(7f,10f)
        )
    }
    fun move(dx: Float, dy: Float) {
        val next = Offset((player.x + dx).coerceIn(1f, 9f), (player.y + dy).coerceIn(1f, 13f))
        direction = atan2(dy, dx)
        if (next !in walls) {
            player = next
            if (next in pellets) { pellets = pellets - next; score++ }
        }
    }

    MaterialTheme {
        Column(
            Modifier.fillMaxSize().background(Color(0xFF080A23)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("POCKET MAZE", color = Color.Yellow, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Score: $score", color = Color.White)
            Canvas(
                Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
                    detectTapGestures { tap ->
                        if (tap.x > size.width / 2) move(1f, 0f) else move(-1f, 0f)
                    }
                }
            ) {
                val cell = size.width / 11f
                walls.forEach { drawRect(Color(0xFF246BFF), Offset(it.x * cell, it.y * cell), Size(cell, cell), style = Stroke(4f)) }
                pellets.forEach { drawCircle(Color.White, cell / 9f, Offset((it.x + .5f) * cell, (it.y + .5f) * cell)) }
                drawArc(Color.Yellow, direction * 57.3f + 25f, 310f, true, Offset(player.x * cell, player.y * cell), Size(cell, cell))
                drawCircle(Color.Red, cell * .35f, Offset(8.5f * cell, 7.5f * cell))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { move(0f,-1f) }) { Text("↑") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { move(-1f,0f) }) { Text("←") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { move(1f,0f) }) { Text("→") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { move(0f,1f) }) { Text("↓") }
            }
            Text("Collect the dots. Tap the left or right half of the maze or use the arrows.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
