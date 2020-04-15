package dev.jahir.blueprint.data.requests


data class RequestState(val state: State, val requestsLeft: Int = 0, val timeLeft: Long = 0) {
    enum class State { UNKNOWN, NORMAL, COUNT_LIMITED, TIME_LIMITED }

    @Suppress("FunctionName")
    companion object {
        fun UNKNOWN() = RequestState(State.UNKNOWN)
        fun NORMAL() = RequestState(State.NORMAL)

        fun COUNT_LIMITED(requestsLeft: Int = -1) =
            RequestState(State.COUNT_LIMITED, requestsLeft)

        fun TIME_LIMITED(timeLeft: Long = 0) =
            RequestState(State.TIME_LIMITED, timeLeft = timeLeft)
    }
}