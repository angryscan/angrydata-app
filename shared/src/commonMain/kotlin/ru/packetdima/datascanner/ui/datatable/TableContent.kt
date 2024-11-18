package ru.packetdima.datascanner.ui.datatable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableContent(
    rows: List<Row>,
    onCellSecondaryClick: (Row, TableHeader) -> Unit,
    onCellPrimaryClick: (Row, TableHeader) -> Unit
) {
    val scrollState = rememberLazyListState()
    Row {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = scrollState
        ) {
            items(items = rows) {
                TableRow(it, onCellSecondaryClick, onCellPrimaryClick)
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.padding(1.dp, 0.dp, 1.dp, 0.dp).width(8.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun TableRow(
    row: Row,
    onCellSecondaryClick: (Row, TableHeader) -> Unit,
    onCellPrimaryClick: (Row, TableHeader) -> Unit
) {
    var hoverRow by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .onPointerEvent(PointerEventType.Enter){ hoverRow = true }
            .onPointerEvent(PointerEventType.Exit){ hoverRow = false }
            .background(color = if(hoverRow) Color.Gray.copy(alpha = 0.1f) else Color.Transparent),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val rowHeaders = row.map { it.key }.sortedBy { it.index }

        val iterator = rowHeaders.iterator()
        while (iterator.hasNext()) {
            val header = iterator.next()
            val interactionSourceCell = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(header.weightRatio)
                    .fillMaxHeight()
                    .onClick(
                        matcher = PointerMatcher.mouse(PointerButton.Secondary),
                        interactionSource = interactionSourceCell,
                        onClick = { onCellSecondaryClick(row, header) }
                    )
                    .onClick(
                        matcher = PointerMatcher.mouse(PointerButton.Primary),
                        interactionSource = interactionSourceCell,
                        onClick = {onCellPrimaryClick (row, header)}
                    )
                    .indication(interactionSourceCell, LocalIndication.current)
                    .padding(5.dp)
            ) {
                if (row[header] != null) {
                    Text(
                        text = "${row[header]}",
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "--"
                    )
                }
            }
            if (iterator.hasNext()) {
                Divider(Modifier.fillMaxHeight().width(1.dp))
            }
        }
    }
}