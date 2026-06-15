package com.example.domain

import com.example.data.CompanyLeagueStats
import com.example.data.GameState
import com.example.data.ShowReport
import com.example.data.Wrestler
import kotlin.math.roundToInt
import kotlin.random.Random

object LeagueEngine {

    // Predefined baseline parameters for the rival companies
    val BASES = mapOf(
        1 to CompanyBaseline("Apex Pro Wrestling", 65f, 68f, 85, 80, "Mainstream"),
        2 to CompanyBaseline("Giga Wrestling Global", 62f, 60f, 75, 85, "Chaos / Hardcore"),
        3 to CompanyBaseline("Global Pure Wrestling", 72f, 74f, 88, 70, "Pure / Athletic"),
        4 to CompanyBaseline("Total Wrestling Action", 60f, 65f, 80, 78, "High Flying / Lucha")
    )

    data class CompanyBaseline(
        val name: String,
        val baseQuality: Float,
        val basePpv: Float,
        val baseMorale: Int,
        val baseTitlePrestige: Int,
        val style: String
    )

    /**
     * Initializes default league statistics when starting a new game (or if stats are missing).
     */
    fun createDefaultStats(currentWeek: Int): List<CompanyLeagueStats> {
        return BASES.map { (companyId, base) ->
            CompanyLeagueStats(
                companyId = companyId,
                companyName = base.name,
                totalViewers = if (companyId == 1) 25000L else (24000L + Random.nextInt(3000)),
                totalShowsBooked = if (currentWeek > 1) currentWeek - 1 else 0,
                averageShowQuality = base.baseQuality,
                ppvPerformanceRating = base.basePpv,
                averageRosterMorale = base.baseMorale,
                averageTitlePrestige = base.baseTitlePrestige,
                recentRatingsTrend = "60,60,62",
                lastWeeklyViewers = 0,
                viewersWeeklyChange = 0,
                historicalRankings = "3,2,2"
            )
        }
    }

    /**
     * Simulates one week of action for ALL companies, including AI behavior.
     * Integrates the Player's actual ShowReport if provided, or simulates placeholder if needed.
     */
    fun simulateWeek(
        currentWeek: Int,
        playerReport: ShowReport?,
        currentStats: List<CompanyLeagueStats>,
        roster: List<Wrestler>
    ): List<CompanyLeagueStats> {
        val nextStatsList = mutableListOf<CompanyLeagueStats>()
        val isPpvWeek = currentWeek % 4 == 0

        // Find current stats or build defaults
        val statsMap = currentStats.associateBy { it.companyId }

        for (companyId in 1..4) {
            val prevStats = statsMap[companyId] ?: createDefaultStats(currentWeek).first { it.companyId == companyId }
            val base = BASES[companyId] ?: CompanyBaseline("Company $companyId", 60f, 60f, 80, 70, "Unknown")

            // 1. Quality and viewer calculation
            val showQuality: Float
            val viewersGained: Long

            if (companyId == 1 && playerReport != null) {
                // Actual player values
                showQuality = playerReport.overallRating.toFloat()
                // Convert attendance/rating to absolute weekly viewers
                viewersGained = (playerReport.attendance * 1.5 + playerReport.overallRating * 110 + Random.nextInt(1000)).toLong()
            } else {
                // Generate simulated AI show results
                val baseQuality = if (isPpvWeek) base.basePpv else base.baseQuality
                val qualityFluctuation = Random.nextFloat() * 14f - 7f // +/- 7 points
                showQuality = (baseQuality + qualityFluctuation).coerceIn(40f, 98f)

                // Viewers calculation based on show quality and company rank profile
                val multiplier = if (isPpvWeek) 1.8f else 1.0f
                val baseViewers = when(companyId) {
                    2 -> 4500L // GWG high default fanbase representation
                    3 -> 3500L // GPW high quality but smaller niche fanbase
                    4 -> 3900L // TWA medium Lucha fanbase
                    else -> 4000L
                }
                viewersGained = ((baseViewers + showQuality * 105) * multiplier + Random.nextInt(1200) - 600).toLong()
            }

            // 2. Incremental updates
            val newShowsCount = prevStats.totalShowsBooked + 1
            val newAvgQuality = ((prevStats.averageShowQuality * prevStats.totalShowsBooked) + showQuality) / newShowsCount

            val newPpvRating = if (isPpvWeek) {
                ((prevStats.ppvPerformanceRating * (prevStats.totalShowsBooked / 4)) + showQuality) / ((prevStats.totalShowsBooked / 4) + 1)
            } else {
                prevStats.ppvPerformanceRating
            }

            // Fluctuations in morale and prestige
            val baseMoraleChange = if (showQuality > 70f) 1 else if (showQuality < 55f) -2 else 0
            val newMorale = (prevStats.averageRosterMorale + baseMoraleChange + Random.nextInt(3) - 1).coerceIn(50, 100)

            val basePrestigeChange = if (showQuality > 75f) 2 else if (showQuality < 50f) -3 else 0
            val newPrestige = (prevStats.averageTitlePrestige + basePrestigeChange + Random.nextInt(3) - 1).coerceIn(40, 100)

            // Trend parsing
            val trendTokens = prevStats.recentRatingsTrend.split(",").toMutableList()
            if (trendTokens.size >= 5) trendTokens.removeAt(0)
            trendTokens.add(showQuality.roundToInt().toString())
            val newTrend = trendTokens.joinToString(",")

            // Add viewers
            val newCumViewers = prevStats.totalViewers + viewersGained

            nextStatsList.add(
                prevStats.copy(
                    totalViewers = newCumViewers,
                    totalShowsBooked = newShowsCount,
                    averageShowQuality = newAvgQuality,
                    ppvPerformanceRating = newPpvRating,
                    averageRosterMorale = newMorale,
                    averageTitlePrestige = newPrestige,
                    recentRatingsTrend = newTrend,
                    lastWeeklyViewers = viewersGained,
                    viewersWeeklyChange = viewersGained - prevStats.lastWeeklyViewers
                )
            )
        }

        // Calculate rankings for history tracking
        val sortedForHistory = nextStatsList.sortedWith(
            compareByDescending<CompanyLeagueStats> { it.totalViewers }
                .thenByDescending { it.averageShowQuality }
                .thenByDescending { it.averageRosterMorale }
        )

        val updatedWithRankHistory = sortedForHistory.mapIndexed { index, stats ->
            val curRank = index + 1
            val histTokens = stats.historicalRankings.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (histTokens.size >= 10) histTokens.removeAt(0)
            histTokens.add(curRank.toString())
            stats.copy(historicalRankings = histTokens.joinToString(","))
        }

        return updatedWithRankHistory
    }

    /**
     * Determines current season leaderboard rankings utilizing specified tie-breakers:
     * 1. Cumulative Viewers (Primary)
     * 2. Average Show Quality
     * 3. PPV Performance Rating
     * 4. Roster Morale
     * 5. Title Prestige
     */
    fun rankStandings(statsList: List<CompanyLeagueStats>): List<RankedCompany> {
        val sorted = statsList.sortedWith(
            compareByDescending<CompanyLeagueStats> { it.totalViewers }
                .thenByDescending { it.averageShowQuality }
                .thenByDescending { it.ppvPerformanceRating }
                .thenByDescending { it.averageRosterMorale }
                .thenByDescending { it.averageTitlePrestige }
        )

        return sorted.mapIndexed { index, stats ->
            val rank = index + 1
            val trendSymbols = stats.historicalRankings.split(",").filter { it.isNotEmpty() }
            val trendDirection = if (trendSymbols.size >= 2) {
                val last = trendSymbols.last().toIntOrNull() ?: rank
                val prev = trendSymbols[trendSymbols.size - 2].toIntOrNull() ?: rank
                when {
                    last < prev -> TrendDirection.RISING   // Rank number is lower, meaning higher position!
                    last > prev -> TrendDirection.FALLING
                    else -> TrendDirection.STEADY
                }
            } else {
                TrendDirection.STEADY
            }

            RankedCompany(
                rank = rank,
                stats = stats,
                trend = trendDirection,
                style = BASES[stats.companyId]?.style ?: "Varies"
            )
        }
    }

    /**
     * Predict season end awards based on simulated and player performance database stats.
     */
    fun generateSeasonAwards(
        statsList: List<CompanyLeagueStats>,
        roster: List<Wrestler>
    ): List<AwardRepresentation> {
        val ranked = rankStandings(statsList)
        val awards = mutableListOf<AwardRepresentation>()

        // 1. Company of the Year
        val bestCompany = ranked.firstOrNull()
        if (bestCompany != null) {
            awards.add(
                AwardRepresentation(
                    title = "ROSTER MASTER BRAND PRIZE",
                    winnerName = bestCompany.stats.companyName,
                    description = "Presented to the promotion achieving maximal viewer reach of ${String.format("%,d", bestCompany.stats.totalViewers)} total cumulative spectators.",
                    trophyIcon = "🏆"
                )
            )
        }

        // 2. Best Show Event of the Year
        val highestShowRatedCompany = statsList.maxByOrNull { it.averageShowQuality }
        if (highestShowRatedCompany != null) {
            val highShowQuality = highestShowRatedCompany.averageShowQuality.roundToInt()
            awards.add(
                AwardRepresentation(
                    title = "CRITICS CHOICE SHOW EVENT PLATINUM",
                    winnerName = highestShowRatedCompany.companyName,
                    description = "Honoring state-of-the-art match booking with a stellar average show production rating of $highShowQuality%.",
                    trophyIcon = "⭐"
                )
            )
        }

        // 3. Wrestler of the Year (Calculated from general roster of peak workers)
        val mvpWrestler = roster.filter { it.id > 0 }.maxByOrNull { it.popularity * 0.6f + it.inRingSkill * 0.4f }
        if (mvpWrestler != null) {
            val compName = when(mvpWrestler.companyId) {
                1 -> "Apex Pro Wrestling"
                2 -> "Giga Wrestling Global"
                3 -> "Global Pure Wrestling"
                4 -> "Total Wrestling Action"
                else -> "Free Agent Pool"
            }
            awards.add(
                AwardRepresentation(
                    title = "GLOBAL SENSATIONAL MVP GOLD",
                    winnerName = mvpWrestler.name,
                    description = "Acknowledging ${mvpWrestler.name} of '$compName' as the premier drawing card with ${mvpWrestler.popularity} reach index and ${mvpWrestler.finisher} finish dominance.",
                    trophyIcon = "🥇"
                )
            )
        } else {
            awards.add(
                AwardRepresentation(
                    title = "GLOBAL SENSATIONAL MVP GOLD",
                    winnerName = "John Steel",
                    description = "Earning praise as the premier drawing card of Apex, utilizing 'The Steel Hammer' to command global arenas.",
                    trophyIcon = "🥇"
                )
            )
        }

        // 4. Athletic Workhorse Cup
        val workerWrestler = roster.filter { it.id > 0 }.maxByOrNull { it.inRingSkill }
        if (workerWrestler != null) {
            awards.add(
                AwardRepresentation(
                    title = "PURE WORKHORSE ATHLETIC CUP",
                    winnerName = workerWrestler.name,
                    description = "Given to the roster's ultimate technical ring master for achieving a pristine in-ring execution skill rating of ${workerWrestler.inRingSkill}%.",
                    trophyIcon = "⚔️"
                )
            )
        }

        // 5. PPV King
        val bestPpv = statsList.maxByOrNull { it.ppvPerformanceRating }
        if (bestPpv != null) {
            awards.add(
                AwardRepresentation(
                    title = "PAY-PER-VIEW REVENUE MAGNITUDE PLUG",
                    winnerName = bestPpv.companyName,
                    description = "For superior monthly premium pay-per-view events resulting in a strong ${bestPpv.ppvPerformanceRating.roundToInt()}% PPV production rating standard.",
                    trophyIcon = "💎"
                )
            )
        }

        return awards
    }
}

enum class TrendDirection {
    RISING,
    FALLING,
    STEADY
}

data class RankedCompany(
    val rank: Int,
    val stats: CompanyLeagueStats,
    val trend: TrendDirection,
    val style: String
)

data class AwardRepresentation(
    val title: String,
    val winnerName: String,
    val description: String,
    val trophyIcon: String
)
