package com.example.data

import androidx.room.*

@Entity(tableName = "wrestlers")
data class Wrestler(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ringName: String = name,
    val realName: String = "",
    val companyId: Int = 1, // 1 = Apex (Player), 0 = Free Agent, 2 = GWG (Rival), 3 = GPW, 4 = TWA
    val popularity: Int,       // 0 - 100
    val inRingSkill: Int,      // 0 - 100
    val ringSkill: Int = inRingSkill,
    val charisma: Int = 65,
    val micSkill: Int,         // 0 - 100
    val stamina: Int,          // 0 - 100 (depleted by matches, recovers weekly)
    val injuryWeeks: Int = 0,  // 0 of course means healthy, > 0 is injured weeks remaining
    val injuryRisk: Int = 12,  // 0 - 100 risk score
    val morale: Int,           // 0 - 100 (affected by contract, losses, and booking)
    val momentum: Int = 50,    // 0 - 100
    val heelFace: String,      // "FACE" or "HEEL"
    val alignment: String = heelFace,
    val salary: Double,        // weekly payroll cost
    val isContracted: Boolean, // true = signed with player company, false = Free Agent
    val style: String,         // "Showman", "Brawler", "Technician", "High Flyer"
    val traits: String = "Resilient, Crowd Pleaser",
    val finisher: String = "Apex Slammer",
    val gender: String = "Male",
    val age: Int = 26,
    val activeStatus: String = "Active", // "Active", "Retired", "Prospect"
    val portraitPath: String = "",
    val loyalty: Int = 70,               // 0 - 100 (loyalty to current company)
    val transferValue: Double = 0.0,     // 0.0 means calculate dynamically
    val interestLevel: Int = 50,         // 0 - 100 interest to join player company
    val isShortlisted: Boolean = false,  // shortlist tracking
    val scoutingProgress: Int = 0        // 0 to 100 (unscouted to fully scouted)
)

@Entity(tableName = "transfer_negotiations")
data class TransferNegotiation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wrestlerId: Int,
    val wrestlerName: String,
    val buyerCompanyId: Int,
    val sellerCompanyId: Int,
    val bidAmount: Double,
    val wageOffered: Double,
    val contractWeeksOffered: Int,
    val roleOffered: String,
    val status: String, // "PENDING_COMPANY", "COMPANY_REJECTED", "COMPANY_ACCEPTED", "PENDING_TALENT", "TALENT_REJECTED", "TALENT_ACCEPTED", "COUNTERED_BY_COMPANY"
    val counterBidAmount: Double = 0.0,
    val counterWage: Double = 0.0,
    val rejectionReason: String = "",
    val weekNegotiated: Int
)

@Entity(tableName = "scout_assignments")
data class ScoutAssignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scoutName: String,
    val wrestlerId: Int,
    val wrestlerName: String,
    val weeksRemaining: Int,
    val focusType: String = "" // "General", "Financial demands", "Loyalty scan"
)

@Entity(tableName = "rumours")
data class Rumour(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val weekGenerated: Int
)

@Entity(tableName = "wrestler_era_snapshots")
data class WrestlerEraSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wrestlerId: Int,
    val yearOrEraKey: String, // e.g. "Era of Gold (2024)"
    val company: String,
    val pushLevel: String,    // "Main Eventer", "Upper Midcard", "Underdog"
    val role: String,         // "Wrestler", "Manager", "Referee"
    val activeTitles: String,
    val allies: String,
    val rivals: String,
    val tagPartners: String,
    val manager: String,
    val notes: String
)

@Entity(tableName = "contracts")
data class Contract(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wrestlerId: Int,
    val companyName: String,
    val weeklySalary: Double,
    val weeksRemaining: Int,
    val moraleClause: Boolean = false,
    val roleClause: String = "Full Time Active" // e.g. "Part Time", "Special Attraction"
)

@Entity(tableName = "injury_statuses")
data class InjuryStatus(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wrestlerId: Int,
    val injuryType: String, // e.g. "Torn ACL", "Concussion-level A"
    val severity: String,   // "Minor", "Major", "Severe"
    val weeksRemaining: Int,
    val rehabActivity: String = "Gym rehab workouts"
)

@Entity(tableName = "titles")
data class Title(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleName: String,
    val prestige: Int = 60, // 0 - 100
    val division: String    // "World Heavyweight", "Tag Team", "Midcard", "Women's"
)

@Entity(tableName = "title_holders")
data class TitleHolder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleId: Int,
    val titleName: String,
    val wrestlerId: Int,
    val wrestlerName: String,
    val wonWeek: Int = 1,
    val daysHeld: Int = 45,
    val successfulDefenses: Int = 2
)

@Entity(tableName = "team_memberships")
data class TeamMembership(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamName: String,
    val wrestler1Id: Int,
    val wrestler1Name: String,
    val wrestler2Id: Int,
    val wrestler2Name: String,
    val activeStatus: Boolean = true,
    val experience: Int = 45,
    val finisher: String = "Double Chokeslam"
)

@Entity(tableName = "stable_memberships")
data class StableMembership(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stableName: String,
    val leaderId: Int,
    val leaderName: String,
    val memberIdsRaw: String, // Comma separated IDs
    val memberNames: String,   // Comma separated names
    val activeStatus: Boolean = true,
    val influence: Int = 70
)

@Entity(tableName = "wrestler_relationships")
data class WrestlerRelationship(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wrestler1Id: Int,
    val wrestler1Name: String,
    val wrestler2Id: Int,
    val wrestler2Name: String,
    val relationType: String, // "Rival", "Ally", "Manager", "Trainer"
    val heatLevel: Int = 50   // 0 - 100
)

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1, // Single row state
    val companyName: String = "Apex Wrestling Engine",
    val cash: Double = 250000.0,
    val prestige: Int = 50,      // 0 - 100 (reputation of promotion)
    val fanbase: Int = 15000,
    val currentWeek: Int = 1,
    val currentShowName: String = "Apex Friday Night",
    val showStyle: String = "Mainstream", // "Mainstream", "Hardcore", "Classic", "Lucha"
    val scoutingLevel: Int = 1,
    val lastShowRating: Int = 0,
    val tvDeal: String = "USA_NETWORK", // "LOCAL_CABLE", "USA_NETWORK", "FAST_STREAM", "PRESTIGE_PREMIUM"
    val physicianLevel: Int = 1,        // 1 = Cadet, 2 = Dr. John Brooks, 3 = Sports Science Hub
    val scoutLevel: Int = 1,            // 1 = Freelance, 2 = Chief Recruiter, 3 = Global Network
    val creativeLevel: Int = 1,         // 1 = Basic Writers, 2 = Gold Booking Advisor, 3 = Legendary Booker
    val facilityGym: Int = 1,           // 1 = Standard Cardio, 2 = Gold Gyms, 3 = Apex Academy
    val facilityPyro: Int = 1,          // 1 = Standard Sparks, 2 = HD Lighting, 3 = Stadium Pyro
    val facilityInfirmary: Int = 1,     // 1 = First Aid, 2 = Treatment Labs, 3 = Hyperbaric Suites
    val identityBrand: String = "Classic", // "Mainstream", "Hardcore", "Classic", "Lucha"
    val completedObjMask: Int = 0       // bitmask of completed goals
)

@Entity(tableName = "draft_matches")
data class DraftMatch(
    @PrimaryKey val id: Int,   // Row number (1 to 4) representing card slots
    val worker1Id: Int = 0,
    val worker1Name: String = "",
    val worker2Id: Int = 0,
    val worker2Name: String = "",
    val worker3Id: Int = 0,
    val worker3Name: String = "",
    val worker4Id: Int = 0,
    val worker4Name: String = "",
    val segmentType: String = "Match", // "Match", "Promo", "Angle"
    val detailedSegmentType: String = "", // "Singles Match", "Tag Match", "Triple Threat", "Fatal Four-Way", "Title Match", "Promo", "Backstage Segment", "Contract Signing", "Interview", "Brawl", "Authority Segment", "Vignette"
    val matchStipulation: String = "Normal", // "Normal", "Steel Cage", "No DQ", "Ladder"
    val bookingWeight: Int = 10, // weight of booking duration (minutes)
    val winnerSelection: Int = 0, // 0 = Auto/Sim, 1 = Worker 1, 2 = Worker 2, 3 = Worker 3, 4 = Worker 4
    val durationMinutes: Int = 15,
    val linkedRivalryId: Int = 0,
    val linkedRivalryName: String = "",
    val isTitleMatch: Boolean = false
) {
    fun isBooked(): Boolean {
        return worker1Id > 0 || detailedSegmentType.isNotEmpty()
    }
}

@Entity(tableName = "inbox_messages")
data class InboxMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val sender: String,
    val body: String,
    val weekReceived: Int,
    val isRead: Boolean = false,
    val type: String = "NEWS",           // "NEWS", "OFFER", "COMPLAINT", "REPORT", "SPONSOR"
    val cashBonus: Double = 0.0,         // Optional finance reward
    val rosterInfluenceId: Int = 0,      // ID of wrestler affected (morale change etc)
    val influenceAction: String = "",    // "MORALE_UP", "MORALE_DOWN", "INJURY_HEAL", "NONE"
    val isHandled: Boolean = false       // True if action executed
)

@Entity(tableName = "show_reports")
data class ShowReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val week: Int,
    val showName: String,
    val overallRating: Int,
    val attendance: Int,
    val ticketRevenue: Double,
    val merchandiseRevenue: Double,
    val showExpense: Double,
    val profitLoss: Double,
    val matchLogsRaw: String          // Delimited or pipe-separated summary logs
) {
    fun getMatchLogs(): List<String> {
        return if (matchLogsRaw.isEmpty()) emptyList() else matchLogsRaw.split("||")
    }
}

@Entity(tableName = "company_league_stats")
data class CompanyLeagueStats(
    @PrimaryKey val companyId: Int, // 1 = Apex, 2 = GWG, 3 = GPW, 4 = TWA
    val companyName: String,
    val totalViewers: Long = 0,
    val totalShowsBooked: Int = 0,
    val averageShowQuality: Float = 0f,
    val ppvPerformanceRating: Float = 0f,
    val averageRosterMorale: Int = 85,
    val averageTitlePrestige: Int = 75,
    val recentRatingsTrend: String = "",
    val lastWeeklyViewers: Long = 0,
    val viewersWeeklyChange: Long = 0,
    val historicalRankings: String = ""
)

