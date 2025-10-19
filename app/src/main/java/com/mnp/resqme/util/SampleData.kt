package com.mnp.resqme.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import com.mnp.resqme.data.models.*
import com.mnp.resqme.ui.navigation.Screen

fun getGuideCategories() = listOf(
    GuideCategory("all", "All", Icons.Default.Apps),
    GuideCategory("medical", "Medical", Icons.Default.LocalHospital),
    GuideCategory("fire", "Fire Safety", Icons.Default.LocalFireDepartment),
    GuideCategory("water", "Water Safety", Icons.Default.Water),
    GuideCategory("natural_disaster", "Natural Disasters", Icons.Default.Tsunami),
    GuideCategory("personal_safety", "Personal Safety", Icons.Default.Security)
)

fun getGuidesByCategory(category: String): List<GuideItem> {
    val allGuides = listOf(
        GuideItem("1", "CPR for Adults", "Critical life-saving technique for cardiac arrest", "medical", 1, 5,
            listOf("Check responsiveness", "Call 108", "Position hands correctly", "Push hard and fast", "Give rescue breaths if trained")),
        GuideItem("2", "Fire Extinguisher Use", "How to safely use fire extinguishers", "fire", 1, 3,
            listOf("Pull the pin", "Aim at base of fire", "Squeeze handle", "Sweep side to side")),
        GuideItem("3", "Earthquake Response", "What to do during an earthquake", "natural_disaster", 2, 2,
            listOf("Drop to hands and knees", "Take cover under sturdy furniture", "Hold on until shaking stops", "Exit carefully when safe")),
        GuideItem("4", "Water Rescue Basics", "Basic water rescue techniques", "water", 2, 10,
            listOf("Assess the situation", "Look for flotation devices", "Throw, don't go", "Call for professional help")),
        GuideItem("5", "Personal Safety Tips", "General personal safety guidelines", "personal_safety", 3, 15,
            listOf("Be aware of surroundings", "Trust your instincts", "Have emergency contacts ready", "Keep devices charged"))
    )

    return if (category == "all") allGuides else allGuides.filter { it.category == category }
}

fun getGuideById(id: String): GuideItem {
    return getGuidesByCategory("all").find { it.id == id } ?: getGuidesByCategory("all").first()
}

fun getCategoryIcon(category: String) = when (category) {
    "medical" -> Icons.Default.LocalHospital
    "fire" -> Icons.Default.LocalFireDepartment
    "water" -> Icons.Default.Water
    "natural_disaster" -> Icons.Default.Tsunami
    "personal_safety" -> Icons.Default.Security
    else -> Icons.Default.Book
}

fun getEmergencyInfoFromUser(user: User) = listOf(
    ProfileInfo("Emergency Contact", user.emergencyContact, Icons.Default.ContactPhone),
    ProfileInfo("Emergency Phone", user.emergencyPhone, Icons.Default.Phone),
    ProfileInfo("Blood Type", user.bloodType, Icons.Default.Bloodtype),
    ProfileInfo("Medical Conditions", user.medicalConditions, Icons.Default.MedicalInformation),
    ProfileInfo("Allergies", user.allergies, Icons.Default.Warning)
)

fun getAppSettings(navController: NavController) = listOf(
    AppSetting("Emergency Contacts", "Manage your emergency contacts", Icons.Default.Contacts) {
        navController.navigate(Screen.EmergencyContacts.route)
    },
    AppSetting("Notifications", "Manage emergency alerts", Icons.Default.Notifications) { },
    AppSetting("Offline Guides", "Download guides for offline use", Icons.Default.Download) { },
    AppSetting("Location Services", "Allow location access for emergencies", Icons.Default.LocationOn) { },
    AppSetting("Privacy", "Privacy and data settings", Icons.Default.PrivacyTip) { },
    AppSetting("About", "App version and information", Icons.Default.Info) { }
)

fun getQuickActions() = listOf(
    QuickAction("First Aid", Icons.Default.MedicalServices, Screen.Guides.route),
    QuickAction("Fire Safety", Icons.Default.LocalFireDepartment, Screen.Guides.route),
    QuickAction("Water Rescue", Icons.Default.Water, Screen.Guides.route),
    QuickAction("Call 112", Icons.Default.Phone, Screen.Emergency.route)
)

fun getEmergencyServices() = listOf(
    EmergencyServices("sample Emergency", "9321955817", "Police, Fire, Medical Emergency", Icons.Default.Emergency, true),
    EmergencyServices("Police", "100", "Law enforcement emergency", Icons.Default.LocalPolice, true),
    EmergencyServices("Fire Department", "101", "Fire emergency services", Icons.Default.LocalFireDepartment, true),
    EmergencyServices("Medical Emergency", "102", "Ambulance and medical services", Icons.Default.LocalHospital, true),
    EmergencyServices("Poison Control", "1-800-222-1222", "24/7 poison emergency helpline", Icons.Default.MedicalServices, false)
)

fun getEmergencyActions() = listOf(
    EmergencyAction("Share Location", "Send your current location to emergency contacts", Icons.Default.LocationOn) { },
    EmergencyAction("Medical Info", "Access your medical information", Icons.Default.MedicalInformation) { },
    EmergencyAction("Emergency Checklist", "Quick emergency response checklist", Icons.Default.Checklist) { },
    EmergencyAction("Flashlight", "Use phone flashlight", Icons.Default.FlashlightOn) { }
)