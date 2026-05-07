package com.cornai.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Result : Screen("result/{diseaseName}/{confidence}/{isHealthy}") {
        fun createRoute(diseaseName: String, confidence: Float, isHealthy: Boolean): String {
            return "result/$diseaseName/$confidence/$isHealthy"
        }
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object ProfileEdit : Screen("profile_edit")
    object NotificationSettings : Screen("notification_settings")
    object Welcome : Screen("welcome")
    object Settings : Screen("settings")
    object ResultDetail : Screen("result_detail")
    object ProfileEnhanced : Screen("profile_enhanced")
    object HelpSupport : Screen("help_support")
    object HistoryEnhanced : Screen("history_enhanced")
    object Help : Screen("help")
    object PrivacyPolicy : Screen("privacy_policy")
}
