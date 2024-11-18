package ru.packetdima.datascanner.ui.datatable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.exposed.sql.SortOrder
import ru.packetdima.datascanner.ui.strings.uiText

@Composable
fun TableHeaderView(
    headerList: List<TableHeader>,
    sortingStates: MutableState<Map<TableHeader, MutableState<SortOrder?>>>,
    onSortingUpdate: (Pair<TableHeader, SortOrder?>) -> Unit
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(0.dp, 0.dp, 10.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val headerIterator = headerList.iterator()
        while (headerIterator.hasNext()) {
            val header = headerIterator.next()
            Box(
                modifier = Modifier
                    .weight(header.weightRatio)
                    .testTag("myTestTag")
                    .fillMaxHeight()
                    .clickable {
                        updateSortingStates(sortingStates, header)
                        onSortingUpdate(Pair(header, sortingStates.value[header]!!.value))
                    }
                    .padding(5.dp),
                contentAlignment = Alignment.Center

            ) {
                Row(
                    modifier = Modifier.wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = header.uiText(),
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500
                    )
                    Row(
                        modifier = Modifier.width(20.dp)
                    ) {
                        when (sortingStates.value[header]!!.value) {
                            SortOrder.ASC -> {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowUpward,
                                    contentDescription = ""
                                )
                            }
                            SortOrder.DESC -> {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDownward,
                                    contentDescription = ""
                                )
                            }
                            else -> {
                                //Empty
                            }
                        }
                    }
                }
            }
            if (headerIterator.hasNext()) {
                Divider(Modifier.width(1.dp).fillMaxHeight())
            }
        }
    }
}