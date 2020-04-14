package dev.jahir.blueprint.data.requests

enum class RequestState(key: Int) {
    STATE_UNKNOWN(-1),
    STATE_NORMAL(0),
    STATE_COUNT_LIMITED(1),
    STATE_TIME_LIMITED(2)
}