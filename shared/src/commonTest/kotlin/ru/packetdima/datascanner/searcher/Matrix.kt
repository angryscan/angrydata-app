package ru.packetdima.datascanner.searcher

import ru.packetdima.datascanner.common.DetectFunction
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class Matrix {
    companion object {
        private val matrix = readMatrix("/common/Matrix.txt")
        private val matrixFastscan = readMatrix("/common/MatrixFastscan.txt")

        private fun readMatrix(file: String) : Map<String, Array<Int>?>{
            val path = Matrix::class.java.getResource(file)
            assertNotNull(path)
            val f = File(path.file)
            assertTrue(f.exists())

            var m = mutableMapOf<String, Array<Int>?>()

            f.readLines()
                .filterNot { str ->
                    str.startsWith(" ") || str.startsWith("\t")
                }
                .map { str ->
                    val array = str.trim().split("\\s+".toRegex())
                    m[array[0]] = array.drop(1).map { it.toInt() }.toTypedArray()
                }
            return m
        }

        fun getMap(file: String, isFastscan: Boolean = false): Map<DetectFunction, Int>? {
            val filename = file.replace('\\', '/')
            var res = mutableMapOf<DetectFunction, Int>()
            if (matrix[filename] == null)
                return null
            if (isFastscan)
                matrixFastscan[filename]?.mapIndexed { id, value ->
                    if (value > 0)
                        res[getFnById(id)] = value
                }
            else
                matrix[filename]?.mapIndexed { id, value ->
                    if (value > 0)
                        res[getFnById(id)] = value
                }
            return res.toMap()
        }

        fun getFnByName(name: String): DetectFunction? {
            return DetectFunction.entries.firstOrNull { it.writeName == name }
        }

        private fun getFnById(id: Int): DetectFunction {
            return DetectFunction.entries.toList()[id]
        }

        fun contains(name: String): Boolean {
            return matrix.containsKey(name)
        }
    }

}