package com.example.repository

import com.example.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class GameRepository(initialDatabase: GameDatabase, private val context: android.content.Context) {

    private val currentDatabase = MutableStateFlow(initialDatabase)
    private val database: GameDatabase get() = currentDatabase.value

    val wrestlers: Flow<List<Wrestler>> = currentDatabase.flatMapLatest { it.wrestlerDao().getAllWrestlers() }
    val gameState: Flow<GameState?> = currentDatabase.flatMapLatest { it.gameStateDao().getGameState() }
    val draftMatches: Flow<List<DraftMatch>> = currentDatabase.flatMapLatest { it.draftMatchDao().getDraftMatches() }
    val inboxMessages: Flow<List<InboxMessage>> = currentDatabase.flatMapLatest { it.inboxMessageDao().getInboxMessages() }
    val showReports: Flow<List<ShowReport>> = currentDatabase.flatMapLatest { it.showReportDao().getAllShowReports() }
    val leagueStats: Flow<List<CompanyLeagueStats>> = currentDatabase.flatMapLatest { it.companyLeagueStatsDao().getAllLeagueStats() }

    val eraSnapshots: Flow<List<WrestlerEraSnapshot>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllEraSnapshots() }
    val contracts: Flow<List<Contract>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllContracts() }
    val injuryStatuses: Flow<List<InjuryStatus>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllInjuryStatuses() }
    val titles: Flow<List<Title>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllTitles() }
    val titleHolders: Flow<List<TitleHolder>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllTitleHolders() }
    val teamMemberships: Flow<List<TeamMembership>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllTeamMemberships() }
    val stableMemberships: Flow<List<StableMembership>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllStableMemberships() }
    val wrestlerRelationships: Flow<List<WrestlerRelationship>> = currentDatabase.flatMapLatest { it.rosterAdminDao().getAllWrestlerRelationships() }

    val negotiations: Flow<List<TransferNegotiation>> = currentDatabase.flatMapLatest { it.transferNegotiationDao().getAllNegotiations() }
    val scoutAssignments: Flow<List<ScoutAssignment>> = currentDatabase.flatMapLatest { it.scoutAssignmentDao().getAllAssignments() }
    val rumours: Flow<List<Rumour>> = currentDatabase.flatMapLatest { it.rumourDao().getAllRumours() }

    fun getEraSnapshotsForWrestler(wrestlerId: Int): Flow<List<WrestlerEraSnapshot>> = database.rosterAdminDao().getEraSnapshotsForWrestler(wrestlerId)

    suspend fun getWrestlersSync(): List<Wrestler> = database.wrestlerDao().getWrestlersSync()
    suspend fun getWrestlerById(id: Int): Wrestler? = database.wrestlerDao().getWrestlerById(id)
    suspend fun insertWrestler(wrestler: Wrestler) = database.wrestlerDao().insertWrestler(wrestler)
    suspend fun insertWrestlers(wrestlers: List<Wrestler>) = database.wrestlerDao().insertWrestlers(wrestlers)
    suspend fun updateWrestler(wrestler: Wrestler) = database.wrestlerDao().updateWrestler(wrestler)
    suspend fun deleteWrestler(wrestler: Wrestler) = database.wrestlerDao().deleteWrestler(wrestler)

    suspend fun getGameStateSync(): GameState? = database.gameStateDao().getGameStateSync()
    suspend fun updateGameState(state: GameState) = database.gameStateDao().updateGameState(state)
    suspend fun insertGameState(state: GameState) = database.gameStateDao().insertGameState(state)

    suspend fun getDraftMatchesSync(): List<DraftMatch> = database.draftMatchDao().getDraftMatchesSync()
    suspend fun updateDraftMatch(match: DraftMatch) = database.draftMatchDao().updateDraftMatch(match)
    suspend fun insertDraftMatches(matches: List<DraftMatch>) = database.draftMatchDao().insertDraftMatches(matches)
    suspend fun clearDraftMatches() = database.draftMatchDao().clearDraftMatches()

    suspend fun insertInboxMessage(message: InboxMessage) = database.inboxMessageDao().insertInboxMessage(message)
    suspend fun updateInboxMessage(message: InboxMessage) = database.inboxMessageDao().updateInboxMessage(message)
    suspend fun deleteInboxMessage(id: Int) = database.inboxMessageDao().deleteInboxMessage(id)

    suspend fun insertShowReport(report: ShowReport) = database.showReportDao().insertShowReport(report)

    suspend fun insertEraSnapshots(snapshots: List<WrestlerEraSnapshot>) = database.rosterAdminDao().insertEraSnapshots(snapshots)
    suspend fun insertContracts(contracts: List<Contract>) = database.rosterAdminDao().insertContracts(contracts)
    suspend fun insertInjuryStatuses(statuses: List<InjuryStatus>) = database.rosterAdminDao().insertInjuryStatuses(statuses)
    suspend fun insertTitles(titles: List<Title>) = database.rosterAdminDao().insertTitles(titles)
    suspend fun insertTitleHolders(holders: List<TitleHolder>) = database.rosterAdminDao().insertTitleHolders(holders)
    suspend fun insertTeamMemberships(teams: List<TeamMembership>) = database.rosterAdminDao().insertTeamMemberships(teams)
    suspend fun insertStableMemberships(stables: List<StableMembership>) = database.rosterAdminDao().insertStableMemberships(stables)
    suspend fun insertWrestlerRelationships(relations: List<WrestlerRelationship>) = database.rosterAdminDao().insertWrestlerRelationships(relations)

    suspend fun getLeagueStatsSync(): List<CompanyLeagueStats> = database.companyLeagueStatsDao().getAllLeagueStatsSync()
    suspend fun insertLeagueStats(stats: List<CompanyLeagueStats>) = database.companyLeagueStatsDao().insertLeagueStats(stats)
    suspend fun updateLeagueStats(stats: CompanyLeagueStats) = database.companyLeagueStatsDao().updateLeagueStats(stats)

    suspend fun getNegotiationsSync(): List<TransferNegotiation> = database.transferNegotiationDao().getNegotiationsSync()
    suspend fun insertNegotiation(negotiation: TransferNegotiation) = database.transferNegotiationDao().insertNegotiation(negotiation)
    suspend fun updateNegotiation(negotiation: TransferNegotiation) = database.transferNegotiationDao().updateNegotiation(negotiation)
    suspend fun deleteNegotiation(negotiation: TransferNegotiation) = database.transferNegotiationDao().deleteNegotiation(negotiation)

    suspend fun getScoutAssignmentsSync(): List<ScoutAssignment> = database.scoutAssignmentDao().getAssignmentsSync()
    suspend fun insertScoutAssignment(assignment: ScoutAssignment) = database.scoutAssignmentDao().insertAssignment(assignment)
    suspend fun updateScoutAssignment(assignment: ScoutAssignment) = database.scoutAssignmentDao().updateAssignment(assignment)
    suspend fun deleteScoutAssignment(assignment: ScoutAssignment) = database.scoutAssignmentDao().deleteAssignment(assignment)

    suspend fun insertRumour(rumour: Rumour) = database.rumourDao().insertRumour(rumour)
    suspend fun insertRumours(rumours: List<Rumour>) = database.rumourDao().insertRumours(rumours)
    suspend fun clearAllRumours() = database.rumourDao().clearAllRumours()

    /**
     * Seed initial simulation database values if missing.
     */
    suspend fun seedInitialDataIfNeeded() {
        val existingState = getGameStateSync()
        if (existingState == null) {
            // Seed default league stats
            insertLeagueStats(com.example.domain.LeagueEngine.createDefaultStats(1))

            // 1. Core game stats
            val startState = GameState(
                id = 1,
                companyName = "Apex Pro Wrestling",
                cash = 300000.0,
                prestige = 60,
                fanbase = 25000,
                currentWeek = 1,
                currentShowName = "Apex Showcase",
                showStyle = "Mainstream",
                scoutingLevel = 1,
                lastShowRating = 0
            )
            database.gameStateDao().insertGameState(startState)

            // 2. Draft slots (4 matches per event card)
            val draftSlots = listOf(
                DraftMatch(id = 1),
                DraftMatch(id = 2),
                DraftMatch(id = 3),
                DraftMatch(id = 4)
            )
            database.draftMatchDao().insertDraftMatches(draftSlots)

            // 3. Seed active promotion roster (12 wrestlers of varying alignments & styles)
            val seeds = listOf(
                Wrestler(id = 1, name = "John Steel", realName = "Johnathon Sullivan", companyId = 1, popularity = 82, inRingSkill = 75, micSkill = 88, stamina = 95, morale = 90, heelFace = "FACE", salary = 4200.0, isContracted = true, style = "Showman", traits = "Charismatic Leader, Resilient", finisher = "The Steel Hammer", age = 29),
                Wrestler(id = 2, name = "Titan Maverick", realName = "Marcus Vance", companyId = 1, popularity = 85, inRingSkill = 90, micSkill = 70, stamina = 100, morale = 85, heelFace = "HEEL", salary = 4800.0, isContracted = true, style = "Brawler", traits = "Ruthless, Powerhouse", finisher = "Maverick Bomb", age = 32),
                Wrestler(id = 3, name = "Kage Vance", realName = "Kagehiro Watanabe", companyId = 1, popularity = 70, inRingSkill = 85, micSkill = 60, stamina = 90, morale = 95, heelFace = "HEEL", salary = 2800.0, isContracted = true, style = "Technician", traits = "Technical Expert, Cold-Clipped", finisher = "Kage Lock", age = 25),
                Wrestler(id = 4, name = "Alex Breeze", realName = "Alexander Wind", companyId = 1, popularity = 75, inRingSkill = 88, micSkill = 80, stamina = 95, morale = 98, heelFace = "FACE", salary = 3100.0, isContracted = true, style = "High Flyer", traits = "Acrobatic, Crowd Pleaser", finisher = "Summer Breeze", age = 24),
                Wrestler(id = 5, name = "El Dragone", realName = "Eduardo Ramirez", companyId = 1, popularity = 68, inRingSkill = 92, micSkill = 50, stamina = 88, morale = 90, heelFace = "FACE", salary = 2500.0, isContracted = true, style = "High Flyer", traits = "High-Risk Specialist", finisher = "Dragon Fire Splash", age = 22),
                Wrestler(id = 6, name = "Rex Gung", realName = "Reginald Gunning", companyId = 1, popularity = 79, inRingSkill = 72, micSkill = 85, stamina = 92, morale = 88, heelFace = "HEEL", salary = 3500.0, isContracted = true, style = "Brawler", traits = "Loudmouth, Hostile", finisher = "Outlaw Lasso", age = 34),
                Wrestler(id = 7, name = "Serena Swift", realName = "Serena Reynolds", companyId = 1, popularity = 80, inRingSkill = 86, micSkill = 82, stamina = 96, morale = 92, heelFace = "FACE", salary = 3800.0, isContracted = true, style = "Technician", gender = "Female", traits = "Fan Favorite, Speedster", finisher = "Swift Submission", age = 27),
                Wrestler(id = 8, name = "Vipera", realName = "Valerie Thorne", companyId = 1, popularity = 78, inRingSkill = 82, micSkill = 84, stamina = 90, morale = 78, heelFace = "HEEL", salary = 3400.0, isContracted = true, style = "Showman", gender = "Female", traits = "Sly tactician, Merciless", finisher = "Venom Strike", age = 28),
                Wrestler(id = 9, name = "Mitch Dynamo", realName = "Mitchell Hall", companyId = 1, popularity = 62, inRingSkill = 78, micSkill = 65, stamina = 85, morale = 100, heelFace = "FACE", salary = 1800.0, isContracted = true, style = "High Flyer", traits = "Young Blood, High Energy", finisher = "Dynamo Drop", age = 21, activeStatus = "Prospect"),
                Wrestler(id = 10, name = "Crusher Cobb", realName = "William Cobb", companyId = 1, popularity = 66, inRingSkill = 60, micSkill = 55, stamina = 80, morale = 82, heelFace = "HEEL", salary = 1500.0, isContracted = true, style = "Brawler", traits = "Hard Headed, Gatekeeper", finisher = "Cobb Crusher", age = 30),
                Wrestler(id = 11, name = "Christian Zenith", realName = "Christian Grey", companyId = 1, popularity = 72, inRingSkill = 94, micSkill = 75, stamina = 90, morale = 95, heelFace = "FACE", salary = 3200.0, isContracted = true, style = "Technician", traits = "Perfectionist, Workhorse", finisher = "Zenith Edge", age = 26),
                Wrestler(id = 12, name = "Damian Sledge", realName = "Damian Sledge", companyId = 1, popularity = 76, inRingSkill = 70, micSkill = 90, stamina = 85, morale = 85, heelFace = "HEEL", salary = 3600.0, isContracted = true, style = "Showman", traits = "Egotistical, Loud", finisher = "Sledgehammer", age = 31)
            )
            database.wrestlerDao().insertWrestlers(seeds)

            // 4. Seed Freelancers / Marketplace (8 Free Agents to hire)
            val freeAgents = listOf(
                Wrestler(id = 13, name = "Rick Flair-like", realName = "Richard Fliehr", companyId = 0, popularity = 88, inRingSkill = 92, micSkill = 98, stamina = 90, morale = 75, heelFace = "HEEL", salary = 8500.0, isContracted = false, style = "Showman", traits = "Legendary, Golden Tongue", finisher = "Figure-Four Lock", age = 45),
                Wrestler(id = 14, name = "Bryan Golden", realName = "Bryan Danielson-like", companyId = 0, popularity = 81, inRingSkill = 98, micSkill = 82, stamina = 100, morale = 100, heelFace = "FACE", salary = 7000.0, isContracted = false, style = "Technician", traits = "Master Submissionist", finisher = "Cattle Mutilation", age = 38),
                Wrestler(id = 15, name = "Goliath Gargant", realName = "Gregory Gigantus", companyId = 0, popularity = 74, inRingSkill = 55, micSkill = 68, stamina = 70, morale = 80, heelFace = "HEEL", salary = 4000.0, isContracted = false, style = "Brawler", traits = "Behemoth, Threatening", finisher = "Goliath Chokeslam", age = 35),
                Wrestler(id = 16, name = "Aero Hawk", realName = "Takeshi Falcon", companyId = 0, popularity = 65, inRingSkill = 84, micSkill = 70, stamina = 92, morale = 90, heelFace = "FACE", salary = 2900.0, isContracted = false, style = "High Flyer", traits = "Daredevil, Honorable", finisher = "Falcon Arrow Flight", age = 23, activeStatus = "Prospect"),
                Wrestler(id = 17, name = "Tessa Valk", realName = "Tessa Valkyrie", companyId = 0, popularity = 73, inRingSkill = 88, micSkill = 78, stamina = 95, morale = 85, heelFace = "FACE", salary = 3300.0, isContracted = false, style = "Technician", gender = "Female", traits = "Fierce Competitor", finisher = "Valkyrie Drop", age = 25),
                Wrestler(id = 18, name = "Buster Bruiser", realName = "Buster McAlister", companyId = 0, popularity = 58, inRingSkill = 62, micSkill = 50, stamina = 80, morale = 90, heelFace = "HEEL", salary = 1200.0, isContracted = false, style = "Brawler", traits = "Lover of Fights", finisher = "Buster Boot", age = 27),
                Wrestler(id = 19, name = "Starlight Kid", realName = "Ayumi Sato", companyId = 0, popularity = 68, inRingSkill = 86, micSkill = 64, stamina = 88, morale = 92, heelFace = "FACE", salary = 2500.0, isContracted = false, style = "High Flyer", gender = "Female", traits = "High Speed Acrobat", finisher = "Starlight Moonsault", age = 20, activeStatus = "Prospect"),
                Wrestler(id = 20, name = "Scythe", realName = "Unknown Reaper", companyId = 0, popularity = 80, inRingSkill = 75, micSkill = 85, stamina = 85, morale = 70, heelFace = "HEEL", salary = 5900.0, isContracted = false, style = "Showman", traits = "Mysthetic, Chilling", finisher = "Scythe Kick", age = 33)
            )
            database.wrestlerDao().insertWrestlers(freeAgents)

            // Seed Rival Competitor Rosters
            val rivalWrestlers = listOf(
                // GWG (companyId = 2)
                Wrestler(id = 21, name = "Ace Archer", realName = "Ace Archer", companyId = 2, popularity = 86, inRingSkill = 84, micSkill = 90, stamina = 95, morale = 88, heelFace = "FACE", salary = 7500.0, isContracted = false, style = "Showman", traits = "World Star, Confident", finisher = "Archer Arrow", age = 28, loyalty = 85, interestLevel = 45),
                Wrestler(id = 22, name = "Midnight Shadow", realName = "Kenjiro Sato", companyId = 2, popularity = 72, inRingSkill = 92, micSkill = 55, stamina = 98, morale = 82, heelFace = "HEEL", salary = 3200.0, isContracted = false, style = "High Flyer", traits = "Silent Assassin, Ninja Style", finisher = "Ninjutsu Drop", age = 24, loyalty = 60, interestLevel = 75),
                Wrestler(id = 23, name = "Thunder Gung", realName = "Thomas Gunning", companyId = 2, popularity = 78, inRingSkill = 75, micSkill = 82, stamina = 90, morale = 80, heelFace = "HEEL", salary = 4800.0, isContracted = false, style = "Brawler", traits = "Aggressive Powerhouse", finisher = "Thunder Slam", age = 33, loyalty = 75, interestLevel = 50),
                
                // GPW (companyId = 3)
                Wrestler(id = 24, name = "Sterling Sterling", realName = "Robert Sterling", companyId = 3, popularity = 83, inRingSkill = 82, micSkill = 94, stamina = 92, morale = 85, heelFace = "HEEL", salary = 6800.0, isContracted = false, style = "Showman", traits = "Golden Heir, Wealthy Elite", finisher = "Sterling Sovereign", age = 30, loyalty = 90, interestLevel = 35),
                Wrestler(id = 25, name = "Desert Viper", realName = "Sabit Al-Jamil", companyId = 3, popularity = 75, inRingSkill = 78, micSkill = 72, stamina = 88, morale = 76, heelFace = "HEEL", salary = 3600.0, isContracted = false, style = "Brawler", traits = "Cruel, Tactical Wrestler", finisher = "Viper Squeeze", age = 35, loyalty = 50, interestLevel = 80),
                Wrestler(id = 26, name = "Nova Sparks", realName = "Nova Sparks", companyId = 3, popularity = 71, inRingSkill = 88, micSkill = 78, stamina = 96, morale = 94, heelFace = "FACE", salary = 2800.0, isContracted = false, style = "High Flyer", traits = "Acrobatic, High Velocity", finisher = "Supernova Press", age = 22, loyalty = 40, interestLevel = 85),

                // TWA (companyId = 4)
                Wrestler(id = 27, name = "Ryu Hayabusa", realName = "Ryu Hayabusa", companyId = 4, popularity = 82, inRingSkill = 98, micSkill = 65, stamina = 94, morale = 90, heelFace = "FACE", salary = 5800.0, isContracted = false, style = "Technician", traits = "Puroresu Legend, Honor Bound", finisher = "Hayabusa Driver", age = 36, loyalty = 95, interestLevel = 40),
                Wrestler(id = 28, name = "Akira Kushida", realName = "Akira Kushida", companyId = 4, popularity = 66, inRingSkill = 85, micSkill = 62, stamina = 92, morale = 92, heelFace = "FACE", salary = 1600.0, isContracted = false, style = "Technician", traits = "Young Master, High Focus", finisher = "Akira Lock", age = 21, loyalty = 80, interestLevel = 90),
                Wrestler(id = 29, name = "Yuri Jin", realName = "Yuri Jin", companyId = 4, popularity = 76, inRingSkill = 94, micSkill = 68, stamina = 90, morale = 84, heelFace = "FACE", salary = 3400.0, isContracted = false, style = "Technician", gender = "Female", traits = "Submission Queen, Strong Style", finisher = "Yuri Armbar", age = 25, loyalty = 70, interestLevel = 65)
            )
            database.wrestlerDao().insertWrestlers(rivalWrestlers)

            // Seed initial rumors
            val seedRumours = listOf(
                Rumour(text = "Ace Archer of GWG tells reporters he is 'completely focused' on GWG and has zero interest in a buyout.", weekGenerated = 1),
                Rumour(text = "RUMOUR: Nova Sparks is reportedly frustrated over mid-card booking in GPW and is seeking an early departure.", weekGenerated = 1),
                Rumour(text = "Free Agent veteran Rick Flair-like is spotted backstage at Apex Pro Wrestling. Is a blockbuster signing imminent?", weekGenerated = 1),
                Rumour(text = "Tokyo Wrestling Alliance is reportedly scout-gathering prospective talent from the Americas' freelance markets.", weekGenerated = 1)
            )
            database.rumourDao().insertRumours(seedRumours)

            // 5. Seed secondary administrative entities with realistic data
            val seedTitles = listOf(
                Title(id = 1, titleName = "World Heavyweight Championship", prestige = 90, division = "World Heavyweight"),
                Title(id = 2, titleName = "Apex Television Championship", prestige = 75, division = "Midcard"),
                Title(id = 3, titleName = "Tag Team Championship Elite", prestige = 80, division = "Tag Team"),
                Title(id = 4, titleName = "Women's Championship Trophy", prestige = 78, division = "Women's")
            )
            database.rosterAdminDao().insertTitles(seedTitles)

            val seedHolders = listOf(
                TitleHolder(titleId = 1, titleName = "World Heavyweight Championship", wrestlerId = 1, wrestlerName = "John Steel", wonWeek = 1, daysHeld = 120, successfulDefenses = 4),
                TitleHolder(titleId = 2, titleName = "Apex Television Championship", wrestlerId = 6, wrestlerName = "Rex Gung", wonWeek = 1, daysHeld = 30, successfulDefenses = 1),
                TitleHolder(titleId = 3, titleName = "Tag Team Championship Elite", wrestlerId = 4, wrestlerName = "Alex Breeze & Mitch Dynamo", wonWeek = 1, daysHeld = 200, successfulDefenses = 8),
                TitleHolder(titleId = 4, titleName = "Women's Championship Trophy", wrestlerId = 7, wrestlerName = "Serena Swift", wonWeek = 1, daysHeld = 45, successfulDefenses = 2)
            )
            database.rosterAdminDao().insertTitleHolders(seedHolders)

            val seedTeams = listOf(
                TeamMembership(teamName = "Midnight Express", wrestler1Id = 4, wrestler1Name = "Alex Breeze", wrestler2Id = 9, wrestler2Name = "Mitch Dynamo", activeStatus = true, experience = 95, finisher = "Midnight Flight"),
                TeamMembership(teamName = "The Demolition Force", wrestler1Id = 10, wrestler1Name = "Crusher Cobb", wrestler2Id = 12, wrestler2Name = "Damian Sledge", activeStatus = true, experience = 80, finisher = "Demolition Slam"),
                TeamMembership(teamName = "Higher Flyers", wrestler1Id = 5, wrestler1Name = "El Dragone", wrestler2Id = 9, wrestler2Name = "Mitch Dynamo", activeStatus = false, experience = 60, finisher = "Double Dragon Splash")
            )
            database.rosterAdminDao().insertTeamMemberships(seedTeams)

            val seedStables = listOf(
                StableMembership(stableName = "The Elite Syndicate", leaderId = 1, leaderName = "John Steel", memberIdsRaw = "1,4,9,11", memberNames = "John Steel, Alex Breeze, Mitch Dynamo, Christian Zenith", activeStatus = true, influence = 88),
                StableMembership(stableName = "House of Pain", leaderId = 6, leaderName = "Rex Gung", memberIdsRaw = "6,10,12", memberNames = "Rex Gung, Crusher Cobb, Damian Sledge", activeStatus = true, influence = 75)
            )
            database.rosterAdminDao().insertStableMemberships(seedStables)

            val seedRelations = listOf(
                WrestlerRelationship(wrestler1Id = 1, wrestler1Name = "John Steel", wrestler2Id = 2, wrestler2Name = "Titan Maverick", relationType = "Rival", heatLevel = 92),
                WrestlerRelationship(wrestler1Id = 4, wrestler1Name = "Alex Breeze", wrestler2Id = 9, wrestler2Name = "Mitch Dynamo", relationType = "Ally", heatLevel = 85),
                WrestlerRelationship(wrestler1Id = 7, wrestler1Name = "Serena Swift", wrestler2Id = 8, wrestler2Name = "Vipera", relationType = "Rival", heatLevel = 78),
                WrestlerRelationship(wrestler1Id = 6, wrestler1Name = "Rex Gung", wrestler2Id = 12, wrestler2Name = "Damian Sledge", relationType = "Ally", heatLevel = 65)
            )
            database.rosterAdminDao().insertWrestlerRelationships(seedRelations)

            val seedContracts = listOf(
                Contract(wrestlerId = 1, companyName = "Apex Pro Wrestling", weeklySalary = 4200.0, weeksRemaining = 52, moraleClause = true, roleClause = "Main Event Star"),
                Contract(wrestlerId = 2, companyName = "Apex Pro Wrestling", weeklySalary = 4800.0, weeksRemaining = 38, moraleClause = false, roleClause = "Heel Attraction"),
                Contract(wrestlerId = 3, companyName = "Apex Pro Wrestling", weeklySalary = 2800.0, weeksRemaining = 24, moraleClause = true, roleClause = "Upper Midcarder"),
                Contract(wrestlerId = 4, companyName = "Apex Pro Wrestling", weeklySalary = 3100.0, weeksRemaining = 18, moraleClause = false, roleClause = "Tag Team Specialist"),
                Contract(wrestlerId = 5, companyName = "Apex Pro Wrestling", weeklySalary = 2500.0, weeksRemaining = 40, moraleClause = false, roleClause = "High Flyer Challenger"),
                Contract(wrestlerId = 6, companyName = "Apex Pro Wrestling", weeklySalary = 3500.0, weeksRemaining = 12, moraleClause = true, roleClause = "Midcard Champion"),
                Contract(wrestlerId = 7, companyName = "Apex Pro Wrestling", weeklySalary = 3800.0, weeksRemaining = 45, moraleClause = true, roleClause = "Women's Leader"),
                Contract(wrestlerId = 8, companyName = "Apex Pro Wrestling", weeklySalary = 3400.0, weeksRemaining = 50, moraleClause = false, roleClause = "Heel Contender"),
                Contract(wrestlerId = 9, companyName = "Apex Pro Wrestling", weeklySalary = 1800.0, weeksRemaining = 8,  moraleClause = false, roleClause = "Young Prospect"),
                Contract(wrestlerId = 10, companyName = "Apex Pro Wrestling", weeklySalary = 1500.0, weeksRemaining = 14, moraleClause = false, roleClause = "Enforcer"),
                Contract(wrestlerId = 11, companyName = "Apex Pro Wrestling", weeklySalary = 3200.0, weeksRemaining = 22, moraleClause = true, roleClause = "Workhorse Technician"),
                Contract(wrestlerId = 12, companyName = "Apex Pro Wrestling", weeklySalary = 3600.0, weeksRemaining = 3,  moraleClause = false, roleClause = "Showman Contender"),
                
                // Rival Promotions Contracts
                Contract(wrestlerId = 21, companyName = "Giga-Wrestling Global", weeklySalary = 7500.0, weeksRemaining = 40, roleClause = "Main Event Star"),
                Contract(wrestlerId = 22, companyName = "Giga-Wrestling Global", weeklySalary = 3200.0, weeksRemaining = 15, roleClause = "Tag Team Specialist"),
                Contract(wrestlerId = 23, companyName = "Giga-Wrestling Global", weeklySalary = 4800.0, weeksRemaining = 24, roleClause = "Upper Midcarder"),
                Contract(wrestlerId = 24, companyName = "Global Pro Wrestling", weeklySalary = 6800.0, weeksRemaining = 32, roleClause = "Main Event Star"),
                Contract(wrestlerId = 25, companyName = "Global Pro Wrestling", weeklySalary = 3600.0, weeksRemaining = 8, roleClause = "Midcard Champion"),
                Contract(wrestlerId = 26, companyName = "Global Pro Wrestling", weeklySalary = 2800.0, weeksRemaining = 48, roleClause = "High Flyer Challenger"),
                Contract(wrestlerId = 27, companyName = "Tokyo Wrestling Alliance", weeklySalary = 5800.0, weeksRemaining = 12, roleClause = "Main Event Star"),
                Contract(wrestlerId = 28, companyName = "Tokyo Wrestling Alliance", weeklySalary = 1600.0, weeksRemaining = 50, roleClause = "Young Prospect"),
                Contract(wrestlerId = 29, companyName = "Tokyo Wrestling Alliance", weeklySalary = 3400.0, weeksRemaining = 28, roleClause = "Workhorse Technician")
            )
            database.rosterAdminDao().insertContracts(seedContracts)

            val seedInjuries = listOf(
                InjuryStatus(wrestlerId = 9, injuryType = "Minor Rib Strain", severity = "Minor", weeksRemaining = 0, rehabActivity = "Light gym rehabbing"),
                InjuryStatus(wrestlerId = 8, injuryType = "Sprained Ankle", severity = "Minor", weeksRemaining = 0, rehabActivity = "Massage therapy done"),
                InjuryStatus(wrestlerId = 20, injuryType = "Fractured Jaw", severity = "Major", weeksRemaining = 3, rehabActivity = "Resting with wire jaw")
            )
            database.rosterAdminDao().insertInjuryStatuses(seedInjuries)

            val seedSnapshots = listOf(
                WrestlerEraSnapshot(wrestlerId = 1, yearOrEraKey = "Era of Gold (2025)", company = "APW", pushLevel = "Main Eventer", role = "Babyface Champion", activeTitles = "World Heavyweight Championship", allies = "Alex Breeze, Christian Zenith", rivals = "Titan Maverick, Rex Gung", tagPartners = "None", manager = "None", notes = "Carried Apex television rating scores up by 15% through high-impact face-vs-heel matches."),
                WrestlerEraSnapshot(wrestlerId = 2, yearOrEraKey = "Ruthless Aggression Era (2024)", company = "GPW", pushLevel = "Upper Midcard", role = "Monster Heel", activeTitles = "Apex Television Championship", allies = "Rex Gung", rivals = "John Steel", tagPartners = "None", manager = "Vipera", notes = "Voted heel of the year by WrestlePulse readers after cage match destruction of El Dragone."),
                WrestlerEraSnapshot(wrestlerId = 3, yearOrEraKey = "Technical Revolution (2024)", company = "GPW", pushLevel = "Upper Midcard", role = "Technician", activeTitles = "None", allies = "El Dragone", rivals = "John Steel", tagPartners = "None", manager = "None", notes = "Showcased spectacular submission work throughout the Tokyo Grand Prix.")
            )
            database.rosterAdminDao().insertEraSnapshots(seedSnapshots)

            // 6. Seed diverse Football Manager-style emails across multiple categories
            val seedMails = listOf(
                InboxMessage(
                    subject = "Welcome to WrestleGM Pro!",
                    sender = "Board of Directors",
                    body = "Welcome Commissioner!\n\nYou have been chosen to lead Apex Pro Wrestling to global domination. Your objectives are simple:\n\n1. Maintain financial profitability — stay in the black! Pay attention to weekly payroll vs event ticket sales.\n2. Book compelling show cards using your outstanding roster. Selecting appropriate match types, Heels vs Faces, and highly skilled workers ensures outstanding event ratings.\n3. Hire elite Free Agents and manage backstage morale.\n\nGood luck! Your first show is scheduled for this Friday.",
                    weekReceived = 1,
                    isRead = false,
                    type = "NEWS"
                ),
                InboxMessage(
                    subject = "Medical Progress: Mitch Dynamo",
                    sender = "Dr. Keith (Chief Medical Liaison)",
                    body = "Commissioner, Mitch Dynamo is recovering well from his minor rib strain. He is progressing nicely during lightweight gym sessions. Ensure he doesn't work high-risk stipulations (like Steel Cage or Ladder) in the immediate matches to prevent worsening.",
                    weekReceived = 1,
                    isRead = false,
                    type = "COMPLAINT" // injury category
                ),
                InboxMessage(
                    subject = "External Inquiry: John Steel",
                    sender = "Tokyo Wrestling Alliance (TWA)",
                    body = "Dear Commissioner, TWA is scouting elite Showmen. Our board is preparing a contract buyout packet for John Steel. They are prepared to offer \$75,000 for immediate release. Do you want to enter transfer negotiations, or lock John into a secure extension?",
                    weekReceived = 1,
                    isRead = false,
                    type = "OFFER" // transfer offer category
                ),
                InboxMessage(
                    subject = "Frustration in the ranks: Vipera",
                    sender = "Vipera (Locker Room Liaison)",
                    body = "Commissioner, our talent Vipera is unhappy. She has expressed concerns that her status is undervalued compared to local faces. She demands more promo times on Friday Nights to re-establish her prestige, or her morale will continue to decay.",
                    weekReceived = 1,
                    isRead = false,
                    type = "COMPLAINT" // morale concern category
                ),
                InboxMessage(
                    subject = "Industry Watch: Giga-Wrestling Global",
                    sender = "Dave Melts (WrestlePulse)",
                    body = "Rival promotion GWG signed 2 new TV agreements last night, expanding their domestic viewing index. Their brand prestige has surged to 75%. If we don't increase our show ratings, our fans might transition to their alternative network.",
                    weekReceived = 1,
                    isRead = false,
                    type = "NEWS" // rival company activity category
                ),
                InboxMessage(
                    subject = "Broadcast Rating Index: Friday Night Apex",
                    sender = "Neilson Media Metrics",
                    body = "The official television reports are in for our show. The broadcast capped off with a solid 1.83 TV rating, indicating high interest in our ongoing storylines. This outperforms our initial benchmark ratings by 12%!",
                    weekReceived = 1,
                    isRead = false,
                    type = "REPORT" // weekly TV ratings category
                ),
                InboxMessage(
                    subject = "Upcoming Expiry Warning: Damian Sledge",
                    sender = "HR Payroll Specialist",
                    body = "Warning: Damian Sledge's contract expires in 3 weeks. If you do not offer him a renewed contract, he will declare free agency and seek alternative promotion teams. Address this term immediately!",
                    weekReceived = 1,
                    isRead = false,
                    type = "OFFER" // contract issue category
                ),
                InboxMessage(
                    subject = "Quarterly Board Objectives",
                    sender = "Mr. Apex (CEO)",
                    body = "Welcome Commissioner. The board has defined three major objectives for your first season:\n\n1. Maintain liquidity above \$150,000.\n2. Achieve a show rating scorecard of 75% or higher within the first 4 weeks.\n3. Keep overall contracted staff morale above 80%.\n\nFulfil these milestones to secure your employment extension.",
                    weekReceived = 1,
                    isRead = false,
                    type = "SPONSOR" // board/company objectives category
                )
            )
            for (mail in seedMails) {
                database.inboxMessageDao().insertInboxMessage(mail)
            }
        }
    }

    // --- SAVE AND DATABASE ENGINE OPERATIONS ---

    data class SlotInfo(
        val slotId: Int,
        val exists: Boolean,
        val companyName: String = "",
        val currentWeek: Int = 1,
        val cash: Double = 0.0,
        val timestamp: Long = 0L
    )

    private fun copyFile(src: java.io.File, dest: java.io.File) {
        if (!src.exists()) return
        try {
            dest.parentFile?.mkdirs()
            src.inputStream().use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            java.io.File(dest.absolutePath + "-wal").delete()
            java.io.File(dest.absolutePath + "-shm").delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveToSlot(slotId: Int): Boolean {
        return try {
            val dbName = "wrestle_simulator_save.db"
            val slotName = "wrestle_simulator_slot_$slotId.db"
            
            val activeDb = currentDatabase.value
            try {
                activeDb.openHelper.writableDatabase.query("PRAGMA checkpoint(FULL)")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            activeDb.close()
            
            val srcFile = context.getDatabasePath(dbName)
            val destFile = context.getDatabasePath(slotName)
            
            copyFile(srcFile, destFile)
            
            val reopenedDb = androidx.room.Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                dbName
            ).fallbackToDestructiveMigration().build()
            
            currentDatabase.value = reopenedDb
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadFromSlot(slotId: Int): Boolean {
        return try {
            val dbName = "wrestle_simulator_save.db"
            val slotName = "wrestle_simulator_slot_$slotId.db"
            
            val srcFile = context.getDatabasePath(slotName)
            val destFile = context.getDatabasePath(dbName)
            
            if (!srcFile.exists()) return false
            
            currentDatabase.value.close()
            
            copyFile(srcFile, destFile)
            
            val reopenedDb = androidx.room.Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                dbName
            ).fallbackToDestructiveMigration().build()
            
            currentDatabase.value = reopenedDb
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun saveAutosave(): Boolean {
        return try {
            val dbName = "wrestle_simulator_save.db"
            val autosaveName = "wrestle_simulator_autosave.db"
            
            val activeDb = currentDatabase.value
            try {
                activeDb.openHelper.writableDatabase.query("PRAGMA checkpoint(FULL)")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            activeDb.close()
            
            val srcFile = context.getDatabasePath(dbName)
            val destFile = context.getDatabasePath(autosaveName)
            
            copyFile(srcFile, destFile)
            
            val reopenedDb = androidx.room.Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                dbName
            ).fallbackToDestructiveMigration().build()
            
            currentDatabase.value = reopenedDb
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadAutosave(): Boolean {
        return try {
            val dbName = "wrestle_simulator_save.db"
            val autosaveName = "wrestle_simulator_autosave.db"
            
            val srcFile = context.getDatabasePath(autosaveName)
            val destFile = context.getDatabasePath(dbName)
            
            if (!srcFile.exists()) return false
            
            currentDatabase.value.close()
            
            copyFile(srcFile, destFile)
            
            val reopenedDb = androidx.room.Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                dbName
            ).fallbackToDestructiveMigration().build()
            
            currentDatabase.value = reopenedDb
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getSlotInfo(slotId: Int): SlotInfo {
        val fileName = if (slotId == -1) "wrestle_simulator_autosave.db" else "wrestle_simulator_slot_$slotId.db"
        val file = context.getDatabasePath(fileName)
        if (!file.exists()) {
            return SlotInfo(slotId = slotId, exists = false)
        }
        return try {
            val tempDb = androidx.room.Room.databaseBuilder(
                context,
                GameDatabase::class.java,
                fileName
            ).fallbackToDestructiveMigration().build()
            
            val state = tempDb.gameStateDao().getGameStateSync()
            tempDb.close()
            
            if (state != null) {
                SlotInfo(
                    slotId = slotId,
                    exists = true,
                    companyName = state.companyName,
                    currentWeek = state.currentWeek,
                    cash = state.cash,
                    timestamp = file.lastModified()
                )
            } else {
                SlotInfo(slotId = slotId, exists = false)
            }
        } catch (e: java.lang.Exception) {
            SlotInfo(slotId = slotId, exists = false)
        }
    }

    suspend fun startNewGame(scenarioKey: String): Boolean {
        return try {
            currentDatabase.value.clearAllTables()
            seedScenario(scenarioKey)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun seedScenario(scenarioKey: String) {
        val activeDb = currentDatabase.value
        
        // 1. Setup League Stats default
        activeDb.companyLeagueStatsDao().insertLeagueStats(com.example.domain.LeagueEngine.createDefaultStats(1))
        
        // 2. Draft Slots
        val draftSlots = listOf(
            DraftMatch(id = 1),
            DraftMatch(id = 2),
            DraftMatch(id = 3),
            DraftMatch(id = 4)
        )
        activeDb.draftMatchDao().insertDraftMatches(draftSlots)

        when (scenarioKey) {
            "ruthless" -> {
                val state = GameState(
                    id = 1,
                    companyName = "Apex Ruthless Wrestling",
                    cash = 500000.0,
                    prestige = 75,
                    fanbase = 65000,
                    currentWeek = 1,
                    currentShowName = "Apex WARZONE",
                    showStyle = "Hardcore",
                    scoutingLevel = 2,
                    physicianLevel = 1,
                    scoutLevel = 1,
                    creativeLevel = 2
                )
                activeDb.gameStateDao().insertGameState(state)

                val wrestlers = listOf(
                    Wrestler(id = 1, name = "Titan Maverick", realName = "Marcus Vance", companyId = 1, popularity = 92, inRingSkill = 88, micSkill = 85, stamina = 98, morale = 90, heelFace = "HEEL", salary = 6000.0, isContracted = true, style = "Brawler", traits = "Ruthless Champion", finisher = "Maverick Bomb", age = 28),
                    Wrestler(id = 2, name = "John Steel", realName = "Johnathon Sullivan", companyId = 1, popularity = 85, inRingSkill = 80, micSkill = 90, stamina = 95, morale = 88, heelFace = "FACE", salary = 5000.0, isContracted = true, style = "Showman", traits = "Charismatic", finisher = "Steel Hammer", age = 25),
                    Wrestler(id = 3, name = "Buster Bruiser", realName = "Buster McAlister", companyId = 1, popularity = 80, inRingSkill = 75, micSkill = 70, stamina = 90, morale = 92, heelFace = "HEEL", salary = 3500.0, isContracted = true, style = "Brawler", traits = "Lover of Fights", finisher = "Buster Boot", age = 27),
                    Wrestler(id = 4, name = "Goliath Gargant", realName = "Gregory Gigantus", companyId = 1, popularity = 82, inRingSkill = 60, micSkill = 72, stamina = 85, morale = 85, heelFace = "HEEL", salary = 4500.0, isContracted = true, style = "Brawler", traits = "Behemoth", finisher = "Goliath Chokeslam", age = 30),
                    Wrestler(id = 5, name = "Scythe", realName = "Unknown Reaper", companyId = 1, popularity = 84, inRingSkill = 82, micSkill = 88, stamina = 90, morale = 84, heelFace = "HEEL", salary = 5500.0, isContracted = true, style = "Showman", traits = "Chilling Aura", finisher = "Scythe Kick", age = 31),
                    Wrestler(id = 6, name = "Alex Breeze", realName = "Alexander Wind", companyId = 1, popularity = 70, inRingSkill = 85, micSkill = 76, stamina = 92, morale = 95, heelFace = "FACE", salary = 2800.0, isContracted = true, style = "High Flyer", finisher = "Summer Breeze", age = 22),
                    Wrestler(id = 7, name = "Serena Swift", realName = "Serena Reynolds", companyId = 1, popularity = 82, inRingSkill = 88, micSkill = 84, stamina = 95, morale = 94, heelFace = "FACE", salary = 4000.0, isContracted = true, style = "Technician", gender = "Female", finisher = "Swift Submission", age = 26),
                    Wrestler(id = 8, name = "Vipera", realName = "Valerie Thorne", companyId = 1, popularity = 79, inRingSkill = 80, micSkill = 82, stamina = 90, morale = 80, heelFace = "HEEL", salary = 3200.0, isContracted = true, style = "Showman", gender = "Female", finisher = "Venom Strike", age = 27),
                    Wrestler(id = 9, name = "Rex Gung", realName = "Reginald Gunning", companyId = 1, popularity = 75, inRingSkill = 70, micSkill = 80, stamina = 88, morale = 84, heelFace = "HEEL", salary = 3000.0, isContracted = true, style = "Brawler", finisher = "Outlaw Lasso", age = 36),
                    Wrestler(id = 10, name = "Crusher Cobb", realName = "William Cobb", companyId = 1, popularity = 72, inRingSkill = 65, micSkill = 60, stamina = 85, morale = 85, heelFace = "HEEL", salary = 1800.0, isContracted = true, style = "Brawler", finisher = "Cobb Crusher", age = 29)
                )
                activeDb.wrestlerDao().insertWrestlers(wrestlers)

                seedCompetitorsAndAgents(activeDb)
                
                activeDb.rosterAdminDao().insertTitles(listOf(
                    Title(id = 1, titleName = "Apex Ruthless Heavyweight Title", prestige = 85, division = "World Heavyweight"),
                    Title(id = 2, titleName = "Zone Hardcore Title", prestige = 70, division = "Midcard")
                ))
                activeDb.rosterAdminDao().insertTitleHolders(listOf(
                    TitleHolder(titleId = 1, titleName = "Apex Ruthless Heavyweight Title", wrestlerId = 1, wrestlerName = "Titan Maverick", wonWeek = 1, daysHeld = 150, successfulDefenses = 6),
                    TitleHolder(titleId = 2, titleName = "Zone Hardcore Title", wrestlerId = 3, wrestlerName = "Buster Bruiser", wonWeek = 1, daysHeld = 40, successfulDefenses = 2)
                ))
            }
            "golden" -> {
                val state = GameState(
                    id = 1,
                    companyName = "Apex Golden Age Promotion",
                    cash = 800000.0,
                    prestige = 85,
                    fanbase = 120000,
                    currentWeek = 1,
                    currentShowName = "Gold Wrestling Superstars",
                    showStyle = "Classic",
                    scoutingLevel = 1,
                    physicianLevel = 1,
                    scoutLevel = 1,
                    creativeLevel = 3
                )
                activeDb.gameStateDao().insertGameState(state)

                val wrestlers = listOf(
                    Wrestler(id = 1, name = "Rick Flair-like", realName = "Richard Fliehr", companyId = 1, popularity = 94, inRingSkill = 90, micSkill = 98, stamina = 92, morale = 92, heelFace = "HEEL", salary = 8000.0, isContracted = true, style = "Showman", traits = "Larger than Life, Wooooo!", finisher = "Figure-Four Lock", age = 45),
                    Wrestler(id = 2, name = "John Steel", realName = "Johnathon Sullivan", companyId = 1, popularity = 88, inRingSkill = 75, micSkill = 92, stamina = 95, morale = 90, heelFace = "FACE", salary = 6000.0, isContracted = true, style = "Showman", traits = "All-American Hero", finisher = "Steel Slammer", age = 27),
                    Wrestler(id = 3, name = "Goliath Gargant", realName = "Gregory Gigantus", companyId = 1, popularity = 86, inRingSkill = 55, micSkill = 75, stamina = 80, morale = 85, heelFace = "HEEL", salary = 5000.0, isContracted = true, style = "Brawler", traits = "Unstoppable Force", finisher = "Chokeslam", age = 28),
                    Wrestler(id = 4, name = "Rex Gung", realName = "Reginald Gunning", companyId = 1, popularity = 82, inRingSkill = 74, micSkill = 88, stamina = 92, morale = 90, heelFace = "HEEL", salary = 3800.0, isContracted = true, style = "Brawler", finisher = "Outlaw Lasso", age = 30),
                    Wrestler(id = 5, name = "Serena Swift", realName = "Serena Reynolds", companyId = 1, popularity = 84, inRingSkill = 82, micSkill = 85, stamina = 95, morale = 92, heelFace = "FACE", salary = 4000.0, isContracted = true, style = "Technician", gender = "Female", finisher = "Swift Crossbody", age = 24),
                    Wrestler(id = 6, name = "Vipera", realName = "Valerie Thorne", companyId = 1, popularity = 80, inRingSkill = 78, micSkill = 84, stamina = 90, morale = 82, heelFace = "HEEL", salary = 3400.0, isContracted = true, style = "Showman", gender = "Female", finisher = "Venom Strike", age = 25)
                )
                activeDb.wrestlerDao().insertWrestlers(wrestlers)

                seedCompetitorsAndAgents(activeDb)
                
                activeDb.rosterAdminDao().insertTitles(listOf(
                    Title(id = 1, titleName = "Supreme World Golden Belt", prestige = 95, division = "World Heavyweight")
                ))
                activeDb.rosterAdminDao().insertTitleHolders(listOf(
                    TitleHolder(titleId = 1, titleName = "Supreme World Golden Belt", wrestlerId = 1, wrestlerName = "Rick Flair-like", wonWeek = 1, daysHeld = 300, successfulDefenses = 12)
                ))
            }
            "indie" -> {
                val state = GameState(
                    id = 1,
                    companyName = "Apex Indie Evolution",
                    cash = 100000.0,
                    prestige = 40,
                    fanbase = 8000,
                    currentWeek = 1,
                    currentShowName = "Apex Underground",
                    showStyle = "Lucha",
                    scoutingLevel = 1,
                    physicianLevel = 2,
                    scoutLevel = 3,
                    creativeLevel = 1
                )
                activeDb.gameStateDao().insertGameState(state)

                val wrestlers = listOf(
                    Wrestler(id = 1, name = "Bryan Golden", realName = "Bryan Danielson-like", companyId = 1, popularity = 86, inRingSkill = 98, micSkill = 84, stamina = 100, morale = 98, heelFace = "FACE", salary = 3500.0, isContracted = true, style = "Technician", traits = "Technical Dragon, Workhorse", finisher = "Cattle Mutilation", age = 30),
                    Wrestler(id = 2, name = "Kage Vance", realName = "Kagehiro Watanabe", companyId = 1, popularity = 78, inRingSkill = 92, micSkill = 65, stamina = 95, morale = 90, heelFace = "HEEL", salary = 2400.0, isContracted = true, style = "Technician", traits = "Submission Master", finisher = "Kage Lock", age = 23),
                    Wrestler(id = 3, name = "Alex Breeze", realName = "Alexander Wind", companyId = 1, popularity = 80, inRingSkill = 90, micSkill = 82, stamina = 96, morale = 96, heelFace = "FACE", salary = 2600.0, isContracted = true, style = "High Flyer", finisher = "Summer Breeze", age = 22),
                    Wrestler(id = 4, name = "El Dragone", realName = "Eduardo Ramirez", companyId = 1, popularity = 76, inRingSkill = 94, micSkill = 55, stamina = 92, morale = 92, heelFace = "FACE", salary = 2000.0, isContracted = true, style = "High Flyer", finisher = "Dragon Fire Splash", age = 21),
                    Wrestler(id = 5, name = "Christian Zenith", realName = "Christian Grey", companyId = 1, popularity = 78, inRingSkill = 95, micSkill = 78, stamina = 94, morale = 95, heelFace = "FACE", salary = 2500.0, isContracted = true, style = "Technician", finisher = "Zenith Edge", age = 24),
                    Wrestler(id = 6, name = "Aero Hawk", realName = "Takeshi Falcon", companyId = 1, popularity = 72, inRingSkill = 86, micSkill = 72, stamina = 94, morale = 94, heelFace = "FACE", salary = 1800.0, isContracted = true, style = "High Flyer", finisher = "Falcon Arrow Flight", age = 22, activeStatus = "Prospect")
                )
                activeDb.wrestlerDao().insertWrestlers(wrestlers)

                seedCompetitorsAndAgents(activeDb)

                activeDb.rosterAdminDao().insertTitles(listOf(
                    Title(id = 1, titleName = "Apex Grand Cruiserweight Title", prestige = 72, division = "Midcard")
                ))
                activeDb.rosterAdminDao().insertTitleHolders(listOf(
                    TitleHolder(titleId = 1, titleName = "Apex Grand Cruiserweight Title", wrestlerId = 1, wrestlerName = "Bryan Golden", wonWeek = 1, daysHeld = 60, successfulDefenses = 3)
                ))
            }
            else -> {
                val startState = GameState(
                    id = 1,
                    companyName = "Apex Pro Wrestling",
                    cash = 300000.0,
                    prestige = 60,
                    fanbase = 25000,
                    currentWeek = 1,
                    currentShowName = "Apex Showcase"
                )
                activeDb.gameStateDao().insertGameState(startState)

                val seeds = listOf(
                    Wrestler(id = 1, name = "John Steel", realName = "Johnathon Sullivan", companyId = 1, popularity = 82, inRingSkill = 75, micSkill = 88, stamina = 95, morale = 90, heelFace = "FACE", salary = 4200.0, isContracted = true, style = "Showman", traits = "Charismatic Leader, Resilient", finisher = "The Steel Hammer", age = 29),
                    Wrestler(id = 2, name = "Titan Maverick", realName = "Marcus Vance", companyId = 1, popularity = 85, inRingSkill = 90, micSkill = 70, stamina = 100, morale = 85, heelFace = "HEEL", salary = 4800.0, isContracted = true, style = "Brawler", traits = "Ruthless, Powerhouse", finisher = "Maverick Bomb", age = 32),
                    Wrestler(id = 3, name = "Kage Vance", realName = "Kagehiro Watanabe", companyId = 1, popularity = 70, inRingSkill = 85, micSkill = 60, stamina = 90, morale = 95, heelFace = "HEEL", salary = 2800.0, isContracted = true, style = "Technician", traits = "Technical Expert, Cold-Clipped", finisher = "Kage Lock", age = 25),
                    Wrestler(id = 4, name = "Alex Breeze", realName = "Alexander Wind", companyId = 1, popularity = 75, inRingSkill = 88, micSkill = 80, stamina = 95, morale = 98, heelFace = "FACE", salary = 3100.0, isContracted = true, style = "High Flyer", traits = "Acrobatic, Crowd Pleaser", finisher = "Summer Breeze", age = 24),
                    Wrestler(id = 5, name = "El Dragone", realName = "Eduardo Ramirez", companyId = 1, popularity = 68, inRingSkill = 92, micSkill = 50, stamina = 88, morale = 90, heelFace = "FACE", salary = 2500.0, isContracted = true, style = "High Flyer", traits = "High-Risk Specialist", finisher = "Dragon Fire Splash", age = 22),
                    Wrestler(id = 6, name = "Rex Gung", realName = "Reginald Gunning", companyId = 1, popularity = 79, inRingSkill = 72, micSkill = 85, stamina = 92, morale = 88, heelFace = "HEEL", salary = 3500.0, isContracted = true, style = "Brawler", traits = "Loudmouth, Hostile", finisher = "Outlaw Lasso", age = 34),
                    Wrestler(id = 7, name = "Serena Swift", realName = "Serena Reynolds", companyId = 1, popularity = 80, inRingSkill = 86, micSkill = 82, stamina = 96, morale = 92, heelFace = "FACE", salary = 3800.0, isContracted = true, style = "Technician", gender = "Female", traits = "Fan Favorite, Speedster", finisher = "Swift Submission", age = 27),
                    Wrestler(id = 8, name = "Vipera", realName = "Valerie Thorne", companyId = 1, popularity = 78, inRingSkill = 82, micSkill = 84, stamina = 90, morale = 78, heelFace = "HEEL", salary = 3400.0, isContracted = true, style = "Showman", gender = "Female", traits = "Sly tactician, Merciless", finisher = "Venom Strike", age = 28),
                    Wrestler(id = 9, name = "Mitch Dynamo", realName = "Mitchell Hall", companyId = 1, popularity = 62, inRingSkill = 78, micSkill = 65, stamina = 85, morale = 100, heelFace = "FACE", salary = 1800.0, isContracted = true, style = "High Flyer", traits = "Young Blood, High Energy", finisher = "Dynamo Drop", age = 21, activeStatus = "Prospect"),
                    Wrestler(id = 10, name = "Crusher Cobb", realName = "William Cobb", companyId = 1, popularity = 66, inRingSkill = 60, micSkill = 55, stamina = 80, morale = 82, heelFace = "HEEL", salary = 1500.0, isContracted = true, style = "Brawler", traits = "Hard Headed, Gatekeeper", finisher = "Cobb Crusher", age = 30),
                    Wrestler(id = 11, name = "Christian Zenith", realName = "Christian Grey", companyId = 1, popularity = 72, inRingSkill = 94, micSkill = 75, stamina = 90, morale = 95, heelFace = "FACE", salary = 3200.0, isContracted = true, style = "Technician", traits = "Perfectionist, Workhorse", finisher = "Zenith Edge", age = 26),
                    Wrestler(id = 12, name = "Damian Sledge", realName = "Damian Sledge", companyId = 1, popularity = 76, inRingSkill = 70, micSkill = 90, stamina = 85, morale = 85, heelFace = "HEEL", salary = 3600.0, isContracted = true, style = "Showman", traits = "Egotistical, Loud", finisher = "Sledgehammer", age = 31)
                )
                activeDb.wrestlerDao().insertWrestlers(seeds)

                seedCompetitorsAndAgents(activeDb)

                activeDb.rosterAdminDao().insertTitles(listOf(
                    Title(id = 1, titleName = "World Heavyweight Championship", prestige = 90, division = "World Heavyweight"),
                    Title(id = 2, titleName = "Apex Television Championship", prestige = 75, division = "Midcard")
                ))
                activeDb.rosterAdminDao().insertTitleHolders(listOf(
                    TitleHolder(titleId = 1, titleName = "World Heavyweight Championship", wrestlerId = 1, wrestlerName = "John Steel", wonWeek = 1, daysHeld = 120, successfulDefenses = 4),
                    TitleHolder(titleId = 2, titleName = "Apex Television Championship", wrestlerId = 6, wrestlerName = "Rex Gung", wonWeek = 1, daysHeld = 30, successfulDefenses = 1)
                ))
            }
        }
    }

    private suspend fun seedCompetitorsAndAgents(activeDb: GameDatabase) {
        val freeAgents = listOf(
            Wrestler(id = 13, name = "Rick Flair-like", realName = "Richard Fliehr", companyId = 0, popularity = 88, inRingSkill = 92, micSkill = 98, stamina = 90, morale = 75, heelFace = "HEEL", salary = 8500.0, isContracted = false, style = "Showman", traits = "Legendary", finisher = "Figure-Four Lock", age = 45),
            Wrestler(id = 14, name = "Bryan Golden", realName = "Bryan Danielson-like", companyId = 0, popularity = 81, inRingSkill = 98, micSkill = 82, stamina = 100, morale = 100, heelFace = "FACE", salary = 7000.0, isContracted = false, style = "Technician", traits = "Master Submissionist", finisher = "Cattle Mutilation", age = 38),
            Wrestler(id = 15, name = "Goliath Gargant", realName = "Gregory Gigantus", companyId = 0, popularity = 74, inRingSkill = 55, micSkill = 68, stamina = 70, morale = 80, heelFace = "HEEL", salary = 4000.0, isContracted = false, style = "Brawler", traits = "Behemoth", finisher = "Goliath Chokeslam", age = 35),
            Wrestler(id = 16, name = "Aero Hawk", realName = "Takeshi Falcon", companyId = 0, popularity = 65, inRingSkill = 84, micSkill = 70, stamina = 92, morale = 90, heelFace = "FACE", salary = 2900.0, isContracted = false, style = "High Flyer", traits = "Daredevil, Honorable", finisher = "Falcon Arrow Flight", age = 23, activeStatus = "Prospect"),
            Wrestler(id = 17, name = "Tessa Valk", realName = "Tessa Valkyrie", companyId = 0, popularity = 73, inRingSkill = 88, micSkill = 78, stamina = 95, morale = 85, heelFace = "FACE", salary = 3300.0, isContracted = false, style = "Technician", gender = "Female", traits = "Fierce Competitor", finisher = "Valkyrie Drop", age = 25),
            Wrestler(id = 18, name = "Buster Bruiser", realName = "Buster McAlister", companyId = 0, popularity = 58, inRingSkill = 62, micSkill = 50, stamina = 80, morale = 90, heelFace = "HEEL", salary = 1200.0, isContracted = false, style = "Brawler", traits = "Lover of Fights", finisher = "Buster Boot", age = 27),
            Wrestler(id = 19, name = "Starlight Kid", realName = "Ayumi Sato", companyId = 0, popularity = 68, inRingSkill = 86, micSkill = 64, stamina = 88, morale = 92, heelFace = "FACE", salary = 2500.0, isContracted = false, style = "High Flyer", gender = "Female", traits = "High Speed Acrobat", finisher = "Starlight Moonsault", age = 20, activeStatus = "Prospect"),
            Wrestler(id = 20, name = "Scythe", realName = "Unknown Reaper", companyId = 0, popularity = 80, inRingSkill = 75, micSkill = 85, stamina = 85, morale = 70, heelFace = "HEEL", salary = 5900.0, isContracted = false, style = "Showman", traits = "Mysthetic", finisher = "Scythe Kick", age = 33)
        )
        activeDb.wrestlerDao().insertWrestlers(freeAgents)

        val rivalWrestlers = listOf(
            Wrestler(id = 21, name = "Ace Archer", realName = "Ace Archer", companyId = 2, popularity = 86, inRingSkill = 84, micSkill = 90, stamina = 95, morale = 88, heelFace = "FACE", salary = 7500.0, isContracted = false, style = "Showman", traits = "Confident", finisher = "Archer Arrow", age = 28, loyalty = 85, interestLevel = 45),
            Wrestler(id = 22, name = "Midnight Shadow", realName = "Kenjiro Sato", companyId = 2, popularity = 72, inRingSkill = 92, micSkill = 55, stamina = 98, morale = 82, heelFace = "HEEL", salary = 3200.0, isContracted = false, style = "High Flyer", traits = "Ghost style", finisher = "Ninjutsu Drop", age = 24, loyalty = 60, interestLevel = 75),
            Wrestler(id = 23, name = "Thunder Gung", realName = "Thomas Gunning", companyId = 2, popularity = 78, inRingSkill = 75, micSkill = 82, stamina = 90, morale = 80, heelFace = "HEEL", salary = 4800.0, isContracted = false, style = "Brawler", traits = "Powerhouse", finisher = "Thunder Slam", age = 33, loyalty = 75, interestLevel = 50),
            Wrestler(id = 24, name = "Sterling Sterling", realName = "Robert Sterling", companyId = 3, popularity = 83, inRingSkill = 82, micSkill = 94, stamina = 92, morale = 85, heelFace = "HEEL", salary = 6800.0, isContracted = false, style = "Showman", traits = "Wealthy Elite", finisher = "Sterling Sovereign", age = 30, loyalty = 90, interestLevel = 35),
            Wrestler(id = 25, name = "Desert Viper", realName = "Sabit Al-Jamil", companyId = 3, popularity = 75, inRingSkill = 78, micSkill = 72, stamina = 88, morale = 76, heelFace = "HEEL", salary = 3600.0, isContracted = false, style = "Brawler", traits = "Cruel", finisher = "Viper Squeeze", age = 35, loyalty = 50, interestLevel = 80),
            Wrestler(id = 26, name = "Nova Sparks", realName = "Nova Sparks", companyId = 3, popularity = 71, inRingSkill = 88, micSkill = 78, stamina = 96, morale = 94, heelFace = "FACE", salary = 2800.0, isContracted = false, style = "High Flyer", traits = "High Velocity", finisher = "Supernova Press", age = 22, loyalty = 40, interestLevel = 85),
            Wrestler(id = 27, name = "Ryu Hayabusa", realName = "Ryu Hayabusa", companyId = 4, popularity = 82, inRingSkill = 98, micSkill = 65, stamina = 94, morale = 90, heelFace = "FACE", salary = 5800.0, isContracted = false, style = "Technician", traits = "Legend", finisher = "Hayabusa Driver", age = 36, loyalty = 95, interestLevel = 40)
        )
        activeDb.wrestlerDao().insertWrestlers(rivalWrestlers)

        val seedRumours = listOf(
            Rumour(text = "Ace Archer of GWG tells reporters he is 'completely focused' on GWG.", weekGenerated = 1),
            Rumour(text = "RUMOUR: Nova Sparks is reportedly frustrated over midcard booking.", weekGenerated = 1)
        )
        activeDb.rumourDao().insertRumours(seedRumours)
    }

    suspend fun importRosterFromJson(jsonStr: String): Pair<Boolean, String> {
        return try {
            val root = org.json.JSONObject(jsonStr)
            
            if (!root.has("wrestlers")) {
                return Pair(false, "Invalid JSON: Root object must contain 'wrestlers' array.")
            }
            
            val wrestlersArray = root.getJSONArray("wrestlers")
            val wrestlersToInsert = mutableListOf<Wrestler>()
            
            for (i in 0 until wrestlersArray.length()) {
                val obj = wrestlersArray.getJSONObject(i)
                
                val name = if (obj.has("name")) obj.getString("name") else return Pair(false, "Wrestler #$i is missing 'name' field.")
                if (name.isBlank()) return Pair(false, "Wrestler #$i name cannot be blank.")
                
                val popularity = if (obj.has("popularity")) obj.getInt("popularity") else 50
                val inRingSkill = if (obj.has("inRingSkill")) obj.getInt("inRingSkill") else 50
                val micSkill = if (obj.has("micSkill")) obj.getInt("micSkill") else 50
                val stamina = if (obj.has("stamina")) obj.getInt("stamina") else 80
                val morale = if (obj.has("morale")) obj.getInt("morale") else 80
                val salary = if (obj.has("salary")) obj.getDouble("salary") else 1000.0
                val heelFace = if (obj.has("heelFace")) obj.getString("heelFace") else "FACE"
                val style = if (obj.has("style")) obj.getString("style") else "Showman"
                
                if (popularity !in 0..100) return Pair(false, "Wrestler '$name' popularity ($popularity) must be between 0 and 100.")
                if (inRingSkill !in 0..100) return Pair(false, "Wrestler '$name' inRingSkill ($inRingSkill) must be between 0 and 100.")
                if (micSkill !in 0..100) return Pair(false, "Wrestler '$name' micSkill ($micSkill) must be between 0 and 100.")
                if (stamina !in 0..100) return Pair(false, "Wrestler '$name' stamina ($stamina) must be between 0 and 100.")
                if (morale !in 0..100) return Pair(false, "Wrestler '$name' morale ($morale) must be between 0 and 100.")
                
                if (heelFace != "FACE" && heelFace != "HEEL") {
                    return Pair(false, "Wrestler '$name' heelFace must be either 'FACE' or 'HEEL'.")
                }
                
                val w = Wrestler(
                    id = 0,
                    name = name,
                    ringName = if (obj.has("ringName")) obj.getString("ringName") else name,
                    realName = if (obj.has("realName")) obj.getString("realName") else "",
                    companyId = if (obj.has("companyId")) obj.getInt("companyId") else 1,
                    popularity = popularity,
                    inRingSkill = inRingSkill,
                    micSkill = micSkill,
                    stamina = stamina,
                    morale = morale,
                    heelFace = heelFace,
                    salary = salary,
                    isContracted = if (obj.has("isContracted")) obj.getBoolean("isContracted") else true,
                    style = style,
                    traits = if (obj.has("traits")) obj.getString("traits") else "Resilient",
                    finisher = if (obj.has("finisher")) obj.getString("finisher") else "Finisher Slam",
                    gender = if (obj.has("gender")) obj.getString("gender") else "Male",
                    age = if (obj.has("age")) obj.getInt("age") else 25,
                    activeStatus = if (obj.has("activeStatus")) obj.getString("activeStatus") else "Active"
                )
                wrestlersToInsert.add(w)
            }
            
            var customState: GameState? = null
            if (root.has("gameState")) {
                val stObj = root.getJSONObject("gameState")
                val companyName = if (stObj.has("companyName")) stObj.getString("companyName") else "Apex Custom Fed"
                val cash = if (stObj.has("cash")) stObj.getDouble("cash") else 300000.0
                val prestige = if (stObj.has("prestige")) stObj.getInt("prestige") else 60
                val fanbase = if (stObj.has("fanbase")) stObj.getInt("fanbase") else 20000
                val currentWeek = if (stObj.has("currentWeek")) stObj.getInt("currentWeek") else 1
                
                customState = GameState(
                    id = 1,
                    companyName = companyName,
                    cash = cash,
                    prestige = prestige,
                    fanbase = fanbase,
                    currentWeek = currentWeek,
                    currentShowName = "Apex Showcase"
                )
            }
            
            val db = currentDatabase.value
            db.clearAllTables()
            db.wrestlerDao().insertWrestlers(wrestlersToInsert)
            
            db.draftMatchDao().insertDraftMatches(listOf(DraftMatch(1), DraftMatch(2), DraftMatch(3), DraftMatch(4)))
            db.companyLeagueStatsDao().insertLeagueStats(com.example.domain.LeagueEngine.createDefaultStats(1))
            
            if (customState != null) {
                db.gameStateDao().insertGameState(customState)
            } else {
                val defaultState = GameState(
                    id = 1,
                    companyName = "Apex Pro Wrestling",
                    cash = 300000.0,
                    prestige = 60,
                    fanbase = 25000,
                    currentWeek = 1,
                    currentShowName = "Apex Showcase"
                )
                db.gameStateDao().insertGameState(defaultState)
            }
            
            Pair(true, "Successfully imported ${wrestlersToInsert.size} superstars and custom configurations!")
        } catch (e: org.json.JSONException) {
            Pair(false, "Malformed JSON syntax: ${e.localizedMessage}")
        } catch (e: Exception) {
            Pair(false, "Import failed: ${e.localizedMessage}")
        }
    }

    suspend fun exportRosterToJson(): String {
        return try {
            val list = getWrestlersSync()
            val state = getGameStateSync()
            
            val root = org.json.JSONObject()
            val wrArr = org.json.JSONArray()
            
            for (w in list) {
                val wObj = org.json.JSONObject()
                wObj.put("name", w.name)
                wObj.put("ringName", w.ringName)
                wObj.put("realName", w.realName)
                wObj.put("companyId", w.companyId)
                wObj.put("popularity", w.popularity)
                wObj.put("inRingSkill", w.inRingSkill)
                wObj.put("micSkill", w.micSkill)
                wObj.put("stamina", w.stamina)
                wObj.put("morale", w.morale)
                wObj.put("heelFace", w.heelFace)
                wObj.put("salary", w.salary)
                wObj.put("isContracted", w.isContracted)
                wObj.put("style", w.style)
                wObj.put("traits", w.traits)
                wObj.put("finisher", w.finisher)
                wObj.put("gender", w.gender)
                wObj.put("age", w.age)
                wObj.put("activeStatus", w.activeStatus)
                wrArr.put(wObj)
            }
            root.put("wrestlers", wrArr)
            
            if (state != null) {
                val stObj = org.json.JSONObject()
                stObj.put("companyName", state.companyName)
                stObj.put("cash", state.cash)
                stObj.put("prestige", state.prestige)
                stObj.put("fanbase", state.fanbase)
                stObj.put("currentWeek", state.currentWeek)
                root.put("gameState", stObj)
            }
            
            root.toString(2)
        } catch (e: Exception) {
            e.printStackTrace()
            "{}"
        }
    }
}
