package ru.packetdima.datascanner.ui.datatable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.exposed.sql.*
import ru.packetdima.datascanner.searcher.model.ResultRows

@Composable
fun TableView(
    dataTable: Table,
    onCellSecondaryClick: (Row, TableHeader) -> Unit,
    onCellPrimaryClick: (Row, TableHeader) -> Unit,
    defaultSort: Pair<Column<*>, SortOrder>? = null,
    rowsLimit: Int? = null,
    updateTrigger: Any? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        val page by remember { mutableStateOf(0) }

        val fields = ResultRows::class.members.filter { it.annotations.any { a -> a is TableHeader } }
        val columns = dataTable.columns.filter { col -> fields.any { f -> f.name == col.name } }
        val columnsWithHeader = columns.map { column ->
            ColumnHeader(
                column,
                fields.first { h -> h.name == column.name }.annotations.first { an -> an is TableHeader } as TableHeader)
        }
        val headers = columnsWithHeader.map { it.header }

        val sortingStates = remember {
            mutableStateOf(headers.associateWith { mutableStateOf(null as SortOrder?) })
        }

        var sortWithHeader by remember { mutableStateOf<Pair<TableHeader, SortOrder>?>(null) }

        LaunchedEffect(Unit) {
            if (defaultSort != null) {
                val sortHeader = columnsWithHeader.first { it.value == defaultSort.first }.header
                sortingStates.value[sortHeader]?.value = defaultSort.second
                sortWithHeader = Pair(sortHeader, defaultSort.second)
            }
        }

        var contentList by remember(key1 = updateTrigger, key2 = page, key3 = sortWithHeader) {
            mutableStateOf(getContent(dataTable, columnsWithHeader, sortWithHeader, rowsLimit ?: 0, page))
        }

        val onSortingUpdate: (Pair<TableHeader, SortOrder?>) -> Unit = { sort ->
            contentList = getContent(dataTable, columnsWithHeader, sort, rowsLimit ?: 0, page)
        }

        TableHeaderView(
            headerList = headers,
            sortingStates = sortingStates,
            onSortingUpdate = onSortingUpdate
        )
        Divider(thickness = 2.dp)
        TableContent(contentList, onCellSecondaryClick, onCellPrimaryClick)
    }
}