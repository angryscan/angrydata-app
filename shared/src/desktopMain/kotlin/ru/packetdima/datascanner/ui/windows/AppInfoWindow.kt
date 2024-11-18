package ru.packetdima.datascanner.ui.windows

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.close
import ru.packetdima.datascanner.resources.iAbout
import ru.packetdima.datascanner.resources.iVersion


fun getAppVersion(): String = System.getProperty("jpackage.app-version") ?: "Debug"

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppInfoWindow(onCloseClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .size(400.dp, 300.dp)
            .background(colors.surface, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))
            .padding(10.dp)
            .testTag("appinfo_window"),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
        ) {
            Column(
                modifier = Modifier.height(240.dp)
            ) {

                Column(
                    modifier = Modifier.verticalScroll(
                        state = scrollState
                    )
                ) {
                    Text(
                        text = "${stringResource(Res.string.iVersion)}: ${getAppVersion()}",
                        color = colors.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.iAbout),
                        color = colors.onSurface,
                        modifier = Modifier.padding(0.dp, 10.dp, 2.dp, 0.dp)
                    )

                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            ) {
                OutlinedButton(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = colors.error
                    ),
                    border = null,
                    modifier = Modifier.testTag("close_appinfo_button")
                ) {
                    Text(stringResource(Res.string.close).uppercase(), fontWeight = FontWeight.W600)
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 40.dp).width(8.dp)
        )
    }
}