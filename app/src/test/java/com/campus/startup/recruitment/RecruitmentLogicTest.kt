package com.campus.startup.recruitment

import com.campus.startup.features.recruitment.data.model.StartupOpening
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecruitmentLogicTest {

    @Test
    fun testRoleCategoriesContainExpectedRoles() {
        val categories = StartupRoleCategory.values().map { it.name }
        assertTrue(categories.contains("DEVELOPER"))
        assertTrue(categories.contains("DESIGNER"))
        assertTrue(categories.contains("MARKETER"))
        assertTrue(categories.contains("INTERN"))
        assertTrue(categories.contains("ALL"))
    }

    @Test
    fun testSkillMatchCalculation_FullMatch() {
        val studentSkills = setOf("Kotlin", "Jetpack Compose", "Room")
        val opening = StartupOpening(
            id = "test_1",
            startupName = "CampusBites",
            startupStage = "Incubated",
            title = "Android Developer",
            roleCategory = StartupRoleCategory.DEVELOPER,
            requiredSkills = listOf("Kotlin", "Jetpack Compose", "Room"),
            stipend = "₹15,000/mo",
            description = "Lead Android development"
        )

        val matched = opening.requiredSkills.filter { reqSkill ->
            studentSkills.any { s -> reqSkill.contains(s, ignoreCase = true) || s.contains(reqSkill, ignoreCase = true) }
        }
        val matchPercent = ((matched.size.toFloat() / opening.requiredSkills.size.toFloat()) * 100).toInt()

        assertEquals(3, matched.size)
        assertEquals(100, matchPercent)
    }

    @Test
    fun testSkillMatchCalculation_PartialMatch() {
        val studentSkills = setOf("Figma")
        val opening = StartupOpening(
            id = "test_2",
            startupName = "CampusConnect",
            startupStage = "E-Cell",
            title = "UI/UX Designer",
            roleCategory = StartupRoleCategory.DESIGNER,
            requiredSkills = listOf("Figma", "UI/UX Design", "Design Systems", "Prototyping"),
            stipend = "₹12,000/mo",
            description = "Figma product design"
        )

        val matched = opening.requiredSkills.filter { reqSkill ->
            studentSkills.any { s -> reqSkill.contains(s, ignoreCase = true) || s.contains(reqSkill, ignoreCase = true) }
        }
        val matchPercent = ((matched.size.toFloat() / opening.requiredSkills.size.toFloat()) * 100).toInt()

        assertEquals(1, matched.size)
        assertEquals(25, matchPercent)
    }

    @Test
    fun testCategoryFiltering() {
        val openings = listOf(
            StartupOpening(
                id = "1",
                startupName = "TechCo",
                startupStage = "Seed",
                title = "Backend Dev",
                roleCategory = StartupRoleCategory.DEVELOPER,
                requiredSkills = listOf("Python"),
                stipend = "₹20,000",
                description = "Python APIs"
            ),
            StartupOpening(
                id = "2",
                startupName = "GrowthCo",
                startupStage = "Bootstrapped",
                title = "Campus Marketer",
                roleCategory = StartupRoleCategory.MARKETER,
                requiredSkills = listOf("Social Media"),
                stipend = "₹10,000",
                description = "Campus campaigns"
            )
        )

        val devOpenings = openings.filter { it.roleCategory == StartupRoleCategory.DEVELOPER }
        assertEquals(1, devOpenings.size)
        assertEquals("Backend Dev", devOpenings.first().title)
    }
}
