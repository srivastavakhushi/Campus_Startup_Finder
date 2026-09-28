package com.campus.startup.features.opportunities.data.repository

import com.campus.startup.features.opportunities.data.local.OpportunityDao
import com.campus.startup.features.opportunities.data.local.OpportunityEntity
import com.campus.startup.features.opportunities.data.model.Opportunity
import com.campus.startup.features.opportunities.data.model.OpportunityType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class OpportunityRepository(
    private val opportunityDao: OpportunityDao
) {

    // Initial realistic mock opportunities (can be plugged into Cloud Firestore)
    private val _opportunities = MutableStateFlow<List<Opportunity>>(
        listOf(
            Opportunity(
                id = "opp_1",
                title = "Smart India Hackathon 2026 - Campus Qualifier",
                organization = "College E-Cell & Incubation Center",
                type = OpportunityType.HACKATHON,
                domain = "AI & Smart Governance",
                stipendOrPrize = "₹1,00,000 Prize Pool",
                location = "Campus Tech Auditorium",
                deadlineDays = 4,
                description = "Represent our college at the National Smart India Hackathon. Build innovative digital solutions for hardware and software problem statements given by ministries.",
                requirements = listOf(
                    "Team of 6 students (at least 1 female team member)",
                    "Basic knowledge of Mobile App, AI, or Web Development",
                    "Working MVP or prototype submission"
                ),
                perks = listOf("Direct entry to national round", "Full travel sponsorship", "Incubation mentorship"),
                applyUrl = "https://sih.gov.in"
            ),
            Opportunity(
                id = "opp_2",
                title = "Founding Android Engineer (Jetpack Compose)",
                organization = "CampusBites (Student Delivery Startup)",
                type = OpportunityType.STARTUP_ROLE,
                domain = "Android / Kotlin",
                stipendOrPrize = "₹18,000 / month + Equity",
                location = "Hybrid (Campus Incubator Lab)",
                deadlineDays = 6,
                description = "CampusBites is currently operating in 3 hostels and scaling to the whole university. We are looking for an ambitious Android engineer to lead our student consumer app development.",
                requirements = listOf(
                    "Strong proficiency in Kotlin and Jetpack Compose",
                    "Experience with Room Database and Firebase",
                    "Passion for early-stage startup culture"
                ),
                perks = listOf("Founding engineer equity grant", "Flexible hours during exam weeks", "Direct user feedback"),
                applyUrl = "https://campusbites.internal/apply"
            ),
            Opportunity(
                id = "opp_3",
                title = "Student Entrepreneurship & Prototype Grant",
                organization = "Institution Innovation Council (IIC)",
                type = OpportunityType.GRANT,
                domain = "Fintech, EdTech, Healthcare",
                stipendOrPrize = "₹2,50,000 Equity-free Grant",
                location = "College Incubation Cell",
                deadlineDays = 12,
                description = "Have a startup idea? The College IIC provides non-dilutive seed funding to build minimum viable products (MVPs). Open to all departments.",
                requirements = listOf(
                    "Must be an enrolled student team",
                    "Pitch deck (max 10 slides)",
                    "Clear problem-solution validation"
                ),
                perks = listOf("Zero equity taken", "Free campus office space", "Legal & patent registration support"),
                applyUrl = "https://iic.college.edu/grant"
            ),
            Opportunity(
                id = "opp_4",
                title = "UI/UX & Product Design Intern",
                organization = "DocuSummarize (AI Startup)",
                type = OpportunityType.INTERNSHIP,
                domain = "UI/UX / Figma",
                stipendOrPrize = "₹12,000 / month",
                location = "Remote",
                deadlineDays = 9,
                description = "Design intuitive user flows and micro-interactions for an AI document summarization platform founded by final-year students with 10k+ monthly active users.",
                requirements = listOf(
                    "Portfolio with at least 2 mobile app case studies",
                    "Proficiency in Figma and Design Systems",
                    "Good eye for typography and spacing"
                ),
                perks = listOf("Letter of Recommendation", "Paid Figma & asset subscriptions", "Mentorship from Senior Designers"),
                applyUrl = "https://docusummarize.ai/careers"
            ),
            Opportunity(
                id = "opp_5",
                title = "Campus Shark Tank: Pitch Competition 2026",
                organization = "E-Cell Annual Summit",
                type = OpportunityType.COMPETITION,
                domain = "All Domains",
                stipendOrPrize = "₹75,000 Cash + Investor Access",
                location = "Main Seminar Hall",
                deadlineDays = 15,
                description = "Pitch your startup in front of renowned angel investors, alumni entrepreneurs, and venture capitalists. Win cash prizes and immediate angel backing.",
                requirements = listOf(
                    "3-minute elevator pitch + 2-minute Q&A",
                    "Demonstrable traction or prototype",
                    "Solo or team entries welcome"
                ),
                perks = listOf("Investor network access", "Media coverage in campus newsletter", "Free cloud credits"),
                applyUrl = "https://ecell.college.edu/sharktank"
            )
        )
    )

    // Flow of opportunities combined with Room bookmark status
    fun getOpportunities(): Flow<List<Opportunity>> {
        return combine(_opportunities, opportunityDao.getAllSavedOpportunities()) { opps, savedEntities ->
            val savedIds = savedEntities.map { it.id }.toSet()
            opps.map { opp ->
                opp.copy(isBookmarked = savedIds.contains(opp.id))
            }
        }
    }

    fun getOpportunityById(id: String): Opportunity? {
        return _opportunities.value.find { it.id == id }
    }

    suspend fun toggleBookmark(opportunity: Opportunity) {
        if (opportunity.isBookmarked) {
            opportunityDao.removeOpportunity(opportunity.id)
        } else {
            opportunityDao.saveOpportunity(
                OpportunityEntity(
                    id = opportunity.id,
                    title = opportunity.title,
                    organization = opportunity.organization,
                    typeName = opportunity.type.name,
                    domain = opportunity.domain,
                    stipendOrPrize = opportunity.stipendOrPrize,
                    location = opportunity.location,
                    deadlineDays = opportunity.deadlineDays,
                    description = opportunity.description
                )
            )
        }
    }

    fun getSavedOpportunities(): Flow<List<OpportunityEntity>> {
        return opportunityDao.getAllSavedOpportunities()
    }

    fun postOpportunity(newOpportunity: Opportunity) {
        _opportunities.value = listOf(newOpportunity) + _opportunities.value
    }
}
