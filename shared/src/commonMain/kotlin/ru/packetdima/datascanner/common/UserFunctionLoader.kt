package ru.packetdima.datascanner.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class UserFunctionLoader {
    val userSignature: MutableList<UserSignature> = mutableListOf()

    constructor(userFunctionFile: File) {
        try{

            val prop = Json.decodeFromString<UserFunctionLoader>(userFunctionFile.readText())

            this.userSignature.addAll(prop.userSignature)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun save(userFunctionFile: File) {
        userFunctionFile.writeText(Json.encodeToString(this))
    }
}