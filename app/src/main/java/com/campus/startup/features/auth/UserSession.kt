package com.campus.startup.features.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CampusUserRole(val displayName: String, val badgeLabel: String) {
    STUDENT("Student", "STUDENT"),
    PROFESSOR("Professor", "PROFESSOR")
}

data class LoggedInUser(
    val username: String,
    val displayName: String,
    val role: CampusUserRole,
    val department: String
)

object UserSession {
    // Predefined credentials for Student and Professor login
    const val STUDENT_USERNAME = "student"
    const val STUDENT_PASSWORD = "password123"

    const val PROFESSOR_USERNAME = "professor"
    const val PROFESSOR_PASSWORD = "password123"

    private val _currentUser = MutableStateFlow<LoggedInUser?>(null)
    val currentUser: StateFlow<LoggedInUser?> = _currentUser.asStateFlow()

    fun authenticate(
        role: CampusUserRole,
        usernameInput: String,
        passwordInput: String
    ): Result<LoggedInUser> {
        val cleanUser = usernameInput.trim().lowercase()
        val cleanPass = passwordInput.trim()

        return when (role) {
            CampusUserRole.STUDENT -> {
                val validUser = cleanUser in setOf("student", "student@campus.edu", "khushi", "arjun")
                val validPass = cleanPass in setOf(STUDENT_PASSWORD, "student123", "campus123", "123456")
                if (validUser && validPass) {
                    val user = LoggedInUser(
                        username = cleanUser,
                        displayName = if (cleanUser == "khushi") "Khushi Srivastava" else "Arjun Mehta",
                        role = CampusUserRole.STUDENT,
                        department = "B.Tech CSE • 3rd Year"
                    )
                    _currentUser.value = user
                    Result.success(user)
                } else {
                    Result.failure(IllegalArgumentException("Invalid Student credentials. Use username: student & password: password123"))
                }
            }
            CampusUserRole.PROFESSOR -> {
                val validUser = cleanUser in setOf("professor", "prof", "prof@campus.edu", "dr.sharma")
                val validPass = cleanPass in setOf(PROFESSOR_PASSWORD, "prof123", "faculty123", "123456")
                if (validUser && validPass) {
                    val user = LoggedInUser(
                        username = cleanUser,
                        displayName = "Dr. Rajesh Sharma",
                        role = CampusUserRole.PROFESSOR,
                        department = "Faculty Mentor • Innovation & Incubation Cell"
                    )
                    _currentUser.value = user
                    Result.success(user)
                } else {
                    Result.failure(IllegalArgumentException("Invalid Professor credentials. Use username: professor & password: password123"))
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}
