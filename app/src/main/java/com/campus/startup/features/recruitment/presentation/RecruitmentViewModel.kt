package com.campus.startup.features.recruitment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.campus.startup.features.recruitment.data.model.JobApplication
import com.campus.startup.features.recruitment.data.model.StartupOpening
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import com.campus.startup.features.recruitment.data.repository.RecruitmentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecruitmentFilters(
    val category: StartupRoleCategory = StartupRoleCategory.ALL,
    val query: String = "",
    val skills: Set<String> = setOf("Kotlin", "Figma", "UI/UX Design"),
    val onlyMatching: Boolean = false,
    val workType: String = "All"
)

class RecruitmentViewModel(
    private val repository: RecruitmentRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.checkAndSeedOpenings()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(StartupRoleCategory.ALL)
    val selectedCategory: StateFlow<StartupRoleCategory> = _selectedCategory.asStateFlow()

    // Student's selected skills and interests
    private val _studentSkills = MutableStateFlow<Set<String>>(
        setOf("Kotlin", "Figma", "UI/UX Design")
    )
    val studentSkills: StateFlow<Set<String>> = _studentSkills.asStateFlow()

    // Filter toggle: only openings matching student skills
    private val _onlyMatchingSkills = MutableStateFlow(false)
    val onlyMatchingSkills: StateFlow<Boolean> = _onlyMatchingSkills.asStateFlow()

    // Work arrangement filter: "All", "Remote", "Hybrid", "On-Campus"
    private val _workTypeFilter = MutableStateFlow("All")
    val workTypeFilter: StateFlow<String> = _workTypeFilter.asStateFlow()

    private val filtersFlow: Flow<RecruitmentFilters> = combine(
        _selectedCategory,
        _searchQuery,
        _studentSkills,
        combine(_onlyMatchingSkills, _workTypeFilter) { onlyMatch, workType -> Pair(onlyMatch, workType) }
    ) { category, query, skills, matchAndWork ->
        RecruitmentFilters(
            category = category,
            query = query,
            skills = skills,
            onlyMatching = matchAndWork.first,
            workType = matchAndWork.second
        )
    }

    val uiState: StateFlow<RecruitmentUiState> = combine(
        repository.getAllOpenings(),
        filtersFlow
    ) { openings: List<StartupOpening>, filters: RecruitmentFilters ->
        val processed = openings.map { opening ->
            val matched = opening.requiredSkills.filter { reqSkill ->
                filters.skills.any { s -> reqSkill.contains(s, ignoreCase = true) || s.contains(reqSkill, ignoreCase = true) }
            }
            val matchPercent = if (opening.requiredSkills.isEmpty()) 100 else {
                ((matched.size.toFloat() / opening.requiredSkills.size.toFloat()) * 100).toInt()
            }
            opening.copy(
                matchPercentage = matchPercent,
                matchedSkills = matched
            )
        }

        val filtered = processed.filter { opening ->
            val matchesCategory = (filters.category == StartupRoleCategory.ALL || opening.roleCategory == filters.category)
            val matchesQuery = filters.query.isBlank() ||
                opening.title.contains(filters.query, ignoreCase = true) ||
                opening.startupName.contains(filters.query, ignoreCase = true) ||
                opening.requiredSkills.any { it.contains(filters.query, ignoreCase = true) } ||
                opening.description.contains(filters.query, ignoreCase = true)

            val matchesWorkType = (filters.workType == "All" || opening.workType.equals(filters.workType, ignoreCase = true))
            val matchesSkillReq = !filters.onlyMatching || opening.matchPercentage > 0

            matchesCategory && matchesQuery && matchesWorkType && matchesSkillReq
        }.sortedWith(
            compareByDescending<StartupOpening> { it.matchPercentage }
                .thenByDescending { it.deadlineDays }
        )

        val highMatchCount = processed.count { it.matchPercentage >= 50 }
        val savedCount = openings.count { it.isSaved }

        RecruitmentUiState.Success(
            openings = filtered,
            totalCount = openings.size,
            highMatchCount = highMatchCount,
            savedCount = savedCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecruitmentUiState.Loading
    )

    val savedOpenings: StateFlow<List<StartupOpening>> = repository.getSavedOpenings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val myPostedOpenings: StateFlow<List<StartupOpening>> = repository.getMyPostedOpenings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allApplications: StateFlow<List<JobApplication>> = repository.getAllApplications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: StartupRoleCategory) {
        _selectedCategory.value = category
    }

    fun toggleStudentSkill(skill: String) {
        val current = _studentSkills.value.toMutableSet()
        if (current.contains(skill)) {
            current.remove(skill)
        } else {
            current.add(skill)
        }
        _studentSkills.value = current
    }

    fun setStudentSkills(newSkills: Set<String>) {
        _studentSkills.value = newSkills
    }

    fun clearStudentSkills() {
        _studentSkills.value = emptySet()
    }

    fun toggleOnlyMatching(enabled: Boolean) {
        _onlyMatchingSkills.value = enabled
    }

    fun onWorkTypeFilterChanged(workType: String) {
        _workTypeFilter.value = workType
    }

    fun toggleSaveOpening(opening: StartupOpening) {
        viewModelScope.launch {
            repository.toggleSave(opening.id, opening.isSaved)
        }
    }

    fun postOpening(
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
        viewModelScope.launch {
            repository.postOpening(
                startupName = startupName,
                startupLogo = startupLogo,
                startupStage = startupStage,
                title = title,
                roleCategory = roleCategory,
                skills = skills,
                workType = workType,
                commitment = commitment,
                stipend = stipend,
                location = location,
                deadlineDays = deadlineDays,
                description = description,
                responsibilities = responsibilities,
                perks = perks,
                founderName = founderName,
                founderEmail = founderEmail
            )
        }
    }

    fun deleteOpening(openingId: String) {
        viewModelScope.launch {
            repository.deleteOpening(openingId)
        }
    }

    fun submitApplication(
        opening: StartupOpening,
        applicantName: String,
        applicantEmail: String,
        applicantPhone: String,
        applicantBranchYear: String,
        portfolioUrl: String,
        matchedSkills: List<String>,
        pitchNote: String
    ) {
        viewModelScope.launch {
            repository.submitApplication(
                opening = opening,
                applicantName = applicantName,
                applicantEmail = applicantEmail,
                applicantPhone = applicantPhone,
                applicantBranchYear = applicantBranchYear,
                portfolioUrl = portfolioUrl,
                matchedSkills = matchedSkills,
                pitchNote = pitchNote
            )
        }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, newStatus)
        }
    }
}

class RecruitmentViewModelFactory(
    private val repository: RecruitmentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecruitmentViewModel::class.java)) {
            return RecruitmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
