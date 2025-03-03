package ru.packetdima.datascanner.db.models

enum class TaskState {
    LOADING,
    PENDING,
    SEARCHING,
    SCANNING,
    COMPLETED,
    FAILED,
    STOPPED
}
