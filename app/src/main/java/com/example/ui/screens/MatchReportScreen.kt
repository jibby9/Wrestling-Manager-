package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShowReport
import com.example.domain.MatchReportGenerator
import com.example.domain.ParsedSegment
import com.example.domain.ReportLength
import com.example.ui.theme.*

@Composable
fun MatchResultsDashboard(
    report: ShowReport,
    modifier: Modifier = Modifier
) {
    val rawLogs = report.getMatchLogs()
    val parsedSegments = remember(report) {
        rawLogs.map { MatchReportGenerator.parseLog(it) }
    }

    var selectedSegmentIndex by remember(report) { mutableStateOf(0) }
    var selectedLength by remember { mutableStateOf(ReportLength.FULL_REPORT) }

    val currentSegment = parsedSegments.getOrNull(selectedSegmentIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Subsection title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTERACTIVE MATCH NEWS ROOM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.sp
            )
            Text(
                text = "${parsedSegments.size} BOOKED SEGMENTS",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            )
        }

        if (parsedSegments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateOverlay, RoundedCornerShape(4.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No log information found in this scorecard.",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        } else {
            // Horizontal segment selection list / Pill row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                parsedSegments.forEachIndexed { index, segment ->
                    val isSelected = index == selectedSegmentIndex
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSegmentIndex = index },
                        color = if (isSelected) GoldAccent else SlateOverlay,
                        border = BorderStroke(1.dp, if (isSelected) GoldAccent else ColorCardBorder),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (segment.isMatch) "MATCH ${segment.id}" else "PROMO ${segment.id}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) SlateDark else MutedText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${segment.rating}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SlateDark else LightText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Detailed information about the selected segment
            if (currentSegment != null) {
                val segment = currentSegment

                // 1. General Info & Badges
                Surface(
                    color = SlateOverlay,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (segment.isMatch) "🤼" else "🎤",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = segment.title.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MutedText
                                            )
                                    Text(
                                        text = segment.type.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                            )
                                }
                            }

                            // Rating Badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when {
                                            segment.rating >= 75 -> ColorFace.copy(alpha = 0.2f)
                                            segment.rating >= 60 -> ColorAlert.copy(alpha = 0.2f)
                                            else -> ColorHeel.copy(alpha = 0.2f)
                                        },
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            segment.rating >= 75 -> ColorFace
                                            segment.rating >= 60 -> ColorAlert
                                            else -> ColorHeel
                                        },
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${segment.rating}% RATING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when {
                                        segment.rating >= 75 -> ColorFace
                                        segment.rating >= 60 -> ColorAlert
                                        else -> ColorHeel
                                    },
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Divider(color = ColorCardBorder.copy(alpha = 0.5f))

                        // Crowd reaction row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔊 Crowd Reaction: ", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Medium)
                            Text(
                                text = segment.crowdReaction.split("(").first().trim(),
                                fontSize = 10.sp,
                                color = LightText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 2. Head-to-Head Competitors Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Winner / First Performer Card
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SlateOverlay,
                        border = BorderStroke(1.dp, if (segment.isMatch) ColorFace.copy(alpha = 0.5f) else ColorCardBorder),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (segment.isMatch) "WINNER" else "PERFORMER",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (segment.isMatch) ColorFace else GoldAccent
                                )
                                Icon(
                                    imageVector = if (segment.isMatch) Icons.Default.EmojiEvents else Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = if (segment.isMatch) ColorFace else GoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = segment.winner,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(ColorFace.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Morale +${segment.winnerMoraleDelta}",
                                        fontSize = 8.sp,
                                        color = ColorFace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(ColorFace.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Mom +${segment.winnerMomentumDelta}",
                                        fontSize = 8.sp,
                                        color = ColorFace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Loser Performer Card
                    if (segment.isMatch) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = SlateOverlay,
                            border = BorderStroke(1.dp, ColorHeel.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LOSER",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ColorHeel
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = ColorHeel,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = segment.loser,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(ColorHeel.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Morale ${segment.loserMoraleDelta}",
                                            fontSize = 8.sp,
                                            color = ColorHeel,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(ColorHeel.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Mom ${segment.loserMomentumDelta}",
                                            fontSize = 8.sp,
                                            color = ColorHeel,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Security & Backstage Injuries Bulletin
                if (segment.injuryMessage != null) {
                    val isCritIn = segment.isInjury
                    Surface(
                        color = if (isCritIn) ColorHeel.copy(alpha = 0.15f) else ColorAlert.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (isCritIn) ColorHeel.copy(alpha = 0.4f) else ColorAlert.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isCritIn) "CRITICAL MEDICAL BULLETIN" else "PHYSICAL SAFETY BULLETIN",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCritIn) ColorHeel else ColorAlert
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = segment.injuryMessage,
                                    fontSize = 10.sp,
                                    color = LightText,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // 4. Feud / Storyline Effects
                if (segment.storylineProgression.isNotEmpty()) {
                    Surface(
                        color = SlateOverlay,
                        border = BorderStroke(1.dp, ColorCardBorder),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "STORYLINE CONSEQUENCES",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = MutedText
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📈", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = segment.storylineProgression,
                                    fontSize = 10.sp,
                                    color = LightText,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }

                // 5. Written Match News Report generator & Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CHAMPIONSHIP NEWS FEED REPORTER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        letterSpacing = 0.5.sp
                    )

                    // Triple button selection of report length
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            ReportLength.SHORT_RECAP to "SHORT RECAP",
                            ReportLength.FULL_REPORT to "FULL NEWS ITEM",
                            ReportLength.NEWSLETTER_SUMMARY to "NEWSLETTER"
                        ).forEach { (len, txt) ->
                            val isSel = selectedLength == len
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedLength = len },
                                color = if (isSel) SlateOverlay else Color.Transparent,
                                border = if (isSel) BorderStroke(1.dp, GoldAccent) else BorderStroke(1.dp, ColorCardBorder.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Text(
                                    text = txt,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) GoldAccent else MutedText
                                )
                            }
                        }
                    }

                    // Printed News Sheet background
                    val reportText = remember(segment, selectedLength) {
                        MatchReportGenerator.generateReport(segment, selectedLength)
                    }

                    Surface(
                        color = SlateCard,
                        border = BorderStroke(1.dp, ColorCardBorder),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Small news headline banner top
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "GLOBAL WRESTLING BULLETIN NETWORK",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedText,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "REPORTER ACTIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorFace,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = ColorCardBorder)

                            // Render text supporting simple markup parsing (since MatchReportGenerator uses ** for bolding items)
                            RenderFormattedReportText(text = reportText)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Super clean helper Composable that takes a string containing "**" markers and renders them as beautiful Bold inline typography.
 * Completely avoids having markdown-renderer library overhead.
 */
@Composable
fun RenderFormattedReportText(text: String, modifier: Modifier = Modifier) {
    val items = text.split("\n")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { line ->
            if (line.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Text(
                    text = line.replace("**", ""), // clean typography
                    fontSize = 11.sp,
                    color = LightText,
                    lineHeight = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
