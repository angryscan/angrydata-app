package ru.packetdima.datascanner.ui.screens.scanner

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.ui.custom.CustomOutlinedTextBox
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.selectFolderPlaceholder
import ru.packetdima.datascanner.resources.start
import java.io.File
import javax.swing.JFileChooser

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SelectPathScreen(onStart: (directory: File) -> Unit, onPathSelectionChanged: (isSelected: Boolean) -> Unit) {
    var path by remember { mutableStateOf("") }
    var isCorrectPath by remember { mutableStateOf(File(path).exists()) }
    val borderColor = remember { Animatable(Color.Gray) }
    val clickNotCorrectPath = remember { mutableStateOf(false) }
    val scanTask by remember { Settings.scanningTaskPath }

    LaunchedEffect(scanTask) {
        if(Settings.scanningTaskPath.value.isNotEmpty()) {
            path = scanTask
            isCorrectPath = File(path).exists()
            onPathSelectionChanged(isCorrectPath)
            Settings.scanningTaskPath.value = ""
        }
    }

    LaunchedEffect(clickNotCorrectPath.value) {
        if (clickNotCorrectPath.value) {
            borderColor.animateTo(Color.Red, animationSpec = tween(100))
            delay(50)
            borderColor.animateTo(Color.Gray, animationSpec = tween(100))
            delay(100)
            borderColor.animateTo(Color.Red, animationSpec = tween(100))
            delay(50)
            borderColor.animateTo(Color.Gray, animationSpec = tween(100))
            clickNotCorrectPath.value = false
        }
    }

    MaterialTheme {
        Column {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth().testTag("select_path_screen"),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Row {
                        CustomOutlinedTextBox(
                            placeholder = stringResource(Res.string.selectFolderPlaceholder),
                            value = path,
                            onValueChange = {
                                path = it
                                isCorrectPath = File(path).exists()
                                onPathSelectionChanged(isCorrectPath)
                                if (path == "show memory") {
                                    Settings.ui.propertiesList.add("heap_memory")
                                    path = ""
                                } else if (path == "close memory") {
                                    Settings.ui.propertiesList.remove("heap_memory")
                                    path = ""
                                }
                            },
                            textColor = MaterialTheme.colors.onSurface,
                            modifier = Modifier.height(40.dp).width(400.dp)
                                .testTag("select_path_input"),
                            borderColor = borderColor.value
                        )
                        OutlinedButton(
                            onClick = {
                                val f = JFileChooser()
                                f.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
                                f.isMultiSelectionEnabled = false
                                if (f.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                    path = f.selectedFile.absolutePath
                                    isCorrectPath = File(path).exists()
                                    onPathSelectionChanged(isCorrectPath)
                                }
                            },
                            modifier = Modifier.height(40.dp).testTag("open_path_button"),
                            shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
                            border = BorderStroke(1.dp, borderColor.value)
                        ) {
                            Icon(Icons.Outlined.FolderOpen, "Info", tint = MaterialTheme.colors.onSurface)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 0.dp, 0.dp)
                        .height(40.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.width(100.dp)
                            .testTag("scan_start_button"),
                        onClick = {
                            if (isCorrectPath) {
                                if (AppFiles.ResultDBFile.exists()) {
                                    AppFiles.ResultDBFile.delete()
                                }
                                onStart(File(path))
                            } else {
                                clickNotCorrectPath.value = true
                            }
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                    ) {
                        Text(
                            text = stringResource(Res.string.start).uppercase(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W500,
                        )
                    }
                }
            }
        }
    }
}