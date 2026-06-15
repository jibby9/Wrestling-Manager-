package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.domain.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel
import kotlin.math.*

sealed class Screen(val title: String, val icon: ImageVector, val subtabs: List<String>) {
    object Home : Screen(
        "Home",
        Icons.Default.Home,
        listOf("Inbox", "Dashboard", "Calendar", "News", "Alerts")
    )
    object Roster : Screen(
        "Roster",
        Icons.Default.Group,
        listOf("All Wrestlers", "Champions", "Tag Teams", "Stables", "Prospects", "Medical")
    )
    object Booking : Screen(
        "Booking",
        Icons.Default.SportsScore,
        listOf("Weekly Show", "PPV Planner", "Rivalries", "Match Card", "Segment Builder", "Results")
    )
    object League : Screen(
        "League",
        Icons.Default.Star,
        listOf("Standings", "Weekly Ratings", "Season Table", "Company Comparison", "Awards", "History")
    )
    object Transfers : Screen(
        "Transfers",
        Icons.Default.Storefront,
        listOf("Market", "Contracts", "Negotiations", "Shortlist", "Scouts", "Rumours")
    )
    object Club : Screen(
        "Club",
        Icons.Default.Settings,
        listOf("Finances", "TV Deals", "Titles", "Staff", "Facilities", "Brand Identity", "Objectives", "Database Management")
    )
}

@Composable
fun TopStatusBar(
    companyName: String,
    currentWeek: Int,
    cash: Double,
    morale: Int,
    viewers: String,
    unreadAlerts: Int,
    isTablet: Boolean
) {
    Surface(
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Main row with branding & financial summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Small logo container
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "W",
                            color = SlateDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = companyName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "MANAGER SIMULATION",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Financial & Calendar indicators
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%,.0f", cash)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GoldAccent
                    )
                    Text(
                        text = "WEEK $currentWeek | SEASON 1",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = ColorCardBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

            // Sub-metrics Row (Morale, Viewers, Alerts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Morale Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "MORALE: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Text(text = "$morale%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (morale >= 70) ColorFace else ColorAlert, fontFamily = FontFamily.Monospace)
                }

                // Season Viewers Indicator (TV Rating momentum)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👁️", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "VIEWERS: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Text(text = viewers, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LightText, fontFamily = FontFamily.Monospace)
                }

                // Alerts count indicator (Unread)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "ALERTS: ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Box(
                        modifier = Modifier
                            .background(if (unreadAlerts > 0) ColorHeel else SlateOverlay, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "$unreadAlerts NEW",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (unreadAlerts > 0) Color.White else MutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubTabsRow(
    tabs: List<String>,
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == activeTab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tab.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldAccent else MutedText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(2.dp)
                                .background(GoldAccent)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(2.dp)
                                .background(Color.Transparent)
                        )
                    }
                }
            }
        }
        Divider(color = ColorCardBorder, thickness = 1.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSimulationApp(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val rostersList by viewModel.wrestlers.collectAsStateWithLifecycle()
    val inboxMails by viewModel.inboxMessages.collectAsStateWithLifecycle()
    val draftSlots by viewModel.draftMatches.collectAsStateWithLifecycle()
    val showReports by viewModel.showReports.collectAsStateWithLifecycle()

    val eraSnapshots by viewModel.eraSnapshots.collectAsStateWithLifecycle()
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    val injuryStatuses by viewModel.injuryStatuses.collectAsStateWithLifecycle()
    val titles by viewModel.titles.collectAsStateWithLifecycle()
    val titleHolders by viewModel.titleHolders.collectAsStateWithLifecycle()
    val teamMemberships by viewModel.teamMemberships.collectAsStateWithLifecycle()
    val stableMemberships by viewModel.stableMemberships.collectAsStateWithLifecycle()
    val wrestlerRelationships by viewModel.wrestlerRelationships.collectAsStateWithLifecycle()
    val leagueStats by viewModel.leagueStats.collectAsStateWithLifecycle()
    
    val negotiations by viewModel.negotiations.collectAsStateWithLifecycle()
    val scoutAssignments by viewModel.scoutAssignments.collectAsStateWithLifecycle()
    val rumours by viewModel.rumours.collectAsStateWithLifecycle()


    var activeTab by remember { mutableStateOf<Screen>(Screen.Home) }
    val subTabStates = remember {
        mutableStateMapOf(
            "Home" to "Dashboard",
            "Roster" to "All Wrestlers",
            "Booking" to "Weekly Show",
            "League" to "Standings",
            "Transfers" to "Market",
            "Club" to "Finances"
        )
    }

    var selectedReportForLog by remember { mutableStateOf<ShowReport?>(null) }

    // Active detail selections (Dual-Pane Detail support)
    var selectedWrestlerForSheet by remember { mutableStateOf<Wrestler?>(null) }
    var matchSlotToBook by remember { mutableStateOf<DraftMatch?>(null) }

    // Dialog state for completed event broadcast
    var completedShowReport by remember { mutableStateOf<ShowReport?>(null) }

    val activeRoster = rostersList.filter { it.isContracted }
    val freeAgents = rostersList.filter { !it.isContracted }
    val unreadCount = inboxMails.count { !it.isRead }

    val avgMorale = if (activeRoster.isNotEmpty()) activeRoster.map { it.morale }.average().toInt() else 85
    val viewersString = "${String.format("%,.1f", (state?.fanbase ?: 15000) * 1.5 / 1000.0)}K"

    val activeSubTab = subTabStates[activeTab.title] ?: activeTab.subtabs.first()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(SlateDark)) {
        val isTablet = maxWidth > 680.dp

        Scaffold(
            topBar = {
                TopStatusBar(
                    companyName = state?.companyName ?: "Apex Wrestling Engine",
                    currentWeek = state?.currentWeek ?: 1,
                    cash = state?.cash ?: 250000.0,
                    morale = avgMorale,
                    viewers = viewersString,
                    unreadAlerts = unreadCount,
                    isTablet = isTablet
                )
            },
            bottomBar = {
                if (!isTablet) {
                    NavigationBar(
                        containerColor = SlateCard,
                        tonalElevation = 8.dp
                    ) {
                        listOf(Screen.Home, Screen.Roster, Screen.Booking, Screen.League, Screen.Transfers, Screen.Club).forEach { screen ->
                            val active = activeTab == screen
                            NavigationBarItem(
                                selected = active,
                                onClick = { activeTab = screen },
                                icon = {
                                    BadgedBox(badge = {
                                        if (screen == Screen.Home && unreadCount > 0) {
                                            Badge(containerColor = ColorHeel) {
                                                Text(text = "$unreadCount", color = Color.White, fontSize = 9.sp)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (active) GoldAccent else MutedText
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 8.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        color = if (active) LightText else MutedText
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = SlateOverlay
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tablet Navigation Rail
                if (isTablet) {
                    NavigationRail(
                        containerColor = SlateCard,
                        header = {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = "Logo",
                                tint = GoldAccent,
                                modifier = Modifier.padding(vertical = 16.dp).size(36.dp)
                            )
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        listOf(Screen.Home, Screen.Roster, Screen.Booking, Screen.League, Screen.Transfers, Screen.Club).forEach { screen ->
                            val active = activeTab == screen
                            NavigationRailItem(
                                selected = active,
                                onClick = { activeTab = screen },
                                icon = {
                                    BadgedBox(badge = {
                                        if (screen == Screen.Home && unreadCount > 0) {
                                            Badge(containerColor = ColorHeel) {
                                                Text(text = "$unreadCount", color = Color.White)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (active) GoldAccent else MutedText
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        color = if (active) LightText else MutedText,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    indicatorColor = SlateOverlay
                                )
                            )
                        }
                    }
                }

                // Primary Content Panel
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(SlateDark)
                ) {
                    SubTabsRow(
                        tabs = activeTab.subtabs,
                        activeTab = activeSubTab,
                        onTabSelected = { selectedSubTab ->
                            subTabStates[activeTab.title] = selectedSubTab
                        }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (activeTab) {
                            Screen.Home -> {
                                when (activeSubTab) {
                                    "Inbox" -> InboxTab(
                                        mails = inboxMails,
                                        onRead = { viewModel.markMessageRead(it) },
                                        onAction = { viewModel.handleInboxAction(it) },
                                        onDelete = { viewModel.deleteInboxMessage(it) }
                                    )
                                    "Dashboard" -> OfficeTab(
                                        state = state,
                                        rosterList = activeRoster,
                                        reports = showReports,
                                        mails = inboxMails,
                                        navigateToInbox = {
                                            activeTab = Screen.Home
                                            subTabStates["Home"] = "Inbox"
                                        },
                                        navigateToBooker = {
                                            activeTab = Screen.Booking
                                            subTabStates["Booking"] = "Weekly Show"
                                        },
                                        onSelectReportLog = { selectedReportForLog = it },
                                        isTablet = isTablet
                                    )
                                    "Calendar" -> CalendarSubTab()
                                    "News" -> NewsSubTab()
                                    "Alerts" -> AlertsSubTab(unreadCount = unreadCount)
                                }
                            }
                            Screen.Roster -> {
                                when (activeSubTab) {
                                    "All Wrestlers" -> RosterTab(
                                        roster = rostersList,
                                        titleHolders = titleHolders,
                                        onSelectWrestler = { selectedWrestlerForSheet = it },
                                        isTablet = isTablet,
                                        contracts = contracts,
                                        relationships = wrestlerRelationships,
                                        eraSnapshots = eraSnapshots,
                                        injuryStatuses = injuryStatuses,
                                        onHeelTurn = { w ->
                                            val updatedAlignment = if (w.heelFace == "FACE") "HEEL" else "FACE"
                                            viewModel.updateWrestler(w.copy(heelFace = updatedAlignment, morale = max(w.morale - 15, 20)))
                                        },
                                        onTrainMic = { w ->
                                            viewModel.updateWrestler(w.copy(micSkill = min(w.micSkill + 5, 100), morale = min(w.morale + 5, 100)))
                                        },
                                        onTrainStamina = { w ->
                                            viewModel.updateWrestler(w.copy(stamina = min(w.stamina + 20, 100)))
                                        },
                                        onRelease = { w ->
                                            viewModel.releaseWrestler(w)
                                        }
                                    )
                                    "Champions" -> ChampionsSubTab(
                                        titles = titles,
                                        holders = titleHolders,
                                        roster = rostersList
                                    )
                                    "Tag Teams" -> TagTeamsSubTab(teams = teamMemberships)
                                    "Stables" -> StablesSubTab(stables = stableMemberships)
                                    "Prospects" -> ProspectsSubTab(roster = rostersList)
                                    "Medical" -> MedicalSubTab(roster = rostersList, injuries = injuryStatuses)
                                }
                            }
                            Screen.Booking -> {
                                when (activeSubTab) {
                                    "Weekly Show" -> BookerTab(
                                        drafts = draftSlots,
                                        healthyRoster = activeRoster.filter { it.injuryWeeks == 0 },
                                        onEditSlot = { matchSlotToBook = it },
                                        onResetCard = { viewModel.clearDraftBoard() },
                                        onRunShow = {
                                            val success = viewModel.runWeeklyShow { report ->
                                                completedShowReport = report
                                            }
                                            if (!success) {
                                                // Empty card handling
                                            }
                                        }
                                    )
                                    "PPV Planner" -> PPVPlannerSubTab()
                                    "Rivalries" -> RivalriesSubTab()
                                    "Match Card" -> MatchCardSubTab(
                                        draftSlots = draftSlots,
                                        activeRoster = activeRoster,
                                        onSwapSlots = { a, b ->
                                            val valA = a.copy(
                                                id = b.id
                                            )
                                            val valB = b.copy(
                                                id = a.id
                                            )
                                            viewModel.updateDraftMatch(valA)
                                            viewModel.updateDraftMatch(valB)
                                        }
                                    )
                                    "Segment Builder" -> SegmentBuilderSubTab()
                                    "Results" -> ResultsSubTab(
                                        reports = showReports,
                                        onSelectReportLog = { selectedReportForLog = it },
                                        isTablet = isTablet
                                    )
                                }
                            }
                            Screen.League -> {
                                when (activeSubTab) {
                                    "Standings" -> StandingsSubTab(activeRoster, leagueStats, isTablet)
                                    "Weekly Ratings" -> WeeklyRatingsSubTab(showReports, leagueStats)
                                    "Season Table" -> SeasonTableSubTab(leagueStats)
                                    "Company Comparison" -> CompanyComparisonSubTab(leagueStats)
                                    "Awards" -> AwardsSubTab(leagueStats, rostersList)
                                    "History" -> HistorySubTab(leagueStats)
                                }
                            }
                            Screen.Transfers -> {
                                when (activeSubTab) {
                                    "Market" -> MarketTab(
                                        rostersList = rostersList,
                                        budget = state?.cash ?: 0.0,
                                        scoutAssignments = scoutAssignments,
                                        viewModel = viewModel,
                                        isTablet = isTablet
                                    )
                                    "Contracts" -> ContractsSubTab(
                                        roster = activeRoster,
                                        contracts = contracts,
                                        viewModel = viewModel
                                    )
                                    "Negotiations" -> NegotiationsSubTab(
                                        negotiations = negotiations,
                                        rostersList = rostersList,
                                        viewModel = viewModel
                                    )
                                    "Shortlist" -> ShortlistSubTab(
                                        rostersList = rostersList,
                                        viewModel = viewModel
                                    )
                                    "Scouts" -> ScoutsSubTab(
                                        scoutAssignments = scoutAssignments,
                                        rostersList = rostersList,
                                        viewModel = viewModel
                                    )
                                    "Rumours" -> RumoursSubTab(
                                        rumours = rumours
                                    )
                                }
                            }
                            Screen.Club -> {
                                when (activeSubTab) {
                                    "Finances" -> FinancesSubTab(activeRoster, state, showReports, viewModel)
                                    "TV Deals" -> TVDealsSubTab(state, viewModel)
                                    "Titles" -> TitlesSubTab(titles, titleHolders, activeRoster, viewModel)
                                    "Staff" -> StaffSubTab(state, viewModel)
                                    "Facilities" -> FacilitiesSubTab(state, viewModel)
                                    "Brand Identity" -> BrandIdentitySubTab(state, viewModel)
                                    "Objectives" -> ObjectivesSubTab(state, activeRoster, showReports, viewModel)
                                    "Database Management" -> DatabaseManagementSubTab(viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Wrestler Action Modal Sheets (Football Manager compact details)
    selectedWrestlerForSheet?.let { wrestler ->
        WrestlerDetailDialog(
            wrestler = wrestler,
            contracts = contracts,
            relationships = wrestlerRelationships,
            eraSnapshots = eraSnapshots,
            injuryStatuses = injuryStatuses,
            titleHolders = titleHolders,
            onDismiss = { selectedWrestlerForSheet = null },
            onHeelTurn = {
                val updatedAlignment = if (wrestler.heelFace == "FACE") "HEEL" else "FACE"
                viewModel.updateWrestler(wrestler.copy(heelFace = updatedAlignment, morale = max(wrestler.morale - 15, 20)))
                selectedWrestlerForSheet = null
            },
            onTrainMic = {
                viewModel.updateWrestler(wrestler.copy(micSkill = min(wrestler.micSkill + 5, 100), morale = min(wrestler.morale + 5, 100)))
                selectedWrestlerForSheet = null
            },
            onTrainStamina = {
                viewModel.updateWrestler(wrestler.copy(stamina = min(wrestler.stamina + 20, 100)))
                selectedWrestlerForSheet = null
            },
            onRelease = {
                viewModel.releaseWrestler(wrestler)
                selectedWrestlerForSheet = null
            }
        )
    }

    // Modal: Booking drafting dialogue sheet
    matchSlotToBook?.let { draft ->
        DraftBookingDialog(
            draft = draft,
            healthyRoster = rostersList.filter { it.isContracted && it.injuryWeeks == 0 },
            onDismiss = { matchSlotToBook = null },
            onConfirmDraft = { updatedDraft ->
                viewModel.updateDraftMatch(updatedDraft)
                matchSlotToBook = null
            }
        )
    }

    // Modal Dialog: Historic Event Report logger detail zoom
    selectedReportForLog?.let { report ->
        EventLogsDialog(report = report, onDismiss = { selectedReportForLog = null })
    }

    // Modal Dialog: Post show simulation feedback scorecard ticker
    completedShowReport?.let { report ->
        ShowResultScorecardDialog(report = report, onDismiss = { completedShowReport = null })
    }
}

// --- ADDITIONAL SUBTAB SCREENS ---

@Composable
fun CalendarSubTab() {
    val items = listOf(
        Triple("Week 1: Friday night Broadcast", "Regular Event", "Live Broadcast, standard arena operations"),
        Triple("Week 4: HEATWAVE PPV SPECIAL", "Pay-Per-View", "Highest hype factor, double television rights bonus"),
        Triple("Week 6: Mid-Season Talent Audit", "Logistics", "Contract renegotiations deadline"),
        Triple("Week 8: ULTIMATE CLIMAX MATCH", "Special Show", "Double prestige points, high fatigue risk"),
        Triple("Week 12: GOLDEN SHIELD GRAND SLAM", "Pay-Per-View Season Finale", "All roster titles defended, stadium capacity boost")
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "PROMOTION CALENDAR SCHEDULE", subTitle = "Upcoming events and critical deadlines")
        items.forEach { event ->
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = event.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Text(text = event.second.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Black, color = MutedText)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = event.third, fontSize = 11.sp, color = LightText)
                    }
                    Box(modifier = Modifier.background(SlateOverlay, RoundedCornerShape(3.dp)).padding(6.dp)) {
                        Text(text = "SCHEDULED", fontSize = 8.sp, color = ColorFace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsSubTab() {
    val bulletins = listOf(
        Triple("WRESTLETALK BLOG", "Apex Friday Night TV Ratings Spike!", "Dave Melts reports Apex TV viewership rose 8.4% last weekend due to improved ring performance indexes from top talent."),
        Triple("ROSTER REPORT", "Average Brand Alignment buoyancy", "Locker room insiders declare wrestlers align well with commissioner alignment rules, keeping morale positive."),
        Triple("INDUSTRY PULSE", "Global Pro Wrestling plans massive expansion", "Rumours suggest rival brand GPW is seeking scouting campaigns to sign elite Cruiserweight talent."),
        Triple("OFFICIAL ADVICE", "Wrestler fatigue warning during back-to-back matches", "Medical staff alerts commissioners: Keep stamina high or face elevated injury weeks penalties!")
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "SPORTS REPUTATION METRICS", subTitle = "Wrestling industry bulletins and media columns")
        bulletins.forEach { news ->
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = news.first, fontSize = 8.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                        Text(text = "LIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ColorFace)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = news.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = news.third, fontSize = 11.sp, color = MutedText, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
fun AlertsSubTab(unreadCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(title = "OPERATIONAL NOTIFICATIONS", subTitle = "Active administrative alerts")

        Surface(
            color = if (unreadCount > 0) ColorHeel.copy(alpha = 0.15f) else SlateCard,
            border = BorderStroke(1.dp, if (unreadCount > 0) ColorHeel else ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✉️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "PENDING MAIL ALERTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Text(text = "You have $unreadCount unread letters in the mailroom. Please read and process important sponsor bonuses or general corporate directives.", fontSize = 11.sp, color = MutedText)
                }
            }
        }

        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔋", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "ROSTER HEALTH & ATHLETIC LEVEL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Text(text = "Ensure wrestlers maintain Energy above 40% before scheduling matches. Fatigue highly inflates injury weeks probabilities.", fontSize = 11.sp, color = MutedText)
                }
            }
        }
    }
}

@Composable
fun ChampionsSubTab(
    titles: List<Title>,
    holders: List<TitleHolder>,
    roster: List<Wrestler>
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "OFFICIAL TITLEHOLDERS DISPLAY",
            subTitle = "Active championship belts tracked in simulation database"
        )
        
        titles.forEach { title ->
            val activeHolder = holders.find { it.titleId == title.id }
            
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).background(SlateOverlay, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title.titleName.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                        Text(
                            text = "CHAMPION: " + (activeHolder?.wrestlerName ?: "VACANT"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeHolder != null) LightText else MutedText
                        )
                        Text(
                            text = "Prestige: ${title.prestige}/100 | Division: ${title.division}",
                            fontSize = 10.sp,
                            color = MutedText
                        )
                    }
                    if (activeHolder != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Held: ${activeHolder.daysHeld} days", fontSize = 10.sp, color = LightText, fontWeight = FontWeight.Bold)
                            Text(text = "${activeHolder.successfulDefenses} Defenses", fontSize = 9.sp, color = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagTeamsSubTab(teams: List<TeamMembership>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "TAG TEAM DIVISION STRENGTH",
            subTitle = "Registered tag teams in league active roster strength"
        )
        
        if (teams.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No tag teams registered in simulation database.", fontSize = 11.sp, color = MutedText)
            }
        } else {
            teams.forEach { team ->
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = team.teamName.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                if (!team.activeStatus) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.background(ColorHeel.copy(alpha = 0.2f), RoundedCornerShape(2.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                        Text(text = "INACTIVE", fontSize = 8.sp, color = ColorHeel, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(text = "Partners: ${team.wrestler1Name} & ${team.wrestler2Name}", fontSize = 11.sp, color = LightText)
                            Text(text = "Cooperative Finisher: ${team.finisher}", fontSize = 10.sp, color = MutedText)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "${team.experience} EXP", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            Text(text = "Chemistry: 92%", fontSize = 9.sp, color = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StablesSubTab(stables: List<StableMembership>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "LOCKER-ROOM FACTION ALLIANCES",
            subTitle = "Stables and unified cliques influencing backstage locker room morale"
        )
        
        if (stables.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No stables registered in database.", fontSize = 11.sp, color = MutedText)
            }
        } else {
            stables.forEach { stable ->
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛡️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stable.stableName.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text(text = "Faction Leader: ${stable.leaderName}", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Influence: ${stable.influence}%", fontSize = 11.sp, color = LightText, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = ColorCardBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Active Members: ${stable.memberNames}", fontSize = 11.sp, color = LightText)
                    }
                }
            }
        }
    }
}

@Composable
fun ProspectsSubTab(roster: List<Wrestler>) {
    val prospects = roster.filter { it.activeStatus == "Prospect" || it.age < 24 }
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "UPCOMING ACADEMY PROSPECTS",
            subTitle = "Rookie classes scouted for potential contract recruitment drafts"
        )
        
        if (prospects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No prospects under 24 scouted on database.", fontSize = 11.sp, color = MutedText)
            }
        } else {
            prospects.forEach { prospect ->
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(text = prospect.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text(text = "Style: ${prospect.style} (Age: ${prospect.age})", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.background(SlateOverlay).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(
                                    text = if (prospect.isContracted) "PLAYER SQUAD" else "FREE AGENT",
                                    fontSize = 8.sp,
                                    color = LightText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Ring Skill: ${prospect.inRingSkill}/100", fontSize = 10.sp, color = MutedText)
                                Text(text = "Mic Skill: ${prospect.micSkill}/100", fontSize = 10.sp, color = MutedText)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Popularity base: ${prospect.popularity}/100", fontSize = 10.sp, color = MutedText)
                                Text(text = "Morale baseline: ${prospect.morale}%", fontSize = 10.sp, color = MutedText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalSubTab(
    roster: List<Wrestler>,
    injuries: List<InjuryStatus>
) {
    val injured = roster.filter { it.injuryWeeks > 0 }
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "INFIRMARY MEDICAL REHABILITATION",
            subTitle = "Rehab timelines and severity logs for performers under surveillance"
        )
        
        if (injured.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All contracted personnel fully healthy and active on the booking board.",
                    fontSize = 11.sp,
                    color = ColorFace,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            injured.forEach { wrestler ->
                val exactInjury = injuries.find { it.wrestlerId == wrestler.id }
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = wrestler.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonRed)
                            Text(
                                text = "Injury Diagnosis: " + (exactInjury?.injuryType ?: "Deep muscle strain"),
                                fontSize = 11.sp,
                                color = LightText
                            )
                            Text(
                                text = "Weeks Remaining: ${wrestler.injuryWeeks} weeks (Severity: ${exactInjury?.severity ?: "Minor"})",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                            Text(
                                text = "Active therapy action: ${exactInjury?.rehabActivity ?: "Resting and light thermal massage"}",
                                fontSize = 10.sp,
                                color = GoldAccent
                            )
                        }
                        Box(modifier = Modifier.background(ColorHeel.copy(alpha = 0.2f), RoundedCornerShape(2.dp)).padding(6.dp)) {
                            Text(text = "INJURED", fontSize = 8.sp, color = ColorHeel, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PPVPlannerSubTab() {
    var ppvName by remember { mutableStateOf("Apex Climax: Summer Showdown") }
    var ppvTheme by remember { mutableStateOf("Mainstream") } // "Mainstream", "Hardcore", "Technical Showcase", "Lucha Libre"
    var ticketPrice by remember { mutableStateOf(50f) }
    var hasSponsorMonster by remember { mutableStateOf(true) }
    var hasSponsorTV by remember { mutableStateOf(false) }
    var hasSponsorStreaming by remember { mutableStateOf(true) }
    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StyledSectionHeader(
            title = "PAY-PER-VIEW MONTHLY PLANNER",
            subTitle = "Configure ultra-prestige corporate broadcast spectacles"
        )

        // 1. PPV Branding Card
        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "EVENT IDENTITY & THEME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                
                OutlinedTextField(
                    value = ppvName,
                    onValueChange = { ppvName = it },
                    label = { Text("PPV Event Name", fontSize = 11.sp, color = MutedText) },
                    textStyle = TextStyle(color = LightText, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ColorCardBorder,
                        focusedContainerColor = SlateOverlay,
                        unfocusedContainerColor = SlateOverlay
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "THEME SPECTACLE STYLE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Mainstream", "Hardcore", "Technical", "Lucha Libre").forEach { theme ->
                        val selected = ppvTheme == theme
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { ppvTheme = theme },
                            color = if (selected) GoldAccent else SlateOverlay,
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(1.dp, if (selected) GoldAccent else ColorCardBorder)
                        ) {
                            Text(
                                text = theme.uppercase(),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) SlateDark else LightText
                            )
                        }
                    }
                }
            }
        }

        // 2. Pricing & Financial Projection
        val attendanceEst = 4200
        val ticketRev = attendanceEst * ticketPrice
        val merchRev = attendanceEst * 12.5f
        val sponsorBonus = (if (hasSponsorMonster) 5000 else 0) + (if (hasSponsorTV) 8000 else 0) + (if (hasSponsorStreaming) 12000 else 0)
        val grossEst = ticketRev + merchRev + sponsorBonus

        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "TICKET PRICING & PROJECTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Ticket Seat Pricing: $${ticketPrice.toInt()}", fontSize = 12.sp, color = LightText)
                    Text(text = "Range: $30 - $150", fontSize = 10.sp, color = MutedText)
                }

                Slider(
                    value = ticketPrice,
                    onValueChange = { ticketPrice = it },
                    valueRange = 30f..150f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = GoldAccent,
                        inactiveTrackColor = SlateOverlay
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = ColorCardBorder, thickness = 1.dp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ESTIMATED ATTENDANCE", fontSize = 8.sp, color = MutedText)
                        Text("${attendanceEst} Seats", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ESTIMATED GATE REVENUE", fontSize = 8.sp, color = MutedText)
                        Text("$${String.format("%,.0f", ticketRev)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TOTAL GROSS REVENUE", fontSize = 8.sp, color = MutedText)
                        Text("$${String.format("%,.0f", grossEst)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorFace)
                    }
                }
            }
        }

        // 3. Sponsors
        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "COMMERCIAL BROADCAST SPONSORS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Energy Drink Endorsement", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Text("Adds +$5,000 cash bonus payout", fontSize = 9.sp, color = MutedText)
                    }
                    Switch(
                        checked = hasSponsorMonster,
                        onCheckedChange = { hasSponsorMonster = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = GoldAccent.copy(alpha = 0.5f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Local Cable TV Advertising Boost", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Text("Boosts ratings reach by +15%", fontSize = 9.sp, color = MutedText)
                    }
                    Switch(
                        checked = hasSponsorTV,
                        onCheckedChange = { hasSponsorTV = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = GoldAccent.copy(alpha = 0.5f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Global Premium Streaming Partner", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Text("Adds +$12,000 cash bonus payout & prestige +3", fontSize = 9.sp, color = MutedText)
                    }
                    Switch(
                        checked = hasSponsorStreaming,
                        onCheckedChange = { hasSponsorStreaming = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = GoldAccent.copy(alpha = 0.5f))
                    )
                }
            }
        }

        Button(
            onClick = { showMessage = true },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("LOCK IN PPV CONFIGURATION PLAN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        if (showMessage) {
            Surface(
                color = SlateOverlay,
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✅", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PPV configurations successfully submitted to the board. Rules will apply to Week 4 ${ppvName.uppercase()}!",
                        fontSize = 11.sp,
                        color = ColorFace
                    )
                }
            }
        }
    }
}

data class LocalRivalry(
    val id: Int,
    val challenger: String,
    val champion: String,
    val heat: Int,
    val theme: String,
    val durationWeeks: Int
)

@Composable
fun RivalriesSubTab() {
    val rivalries = remember {
        mutableStateListOf(
            LocalRivalry(1, "John Steel", "Titan Maverick", 92, "Bitter Retribution feud", 3),
            LocalRivalry(2, "Serena Swift", "Kage Vance", 81, "Championship Ascent", 1),
            LocalRivalry(3, "El Dragone", "Rex Gung", 64, "High Flyer vs Brawler style conflict", 2)
        )
    }

    var selectedWA by remember { mutableStateOf("Alex Breeze") }
    var selectedWB by remember { mutableStateOf("Titan Maverick") }
    var inputTheme by remember { mutableStateOf("Mentor vs Protege Story") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StyledSectionHeader(
            title = "ACTIVE ROSTER FEUDS (RIVALRIES)",
            subTitle = "Generate audience narrative engagement and segment performance multipliers"
        )

        // Active Feuds list
        Text(text = "CURRENT ROSTER CONFLICTS (${rivalries.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)

        rivalries.forEach { rival ->
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${rival.challenger} vs ${rival.champion}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )
                        }
                        
                        Text(
                            text = "HEAT: ${rival.heat}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (rival.heat > 85) ColorHeel else ColorFace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${rival.theme} • Active Week ${rival.durationWeeks}",
                            fontSize = 10.sp,
                            color = MutedText
                        )

                        Text(
                            text = "RESOLVE CONFLICT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed,
                            modifier = Modifier.clickable {
                                rivalries.remove(rival)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Divider(color = ColorCardBorder)

        // Create a new Duel
        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "LAUNCH NEW ROSTER NARRATIVE FEUD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SUPERSTAR A", fontSize = 9.sp, color = MutedText)
                        val names = listOf("Alex Breeze", "El Dragone", "John Steel", "Titan Maverick", "Kage Vance", "Rex Gung", "Serena Swift")
                        Row {
                            Text(
                                text = selectedWA,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateOverlay)
                                    .padding(8.dp)
                                    .clickable {
                                        val idx = names.indexOf(selectedWA)
                                        selectedWA = names[(idx + 1) % names.size]
                                    }
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("SUPERSTAR B", fontSize = 9.sp, color = MutedText)
                        val names = listOf("Titan Maverick", "Rex Gung", "Kage Vance", "John Steel", "Serena Swift", "Alex Breeze", "El Dragone")
                        Row {
                            Text(
                                text = selectedWB,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateOverlay)
                                    .padding(8.dp)
                                    .clickable {
                                        val idx = names.indexOf(selectedWB)
                                        selectedWB = names[(idx + 1) % names.size]
                                    }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inputTheme,
                    onValueChange = { inputTheme = it },
                    label = { Text("Feud Theme / Core Dispute", fontSize = 10.sp) },
                    textStyle = TextStyle(color = LightText, fontSize = 12.sp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ColorCardBorder
                    )
                )

                if (errorMsg.isNotEmpty()) {
                    Text(text = errorMsg, fontSize = 9.sp, color = NeonRed)
                }

                Button(
                    onClick = {
                        if (selectedWA == selectedWB) {
                            errorMsg = "Wrestler A cannot be identical to Wrestler B!"
                        } else {
                            errorMsg = ""
                            rivalries.add(
                                LocalRivalry(
                                    id = (rivalries.maxOfOrNull { it.id } ?: 0) + 1,
                                    challenger = selectedWA,
                                    champion = selectedWB,
                                    heat = 55 + (0..35).random(),
                                    theme = inputTheme,
                                    durationWeeks = 1
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("IGNITE FEUD RIVALRY (ESTABLISH MULTIPLIERS)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchCardSubTab(
    draftSlots: List<DraftMatch>,
    activeRoster: List<Wrestler>,
    onSwapSlots: (DraftMatch, DraftMatch) -> Unit
) {
    val bookedCount = draftSlots.count { it.isBooked() }
    val totalTime = draftSlots.sumOf { it.durationMinutes }

    // Computes all metrics & warnings in modern production details
    val repeatList = remember(draftSlots) {
        val countMap = mutableMapOf<Int, Int>()
        draftSlots.forEach { draft ->
            if (draft.worker1Id > 0) countMap[draft.worker1Id] = (countMap[draft.worker1Id] ?: 0) + 1
            if (draft.worker2Id > 0) countMap[draft.worker2Id] = (countMap[draft.worker2Id] ?: 0) + 1
            if (draft.worker3Id > 0) countMap[draft.worker3Id] = (countMap[draft.worker3Id] ?: 0) + 1
            if (draft.worker4Id > 0) countMap[draft.worker4Id] = (countMap[draft.worker4Id] ?: 0) + 1
        }
        countMap.filter { it.value > 1 }.keys.toList()
    }

    val overuseNames = remember(repeatList, activeRoster) {
        repeatList.mapNotNull { rid -> activeRoster.find { it.id == rid }?.name }
    }

    // Main Event quality tracker
    val slot4Empty = draftSlots.find { it.id == 4 }?.isBooked() == false
    val weakMainEvent = remember(draftSlots, activeRoster) {
        val slot4 = draftSlots.find { it.id == 4 }
        if (slot4 != null && slot4.isBooked()) {
            val w1 = activeRoster.find { it.id == slot4.worker1Id }
            val w2 = activeRoster.find { it.id == slot4.worker2Id }
            val avgPop = ((w1?.popularity ?: 45) + (w2?.popularity ?: 45)) / 2
            avgPop < 72
        } else false
    }

    // Projected performance formula
    val projectedRating = remember(draftSlots, activeRoster) {
        var accu = 0
        var div = 0
        draftSlots.forEach { draft ->
            if (draft.isBooked()) {
                div++
                val w1 = activeRoster.find { it.id == draft.worker1Id }
                val w2 = activeRoster.find { it.id == draft.worker2Id }
                
                val activeType = if (draft.detailedSegmentType.isNotEmpty()) draft.detailedSegmentType else {
                    when (draft.segmentType) {
                        "Promo" -> "Promo"
                        "Angle" -> "Brawl"
                        else -> "Singles Match"
                    }
                }

                val itemScore = when (activeType) {
                    "Promo" -> {
                        val m1 = w1?.micSkill ?: 50
                        val p1 = w1?.popularity ?: 50
                        (m1 * 0.70f + p1 * 0.30f).toInt()
                    }
                    "Interview" -> {
                        val m1 = w1?.micSkill ?: 50
                        val m2 = w2?.micSkill ?: 50
                        ((m1 + m2) / 2 * 0.75f + 10).toInt()
                    }
                    "Brawl" -> {
                        val p1 = w1?.popularity ?: 50
                        val p2 = w2?.popularity ?: 50
                        ((p1 + p2)/2f * 0.7f + 12).toInt()
                    }
                    "Authority Segment" -> {
                        val m1 = w1?.micSkill ?: 50
                        (m1 * 0.82f + 15).toInt()
                    }
                    else -> { // matches
                        val p1 = w1?.popularity ?: 50
                        val p2 = w2?.popularity ?: 50
                        val s1 = w1?.inRingSkill ?: 50
                        val s2 = w2?.inRingSkill ?: 50
                        (((p1 + p2)/2 * 0.4f) + ((s1 + s2)/2 * 0.6f)).toInt() + 4
                    }
                }
                accu += itemScore.coerceIn(10, 100)
            }
        }
        if (div == 0) 40 else accu / div
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(
            title = "CURRENT SHOW DRAFT AUDIT",
            subTitle = "Examine show flow pacing, length limit, and predicted ratings"
        )

        // 1. Digital Control panel (Live Metrics Dial)
        Surface(
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TOTAL DURATION", fontSize = 8.sp, color = MutedText)
                    Text("${totalTime} Min", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (totalTime > 75) ColorAlert else LightText)
                    Text("Limit: 75 Min", fontSize = 7.sp, color = MutedText)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("PROJECTED RATING", fontSize = 8.sp, color = MutedText)
                    Text("${projectedRating}% Rating", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ColorFace)
                    Text("Base on skill metrics", fontSize = 7.sp, color = MutedText)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("EST. TV VIEWERS", fontSize = 8.sp, color = MutedText)
                    val estViewers = 18000 + (projectedRating * 220)
                    Text(String.format("%,d", estViewers), fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                    Text("Sponsor reached", fontSize = 7.sp, color = MutedText)
                }
            }
        }

        // 2. Real-Time Warnings Panel
        val hasWarnings = totalTime > 75 || overuseNames.isNotEmpty() || slot4Empty || weakMainEvent
        if (hasWarnings) {
            Surface(
                color = SlateOverlay,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🚨 PACE WARNINGS & ALIGNMENT ISSUES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ColorAlert)

                    if (totalTime > 75) {
                        Text("• OVERBOOKED CARD: Combined duration of ${totalTime} mins exceeds the 75 mins broadcast slot. Match ratings will drop by -5% due to cutoffs and rushed segments.", fontSize = 9.sp, color = LightText)
                    }

                    if (overuseNames.isNotEmpty()) {
                        Text("• WORKER SEGMENT CONFLICTS: ${overuseNames.joinToString(", ")} are scheduled on multiple segments! Overused performers receive -15 stamina drain and suffer massive fatigue penalties.", fontSize = 9.sp, color = ColorHeel)
                    }

                    if (slot4Empty) {
                        Text("• MAIN EVENT VOID: Match Slot #4 representing the show highpoint is empty! Empty main events dramatically deplete show fanbase development.", fontSize = 9.sp, color = MutedText)
                    } else if (weakMainEvent) {
                        Text("• WEAK STARRING CARD: The Main Event slot contains low-popularity performers (average pop < 72). This lowers overall critical show response index by -8 points.", fontSize = 9.sp, color = ColorAlert)
                    }
                }
            }
        } else {
            Surface(
                color = SlateOverlay,
                border = BorderStroke(1.dp, ColorFace.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Show card is perfectly paced! No warnings detected. Highly geared for optimal rating performance.", fontSize = 9.sp, color = ColorFace)
                }
            }
        }

        // 3. Segment Reordering Section
        Text("MANAGE SEGMENT RUN ORDER (DRAG-REORDER PLANNED BOUTS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)

        draftSlots.forEachIndexed { index, draft ->
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.size(20.dp).background(SlateOverlay, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${draft.id}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        val titleTxt = if (draft.isBooked()) {
                            val activeTp = if (draft.detailedSegmentType.isNotEmpty()) draft.detailedSegmentType else draft.segmentType
                            "${activeTp.uppercase()} (${draft.durationMinutes}m)"
                        } else "EMPTY BROADCAST SLOT"
                        
                        Text(
                            text = titleTxt,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (draft.isBooked()) GoldAccent else MutedText
                        )

                        val detailsTxt = if (draft.isBooked()) {
                            when (draft.detailedSegmentType) {
                                "Tag Match" -> "${draft.worker1Name} & ${draft.worker3Name} vs ${draft.worker2Name} & ${draft.worker4Name}"
                                "Triple Threat" -> "${draft.worker1Name} vs ${draft.worker2Name} vs ${draft.worker3Name}"
                                "Fatal Four-Way" -> "${draft.worker1Name} vs ${draft.worker2Name} vs ${draft.worker3Name} vs ${draft.worker4Name}"
                                "Promo", "Vignette", "Authority Segment" -> "Featuring: ${draft.worker1Name}"
                                else -> "${draft.worker1Name} vs ${draft.worker2Name}"
                            }
                        } else "Empty - Tap slot under Weekly Show to schedule performers"

                        Text(
                            text = detailsTxt,
                            fontSize = 11.sp,
                            color = if (draft.isBooked()) LightText else MutedText,
                            fontFamily = if (draft.isBooked()) FontFamily.SansSerif else FontFamily.Monospace
                        )
                    }

                    // Swap arrows
                    if (index > 0) {
                        IconButton(
                            onClick = { onSwapSlots(draftSlots[index], draftSlots[index - 1]) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, "Move Up", tint = GoldAccent, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (index < draftSlots.size - 1) {
                        IconButton(
                            onClick = { onSwapSlots(draftSlots[index], draftSlots[index + 1]) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, "Move Down", tint = GoldAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentBuilderSubTab() {
    val blueprints = listOf(
        Triple("Single Star Bout", "Classic worker matchup focusing on high skill combinations", "Key Skill: In-Ring | Multiplier: 1.0x | Cost: -10 Stamina"),
        Triple("Tag-Team Synchronizer", "Heavy pacing with team synchronization mechanics and bonus", "Key Skill: In-Ring Teamwork | Multiplier: 1.1x | Cost: -12 Stamina"),
        Triple("Championship Spotlight Title", "Mega hype championship bout defended on standard rules", "Key Skill: Prestige Hype | Multiplier: 1.25x | Cost: -15 Stamina"),
        Triple("Mic Commander Promo", "The talent cuts a fierce microphone promo addressing rivals", "Key Skill: Mic Skill | Multiplier: 0.9x | Cost: -3 Stamina"),
        Triple("Authority Mandate Decree", "Commissioner issues corporate rules and announces matchups", "Key Skill: Charisma | Multiplier: 1.05x | Cost: -2 Stamina"),
        Triple("Fiery Entrance Ramp Brawl", "A highly intense ramp altercation that raises rivalry feud score", "Key Skill: Popularity | Multiplier: 1.0x | Cost: -12 Stamina"),
        Triple("VIP Interview Segment", "Dialogue segment where presenter questions top talent", "Key Skill: Mic Skill | Multiplier: 0.85x | Cost: -2 Stamina"),
        Triple("Contract Signing Ceremony", "Official drama building hype for future pay-per-views", "Key Skill: Blend Mic/Pop | Multiplier: 1.15x | Cost: -4 Stamina"),
        Triple("Cinematic Hype Vignette", "Broadcast cinematic video highlighting training background", "Key Skill: Fanbase popular reach | Multiplier: 1.05x | Cost: 0 Stamina"),
        Triple("Triple Threat Chaos", "Wild high-risk triple performer fight with pacing bonuses", "Key Skill: Agility / InRing | Multiplier: 1.1x | Cost: -14 Stamina"),
        Triple("Fatal Four-Way Spectacle", "Crowd-pleasing four worker action battle ensuring star-power", "Key Skill: Synergy | Multiplier: 1.15x | Cost: -16 Stamina"),
        Triple("Backstage VIP Alignment", "Superstars meet backstage to negotiate alliances or tag strategies", "Key Skill: Charisma | Multiplier: 0.8x | Cost: -2 Stamina")
    )

    var loadedComp by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(
            title = "NARRATIVE SEGMENT BLUEPRINTS",
            subTitle = "Examine required attributes and multipliers for the 12 segment types"
        )

        blueprints.forEach { blueprint ->
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎭", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = blueprint.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                        }

                        Text(
                            text = "USE BLUEPRINT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent,
                            modifier = Modifier
                                .background(SlateOverlay, RoundedCornerShape(2.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clickable {
                                    loadedComp = blueprint.first
                                }
                        )
                    }

                    Text(text = blueprint.second, fontSize = 10.sp, color = MutedText)
                    Text(text = blueprint.third, fontSize = 9.sp, color = ColorFace, fontFamily = FontFamily.Monospace)
                }
            }
        }

        if (loadedComp.isNotEmpty()) {
            Dialog(onDismissRequest = { loadedComp = "" }) {
                Surface(
                    color = SlateDark,
                    border = BorderStroke(1.dp, GoldAccent),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BLUEPRINT ACTIVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Text(
                            text = "'${loadedComp}' has been loaded into memory! Switch over to the 'Weekly Show' tab, tape any Empty Event Slot, and this segment style will be configured automatically for booking.",
                            fontSize = 10.sp,
                            color = LightText,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { loadedComp = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Text("OK, UNDERSTOOD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsDetailContent(report: ShowReport) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("FINANCIAL BALANCE SHEET & CONVERSIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
        Divider(color = ColorCardBorder)

        // Info grid
        Text("TV SHOW STATS WEEK ${report.week}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("OVERALL RATING", fontSize = 8.sp, color = MutedText)
                Text("${report.overallRating}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("SEAT OCCUPANCY", fontSize = 8.sp, color = MutedText)
                Text("${report.attendance} Heads", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("MERCH DEMAND", fontSize = 8.sp, color = MutedText)
                Text("$${String.format("%,.0f", report.merchandiseRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TICKET REVENUE", fontSize = 8.sp, color = MutedText)
                Text("$${String.format("%,.0f", report.ticketRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("ARENA EXPENSES", fontSize = 8.sp, color = MutedText)
                Text("$${String.format("%,.0f", report.showExpense)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorHeel)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("NET BALANCE", fontSize = 8.sp, color = MutedText)
                Text("$${String.format("%,.0f", report.profitLoss)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (report.profitLoss >= 0) ColorFace else ColorHeel)
            }
        }

        Divider(color = ColorCardBorder)

        // Newsdesk report analyzer with interactive media/press summaries
        MatchResultsDashboard(report = report)
    }
}

@Composable
fun ResultsSubTab(
    reports: List<ShowReport>,
    onSelectReportLog: (ShowReport) -> Unit,
    isTablet: Boolean = false
) {
    var activeDetailedReport by remember { mutableStateOf<ShowReport?>(null) }
    var selectedReportWeekForTablet by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(reports) {
        if (selectedReportWeekForTablet == null || reports.none { it.week == selectedReportWeekForTablet }) {
            selectedReportWeekForTablet = reports.firstOrNull()?.week
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(
            title = "HISTORICAL SCORECARDS",
            subTitle = "Audit corporate attendance records, profits/losses, and segment rating indices of retired events"
        )

        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left list column
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (reports.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No retired broadcasts recorded inside the database yet.", fontSize = 11.sp, color = MutedText, textAlign = TextAlign.Center)
                    }
                } else {
                    reports.forEach { report ->
                        val isSelected = isTablet && selectedReportWeekForTablet == report.week
                        Surface(
                            color = if (isSelected) SlateOverlay else SlateCard,
                            border = BorderStroke(1.dp, if (isSelected) GoldAccent else ColorCardBorder),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (isTablet) {
                                    selectedReportWeekForTablet = report.week
                                } else {
                                    activeDetailedReport = report
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "WEEK ${report.week} - ${report.showName.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    Text(text = "Gate seats filled: ${report.attendance} attendees", fontSize = 9.sp, color = MutedText)
                                    Text(
                                        text = "NET EARNING: $${String.format("%,.0f", report.profitLoss)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (report.profitLoss >= 0) ColorFace else ColorHeel
                                    )
                                }

                                // Rating badge
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(GoldAccent, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${report.overallRating}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SlateDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right detail column (Tablet only)
            if (isTablet) {
                Surface(
                    modifier = Modifier.weight(1.7f).fillMaxHeight(),
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    val activeReport = reports.find { it.week == selectedReportWeekForTablet }
                    if (activeReport != null) {
                        ResultsDetailContent(report = activeReport)
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select an event scorecard to examine sheet logs.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Detailed Report Dialog (Phone fallback)
    if (!isTablet && activeDetailedReport != null) {
        val r = activeDetailedReport!!
        Dialog(onDismissRequest = { activeDetailedReport = null }) {
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(10.dp).fillMaxWidth().wrapContentHeight()
            ) {
                ResultsDetailContent(report = r)
            }
        }
    }
}

@Composable
fun StandingsSubTab(
    roster: List<Wrestler>,
    statsList: List<CompanyLeagueStats>,
    isTablet: Boolean = false
) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList
    val rankedCompanies = remember(activeStats) { LeagueEngine.rankStandings(activeStats) }
    val sortedSuperstars = remember(roster) { roster.sortedByDescending { it.popularity } }

    val scrollStateLeft = rememberScrollState()
    val scrollStateRight = rememberScrollState()
    val scrollStateNormal = rememberScrollState()

    @Composable
    fun StandingsBlock() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledSectionHeader(
                title = "GLOBAL PRO WRESTLING COMPOSITE LEADERBOARD",
                subTitle = "Corporate championship standings by cumulative weekly viewers and performance ratings"
            )

            Surface(
                color = SlateCard,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "RST", modifier = Modifier.width(32.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, textAlign = TextAlign.Center)
                        Text(text = "COMPANY BRAND", modifier = Modifier.weight(1f), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(text = "TREND", modifier = Modifier.width(55.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, textAlign = TextAlign.Center)
                        Text(text = "CUMULATIVE VIEWERS", modifier = Modifier.width(130.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, textAlign = TextAlign.Right)
                    }

                    HorizontalDivider(color = ColorCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 6.dp))

                    rankedCompanies.forEachIndexed { idx, rankedComp ->
                        val isPlayer = rankedComp.stats.companyId == 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isPlayer) SlateOverlay else Color.Transparent)
                                .padding(vertical = 10.dp, horizontal = if (isPlayer) 6.dp else 0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank
                            Surface(
                                color = when (rankedComp.rank) {
                                    1 -> GoldAccent
                                    2 -> LightText
                                    3 -> ColorFace
                                    else -> SlateOverlay
                                },
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${rankedComp.rank}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SlateDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Company & style
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rankedComp.stats.companyName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isPlayer) GoldAccent else LightText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rankedComp.style.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedText
                                )
                            }

                            // Trend
                            Box(
                                modifier = Modifier
                                    .width(55.dp)
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ) {
                                val trendColor = when (rankedComp.trend) {
                                    TrendDirection.RISING -> ColorFace
                                    TrendDirection.FALLING -> ColorHeel
                                    TrendDirection.STEADY -> MutedText
                                }
                                val trendSymbol = when (rankedComp.trend) {
                                    TrendDirection.RISING -> "▲ UP"
                                    TrendDirection.FALLING -> "▼ DN"
                                    TrendDirection.STEADY -> "■ STDY"
                                }
                                Surface(
                                    color = trendColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, trendColor.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    Text(
                                        text = trendSymbol,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Black,
                                        color = trendColor,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Cumulative Viewers
                            Column(modifier = Modifier.width(130.dp), horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format("%,d", rankedComp.stats.totalViewers),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "+${String.format("%,d", rankedComp.stats.lastWeeklyViewers)} this week",
                                    fontSize = 8.sp,
                                    color = ColorFace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (idx < rankedCompanies.lastIndex) {
                            HorizontalDivider(color = ColorCardBorder.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SuperstarsIndexBlock() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledSectionHeader(
                title = "GLOBAL SUPERSTAR POPULARITY INDEX",
                subTitle = "Superstars ranking metrics tracking overall market reach and franchise value"
            )

            Surface(
                color = SlateCard,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (sortedSuperstars.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Roster currently vacant. Contract superstars to stream popularity statistics.", fontSize = 11.sp, color = MutedText)
                        }
                    } else {
                        sortedSuperstars.take(5).forEachIndexed { index, wrestler ->
                            val placement = index + 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#$placement",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(36.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = wrestler.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${wrestler.style.uppercase()} • ${wrestler.heelFace} • AGE ${wrestler.age}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MutedText
                                    )
                                }

                                // Popularity Progress Bar / Value
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${wrestler.popularity} PP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorFace,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    // Horizontal visual bar
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(4.dp)
                                            .background(SlateOverlay, RoundedCornerShape(2.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(wrestler.popularity / 100f)
                                                .background(ColorFace, RoundedCornerShape(2.dp))
                                        )
                                    }
                                }
                            }

                            if (index < 4 && index < sortedSuperstars.lastIndex) {
                                HorizontalDivider(color = ColorCardBorder.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (isTablet) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1.1f).fillMaxHeight().verticalScroll(scrollStateLeft),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StandingsBlock()
            }
            Column(
                modifier = Modifier.weight(0.9f).fillMaxHeight().verticalScroll(scrollStateRight),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SuperstarsIndexBlock()
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(scrollStateNormal),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StandingsBlock()
            SuperstarsIndexBlock()
        }
    }
}

@Composable
fun WeeklyRatingsSubTab(reports: List<ShowReport>, statsList: List<CompanyLeagueStats>) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList
    val latestWeek = activeStats.maxOfOrNull { it.totalShowsBooked } ?: 0

    // Find the winners or best metrics this week
    val weekWinner = remember(activeStats) { activeStats.maxByOrNull { it.lastWeeklyViewers } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StyledSectionHeader(
            title = "WEEKLY TELEVISION SHARE COMPILATION",
            subTitle = "Comparative metrics for television network broadcast ratings on Week $latestWeek"
        )

        // Show individual comparisons
        if (latestWeek == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateCard, RoundedCornerShape(4.dp))
                    .border(1.dp, ColorCardBorder)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Weekly ratings data will generate streaming after booking and completing the Friday Night Show.",
                    fontSize = 11.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Visual chart: viewership shares
            Surface(
                color = SlateCard,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "NETWORK VIEWERSHIP VOLUME (LAST WEEK)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent
                    )

                    val maxWeekViewers = activeStats.maxOfOrNull { it.lastWeeklyViewers }?.toDouble() ?: 1.0

                    activeStats.forEach { stats ->
                        val isWeekWinner = stats.companyId == (weekWinner?.companyId ?: -1)
                        val fraction = (stats.lastWeeklyViewers / maxWeekViewers).toFloat().coerceIn(0.05f, 1f)

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stats.companyName.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stats.companyId == 1) GoldAccent else LightText
                                    )
                                    if (isWeekWinner) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "👑 WEEK WINNER", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    }
                                }
                                Text(
                                    text = "${String.format("%,d", stats.lastWeeklyViewers)} viewers",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText
                                )
                            }

                            // Dynamic Stat Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(SlateOverlay, RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction)
                                        .background(
                                            if (stats.companyId == 1) GoldAccent else if (isWeekWinner) ColorFace else ColorCardBorder,
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Quality breakdown cards
            StyledSectionHeader(
                title = "SHOW PRODUCTION QUALITY STATISTICS",
                subTitle = "Critics reviews and production scores recorded across rival networks"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeStats.take(2).forEach { stats ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SlateCard,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, if (stats.companyId == 1) GoldAccent.copy(alpha = 0.5f) else ColorCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stats.companyName.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${stats.averageShowQuality.roundToInt()}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (stats.companyId == 1) GoldAccent else LightText,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Avg Show Output",
                                fontSize = 8.sp,
                                color = MutedText
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeStats.drop(2).take(2).forEach { stats ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SlateCard,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, ColorCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stats.companyName.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${stats.averageShowQuality.roundToInt()}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = LightText,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Avg Show Output",
                                fontSize = 8.sp,
                                color = MutedText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonTableSubTab(statsList: List<CompanyLeagueStats>) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList
    val rankedCompanies = remember(activeStats) { LeagueEngine.rankStandings(activeStats) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StyledSectionHeader(
            title = "SEASON LEAGUE LEDGER DATABASE",
            subTitle = "Complete comparative matrix with metrics and corporate tie-breakers"
        )

        rankedCompanies.forEach { ranked ->
            val isPlayer = ranked.stats.companyId == 1
            Surface(
                color = SlateCard,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, if (isPlayer) GoldAccent else ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (isPlayer) GoldAccent else SlateOverlay,
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${ranked.rank}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isPlayer) SlateDark else LightText
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ranked.stats.companyName.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlayer) GoldAccent else LightText
                            )
                        }

                        // Total viewer banner
                        Text(
                            text = "${String.format("%,d", ranked.stats.totalViewers)} VIEWERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isPlayer) GoldAccent else LightText
                        )
                    }

                    Divider(color = ColorCardBorder.copy(alpha = 0.5f))

                    // Row of stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "AVERAGE QUALITY", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${ranked.stats.averageShowQuality.roundToInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "PPV RATE", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${ranked.stats.ppvPerformanceRating.roundToInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "ROSTER MORALE", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${ranked.stats.averageRosterMorale}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ranked.stats.averageRosterMorale >= 80) ColorFace else ColorAlert,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(text = "TITLE PRESTIGE", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${ranked.stats.averageTitlePrestige}/100",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Form Trend row
                    val ratingsList = ranked.stats.recentRatingsTrend.split(",").filter { it.isNotEmpty() }
                    if (ratingsList.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "RECENT SHOW RESULTS: ", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                ratingsList.forEach { score ->
                                    val scoreInt = score.toIntOrNull() ?: 60
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when {
                                                    scoreInt >= 70 -> ColorFace.copy(alpha = 0.15f)
                                                    scoreInt >= 55 -> ColorAlert.copy(alpha = 0.15f)
                                                    else -> ColorHeel.copy(alpha = 0.15f)
                                                },
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                            .border(
                                                width = 0.5.dp,
                                                color = when {
                                                    scoreInt >= 70 -> ColorFace
                                                    scoreInt >= 55 -> ColorAlert
                                                    else -> ColorHeel
                                                },
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$score%",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when {
                                                scoreInt >= 70 -> ColorFace
                                                scoreInt >= 55 -> ColorAlert
                                                else -> ColorHeel
                                            },
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyComparisonSubTab(statsList: List<CompanyLeagueStats>) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList

    var leftSelectedId by remember { mutableStateOf(1) } // Default Apex
    var rightSelectedId by remember { mutableStateOf(2) } // Default GWG

    val leftCompany = remember(activeStats, leftSelectedId) { activeStats.find { it.companyId == leftSelectedId } ?: activeStats.first() }
    val rightCompany = remember(activeStats, rightSelectedId) { activeStats.find { it.companyId == rightSelectedId } ?: activeStats.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StyledSectionHeader(
            title = "CORPORATE HEAD-TO-HEAD COMPARATOR",
            subTitle = "Compare performance indicators and structures side-by-side"
        )

        // Dropdowns row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left dropdown/selectors
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "PROMOTION ALPHA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    activeStats.forEach {
                        val isSel = it.companyId == leftSelectedId
                        Surface(
                            modifier = Modifier.clickable { leftSelectedId = it.companyId },
                            color = if (isSel) SlateOverlay else SlateCard,
                            border = BorderStroke(1.dp, if (isSel) GoldAccent else ColorCardBorder),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ID ${it.companyId}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) GoldAccent else LightText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = leftCompany.companyName.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(text = "VS", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MutedText)

            // Right dropdown/selectors
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "PROMOTION BETA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ColorFace)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    activeStats.forEach {
                        val isSel = it.companyId == rightSelectedId
                        Surface(
                            modifier = Modifier.clickable { rightSelectedId = it.companyId },
                            color = if (isSel) SlateOverlay else SlateCard,
                            border = BorderStroke(1.dp, if (isSel) ColorFace else ColorCardBorder),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ID ${it.companyId}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) ColorFace else LightText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = rightCompany.companyName.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Comparison Rows Card
        Surface(
            color = SlateCard,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, ColorCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 1. Viewer Comparison
                ComparatorRow(
                    metricName = "TOTAL VIEWERS",
                    leftValue = String.format("%,d", leftCompany.totalViewers),
                    rightValue = String.format("%,d", rightCompany.totalViewers),
                    leftFraction = leftCompany.totalViewers.toFloat() / (leftCompany.totalViewers + rightCompany.totalViewers).coerceAtLeast(1).toFloat(),
                    rightFraction = rightCompany.totalViewers.toFloat() / (leftCompany.totalViewers + rightCompany.totalViewers).coerceAtLeast(1).toFloat()
                )

                Divider(color = ColorCardBorder.copy(alpha = 0.3f))

                // 2. Show Output Quality
                ComparatorRow(
                    metricName = "AVERAGE SHOW QUALITY",
                    leftValue = "${leftCompany.averageShowQuality.roundToInt()}%",
                    rightValue = "${rightCompany.averageShowQuality.roundToInt()}%",
                    leftFraction = leftCompany.averageShowQuality / 100f,
                    rightFraction = rightCompany.averageShowQuality / 100f
                )

                Divider(color = ColorCardBorder.copy(alpha = 0.3f))

                // 3. PPV Quality standard
                ComparatorRow(
                    metricName = "PREMIUM PPV QUALITY",
                    leftValue = "${leftCompany.ppvPerformanceRating.roundToInt()}%",
                    rightValue = "${rightCompany.ppvPerformanceRating.roundToInt()}%",
                    leftFraction = leftCompany.ppvPerformanceRating / 100f,
                    rightFraction = rightCompany.ppvPerformanceRating / 100f
                )

                Divider(color = ColorCardBorder.copy(alpha = 0.3f))

                // 4. Morale Index
                ComparatorRow(
                    metricName = "ROSTER MORALE INDEX",
                    leftValue = "${leftCompany.averageRosterMorale}%",
                    rightValue = "${rightCompany.averageRosterMorale}%",
                    leftFraction = leftCompany.averageRosterMorale / 100f,
                    rightFraction = rightCompany.averageRosterMorale / 100f
                )

                Divider(color = ColorCardBorder.copy(alpha = 0.3f))

                // 5. Championship Prestige
                ComparatorRow(
                    metricName = "CHAMPIONSHIP PRESTIGE",
                    leftValue = "${leftCompany.averageTitlePrestige}/100",
                    rightValue = "${rightCompany.averageTitlePrestige}/100",
                    leftFraction = leftCompany.averageTitlePrestige / 100f,
                    rightFraction = rightCompany.averageTitlePrestige / 100f
                )
            }
        }
    }
}

@Composable
fun ComparatorRow(
    metricName: String,
    leftValue: String,
    rightValue: String,
    leftFraction: Float,
    rightFraction: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = leftValue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = metricName,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = MutedText,
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = rightValue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = ColorFace,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Left progress sliding right-aligned
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(SlateOverlay, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(leftFraction)
                        .background(GoldAccent, RoundedCornerShape(2.dp))
                        .align(Alignment.CenterEnd)
                )
            }

            // Right progress sliding left-aligned
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(SlateOverlay, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(rightFraction)
                        .background(ColorFace, RoundedCornerShape(2.dp))
                        .align(Alignment.CenterStart)
                )
            }
        }
    }
}

@Composable
fun AwardsSubTab(statsList: List<CompanyLeagueStats>, roster: List<Wrestler>) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList
    val awards = remember(activeStats, roster) { LeagueEngine.generateSeasonAwards(activeStats, roster) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StyledSectionHeader(
            title = "SEASON ACCOLADES & INDEPENDENT ACCLAIMS",
            subTitle = "Predictive awards matching real-time database performances"
        )

        awards.forEach { award ->
            Surface(
                color = SlateCard,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Trophy / Accolade Icon
                    Surface(
                        color = SlateOverlay,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.size(44.dp),
                        border = BorderStroke(1.dp, ColorCardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = award.trophyIcon, fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = award.title,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = award.winnerName.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = LightText
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = award.description,
                            fontSize = 10.sp,
                            color = MutedText,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorySubTab(statsList: List<CompanyLeagueStats>) {
    val activeStats = if (statsList.isEmpty()) LeagueEngine.createDefaultStats(1) else statsList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StyledSectionHeader(
            title = "HISTORICAL LEAGUE ACCOMPLISHMENTS",
            subTitle = "Archived corporate performance logs and previous season statistics"
        )

        // Form rankings streams details
        Surface(
            color = SlateCard,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, ColorCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "RANKING FORM DECK STREAM OVER TIME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )

                activeStats.forEach { stats ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stats.companyName.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )

                            // Show form stream string
                            val rankingsList = stats.historicalRankings.split(",").filter { it.isNotEmpty() }
                            val displayStr = if (rankingsList.isEmpty()) "IDLE" else rankingsList.joinToString(" ➜ ")
                            Text(
                                text = "Ranks: $displayStr",
                                fontSize = 9.sp,
                                color = ColorFace,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Divider(color = ColorCardBorder.copy(alpha = 0.2f))
                    }
                }
            }
        }

        // Historic Milestones Box
        Surface(
            color = SlateCard,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, ColorCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "COMMISSIONERS ARCHIVED RECORD LEDGER",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )

                listOf(
                    Pair("PEAK SINGLE-WEEK DRAW", "GWG Broadcast hit 1.95M viewers in week 4 PPV"),
                    Pair("WORST FLOP INCIDENT", "TWA rating dropped below 42% production quality in week 2 Lucha special"),
                    Pair("PRESTIGE MILESTONE", "Apex World Title reached elite 92/100 reputation mark"),
                    Pair("MOST COMBUSTIBLE MORALE", "GPW roster reports record backstage cohesiveness at 95% average morale")
                ).forEach { milestone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = milestone.first,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = MutedText,
                            modifier = Modifier.width(130.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = milestone.second,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = LightText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContractsSubTab(
    roster: List<Wrestler>,
    contracts: List<Contract>,
    viewModel: GameViewModel
) {
    var selectedWrestlerForExtension by remember { mutableStateOf<Wrestler?>(null) }
    var offerWeeks by remember { mutableStateOf(12f) }
    var offerSalary by remember { mutableStateOf(2000f) }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "ROSTER ACCOUNTING SHEETS (CONTRACTS)", subTitle = "Weekly employment wage terms & active renewals")

        if (roster.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No contracted employees active.", fontSize = 11.sp, color = MutedText)
            }
        } else {
            roster.forEach { wrestler ->
                val contract = contracts.find { it.wrestlerId == wrestler.id }
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📁", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = wrestler.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text(
                                    text = "Salary: \$${String.format("%,.0f", wrestler.salary)}/week • Role: ${contract?.roleClause ?: "Active Performer"}",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                            val weeksLeft = contract?.weeksRemaining ?: 0
                            val urgencyColor = if (weeksLeft <= 4) ColorHeel else if (weeksLeft <= 12) ColorAlert else ColorFace
                            Box(modifier = Modifier.background(SlateOverlay, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 4.dp)) {
                                Text(
                                    text = "$weeksLeft WEEKS REMAINING",
                                    fontSize = 8.sp,
                                    color = urgencyColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Action row for renewable contracts
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    selectedWrestlerForExtension = wrestler
                                    offerSalary = wrestler.salary.toFloat()
                                    offerWeeks = 24f
                                }
                            ) {
                                Text("Renew / Extend Contract", fontSize = 11.sp, color = ColorAlert)
                            }
                        }
                    }
                }
            }
        }
    }

    // Contract Extension dialog
    selectedWrestlerForExtension?.let { wrestler ->
        AlertDialog(
            onDismissRequest = { selectedWrestlerForExtension = null },
            containerColor = SlateCard,
            tonalElevation = 6.dp,
            title = {
                Text(
                    text = "Extend Contract: ${wrestler.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Negotiate terms to keep this talent in Apex. Current Wage: \$${String.format("%,.0f", wrestler.salary)} USD.",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                    
                    Text(text = "Contract Duration: ${offerWeeks.toInt()} Weeks", fontSize = 11.sp, color = LightText)
                    Slider(
                        value = offerWeeks,
                        onValueChange = { offerWeeks = it },
                        valueRange = 8f..104f,
                        steps = 12,
                        colors = SliderDefaults.colors(thumbColor = ColorAlert, activeTrackColor = ColorAlert)
                    )

                    Text(text = "Offer Weekly Salary: \$${String.format("%,.0f", offerSalary)} USD", fontSize = 11.sp, color = LightText)
                    Slider(
                        value = offerSalary,
                        onValueChange = { offerSalary = it },
                        valueRange = (wrestler.salary.toFloat() * 0.7f)..(wrestler.salary.toFloat() * 3f),
                        colors = SliderDefaults.colors(thumbColor = ColorAlert, activeTrackColor = ColorAlert)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renewContract(wrestler, offerSalary.toDouble(), offerWeeks.toInt())
                        selectedWrestlerForExtension = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAlert)
                ) {
                    Text("Seal Deal", fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWrestlerForExtension = null }) {
                    Text("Cancel", color = MutedText, fontSize = 11.sp)
                }
            }
        )
    }
}

@Composable
fun NegotiationsSubTab(
    negotiations: List<TransferNegotiation>,
    rostersList: List<Wrestler>,
    viewModel: GameViewModel
) {
    var selectedNegForTalentTerms by remember { mutableStateOf<TransferNegotiation?>(null) }
    var offerWage by remember { mutableStateOf(2000f) }
    var offerWeeks by remember { mutableStateOf(24f) }
    var selectedRole by remember { mutableStateOf("Main Event Star") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(title = "ACTIVE TALENT AGREEMENTS", subTitle = "Contract offers awaiting roster decisions")

        if (negotiations.isEmpty()) {
            Surface(color = SlateCard, border = BorderStroke(1.dp, ColorCardBorder), shape = RoundedCornerShape(4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Negotiations Queue: Empty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "When you draft bid proposals to free agents, responses, counter-demands, or union reports will display in this workspace.", fontSize = 11.sp, color = MutedText)
                }
            }
        } else {
            negotiations.reversed().forEach { nego ->
                val targetWrestler = rostersList.find { it.id == nego.wrestlerId }
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💼", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = nego.wrestlerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text(
                                    text = "Buyout Bid: \$${String.format("%,.0f", nego.bidAmount)} • Status: ${nego.status}",
                                    fontSize = 10.sp,
                                    color = MutedText
                                )
                            }
                            
                            val (badgeColor, statusLabel) = when (nego.status) {
                                "COMPANY_ACCEPTED" -> Pair(ColorFace, "COMPANY ACCEPTED")
                                "COMPANY_REJECTED" -> Pair(ColorHeel, "COMPANY REJECTED")
                                "COUNTERED_BY_COMPANY" -> Pair(ColorAlert, "COUNTERED")
                                "TALENT_ACCEPTED" -> Pair(ColorFace, "CONTRACT SIGNED")
                                "TALENT_REJECTED" -> Pair(ColorHeel, "TALENT REJECTED")
                                else -> Pair(MutedText, nego.status)
                            }
                            Box(modifier = Modifier.background(SlateOverlay, RoundedCornerShape(2.dp)).padding(6.dp)) {
                                Text(text = statusLabel, fontSize = 8.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (targetWrestler != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateOverlay, RoundedCornerShape(2.dp))
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${targetWrestler.style.uppercase()} • AGE: ${targetWrestler.age}", 
                                        fontSize = 9.sp, 
                                        color = MutedText,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text("POP", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                        AttributeBadge(value = targetWrestler.popularity)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text("RING", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                        AttributeBadge(value = targetWrestler.inRingSkill)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text("MIC", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                        AttributeBadge(value = targetWrestler.micSkill)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = { viewModel.deleteTransferNegotiation(nego) }
                            ) {
                                Text("Dismiss / Clean Office", fontSize = 11.sp, color = ColorHeel)
                            }

                            if (nego.status == "COMPANY_ACCEPTED" || nego.status == "TALENT_REJECTED") {
                                Button(
                                    onClick = {
                                        selectedNegForTalentTerms = nego
                                        val initialWage = targetWrestler?.salary?.toFloat() ?: 1800f
                                        offerWage = initialWage * 1.15f
                                        offerWeeks = 24f
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorFace)
                                ) {
                                    Text("Negotiate Personal Terms", fontSize = 11.sp)
                                }
                            } else if (nego.status == "COUNTERED_BY_COMPANY") {
                                Button(
                                    onClick = {
                                        targetWrestler?.let {
                                            viewModel.submitTransferBid(it, nego.counterBidAmount)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorAlert)
                                ) {
                                    Text("Accept Counter \$${String.format("%,.0f", nego.counterBidAmount)}", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Negotiate terms dialog
    selectedNegForTalentTerms?.let { nego ->
        val targetWrestler = rostersList.find { it.id == nego.wrestlerId } ?: return@let
        AlertDialog(
            onDismissRequest = { selectedNegForTalentTerms = null },
            containerColor = SlateCard,
            title = {
                Text(
                    text = "Personal Terms: ${nego.wrestlerName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Offer employment conditions directly to the wrestler. Base valuation: \$${String.format("%,.0f", targetWrestler.salary)} demands.",
                        fontSize = 11.sp,
                        color = MutedText
                    )

                    Text(text = "Contract Duration: ${offerWeeks.toInt()} Weeks", fontSize = 11.sp, color = LightText)
                    Slider(
                        value = offerWeeks,
                        onValueChange = { offerWeeks = it },
                        valueRange = 8f..104f,
                        steps = 12,
                        colors = SliderDefaults.colors(thumbColor = ColorFace, activeTrackColor = ColorFace)
                    )

                    Text(text = "Weekly Salary Term: \$${String.format("%,.0f", offerWage)} USD", fontSize = 11.sp, color = LightText)
                    Slider(
                        value = offerWage,
                        onValueChange = { offerWage = it },
                        valueRange = (targetWrestler.salary.toFloat() * 0.8f)..(targetWrestler.salary.toFloat() * 3f),
                        colors = SliderDefaults.colors(thumbColor = ColorFace, activeTrackColor = ColorFace)
                    )

                    Text(text = "Roster Status Role Promised", fontSize = 11.sp, color = LightText)
                    val roles = listOf("Main Event Star", "Upper Midcarder", "Tag Team Specialist", "Young Prospect")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        roles.forEach { role ->
                            val isChosen = selectedRole == role
                            val selBg = if (isChosen) ColorFace else SlateOverlay
                            val selFg = if (isChosen) SlateCard else LightText
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(selBg, RoundedCornerShape(2.dp))
                                    .clickable { selectedRole = role }
                                    .padding(vertical = 4.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = role.substringBefore(" "),
                                    fontSize = 9.sp,
                                    color = selFg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitContractOffer(nego.id, targetWrestler, offerWage.toDouble(), offerWeeks.toInt(), selectedRole)
                        selectedNegForTalentTerms = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorFace)
                ) {
                    Text("Submit Offer Letter", fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNegForTalentTerms = null }) {
                    Text("Cancel", color = MutedText, fontSize = 11.sp)
                }
            }
        )
    }
}

@Composable
fun ShortlistSubTab(
    rostersList: List<Wrestler>,
    viewModel: GameViewModel
) {
    val shortlist = rostersList.filter { it.isShortlisted }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(title = "TARGET SUPERSTARS LIST", subTitle = "Shortlisted recruit priorities")

        if (shortlist.isEmpty()) {
            Surface(color = SlateCard, border = BorderStroke(1.dp, ColorCardBorder), shape = RoundedCornerShape(4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Talent Shortlist Empty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Add prospective elite talent to shortlists from the free agency markets to receive alerts and manage scouting profiles.", fontSize = 11.sp, color = MutedText)
                }
            }
        } else {
            shortlist.forEach { wrestler ->
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = wrestler.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                            Text(
                                text = "Pop: ${wrestler.popularity} • In-Ring: ${wrestler.inRingSkill} • Mic: ${wrestler.micSkill} • Status: ${if (wrestler.companyId == 0) "Free Agent" else "Rival Star"}",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                        
                        TextButton(
                            onClick = { viewModel.toggleShortlist(wrestler) }
                        ) {
                            Text("Remove", fontSize = 10.sp, color = ColorHeel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoutsSubTab(
    scoutAssignments: List<ScoutAssignment>,
    rostersList: List<Wrestler>,
    viewModel: GameViewModel
) {
    val scouts = listOf("Scout Anderson", "Scout Bailey", "Scout Carter")
    var selectedScoutForDispatch by remember { mutableStateOf<String?>(null) }
    var selectedWrestlerIdForScouting by remember { mutableStateOf<Int?>(null) }
    var selectedFocusType by remember { mutableStateOf("Complete Profile") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "TALENT ACQUISITION CAMPAIGNS", subTitle = "Send staff scouts into international environments")

        scouts.forEach { scoutName ->
            val assignment = scoutAssignments.find { it.scoutName == scoutName }
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✈️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = scoutName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                        if (assignment != null) {
                            Text(
                                text = "Target: ${assignment.wrestlerName} • Focus: ${assignment.focusType} • Remaining: ${assignment.weeksRemaining} Week(s)",
                                fontSize = 10.sp,
                                color = ColorFace,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(text = "Status: IDLE / AVAILABLE FOR DISPATCH", fontSize = 10.sp, color = MutedText)
                        }
                    }
                    
                    if (assignment != null) {
                        TextButton(
                            onClick = { viewModel.cancelScoutAssignment(assignment) }
                        ) {
                            Text("Recall Intel", fontSize = 11.sp, color = ColorHeel)
                        }
                    } else {
                        Button(
                            onClick = { selectedScoutForDispatch = scoutName },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFace)
                        ) {
                            Text("Dispatch", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Dispatch Dialog
    selectedScoutForDispatch?.let { scoutName ->
        val candidateWrestlers = rostersList.filter { it.companyId != 1 && it.scoutingProgress < 100 }
        AlertDialog(
            onDismissRequest = { selectedScoutForDispatch = null },
            containerColor = SlateCard,
            title = {
                Text(
                    text = "Dispatch Scout: $scoutName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Complete deep analysis checklist on target competitors in rival rosters and the free agent pool.",
                        fontSize = 11.sp,
                        color = MutedText
                    )

                    Text(text = "Select Target Wrestler", fontSize = 11.sp, color = LightText)
                    LazyColumn(
                        modifier = Modifier
                            .height(140.dp)
                            .fillMaxWidth()
                            .background(SlateOverlay, RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(candidateWrestlers) { wrestler ->
                            val isSelected = selectedWrestlerIdForScouting == wrestler.id
                            Text(
                                text = "${wrestler.name} (Pop: ${wrestler.popularity})",
                                fontSize = 11.sp,
                                color = if (isSelected) ColorFace else LightText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedWrestlerIdForScouting = wrestler.id }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            )
                        }
                    }

                    Text(text = "Dossier Focus Objective", fontSize = 11.sp, color = LightText)
                    val objectives = listOf("Financial demands", "Loyalty metrics", "Complete Profile")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        objectives.forEach { obj ->
                            val isChosen = selectedFocusType == obj
                            val selBg = if (isChosen) ColorFace else SlateOverlay
                            val selFg = if (isChosen) SlateCard else LightText
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(selBg, RoundedCornerShape(2.dp))
                                    .clickable { selectedFocusType = obj }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = obj.substringBefore(" "),
                                    fontSize = 9.sp,
                                    color = selFg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val wrestlerId = selectedWrestlerIdForScouting
                        if (wrestlerId != null) {
                            val wrestler = candidateWrestlers.find { it.id == wrestlerId }
                            if (wrestler != null) {
                                viewModel.assignScout(wrestler, scoutName, selectedFocusType)
                            }
                        }
                        selectedScoutForDispatch = null
                        selectedWrestlerIdForScouting = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorFace),
                    enabled = selectedWrestlerIdForScouting != null
                ) {
                    Text("Launch Mission", fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedScoutForDispatch = null }) {
                    Text("Cancel", color = MutedText, fontSize = 11.sp)
                }
            }
        )
    }
}

@Composable
fun RumoursSubTab(
    rumours: List<Rumour>
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "SPORTS CHATTER & LOCKER-ROOM GOSSIP", subTitle = "Unverified league rumors ticker")

        if (rumours.isEmpty()) {
            Surface(
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("No dynamic chatter leaked to reporters yet. Check back next weekly show transition!", fontSize = 11.sp, color = MutedText, modifier = Modifier.padding(16.dp))
            }
        } else {
            rumours.reversed().forEach { rumour ->
                Surface(
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💬", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = rumour.text, fontSize = 11.sp, color = LightText)
                            Text(text = "Reporter leak • Week ${rumour.weekGenerated}", fontSize = 9.sp, color = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinancesSubTab(
    roster: List<Wrestler>,
    state: GameState?,
    reports: List<ShowReport>,
    viewModel: GameViewModel
) {
    if (state == null) return
    val payroll = roster.filter { it.companyId == 1 && it.isContracted }.sumOf { it.salary }
    val liquid = state.cash

    val tvLicensing = when (state.tvDeal) {
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
        1 -> 0.0
        2 -> 3500.0
        else -> 12000.0
    }
    val totalStaffWeeklyCost = physicianCost + scoutCost + creativeCost
    val fixedExpenses = 12000.0 + 8000.0 

    val estWeeklyNet = tvLicensing - (payroll + totalStaffWeeklyCost + fixedExpenses)

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(title = "CORPORATE ACCOUNTING GENERAL SHEETS", subTitle = "Promotion income and expense audit")

        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, ColorCardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "TREASURY SUMMARY BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                Text(text = "USD $" + String.format("%,.2f", liquid), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = ColorFace, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = ColorCardBorder)
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(text = "WEEKLY INCOME ESTIMATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorFace)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "TV Net Licensing Contract Fee", fontSize = 11.sp, color = LightText)
                    Text(text = "+$" + String.format("%,.0f", tvLicensing), fontSize = 11.sp, color = ColorFace, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "WEEKLY OUTGOING FIXED EXPENSES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorHeel)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Active Contract Roster Payroll", fontSize = 11.sp, color = LightText)
                    Text(text = "-$" + String.format("%,.0f", payroll), fontSize = 11.sp, color = ColorAlert, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Advisory Board Personnel Payroll", fontSize = 11.sp, color = LightText)
                    Text(text = "-$" + String.format("%,.0f", totalStaffWeeklyCost), fontSize = 11.sp, color = ColorAlert, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Arena Operations & Security Overhead", fontSize = 11.sp, color = LightText)
                    Text(text = "-$" + String.format("%,.0f", fixedExpenses), fontSize = 11.sp, color = ColorAlert, fontFamily = FontFamily.Monospace)
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = ColorCardBorder)
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "ESTIMATED WEEKLY OPERATIONAL NET:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Text(
                        text = (if (estWeeklyNet >= 0) "+$" else "-$") + String.format("%,.0f", Math.abs(estWeeklyNet)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (estWeeklyNet >= 0) ColorFace else ColorHeel,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Text(text = "HISTORICAL ACCOUNTS LEDGER", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent, modifier = Modifier.padding(top = 8.dp))
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp).background(SlateCard, RoundedCornerShape(4.dp)).border(BorderStroke(1.dp, ColorCardBorder)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No prior weekly ledger records found. Launch your first show event to audit history.", fontSize = 11.sp, color = MutedText)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                reports.reversed().forEach { report ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        border = BorderStroke(1.dp, ColorCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "W${report.week} - ${report.showName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text(
                                    text = (if (report.profitLoss >= 0) "+$" else "-$") + String.format("%,.2f", Math.abs(report.profitLoss)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.profitLoss >= 0) ColorFace else ColorHeel,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Attendance: ${report.attendance} fans • Rating: ${report.overallRating}%", fontSize = 10.sp, color = MutedText)
                                Text(text = "Ticket: \$${String.format("%,.0f", report.ticketRevenue)} • Merch: \$${String.format("%,.0f", report.merchandiseRevenue)}", fontSize = 10.sp, color = MutedText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TVDealsSubTab(state: GameState?, viewModel: GameViewModel) {
    if (state == null) return
    val deals = listOf(
        Triple("LOCAL_CABLE", "LOCAL CABLE ACCESS TV", "License Payout: $12,000/wk • Reach: District Base Only • Prestige required: 0+"),
        Triple("FAST_STREAM", "FAST STREAM CHANNEL", "License Payout: $35,000/wk • Reach: Global Streaming • Prestige required: 40+"),
        Triple("USA_NETWORK", "USA BROADCAST NETWORK", "License Payout: $120,000/wk • Reach: National Core • Prestige required: 50+"),
        Triple("PRESTIGE_PREMIUM", "PRESTIGE SPORTS PAY-PER-VIEW", "License Payout: $280,000/wk • Reach: Global Premium • Prestige required: 75+")
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "TELEVISION NETWORKS LICENSE WORKSPACE", subTitle = "Manage brand distribution networks")

        deals.forEach { deal ->
            val dealCode = deal.first
            val reqPrestige = when (dealCode) {
                "FAST_STREAM" -> 40
                "PRESTIGE_PREMIUM" -> 75
                "USA_NETWORK" -> 50
                else -> 0
            }
            val isUnlocked = state.prestige >= reqPrestige
            val isActive = state.tvDeal == dealCode

            Card(
                colors = CardDefaults.cardColors(containerColor = if (isActive) SlateOverlay else SlateCard),
                border = BorderStroke(1.dp, if (isActive) GoldAccent else ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📺", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = deal.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) GoldAccent else LightText)
                        Text(text = deal.third, fontSize = 10.sp, color = MutedText, fontFamily = FontFamily.Monospace)
                        if (!isUnlocked) {
                            Text(text = "LOCKED: Requires ${reqPrestige} reputation prestige (Current: ${state.prestige})", fontSize = 9.sp, color = ColorHeel, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (isActive) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFace),
                            shape = RoundedCornerShape(4.dp),
                            enabled = false,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }
                    } else if (isUnlocked) {
                        Button(
                            onClick = { viewModel.changeTVDeal(dealCode) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, ColorFace),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(30.dp).testTag("sign_tv_" + dealCode.lowercase())
                        ) {
                            Text("SIGN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ColorFace)
                        }
                    } else {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                            shape = RoundedCornerShape(4.dp),
                            enabled = false,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("LOCKED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitlesSubTab(
    titles: List<Title>,
    titleHolders: List<TitleHolder>,
    roster: List<Wrestler>,
    viewModel: GameViewModel
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTitleName by remember { mutableStateOf("") }
    var newTitleDivision by remember { mutableStateOf("World Heavyweight") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StyledSectionHeader(title = "CHAMPIONSHIP DESIGN & MANAGEMENT", subTitle = "Configure division championships")
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("create_title_button")
            ) {
                Text("+ DESIGN TITLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateDark)
            }
        }

        titles.forEach { title ->
            val matchingHolderRecord = titleHolders.find { it.titleId == title.id }
            val championWrestler = if (matchingHolderRecord != null) {
                roster.find { it.id == matchingHolderRecord.wrestlerId }
            } else null

            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = title.titleName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Text(text = "Division: ${title.division} • Prestige: ${title.prestige}/100", fontSize = 10.sp, color = MutedText)
                            }
                        }
                        Button(
                            onClick = { viewModel.upgradeTitlePrestige(title.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, GoldAccent),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(28.dp).testTag("hype_title_" + title.id)
                        ) {
                            Text("HYPE TITLE ($15k)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().background(SlateOverlay, RoundedCornerShape(2.dp)).padding(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑 ", fontSize = 12.sp)
                            Text(
                                text = if (championWrestler != null) "Current Champion: ${championWrestler.name} (Pop: ${championWrestler.popularity})" else "Vacancy: Champion unassigned",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (championWrestler != null) ColorFace else ColorHeel
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = SlateDark,
            title = { Text("Design New Title Strap", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Creation expense: $40,000 USD. Higher quality straps yield better weekly reputation multipliers.", fontSize = 11.sp, color = LightText)
                    OutlinedTextField(
                        value = newTitleName,
                        onValueChange = { newTitleName = it },
                        label = { Text("Championship Name", color = MutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ColorFace,
                            unfocusedTextColor = LightText,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = ColorCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("title_name_input")
                    )
                    
                    val divisions = listOf("World Heavyweight", "Midcard", "Women's", "Tag Team", "Custom Hardcore")
                    Text("Select Division Assignment", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        divisions.take(3).forEach { div ->
                            Button(
                                onClick = { newTitleDivision = div },
                                colors = ButtonDefaults.buttonColors(containerColor = if (newTitleDivision == div) GoldAccent else SlateCard),
                                border = BorderStroke(1.dp, ColorCardBorder),
                                modifier = Modifier.weight(1f).height(32.dp).padding(0.dp)
                            ) {
                                Text(div.take(9), fontSize = 8.sp, color = if (newTitleDivision == div) SlateDark else LightText)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitleName.isNotBlank()) {
                            viewModel.createNewTitle(newTitleName, newTitleDivision)
                            newTitleName = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("FORGE TITLE", color = SlateDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("CANCEL", color = ColorHeel)
                }
            }
        )
    }
}

data class StaffOption(
    val key: String,
    val second: String,
    val third: List<String>,
    val fourth: Int
)

@Composable
fun StaffSubTab(state: GameState?, viewModel: GameViewModel) {
    if (state == null) return
    val personnel = listOf(
        StaffOption(
            "PHYSICIAN",
            "CHIEF MEDICAL OFFICER",
            listOf(
                "Level 1: Cadet Trainer • Rehab Bonus: Standard • Salary: Included",
                "Level 2: Dr. John Brooks • Rehab Bonus: +15% • Hire Expense: $25k • Weekly: $2.5k",
                "Level 3: Sports Science Hub • Rehab Bonus: +30% • Upgrade Expense: $60k • Weekly: $8k"
            ),
            state.physicianLevel
        ),
        StaffOption(
            "SCOUT",
            "ROSTER ACQUISITIONS SCOUT",
            listOf(
                "Level 1: Freelance Agent • Search Bonus: Standard • Salary: Included",
                "Level 2: Chief Recruiter • Speed Bonus: +1 Wk • Hire Expense: $20k • Weekly: $3k",
                "Level 3: Global Network • Speed Bonus: +2 Wk • Upgrade Expense: $50k • Weekly: $10k"
            ),
            state.scoutLevel
        ),
        StaffOption(
            "CREATIVE",
            "HEAD CHIEF WRITER",
            listOf(
                "Level 1: Standard Writers • Morale Bonus: Standard • Salary: Included",
                "Level 2: Gold Booking Advisor • Morale Bonus: +1/Wk • Hire Expense: $30k • Weekly: $3.5k",
                "Level 3: Legendary Booker • Morale Bonus: +3/Wk • Upgrade Expense: $75k • Weekly: $12k"
            ),
            state.creativeLevel
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "CORPORATE STAFF DIRECTORY", subTitle = "Manage office personnel and trainer assists")

        personnel.forEach { member ->
            val staffKey = member.key
            val curLevel = member.fourth

            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍💼", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = member.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Box(modifier = Modifier.background(SlateOverlay, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(text = "GRADE $curLevel/3", fontSize = 8.sp, color = ColorFace, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        member.third.forEachIndexed { idx, desc ->
                            val isCurrent = idx + 1 == curLevel
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = if (isCurrent) ColorFace else MutedText,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    if (curLevel < 3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val upgradeCost = when (staffKey) {
                            "PHYSICIAN" -> if (curLevel == 1) 25000.0 else 60000.0
                            "SCOUT" -> if (curLevel == 1) 20000.0 else 50000.0
                            else -> if (curLevel == 1) 30000.0 else 75000.0
                        }
                        Button(
                            onClick = { viewModel.upgradeStaff(staffKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, ColorFace),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.align(Alignment.End).height(30.dp).testTag("upgrade_staff_" + staffKey.lowercase())
                        ) {
                            Text("UPGRADE PERSON (Cost $" + String.format("%,.0f", upgradeCost) + ")", fontSize = 8.sp, color = ColorFace)
                        }
                    }
                }
            }
        }
    }
}

data class FacilityOption(
    val key: String,
    val second: String,
    val third: List<String>,
    val fourth: Int
)

@Composable
fun FacilitiesSubTab(state: GameState?, viewModel: GameViewModel) {
    if (state == null) return
    val facilities = listOf(
        FacilityOption(
            "GYM",
            "TRAINING CARDIO GYMS",
            listOf(
                "Level 1: Core Treadmills • Gains: Standard (5% chance)",
                "Level 2: Gold Iron Gyms • Gains: +1 skills (25% chance) • Cost: $50,000",
                "Level 3: Apex Training Academy • Gains: +1 skills (50% chance) • Cost: $120,000"
            ),
            state.facilityGym
        ),
        FacilityOption(
            "PYRO",
            "PYRO & AUDIO ENHANCEMENTS",
            listOf(
                "Level 1: Spark Sparkler Guns • Rating Bonus: Standard • Cost: Included",
                "Level 2: HD Strobe Lighting • Rating: +3 points • Ticket: +10% • Cost: $35,000",
                "Level 3: Stadium Ground Fireworks • Rating: +8 points • Ticket: +30% • Cost: $85,000"
            ),
            state.facilityPyro
        ),
        FacilityOption(
            "INFIRMARY",
            "MEDICAL INFIRMARY CARE LABS",
            listOf(
                "Level 1: Standard First-Aid • Injury Weeks Recovery: Base (1 wk)",
                "Level 2: Treatment Care Labs • Recovery Bonus: Double Speed (30% chance) • Cost: $80,000",
                "Level 3: Elite Hyperbaric Suites • Recovery Bonus: Double Speed (65% chance) • Cost: $180,000"
            ),
            state.facilityInfirmary
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "CORPORATE REALTY FACILITIES", subTitle = "Amplify talent capabilities and arena income coefficients")

        facilities.forEach { facility ->
            val facKey = facility.key
            val curLevel = facility.fourth

            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏢", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = facility.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Box(modifier = Modifier.background(SlateOverlay, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(text = "TIER $curLevel/3", fontSize = 8.sp, color = ColorFace, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        facility.third.forEachIndexed { idx, desc ->
                            val isCurrent = idx + 1 == curLevel
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = if (isCurrent) ColorFace else MutedText,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    if (curLevel < 3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val cost = when (facKey) {
                            "GYM" -> if (curLevel == 1) 50000.0 else 120000.0
                            "PYRO" -> if (curLevel == 1) 35000.0 else 85000.0
                            else -> if (curLevel == 1) 80000.0 else 180000.0
                        }
                        Button(
                            onClick = { viewModel.upgradeFacility(facKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, ColorFace),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.align(Alignment.End).height(30.dp).testTag("upgrade_facility_" + facKey.lowercase())
                        ) {
                            Text("UPGRADE SUITE (Cost $" + String.format("%,.0f", cost) + ")", fontSize = 8.sp, color = ColorFace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandIdentitySubTab(state: GameState?, viewModel: GameViewModel) {
    if (state == null) return
    val identities = listOf(
        Pair("Classic", "WRESTLING Grappling Traditionalism: Technical wrestlers get rating multipliers. Audiences are core technical grappler purists."),
        Pair("Mainstream", "HOLLYWOOD Entertaining Charisma: Wrestlers with high popularity and charisma get multipliers. Audiences are broad families/casuals."),
        Pair("Hardcore", "CAGE MATCH Extreme Violence: Matches with cage/ladder stipulations receive severe grade upgrades ($12 rating spike!)."),
        Pair("Lucha", "HIGH FLYING Mexican Acrobatics: Speed flight high-flyers get bonuses. Audiences are quick acrobatic flight appreciators.")
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyledSectionHeader(title = "PROMOTION LOGO & GRAPHICS CODES", subTitle = "Aesthetic brand identity parameters")

        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, ColorCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "CURRENT ACTIVE BRAND IDENTITY TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                Text(text = state.identityBrand.uppercase() + " BRAND STYLE", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                Spacer(modifier = Modifier.height(6.dp))
                val desc = identities.find { it.first == state.identityBrand }?.second ?: "Default style"
                Text(text = desc, fontSize = 11.sp, color = LightText)
            }
        }

        Text(text = "RE-BRAND OPERATIONS SYSTEM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorFace)

        identities.forEach { ident ->
            val isCurrent = state.identityBrand == ident.first
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isCurrent) SlateOverlay else SlateCard),
                border = BorderStroke(1.dp, if (isCurrent) ColorFace else ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = when (ident.first) {
                        "Classic" -> "🤼"
                        "Mainstream" -> "🎬"
                        "Hardcore" -> "⛓️"
                        else -> "🛩️"
                    }, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ident.first.uppercase() + " CORE PLATFORM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Text(text = ident.second, fontSize = 10.sp, color = MutedText)
                    }
                    if (isCurrent) {
                        Box(modifier = Modifier.background(ColorFace, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(text = "ACTIVE", fontSize = 8.sp, color = SlateDark, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.updateBrandIdentity(ident.first) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, ColorCardBorder),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(28.dp).testTag("brand_select_" + ident.first.lowercase())
                        ) {
                            Text("SELECT", fontSize = 8.sp, color = LightText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ObjectivesSubTab(
    state: GameState?,
    roster: List<Wrestler>,
    reports: List<ShowReport>,
    viewModel: GameViewModel
) {
    if (state == null) return
    val objectivesList = listOf(
        valObj(0, "Preserve Treasury Reserve", "Keep operating cash above \$100,000 USD.", 25000.0, 5, state.cash >= 100000.0),
        valObj(1, "Sign Elite Superstars", "Sign at least 3 active superstars with Popularity above 60.", 35000.0, 8, roster.filter { it.companyId == 1 && it.isContracted && it.popularity > 60 }.size >= 3),
        valObj(2, "Locker Room Harmony", "Achieve average active roster morale score above 75%.", 40000.0, 10, roster.filter { it.companyId == 1 && it.isContracted }.let { list -> list.isNotEmpty() && list.map { it.morale }.average() >= 75.0 }),
        valObj(3, "High-Octane Broadcast", "Deliver at least 1 show rating score above 78% in reports history.", 50000.0, 12, reports.any { it.overallRating >= 78 })
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StyledSectionHeader(title = "BOARD OF DIRECTORS OBJECTIVES", subTitle = "Critical milestones required to retain your position")

        objectivesList.forEach { obj ->
            val idx = obj.idx
            val mask = 1 shl idx
            val isClaimed = (state.completedObjMask and mask) != 0
            val isEligible = obj.isValid && !isClaimed

            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, if (isClaimed) ColorFace else ColorCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isClaimed) "✅" else "🎯",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = obj.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isClaimed) MutedText else LightText)
                        Text(text = obj.description, fontSize = 11.sp, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reward: \$" + String.format("%,.0f", obj.rewardCash) + " USD • Reputation: +${obj.rewardPrestige}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                    if (isClaimed) {
                        Text("CLAIMED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.padding(6.dp))
                    } else if (isEligible) {
                        Button(
                            onClick = { viewModel.claimObjectiveReward(idx, obj.rewardCash, obj.rewardPrestige) },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFace),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(30.dp).testTag("claim_obj_" + idx)
                        ) {
                            Text("CLAIM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }
                    } else {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                            shape = RoundedCornerShape(4.dp),
                            enabled = false,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("LOCKED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LightText)
                        }
                    }
                }
            }
        }
    }
}

data class valObj(
    val idx: Int,
    val title: String,
    val description: String,
    val rewardCash: Double,
    val rewardPrestige: Int,
    val isValid: Boolean
)

// --- SUB TABS ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OfficeTab(
    state: GameState?,
    rosterList: List<Wrestler>,
    reports: List<ShowReport>,
    mails: List<InboxMessage>,
    navigateToInbox: () -> Unit,
    navigateToBooker: () -> Unit,
    onSelectReportLog: (ShowReport) -> Unit,
    isTablet: Boolean = false
) {
    val scrollStateLeft = rememberScrollState()
    val scrollStateRight = rememberScrollState()
    val scrollStateNormal = rememberScrollState()

    val unreadMails = mails.filter { !it.isRead }
    val displayMails = if (unreadMails.isNotEmpty()) unreadMails.take(2) else mails.take(2)
    val isPPV = (state?.currentWeek ?: 1) % 4 == 0

    @Composable
    fun InboxBulletinBlock() {
        if (displayMails.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SlateCard,
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✉️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CRITICAL OFFICE BULLETIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "${unreadMails.size} UNREAD",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorHeel,
                            modifier = Modifier
                                .background(ColorHeel.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        displayMails.forEach { mail ->
                            val indicatorColor = when (mail.type) {
                                "COMPLAINT" -> ColorHeel
                                "SPONSOR" -> ColorFace
                                else -> GoldAccent
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navigateToInbox() }
                                    .background(SlateOverlay, RoundedCornerShape(3.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(indicatorColor, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = mail.sender.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = indicatorColor
                                        )
                                        Text(
                                            text = mail.subject,
                                            fontSize = 11.sp,
                                            color = LightText,
                                            fontWeight = if (mail.isRead) FontWeight.Normal else FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Read",
                                    tint = MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NextBroadcastTrendBlock() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1.2f),
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "NEXT BROADCAST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state?.currentShowName ?: "Friday Night Showcase",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(if (isPPV) ColorHeel.copy(alpha = 0.2f) else ColorFace.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isPPV) "PRESTIGE PPV SPECIAL" else "WEEKLY TV SHOW",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPPV) ColorHeel else ColorFace
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(0.8f),
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "VIEWER TREND", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (reports.isEmpty()) "STABLE 100%" else {
                            val last = reports.last().overallRating
                            if (last >= 70) "+8.4% 📈" else "-3.2% 📉"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Audience momentum", fontSize = 8.sp, color = MutedText)
                }
            }
        }
    }

    @Composable
    fun CashFanbaseBlock() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoStatBlock(
                title = "LIQUID CASH",
                value = "\$${String.format("%,.2f", state?.cash ?: 0.0)}",
                modifier = Modifier.weight(1f),
                subText = "Operating budget"
            )
            InfoStatBlock(
                title = "FANBASE SIZE",
                value = "${String.format("%,d", state?.fanbase ?: 0)} fans",
                modifier = Modifier.weight(1f),
                subText = "Local exposure"
            )
        }
    }

    @Composable
    fun StandingBlock() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "WRESTLING PROMOTION LEAGUE STANDINGS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val currentFanbase = state?.fanbase ?: 25000
                val league = listOf(
                    Triple("1. GWG (Giga-Wrestling Global)", 110000, 85),
                    Triple("2. GPW (Global Pro Wrestling)", 55000, 72),
                    Triple("3. APW (Apex Pro Wrestling)", currentFanbase, state?.prestige ?: 60),
                    Triple("4. TWA (Tokyo Wrestling Alliance)", 18000, 48)
                ).sortedByDescending { it.second }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    league.forEachIndexed { idx, item ->
                        val isUs = item.first.contains("APW")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isUs) SlateOverlay else Color.Transparent, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isUs) "▶ ${item.first}" else item.first,
                                fontSize = 11.sp,
                                fontWeight = if (isUs) FontWeight.Bold else FontWeight.Normal,
                                color = if (isUs) GoldAccent else LightText
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "${String.format("%,d", item.second)} fans",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isUs) GoldAccent else MutedText
                                )
                                Text(
                                    text = "PRESTIGE: ${item.third}%",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (idx == 0) ColorFace else if (idx == 3) ColorHeel else LightText
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FeudChampionMoraleBlock() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "WORLD CHAMPION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    val champName = rosterList.maxByOrNull { it.popularity }?.name ?: "Vacant"
                    Text(
                        text = champName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "Title Defended: Active", fontSize = 8.sp, color = MutedText)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                color = SlateCard,
                border = BorderStroke(1.dp, ColorCardBorder),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "HOT FEUD HEAT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Steel vs Maverick",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "92% Hype (Scorching)", fontSize = 8.sp, color = MutedText)
                }
            }
        }
    }

    @Composable
    fun BackstageMoraleBlock() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                val lowMoraleWrestlers = rosterList.filter { it.morale < 70 }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "BACKSTAGE MORALE AUDIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Box(
                        modifier = Modifier
                            .background(if (lowMoraleWrestlers.isNotEmpty()) ColorHeel.copy(alpha = 0.2f) else ColorFace.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (lowMoraleWrestlers.isNotEmpty()) "${lowMoraleWrestlers.size} WARNINGS" else "STABLE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lowMoraleWrestlers.isNotEmpty()) ColorHeel else ColorFace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                if (lowMoraleWrestlers.isEmpty()) {
                    Text(
                        text = "Locker room atmosphere is harmonious. Your current squad is content with booking times and contract terms.",
                        fontSize = 11.sp,
                        color = LightText
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        lowMoraleWrestlers.take(3).forEach { wrestler ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⚠️", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = wrestler.name, fontSize = 11.sp, color = LightText, fontWeight = FontWeight.Medium)
                                }
                                Text(
                                    text = "Morale: ${wrestler.morale}% (Needs Bookings)",
                                    fontSize = 10.sp,
                                    color = ColorHeel,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PrestigeInfluenceBlock() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "APX COMMISSIONER INFLUENCE & PRESTIGE", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                    Text(text = "${state?.prestige ?: 10} / 100", fontSize = 11.sp, color = GoldAccent, fontFamily = FontFamily.Monospace)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (state?.prestige ?: 0) / 100.0f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = GoldAccent,
                    trackColor = SlateOverlay,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Controls national TV deal offers and sponsor attraction yields.", fontSize = 9.sp, color = MutedText)
            }
        }
    }

    @Composable
    fun ActionControlsRow() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = navigateToBooker,
                modifier = Modifier.weight(1f).testTag("go_booker_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.SportsScore, "Book Match", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("BOOK NEW CARD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = navigateToInbox,
                modifier = Modifier.weight(1.0f).testTag("go_inbox_button"),
                border = BorderStroke(1.dp, ColorCardBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LightText),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.MailOutline, "Open Mail", modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("CHECK MAIL", fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun BroadcastHistoryBlock() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledSectionHeader(title = "EVENT BROADCAST HISTORY", subTitle = "Press cards to audit historical records")
            if (reports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(SlateCard).padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No recorded shows yet. Run your first event booking card to populate analytics.", fontSize = 11.sp, color = MutedText, textAlign = TextAlign.Center)
                }
            } else {
                reports.forEach { report ->
                    ShowHistoryCard(report = report, onClick = { onSelectReportLog(report) })
                }
            }
        }
    }

    if (isTablet) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column (Operational Administration)
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollStateLeft),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledSectionHeader(title = "PROMOTION DASHBOARD", subTitle = "Weekly administration overview")
                InboxBulletinBlock()
                NextBroadcastTrendBlock()
                CashFanbaseBlock()
                StandingBlock()
                ActionControlsRow()
            }

            // Right Column (Strategic Assets & Performance History)
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollStateRight),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeudChampionMoraleBlock()
                BackstageMoraleBlock()
                PrestigeInfluenceBlock()
                FinancialSheetWidget(rosterList = rosterList)
                BroadcastHistoryBlock()
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(scrollStateNormal),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StyledSectionHeader(title = "PROMOTION DASHBOARD", subTitle = "Weekly administration overview")
            InboxBulletinBlock()
            NextBroadcastTrendBlock()
            CashFanbaseBlock()
            StandingBlock()
            FeudChampionMoraleBlock()
            BackstageMoraleBlock()
            PrestigeInfluenceBlock()
            ActionControlsRow()
            FinancialSheetWidget(rosterList = rosterList)
            BroadcastHistoryBlock()
        }
    }
}

@Composable
fun InboxTab(
    mails: List<InboxMessage>,
    onRead: (Int) -> Unit,
    onAction: (InboxMessage) -> Unit,
    onDelete: (Int) -> Unit
) {
    var selectedMailId by remember { mutableStateOf<Int?>(null) }
    var activeCategory by remember { mutableStateOf("ALL") }

    val categories = listOf(
        Pair("ALL", "All"),
        Pair("INJURIES", "Injuries 🏥"),
        Pair("TRANSFERS", "Transfers 💸"),
        Pair("MORALE", "Morale 🔥"),
        Pair("RIVALS", "Rivals ⚔️"),
        Pair("RATINGS", "Ratings 📊"),
        Pair("CONTRACTS", "Contracts 📝"),
        Pair("OBJECTIVES", "Objectives 🎯")
    )

    fun filterMail(mail: InboxMessage, category: String): Boolean {
        val subjectLower = mail.subject.lowercase()
        val bodyLower = mail.body.lowercase()
        val senderLower = mail.sender.lowercase()
        return when (category) {
            "ALL" -> true
            "INJURIES" -> (mail.type == "COMPLAINT" || mail.type == "NEWS") && (subjectLower.contains("medical") || subjectLower.contains("injury") || bodyLower.contains("recover") || bodyLower.contains("rib") || bodyLower.contains("strain") || bodyLower.contains("medical") || senderLower.contains("dr."))
            "TRANSFERS" -> mail.type == "OFFER" && (subjectLower.contains("offer") || subjectLower.contains("buyout") || subjectLower.contains("negotiat") || bodyLower.contains("buyout") || bodyLower.contains("transfer"))
            "MORALE" -> mail.type == "COMPLAINT" && (subjectLower.contains("morale") || subjectLower.contains("frustration") || bodyLower.contains("unhappy") || bodyLower.contains("morale") || bodyLower.contains("frustrated") || bodyLower.contains("backstage"))
            "RIVALS" -> subjectLower.contains("rival") || subjectLower.contains("watch") || bodyLower.contains("gwg") || bodyLower.contains("global") || bodyLower.contains("giga") || bodyLower.contains("twa") || bodyLower.contains("competitor")
            "RATINGS" -> mail.type == "REPORT" || subjectLower.contains("rating") || subjectLower.contains("ppv") || subjectLower.contains("recap") || bodyLower.contains("viewe") || bodyLower.contains("tv")
            "CONTRACTS" -> subjectLower.contains("expiry") || subjectLower.contains("contract") || bodyLower.contains("expiry") || bodyLower.contains("renew") || bodyLower.contains("payroll") || bodyLower.contains("buyout")
            "OBJECTIVES" -> mail.type == "SPONSOR" || subjectLower.contains("objective") || subjectLower.contains("board") || bodyLower.contains("objective") || bodyLower.contains("benchmark")
            else -> true
        }
    }

    val filteredMails = mails.filter { filterMail(it, activeCategory) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth > 680.dp

        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            StyledSectionHeader(title = "MAILROOM INBOX", subTitle = "Board objectives and locker room logistics")
            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal sliding filter bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isCatActive = cat.first == activeCategory
                    val unreadCatCount = mails.count { !it.isRead && filterMail(it, cat.first) }

                    Surface(
                        color = if (isCatActive) GoldAccent else SlateCard,
                        border = BorderStroke(1.dp, if (isCatActive) GoldAccent else ColorCardBorder),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .clickable {
                                activeCategory = cat.first
                                selectedMailId = null // clear selection when category switches
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = cat.second.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCatActive) SlateDark else LightText
                            )
                            if (unreadCatCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(if (isCatActive) SlateDark else ColorHeel, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$unreadCatCount",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCatActive) GoldAccent else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isTablet) {
                // Table / Dual-Pane Layout (List left, detail right)
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Left list panel (weight 1.5)
                    Column(modifier = Modifier.weight(1.5f)) {
                        if (filteredMails.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SlateCard, RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, ColorCardBorder), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No messages under this filter.",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredMails) { mail ->
                                    val isCurSelected = mail.id == selectedMailId
                                    InboxCompactListItem(
                                        mail = mail,
                                        isSelected = isCurSelected,
                                        onClick = {
                                            selectedMailId = mail.id
                                            onRead(mail.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Right detail panel (weight 2.5)
                    Column(
                        modifier = Modifier
                            .weight(2.5f)
                            .fillMaxHeight()
                    ) {
                        val activeMail = mails.find { it.id == selectedMailId }
                        if (activeMail != null) {
                            InboxDetailView(
                                mail = activeMail,
                                onActionClicked = { onAction(activeMail) },
                                onDelete = {
                                    onDelete(activeMail.id)
                                    selectedMailId = null
                                },
                                onBack = null
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SlateCard, RoundedCornerShape(4.dp))
                                    .border(BorderStroke(1.dp, ColorCardBorder), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.MarkEmailRead, "No selection", tint = MutedText, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Select an inbox item to inspect contents",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MutedText
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Mobile single view layout (Standard list or Full Screen detail overlay)
                val activeMail = mails.find { it.id == selectedMailId }
                if (activeMail != null) {
                    InboxDetailView(
                        mail = activeMail,
                        onActionClicked = { onAction(activeMail) },
                        onDelete = {
                            onDelete(activeMail.id)
                            selectedMailId = null
                        },
                        onBack = { selectedMailId = null }
                    )
                } else {
                    if (filteredMails.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Email, "Empty", tint = MutedText, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "Empty inbox under this category filter.", fontSize = 12.sp, color = MutedText)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredMails) { mail ->
                                InboxRowItem(
                                    mail = mail,
                                    onRead = {
                                        selectedMailId = mail.id
                                        onRead(mail.id)
                                    },
                                    onActionClicked = { onAction(mail) },
                                    onDeleteClick = { onDelete(mail.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InboxCompactListItem(
    mail: InboxMessage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val indicatorColor = when (mail.type) {
        "COMPLAINT" -> ColorHeel
        "SPONSOR" -> ColorFace
        else -> GoldAccent
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) SlateOverlay else if (mail.isRead) SlateCard.copy(alpha = 0.5f) else SlateCard,
        border = BorderStroke(1.dp, if (isSelected) GoldAccent else if (mail.isRead) ColorCardBorder.copy(alpha = 0.4f) else ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )
            Column(modifier = Modifier.padding(8.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mail.sender,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) GoldAccent else indicatorColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "WK ${mail.weekReceived}",
                        fontSize = 8.sp,
                        color = MutedText,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mail.subject,
                    fontSize = 11.sp,
                    fontWeight = if (mail.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (mail.isRead) LightText.copy(alpha = 0.8f) else LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun InboxDetailView(
    mail: InboxMessage,
    onActionClicked: () -> Unit,
    onDelete: () -> Unit,
    onBack: (() -> Unit)?
) {
    val indicatorColor = when (mail.type) {
        "COMPLAINT" -> ColorHeel
        "SPONSOR" -> ColorFace
        else -> GoldAccent
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "BACK TO LIST", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(indicatorColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = mail.type.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = indicatorColor)
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Mail", tint = NeonRed, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date / Week received
            Text(
                text = "DATE STAMP: WEEK ${mail.weekReceived} | SEASON 1",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Subject Line
            Text(
                text = mail.subject,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sender
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(SlateOverlay, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = mail.sender.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "FROM: " + mail.sender, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LightText)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = ColorCardBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Body Message
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = mail.body,
                    fontSize = 12.sp,
                    color = LightText,
                    lineHeight = 18.sp
                )

                if (mail.cashBonus > 0.0 && !mail.isHandled) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onActionClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorFace, contentColor = SlateDark),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, "Activate", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ACTIVATE SPONSOR BONUS +\$${String.format("%,.0f", mail.cashBonus)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (mail.cashBonus > 0.0 && mail.isHandled) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Surface(
                        color = SlateOverlay,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, ColorCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, "Activated", tint = ColorFace, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "COMMISSION ACTIVATED AND SECURED", fontSize = 11.sp, color = ColorFace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RosterTab(
    roster: List<Wrestler>,
    titleHolders: List<TitleHolder>,
    onSelectWrestler: (Wrestler) -> Unit,
    isTablet: Boolean = false,
    contracts: List<Contract> = emptyList(),
    relationships: List<WrestlerRelationship> = emptyList(),
    eraSnapshots: List<WrestlerEraSnapshot> = emptyList(),
    injuryStatuses: List<InjuryStatus> = emptyList(),
    onHeelTurn: (Wrestler) -> Unit = {},
    onTrainMic: (Wrestler) -> Unit = {},
    onTrainStamina: (Wrestler) -> Unit = {},
    onRelease: (Wrestler) -> Unit = {}
) {
    var selectedWrestlerIdForTablet by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filterCompanyId by remember { mutableStateOf(-1) } // -1 is ALL, 1 is Apex, 0 is Free Agents, 2 is Rival
    var filterAlignment by remember { mutableStateOf("ALL") } // ALL, FACE, HEEL
    var filterStyle by remember { mutableStateOf("ALL") } // ALL, Showman, Brawler, Technician, High Flyer
    var filterInjury by remember { mutableStateOf("ALL") } // ALL, HEALTHY, INJURED
    var filterChampion by remember { mutableStateOf("ALL") } // ALL, CHAMPION, NON-CHAMPION
    var filterContract by remember { mutableStateOf("ALL") } // ALL, CONTRACTED, FREE_AGENT

    var sortBy by remember { mutableStateOf("Name") } // Name, Popularity, Ring Skill, Mic Skill, Stamina, Morale, Salary, Age
    var sortAscending by remember { mutableStateOf(true) }

    // Filter computation
    val filteredRoster = remember(roster, titleHolders, searchQuery, filterCompanyId, filterAlignment, filterStyle, filterInjury, filterChampion, filterContract, sortBy, sortAscending) {
        roster.filter { wrestler ->
            val matchQuery = wrestler.name.contains(searchQuery, ignoreCase = true) || wrestler.realName.contains(searchQuery, ignoreCase = true)
            val matchCompany = when (filterCompanyId) {
                -1 -> true
                else -> wrestler.companyId == filterCompanyId
            }
            val matchAlign = when (filterAlignment) {
                "ALL" -> true
                else -> wrestler.heelFace.equals(filterAlignment, ignoreCase = true) || wrestler.alignment.equals(filterAlignment, ignoreCase = true)
            }
            val matchStyle = when (filterStyle) {
                "ALL" -> true
                else -> wrestler.style.equals(filterStyle, ignoreCase = true)
            }
            val matchInjury = when (filterInjury) {
                "ALL" -> true
                "HEALTHY" -> wrestler.injuryWeeks == 0
                "INJURED" -> wrestler.injuryWeeks > 0
                else -> true
            }
            val matchChamp = when (filterChampion) {
                "ALL" -> true
                "CHAMPION" -> titleHolders.any { it.wrestlerId == wrestler.id || it.wrestlerName.contains(wrestler.name, ignoreCase = true) }
                "NON-CHAMPION" -> !titleHolders.any { it.wrestlerId == wrestler.id || it.wrestlerName.contains(wrestler.name, ignoreCase = true) }
                else -> true
            }
            val matchContract = when (filterContract) {
                "ALL" -> true
                "CONTRACTED" -> wrestler.isContracted
                "FREE_AGENT" -> !wrestler.isContracted
                else -> true
            }

            matchQuery && matchCompany && matchAlign && matchStyle && matchInjury && matchChamp && matchContract
        }.let { list ->
            val comparator = when (sortBy) {
                "Name" -> compareBy<Wrestler> { it.name.lowercase() }
                "Popularity" -> compareBy { it.popularity }
                "Ring Skill" -> compareBy { it.inRingSkill }
                "Mic Skill" -> compareBy { it.micSkill }
                "Stamina" -> compareBy { it.stamina }
                "Morale" -> compareBy { it.morale }
                "Salary" -> compareBy { wrestler -> wrestler.salary }
                "Age" -> compareBy { it.age }
                else -> compareBy { it.name.lowercase() }
            }
            if (sortAscending) list.sortedWith(comparator) else list.sortedWith(comparator).reversed()
        }
    }

    LaunchedEffect(filteredRoster) {
        if (selectedWrestlerIdForTablet == null || filteredRoster.none { it.id == selectedWrestlerIdForTablet }) {
            selectedWrestlerIdForTablet = filteredRoster.firstOrNull()?.id
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        StyledSectionHeader(
            title = "HISTORICAL ROSTER MANAGER",
            subTitle = "${filteredRoster.size} / ${roster.size} wrestlers matched current query criteria"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search and Filters Bar
        Surface(
            color = SlateCard,
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, ColorCardBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by ring name or real name...", fontSize = 11.sp, color = MutedText) },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("roster_search_input"),
                    textStyle = TextStyle(fontSize = 12.sp, color = LightText),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ColorCardBorder,
                        focusedContainerColor = SlateOverlay,
                        unfocusedContainerColor = SlateOverlay
                    )
                )

                // Inline filters (Football Manager style) - Extremely high touch target compatibility
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Company/Brand filter
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("BRAND:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(54.dp))
                        listOf("ALL" to -1, "APEX" to 1, "FREE AGENTS" to 0, "RIVALS" to 2).forEach { (lbl, cmpId) ->
                            val active = filterCompanyId == cmpId
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterCompanyId = cmpId }
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Alignment & Contract Filters
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ALIGN:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(54.dp))
                        listOf("ALL", "FACE", "HEEL").forEach { align ->
                            val active = filterAlignment == align
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterAlignment = align }
                            ) {
                                Text(
                                    text = align,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("CONTRACT STATUS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        listOf("ALL", "CONTRACTED", "FREE_AGENT").forEach { con ->
                            val active = filterContract == con
                            val lbl = if (con == "FREE_AGENT") "FREE AGENT" else con
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterContract = con }
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Style & Health Filters
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("STYLE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(54.dp))
                        listOf("ALL", "Showman", "Brawler", "Technician", "High Flyer").forEach { style ->
                            val active = filterStyle == style
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterStyle = style }
                            ) {
                                Text(
                                    text = style.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Health & Title Filters
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("HEALTH:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(54.dp))
                        listOf("ALL", "HEALTHY", "INJURED").forEach { health ->
                            val active = filterInjury == health
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterInjury = health }
                            ) {
                                Text(
                                    text = health,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("TITLES:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        listOf("ALL", "CHAMPION", "NON-CHAMPION").forEach { champ ->
                            val active = filterChampion == champ
                            val lbl = if (champ == "NON-CHAMPION") "NON-CHAMP" else champ
                            Surface(
                                color = if (active) GoldAccent else SlateOverlay,
                                border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.clickable { filterChampion = champ }
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else LightText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Divider(color = ColorCardBorder, thickness = 0.5.dp)

                // Sorting row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Sort by:", fontSize = 10.sp, color = MutedText)
                        val sortOptions = listOf("Name", "Age", "Popularity", "Ring Skill", "Mic Skill", "Stamina", "Morale", "Salary")
                        sortOptions.forEach { opt ->
                            val active = sortBy == opt
                            Text(
                                text = opt.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                color = if (active) GoldAccent else MutedText,
                                modifier = Modifier
                                    .clickable {
                                        if (sortBy == opt) {
                                            sortAscending = !sortAscending
                                        } else {
                                            sortBy = opt
                                            sortAscending = true
                                        }
                                    }
                                    .background(if (active) SlateOverlay else Color.Transparent, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (sortAscending) "↑ ASC" else "↓ DESC",
                        fontSize = 10.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { sortAscending = !sortAscending }
                            .background(SlateOverlay, RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left list column
            Column(
                modifier = Modifier.weight(1.3f).fillMaxHeight()
            ) {
                // High density table headers
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SlateOverlay,
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                    border = BorderStroke(1.dp, ColorCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "NAME", modifier = Modifier.weight(1.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Text(text = "ROLE (AGE)", modifier = Modifier.weight(1.0f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Center)
                        Text(text = "POP", modifier = Modifier.weight(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Center)
                        Text(text = "RING", modifier = Modifier.weight(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Center)
                        Text(text = "MIC", modifier = Modifier.weight(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Center)
                        Text(text = "STAM", modifier = Modifier.weight(0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Center)
                        Text(text = "ALIGN", modifier = Modifier.weight(0.9f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText, textAlign = TextAlign.Right)
                    }
                }

                if (filteredRoster.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f).background(SlateCard).padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No wrestlers match your filter settings.", fontSize = 11.sp, color = MutedText)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(filteredRoster) { wrestler ->
                            val isSelected = isTablet && selectedWrestlerIdForTablet == wrestler.id
                            WrestlerHighDensityRow(
                                wrestler = wrestler,
                                isSelected = isSelected,
                                onClick = {
                                    if (isTablet) {
                                        selectedWrestlerIdForTablet = wrestler.id
                                    } else {
                                        onSelectWrestler(wrestler)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Right detail column (Tablet only)
            if (isTablet) {
                Surface(
                    modifier = Modifier.weight(1.7f).fillMaxHeight(),
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    val activeWrestler = filteredRoster.find { it.id == selectedWrestlerIdForTablet }
                    if (activeWrestler != null) {
                        WrestlerDetailContent(
                            wrestler = activeWrestler,
                            contracts = contracts,
                            relationships = relationships,
                            eraSnapshots = eraSnapshots,
                            injuryStatuses = injuryStatuses,
                            titleHolders = titleHolders,
                            onHeelTurn = { onHeelTurn(activeWrestler) },
                            onTrainMic = { onTrainMic(activeWrestler) },
                            onTrainStamina = { onTrainStamina(activeWrestler) },
                            onRelease = { onRelease(activeWrestler) }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select an athlete to inspect active logs.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketTab(
    rostersList: List<Wrestler>,
    budget: Double,
    scoutAssignments: List<ScoutAssignment>,
    viewModel: GameViewModel,
    isTablet: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Free Agents") } // "Free Agents" or "Rival Roster"
    var selectedStyleFilter by remember { mutableStateOf("All Styles") } // "All Styles", "Technician", "Brawler", "Showman", "High Flyer"
    
    var selectedPlayerProfile by remember { mutableStateOf<Wrestler?>(null) }
    var bidOfferAmount by remember { mutableStateOf("") }

    // Filter roster list based on selections
    val candidates = rostersList.filter { wrestler ->
        val matchesSearch = wrestler.name.contains(searchQuery, ignoreCase = true) || wrestler.realName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == "Free Agents") {
            wrestler.companyId == 0
        } else {
            wrestler.companyId != 1 && wrestler.companyId != 0 // signed to rival promotions
        }
        val matchesStyle = if (selectedStyleFilter == "All Styles") {
            true
        } else {
            wrestler.style.equals(selectedStyleFilter, ignoreCase = true)
        }
        matchesSearch && matchesCategory && matchesStyle
    }

    LaunchedEffect(candidates) {
        if (isTablet && (selectedPlayerProfile == null || candidates.none { it.id == selectedPlayerProfile?.id })) {
            selectedPlayerProfile = candidates.firstOrNull()
            bidOfferAmount = ""
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        StyledSectionHeader(
            title = "TRANSFER MARKETPLACE",
            subTitle = "Acquire new talent for Apex. Available Liquidity: \$${String.format("%,.0f", budget)}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search and Filters Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search superstar...", fontSize = 11.sp) },
                textStyle = TextStyle(fontSize = 11.sp, color = LightText),
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorFace,
                    unfocusedBorderColor = ColorCardBorder,
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
            )

            // Category selector (Free Agents vs Rivals)
            Row(
                modifier = Modifier
                    .weight(1.7f)
                    .background(SlateCard, RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                listOf("Free Agents", "Rival Roster").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val bg = if (isSelected) ColorFace else Color.Transparent
                    val fg = if (isSelected) SlateCard else LightText
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(bg, RoundedCornerShape(3.dp))
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = cat, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = fg)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Styles horizontal scroll filter
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All Styles", "Technician", "Brawler", "Showman", "High Flyer").forEach { style ->
                val isSelected = selectedStyleFilter == style
                val borderCol = if (isSelected) ColorFace else ColorCardBorder
                val chipBg = if (isSelected) SlateOverlay else SlateCard
                Box(
                    modifier = Modifier
                        .background(chipBg, RoundedCornerShape(12.dp))
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { selectedStyleFilter = style }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = style, fontSize = 9.sp, color = LightText, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column (List)
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
            ) {
                if (candidates.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Storefront, "Empty Marketplace", tint = MutedText, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "No listings active under current filters.", fontSize = 11.sp, color = MutedText)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(candidates) { wrestler ->
                            val companyName = when (wrestler.companyId) {
                                2 -> "GWG"
                                3 -> "GPW"
                                4 -> "TWA"
                                else -> "Free Agent"
                            }
                            val scouted = wrestler.scoutingProgress >= 100
                            val isSelected = isTablet && selectedPlayerProfile?.id == wrestler.id
                            
                            Surface(
                                color = if (isSelected) SlateOverlay else SlateCard,
                                border = BorderStroke(1.dp, if (isSelected) GoldAccent else if (wrestler.isShortlisted) ColorFace else ColorCardBorder),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedPlayerProfile = wrestler
                                    bidOfferAmount = ""
                                }
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = wrestler.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                            if (wrestler.isShortlisted) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("⭐", fontSize = 10.sp)
                                            }
                                        }
                                        Text(
                                            text = "Popularity: ${wrestler.popularity} • Style: ${wrestler.style} • Firm: $companyName",
                                            fontSize = 10.sp,
                                            color = MutedText
                                        )
                                    }
                                    
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (scouted || wrestler.companyId == 0) "\$${String.format("%,.0f", wrestler.salary)} USD" else "???? USD",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorFace
                                        )
                                        Text(text = "weekly ask", fontSize = 8.sp, color = MutedText)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Column (Inline details for tablets)
            if (isTablet) {
                Surface(
                    modifier = Modifier
                        .weight(1.7f)
                        .fillMaxHeight(),
                    color = SlateCard,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    val wrestler = selectedPlayerProfile
                    if (wrestler != null) {
                        val companyName = when (wrestler.companyId) {
                            2 -> "Giga-Wrestling Global"
                            3 -> "Global Pro Wrestling"
                            4 -> "Tokyo Wrestling Alliance"
                            else -> "Rogue Free Agent"
                        }
                        val isFreeAgent = wrestler.companyId == 0
                        val isShortlisted = wrestler.isShortlisted
                        val scouted = wrestler.scoutingProgress >= 100

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = wrestler.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText
                                )
                                Text(
                                    text = "Scouted: ${wrestler.scoutingProgress}%",
                                    fontSize = 10.sp,
                                    color = if (scouted) ColorFace else ColorAlert
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(SlateOverlay, RoundedCornerShape(2.dp))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("POPULARITY", fontSize = 8.sp, color = MutedText)
                                        Text("${wrestler.popularity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(SlateOverlay, RoundedCornerShape(2.dp))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("IN-RING SKILL", fontSize = 8.sp, color = MutedText)
                                        Text("${wrestler.inRingSkill}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(SlateOverlay, RoundedCornerShape(2.dp))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("MIC PERFORMANCE", fontSize = 8.sp, color = MutedText)
                                        Text("${wrestler.micSkill}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    }
                                }
                            }

                            Text(
                                text = "Primary Style: ${wrestler.style} • Real Name: ${wrestler.realName} • Age: ${wrestler.age}\nFinisher: ${wrestler.finisher}\nTraits: ${wrestler.traits}",
                                fontSize = 11.sp,
                                color = LightText
                            )

                            HorizontalDivider(color = ColorCardBorder, thickness = 1.dp)

                            if (isFreeAgent) {
                                Text(
                                    text = "This performer is a rogue Free Agent. Hiring them requires a standard payroll guarantee. Signing fee is \$${String.format("%,.0f", wrestler.salary * 2.0)} upfront.",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Contracted to: $companyName",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                    if (scouted) {
                                        Text(
                                            text = "Estimated Buyout Value: \$${String.format("%,.0f", (wrestler.popularity * 1500.0) + 12000.0)}\nLoyalty Factor: ${wrestler.loyalty}% • Interest in Apex: ${wrestler.interestLevel}%",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "We require complete Scout investigation assignments to reveal estimated buyouts, financial wage projections, and loyalty metrics.",
                                            fontSize = 10.sp,
                                            color = ColorAlert,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Buyout bidding field
                                    TextField(
                                        value = bidOfferAmount,
                                        onValueChange = { bidOfferAmount = it },
                                        placeholder = { Text("Transfer fee offer amount ($)...", fontSize = 11.sp) },
                                        maxLines = 1,
                                        textStyle = TextStyle(fontSize = 11.sp, color = LightText),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ColorFace,
                                            unfocusedBorderColor = ColorCardBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.toggleShortlist(wrestler)
                                    }
                                ) {
                                    Text(if (isShortlisted) "Remove Tracker" else "Shortlist", fontSize = 11.sp, color = ColorAlert)
                                }

                                if (isFreeAgent) {
                                    Button(
                                        onClick = {
                                            viewModel.signFreeAgent(wrestler)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorFace)
                                    ) {
                                        Text("Quick Sign", fontSize = 11.sp)
                                    }
                                } else {
                                    val bid = bidOfferAmount.toDoubleOrNull()
                                    Button(
                                        onClick = {
                                            if (bid != null) {
                                                viewModel.submitTransferBid(wrestler, bid)
                                                bidOfferAmount = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorFace),
                                        enabled = bid != null && bid > 0
                                    ) {
                                        Text("Submit Buyout Offer", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select a prospective signing to analyze profile analytics.", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Individual profile dialog with bidding/scouting details (mobile phone only)
    if (!isTablet) {
    selectedPlayerProfile?.let { wrestler ->
        val companyName = when (wrestler.companyId) {
            2 -> "Giga-Wrestling Global"
            3 -> "Global Pro Wrestling"
            4 -> "Tokyo Wrestling Alliance"
            else -> "Rogue Free Agent"
        }
        val isFreeAgent = wrestler.companyId == 0
        val isShortlisted = wrestler.isShortlisted
        val scouted = wrestler.scoutingProgress >= 100

        AlertDialog(
            onDismissRequest = { selectedPlayerProfile = null },
            containerColor = SlateCard,
            tonalElevation = 6.dp,
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wrestler.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                    Text(
                        text = "Scouted: ${wrestler.scoutingProgress}%",
                        fontSize = 9.sp,
                        color = if (scouted) ColorFace else ColorAlert
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlateOverlay, RoundedCornerShape(2.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("POPULARITY", fontSize = 8.sp, color = MutedText)
                                Text("${wrestler.popularity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlateOverlay, RoundedCornerShape(2.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("IN-RING SKILL", fontSize = 8.sp, color = MutedText)
                                Text("${wrestler.inRingSkill}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlateOverlay, RoundedCornerShape(2.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MIC PERFORMANCE", fontSize = 8.sp, color = MutedText)
                                Text("${wrestler.micSkill}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                            }
                        }
                    }

                    Text(
                        text = "Primary Style: ${wrestler.style} • Real Name: ${wrestler.realName} • Age: ${wrestler.age}\nFinisher: ${wrestler.finisher}\nTraits: ${wrestler.traits}",
                        fontSize = 11.sp,
                        color = LightText
                    )

                    HorizontalDivider(color = ColorCardBorder, thickness = 1.dp)

                    if (isFreeAgent) {
                        Text(
                            text = "This performer is a rogue Free Agent. Hiring them requires a standard payroll guarantee. Signing fee is \$${String.format("%,.0f", wrestler.salary * 2.0)} upfront.",
                            fontSize = 10.sp,
                            color = MutedText
                        )
                    } else {
                        Column {
                            Text(
                                text = "Contracted to: $companyName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )
                            if (scouted) {
                                Text(
                                    text = "Estimated Buyout Value: \$${String.format("%,.0f", (wrestler.popularity * 1500.0) + 12000.0)}\nLoyalty Factor: ${wrestler.loyalty}% • Interest in Apex: ${wrestler.interestLevel}%",
                                    fontSize = 10.sp,
                                    color = MutedText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            } else {
                                Text(
                                    text = "We require complete Scout investigation assignments to reveal estimated buyouts, financial wage projections, and loyalty metrics.",
                                    fontSize = 10.sp,
                                    color = ColorAlert,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Buyout bidding field
                            TextField(
                                value = bidOfferAmount,
                                onValueChange = { bidOfferAmount = it },
                                placeholder = { Text("Transfer fee offer amount ($)...", fontSize = 10.sp) },
                                maxLines = 1,
                                textStyle = TextStyle(fontSize = 11.sp, color = LightText),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ColorFace,
                                    unfocusedBorderColor = ColorCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.toggleShortlist(wrestler)
                        }
                    ) {
                        Text(if (isShortlisted) "Remove Tracker" else "Shortlist", fontSize = 11.sp, color = ColorAlert)
                    }

                    if (isFreeAgent) {
                        Button(
                            onClick = {
                                viewModel.signFreeAgent(wrestler)
                                selectedPlayerProfile = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFace)
                        ) {
                            Text("Quick Sign", fontSize = 11.sp)
                        }
                    } else {
                        val bid = bidOfferAmount.toDoubleOrNull()
                        Button(
                            onClick = {
                                if (bid != null) {
                                    viewModel.submitTransferBid(wrestler, bid)
                                    selectedPlayerProfile = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFace),
                            enabled = bid != null && bid > 0
                        ) {
                            Text("Submit Buyout Offer", fontSize = 11.sp)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlayerProfile = null }) {
                    Text("Close Details", color = MutedText, fontSize = 11.sp)
                }
            }
        )
    }
}
}

@Composable
fun BookerTab(
    drafts: List<DraftMatch>,
    healthyRoster: List<Wrestler>,
    onEditSlot: (DraftMatch) -> Unit,
    onResetCard: () -> Unit,
    onRunShow: () -> Unit
) {
    val bookedSegmentsCount = drafts.count { it.isBooked() }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            StyledSectionHeader(
                title = "MATCHMAKER EDITING CARD",
                subTitle = "$bookedSegmentsCount / 4 broadcast events booked",
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onResetCard,
                modifier = Modifier.testTag("reset_card_icon_button")
            ) {
                Icon(Icons.Default.Refresh, "Clear Card", tint = NeonRed)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Slots grid / list
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            drafts.forEach { draft ->
                BookingCardSlotRow(draft = draft, onEdit = { onEditSlot(draft) })
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Giant Event simulator trigger
        Button(
            onClick = onRunShow,
            enabled = bookedSegmentsCount > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_event_show_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldAccent,
                contentColor = SlateDark,
                disabledContainerColor = SlateOverlay,
                disabledContentColor = MutedText
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.PlayArrow, "Sim Show", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("RUN AND BROADCAST SHOW", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

// --- ITEM RENDERERS ---

@Composable
fun InfoStatBlock(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subText: String = ""
) {
    Surface(
        modifier = modifier,
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = LightText, fontFamily = FontFamily.Monospace)
            if (subText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subText, fontSize = 8.sp, color = MutedText)
            }
        }
    }
}

@Composable
fun FinancialSheetWidget(rosterList: List<Wrestler>) {
    val totalExpense = rosterList.sumOf { it.salary }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "WEEKLY LIQUID DISBURSEMENTS (PAYROLL)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Roster Employees Active", fontSize = 12.sp, color = LightText)
                Text(text = "${rosterList.size} Signees", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Contract Payroll Obligation (Debit)", fontSize = 12.sp, color = LightText)
                Text(
                    text = "-\$${String.format("%,.2f", totalExpense)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonRed,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = ColorCardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "These wages will deplete auto-magically next advance turn cycle.",
                fontSize = 9.sp,
                color = MutedText
            )
        }
    }
}

@Composable
fun ShowHistoryCard(report: ShowReport, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(SlateOverlay, RoundedCornerShape(2.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(text = "WEEK", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Text(text = "${report.week}", fontSize = 16.sp, color = GoldAccent, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.showName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                Text(text = "Attendance: ${String.format("%,d", report.attendance)}", fontSize = 10.sp, color = MutedText)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "RATING", fontSize = 8.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Text(text = "${report.overallRating}%", fontSize = 14.sp, color = if (report.overallRating >= 70) ColorFace else ColorAlert, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            }
            Icon(Icons.Default.ChevronRight, "Inspect Logs", tint = MutedText, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun InboxRowItem(
    mail: InboxMessage,
    onRead: () -> Unit,
    onActionClicked: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val indicatorColor = when (mail.type) {
        "COMPLAINT" -> ColorHeel
        "SPONSOR" -> ColorFace
        else -> GoldAccent
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRead),
        color = if (mail.isRead) SlateCard.copy(alpha = 0.7f) else SlateCard,
        border = BorderStroke(1.dp, if (mail.isRead) ColorCardBorder.copy(alpha = 0.5f) else ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Priority Tag Strip Indicators
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )

            Column(modifier = Modifier.padding(10.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mail.sender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = indicatorColor
                    )
                    Text(
                        text = "WK ${mail.weekReceived}",
                        fontSize = 9.sp,
                        color = MutedText,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mail.subject,
                    fontSize = 13.sp,
                    fontWeight = if (mail.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (mail.isRead) LightText.copy(alpha = 0.8f) else LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mail.body,
                    fontSize = 11.sp,
                    color = if (mail.isRead) MutedText.copy(alpha = 0.8f) else MutedText,
                    lineHeight = 15.sp
                )

                // Context actions inside emails
                if (mail.cashBonus > 0.0 && !mail.isHandled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onActionClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorFace, contentColor = SlateDark),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(text = "ACTIVATE AND EARN \$${String.format("%,.0f", mail.cashBonus)}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Mail Purger
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.CenterVertically).testTag("delete_mail_icon_${mail.id}")
            ) {
                Icon(Icons.Default.Delete, "Delete Mail", tint = MutedText.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun AttributeBadge(value: Int) {
    val (bgColor, fgColor) = when {
        value >= 80 -> Pair(Color(0xFF10B981).copy(alpha = 0.15f), Color(0xFF10B981)) // Green
        value >= 65 -> Pair(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF60A5FA)) // Blue
        value >= 45 -> Pair(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFFBBF24)) // Amber
        else -> Pair(Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFFCA5A5)) // Red
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier.width(30.dp).height(18.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$value",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = fgColor
            )
        }
    }
}

@Composable
fun WrestlerHighDensityRow(
    wrestler: Wrestler,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) SlateOverlay else SlateCard,
        border = BorderStroke(0.5.dp, if (isSelected) GoldAccent else ColorCardBorder.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Name / Gender Icon column
            Column(modifier = Modifier.weight(1.8f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wrestler.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (wrestler.injuryWeeks > 0) NeonRed else LightText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (wrestler.injuryWeeks > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Emergency, "Injured", tint = NeonRed, modifier = Modifier.size(10.dp))
                    }
                }
                Text(
                    text = "${wrestler.style} | $${String.format("%,.0f", wrestler.salary)}/wk",
                    fontSize = 9.sp,
                    color = MutedText
                )
            }

            // Role Gender Category
            Text(
                text = if (wrestler.gender == "Female") "Knockout" else "Heavyweight",
                modifier = Modifier.weight(1.0f),
                fontSize = 11.sp,
                color = MutedText,
                textAlign = TextAlign.Center
            )

            // Popularity Points
            Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                AttributeBadge(value = wrestler.popularity)
            }

            // Ring Skill rating
            Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                AttributeBadge(value = wrestler.inRingSkill)
            }

            // Mic Ability rating
            Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                AttributeBadge(value = wrestler.micSkill)
            }

            // Stamina Gauge and Color Alerts (Amber for Fatigue)
            val stamColor = if (wrestler.stamina < 40) ColorAlert else ColorFace
            Text(
                text = "${wrestler.stamina}%",
                modifier = Modifier.weight(0.8f),
                fontSize = 11.sp,
                color = stamColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Face alignment (Heels vs Face tagging styles)
            val alignColor = if (wrestler.heelFace == "FACE") ColorFace else ColorHeel
            Text(
                text = wrestler.heelFace,
                modifier = Modifier.weight(0.9f),
                fontSize = 10.sp,
                color = alignColor,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Right
            )
        }
    }
}

@Composable
fun FreeAgentItemRow(
    agent: Wrestler,
    budget: Double,
    onSign: () -> Unit
) {
    val meetsBudget = budget >= (agent.salary * 2.0)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SlateCard,
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = agent.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                Text(text = "${agent.style} | Pop: ${agent.popularity} | Ring: ${agent.inRingSkill}", fontSize = 10.sp, color = MutedText)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Payroll: $${String.format("%,.0f", agent.salary)}/wk", fontSize = 10.sp, color = GoldAccent, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Hiring upfront: $${String.format("%,.0f", agent.salary * 2.0)}", fontSize = 9.sp, color = MutedText)
                }
            }

            Button(
                onClick = onSign,
                enabled = meetsBudget,
                colors = ButtonDefaults.buttonColors(containerColor = ColorFace, contentColor = SlateDark),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp).testTag("sign_agent_${agent.id}"),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(text = "SIGN EMPLOYEE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BookingCardSlotRow(
    draft: DraftMatch,
    onEdit: () -> Unit
) {
    val isSlotFilled = draft.isBooked()
    val slotRoleLabel = when (draft.id) {
        1 -> "CARD OPENER [TV SLOT 1]"
        2 -> "MID-CARD HIGHLIGHT [TV SLOT 2]"
        3 -> "SPECIAL ATTRACTION [TV SLOT 3]"
        4 -> "MAIN EVENT SHOWCASE [TV SLOT 4]"
        else -> "EVENT SEGMENT #${draft.id}"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        color = if (isSlotFilled) SlateCard else SlateOverlay.copy(alpha = 0.5f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSlotFilled) GoldAccent.copy(alpha = 0.6f) else ColorCardBorder.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card visual slot index circular component
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (isSlotFilled) GoldAccent else SlateOverlay, RoundedCornerShape(14.dp))
                    .border(1.dp, if (isSlotFilled) GoldAccent else ColorCardBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${draft.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSlotFilled) SlateDark else MutedText,
                    fontFamily = FontFamily.Monospace
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                // Tactical card role indicator
                Text(
                    text = slotRoleLabel,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSlotFilled) GoldAccent else MutedText,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(2.dp))

                if (isSlotFilled) {
                    val labelStyleColor = when (draft.segmentType) {
                        "Match" -> ColorHeel
                        "Promo" -> ColorFace
                        else -> ColorAlert
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Title / segment summary
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = draft.segmentType.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = labelStyleColor
                                )
                                if (draft.isTitleMatch) {
                                    Surface(
                                        color = GoldAccent.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(2.dp),
                                        border = BorderStroke(0.5.dp, GoldAccent)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = "Title Match",
                                                tint = GoldAccent,
                                                modifier = Modifier.size(9.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "TITLE BOUT",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Black,
                                                color = GoldAccent
                                            )
                                        }
                                    }
                                }
                                if (draft.segmentType == "Match" && draft.matchStipulation != "Normal") {
                                    Text(
                                        text = "[${draft.matchStipulation.uppercase()}]",
                                        fontSize = 8.sp,
                                        color = ColorAlert,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            
                            // Visual names row
                            if (draft.segmentType == "Match") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = draft.worker1Name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                    Surface(
                                        color = SlateOverlay,
                                        shape = RoundedCornerShape(2.dp),
                                        border = BorderStroke(1.dp, ColorCardBorder.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "VS",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                                        )
                                    }
                                    Text(
                                        text = draft.worker2Name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightText
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Mic, "Promo", tint = ColorFace, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "${draft.worker1Name} segment promo address",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LightText
                                    )
                                }
                            }
                        }

                        // Broadcast length badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(SlateOverlay, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Duration",
                                tint = MutedText,
                                modifier = Modifier.size(10.dp)
                              )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${draft.durationMinutes}'",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "OFFLINE — UNREADIED BROADCST BLOCK",
                                fontSize = 11.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Action required: Allocate worker pool or draft promotional slot",
                                fontSize = 9.sp,
                                color = MutedText.copy(alpha = 0.7f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(ColorHeel.copy(alpha = 0.12f), RoundedCornerShape(2.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "VACANT",
                                fontSize = 8.sp,
                                color = ColorHeel,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = if (isSlotFilled) Icons.Default.EditCalendar else Icons.Default.Add,
                contentDescription = "Edit Slot",
                tint = if (isSlotFilled) GoldAccent else MutedText.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// --- DIA LOGS / MODALS ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WrestlerDetailContent(
    wrestler: Wrestler,
    contracts: List<Contract>,
    relationships: List<WrestlerRelationship>,
    eraSnapshots: List<WrestlerEraSnapshot>,
    injuryStatuses: List<InjuryStatus>,
    titleHolders: List<TitleHolder>,
    onHeelTurn: () -> Unit,
    onTrainMic: () -> Unit,
    onTrainStamina: () -> Unit,
    onRelease: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    var activeSubTab by remember(wrestler.id) { mutableStateOf("PROFILE") } // PROFILE, CONTRACT, HISTORY, MEDICAL, RELATIONS

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = wrestler.ringName.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Black, color = LightText)
                    if (wrestler.realName.isNotEmpty() && wrestler.realName != wrestler.ringName) {
                        Text(text = " (${wrestler.realName})", fontSize = 11.sp, color = MutedText)
                    }
                }
                Text(text = "${wrestler.gender} | ${wrestler.style} | Age: ${wrestler.age}", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
            }
            if (onDismiss != null) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Close", tint = LightText)
                }
            }
        }

        // Internal Subtabs bar
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("PROFILE", "CONTRACT", "HISTORY", "MEDICAL", "RELATIONS").forEach { tab ->
                val active = activeSubTab == tab
                Surface(
                    color = if (active) GoldAccent else SlateOverlay,
                    border = BorderStroke(1.dp, if (active) GoldAccent else ColorCardBorder),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.clickable { activeSubTab = tab }
                ) {
                    Text(
                        text = tab,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.Black else LightText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Divider(color = ColorCardBorder, thickness = 1.dp)

        // Render Content Based on active tab
        when (activeSubTab) {
            "PROFILE" -> {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    WrestlerDialogueStatRow(label = "Popularity Points", value = "${wrestler.popularity} / 100")
                    WrestlerDialogueStatRow(label = "In-Ring Skill Rating", value = "${wrestler.inRingSkill} / 100")
                    WrestlerDialogueStatRow(label = "Mic Skill Rating", value = "${wrestler.micSkill} / 100")
                    WrestlerDialogueStatRow(label = "Charisma Level", value = "${wrestler.charisma} / 100")
                    WrestlerDialogueStatRow(label = "Stamina Level", value = "${wrestler.stamina}%")
                    WrestlerDialogueStatRow(label = "Locker Room Morale", value = "${wrestler.morale}%")
                    WrestlerDialogueStatRow(label = "Momentum Metric", value = "${wrestler.momentum}/100")
                    WrestlerDialogueStatRow(label = "Special Finisher Move", value = wrestler.finisher)
                    WrestlerDialogueStatRow(label = "Traits", value = wrestler.traits)

                    val activeTitles = titleHolders.filter { it.wrestlerId == wrestler.id || it.wrestlerName.contains(wrestler.name, ignoreCase = true) }
                    if (activeTitles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ACTIVE CHAMPIONSHIPS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        activeTitles.forEach { holder ->
                            Text("👑 ${holder.titleName} (Held: ${holder.daysHeld} days, Defenses: ${holder.successfulDefenses})", fontSize = 10.sp, color = LightText)
                        }
                    }
                }
            }
            "CONTRACT" -> {
                val contract = contracts.find { it.wrestlerId == wrestler.id }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    WrestlerDialogueStatRow(label = "Contract Status", value = if (wrestler.isContracted) "Contracted (Player Promotion)" else "Free Agent")
                    WrestlerDialogueStatRow(label = "Brand Company", value = if (wrestler.companyId == 1) "Apex Pro Wrestling" else if (wrestler.companyId == 2) "Giga Wrestling Global" else "Unassigned")
                    WrestlerDialogueStatRow(label = "Weekly Payroll Costs", value = "$${String.format("%,.0f", wrestler.salary)} USD")
                    WrestlerDialogueStatRow(label = "Weeks Remaining", value = if (wrestler.isContracted && contract != null) "${contract.weeksRemaining} weeks" else "${wrestler.injuryWeeks + 12} weeks (Projected)")
                    WrestlerDialogueStatRow(label = "Role Clause", value = contract?.roleClause ?: "Independent Contractor")
                    WrestlerDialogueStatRow(label = "Backstage Morale Clause", value = if (contract?.moraleClause == true) "ENABLED" else "DISABLED")
                }
            }
            "HISTORY" -> {
                val snaps = eraSnapshots.filter { it.wrestlerId == wrestler.id }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("CAREER ERA SNAPSHOTS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    if (snaps.isEmpty()) {
                        Text("No historic era file logged for this generation snapshot.", fontSize = 11.sp, color = MutedText)
                    } else {
                        snaps.forEach { snap ->
                            Surface(
                                color = SlateOverlay,
                                border = BorderStroke(1.dp, ColorCardBorder),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = snap.yearOrEraKey, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    Text(text = "Promotion: ${snap.company} | Push: ${snap.pushLevel} | Role: ${snap.role}", fontSize = 9.sp, color = GoldAccent)
                                    Text(text = "Allies: ${snap.allies} | Rivals: ${snap.rivals}", fontSize = 9.sp, color = MutedText)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = snap.notes, fontSize = 10.sp, color = LightText)
                                }
                            }
                        }
                    }
                }
            }
            "MEDICAL" -> {
                val activeInjury = injuryStatuses.find { it.wrestlerId == wrestler.id }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    WrestlerDialogueStatRow(label = "Current Health Level", value = if (wrestler.injuryWeeks > 0) "INJURED / UNFIT TO BOOK" else "HEALTHY")
                    WrestlerDialogueStatRow(label = "Injury Risk Factor", value = "${wrestler.injuryRisk}% (Base Risk)")

                    if (wrestler.injuryWeeks > 0 || activeInjury != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("INFIRMARY NOTES:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonRed)
                        WrestlerDialogueStatRow(label = "Diagnosis Details", value = activeInjury?.injuryType ?: "Muscle soreness")
                        WrestlerDialogueStatRow(label = "Severity Level", value = activeInjury?.severity ?: "Minor strain")
                        WrestlerDialogueStatRow(label = "Rehabilitation Duration", value = "${wrestler.injuryWeeks} weeks remaining")
                        WrestlerDialogueStatRow(label = "Active Rehab Activity", value = activeInjury?.rehabActivity ?: "Resting and standard stretching")
                    } else {
                        Text("No active medical record. Performer is in peak athletic shape.", fontSize = 11.sp, color = MutedText)
                    }
                }
            }
            "RELATIONS" -> {
                val relations = relationships.filter { it.wrestler1Id == wrestler.id || it.wrestler2Id == wrestler.id }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("LOCKER ROOM CONNECTIONS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    if (relations.isEmpty()) {
                        Text("No active locked relations in this simulation cluster.", fontSize = 11.sp, color = MutedText)
                    } else {
                        relations.forEach { rel ->
                            val otherName = if (rel.wrestler1Id == wrestler.id) rel.wrestler2Name else rel.wrestler1Name
                            Row(
                                modifier = Modifier.fillMaxWidth().background(SlateOverlay).padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Relation: ${rel.relationType}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightText)
                                    Text(text = "With: $otherName", fontSize = 10.sp, color = MutedText)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(if (rel.relationType == "Rival") ColorHeel.copy(alpha = 0.2f) else ColorFace.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "${rel.heatLevel} HEAT", fontSize = 9.sp, color = if (rel.relationType == "Rival") ColorHeel else ColorFace, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(color = ColorCardBorder, thickness = 1.dp)

        // Options/Operations Menu buttons
        if (wrestler.isContracted) {
            Text(text = "COMMISSIONER EXECUTIVE ACTIONS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onHeelTurn,
                    modifier = Modifier.testTag("heel_turn_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateOverlay, contentColor = LightText),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (wrestler.heelFace == "FACE") "Turn HEEL" else "Turn FACE",
                        fontSize = 10.sp
                    )
                }
                Button(
                    onClick = onTrainMic,
                    modifier = Modifier.testTag("train_mic_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateOverlay, contentColor = LightText),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Promo Class (\$500)", fontSize = 10.sp)
                }
                Button(
                    onClick = onTrainStamina,
                    modifier = Modifier.testTag("restore_energy_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateOverlay, contentColor = LightText),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Rehab Rest (\$250)", fontSize = 10.sp)
                }
                Button(
                    onClick = onRelease,
                    modifier = Modifier.testTag("fire_wrestler_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Sever Contract", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text(text = "FREE AGENT - CANNOT PERFORM EXECUTIVE ACTIONS IN APEX ROSTER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
        }
    }
}

@Composable
fun WrestlerDetailDialog(
    wrestler: Wrestler,
    contracts: List<Contract>,
    relationships: List<WrestlerRelationship>,
    eraSnapshots: List<WrestlerEraSnapshot>,
    injuryStatuses: List<InjuryStatus>,
    titleHolders: List<TitleHolder>,
    onDismiss: () -> Unit,
    onHeelTurn: () -> Unit,
    onTrainMic: () -> Unit,
    onTrainStamina: () -> Unit,
    onRelease: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            WrestlerDetailContent(
                wrestler = wrestler,
                contracts = contracts,
                relationships = relationships,
                eraSnapshots = eraSnapshots,
                injuryStatuses = injuryStatuses,
                titleHolders = titleHolders,
                onDismiss = onDismiss,
                onHeelTurn = onHeelTurn,
                onTrainMic = onTrainMic,
                onTrainStamina = onTrainStamina,
                onRelease = onRelease
            )
        }
    }
}

@Composable
fun WrestlerDialogueStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MutedText)
        Text(text = value, fontSize = 11.sp, color = LightText, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DraftBookingDialog(
    draft: DraftMatch,
    healthyRoster: List<Wrestler>,
    onDismiss: () -> Unit,
    onConfirmDraft: (DraftMatch) -> Unit
) {
    var detailedSegmentType by remember { mutableStateOf(if (draft.detailedSegmentType.isNotEmpty()) draft.detailedSegmentType else "Singles Match") }
    var segmentType by remember {
        mutableStateOf(
            when (detailedSegmentType) {
                "Promo", "Interview", "Authority Segment" -> "Promo"
                "Brawl", "Backstage Segment", "Contract Signing", "Vignette" -> "Angle"
                else -> "Match"
            }
        )
    }

    var worker1Id by remember { mutableStateOf(draft.worker1Id) }
    var worker1Name by remember { mutableStateOf(draft.worker1Name) }
    var worker2Id by remember { mutableStateOf(draft.worker2Id) }
    var worker2Name by remember { mutableStateOf(draft.worker2Name) }
    var worker3Id by remember { mutableStateOf(draft.worker3Id) }
    var worker3Name by remember { mutableStateOf(draft.worker3Name) }
    var worker4Id by remember { mutableStateOf(draft.worker4Id) }
    var worker4Name by remember { mutableStateOf(draft.worker4Name) }

    var stipulation by remember { mutableStateOf(draft.matchStipulation) }
    var winnerSelection by remember { mutableStateOf(draft.winnerSelection) }
    var durationMinutes by remember { mutableStateOf(if (draft.durationMinutes > 0) draft.durationMinutes else 15) }
    var isTitleMatch by remember { mutableStateOf(draft.isTitleMatch) }
    var linkedRivalryName by remember { mutableStateOf(draft.linkedRivalryName) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "BOOK BROADCAST EVENT Slot #${draft.id}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Dismiss", tint = LightText) }
                }

                Divider(color = ColorCardBorder, thickness = 1.dp)

                // 1. Selector Segment type
                Text(text = "CHOOSE BROADCAST SEGMENT CLASS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val allTypes = listOf(
                        "Singles Match", "Tag Match", "Triple Threat", "Fatal Four-Way", "Title Match",
                        "Promo", "Interview", "Authority Segment",
                        "Brawl", "Backstage Segment", "Contract Signing", "Vignette"
                    )
                    allTypes.forEach { type ->
                        val selected = detailedSegmentType == type
                        FilterChip(
                            selected = selected,
                            onClick = {
                                detailedSegmentType = type
                                segmentType = when (type) {
                                    "Promo", "Interview", "Authority Segment" -> "Promo"
                                    "Brawl", "Backstage Segment", "Contract Signing", "Vignette" -> "Angle"
                                    else -> "Match"
                                }
                                if (type == "TitleMatch" || type == "Title Match") {
                                    isTitleMatch = true
                                }
                                // Reset participants if they don't apply to new type
                                if (segmentType != "Match" && type != "Interview" && type != "Brawl" && type != "Contract Signing" && type != "Backstage Segment") {
                                    worker2Id = 0
                                    worker2Name = ""
                                }
                                if (type != "Tag Match" && type != "Fatal Four-Way") {
                                    worker3Id = 0
                                    worker3Name = ""
                                    worker4Id = 0
                                    worker4Name = ""
                                } else if (type == "Triple Threat") {
                                    worker4Id = 0
                                    worker4Name = ""
                                }
                            },
                            label = { Text(text = type, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = SlateDark,
                                containerColor = SlateOverlay,
                                labelColor = LightText
                            ),
                            shape = RoundedCornerShape(2.dp),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 2. Duration minutes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "SEGMENT LENGTH: ${durationMinutes} MINS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                    Text(text = "Influence on fatigue: ${if (durationMinutes > 20) "High exhaustion" else "Moderate"}", fontSize = 9.sp, color = MutedText)
                }
                Slider(
                    value = durationMinutes.toFloat(),
                    onValueChange = { durationMinutes = it.toInt() },
                    valueRange = 5f..30f,
                    colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent, inactiveTrackColor = SlateOverlay)
                )

                // 3. Selection of performers horizontally
                Text(text = "SELECT PERFORMER A (PROTAGONIST)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                LazyHorizontalScrollRoster(
                    roster = healthyRoster,
                    selectedId = worker1Id,
                    excludedId = worker2Id,
                    onSelect = { id, name ->
                        worker1Id = id
                        worker1Name = name
                    }
                )

                val needsWorker2 = segmentType == "Match" ||
                        detailedSegmentType == "Interview" ||
                        detailedSegmentType == "Brawl" ||
                        detailedSegmentType == "Contract Signing" ||
                        detailedSegmentType == "Backstage Segment"

                if (needsWorker2) {
                    Text(text = "SELECT PERFORMER B (ANTAGONIST)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    LazyHorizontalScrollRoster(
                        roster = healthyRoster,
                        selectedId = worker2Id,
                        excludedId = worker1Id,
                        onSelect = { id, name ->
                            worker2Id = id
                            worker2Name = name
                        }
                    )
                }

                val needsWorker3 = detailedSegmentType == "Tag Match" ||
                        detailedSegmentType == "Triple Threat" ||
                        detailedSegmentType == "Fatal Four-Way"

                if (needsWorker3) {
                    Text(text = "SELECT PERFORMER C (THIRD MEMBER / TAG PARTNER)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    LazyHorizontalScrollRoster(
                        roster = healthyRoster,
                        selectedId = worker3Id,
                        excludedId = worker1Id,
                        onSelect = { id, name ->
                            worker3Id = id
                            worker3Name = name
                        }
                    )
                }

                val needsWorker4 = detailedSegmentType == "Tag Match" ||
                        detailedSegmentType == "Fatal Four-Way"

                if (needsWorker4) {
                    Text(text = "SELECT PERFORMER D (FOURTH MEMBER / OPPONENT PARTNER)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    LazyHorizontalScrollRoster(
                        roster = healthyRoster,
                        selectedId = worker4Id,
                        excludedId = worker2Id,
                        onSelect = { id, name ->
                            worker4Id = id
                            worker4Name = name
                        }
                    )
                }

                // 4. Stipulations (Matches only)
                if (segmentType == "Match") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "CHAMPIONSHIP TITLE MATCH DEFENSE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                        Switch(
                            checked = isTitleMatch,
                            onCheckedChange = { isTitleMatch = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = GoldAccent.copy(alpha = 0.5f))
                        )
                    }

                    Text(text = "STIPULATION RULES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Normal", "Steel Cage", "No DQ", "Ladder").forEach { stip ->
                            val selected = stipulation == stip
                            FilterChip(
                                selected = selected,
                                onClick = { stipulation = stip },
                                label = { Text(text = stip, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = SlateDark,
                                    containerColor = SlateOverlay,
                                    labelColor = LightText
                                ),
                                shape = RoundedCornerShape(2.dp),
                                border = null
                            )
                        }
                    }

                    // 5. Winner selection options
                    Text(text = "PRE-DETERMINED WINNER SCRIPT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val oText1 = if (worker1Name.isNotEmpty()) worker1Name.split(" ").first() else "Team A"
                        val oText2 = if (worker2Name.isNotEmpty()) worker2Name.split(" ").first() else "Team B"

                        listOf(
                            Triple(0, "SIMULATE", "Auto simulation"),
                            Triple(1, oText1, "Force win"),
                            Triple(2, oText2, "Force win")
                        ).forEach { script ->
                            val selected = winnerSelection == script.first
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { winnerSelection = script.first },
                                color = if (selected) GoldAccent else SlateOverlay,
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, if (selected) GoldAccent else ColorCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = script.second.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (selected) SlateDark else LightText)
                                    Text(text = script.third, fontSize = 7.sp, color = if (selected) SlateDark.copy(alpha = 0.7f) else MutedText)
                                }
                            }
                        }
                    }
                }

                // 6. Link active rivalry
                Text(text = "LINK ACTIVE ROSTER FEUD RIVALRY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeFeuds = listOf("None", "John Steel vs Titan Maverick", "Serena Swift vs Kage Vance", "El Dragone vs Rex Gung")
                    activeFeuds.forEach { feud ->
                        val selected = if (feud == "None") linkedRivalryName.isEmpty() else linkedRivalryName == feud
                        FilterChip(
                            selected = selected,
                            onClick = {
                                linkedRivalryName = if (feud == "None") "" else feud
                            },
                            label = { Text(text = feud, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = SlateDark,
                                containerColor = SlateOverlay,
                                labelColor = LightText
                            ),
                            shape = RoundedCornerShape(2.dp),
                            border = null
                        )
                    }
                }

                Divider(color = ColorCardBorder, thickness = 1.dp)

                // Validation rules
                val isCardValid = worker1Id > 0 && (!needsWorker2 || worker2Id > 0) && (!needsWorker3 || worker3Id > 0) && (!needsWorker4 || worker4Id > 0)

                Button(
                    onClick = {
                        if (isCardValid) {
                            onConfirmDraft(
                                draft.copy(
                                    segmentType = segmentType,
                                    detailedSegmentType = detailedSegmentType,
                                    worker1Id = worker1Id,
                                    worker1Name = worker1Name,
                                    worker2Id = worker2Id,
                                    worker2Name = worker2Name,
                                    worker3Id = worker3Id,
                                    worker3Name = worker3Name,
                                    worker4Id = worker4Id,
                                    worker4Name = worker4Name,
                                    matchStipulation = stipulation,
                                    winnerSelection = winnerSelection,
                                    durationMinutes = durationMinutes,
                                    isTitleMatch = isTitleMatch,
                                    linkedRivalryId = if (linkedRivalryName.isNotEmpty()) 1 else 0,
                                    linkedRivalryName = linkedRivalryName
                                )
                            )
                        }
                    },
                    enabled = isCardValid,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("confirm_draft_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = SlateDark,
                        disabledContainerColor = SlateOverlay,
                        disabledContentColor = MutedText
                    ),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(text = "APPLY BOOKING RULE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LazyHorizontalScrollRoster(
    roster: List<Wrestler>,
    selectedId: Int,
    excludedId: Int,
    onSelect: (Int, String) -> Unit
) {
    if (roster.isEmpty()) {
        Text(text = "[WARNING] No active non-injured wrestlers available.", fontSize = 9.sp, color = NeonRed)
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(SlateOverlay)
            .padding(4.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        roster.forEach { worker ->
            if (worker.id != excludedId) {
                val isSelected = worker.id == selectedId
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clickable { onSelect(worker.id, worker.name) }
                        .testTag("draft_worker_${worker.id}"),
                    color = if (isSelected) GoldAccent else SlateDark,
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.dp, if (isSelected) GoldAccent else ColorCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = worker.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) SlateDark else LightText)
                        Text(text = "Pop: ${worker.popularity} | Stam: ${worker.stamina}%", fontSize = 8.sp, color = if (isSelected) SlateDark.copy(alpha = 0.7f) else MutedText)
                    }
                }
            }
        }
    }
}

@Composable
fun EventLogsDialog(
    report: ShowReport,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "BROADCAST TRANSMISSION LOGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        Text(text = "Week ${report.week} - ${report.showName}", fontSize = 10.sp, color = MutedText)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Dismiss", tint = LightText) }
                }

                Divider(color = ColorCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Scroll logs column
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(report.getMatchLogs()) { log ->
                        val isCritical = log.contains("[CRITICAL]")
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isCritical) NeonRed.copy(alpha = 0.15f) else SlateOverlay,
                            border = BorderStroke(1.dp, if (isCritical) NeonRed.copy(alpha = 0.4f) else ColorCardBorder),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Text(
                                text = log,
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = if (isCritical) NeonRed else LightText,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateOverlay, contentColor = LightText),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(text = "CLOSE TRANSCRIBED CARDS", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ShowResultScorecardDialog(
    report: ShowReport,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            color = SlateCard,
            border = BorderStroke(1.dp, ColorCardBorder),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Flash Headline Grid
                Text(
                    text = "OFFICIAL EVENT SCORECARD REPORT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${report.showName} broadcast finalized successfully!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Score Card Badge
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateOverlay, RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "OVERALL SHOW RATING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Text(
                        text = "${report.overallRating}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (report.overallRating >= 70) ColorFace else ColorAlert,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = when {
                            report.overallRating >= 85 -> "★ FIVE STAR BROADCAST CLASSIC ★"
                            report.overallRating >= 70 -> "High Quality Sports Entertainment"
                            report.overallRating >= 55 -> "Avarage Card Performance"
                            else -> "Muted Crowd Reception / Slow Card"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (report.overallRating >= 70) ColorFace else ColorAlert
                    )
                }

                // Sports Finance Grid
                Text(text = "COMMERCIAL BROADCAST STATEMENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedText)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SlateOverlay,
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        ScorecardFinanceRow(label = "Physical Attendance", value = "${String.format("%,d", report.attendance)} seats")
                        ScorecardFinanceRow(label = "Ticket Sales Income", value = "\$${String.format("%,.2f", report.ticketRevenue)}")
                        ScorecardFinanceRow(label = "Concession Merchandising", value = "\$${String.format("%,.2f", report.merchandiseRevenue)}")
                        ScorecardFinanceRow(label = "Arena Setup Operations Debit", value = "-\$${String.format("%,.2f", report.showExpense)}", isNegative = true)
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = ColorCardBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        val prof = report.profitLoss
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "CARD NET REVENUE YIELD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightText)
                            Text(
                                text = "${if (prof >= 0) "+" else ""}\$${String.format("%,.2f", prof)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (prof >= 0) ColorFace else ColorHeel,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Divider(color = ColorCardBorder)

                // Interactive match and promo dashboard with customizable sports news reporters
                MatchResultsDashboard(report = report)

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_scorecard_dialog_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(text = "DISMISS AND PROGRESS CALENDAR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ScorecardFinanceRow(label: String, value: String, isNegative: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MutedText)
        Text(
            text = value,
            fontSize = 11.sp,
            color = if (isNegative) ColorHeel else LightText,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StyledSectionHeader(title: String, subTitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = LightText,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = subTitle.uppercase(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MutedText,
                letterSpacing = 0.2.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseManagementSubTab(viewModel: GameViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Status states
    var slot1Info by remember { mutableStateOf<com.example.repository.GameRepository.SlotInfo?>(null) }
    var slot2Info by remember { mutableStateOf<com.example.repository.GameRepository.SlotInfo?>(null) }
    var slot3Info by remember { mutableStateOf<com.example.repository.GameRepository.SlotInfo?>(null) }
    var autoInfo by remember { mutableStateOf<com.example.repository.GameRepository.SlotInfo?>(null) }
    
    // Refresh slot states helper
    val refreshSlots: () -> Unit = {
        viewModel.getSlotInfo(1) { slot1Info = it }
        viewModel.getSlotInfo(2) { slot2Info = it }
        viewModel.getSlotInfo(3) { slot3Info = it }
        viewModel.getSlotInfo(-1) { autoInfo = it }
    }
    
    // Initial fetch of slot info
    LaunchedEffect(Unit) {
        refreshSlots()
    }
    
    // Dialog state
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogType by remember { mutableStateOf("") } // "SAVE", "LOAD", "SCENARIO"
    var confirmTargetSlot by remember { mutableStateOf(0) }
    var confirmTargetScenario by remember { mutableStateOf("") }
    
    // JSON editor states
    var jsonText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }
    var importSuccess by remember { mutableStateOf<Boolean?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- SECTION 1: HEADER BANNER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, ColorCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Database Controls",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "DATABASE CONTROL STATION",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LightText,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Text(
                    text = "Manage offline save files, load historical era presets, or import/export customized roster files. Everything runs purely offline on your local device.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                )
            }
        }

        // --- SECTION 2: NEW GAME SETUP & HISTORICAL SCENARIOS ---
        Text(
            text = "CHOOSE ERAS OR HISTORICAL SCENARIOS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GoldAccent,
                letterSpacing = 0.5.sp
            )
        )
        
        // Use an explicit local class helper rather than raw Triple to prevent destructuring ambiguities in Kotlin
        class EraPreset(val key: String, val title: String, val desc: String)
        val scenarios = listOf(
            EraPreset("modern", "Modern Era (Present Day)", "Start in the current timeline with Apex Pro Wrestling. Classic mainstream rules, default television agreements, and balanced superstars."),
            EraPreset("ruthless", "Ruthless Era (2000s Setup)", "Attitude and brute force rules! Start with $500K cash, high initial competitive prestige (75%), and intense hardcore brawler rosters."),
            EraPreset("golden", "Golden Era (1980s Setup)", "Colorful larger-than-life characters with extreme charisma! Start with $800K cash and a massive national audience fanbase of 120,000."),
            EraPreset("indie", "Indie Renaissance (2010s Setup)", "Strong-style submission wizards and acrobatic daredevil cruiserweights! Low starting budget ($100K) but premium technical combat skills.")
        )
        
        scenarios.forEach { preset ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, ColorCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = preset.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightText)
                        )
                        Text(
                            text = preset.desc,
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp)
                        )
                    }
                    Button(
                        onClick = {
                            confirmDialogType = "SCENARIO"
                            confirmTargetScenario = preset.key
                            showConfirmDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("start_scenario_${preset.key}")
                    ) {
                        Text("LAUNCH", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold))
                    }
                }
            }
        }

        // --- SECTION 3: SAVE & LOAD SLOTS WORKSPACE ---
        Text(
            text = "SAVE & LOAD SYSTEMS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GoldAccent,
                letterSpacing = 0.5.sp
            )
        )

        class SetupSlot(val slotId: Int, val label: String, val info: com.example.repository.GameRepository.SlotInfo?)
        val slots = listOf(
            SetupSlot(1, "Save Slot 1", slot1Info),
            SetupSlot(2, "Save Slot 2", slot2Info),
            SetupSlot(3, "Save Slot 3", slot3Info),
            SetupSlot(-1, "Autosave Slot (End of Week)", autoInfo)
        )

        slots.forEach { slotItem ->
            val slotId = slotItem.slotId
            val label = slotItem.label
            val info = slotItem.info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, ColorCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldAccent)
                        )
                        if (info?.exists == true) {
                            val lastMod = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(info.timestamp))
                            Text(
                                text = "Modified: $lastMod",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 10.sp)
                            )
                        } else {
                            Text(
                                text = "NO DATA",
                                style = MaterialTheme.typography.bodySmall.copy(color = ColorHeel, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    if (info?.exists == true) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("PROMOTION", style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 9.sp))
                                Text(info.companyName, style = MaterialTheme.typography.bodySmall.copy(color = LightText, fontWeight = FontWeight.Bold))
                            }
                            Column {
                                Text("CALENDAR", style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 9.sp))
                                Text("Week ${info.currentWeek}", style = MaterialTheme.typography.bodySmall.copy(color = LightText, fontWeight = FontWeight.Bold))
                            }
                            Column {
                                Text("FINANCES", style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 9.sp))
                                Text("$${String.format("%,.0f", info.cash)}", style = MaterialTheme.typography.bodySmall.copy(color = LightText, fontWeight = FontWeight.Bold))
                            }
                        }
                    } else {
                        Text(
                            text = "This slot is empty. Record your active simulation progress to backup.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (slotId != -1) {
                            Button(
                                onClick = {
                                    confirmDialogType = "SAVE"
                                    confirmTargetSlot = slotId
                                    showConfirmDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlateDark, contentColor = LightText),
                                border = BorderStroke(1.dp, ColorCardBorder),
                                modifier = Modifier.weight(1f).testTag("save_button_slot_${slotId}"),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("OVERWRITE SAVE", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                        Button(
                            onClick = {
                                confirmDialogType = "LOAD"
                                confirmTargetSlot = slotId
                                showConfirmDialog = true
                            },
                            enabled = info?.exists == true,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (info?.exists == true) GoldAccent else Color.Gray.copy(0.2f),
                                contentColor = SlateDark
                            ),
                            modifier = Modifier.weight(1f).testTag("load_button_slot_${slotId}"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("RESTORE PROGRESS", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold))
                        }
                    }
                }
            }
        }

        // --- SECTION 4: JSON CONFIGURATOR ROSTER IMPORT EXPORT SANDBOX ---
        Text(
            text = "JSON ROSTER SEED & PORTABLE EDITOR",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GoldAccent,
                letterSpacing = 0.5.sp
            )
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, ColorCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Transfer wrestling data from custom JSON catalogs, or dump the active world registry to make manual modifications in any external text editor.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText, fontSize = 11.sp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.exportRoster { json ->
                                jsonText = json
                                importStatusMessage = "Roster blueprint exported successfully! Copy the JSON contents from the editor box below."
                                importSuccess = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark, contentColor = LightText),
                        border = BorderStroke(1.dp, ColorCardBorder),
                        modifier = Modifier.weight(1f).testTag("export_roster_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DUMP CURRENT SEED", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Button(
                        onClick = {
                            if (jsonText.isBlank()) {
                                importStatusMessage = "ValidationError: Field cannot be empty! Paste a valid wrestlers/gameState JSON block to compile."
                                importSuccess = false
                            } else {
                                viewModel.importRoster(jsonText) { success, msg ->
                                    importSuccess = success
                                    importStatusMessage = msg
                                    if (success) {
                                        refreshSlots()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark),
                        modifier = Modifier.weight(1f).testTag("import_roster_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("IMPORT PLATFORM DATA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold))
                    }
                }
                
                importStatusMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (importSuccess == true) ColorFace else ColorHeel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp).testTag("import_status_log")
                    )
                }

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("Custom Seed JSON Box", color = MutedText, fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .testTag("json_seed_text_field"),
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = LightText, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightText,
                        unfocusedTextColor = LightText,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ColorCardBorder,
                        focusedContainerColor = SlateOverlay,
                        unfocusedContainerColor = SlateOverlay
                    )
                )
            }
        }
    }

    // --- CONFIRMATION DIALOG MODAL ---
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = SlateCard,
            title = {
                Text(
                    text = when (confirmDialogType) {
                        "SAVE" -> "Overwrite Saved Data?"
                        "LOAD" -> "Restore Backup State?"
                        "SCENARIO" -> "Start Different Era?"
                        else -> "Confirm Operation"
                    },
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (confirmDialogType) {
                        "SAVE" -> "Are you sure you want to write your current active simulation records over Save Slot $confirmTargetSlot? This change is irreversible."
                        "LOAD" -> {
                            val slotLabel = if (confirmTargetSlot == -1) "Autosave" else "Save Slot $confirmTargetSlot"
                            "Are you sure you want to roll back the simulator to $slotLabel? Current progress will be overwritten."
                        }
                        "SCENARIO" -> "Starting a new scenario will completely wipe the existing simulation universe and seed starting rosters. Do you want to proceed?"
                        else -> "Do you want to confirm this database transfer?"
                    },
                    color = LightText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        when (confirmDialogType) {
                            "SAVE" -> {
                                viewModel.saveToSlot(confirmTargetSlot) { ok ->
                                    if (ok) {
                                        refreshSlots()
                                        android.widget.Toast.makeText(context, "Progress backed up cleanly inside Slot $confirmTargetSlot!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Backup failed: File write operation was rejected.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            "LOAD" -> {
                                val loadAction = if (confirmTargetSlot == -1) {
                                    { callback: (Boolean) -> Unit -> viewModel.loadAutosave(callback) }
                                } else {
                                    { callback: (Boolean) -> Unit -> viewModel.loadFromSlot(confirmTargetSlot, callback) }
                                }
                                loadAction { ok ->
                                    if (ok) {
                                        refreshSlots()
                                        android.widget.Toast.makeText(context, "State restored successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Restore failed: Data corruption detected.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            "SCENARIO" -> {
                                viewModel.runScenario(confirmTargetScenario) { ok ->
                                    if (ok) {
                                        refreshSlots()
                                        android.widget.Toast.makeText(context, "New Era launched successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Scenario launch failed.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SlateDark)
                ) {
                    Text("CONFIRM", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("CANCEL", color = MutedText, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        )
    }
}
