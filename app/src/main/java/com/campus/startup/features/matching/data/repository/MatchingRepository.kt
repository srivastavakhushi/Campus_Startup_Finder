package com.campus.startup.features.matching.data.repository

import com.campus.startup.features.matching.data.local.ConnectionRequestEntity
import com.campus.startup.features.matching.data.local.MatchingDao
import com.campus.startup.features.matching.data.local.StudentProfileEntity
import com.campus.startup.features.matching.data.model.ConnectionRequest
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.TeamPreference
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Local co-founder directory.
 *
 * The project has no authentication or Firebase user profile. The signed-in student is the
 * single local row [MatchCatalog.CURRENT_USER_ID]. Other rows are sample campus profiles,
 * inserted only when the table is empty, and are kept separate from that row.
 */
class MatchingRepository(
    private val matchingDao: MatchingDao
) {

    suspend fun checkAndSeed() {
        if (matchingDao.profileCount() == 0) {
            matchingDao.insertProfiles(sampleDirectory() + currentUserShell())
        } else if (matchingDao.getProfile(MatchCatalog.CURRENT_USER_ID) == null) {
            matchingDao.insertProfile(currentUserShell())
        }
        if (matchingDao.connectionCount() == 0 && matchingDao.getProfile(MEERA_ID) != null) {
            matchingDao.insertConnection(
                ConnectionRequestEntity(
                    id = "conn_seed_meera",
                    fromProfileId = MEERA_ID,
                    toProfileId = MatchCatalog.CURRENT_USER_ID,
                    status = STATUS_PENDING
                )
            )
        }
    }

    fun observeProfiles(): Flow<List<StudentMatchProfile>> {
        return matchingDao.observeProfiles().map { rows -> rows.map { it.toDomain() } }
    }

    fun observeConnections(): Flow<List<ConnectionRequest>> {
        return matchingDao.observeConnections().map { rows -> rows.map { it.toDomain() } }
    }

    suspend fun saveCurrentProfile(profile: StudentMatchProfile) {
        val existing = matchingDao.getProfile(MatchCatalog.CURRENT_USER_ID)
        matchingDao.insertProfile(
            profile.copy(
                id = MatchCatalog.CURRENT_USER_ID,
                isCurrentUser = true
            ).toEntity(updatedAt = System.currentTimeMillis(), fallbackAvatar = existing?.avatar)
        )
    }

    suspend fun sendConnection(toProfileId: String): String {
        if (toProfileId == MatchCatalog.CURRENT_USER_ID) {
            return "You can't send a request to yourself."
        }
        val other = matchingDao.getProfile(toProfileId) ?: return "That student is no longer available."
        val name = other.name.ifBlank { "this student" }
        val existing = matchingDao.findConnection(MatchCatalog.CURRENT_USER_ID, toProfileId)
        if (existing != null) {
            return when {
                existing.status.equals(STATUS_ACCEPTED, ignoreCase = true) -> "You're already connected with $name."
                existing.fromProfileId == MatchCatalog.CURRENT_USER_ID -> "Your request to $name is already pending."
                else -> "$name already sent you a request. Accept it from Connections."
            }
        }
        matchingDao.insertConnection(
            ConnectionRequestEntity(
                id = "conn_" + UUID.randomUUID().toString().take(8),
                fromProfileId = MatchCatalog.CURRENT_USER_ID,
                toProfileId = toProfileId,
                status = STATUS_PENDING
            )
        )
        return "Connection request sent to $name."
    }

    suspend fun acceptConnection(requestId: String): String {
        val request = matchingDao.getConnection(requestId) ?: return "That request is no longer available."
        if (request.toProfileId != MatchCatalog.CURRENT_USER_ID) {
            return "You can only accept requests sent to you."
        }
        if (request.status.equals(STATUS_ACCEPTED, ignoreCase = true)) {
            return "You're already connected."
        }
        matchingDao.updateConnectionStatus(requestId, STATUS_ACCEPTED)
        val name = matchingDao.getProfile(request.fromProfileId)?.name?.ifBlank { null } ?: "this student"
        return "You're now connected with $name."
    }

    private fun StudentProfileEntity.toDomain(): StudentMatchProfile {
        val role = try {
            StartupRoleCategory.valueOf(preferredRoleName)
        } catch (_: Exception) {
            StartupRoleCategory.DEVELOPER
        }
        return StudentMatchProfile(
            id = id,
            name = name,
            avatar = avatar.ifBlank { "ME" },
            branch = branch,
            year = year,
            college = college,
            skills = skillsCsv.toCsvList(),
            interests = interestsCsv.toCsvList(),
            areasOfInterest = areasCsv.toCsvList(),
            experience = experience,
            experienceTags = experienceTagsCsv.toCsvList(),
            goals = goals,
            preferredRole = role,
            teamPreference = TeamPreference.fromStored(teamPreferenceName),
            isCurrentUser = isCurrentUser,
            profileComplete = profileComplete
        )
    }

    private fun StudentMatchProfile.toEntity(updatedAt: Long, fallbackAvatar: String?): StudentProfileEntity {
        return StudentProfileEntity(
            id = id,
            name = name.trim(),
            avatar = avatar.ifBlank { fallbackAvatar ?: "ME" },
            branch = branch.trim(),
            year = year.trim(),
            college = college.trim(),
            skillsCsv = skills.toCsv(),
            interestsCsv = interests.toCsv(),
            areasCsv = areasOfInterest.toCsv(),
            experience = experience.trim(),
            experienceTagsCsv = experienceTags.toCsv(),
            goals = goals.trim(),
            preferredRoleName = preferredRole.name,
            teamPreferenceName = teamPreference.name,
            isCurrentUser = isCurrentUser,
            profileComplete = profileComplete,
            updatedAt = updatedAt
        )
    }

    private fun ConnectionRequestEntity.toDomain(): ConnectionRequest {
        return ConnectionRequest(
            id = id,
            fromProfileId = fromProfileId,
            toProfileId = toProfileId,
            status = status,
            createdAt = createdAt
        )
    }

    private fun String.toCsvList(): List<String> {
        return split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun List<String>.toCsv(): String = joinToString(", ")

    private fun currentUserShell(): StudentProfileEntity {
        return StudentProfileEntity(
            id = MatchCatalog.CURRENT_USER_ID,
            name = "",
            avatar = "ME",
            branch = "",
            year = "",
            college = "",
            skillsCsv = "",
            interestsCsv = "",
            areasCsv = "",
            experience = "",
            experienceTagsCsv = "",
            goals = "",
            preferredRoleName = StartupRoleCategory.DEVELOPER.name,
            teamPreferenceName = TeamPreference.FOUNDING.name,
            isCurrentUser = true,
            profileComplete = false
        )
    }

    /**
     * Development fixtures for the campus directory. Not the current student.
     */
    private fun sampleDirectory(): List<StudentProfileEntity> {
        return listOf(
            sample(
                id = "student_aisha",
                name = "Aisha Rahman",
                avatar = "AR",
                branch = "Design",
                year = "3rd Year",
                skills = "Figma, UI/UX Design, Prototyping",
                interests = "Product Design, Sustainability",
                areas = "Climate, Campus consumer",
                experience = "Led the design system for a hostel fest app used by 2,000 students.",
                tags = "Shipped a product, Hackathon",
                goals = "I want a founding team building sustainable campus products students actually use.",
                role = StartupRoleCategory.DESIGNER,
                team = TeamPreference.FOUNDING
            ),
            sample(
                id = "student_rohan",
                name = "Rohan Verma",
                avatar = "RV",
                branch = "CSE",
                year = "4th Year",
                skills = "Python, AI & LLMs, Firebase / SQL",
                interests = "Artificial Intelligence, Data",
                areas = "EdTech, SaaS",
                experience = "Research assistant on a syllabus search prototype and an AI lab internship.",
                tags = "Research, Internship",
                goals = "Looking for a research spinout that turns course material into a useful study product.",
                role = StartupRoleCategory.DEVELOPER,
                team = TeamPreference.RESEARCH
            ),
            sample(
                id = MEERA_ID,
                name = "Meera Iyer",
                avatar = "MI",
                branch = "Management",
                year = "2nd Year",
                skills = "Growth Marketing, Campus Outreach, Community Building",
                interests = "Community, Public Speaking",
                areas = "Campus consumer, Social impact",
                experience = "Ran ambassador programs across four hostels and a club-fair campaign.",
                tags = "Campus leadership, Shipped a product",
                goals = "I want to be the growth co-founder for a campus product that needs distribution.",
                role = StartupRoleCategory.MARKETER,
                team = TeamPreference.FOUNDING
            ),
            sample(
                id = "student_kabir",
                name = "Kabir Shah",
                avatar = "KS",
                branch = "CSE",
                year = "3rd Year",
                skills = "Kotlin, Jetpack Compose, Firebase / SQL",
                interests = "Open Source, Artificial Intelligence",
                areas = "Campus consumer, Marketplace",
                experience = "Android intern who shipped a club attendance app and joins hackathons often.",
                tags = "Internship, Hackathon",
                goals = "I want to build campus marketplaces with a small founding team and keep writing Kotlin.",
                role = StartupRoleCategory.DEVELOPER,
                team = TeamPreference.HACKATHON
            ),
            sample(
                id = "student_ananya",
                name = "Ananya Sen",
                avatar = "AS",
                branch = "Mechanical",
                year = "3rd Year",
                skills = "Product Management, Operations, Market Research",
                interests = "Sustainability, Writing",
                areas = "Climate, Social impact",
                experience = "Coordinated a reusable-container pilot with the campus canteen.",
                tags = "Shipped a product, Campus leadership",
                goals = "Building a social impact venture around waste on campus and need technical partners.",
                role = StartupRoleCategory.INTERN,
                team = TeamPreference.SOCIAL
            ),
            sample(
                id = "student_dev",
                name = "Dev Patel",
                avatar = "DP",
                branch = "IT",
                year = "2nd Year",
                skills = "Flutter, Node.js / Express, React / Next.js",
                interests = "Finance, Open Source",
                areas = "FinTech, SaaS",
                experience = "Built a split-bills prototype during a weekend hackathon.",
                tags = "Hackathon, First-time founder",
                goals = "Looking for a founding team to turn a student payments idea into a real product.",
                role = StartupRoleCategory.DEVELOPER,
                team = TeamPreference.FOUNDING
            ),
            sample(
                id = "student_sara",
                name = "Sara Khan",
                avatar = "SK",
                branch = "Design",
                year = "2nd Year",
                skills = "Graphic Design, Canva & Adobe Suite, Content Writing",
                interests = "Writing, Community",
                areas = "Media, Campus consumer",
                experience = "Content lead for the cultural fest social channels.",
                tags = "Campus leadership, Hackathon",
                goals = "I want a hackathon squad that cares about storytelling as much as the product.",
                role = StartupRoleCategory.DESIGNER,
                team = TeamPreference.HACKATHON
            ),
            sample(
                id = "student_arjun",
                name = "Arjun Nair",
                avatar = "AN",
                branch = "Commerce",
                year = "4th Year",
                skills = "Sales & Pitching, Market Research, Growth Marketing",
                interests = "Finance, Public Speaking",
                areas = "FinTech, Marketplace",
                experience = "First-time founder of a textbook resale pilot, plus a sponsorship internship.",
                tags = "First-time founder, Internship",
                goals = "I can sell and fundraise, and I am looking for builders to join a marketplace team.",
                role = StartupRoleCategory.MARKETER,
                team = TeamPreference.FOUNDING
            )
        )
    }

    private fun sample(
        id: String,
        name: String,
        avatar: String,
        branch: String,
        year: String,
        skills: String,
        interests: String,
        areas: String,
        experience: String,
        tags: String,
        goals: String,
        role: StartupRoleCategory,
        team: TeamPreference
    ): StudentProfileEntity {
        return StudentProfileEntity(
            id = id,
            name = name,
            avatar = avatar,
            branch = branch,
            year = year,
            college = "Campus Engineering",
            skillsCsv = skills,
            interestsCsv = interests,
            areasCsv = areas,
            experience = experience,
            experienceTagsCsv = tags,
            goals = goals,
            preferredRoleName = role.name,
            teamPreferenceName = team.name,
            isCurrentUser = false,
            profileComplete = true
        )
    }

    private companion object {
        const val MEERA_ID = "student_meera"
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
    }
}
