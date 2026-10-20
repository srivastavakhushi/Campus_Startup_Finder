package com.campus.startup.features.startupprofiles.data.repository

import com.campus.startup.features.startupprofiles.data.local.StartupDao
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import kotlinx.coroutines.flow.Flow

class StartupRepository(private val dao: StartupDao) {
    fun observeAll(): Flow<List<StartupEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<StartupEntity?> = dao.observeById(id)
    suspend fun getById(id: String): StartupEntity? = dao.getById(id)
    suspend fun save(startup: StartupEntity) = dao.upsert(startup)
    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun toggleInterested(id: String, currentInterested: Boolean) {
        dao.updateInterested(id, !currentInterested)
    }

    suspend fun setRecruited(id: String, isRecruited: Boolean, role: String = "Core Team Member") {
        dao.updateRecruited(id, isRecruited, if (isRecruited) role else "")
    }

    suspend fun checkAndSeedStartups() {
        if (dao.count() == 0) {
            val now = System.currentTimeMillis()
            val sampleStartups = listOf(
                StartupEntity(
                    id = "startup_studygenie",
                    name = "StudyGenie AI",
                    domain = "AI / ML",
                    stage = "Early Traction",
                    description = "AI-powered campus exam preparation and lecture summarizer trained on university course syllabi with 2,500+ active students.",
                    founderName = "Rohan Verma (CSE '25)",
                    teamMembers = "Rohan Verma (AI Lead), Kabir Shah (Android), Aisha Rahman (Design)",
                    collaborationTypes = "Developers, Designers, Interns",
                    requirements = "Looking for Python/LLM engineers and mobile developers passionate about EdTech & AI.",
                    tags = "AI / ML, Artificial Intelligence, EdTech, Data, Open Source, Python, Kotlin",
                    isInterested = true,
                    isRecruited = true,
                    recruitedRole = "Product & Engineering Team",
                    createdAt = now - 100_000L
                ),
                StartupEntity(
                    id = "startup_campusbites",
                    name = "CampusBites",
                    domain = "E-Commerce",
                    stage = "Early Traction",
                    description = "Late-night hostel food and essentials delivery network run by students, delivering from campus canteens to 8 hostels in under 20 minutes.",
                    founderName = "Aman Sharma (ECE '26)",
                    teamMembers = "Aman Sharma (Founder), Vikram Patel (Ops), Dev Patel (Tech)",
                    collaborationTypes = "Developers, Marketers, Co-founder",
                    requirements = "Kotlin / Jetpack Compose developers for live order tracking and campus growth marketers.",
                    tags = "E-Commerce, Campus consumer, Marketplace, Community, Kotlin, Jetpack Compose, Flutter",
                    isInterested = true,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 200_000L
                ),
                StartupEntity(
                    id = "startup_finpulse",
                    name = "FinPulse Campus",
                    domain = "FinTech",
                    stage = "Prototype / MVP",
                    description = "Smart bill-splitting, mess budget tracker, and micro-savings app built specifically for college students and club treasurers.",
                    founderName = "Dev Patel (IT '27) & Arjun Nair (Commerce '25)",
                    teamMembers = "Dev Patel (Full-Stack), Arjun Nair (Finance & Growth)",
                    collaborationTypes = "Developers, Designers, Funding",
                    requirements = "UI/UX designers and FinTech enthusiasts interested in student payments and financial literacy.",
                    tags = "FinTech, Finance, SaaS, Data, Product Design, Flutter, Figma",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 300_000L
                ),
                StartupEntity(
                    id = "startup_ecocampus",
                    name = "EcoCampus Goods",
                    domain = "Social Impact",
                    stage = "Prototype / MVP",
                    description = "Zero-waste circular packaging and smart returnable coffee cups for campus cafes and annual cultural fests.",
                    founderName = "Ananya Sen (Mechanical '26)",
                    teamMembers = "Ananya Sen (Operations), Sara Khan (Brand Design)",
                    collaborationTypes = "Designers, Marketers, Interns",
                    requirements = "Sustainability advocates, product designers, and campus outreach leads.",
                    tags = "Social Impact, Social impact, Sustainability, Climate, Product Design, Community",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 400_000L
                ),
                StartupEntity(
                    id = "startup_peertutor",
                    name = "PeerTutor Network",
                    domain = "EdTech",
                    stage = "Growth",
                    description = "1-on-1 instant peer micro-tutoring and project mentorship connecting senior toppers with 1st and 2nd year students.",
                    founderName = "Vikram Patel (EE '25)",
                    teamMembers = "Vikram Patel (Growth), Meera Iyer (Community)",
                    collaborationTypes = "Developers, Marketers, Mentorship",
                    requirements = "React/Mobile developers and departmental ambassadors.",
                    tags = "EdTech, Community, Public Speaking, Writing, Artificial Intelligence, SaaS",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 500_000L
                ),
                StartupEntity(
                    id = "startup_medvault",
                    name = "MedVault Student",
                    domain = "HealthTech",
                    stage = "Idea",
                    description = "Digital campus infirmary appointments, mental wellness peer support, and instant prescription delivery for hostel residents.",
                    founderName = "Dr. Neha Kapoor & Priya Nair (Design '26)",
                    teamMembers = "Priya Nair (Product Design), Karthik Raj (Systems)",
                    collaborationTypes = "Developers, Designers, Co-founder",
                    requirements = "Android/Web builders and UX researchers passionate about student healthcare.",
                    tags = "HealthTech, Product Design, Community, Social impact, AI / ML, Figma",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 600_000L
                ),
                StartupEntity(
                    id = "startup_hostelmate",
                    name = "HostelMate IoT",
                    domain = "SaaS",
                    stage = "Prototype / MVP",
                    description = "IoT smart sensors and mobile availability alerts for hostel washing machines, gym occupancy, and study rooms.",
                    founderName = "Karthik Raj (ECE '25)",
                    teamMembers = "Karthik Raj (Embedded & IoT), Rohan Verma (Cloud)",
                    collaborationTypes = "Developers, Interns",
                    requirements = "Hardware & IoT tinkerers, Flutter/Android developers, and backend engineers.",
                    tags = "SaaS, Hardware, Hardware & IoT, Open Source, Campus consumer, Python, Flutter",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 700_000L
                ),
                StartupEntity(
                    id = "startup_pixelforge",
                    name = "PixelForge Indie",
                    domain = "Gaming",
                    stage = "Idea",
                    description = "Campus indie game studio building multiplayer strategy and AR campus treasure-hunt games for college fests.",
                    founderName = "Kabir Shah (CSE '26)",
                    teamMembers = "Kabir Shah (Game Dev), Sara Khan (Art & Story)",
                    collaborationTypes = "Developers, Designers, Co-founder",
                    requirements = "Game developers, UI artists, and sound/storytelling enthusiasts.",
                    tags = "Gaming, Product Design, Open Source, Writing, Artificial Intelligence",
                    isInterested = false,
                    isRecruited = false,
                    recruitedRole = "",
                    createdAt = now - 800_000L
                )
            )
            dao.insertAll(sampleStartups)
        }
    }
}
