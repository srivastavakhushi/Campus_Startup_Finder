package com.campus.startup.features.matching.data.model

enum class ConnectionStatus {
    NONE,
    PENDING_OUTGOING,
    PENDING_INCOMING,
    ACCEPTED
}

data class ConnectionRequest(
    val id: String,
    val fromProfileId: String,
    val toProfileId: String,
    val status: String,
    val createdAt: Long
)

fun resolveConnectionStatus(
    myId: String,
    otherId: String,
    connections: List<ConnectionRequest>
): ConnectionStatus {
    val link = connections.find { request ->
        (request.fromProfileId == myId && request.toProfileId == otherId) ||
            (request.toProfileId == myId && request.fromProfileId == otherId)
    } ?: return ConnectionStatus.NONE

    return when {
        requestStatusAccepted(link.status) -> ConnectionStatus.ACCEPTED
        link.fromProfileId == myId -> ConnectionStatus.PENDING_OUTGOING
        else -> ConnectionStatus.PENDING_INCOMING
    }
}

private fun requestStatusAccepted(status: String): Boolean = status.equals("ACCEPTED", ignoreCase = true)
