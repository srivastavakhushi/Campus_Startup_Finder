package com.campus.startup.features.opportunities.data.model

enum class OpportunityType(val displayName: String, val emoji: String) {
    ALL("All", "✨"),
    STARTUP_ROLE("Startup Role", "🚀"),
    HACKATHON("Hackathon", "💻"),
    INTERNSHIP("Internship", "💼"),
    GRANT("Grant & Funding", "💰"),
    COMPETITION("Pitch / Competition", "🏆")
}
