package ru.packetdima.datascanner.ui.datatable

import androidx.compose.runtime.MutableState
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.KProperty0

@Target(AnnotationTarget.PROPERTY)
annotation class TableHeader(
    val text: String,
    val index: Int,
    val weightRatio: Float
)

data class ColumnHeader<out A, out B>(
    val value: A,
    val header: B
)

class Row : LinkedHashMap<TableHeader, Any?>() {
    operator fun get(index: Column<*>): Any? {
        val res = index::class.annotations.firstOrNull { an -> an is TableHeader }
        return this[res]
    }
    operator fun get(index: KProperty0<Column<String>>): Any? {
        return this[index.annotations.firstOrNull { an -> an is TableHeader }]
    }
    override operator fun get(key: TableHeader): Any? {
        return super.get(key)
    }
}
@Suppress("Unused")
inline fun <reified T : Any> getColumns(fields: List<T>): List<T> {
    return fields.filter { member ->
        member::class.annotations.any { an -> an is TableHeader }
    }.sortedBy {
        getTableHeader(it).index
    }
}

inline fun <reified T : Any> getTableHeader(headers: T): TableHeader {
    val header = headers::class.annotations.firstOrNull { an ->
        an is TableHeader
    } ?: throw IllegalArgumentException("Given headers don't contain a TableHeader annotations")
    return header as TableHeader
}

fun updateSortingStates(
    sortingStates: MutableState<Map<TableHeader, MutableState<SortOrder?>>>,
    tableHeader: TableHeader
) {
    val newMap = sortingStates.value
    when (sortingStates.value[tableHeader]!!.value) {
        SortOrder.ASC -> newMap[tableHeader]!!.value = SortOrder.DESC
        SortOrder.DESC -> newMap[tableHeader]!!.value = null
        else -> newMap[tableHeader]!!.value = SortOrder.ASC
    }
    sortingStates.value.filter { it.key != tableHeader }.forEach {
        newMap[it.key]!!.value = null
    }

    sortingStates.value = newMap
}

fun getContent(
    dataTable: Table,
    columnsWithHeader: List<ColumnHeader<Column<*>, TableHeader>>,
    sort: Pair<TableHeader, SortOrder?>?,
    rowsLimit: Int,
    page: Int
): List<Row> {
    var res = listOf<Row>()

    transaction {
        var dt = dataTable
            .select(columns = columnsWithHeader.map { it.value })

        if (rowsLimit != 0)
            dt = dt.limit(n = rowsLimit, offset = page.toLong() * rowsLimit)

        if (sort?.second != null) {
            dt = dt.orderBy(
                column = columnsWithHeader.first { it.header == sort.first }.value,
                order = sort.second!!
            )
        }

        res =
            dt.map { resultRow ->
                val row = Row()
                columnsWithHeader.forEach { col ->
                    row[col.header] = resultRow[col.value]
                }
                row
            }
    }
    return res
}