package com.campus.startup.matching

import com.campus.startup.features.matching.data.model.ConnectionRequest
import com.campus.startup.features.matching.data.model.ConnectionStatus
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.MatchResult
import com.campus.startup.features.matching.data.model.MatchingDirectory
import com.campus.startup.features.matching.data.model.MatchingFilters
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.TeamPreference
import com.campus.startup.features.matching.data.model.resolveConnectionStatus
import com.campus.startup.features.matching.domain.CofounderMatcher
import com.campus.startup.features.matching.domain.ProfileValidator
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CofounderMatcherTest {

    @Test
    fun complementaryDesignerOutranksIdenticalSkillClone() {
        val me = technicalStudent(name = "Me")
        val designer = me.copy(
            id = "designer",
            name = "Aisha",
            skills = listOf("Figma", "UI/UX Design", "Prototyping"),
            preferredRole = StartupRoleCategory.DESIGNER
        )
        val clone = me.copy(id = "clone", name = "Kabir")

        val designMatch = CofounderMatcher.evaluate(me, designer)
        val cloneMatch = CofounderMatcher.evaluate(me, clone)

        assertTrue(designMatch.compatibility > cloneMatch.compatibility)
        assertTrue(designMatch.complementarySkills.isNotEmpty())
        assertTrue(cloneMatch.complementarySkills.isEmpty())
        assertTrue(designMatch.reasons.any { it.contains("design", ignoreCase = true) })
        assertTrue(cloneMatch.reasons.any { it.contains("peer", ignoreCase = true) })
        assertTrue(designMatch.compatibility in 0..100)
        assertTrue(cloneMatch.compatibility in 0..100)
    }

    @Test
    fun sameCraftDifferentToolsScoreBelowANewCraft() {
        val me = technicalStudent(name = "Me")
        val designer = me.copy(
            id = "designer",
            name = "Aisha",
            skills = listOf("Figma", "UI/UX Design", "Prototyping"),
            preferredRole = StartupRoleCategory.DESIGNER
        )
        val pythonPeer = me.copy(
            id = "peer",
            name = "Rohan",
            skills = listOf("Python", "AI & LLMs", "Node.js / Express")
        )

        val designMatch = CofounderMatcher.evaluate(me, designer)
        val peerMatch = CofounderMatcher.evaluate(me, pythonPeer)

        assertTrue(peerMatch.complementarySkills.isNotEmpty())
        assertTrue(designMatch.compatibility > peerMatch.compatibility)
    }

    @Test
    fun sharedInterestsAndGoalsAreExplained() {
        val me = technicalStudent(name = "Me").copy(
            interests = listOf("Artificial Intelligence"),
            areasOfInterest = listOf("EdTech"),
            teamPreference = TeamPreference.RESEARCH,
            experienceTags = emptyList(),
            goals = "I want to build a nightfood study assistant for hostels."
        )
        val other = technicalStudent(name = "Rohan").copy(
            id = "rohan",
            skills = listOf("Figma", "Prototyping"),
            preferredRole = StartupRoleCategory.DESIGNER,
            interests = listOf("Product Design"),
            areasOfInterest = listOf("EdTech"),
            teamPreference = TeamPreference.FOUNDING,
            experienceTags = emptyList(),
            goals = "A nightfood product for students who study late."
        )

        val match = CofounderMatcher.evaluate(me, other)

        assertTrue(match.sharedInterests.any { it.equals("EdTech", ignoreCase = true) })
        assertTrue(match.reasons.any { it.contains("EdTech") })
        assertTrue(match.reasons.any { it.contains("nightfood") })
    }

    @Test
    fun incompleteProfileDoesNotInventAScore() {
        val me = StudentMatchProfile(id = "me", name = "", skills = emptyList(), goals = "")
        val other = technicalStudent(name = "Aisha")

        val match = CofounderMatcher.evaluate(me, other)

        assertFalse(match.profileReady)
        assertEquals(0, match.compatibility)
        assertTrue(match.reasons.any { it.contains("Complete your profile") })
    }

    @Test
    fun filtersKeepComplementaryStudentsAndHonorSearch() {
        val readyMatch = MatchResult(80, listOf("Complements your technical background with design."), listOf("Figma"), emptyList(), emptyList(), true)
        val identicalMatch = MatchResult(20, listOf("peer"), emptyList(), emptyList(), emptyList(), true)
        val designer = DiscoverableProfile(
            profile = technicalStudent("Aisha").copy(
                id = "aisha",
                skills = listOf("Figma"),
                preferredRole = StartupRoleCategory.DESIGNER,
                interests = listOf("Product Design")
            ),
            match = readyMatch,
            connectionStatus = ConnectionStatus.NONE
        )
        val clone = DiscoverableProfile(
            profile = technicalStudent("Kabir").copy(id = "kabir"),
            match = identicalMatch,
            connectionStatus = ConnectionStatus.NONE
        )

        val complementary = MatchingDirectory.filter(
            students = listOf(designer, clone),
            filters = MatchingFilters(complementaryOnly = true),
            profileReady = true
        )
        assertEquals(listOf("aisha"), complementary.map { it.profile.id })

        val byName = MatchingDirectory.filter(
            students = listOf(designer, clone),
            filters = MatchingFilters(query = "Kabir"),
            profileReady = true
        )
        assertEquals(listOf("kabir"), byName.map { it.profile.id })

        val byRole = MatchingDirectory.filter(
            students = listOf(designer, clone),
            filters = MatchingFilters(role = StartupRoleCategory.DESIGNER),
            profileReady = true
        )
        assertEquals(listOf("aisha"), byRole.map { it.profile.id })

        val ignoredWhenProfileMissing = MatchingDirectory.filter(
            students = listOf(designer, clone),
            filters = MatchingFilters(complementaryOnly = true),
            profileReady = false
        )
        assertEquals(2, ignoredWhenProfileMissing.size)
    }

    @Test
    fun validatorRejectsAnEmptyProfileAndAcceptsACompleteOne() {
        val empty = StudentMatchProfile(id = "me", name = "A", goals = "short")
        val emptyErrors = ProfileValidator.validate(empty)
        assertFalse(emptyErrors.isValid)
        assertTrue(emptyErrors.name != null)
        assertTrue(emptyErrors.skills != null)
        assertTrue(emptyErrors.goals != null)

        val complete = technicalStudent("Khushi Sharma").copy(
            branch = "CSE",
            year = "3rd Year",
            interests = listOf("Open Source"),
            areasOfInterest = listOf("EdTech")
        )
        assertTrue(ProfileValidator.validate(complete).isValid)
    }

    @Test
    fun connectionStatusFollowsDirection() {
        val pendingIn = ConnectionRequest("1", "them", "me", "PENDING", 1L)
        val pendingOut = ConnectionRequest("2", "me", "them", "PENDING", 1L)
        val accepted = ConnectionRequest("3", "them", "me", "ACCEPTED", 1L)

        assertEquals(ConnectionStatus.PENDING_INCOMING, resolveConnectionStatus("me", "them", listOf(pendingIn)))
        assertEquals(ConnectionStatus.PENDING_OUTGOING, resolveConnectionStatus("me", "them", listOf(pendingOut)))
        assertEquals(ConnectionStatus.ACCEPTED, resolveConnectionStatus("me", "them", listOf(accepted)))
        assertEquals(ConnectionStatus.NONE, resolveConnectionStatus("me", "them", emptyList()))
    }

    private fun technicalStudent(name: String): StudentMatchProfile {
        return StudentMatchProfile(
            id = name.lowercase(),
            name = name,
            branch = "CSE",
            year = "3rd Year",
            college = "Campus Engineering",
            skills = listOf("Kotlin", "Jetpack Compose", "Firebase / SQL"),
            interests = listOf("Artificial Intelligence", "Open Source"),
            areasOfInterest = listOf("Campus consumer", "Marketplace"),
            experience = "Shipped a campus ordering app.",
            experienceTags = listOf("Shipped a product", "Hackathon"),
            goals = "I want a founding team for a campus product students use every week.",
            preferredRole = StartupRoleCategory.DEVELOPER,
            teamPreference = TeamPreference.FOUNDING,
            profileComplete = true
        )
    }
}
