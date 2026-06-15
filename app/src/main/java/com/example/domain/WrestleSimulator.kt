package com.example.domain

import com.example.data.*
import java.util.Random
import kotlin.math.max
import kotlin.math.min

object WrestleSimulator {
    private val rand = Random()

    private val EXCITING_NAMES = listOf(
        "Dustin Fury", "Lance Blade", "Colt Bullet", "Zack Rebel", "Omega Kid",
        "Brad Beast", "Brock Hammer", "Sasha Strike", "Mulan Blade", "Gorgon"
    )

    private val TECH_NAMES = listOf("Zane Sterling", "Kurt Steel", "Dean Viper", "Professor Krauss")

    fun generateFreeAgent(week: Int): Wrestler {
        val style = listOf("Showman", "Brawler", "Technician", "High Flyer").random()
        val gender = if (rand.nextInt(4) == 0) "Female" else "Male"
        val name = if (style == "Technician") {
            TECH_NAMES.random() + " " + listOf("Jr.", "the Great", "Noble").random()
        } else {
            EXCITING_NAMES.random() + " " + listOf("Stone", "Storm", "Vader", "Flash").random()
        }

        val popularity = rand.nextInt(40) + 35
        val inRing = rand.nextInt(45) + 40
        val mic = rand.nextInt(50) + 35
        val stamina = 100
        val morale = 90
        val heelFace = if (rand.nextBoolean()) "FACE" else "HEEL"
        // Salary based on skills and popularity
        val salaryRate = (popularity * 50 + inRing * 30 + mic * 20) * (if (gender == "Female") 0.85 else 1.0)
        val salary = (salaryRate / 10).coerceIn(1200.0, 6000.0)

        return Wrestler(
            id = 0,
            name = name,
            popularity = popularity,
            inRingSkill = inRing,
            micSkill = mic,
            stamina = stamina,
            morale = morale,
            heelFace = heelFace,
            salary = Math.round(salary / 100.0) * 100.0,
            isContracted = false,
            style = style,
            gender = gender
        )
    }

    /**
     * Simulates a single show based on the drafted match card and current roster.
     */
    fun simulateShowCard(
        state: GameState,
        drafts: List<DraftMatch>,
        roster: List<Wrestler>
    ): Pair<ShowReport, List<Wrestler>> {
        val updatedRoster = roster.map { it.copy() }.toMutableList()
        val rosterMap = updatedRoster.associateBy { it.id }

        val matchLogs = mutableListOf<String>()
        var totalRatingAccumulator = 0
        var segmentsCounted = 0

        // Track who was booked this week
        val bookedWrestlerIds = mutableSetOf<Int>()

        fun updateWrestlerState(id: Int, staminaDelta: Int, moraleDelta: Int, popDelta: Int = 0, momentumDelta: Int = 0, injuryWeeksSetting: Int = 0) {
            if (id <= 0) return
            val idx = updatedRoster.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val currentW = updatedRoster[idx]
                val newSt = (currentW.stamina + staminaDelta).coerceIn(0, 100)
                val newMor = (currentW.morale + moraleDelta).coerceIn(0, 100)
                val newPop = (currentW.popularity + popDelta).coerceIn(10, 100)
                val newMom = (currentW.momentum + momentumDelta).coerceIn(10, 100)
                val newInj = if (injuryWeeksSetting > 0) injuryWeeksSetting else currentW.injuryWeeks
                updatedRoster[idx] = currentW.copy(
                    stamina = newSt,
                    morale = newMor,
                    popularity = newPop,
                    momentum = newMom,
                    injuryWeeks = newInj
                )
            }
        }

        for (draft in drafts) {
            if (!draft.isBooked()) continue

            segmentsCounted++
            
            // Build safe fallback performers to prevent any crashes on sparse setups
            val w1 = rosterMap[draft.worker1Id] ?: Wrestler(id = -1, name = draft.worker1Name.ifEmpty { "Local Competitor A" }, popularity = 40, inRingSkill = 45, micSkill = 45, stamina = 100, morale = 80, heelFace = "FACE", salary = 0.0, isContracted = false, style = "Brawler")
            val w2 = rosterMap[draft.worker2Id] ?: Wrestler(id = -2, name = draft.worker2Name.ifEmpty { "Local Competitor B" }, popularity = 40, inRingSkill = 45, micSkill = 45, stamina = 100, morale = 80, heelFace = "HEEL", salary = 0.0, isContracted = false, style = "Brawler")
            val w3 = rosterMap[draft.worker3Id] ?: Wrestler(id = -3, name = draft.worker3Name.ifEmpty { "Local Competitor C" }, popularity = 35, inRingSkill = 42, micSkill = 40, stamina = 100, morale = 80, heelFace = "FACE", salary = 0.0, isContracted = false, style = "Brawler")
            val w4 = rosterMap[draft.worker4Id] ?: Wrestler(id = -4, name = draft.worker4Name.ifEmpty { "Local Competitor D" }, popularity = 35, inRingSkill = 42, micSkill = 40, stamina = 100, morale = 80, heelFace = "HEEL", salary = 0.0, isContracted = false, style = "Brawler")

            if (w1.id > 0) bookedWrestlerIds.add(w1.id)
            if (w2.id > 0) bookedWrestlerIds.add(w2.id)
            if (w3.id > 0) bookedWrestlerIds.add(w3.id)
            if (w4.id > 0) bookedWrestlerIds.add(w4.id)

            val simResult = MatchSimulationEngine.simulate(
                state = state,
                draft = draft,
                w1 = w1,
                w2 = if (draft.worker2Id > 0) w2 else null,
                w3 = if (draft.worker3Id > 0) w3 else null,
                w4 = if (draft.worker4Id > 0) w4 else null
            )

            // Update wrestler stamina
            simResult.staminaDeltas.forEach { (wId, sDelta) ->
                updateWrestlerState(id = wId, staminaDelta = sDelta, moraleDelta = 0)
            }

            // Apply winner states
            if (simResult.winnerId > 0) {
                updateWrestlerState(
                    id = simResult.winnerId,
                    staminaDelta = 0,
                    moraleDelta = simResult.winnerMoraleDelta,
                    popDelta = if (simResult.winnerId == w1.id) 1 else 0,
                    momentumDelta = simResult.winnerMomentumDelta,
                    injuryWeeksSetting = if (simResult.finalInjuryWeeks > 0 && simResult.winnerId == w1.id) simResult.finalInjuryWeeks else 0
                )
            }

            // Apply loser states
            if (simResult.loserId > 0) {
                updateWrestlerState(
                    id = simResult.loserId,
                    staminaDelta = 0,
                    moraleDelta = simResult.loserMoraleDelta,
                    popDelta = 0,
                    momentumDelta = simResult.loserMomentumDelta,
                    injuryWeeksSetting = if (simResult.finalInjuryWeeks > 0 && simResult.loserId == w2.id) simResult.finalInjuryWeeks else 0
                )
            }

            totalRatingAccumulator += simResult.segmentRating
            matchLogs.add(simResult.logOutput)
        }

        // 7. Overall Show Rating with Pyro Facility bonuses
        val pyroShowRatingBonus = if (state.facilityPyro == 2) 3 else if (state.facilityPyro == 3) 8 else 0
        val finalShowRating = ((if (segmentsCounted == 0) 20 else totalRatingAccumulator / segmentsCounted) + pyroShowRatingBonus).coerceIn(15, 100)

        // 8. Financial Formulas (Football Manager dense feel)
        val avgCapacity = 2000 + (state.fanbase * 0.2).toInt()
        val seatPremiumMultiplier = (state.prestige * 0.4 + finalShowRating * 0.6) / 100.0
        val attendance = (avgCapacity * seatPremiumMultiplier + rand.nextInt(400) - 200).toInt().coerceAtLeast(400)

        val pyroPriceMultiplier = if (state.facilityPyro == 2) 1.10 else if (state.facilityPyro == 3) 1.30 else 1.0
        val ticketPrice = 25.0 * pyroPriceMultiplier
        val merchSpendingPerHead = 6.5 * pyroPriceMultiplier
        val ticketRevenue = attendance * ticketPrice
        val merchandiseRevenue = attendance * merchSpendingPerHead

        // Base arena rent + staff security cost
        val arenaRentExpense = 12000.0
        val productionStaffExpense = 8000.0
        val totalShowExpenses = arenaRentExpense + productionStaffExpense
        val profitLoss = (ticketRevenue + merchandiseRevenue) - totalShowExpenses

        // Deduct morale for workers NOT booked at all (FM Squad Rotation penalty)
        for (i in updatedRoster.indices) {
            val wrestler = updatedRoster[i]
            if (wrestler.isContracted && wrestler.injuryWeeks == 0 && !bookedWrestlerIds.contains(wrestler.id)) {
                // Lose 2 morale for stagnation, unless they are already down to 30
                if (wrestler.morale > 30) {
                    updatedRoster[i] = wrestler.copy(morale = max(wrestler.morale - 2, 0))
                }
            }
        }

        val report = ShowReport(
            id = 0,
            week = state.currentWeek,
            showName = state.currentShowName,
            overallRating = finalShowRating,
            attendance = attendance,
            ticketRevenue = ticketRevenue,
            merchandiseRevenue = merchandiseRevenue,
            showExpense = totalShowExpenses,
            profitLoss = profitLoss,
            matchLogsRaw = matchLogs.joinToString("||")
        )

        return Pair(report, updatedRoster)
    }

    /**
     * Conducts weekly progression changes (called when advancing to next week).
     */
    fun processWeeklyTransition(
        state: GameState,
        report: ShowReport,
        wrestlers: List<Wrestler>,
        payroll: Double,
        titleHolders: List<TitleHolder> = emptyList(),
        activeTitles: List<Title> = emptyList()
    ): Triple<GameState, List<Wrestler>, InboxMessage> {
        val rand = Random()

        // 1. Financial Update (TV Deal Income vs Staff Costs)
        val tvIncome = when (state.tvDeal) {
            "LOCAL_CABLE" -> 12000.0
            "USA_NETWORK" -> 120000.0
            "FAST_STREAM" -> 35000.0
            "PRESTIGE_PREMIUM" -> 280000.0
            else -> 120000.0
        }
        val physicianCost = when (state.physicianLevel) {
            2 -> 2500.0
            3 -> 8000.0
            else -> 0.0
        }
        val scoutCost = when (state.scoutLevel) {
            2 -> 3000.0
            3 -> 10000.0
            else -> 0.0
        }
        val creativeCost = when (state.creativeLevel) {
            2 -> 3500.0
            3 -> 12000.0
            else -> 0.0
        }
        val totalStaffCost = physicianCost + scoutCost + creativeCost
        val corporateNetProfit = report.profitLoss + tvIncome - totalStaffCost
        val newCash = state.cash + corporateNetProfit - payroll

        // 2. Prestige and Fanbase Adjustments
        // Shows with rating > 75 increase prestige/fanbase. Shows < 55 decrease them.
        var prestigeChange = 0
        var fanbaseChange = 0
        if (report.overallRating > 75) {
            prestigeChange = 2
            fanbaseChange = (2300 * (report.overallRating / 75.0f)).toInt()
        } else if (report.overallRating > 60) {
            prestigeChange = 0
            fanbaseChange = 600
        } else {
            prestigeChange = -2
            fanbaseChange = -1500
        }

        // Apply visual Pyro boosts to Fanbase
        val pyroMultiplier = if (state.facilityPyro == 2) 1.10f else if (state.facilityPyro == 3) 1.30f else 1.0f
        val finalFanbaseChange = (fanbaseChange * pyroMultiplier).toInt()

        val newPrestige = (state.prestige + prestigeChange).coerceIn(20, 100)
        val newFanbase = (state.fanbase + finalFanbaseChange).coerceAtLeast(3000)

        val nextShowSeriesName = when (state.currentWeek % 4) {
            0 -> "Slam-O-Mania PPV"
            1 -> "Friday Night Brawl"
            2 -> "Apex Showdown"
            else -> "Ultimate Cage Night"
        }

        val nextState = state.copy(
            cash = newCash,
            prestige = newPrestige,
            fanbase = newFanbase,
            currentWeek = state.currentWeek + 1,
            currentShowName = nextShowSeriesName,
            lastShowRating = report.overallRating
        )

        // 3. Roster stamina recoveries, injury countdowns, training, and Champion boosts
        val updatedRoster = wrestlers.map { wrestler ->
            var finalInjuryWeeks = wrestler.injuryWeeks
            var finalStamina = wrestler.stamina
            var finalPop = wrestler.popularity
            var finalInRing = wrestler.inRingSkill
            var finalMic = wrestler.micSkill
            var finalMorale = wrestler.morale

            // A. Medical recoveries
            if (wrestler.injuryWeeks > 0) {
                // Infirmary care speedup
                val speedyRecoverChance = if (state.facilityInfirmary == 2) 0.30f else if (state.facilityInfirmary == 3) 0.65f else 0.0f
                val weeksToReduce = if (rand.nextFloat() < speedyRecoverChance) 2 else 1
                finalInjuryWeeks = max(wrestler.injuryWeeks - weeksToReduce, 0)
                
                val physicianBonus = if (state.physicianLevel == 2) 5 else if (state.physicianLevel == 3) 12 else 0
                finalStamina = min(wrestler.stamina + 10 + physicianBonus, 100)
            } else {
                val recoveryBase = 20
                val rehabBonus = (if (state.physicianLevel == 2) 6 else if (state.physicianLevel == 3) 15 else 0) +
                                 (if (state.facilityGym == 2) 3 else if (state.facilityGym == 3) 8 else 0)
                finalStamina = min(wrestler.stamina + recoveryBase + rehabBonus, 100)
            }

            // B. Development Gym Training
            if (wrestler.isContracted && wrestler.companyId == 1 && wrestler.injuryWeeks == 0) {
                val trainChance = if (state.facilityGym == 2) 0.25f else if (state.facilityGym == 3) 0.50f else 0.05f
                if (rand.nextFloat() < trainChance) {
                    when (rand.nextInt(3)) {
                        0 -> {
                            finalInRing = min(wrestler.inRingSkill + 1, 100)
                        }
                        1 -> {
                            finalMic = min(wrestler.micSkill + 1, 100)
                        }
                        else -> {
                            finalPop = min(wrestler.popularity + 1, 100)
                        }
                    }
                }
            }

            // C. Champion boosts (status / prestige impact)
            val titlesHeldByWrestler = titleHolders.filter { it.wrestlerId == wrestler.id }
            if (titlesHeldByWrestler.isNotEmpty() && wrestler.isContracted && wrestler.companyId == 1) {
                val peakPrestige = titlesHeldByWrestler.maxOfOrNull { th ->
                    activeTitles.find { it.id == th.titleId }?.prestige ?: 60
                } ?: 60
                
                val pBoost = if (peakPrestige > 80) 2 else 1
                val mBoost = if (peakPrestige > 80) 4 else 2
                finalPop = min(finalPop + pBoost, 100)
                finalMorale = min(finalMorale + mBoost, 100)
            }

            // D. Creative staff morale booster
            if (wrestler.isContracted && wrestler.companyId == 1) {
                val creativeBoost = if (state.creativeLevel == 2) 1 else if (state.creativeLevel == 3) 3 else 0
                finalMorale = min(finalMorale + creativeBoost, 100)
            }

            wrestler.copy(
                injuryWeeks = finalInjuryWeeks,
                stamina = finalStamina,
                popularity = finalPop,
                inRingSkill = finalInRing,
                ringSkill = finalInRing,
                micSkill = finalMic,
                morale = finalMorale
            )
        }

        // 4. Procedurally generate weekly inbox message (Football Manager event)
        val emails = listOf(
            InboxMessage(
                subject = "Weekly Show rating analytics",
                sender = "Social Team",
                body = "Congratulations commissioner. Our latest show broadcast rated with ${report.overallRating} % overall feedback. The attendance peaked around ${report.attendance} loyal fans inside the arena.\n\nFinancial sheets record ticket revenues of \$${String.format("%,.2f", report.ticketRevenue)} and payroll costs of \$${String.format("%,.2f", payroll)}.\nLet's maintain this trajectory!",
                weekReceived = nextState.currentWeek,
                type = "NEWS"
            ),
            InboxMessage(
                subject = "Wrestler Morale Alert!",
                sender = "Backstage Liaison",
                body = "Commissioner, some wrestlers on our inactive roster are feeling frustrated due to a lack of booking. Ensure you rotate squad members periodically to keep roster morale high and avoid contract negotiation disputes.",
                weekReceived = nextState.currentWeek,
                type = "COMPLAINT"
            ),
            InboxMessage(
                subject = "Sponsorship Proposal",
                sender = "SlimJim Nutrition",
                body = "We are pleased with the company's progress and want to offer a micro-sponsorship deal! Activating this advertisement grants a \$15,000 cash bonus immediately.\n\nHowever, our partners demand a \$3,000 marketing commission next week.",
                weekReceived = nextState.currentWeek,
                cashBonus = 12000.0,
                type = "SPONSOR"
            ),
            InboxMessage(
                subject = "Doping Commission Notification",
                sender = "Athletic Board",
                body = "Our routine medical wellness reviews returned clean rosters. Professional levels are excellent, giving Apex Pro Wrestling a great reputation! Fans appreciate our sportsmanship.",
                weekReceived = nextState.currentWeek,
                type = "NEWS"
            ),
            InboxMessage(
                subject = "Creative Dispute",
                sender = "Head Writer",
                body = "Two wrestlers are disagreeing on are back-stage alignment goals. Having contrasting styles (Heels vs Faces) is great on the mic, but maintaining backstage harmony requires active intervention. Keep an eye on morale ratios!",
                weekReceived = nextState.currentWeek,
                type = "COMPLAINT"
            )
        )

        val selectedMail = if (nextState.currentWeek == 2) {
            emails[0] // Always show broadcast stat recap in week 2
        } else {
            emails.random()
        }

        return Triple(nextState, updatedRoster, selectedMail)
    }
}
