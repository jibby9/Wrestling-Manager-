package com.example.domain

import com.example.data.GameState
import com.example.data.DraftMatch
import com.example.data.Wrestler
import kotlin.random.Random

data class MatchSimulationResult(
    val id: Int,
    val segmentRating: Int,
    val winnerId: Int,
    val winnerName: String,
    val loserId: Int,
    val loserName: String,
    val finishType: String,
    val crowdReaction: String,
    val isInjuryScare: Boolean,
    val finalInjuryWeeks: Int,
    val injuryMessage: String?,
    val storylineProgression: String,
    val viewerImpact: String,
    val logOutput: String, // Fully detailed bullet-pointed phase breakdown
    val winnerMoraleDelta: Int,
    val loserMoraleDelta: Int,
    val winnerMomentumDelta: Int,
    val loserMomentumDelta: Int,
    val staminaDeltas: Map<Int, Int> // worker ID to delta
)

object MatchSimulationEngine {

    // Style Compatibility Table
    private fun getStyleCompatibility(style1: String, style2: String): Pair<Int, String> {
        return when {
            (style1 == "Technician" && style2 == "High Flyer") || (style1 == "High Flyer" && style2 == "Technician") -> {
                6 to "Spectacular clash of engineering & high-risk aerial agility (+6 points Dynamic Chem)"
            }
            (style1 == "Showman" && style2 == "Brawler") || (style1 == "Brawler" && style2 == "Showman") -> {
                4 to "Perfect athletic charisma matched with stiff, hard-hitting slugfest physics (+4 points Hype Chem)"
            }
            (style1 == "Showman" && style2 == "Showman") -> {
                4 to "Fierce competition of top-tier showboating crowd dynamics (+4 points Entertainment Chem)"
            }
            (style1 == "Technician" && style2 == "Technician") -> {
                3 to "Purist showcase of complex technical submission exchanges (+3 points Technical Chem)"
            }
            else -> 1 to "Balanced styled pacing with competitive back-and-forth flow (+1 point Standard Chem)"
        }
    }

    // Determine deterministic finish type
    private fun getFinishStyle(stipulation: String, hasInterference: Boolean, rand: Random): String {
        return if (hasInterference) {
            if (stipulation == "Steel Cage") "Interference Door Escape" else "Distraction Roll-Up"
        } else {
            when (stipulation) {
                "Steel Cage" -> if (rand.nextBoolean()) "Cage Climb Escape" else "Pinfall on Canvas"
                "No DQ" -> if (rand.nextBoolean()) "Weapon-Assisted Pinfall" else "Pinfall"
                "Ladder" -> "Briefcase Unhooking Climax"
                else -> {
                    val roll = rand.nextInt(4)
                    when (roll) {
                        0 -> "Pinfall"
                        1 -> "Submission Lockout"
                        2 -> "Countout Victory"
                        3 -> "Referee Stoppage"
                        else -> "Pinfall"
                    }
                }
            }
        }
    }

    /**
     * Conducts a fully deterministic phase-by-phase match or segment simulation.
     * Seed is strictly calculated based on week, draft match id, and wrestler IDs to guarantee repeatability.
     */
    fun simulate(
        state: GameState,
        draft: DraftMatch,
        w1: Wrestler,
        w2: Wrestler?,
        w3: Wrestler?,
        w4: Wrestler?
    ): MatchSimulationResult {
        // 1. Build Deterministic Seed
        val w2Id = w2?.id ?: 0
        val w3Id = w3?.id ?: 0
        val w4Id = w4?.id ?: 0
        val seed = (state.currentWeek * 10000L) + (draft.id * 1000L) + (w1.id * 100L) + (w2Id * 10L) + (w3Id + w4Id)
        val rand = Random(seed)

        val activeStyleBias = state.showStyle // "Mainstream", "Hardcore", "Classic", "Lucha", "Technical" (we supported in PPV)
        val isPromoSegment = draft.segmentType != "Match"

        if (isPromoSegment) {
            // Handle Narrative Promo Segments (Promo, Brawl, Interview, Vignette, etc.)
            return simulatePromoSegment(state, draft, w1, w2, seed, rand)
        }

        // --- MATCH SIMULATION CORE ---
        val opp = w2 ?: Wrestler(id = -2, name = "Local Jobber", popularity = 30, inRingSkill = 30, micSkill = 20, stamina = 100, morale = 80, heelFace = "HEEL", salary = 0.0, isContracted = false, style = "Brawler")
        
        // 2. Fetch parameters
        val p1Pop = w1.popularity
        val p2Pop = opp.popularity
        val p1Skill = w1.inRingSkill
        val p2Skill = opp.inRingSkill
        val p1Char = w1.charisma
        val p2Char = opp.charisma
        val p1Stamina = w1.stamina
        val p2Stamina = opp.stamina

        val isTitle = draft.isTitleMatch
        val stipulation = draft.matchStipulation
        val linkedRivalry = draft.linkedRivalryName
        val hasRivalry = linkedRivalry.isNotEmpty()
        val rivalryHeat = if (hasRivalry) {
            // Read rivalry heat or simulate standard high-octane feud heat
            if (linkedRivalry.contains("John Steel")) 92 else if (linkedRivalry.contains("Serena Swift")) 81 else 64
        } else 0

        // 3. Performance ratings
        val w1StaminaMult = if (p1Stamina < 40) 0.72f else 1.0f
        val w2StaminaMult = if (p2Stamina < 40) 0.72f else 1.0f
        
        val workerA_Index = (p1Skill * 0.50f + p1Pop * 0.35f + p1Char * 0.15f) * w1StaminaMult
        val workerB_Index = (p2Skill * 0.50f + p2Pop * 0.35f + p2Char * 0.15f) * w2StaminaMult

        var baseRating = ((workerA_Index + workerB_Index) / 2.0f).toInt()

        // 4. Style Compatibility Rating Additions
        val (chemBonus, chemDetails) = getStyleCompatibility(w1.style, opp.style)
        baseRating += chemBonus

        // Heel/Face alignment chemistry
        val alignmentBonus = if (w1.heelFace != opp.heelFace) 7 else -4
        baseRating += alignmentBonus

        // 5. Company Style Bias application
        var styleBiasLogs = "Standard corporate style applied."
        when (activeStyleBias) {
            "Mainstream" -> {
                val averagePop = (p1Pop + p2Pop) / 2.0f
                if (averagePop > 70) {
                    baseRating += 8
                    styleBiasLogs = "Mainstream fans went ecstatic over spectacular superstar pop starpower (+8 rating)"
                } else if (averagePop < 45) {
                    baseRating -= 5
                    styleBiasLogs = "Mainstream crowd sat completely quiet for low popularity wrestlers (-5 rating)"
                }
            }
            "Hardcore" -> {
                if (stipulation == "Steel Cage" || stipulation == "No DQ" || stipulation == "Ladder") {
                    baseRating += 12
                    styleBiasLogs = "Extreme crowd went savage over brutal $stipulation matches (+12 rating)"
                } else {
                    baseRating -= 6
                    styleBiasLogs = "Extreme violence purists heckled standard normal ring-rules (-6 rating)"
                }
            }
            "Technical" -> {
                val averageSkill = (p1Skill + p2Skill) / 2.0f
                if (averageSkill > 72) {
                    baseRating += 10
                    styleBiasLogs = "Technical purists appreciated the pristine high-skill ground maneuvers (+10 rating)"
                } else {
                    baseRating -= 5
                    styleBiasLogs = "Pristine technical logic suffered due to sloppy worker ring-awareness (-5 rating)"
                }
            }
            "Lucha" -> {
                val flyCount = (if (w1.style == "High Flyer") 1 else 0) + (if (opp.style == "High Flyer") 1 else 0)
                if (flyCount >= 1) {
                    baseRating += 9
                    styleBiasLogs = "Fast-faced high flyer aerial acrobatics triggered grand lucha cheers (+9 rating)"
                } else {
                    baseRating -= 4
                    styleBiasLogs = "Timid ground speed clashed with traditional Lucha Libre quick pacing expectations (-4 rating)"
                }
            }
        }

        // 6. Feud/Rivalry & Title multipliers
        if (hasRivalry) {
            val feudBoost = (rivalryHeat / 12) + 4
            baseRating += feudBoost
        }
        if (isTitle) {
            baseRating += 9
        }

        // Add small deterministic variance (-3 to +3) based on week & draft matching
        val variance = (seed % 7).toInt() - 3
        baseRating += variance

        val finalRating = baseRating.coerceIn(15, 100)

        // 7. Determine Winner & Loser
        val winDecision = draft.winnerSelection
        val w1PerformanceSeed = workerA_Index + w1.momentum * 0.2f + (seed % 15)
        val w2PerformanceSeed = workerB_Index + opp.momentum * 0.2f + (seed % 9)

        val winnerIsW1 = when (winDecision) {
            1 -> true
            2 -> false
            else -> w1PerformanceSeed >= w2PerformanceSeed
        }

        val winner = if (winnerIsW1) w1 else opp
        val loser = if (winnerIsW1) opp else w1

        // 8. Interference calculations (determines distraction rollups)
        // High charisma heel or existing bitter feud increases chance of interference
        val heelInMatch = w1.heelFace == "HEEL" || opp.heelFace == "HEEL"
        val interferenceChance = if (hasRivalry) 35 else (if (heelInMatch) 15 else 5)
        val hasInterference = rand.nextInt(100) < interferenceChance
        val finishType = getFinishStyle(stipulation, hasInterference, rand)

        // 9. Crowd Reaction Determinators
        val avgPop = (p1Pop + p2Pop) / 2
        val crowdReaction = when {
            finalRating >= 85 -> "DEAFENING (Crowd chanting on their feet, ecstatic atmosphere!)"
            avgPop > 75 -> "ELECTRIC (Hot arena, exploding for top superstar starpower)"
            finalRating >= 70 -> "ENGAGED (Focused reaction with rhythmic rhythmic claps)"
            avgPop < 45 -> "LUKEWARM (Muted murmurs, low starpower translation)"
            else -> "INTERMITTENT (Chants flare during high impact spots)"
        }

        // 10. Injury and Injury Scare determinations (Ring skill & stamina prevent injuries)
        val avgSkill = (p1Skill + p2Skill) / 2
        val baseInjuryChance = when (stipulation) {
            "Steel Cage" -> 16
            "No DQ" -> 12
            "Ladder" -> 18
            else -> 6
        }
        val reducedInjuryChance = (baseInjuryChance - (avgSkill / 15)).coerceAtLeast(2)
        val isInjuryTriggered = rand.nextInt(100) < reducedInjuryChance
        val isInjuryScare = !isInjuryTriggered && (rand.nextInt(100) < 15) // scare warning
        
        var finalInjuryWeeks = 0
        var injuryMessage: String? = null
        if (isInjuryTriggered) {
            finalInjuryWeeks = rand.nextInt(3) + 2 // 2 to 4 weeks
            val injuredOne = if (rand.nextBoolean()) w1 else opp
            val bodyPart = listOf("Ankle", "Shoulder", "Knee ligament", "Neck whip-lash").random(rand)
            injuryMessage = "[CRITICAL] MEDICAL BREAKDOWN: ${injuredOne.name} suffered a severe $bodyPart hyperextension during an intense sequence in the $stipulation match! Sidelined for $finalInjuryWeeks weeks."
        } else if (isInjuryScare) {
            val scareOne = if (rand.nextBoolean()) w1 else opp
            injuryMessage = "⚠️ INJURY SCARE: ${scareOne.name} landed awkwardly on their neck following a high backdrop, but quickly waved off the medical crew to finish the bout!"
        }

        // 11. Morale / Momentum changes
        val winMoraleDelta = (if (isTitle) 8 else 4) + (if (hasRivalry) 2 else 0)
        val loseMoraleDelta = (if (isTitle) -5 else -3) + (if (hasRivalry) -2 else 0)

        val winMomentumDelta = (if (isTitle) 12 else 8) + (finalRating / 10)
        val loseMomentumDelta = (if (isTitle) -8 else -4) - (finalRating / 15)

        val staminaDrain = when (stipulation) {
            "Steel Cage" -> 19
            "No DQ" -> 16
            "Ladder" -> 22
            else -> 12
        }

        val staminaDeltas = buildMap {
            put(w1.id, -staminaDrain)
            put(opp.id, -staminaDrain)
            w3?.let { put(it.id, -10) }
            w4?.let { put(it.id, -10) }
        }

        // 12. Storyline progression logs
        val storylineProgression = if (hasRivalry) {
            "FEUD MULTIPLIER: The bitter score between ${w1.name} and ${opp.name} boiled to an extreme climax, increasing active feud heat index by +5 points!"
        } else {
            "NARRATIVE ESTABLISHED: This physical encounter has started lingering tension. Roster observers think a new rivalry could ignite here!"
        }

        // 13. Viewer impact translations
        val baseViewers = 22000 + (p1Pop * 120) + (p2Pop * 120)
        val ratingsGain = (finalRating * 0.08f) + (if (isTitle) 2.2f else 0f)
        val viewerImpact = "PROJECTED BROADCAST AUDIENCE REACH: ${String.format("%,d", baseViewers)} active TV live feeds tracked. Net ratings shift: +${String.format("%.2f", ratingsGain)} points."

        // 14. GENERATING DETAILED PHASE LOGS (DETOUR OR MULTILINE BREAKDOWN)
        val logsBuilder = StringBuilder()
        logsBuilder.append("📺 MATCH SLATE #${draft.id} (${stipulation.uppercase()} CHALLENGE) [RATING: $finalRating%]\n")
        logsBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        logsBuilder.append("⚡ CARD MATCHUP: ${w1.name} (${w1.style}) vs ${opp.name} (${opp.style})\n")
        if (isTitle) {
            logsBuilder.append("🏆 GOLDEN STAKES: Official Roster Championship Gold is defended on the canvas!\n")
        }
        logsBuilder.append("🔊 CROWD ATMOSPHERE: $crowdReaction\n")
        logsBuilder.append("🧬 CHEMISTRY NOTES: $chemDetails • $alignmentBonus Alignment adjustment\n")
        logsBuilder.append("📈 STYLING ALIGNMENT: $styleBiasLogs\n")
        logsBuilder.append("──────────────────────────────────────────────────\n")

        // Phase 1: Opening
        logsBuilder.append("🚀 1. OPENING EXCHANGE:\n")
        val openingLines = getOpeningLine(w1, opp, stipulation, rand)
        logsBuilder.append("   $openingLines\n\n")

        // Phase 2: Control Phase
        logsBuilder.append("⛓️ 2. GROUND CONTROL PHASE:\n")
        val controlLines = getControlLine(winner, loser, stipulation, rand)
        logsBuilder.append("   $controlLines\n\n")

        // Phase 3: Momentum Shift
        logsBuilder.append("🔄 3. DRAMATIC MOMENTUM SHIFT:\n")
        val shiftLines = getShiftLine(winner, loser, andFeud = hasRivalry, rand)
        logsBuilder.append("   $shiftLines\n\n")

        // Phase 4: Climax
        logsBuilder.append("💥 4. THE EXTREME CLIMAX:\n")
        val climaxLines = getClimaxLine(winner, loser, stipulation, isTitle, hasInterference, rand)
        logsBuilder.append("   $climaxLines\n\n")

        // Phase 5: Finish
        logsBuilder.append("🏁 5. FINISH SEQUENCE:\n")
        val finishLine = getFinishLine(winner, loser, finishType, hasInterference, rand)
        logsBuilder.append("   $finishLine\n")
        
        logsBuilder.append("──────────────────────────────────────────────────\n")
        logsBuilder.append("📊 PERFORMANCE METRIC IMPACTS:\n")
        logsBuilder.append("  • Winner (${winner.name}): Morale +$winMoraleDelta | Momentum +$winMomentumDelta | stamina -${staminaDrain}\n")
        logsBuilder.append("  • Loser (${loser.name}): Morale $loseMoraleDelta | Momentum $loseMomentumDelta | stamina -${staminaDrain}\n")
        logsBuilder.append("  • $storylineProgression\n")
        logsBuilder.append("  • $viewerImpact")

        if (injuryMessage != null) {
            logsBuilder.append("\n  • $injuryMessage")
        }

        return MatchSimulationResult(
            id = draft.id,
            segmentRating = finalRating,
            winnerId = winner.id,
            winnerName = winner.name,
            loserId = loser.id,
            loserName = loser.name,
            finishType = finishType,
            crowdReaction = crowdReaction,
            isInjuryScare = isInjuryScare,
            finalInjuryWeeks = finalInjuryWeeks,
            injuryMessage = injuryMessage,
            storylineProgression = storylineProgression,
            viewerImpact = viewerImpact,
            logOutput = logsBuilder.toString(),
            winnerMoraleDelta = winMoraleDelta,
            loserMoraleDelta = loseMoraleDelta,
            winnerMomentumDelta = winMomentumDelta,
            loserMomentumDelta = loseMomentumDelta,
            staminaDeltas = staminaDeltas
        )
    }

    private fun simulatePromoSegment(
        state: GameState,
        draft: DraftMatch,
        w1: Wrestler,
        w2: Wrestler?,
        seed: Long,
        rand: Random
    ): MatchSimulationResult {
        // Conduct narrative entertainment segments (Promo, Authority Segment, Vignette etc)
        val detailedType = draft.detailedSegmentType.ifEmpty { draft.segmentType }
        val p1Mic = w1.micSkill
        val p1Pop = w1.popularity
        val p1Char = w1.charisma

        val logsBuilder = StringBuilder()
        logsBuilder.append("🎭 NARRATIVE ENTERTAINMENT SLATE #${draft.id} ($detailedType)\n")
        logsBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        logsBuilder.append("⚡ FEATURE TALENT: ${w1.name}\n")
        
        var baseRating = 50
        var mainAction = ""
        var performanceLogs = ""

        val winMoraleDelta = 3
        val winMomentumDelta = 5
        val staminaDrain = 3

        when (detailedType) {
            "Promo" -> {
                baseRating = (p1Mic * 0.65f + p1Pop * 0.25f + p1Char * 0.10f).toInt()
                mainAction = "takes the center mic as the lights dim. They deliver a passionate callout addressing historical rosters, claiming complete dominance."
                performanceLogs = "Key Skill Focus: Mic Command (${p1Mic}/100) & Charisma Aura (${p1Char}/100)."
            }
            "Authority Segment" -> {
                baseRating = (p1Mic * 0.8f + 12).toInt()
                mainAction = "steps into the ring as General Manager. Refusing to tolerate roster chaos, they structure a major rule decree defining championship implications."
                performanceLogs = "Corporate Influence Focus: GM Mic Skills (${p1Mic}/100)."
            }
            "Vignette" -> {
                baseRating = (p1Pop * 0.82f + 15).toInt()
                mainAction = "is highlighted in an high-contrast cinematic broadcast trailer, portraying intense gym physical conditioning and dramatic ring training."
                performanceLogs = "No stamina drain incurred! Popularity boost configured."
            }
            "Interview" -> {
                val interviewerSkill = 65
                val talentResponse = w2?.popularity ?: 45
                baseRating = (p1Mic * 0.5f + talentResponse * 0.5f).toInt()
                mainAction = "is questioned by the backstage reporter. ${w2?.name ?: "Opponent"} interrupts the segment immediately, trading heavy verbal warnings."
                performanceLogs = "Key Dialogue Exchange: Interactive microphone flow."
            }
            "Brawl" -> {
                val targetName = w2?.name ?: "Bitter Opponent"
                baseRating = (p1Pop * 0.55f + (w2?.popularity ?: 45) * 0.45f).toInt()
                mainAction = "and $targetName collide violently near the entrance ramps! Referees and arena security scramble immediately to pull them apart as fans scream."
                performanceLogs = "Physical Confrontation: High Popularity influence."
            }
            "Contract Signing" -> {
                val targetName = w2?.name ?: "Challenger"
                baseRating = (p1Mic * 0.5f + (w2?.micSkill ?: 45) * 0.5f).toInt() + 4
                mainAction = "and $targetName sit opposite each other around the VIP stable table. After inking contracts, they overturn the table, needing physical restraint!"
                performanceLogs = "Pre-PPVs Drama: Dual verbal skill synergy ratings."
            }
            "Backstage Segment" -> {
                val targetName = w2?.name ?: "Stable Companion"
                baseRating = (p1Char * 0.7f + 25).toInt()
                mainAction = "meets with $targetName in the secure corporate gym room, whispering tactical strategies and aligning on backstage political moves."
                performanceLogs = "Narrative Seed: Deep stable alignment work."
            }
            else -> {
                baseRating = 50
                mainAction = "engages in a generic entertainment segment, keeping fans visual attention focused on the screen roster."
                performanceLogs = "Balanced broadcast rating."
            }
        }

        baseRating += (seed % 9).toInt() - 4
        val finalRating = baseRating.coerceIn(20, 100)

        // Write phases of Promo
        logsBuilder.append("📝 PHASE 1: INTRODUCTION & ENTRANCE\n")
        logsBuilder.append("   ${w1.name} steps into the view space with intense focal intensity, instantly commanding the attention of the live crowd.\n\n")

        logsBuilder.append("🗣️ PHASE 2: PRIMARY DIALOGUE & ACTION\n")
        logsBuilder.append("   $mainAction\n\n")

        logsBuilder.append("🌟 PHASE 3: AUDIENCE CLIMAX & REACTION\n")
        logsBuilder.append("   The segment reaches a peak as the visual titantron flashes. $performanceLogs Crew metrics estimate highly focused attention indices.\n")
        logsBuilder.append("──────────────────────────────────────────────────\n")
        
        val viewerCount = 18000 + (p1Pop * 110)
        val ratingsGain = finalRating * 0.05f
        logsBuilder.append("📊 NARRATIVE ENGAGEMENT OUTCOMES:\n")
        logsBuilder.append("  • Active Rating Score: $finalRating% audience approval\n")
        logsBuilder.append("  • Projected viewership: ${String.format("%,d", viewerCount)} active viewers\n")
        logsBuilder.append("  • Performer gains: ${w1.name} triggers Morale +$winMoraleDelta and Momentum +$winMomentumDelta")

        val staminaDeltas = buildMap {
            put(w1.id, -staminaDrain)
            w2?.let { put(it.id, -staminaDrain) }
        }

        return MatchSimulationResult(
            id = draft.id,
            segmentRating = finalRating,
            winnerId = w1.id,
            winnerName = w1.name,
            loserId = w2?.id ?: 0,
            loserName = w2?.name ?: "",
            finishType = "Narrative",
            crowdReaction = "Focused",
            isInjuryScare = false,
            finalInjuryWeeks = 0,
            injuryMessage = null,
            storylineProgression = "Promo establishes roster presence.",
            viewerImpact = "TV Ratings shift: +${String.format("%.2f", ratingsGain)} points.",
            logOutput = logsBuilder.toString(),
            winnerMoraleDelta = winMoraleDelta,
            loserMoraleDelta = 0,
            winnerMomentumDelta = winMomentumDelta,
            loserMomentumDelta = 0,
            staminaDeltas = staminaDeltas
        )
    }

    // --- PROCEDURAL PHASE TEXT GENERATORS ---

    private fun getOpeningLine(w1: Wrestler, w2: Wrestler, stip: String, rand: Random): String {
        return when {
            stip == "Steel Cage" -> "As the giant steel cage lock clicks shut, both wrestlers look up warily. They instantly charge, locking up in a brutal struggle against the cold metal mesh!"
            stip == "Ladder" -> "The golden briefcase is highlighted in spot beams overhead. Neither wrestler wastes time locking up—they trade quick, frantic punches, constantly glancing at the ladders set at the ramp."
            w1.style == "Technician" && w2.style == "High Flyer" -> "${w1.name} immediately targets ${w2.name}'s legs, looking to ground the high-flying speedster with clean mat transitions and standard wrestle locks."
            w1.style == "High Flyer" && w2.style == "High Flyer" -> "The referee sounds the bell and the pace immediately breaks land records! Speed vaults, mutual rope springboards, and matching athletic layouts bring the arena to an instant gasp!"
            w1.style == "Brawler" && w2.style == "Brawler" -> "There is no wrestling here! They meet dead center in a savage hockey-fight trade of stiff punches and forearms. The arena is instantly screaming!"
            else -> "The bell rings. They engage in a standard technical lockup, circling the canvas before trading deep wristlocks and collar-and-elbow transitions."
        }
    }

    private fun getControlLine(winner: Wrestler, loser: Wrestler, stip: String, rand: Random): String {
        return when {
            stip == "Steel Cage" -> "${winner.name} takes control, repeatedly grinding ${loser.name}'s forehead into the steel mesh. A dark bruise is already surfacing as ${loser.name} groans!"
            stip == "No DQ" -> "${winner.name} rolls out of the ring and retrieves a thick wooden kendo stick! They deliver multiple brutal cracks across ${loser.name}'s back, maintaining complete authority."
            winner.style == "Technician" -> "${winner.name} systematically targets the joints, locking in a punishing grapevined ankle lock, followed by multiple bridging german suplexes layout."
            winner.style == "Brawler" -> "${winner.name} unleashes a barrage of ruthless close-range headbutts, cornering ${loser.name} and drilling them with a towering running powerbomb!"
            winner.style == "High Flyer" -> "${winner.name} keeps the pressure exceptionally light and quick, landing multiple spring-board armdrags and an athletic running dropkick that sends ${loser.name} to the floor."
            else -> "${winner.name} controls the pace, implementing a deep neckbreaker and locking in a wear-down chinlock to deplete ${loser.name}'s stamina reserves."
        }
    }

    private fun getShiftLine(winner: Wrestler, loser: Wrestler, andFeud: Boolean, rand: Random): String {
        val fLine = if (andFeud) "This deeply personal bitter rivalry boils over! " else ""
        return when (loser.style) {
            "High Flyer" -> "${fLine}Avoiding a charging corner tackle, ${loser.name} executes a lightning-quick step-up enzuigiri! They launch a breathtaking springboard corkscrew moon-sault, turning the tides completely!"
            "Technician" -> "${fLine}Suddenly reversing a vertical suplex attempt, ${loser.name} smoothly rolls into a deep cross-armbar! ${winner.name} screams in agony, desperately scrambling to hook their boots on the ropes."
            "Brawler" -> "${fLine}Shrugging off a high impact kick, ${loser.name} lets out a primal roar. They connect with a devastating swinging lariat that nearly takes ${winner.name}'s head off!"
            else -> "${fLine}${loser.name} counters a signature hold with a sudden high-angle DDT! Both superstars lie motionless on the canvas as the referee administers a double count of five."
        }
    }

    private fun getClimaxLine(winner: Wrestler, loser: Wrestler, stip: String, isTitle: Boolean, helper: Boolean, rand: Random): String {
        val tLine = if (isTitle) "With the championship stakes weighing heavy, the desperation is intense! " else ""
        val hLine = if (helper) "Suddenly, a shadow appears on the entrance ramp—a faction partner runs down to cause an official distraction! " else ""
        
        return when {
            stip == "Ladder" -> "${tLine}${hLine}${winner.name} and ${loser.name} climb the tall metallic ladder together. They exchange savage punches at the very top. ${winner.name} grabs a steel rung and drives it into ${loser.name}'s face, sending them crashing all the way down through a preview table!"
            stip == "Steel Cage" -> "${tLine}${hLine}Both superstars are clawing near the top iron cage rail. ${winner.name} executes a jaw-dropping bulldog off the top rail, sending both bodies crashing down onto the canvas! The crowd is in sheer disbelief!"
            else -> "${tLine}${hLine}${loser.name} hits their signature move and goes for the cover! 1... 2... No! ${winner.name} manages to get a shoulder up at the absolute last microsecond! The crowd is in a state of absolute frenzy!"
        }
    }

    private fun getFinishLine(winner: Wrestler, loser: Wrestler, fType: String, helper: Boolean, rand: Random): String {
        val start = if (helper) "Leveraging the backstage helper distraction, " else ""
        return when (fType) {
            "Submission Lockout" -> "${start}${winner.name} locks in their signature technical submission. ${loser.name} struggles violently to reach the ropes, but is forced to tap out in the center of the ring! Winner: ${winner.name}!"
            "Countout Victory" -> "${winner.name} lands an explosive slide-kick, launching ${loser.name} over the guardrail. ${winner.name} rolls back inside as the official counts: 8... 9... 10! ${loser.name} is unable to beat the count!"
            "DQ Match Stop" -> "In a fit of wild rage, ${loser.name} strikes the referee! The official immediately sounds the bell, disqualifying ${loser.name}. Winner by DQ: ${winner.name}!"
            "Distraction Roll-Up", "Interference Door Escape" -> "With the referee completely distracted by ring interventions, ${winner.name} executes a low blow and instantly rolls up ${loser.name} with feet on the ropes! 1... 2... 3! A sneaky, underhanded victory!"
            "Briefcase Unhooking Climax" -> "With ${loser.name} completely incapacitated on the floor, ${winner.name} slowly climbs the ladder and unhooks the golden briefcase to score the spectacular victory!"
            "Cage Climb Escape" -> "${winner.name} kicks ${loser.name} away and climbs over the top cage iron bars, dropping down to touch both boots to the concrete floor just in time!"
            else -> "${start}${winner.name} executes their devastating finisher, the '${winner.finisher.ifEmpty { "Apex Slam" }}'! They hook both legs for the cover: 1... 2... 3! A clean and massive victory!"
        }
    }
}
