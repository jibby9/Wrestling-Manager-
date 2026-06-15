package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WrestlerDao {
    @Query("SELECT * FROM wrestlers ORDER BY name ASC")
    fun getAllWrestlers(): Flow<List<Wrestler>>

    @Query("SELECT * FROM wrestlers")
    suspend fun getWrestlersSync(): List<Wrestler>

    @Query("SELECT * FROM wrestlers WHERE id = :id LIMIT 1")
    suspend fun getWrestlerById(id: Int): Wrestler?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrestlers(wrestlers: List<Wrestler>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrestler(wrestler: Wrestler)

    @Update
    suspend fun updateWrestler(wrestler: Wrestler)

    @Delete
    suspend fun deleteWrestler(wrestler: Wrestler)
}

@Dao
interface GameStateDao {
    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    fun getGameState(): Flow<GameState?>

    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    suspend fun getGameStateSync(): GameState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(state: GameState)

    @Update
    suspend fun updateGameState(state: GameState)
}

@Dao
interface DraftMatchDao {
    @Query("SELECT * FROM draft_matches ORDER BY id ASC")
    fun getDraftMatches(): Flow<List<DraftMatch>>

    @Query("SELECT * FROM draft_matches ORDER BY id ASC")
    suspend fun getDraftMatchesSync(): List<DraftMatch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraftMatches(matches: List<DraftMatch>)

    @Update
    suspend fun updateDraftMatch(match: DraftMatch)

    @Query("UPDATE draft_matches SET worker1Id = 0, worker1Name = '', worker2Id = 0, worker2Name = '', segmentType = 'Match', matchStipulation = 'Normal', winnerSelection = 0")
    suspend fun clearDraftMatches()
}

@Dao
interface InboxMessageDao {
    @Query("SELECT * FROM inbox_messages ORDER BY weekReceived DESC, id DESC")
    fun getInboxMessages(): Flow<List<InboxMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInboxMessage(message: InboxMessage)

    @Update
    suspend fun updateInboxMessage(message: InboxMessage)

    @Query("DELETE FROM inbox_messages WHERE id = :id")
    suspend fun deleteInboxMessage(id: Int)
}

@Dao
interface ShowReportDao {
    @Query("SELECT * FROM show_reports ORDER BY week DESC, id DESC")
    fun getAllShowReports(): Flow<List<ShowReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShowReport(report: ShowReport)
}

@Dao
interface CompanyLeagueStatsDao {
    @Query("SELECT * FROM company_league_stats ORDER BY totalViewers DESC")
    fun getAllLeagueStats(): Flow<List<CompanyLeagueStats>>

    @Query("SELECT * FROM company_league_stats ORDER BY totalViewers DESC")
    suspend fun getAllLeagueStatsSync(): List<CompanyLeagueStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagueStats(stats: List<CompanyLeagueStats>)

    @Update
    suspend fun updateLeagueStats(stats: CompanyLeagueStats)
}


@Dao
interface RosterAdminDao {
    @Query("SELECT * FROM wrestler_era_snapshots ORDER BY id DESC")
    fun getAllEraSnapshots(): Flow<List<WrestlerEraSnapshot>>

    @Query("SELECT * FROM wrestler_era_snapshots WHERE wrestlerId = :wrestlerId ORDER BY id DESC")
    fun getEraSnapshotsForWrestler(wrestlerId: Int): Flow<List<WrestlerEraSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEraSnapshots(snapshots: List<WrestlerEraSnapshot>)

    @Query("SELECT * FROM contracts ORDER BY id DESC")
    fun getAllContracts(): Flow<List<Contract>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContracts(contracts: List<Contract>)

    @Query("SELECT * FROM injury_statuses ORDER BY id DESC")
    fun getAllInjuryStatuses(): Flow<List<InjuryStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInjuryStatuses(statuses: List<InjuryStatus>)

    @Query("SELECT * FROM titles ORDER BY id DESC")
    fun getAllTitles(): Flow<List<Title>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitles(titles: List<Title>)

    @Query("SELECT * FROM title_holders ORDER BY id DESC")
    fun getAllTitleHolders(): Flow<List<TitleHolder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitleHolders(holders: List<TitleHolder>)

    @Query("SELECT * FROM team_memberships ORDER BY id DESC")
    fun getAllTeamMemberships(): Flow<List<TeamMembership>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMemberships(teams: List<TeamMembership>)

    @Query("SELECT * FROM stable_memberships ORDER BY id DESC")
    fun getAllStableMemberships(): Flow<List<StableMembership>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStableMemberships(stables: List<StableMembership>)

    @Query("SELECT * FROM wrestler_relationships ORDER BY id DESC")
    fun getAllWrestlerRelationships(): Flow<List<WrestlerRelationship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrestlerRelationships(relations: List<WrestlerRelationship>)
}

@Dao
interface TransferNegotiationDao {
    @Query("SELECT * FROM transfer_negotiations ORDER BY id DESC")
    fun getAllNegotiations(): Flow<List<TransferNegotiation>>

    @Query("SELECT * FROM transfer_negotiations")
    suspend fun getNegotiationsSync(): List<TransferNegotiation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegotiation(negotiation: TransferNegotiation)

    @Update
    suspend fun updateNegotiation(negotiation: TransferNegotiation)

    @Delete
    suspend fun deleteNegotiation(negotiation: TransferNegotiation)
}

@Dao
interface ScoutAssignmentDao {
    @Query("SELECT * FROM scout_assignments ORDER BY id DESC")
    fun getAllAssignments(): Flow<List<ScoutAssignment>>

    @Query("SELECT * FROM scout_assignments")
    suspend fun getAssignmentsSync(): List<ScoutAssignment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ScoutAssignment)

    @Update
    suspend fun updateAssignment(assignment: ScoutAssignment)

    @Delete
    suspend fun deleteAssignment(assignment: ScoutAssignment)
}

@Dao
interface RumourDao {
    @Query("SELECT * FROM rumours ORDER BY weekGenerated DESC, id DESC")
    fun getAllRumours(): Flow<List<Rumour>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRumours(rumours: List<Rumour>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRumour(rumour: Rumour)

    @Query("DELETE FROM rumours")
    suspend fun clearAllRumours()
}

@Database(
    entities = [
        Wrestler::class,
        GameState::class,
        DraftMatch::class,
        InboxMessage::class,
        ShowReport::class,
        WrestlerEraSnapshot::class,
        Contract::class,
        InjuryStatus::class,
        Title::class,
        TitleHolder::class,
        TeamMembership::class,
        StableMembership::class,
        WrestlerRelationship::class,
        CompanyLeagueStats::class,
        TransferNegotiation::class,
        ScoutAssignment::class,
        Rumour::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun wrestlerDao(): WrestlerDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun draftMatchDao(): DraftMatchDao
    abstract fun inboxMessageDao(): InboxMessageDao
    abstract fun showReportDao(): ShowReportDao
    abstract fun rosterAdminDao(): RosterAdminDao
    abstract fun companyLeagueStatsDao(): CompanyLeagueStatsDao
    abstract fun transferNegotiationDao(): TransferNegotiationDao
    abstract fun scoutAssignmentDao(): ScoutAssignmentDao
    abstract fun rumourDao(): RumourDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: android.content.Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "wrestle_simulator_save.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}
