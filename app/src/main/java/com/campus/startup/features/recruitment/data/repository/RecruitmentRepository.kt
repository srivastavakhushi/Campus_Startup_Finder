package com.campus.startup.features.recruitment.data.repository

import com.campus.startup.features.recruitment.data.local.JobApplicationEntity
import com.campus.startup.features.recruitment.data.local.RecruitmentDao
import com.campus.startup.features.recruitment.data.local.StartupOpeningEntity
import com.campus.startup.features.recruitment.data.model.JobApplication
import com.campus.startup.features.recruitment.data.model.StartupOpening
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RecruitmentRepository(
    private val recruitmentDao: RecruitmentDao
) {

    suspend fun checkAndSeedOpenings() {
        if (recruitmentDao.getOpeningCount() == 0) {
            val initialOpenings = listOf(
                // 1. DEVELOPER
                StartupOpeningEntity(
                    id = "rec_dev_1",
                    startupName = "CampusBites",
                    startupLogo = "🍔",
                    startupStage = "Incubated in Lab 2 • Pre-Seed",
                    title = "Founding Android / Jetpack Compose Engineer",
                    roleCategoryName = StartupRoleCategory.DEVELOPER.name,
                    skillsCsv = "Kotlin, Jetpack Compose, Room, Firebase",
                    workType = "Hybrid",
                    commitment = "Part-Time / Internship",
                    stipend = "₹18,000/mo + 1.5% Equity",
                    location = "Campus Incubation Hub",
                    deadlineDays = 5,
                    description = "CampusBites is scaling campus-wide hostel night food delivery. We are looking for an ambitious Android engineer to build our customer ordering app and real-time order tracking.",
                    responsibilitiesCsv = "Build fluid UI using Jetpack Compose and Material 3|||Integrate local Room cache and live Firebase order updates|||Collaborate with campus operations team for dispatching",
                    perksCsv = "Founding engineer equity grant|||Flexible hours during exams|||Free meals on campus during launch",
                    founderName = "Aman Sharma (ECE '26)",
                    founderEmail = "aman@campusbites.internal",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 2. DEVELOPER
                StartupOpeningEntity(
                    id = "rec_dev_2",
                    startupName = "StudyGenie AI",
                    startupLogo = "⚡",
                    startupStage = "Student MVP • 2,500 Active Users",
                    title = "AI & Backend Engineer (Python / LLMs)",
                    roleCategoryName = StartupRoleCategory.DEVELOPER.name,
                    skillsCsv = "Python, AI & LLMs, Node.js / Express, Firebase / SQL",
                    workType = "Remote",
                    commitment = "Part-Time",
                    stipend = "₹22,000/mo",
                    location = "Remote (Campus Startup)",
                    deadlineDays = 8,
                    description = "Building next-gen exam preparation assistant powered by fine-tuned campus course syllabus models. Help us scale our FastAPI backend and vector search pipeline.",
                    responsibilitiesCsv = "Design RAG workflows for student textbook search|||Optimize vector database queries and latency|||Ship API endpoints for Web and Mobile clients",
                    perksCsv = "Direct mentorship from alumni AI researchers|||Access to GPU compute credits|||Flexible schedule",
                    founderName = "Rohan Verma (CSE '25)",
                    founderEmail = "rohan@studygenie.ai",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 3. DESIGNER
                StartupOpeningEntity(
                    id = "rec_des_1",
                    startupName = "CampusConnect",
                    startupLogo = "🎨",
                    startupStage = "College E-Cell Backed",
                    title = "Lead UI/UX & Product Designer",
                    roleCategoryName = StartupRoleCategory.DESIGNER.name,
                    skillsCsv = "Figma, UI/UX Design, Design Systems, Prototyping",
                    workType = "Hybrid",
                    commitment = "Internship",
                    stipend = "₹15,000/mo + Certificate",
                    location = "Design Lab 102",
                    deadlineDays = 10,
                    description = "We are designing the unified student lifestyle and club discovery app. Looking for a passionate Figma designer to create high-fidelity prototypes and design systems.",
                    responsibilitiesCsv = "Create complete Figma component library and interactive prototypes|||Conduct usability tests with hostel students|||Coordinate handover with Flutter and Android developers",
                    perksCsv = "High-impact portfolio case studies|||Letter of recommendation signed by Incubation Director|||Paid Figma Pro subscription",
                    founderName = "Priya Nair (Design '26)",
                    founderEmail = "priya@campusconnect.club",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 4. DESIGNER
                StartupOpeningEntity(
                    id = "rec_des_2",
                    startupName = "EcoCampus Goods",
                    startupLogo = "🌱",
                    startupStage = "Green Innovation Grant Winner",
                    title = "Brand & Graphic Designer",
                    roleCategoryName = StartupRoleCategory.DESIGNER.name,
                    skillsCsv = "Graphic Design, Canva & Adobe Suite, Figma",
                    workType = "On-Campus",
                    commitment = "Project-Based",
                    stipend = "₹10,000 / project",
                    location = "Campus Incubator",
                    deadlineDays = 12,
                    description = "EcoCampus manufactures reusable biodegradable containers and merch for campus fests. We need vibrant poster designs, packaging art, and social visual identity.",
                    responsibilitiesCsv = "Design fest merchandise graphics and stickers|||Create packaging artwork for campus cafes|||Deliver high-resolution print-ready assets",
                    perksCsv = "Official merchandise kit|||Certificate of appreciation|||Creative freedom on all brand touchpoints",
                    founderName = "Ananya Sen (Mech '26)",
                    founderEmail = "ananya@ecocampus.in",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 5. MARKETER
                StartupOpeningEntity(
                    id = "rec_mkt_1",
                    startupName = "PeerTutor Network",
                    startupLogo = "📢",
                    startupStage = "Early Traction • 1,200 Bookings",
                    title = "Growth Marketer & Campus Evangelist",
                    roleCategoryName = StartupRoleCategory.MARKETER.name,
                    skillsCsv = "Growth Marketing, Campus Outreach, Community Building, Social Media & Reels",
                    workType = "On-Campus",
                    commitment = "Part-Time",
                    stipend = "₹12,000/mo + Performance Bonus",
                    location = "Across Campus Hostels",
                    deadlineDays = 6,
                    description = "PeerTutor connects seniors with juniors for instant exam tutoring. We need high-energy student marketers to drive departmental adoption and onboarding campaigns.",
                    responsibilitiesCsv = "Lead campus ambassador drives across 1st and 2nd year batches|||Organize pre-exam crash workshops and demo sessions|||Track referral metrics and conversion funnels",
                    perksCsv = "Free unlimited tutoring access|||Performance bonuses on student signups|||Direct leadership experience",
                    founderName = "Vikram Patel (EE '25)",
                    founderEmail = "vikram@peertutor.io",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 6. MARKETER
                StartupOpeningEntity(
                    id = "rec_mkt_2",
                    startupName = "FestPass Live",
                    startupLogo = "🎟️",
                    startupStage = "Official College Fest Partner",
                    title = "Social Media Lead & Content Creator",
                    roleCategoryName = StartupRoleCategory.MARKETER.name,
                    skillsCsv = "Social Media & Reels, Content Writing, SEO & Analytics",
                    workType = "Remote",
                    commitment = "Internship",
                    stipend = "₹14,000/mo",
                    location = "Campus Media Room",
                    deadlineDays = 9,
                    description = "FestPass is digitizing ticketing and voting for all cultural and tech fests. We need a creative storyteller who lives on Instagram Reels and campus trends.",
                    responsibilitiesCsv = "Create viral short-form video reels and memes|||Write engaging announcements for events and artists|||Manage community engagement and DMs",
                    perksCsv = "VIP passes to all campus cultural festivals|||Backstage artist interaction pass|||Stipend + equipment support",
                    founderName = "Tanvi Kulkarni (IT '26)",
                    founderEmail = "tanvi@festpass.campus",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 7. INTERN
                StartupOpeningEntity(
                    id = "rec_int_1",
                    startupName = "HostelMate IoT",
                    startupLogo = "🧺",
                    startupStage = "Prototype Stage • Hardware + App",
                    title = "IoT & Flutter Software Intern",
                    roleCategoryName = StartupRoleCategory.INTERN.name,
                    skillsCsv = "Flutter, Python, Firebase / SQL, Operations",
                    workType = "Hybrid",
                    commitment = "Internship",
                    stipend = "₹16,000/mo",
                    location = "IoT Maker Lab",
                    deadlineDays = 14,
                    description = "Smart automated hostel washing machine tracker with mobile notifications. Great for 2nd and 3rd year students looking for hardware-software integration experience.",
                    responsibilitiesCsv = "Assist in microcontroller firmware-to-cloud telemetry|||Build notification widget in Flutter|||Conduct hardware testing in hostel laundries",
                    perksCsv = "Hands-on hardware lab access|||Flexible working slots|||Formal internship certificate",
                    founderName = "Karthik Raj (ECE '25)",
                    founderEmail = "karthik@hostelmate.tech",
                    isSaved = false,
                    isPostedByMe = false
                ),
                // 8. INTERN
                StartupOpeningEntity(
                    id = "rec_int_2",
                    startupName = "CampusStore Exchange",
                    startupLogo = "📚",
                    startupStage = "Bootstrapped Student Team",
                    title = "Product Management & Operations Intern",
                    roleCategoryName = StartupRoleCategory.INTERN.name,
                    skillsCsv = "Product Management, Operations, Market Research, Sales & Pitching",
                    workType = "On-Campus",
                    commitment = "Internship",
                    stipend = "₹11,000/mo",
                    location = "Student Union Building",
                    deadlineDays = 11,
                    description = "CampusStore is a circular economy marketplace for textbooks, drafters, calculators, and lab coats. Join as a product & operations intern to manage inventory and user feedback.",
                    responsibilitiesCsv = "Gather student feedback to shape future app features|||Oversee book collection points at semester ends|||Coordinate with departmental class representatives",
                    perksCsv = "Holistic 0-to-1 startup exposure|||Letter of recommendation from founder|||Leadership role opportunity",
                    founderName = "Siddharth Das (Civil '26)",
                    founderEmail = "sid@campusstore.org",
                    isSaved = false,
                    isPostedByMe = false
                )
            )
            recruitmentDao.insertOpenings(initialOpenings)
        }
    }

    fun getAllOpenings(): Flow<List<StartupOpening>> {
        return recruitmentDao.getAllOpenings().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getSavedOpenings(): Flow<List<StartupOpening>> {
        return recruitmentDao.getSavedOpenings().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getMyPostedOpenings(): Flow<List<StartupOpening>> {
        return recruitmentDao.getMyPostedOpenings().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun toggleSave(openingId: String, currentSaved: Boolean) {
        recruitmentDao.updateSavedStatus(openingId, !currentSaved)
    }

    suspend fun postOpening(
        startupName: String,
        startupLogo: String,
        startupStage: String,
        title: String,
        roleCategory: StartupRoleCategory,
        skills: List<String>,
        workType: String,
        commitment: String,
        stipend: String,
        location: String,
        deadlineDays: Int,
        description: String,
        responsibilities: List<String>,
        perks: List<String>,
        founderName: String,
        founderEmail: String
    ) {
        val entity = StartupOpeningEntity(
            id = "opening_" + UUID.randomUUID().toString().take(8),
            startupName = startupName,
            startupLogo = startupLogo.ifBlank { "🚀" },
            startupStage = startupStage.ifBlank { "Campus Startup" },
            title = title,
            roleCategoryName = roleCategory.name,
            skillsCsv = skills.joinToString(", "),
            workType = workType,
            commitment = commitment,
            stipend = stipend,
            location = location,
            deadlineDays = deadlineDays,
            description = description,
            responsibilitiesCsv = responsibilities.joinToString("|||"),
            perksCsv = perks.joinToString("|||"),
            founderName = founderName,
            founderEmail = founderEmail,
            isSaved = false,
            isPostedByMe = true
        )
        recruitmentDao.insertOpening(entity)
    }

    suspend fun deleteOpening(id: String) {
        recruitmentDao.deleteOpening(id)
    }

    // Applications
    fun getAllApplications(): Flow<List<JobApplication>> {
        return recruitmentDao.getAllApplications().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getApplicationsForOpening(openingId: String): Flow<List<JobApplication>> {
        return recruitmentDao.getApplicationsForOpening(openingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun hasApplied(openingId: String): Flow<Boolean> {
        return recruitmentDao.hasAppliedForOpening(openingId)
    }

    suspend fun submitApplication(
        opening: StartupOpening,
        applicantName: String,
        applicantEmail: String,
        applicantPhone: String,
        applicantBranchYear: String,
        portfolioUrl: String,
        matchedSkills: List<String>,
        pitchNote: String
    ) {
        val app = JobApplicationEntity(
            id = "app_" + UUID.randomUUID().toString().take(8),
            openingId = opening.id,
            openingTitle = opening.title,
            startupName = opening.startupName,
            roleCategory = opening.roleCategory.displayName,
            applicantName = applicantName,
            applicantEmail = applicantEmail,
            applicantPhone = applicantPhone,
            applicantBranchYear = applicantBranchYear,
            portfolioUrl = portfolioUrl,
            matchedSkillsCsv = matchedSkills.joinToString(", "),
            pitchNote = pitchNote,
            status = "Submitted"
        )
        recruitmentDao.insertApplication(app)
    }

    suspend fun updateApplicationStatus(applicationId: String, newStatus: String) {
        recruitmentDao.updateApplicationStatus(applicationId, newStatus)
    }

    // Helpers
    private fun StartupOpeningEntity.toDomain(): StartupOpening {
        val cat = try {
            StartupRoleCategory.valueOf(roleCategoryName)
        } catch (e: Exception) {
            StartupRoleCategory.ALL
        }
        val skills = skillsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val resps = responsibilitiesCsv.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
        val prks = perksCsv.split("|||").map { it.trim() }.filter { it.isNotEmpty() }

        return StartupOpening(
            id = id,
            startupName = startupName,
            startupLogo = startupLogo,
            startupStage = startupStage,
            title = title,
            roleCategory = cat,
            requiredSkills = skills,
            workType = workType,
            commitment = commitment,
            stipend = stipend,
            location = location,
            deadlineDays = deadlineDays,
            description = description,
            responsibilities = resps,
            perks = prks,
            founderName = founderName,
            founderEmail = founderEmail,
            isSaved = isSaved,
            isPostedByMe = isPostedByMe
        )
    }

    private fun JobApplicationEntity.toDomain(): JobApplication {
        val skills = matchedSkillsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return JobApplication(
            id = id,
            openingId = openingId,
            openingTitle = openingTitle,
            startupName = startupName,
            roleCategory = roleCategory,
            applicantName = applicantName,
            applicantEmail = applicantEmail,
            applicantPhone = applicantPhone,
            applicantBranchYear = applicantBranchYear,
            portfolioUrl = portfolioUrl,
            matchedSkills = skills,
            pitchNote = pitchNote,
            status = status,
            appliedTimestamp = appliedTimestamp
        )
    }
}
