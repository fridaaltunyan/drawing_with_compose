package com.example.painting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FaceRetouchingOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.painting.ui.theme.PaintingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaintingTheme {
                DrawingScreen("Երկինք")
            }
        }
    }
}

@Composable
fun DrawingScreen(word: String) {
    val lines = remember {
        mutableStateListOf<DrawnPoints>()
    }
    var currentColor by remember {
        mutableStateOf(Color.Blue)
    }
    var needToBeCleared by remember {
        mutableStateOf(false)
    }
    var showDialog by remember { mutableStateOf(false) }
    var showWord by remember { mutableStateOf(false) }
    var selectedLine by remember { mutableStateOf<DrawnPoints?>(null) }
    var clearSelectedPart by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            modifier = Modifier
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
            onClick = { showWord = !showWord }
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = if (showWord) word else "******",
                maxLines = 2,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall,
            )
            Icon(
                if (showWord) Icons.Default.FaceRetouchingOff else Icons.Default.Face,
                "showHide"
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val line = DrawnPoints(
                                start = change.position - dragAmount,
                                end = change.position,
                                color = currentColor
                            )
                            if (clearSelectedPart) {
                                selectedLine = line
                                return@detectDragGestures
                            }
                            lines.add(line)
                        }
                    }
            ) {
                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement
                    .spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    onClick = {
                        clearSelectedPart = !clearSelectedPart
                    }) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp),
                        text = "Clear selected part",
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    onClick = {
                        needToBeCleared = !needToBeCleared
                    }) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp),
                        text = "Clear All",
                        textAlign = TextAlign.Start,
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    onClick = {
                        showDialog = !showDialog
                    }) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp), text = "Paint",
                        textAlign = TextAlign.Start,
                    )
                }
            }
            if (needToBeCleared) {
                lines.clear()
                needToBeCleared = false
            }
            if (showDialog) {
                ColorPickerDialog(
                    colorList = listOf(
                        Color.Black,
                        Color.Blue,
                        Color.Yellow,
                        Color.Red,
                        Color.Magenta,
                        Color.Green
                    ),
                    onDismiss = { showDialog = false },
                    currentlySelected = currentColor,
                    onColorSelected = { color ->
                        currentColor = color
                    }
                )
            }
            @Synchronized
            if (selectedLine != null) {
                lines.removeIf { line ->
                    line.start == selectedLine!!.start && line.end == selectedLine!!.end
                }
                selectedLine = null
                clearSelectedPart = false
            }
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen("Sky")
}

data class DrawnPoints(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Dp = 6.dp,
)