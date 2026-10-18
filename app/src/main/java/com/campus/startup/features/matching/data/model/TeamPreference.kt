package com.campus.startup.features.matching.data.model

enum class TeamPreference(
    val displayName: String,
    val emoji: String
) {
    FOUNDING("Founding team", "🚀"),
    HACKATHON("Hackathon squad", "⚡"),
    RESEARCH("Research spinout", "🔬"),
    SOCIAL("Social impact venture", "🌱"),
    CAMPUS("Campus consumer startup", "🎓");

    companion object {
        fun fromStored(value: String): TeamPreference {
            return values().find { it.name == value } ?: FOUNDING
        }
    }
}
