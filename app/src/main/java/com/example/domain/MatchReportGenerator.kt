package com.example.domain

import java.util.Locale

enum class ReportLength {
    SHORT_RECAP,
    FULL_REPORT,
    NEWSLETTER_SUMMARY
}

data class ParsedSegment(
    val id: Int = 1,
    val isMatch: Boolean = true,
    val title: String = "Match Segment",
    val type: String = "Singles Match",
    val rating: Int = 50,
    val worker1: String = "Worker A",
    val worker2: String = "Worker B",
    val winner: String = "Worker A",
    val loser: String = "Worker B",
    val finishType: String = "Pinfall",
    val crowdReaction: String = "ENGAGED",
    val chemistryNotes: String = "",
    val styleNotes: String = "",
    val isInjury: Boolean = false,
    val isInjuryScare: Boolean = false,
    val injuryMessage: String? = null,
    val openingExchange: String = "",
    val controlPhase: String = "",
    val momentumShift: String = "",
    val climax: String = "",
    val finishSequence: String = "",
    val winnerMoraleDelta: Int = 4,
    val loserMoraleDelta: Int = -3,
    val winnerMomentumDelta: Int = 8,
    val loserMomentumDelta: Int = -4,
    val storylineProgression: String = "The feud heats up.",
    val viewerImpact: String = "TV ratings shift: +1.5 points",
    val rawLog: String = ""
)

object MatchReportGenerator {

    fun parseLog(rawLog: String): ParsedSegment {
        // Fallback simple parsing for old logs or unrecognized format
        if (!rawLog.contains("📺 MATCH SLATE") && !rawLog.contains("🎭 NARRATIVE ENTERTAINMENT SLATE")) {
            return parseSimpleFallback(rawLog)
        }

        val lines = rawLog.lines().map { it.trim() }
        val isMatch = rawLog.contains("📺 MATCH SLATE")

        var id = 1
        var type = if (isMatch) "Singles Match" else "Promo"
        var rating = 50
        var worker1 = ""
        var worker2 = ""
        var winner = ""
        var loser = ""
        var finishType = "Pinfall"
        var crowdReaction = "ENGAGED"
        var chemistryNotes = ""
        var styleNotes = ""
        var isInjury = false
        var isInjuryScare = false
        var injuryMessage: String? = null
        var openingExchange = ""
        var controlPhase = ""
        var momentumShift = ""
        var climax = ""
        var finishSequence = ""
        var winnerMoraleDelta = 4
        var loserMoraleDelta = -3
        var winnerMomentumDelta = 8
        var loserMomentumDelta = -4
        var storylineProgression = ""
        var viewerImpact = ""

        // Extract ID and Type from first line
        val firstLine = lines.firstOrNull { it.startsWith("📺 MATCH SLATE") || it.startsWith("🎭 NARRATIVE ENTERTAINMENT SLATE") } ?: ""
        if (firstLine.isNotEmpty()) {
            val idRegex = "#(\\d+)".toRegex()
            id = idRegex.find(firstLine)?.groupValues?.get(1)?.toIntOrNull() ?: 1

            val typeRegex = "\\(([^)]+)\\)".toRegex()
            type = typeRegex.find(firstLine)?.groupValues?.get(1) ?: type

            val ratingRegex = "RATING:\\s*(\\d+)".toRegex()
            rating = ratingRegex.find(firstLine)?.groupValues?.get(1)?.toIntOrNull() ?: 50
        }

        // Matchup extraction
        val matchupLine = lines.firstOrNull { it.startsWith("⚡ CARD MATCHUP:") || it.startsWith("⚡ FEATURE TALENT:") } ?: ""
        if (matchupLine.isNotEmpty()) {
            if (matchupLine.contains("⚡ CARD MATCHUP:")) {
                val cleanMatch = matchupLine.replace("⚡ CARD MATCHUP:", "").trim()
                if (cleanMatch.contains(" vs ")) {
                    val parts = cleanMatch.split(" vs ")
                    worker1 = parts.getOrNull(0)?.split("(")?.getOrNull(0)?.trim() ?: ""
                    worker2 = parts.getOrNull(1)?.split("(")?.getOrNull(0)?.trim() ?: ""
                } else {
                    worker1 = cleanMatch
                }
            } else {
                worker1 = matchupLine.replace("⚡ FEATURE TALENT:", "").trim()
            }
        }

        // Crowd reaction
        val crowdLine = lines.firstOrNull { it.startsWith("🔊 CROWD ATMOSPHERE:") } ?: ""
        if (crowdLine.isNotEmpty()) {
            crowdReaction = crowdLine.replace("🔊 CROWD ATMOSPHERE:", "").trim()
        }

        // Chemistry and Style
        val chemLine = lines.firstOrNull { it.startsWith("🧬 CHEMISTRY NOTES:") } ?: ""
        if (chemLine.isNotEmpty()) {
            chemistryNotes = chemLine.replace("🧬 CHEMISTRY NOTES:", "").trim()
        }
        val styleLine = lines.firstOrNull { it.startsWith("📈 STYLING ALIGNMENT:") } ?: ""
        if (styleLine.isNotEmpty()) {
            styleNotes = styleLine.replace("📈 STYLING ALIGNMENT:", "").trim()
        }

        // Section collector helper
        fun collectSectionText(header: String): String {
            val idx = lines.indexOfFirst { it.startsWith(header) }
            if (idx == -1) return ""
            val contentLines = mutableListOf<String>()
            for (i in (idx + 1) until lines.size) {
                val line = lines[i]
                if (line.isEmpty()) continue
                if (line.startsWith("🚀") || line.startsWith("⛓️") || line.startsWith("🔄") || line.startsWith("💥") || line.startsWith("🏁") || line.startsWith("📝") || line.startsWith("🗣️") || line.startsWith("🌟") || line.startsWith("━━") || line.startsWith("──") || line.startsWith("📊")) {
                    break
                }
                contentLines.add(line)
            }
            return contentLines.joinToString(" ").replace("^\\s*\\*\\s*".toRegex(), "").replace("^\\s*-\\s*".toRegex(), "").trim()
        }

        openingExchange = collectSectionText("🚀 1. OPENING EXCHANGE:")
        if (openingExchange.isEmpty()) {
            openingExchange = collectSectionText("📝 PHASE 1: INTRODUCTION & ENTRANCE")
        }

        controlPhase = collectSectionText("⛓️ 2. GROUND CONTROL PHASE:")
        if (controlPhase.isEmpty()) {
            controlPhase = collectSectionText("🗣️ PHASE 2: PRIMARY DIALOGUE & ACTION")
        }

        momentumShift = collectSectionText("🔄 3. DRAMATIC MOMENTUM SHIFT:")
        if (momentumShift.isEmpty()) {
            momentumShift = collectSectionText("🌟 PHASE 3: AUDIENCE CLIMAX & REACTION")
        }

        climax = collectSectionText("💥 4. THE EXTREME CLIMAX:")
        finishSequence = collectSectionText("🏁 5. FINISH SEQUENCE:")

        // Performance and outcomes block
        lines.forEach { line ->
            if (line.contains("Winner (") || line.contains("Winner:")) {
                val winName = if (line.contains("Winner (")) line.substringAfter("Winner (").substringBefore(")") else line.substringAfter("Winner: ").trim()
                winner = winName.split(" ").firstOrNull() ?: winName
                val moraleVal = line.substringAfter("Morale ").substringBefore(" ").trim()
                winnerMoraleDelta = extractDelta(moraleVal, 4)
                val momentumVal = line.substringAfter("Momentum ").substringBefore(" ").trim()
                winnerMomentumDelta = extractDelta(momentumVal, 8)
            }
            if (line.contains("Loser (") || line.contains("Loser:")) {
                val loseName = if (line.contains("Loser (")) line.substringAfter("Loser (").substringBefore(")") else line.substringAfter("Loser: ").trim()
                loser = loseName.split(" ").firstOrNull() ?: loseName
                val moraleVal = line.substringAfter("Morale ").substringBefore(" ").trim()
                loserMoraleDelta = extractDelta(moraleVal, -3)
                val momentumVal = line.substringAfter("Momentum ").substringBefore(" ").trim()
                loserMomentumDelta = extractDelta(momentumVal, -4)
            }
            if (line.contains("• [CRITICAL] MEDICAL BREAKDOWN:")) {
                isInjury = true
                injuryMessage = line.substringAfter("• ").trim()
            }
            if (line.contains("⚠️ INJURY SCARE:")) {
                isInjuryScare = true
                injuryMessage = line.substringAfter("• ").trim()
                if (injuryMessage?.isEmpty() == true) {
                    injuryMessage = line.trim()
                }
            }
            if (line.contains("FEUD MULTIPLIER:") || line.contains("NARRATIVE ESTABLISHED:") || line.contains("Promo establishes")) {
                storylineProgression = line.substringAfter("• ").trim()
            }
            if (line.contains("PROJECTED BROADCAST AUDIENCE") || line.contains("TV Ratings shift:") || line.contains("Narrative establishes")) {
                viewerImpact = line.substringAfter("• ").trim()
            }
            if (line.contains("Winner:") && finishSequence.isNotEmpty() && isMatch) {
                val checkFin = lines.firstOrNull { it.startsWith("🏁 5. FINISH SEQUENCE:") } ?: ""
                finishType = if (rawLog.contains("Submission Lockout")) "Submission Lockout"
                             else if (rawLog.contains("Countout Victory")) "Countout Victory"
                             else if (rawLog.contains("DQ Match Stop")) "Disqualification"
                             else if (rawLog.contains("Distraction Roll-Up")) "Distraction Roll-Up"
                             else if (rawLog.contains("Briefcase Unhooking Climax")) "Ladders Escape"
                             else if (rawLog.contains("Cage Climb Escape")) "Cage Escape"
                             else "Pinfall"
            }
        }

        // Post-validation: If matching worker Names were parsed partially, restore full names
        if (worker1.isNotEmpty()) {
            if (winner.isNotEmpty() && worker1.startsWith(winner)) winner = worker1
            if (loser.isNotEmpty() && worker1.startsWith(loser)) loser = worker1
        }
        if (worker2.isNotEmpty()) {
            if (winner.isNotEmpty() && worker2.startsWith(winner)) winner = worker2
            if (loser.isNotEmpty() && worker2.startsWith(loser)) loser = worker2
        }

        if (winner.isEmpty()) {
            winner = worker1.ifEmpty { "Performer A" }
        }
        if (loser.isEmpty() && isMatch) {
            loser = worker2.ifEmpty { "Performer B" }
        }

        return ParsedSegment(
            id = id,
            isMatch = isMatch,
            title = if (isMatch) "Match Slot #$id" else "Promo Slot #$id",
            type = type,
            rating = rating,
            worker1 = worker1,
            worker2 = worker2,
            winner = winner,
            loser = loser,
            finishType = finishType,
            crowdReaction = crowdReaction,
            chemistryNotes = chemistryNotes,
            styleNotes = styleNotes,
            isInjury = isInjury,
            isInjuryScare = isInjuryScare,
            injuryMessage = injuryMessage,
            openingExchange = openingExchange,
            controlPhase = controlPhase,
            momentumShift = momentumShift,
            climax = climax,
            finishSequence = finishSequence,
            winnerMoraleDelta = winnerMoraleDelta,
            loserMoraleDelta = loserMoraleDelta,
            winnerMomentumDelta = winnerMomentumDelta,
            loserMomentumDelta = loserMomentumDelta,
            storylineProgression = storylineProgression,
            viewerImpact = viewerImpact,
            rawLog = rawLog
        )
    }

    private fun parseSimpleFallback(rawLog: String): ParsedSegment {
        // Simple line parser
        val lines = rawLog.lines().map { it.trim() }
        var id = 1
        var rating = 55
        var winner = "Worker"
        var loser = "Opponent"
        
        val firstLine = lines.firstOrNull() ?: ""
        if (firstLine.contains("defeated")) {
            winner = firstLine.substringBefore(" defeated").trim()
            loser = firstLine.substringAfter("defeated ").substringBefore(" via").trim()
            if (loser.contains(" ")) {
                loser = loser.split(" ").first()
            }
        }
        val ratingMatch = "Rating:\\s*(\\d+)".toRegex().find(rawLog)
        if (ratingMatch != null) {
            rating = ratingMatch.groupValues[1].toIntOrNull() ?: 55
        }

        return ParsedSegment(
            id = id,
            isMatch = !rawLog.contains("PROMO") && !rawLog.contains("SEGMENT"),
            winner = winner,
            loser = loser,
            rating = rating,
            rawLog = rawLog
        )
    }

    private fun extractDelta(txt: String, default: Int): Int {
        val cleaned = txt.replace("+", "").replace("-", "").trim()
        val num = cleaned.toIntOrNull() ?: return default
        return if (txt.contains("-")) -num else num
    }

    /**
     * Generates a deterministic match report styled like sports news based on parsed segment data.
     */
    fun generateReport(segment: ParsedSegment, length: ReportLength): String {
        val resultText = if (segment.isMatch) {
            "**OFFICIAL SUMMARY:** **${segment.winner}** defeated **${segment.loser}** in a highly contested **${segment.type}** under the **${segment.finishType} rules**, scoring a final performance rating of **${segment.rating}%**."
        } else {
            "**SEGMENT BROADCAST:** **${segment.winner}** delivered a featured physical or verbal **${segment.type}** address, drawing an overall approval rating of **${segment.rating}%**."
        }

        val crowdText = "The live crowd was recorded as **${segment.crowdReaction.split("(").first().trim()}**, indicating a deeply engaged atmosphere throughout."

        if (length == ReportLength.SHORT_RECAP) {
            val recText = StringBuilder()
            recText.append("$resultText\n\n")
            if (segment.isMatch) {
                recText.append("In a crucial turning point, ${segment.winner} swung the momentum back in their favor before finalizing the match with ${segment.finishType.lowercase()} pinfall or escape tactics. ")
                recText.append("Post-match measurements saw ${segment.winner} gaining +${segment.winnerMomentumDelta} momentum, whilst ${segment.loser} suffered a loss of ${segment.loserMomentumDelta} momentum.")
            } else {
                recText.append("The narrative spot has established ${segment.winner}'s ongoing roster presence, bringing them a positive momentum change of +${segment.winnerMomentumDelta} points.")
            }
            if (segment.injuryMessage != null) {
                recText.append("\n\n⚠️ **Sidelined Update:** ${segment.injuryMessage}")
            }
            return recText.toString()
        }

        if (length == ReportLength.NEWSLETTER_SUMMARY) {
            val nsText = StringBuilder()
            nsText.append("📰 **APEX WRESTLING CHRONICLE**: $resultText\n\n")
            nsText.append("$crowdText ")
            if (segment.isMatch) {
                nsText.append("Observers highlight the athletic exchanges in the opening slot where both fighters established rapid styles. ")
                nsText.append("The turning point was defined in the mid-rounds. Ultimately, **${segment.winner}** sealed the transaction, marking another key landmark on their path up the brand tier. ")
                nsText.append("The defeat causes a temporary setback of **${segment.loserMomentumDelta} points** for **${segment.loser}**, while the winner slides up by **${segment.winnerMomentumDelta} points**.")
            } else {
                nsText.append("Visual and narrative tracking indicates that **${segment.winner}** continues to build solid leverage amongst management. ")
                nsText.append("This booking raises their overall rating portfolio, preparing them for higher-tier matches.")
            }
            if (segment.injuryMessage != null) {
                nsText.append("\n\n🏥 **BACKSTAGE BULLETIN:** ${segment.injuryMessage}")
            }
            return nsText.toString()
        }

        // FULL REPORT
        val fullText = StringBuilder()
        fullText.append("📢 **OFFICIAL BRAND NEWS DECK**\n")
        fullText.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        fullText.append("$resultText\n\n")
        fullText.append("$crowdText\n\n")

        if (segment.isMatch) {
            fullText.append("⭐️ **KEY MATCH SEGMENTS & BREAKDOWN:**\n")
            fullText.append("  • **The Opening Exchange:** ${segment.openingExchange.ifEmpty { "Both superstars grappled for early tactical control of the ring ropes." }}\n")
            fullText.append("  • **The Control Phase:** ${segment.controlPhase.ifEmpty { "The action intensified as one wrestler worked the joints and wore down their opponent." }}\n")
            fullText.append("  • **Momentum Shift:** ${segment.momentumShift.ifEmpty { "A sudden counter or rope bounce completely leveled the playing field, causing a dramatic shift in pacing." }}\n")
            fullText.append("  • **The Climax:** ${segment.climax.ifEmpty { "High stakes maneuvers off the top turnbuckle sent adrenaline surging through the arena." }}\n")
            fullText.append("  • **The Finish:** ${segment.finishSequence.ifEmpty { "Using precise finishers, the match was locked up securely, to the sheer delight of the seated crowd." }}\n\n")

            fullText.append("📈 **CONSEQUENCES & BRAND STANDINGS:**\n")
            fullText.append("  • **Winners Portal:** **${segment.winner}** gains **+${segment.winnerMomentumDelta} Momentum** and **+${segment.winnerMoraleDelta} Morale**.\n")
            fullText.append("  • **Losers Portal:** **${segment.loser}** loses **${segment.loserMomentumDelta} Momentum** and **${segment.loserMoraleDelta} Morale**.\n")
            fullText.append("  • **Narrative Progression:** ${segment.storylineProgression}\n")
            fullText.append("  • **Viewer Coverage:** ${segment.viewerImpact}")
        } else {
            fullText.append("⭐️ **NARRATIVE ANATOMY & FEEDBACK:**\n")
            fullText.append("  • **Performance Segment Details:** ${segment.controlPhase}\n")
            fullText.append("  • **Audience Takeaway:** ${segment.momentumShift}\n\n")

            fullText.append("📈 **BRAND DATA MODULATION:**\n")
            fullText.append("  • **Featured Performer:** **${segment.winner}** gains **+${segment.winnerMomentumDelta} Momentum** and **+${segment.winnerMoraleDelta} Morale**.\n")
            fullText.append("  • **Viewer Statistics:** ${segment.viewerImpact}")
        }

        if (segment.injuryMessage != null) {
            fullText.append("\n\n🏥 **LOCKER ROOM MEDICAL BULLETIN:**\n")
            fullText.append("  ${segment.injuryMessage}")
        }

        return fullText.toString()
    }
}
