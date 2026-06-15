package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.domain.WrestleSimulator
import com.example.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val wrestlers: StateFlow<List<Wrestler>> = repository.wrestlers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val gameState: StateFlow<GameState?> = repository.gameState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val draftMatches: StateFlow<List<DraftMatch>> = repository.draftMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val inboxMessages: StateFlow<List<InboxMessage>> = repository.inboxMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val showReports: StateFlow<List<ShowReport>> = repository.showReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val eraSnapshots: StateFlow<List<WrestlerEraSnapshot>> = repository.eraSnapshots
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val contracts: StateFlow<List<Contract>> = repository.contracts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val injuryStatuses: StateFlow<List<InjuryStatus>> = repository.injuryStatuses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val titles: StateFlow<List<Title>> = repository.titles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val titleHolders: StateFlow<List<TitleHolder>> = repository.titleHolders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val teamMemberships: StateFlow<List<TeamMembership>> = repository.teamMemberships
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val stableMemberships: StateFlow<List<StableMembership>> = repository.stableMemberships
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wrestlerRelationships: StateFlow<List<WrestlerRelationship>> = repository.wrestlerRelationships
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val leagueStats: StateFlow<List<CompanyLeagueStats>> = repository.leagueStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val negotiations: StateFlow<List<TransferNegotiation>> = repository.negotiations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val scoutAssignments: StateFlow<List<ScoutAssignment>> = repository.scoutAssignments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val rumours: StateFlow<List<Rumour>> = repository.rumours
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    init {
        // Initialize game values on launch
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    // --- Draft Match Board Controls ---

    fun updateDraftMatch(match: DraftMatch) {
        viewModelScope.launch {
            repository.updateDraftMatch(match)
        }
    }

    fun clearDraftBoard() {
        viewModelScope.launch {
            repository.clearDraftMatches()
        }
    }

    // --- Roster Management ---

    fun signFreeAgent(wrestler: Wrestler) {
        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            // Free agent signing cost (2x salary fee)
            val hiringCost = wrestler.salary * 2.0
            if (currentState.cash >= hiringCost) {
                // 1. Deduct Cash
                repository.updateGameState(currentState.copy(cash = currentState.cash - hiringCost))
                // 2. Set Contract Status to true, establish high initial morale
                repository.updateWrestler(wrestler.copy(isContracted = true, morale = 95))
                // 3. Send welcome mail to inbox
                val signMail = InboxMessage(
                    subject = "Contract Signed: ${wrestler.name}",
                    sender = "Agent Office",
                    body = "Commissioner, ${wrestler.name} has signed terms with ${currentState.companyName}!\n\nDetails:\nStyle: ${wrestler.style}\nWeekly Payroll: \$${String.format("%,.2f", wrestler.salary)}\nSign-on fee paid: \$${String.format("%,.2f", hiringCost)}.",
                    weekReceived = currentState.currentWeek,
                    type = "NEWS"
                )
                repository.insertInboxMessage(signMail)
            } else {
                // Not enough funds - send a bounce mail
                val errorMail = InboxMessage(
                    subject = "FAILED NEGOTIATION: ${wrestler.name}",
                    sender = "Finance Department",
                    body = "We were unable to secure terms with ${wrestler.name} due to insufficient liquidity. We require at least \$${String.format("%,.2f", hiringCost)} cash for upfront negotiations.",
                    weekReceived = currentState.currentWeek,
                    type = "NEWS"
                )
                repository.insertInboxMessage(errorMail)
            }
        }
    }

    fun releaseWrestler(wrestler: Wrestler) {
        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            // Immediate severance cost is 1x salary
            val severanceCost = wrestler.salary
            repository.updateGameState(currentState.copy(cash = currentState.cash - severanceCost))
            // Revert contract status
            repository.updateWrestler(wrestler.copy(isContracted = false))
            // Mail recap
            val cancelMail = InboxMessage(
                subject = "Severance Issued: ${wrestler.name}",
                sender = "HR Department",
                body = "You have terminated ${wrestler.name}'s contract. A severance package of \$${String.format("%,.2f", severanceCost)} was issued. They are now an active Free Agent.",
                weekReceived = currentState.currentWeek,
                type = "NEWS"
            )
            repository.insertInboxMessage(cancelMail)
        }
    }

    fun updateWrestler(wrestler: Wrestler) {
        viewModelScope.launch {
            repository.updateWrestler(wrestler)
        }
    }

    fun renewContract(wrestler: Wrestler, wage: Double, weeks: Int) {
        viewModelScope.launch {
            repository.updateWrestler(wrestler.copy(salary = wage))
            val allContracts = contracts.value
            val existingContract = allContracts.find { it.wrestlerId == wrestler.id }
            if (existingContract != null) {
                repository.insertContracts(allContracts.map {
                    if (it.wrestlerId == wrestler.id) {
                        it.copy(
                            weeklySalary = wage,
                            weeksRemaining = weeks
                        )
                    } else it
                })
            } else {
                val newContract = Contract(
                    wrestlerId = wrestler.id,
                    companyName = "Apex Pro Wrestling",
                    weeklySalary = wage,
                    weeksRemaining = weeks,
                    roleClause = "Active Performer"
                )
                repository.insertContracts(allContracts + newContract)
            }
        }
    }

    // --- Transfers management ---

    fun toggleShortlist(wrestler: Wrestler) {
        viewModelScope.launch {
            repository.updateWrestler(wrestler.copy(isShortlisted = !wrestler.isShortlisted))
        }
    }

    fun submitTransferBid(wrestler: Wrestler, bidAmount: Double) {
        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            val currentWeek = currentState.currentWeek
            
            // Baseline transfer value calculation
            val calculatedValue = (wrestler.popularity * 1500.0) + (wrestler.inRingSkill * 500.0) + (wrestler.charisma * 300.0) + 10000.0
            val loyaltyFactor = 1.0 + ((wrestler.loyalty - 50) / 100.0)
            val baseValue = calculatedValue * loyaltyFactor

            // Find current contract weeks remaining for evaluation
            val allContracts = contracts.value
            val wrestlerContract = allContracts.find { it.wrestlerId == wrestler.id }
            val weeksLeft = wrestlerContract?.weeksRemaining ?: 20
            val contractFactor = if (weeksLeft < 10) 0.6 else if (weeksLeft < 20) 0.8 else 1.0
            val adjustedValue = baseValue * contractFactor

            val status: String
            var counterAmount = 0.0
            var mailSubject = ""
            var mailBody = ""

            if (bidAmount >= adjustedValue) {
                status = "COMPANY_ACCEPTED"
                mailSubject = "BID ACCEPTED: ${wrestler.name}"
                mailBody = "Congratulations commissioner! The ownership of the promotion representing ${wrestler.name} has officially ACCEPTED your transfer buyout bid of \$${String.format("%,.2f", bidAmount)}.\n\nYou are now authorized to negotiate contract and weekly salary terms directly with the talent in the Transfers screen under negotiations."
            } else if (bidAmount >= adjustedValue * 0.7) {
                status = "COUNTERED_BY_COMPANY"
                counterAmount = adjustedValue * 1.15
                mailSubject = "BID COUNTERED: ${wrestler.name}"
                mailBody = "Our offer of \$${String.format("%,.2f", bidAmount)} for ${wrestler.name} was rejected. However, the selling company has replied with a counter-offer of \$${String.format("%,.2f", counterAmount)}.\n\nYou can accept their counter-offer to proceed with player terms or submit an updated bid."
            } else {
                status = "COMPANY_REJECTED"
                mailSubject = "BID REJECTED: ${wrestler.name}"
                mailBody = "Our opening transfer fee offer of \$${String.format("%,.2f", bidAmount)} for ${wrestler.name} was flatly REJECTED by their current employers. Their negotiators deemed the bid insulting relative to the wrestler's loyalty and contract clauses.\n\nWe must increase our buyout fee to initiate discussions."
            }

            val negotiation = TransferNegotiation(
                wrestlerId = wrestler.id,
                wrestlerName = wrestler.name,
                buyerCompanyId = 1,
                sellerCompanyId = wrestler.companyId,
                bidAmount = bidAmount,
                wageOffered = 0.0,
                contractWeeksOffered = 0,
                roleOffered = "None",
                status = status,
                counterBidAmount = counterAmount,
                weekNegotiated = currentWeek
            )
            repository.insertNegotiation(negotiation)

            val inboxMsg = InboxMessage(
                subject = mailSubject,
                sender = wrestlerContract?.companyName ?: "Agency Office",
                body = mailBody,
                weekReceived = currentWeek,
                type = "NEWS"
            )
            repository.insertInboxMessage(inboxMsg)
        }
    }

    fun submitContractOffer(negotiationId: Int, wrestler: Wrestler, wageOffered: Double, weeksOffered: Int, roleOffered: String) {
        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            val currentWeek = currentState.currentWeek

            // Expected salary is based on popularity and skills
            val expectedSalary = (wrestler.popularity * 50.0) + (wrestler.inRingSkill * 20.0) + 1200.0
            
            val roleMultiplier = when (roleOffered) {
                "Main Event Star" -> 1.2
                "Lower Midcarder" -> 0.8
                "Enforcer" -> 0.95
                else -> 1.0
            }
            val adjustedExpectedSalary = expectedSalary * roleMultiplier
            
            // Loyalty and interest levels evaluation
            val interestFactor = 1.0 + ((60 - wrestler.interestLevel) / 100.0)
            val minWageAccepted = adjustedExpectedSalary * interestFactor

            val status: String
            var mailSubject = ""
            var mailBody = ""
            var isSecured = false

            if (wageOffered >= minWageAccepted) {
                status = "TALENT_ACCEPTED"
                isSecured = true
                mailSubject = "CONTRACT SECURED: ${wrestler.name}"
                mailBody = "Amazing news commissioner! ${wrestler.name} has officially ACCEPTED your personal terms of \$${String.format("%,.2f", wageOffered)}/week for ${weeksOffered} weeks under a ${roleOffered} role.\n\nThe wrestler has signed the contracts and has arrived at the Apex Pro Wrestling main locker room. They are now available for draft booking on your upcoming Friday show cards!"
            } else {
                status = "TALENT_REJECTED"
                mailSubject = "NEGOTIATIONS FAILED: ${wrestler.name}"
                mailBody = "Negotiations with ${wrestler.name} have collapsed. The wrestler felt the weekly salary of \$${String.format("%,.2f", wageOffered)} was disrespectful to their skills and general reputation in the league.\n\nExpected range: \$${String.format("%,.2f", minWageAccepted)} - \$${String.format("%,.2f", minWageAccepted * 1.3)}/week. We must submit a highly-improved contract to re-ignite interests."
            }

            // Find existing negotiation
            val allNegotiations = repository.getNegotiationsSync()
            val matchingNeg = allNegotiations.find { it.id == negotiationId }
            if (matchingNeg != null) {
                repository.updateNegotiation(matchingNeg.copy(
                    wageOffered = wageOffered,
                    contractWeeksOffered = weeksOffered,
                    roleOffered = roleOffered,
                    status = status
                ))
            }

            val inboxMsg = InboxMessage(
                subject = mailSubject,
                sender = wrestler.name,
                body = mailBody,
                weekReceived = currentWeek,
                type = "NEWS"
            )
            repository.insertInboxMessage(inboxMsg)

            if (isSecured) {
                val bidFee = matchingNeg?.bidAmount ?: 0.0
                val totalUpfrontCost = bidFee
                
                // Deduct cash and transfer ownership
                val newCash = currentState.cash - totalUpfrontCost
                repository.updateGameState(currentState.copy(cash = newCash))

                // Update Wrestler
                repository.updateWrestler(wrestler.copy(
                    companyId = 1,
                    isContracted = true,
                    salary = wageOffered,
                    morale = 95
                ))

                // Insert / Update Contract
                val allContracts = contracts.value
                val existingContract = allContracts.find { it.wrestlerId == wrestler.id }
                if (existingContract != null) {
                    repository.insertContracts(allContracts.map {
                        if (it.wrestlerId == wrestler.id) {
                            it.copy(
                                companyName = "Apex Pro Wrestling",
                                weeklySalary = wageOffered,
                                weeksRemaining = weeksOffered,
                                roleClause = roleOffered
                            )
                        } else it
                    })
                } else {
                    val newContract = Contract(
                        wrestlerId = wrestler.id,
                        companyName = "Apex Pro Wrestling",
                        weeklySalary = wageOffered,
                        weeksRemaining = weeksOffered,
                        roleClause = roleOffered
                    )
                    repository.insertContracts(allContracts + newContract)
                }
            }
        }
    }

    fun deleteTransferNegotiation(negotiation: TransferNegotiation) {
        viewModelScope.launch {
            repository.deleteNegotiation(negotiation)
        }
    }

    fun assignScout(wrestler: Wrestler, scoutName: String, focusType: String) {
        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            val assignment = ScoutAssignment(
                scoutName = scoutName,
                wrestlerId = wrestler.id,
                wrestlerName = wrestler.name,
                weeksRemaining = 1,
                focusType = focusType
            )
            repository.insertScoutAssignment(assignment)

            // Send confirmation mail
            val confirmMail = InboxMessage(
                subject = "Scout Dispatched: ${scoutName}",
                sender = "Head Scout",
                body = "Commissioner, ${scoutName} has been fully dispatched to compile an in-depth dossier on ${wrestler.name}.\n\nThe focus area is specified as ${focusType}. The scouted report will complete and arrive inside our Transfers Dashboard by next transition week.",
                weekReceived = currentState.currentWeek,
                type = "NEWS"
            )
            repository.insertInboxMessage(confirmMail)
        }
    }

    fun cancelScoutAssignment(assignment: ScoutAssignment) {
        viewModelScope.launch {
            repository.deleteScoutAssignment(assignment)
        }
    }

    // --- Inbox & Decisions ---

    fun markMessageRead(messageId: Int) {
        viewModelScope.launch {
            val messagesList = inboxMessages.value
            val match = messagesList.find { it.id == messageId }
            if (match != null && !match.isRead) {
                repository.updateInboxMessage(match.copy(isRead = true))
            }
        }
    }

    fun handleInboxAction(message: InboxMessage) {
        viewModelScope.launch {
            if (message.isHandled) return@launch
            val currentState = repository.getGameStateSync() ?: return@launch

            // Execute action attached
            var newCash = currentState.cash
            if (message.cashBonus != 0.0) {
                newCash += message.cashBonus
            }

            repository.updateGameState(currentState.copy(cash = newCash))
            repository.updateInboxMessage(message.copy(isHandled = true, isRead = true))
        }
    }

    fun deleteInboxMessage(id: Int) {
        viewModelScope.launch {
            repository.deleteInboxMessage(id)
        }
    }

    // --- Simulation Execution ---

    /**
     * Executes match draft card processing, saves results, and moves calendar to next week in active state.
     * Returns true on successful simulation, false if draft is empty.
     */
    fun runWeeklyShow(onCompleted: (ShowReport) -> Unit): Boolean {
        // Find if user drafted any valid segments
        val currentDrafts = draftMatches.value
        val hasBooked = currentDrafts.any { it.isBooked() }
        if (!hasBooked) {
            return false
        }

        viewModelScope.launch {
            val currentState = repository.getGameStateSync() ?: return@launch
            val activeRoster = repository.getWrestlersSync()
            val payroll = activeRoster.filter { it.isContracted }.sumOf { it.salary }

            // 1. Simulate Match results & wrestler statistics
            val (report, updatedRoster) = WrestleSimulator.simulateShowCard(
                currentState,
                currentDrafts,
                activeRoster
            )

            // 2. Perform Weekly Transitions & Calendar advancement
            val (nextState, finalRoster, nextMail) = WrestleSimulator.processWeeklyTransition(
                currentState,
                report,
                updatedRoster,
                payroll,
                titleHolders.value,
                titles.value
            )

            // 2.5 Run League competitor company simulations
            val currentStats = repository.getLeagueStatsSync()
            val nextStats = com.example.domain.LeagueEngine.simulateWeek(
                currentState.currentWeek,
                report,
                currentStats.ifEmpty { com.example.domain.LeagueEngine.createDefaultStats(currentState.currentWeek) },
                finalRoster
            )

            // 2.7 Transition Contracts & Scouts & Rumors
            val currentContracts = contracts.value
            val nextContracts = currentContracts.map { contract ->
                val remaining = contract.weeksRemaining - 1
                contract.copy(weeksRemaining = maxOf(remaining, 0))
            }
            repository.insertContracts(nextContracts)

            // Dynamic updates on wrestlers based on expired contracts
            val updatedWithContractsRoster = finalRoster.map { wrestler ->
                val matchingContract = nextContracts.find { it.wrestlerId == wrestler.id }
                if (matchingContract != null && matchingContract.weeksRemaining == 0 && wrestler.companyId != 0) {
                    // Contract expired
                    val isPlayerCo = wrestler.companyId == 1
                    val expiredMsg = InboxMessage(
                        subject = "CONTRACT EXPIRED: ${wrestler.name}",
                        sender = "HR Department",
                        body = if (isPlayerCo) {
                            "Commissioner! The contract for ${wrestler.name} has officially EXPIRED. Since no terms extension was negotiated in time, the wrestler has gathered their baggage and listed themselves as a Free Agent."
                        } else {
                            "Transfer Alert: ${wrestler.name}'s contract with ${matchingContract.companyName} has expired, making them an active Free Agent in the transfer pool!"
                        },
                        weekReceived = nextState.currentWeek,
                        type = "NEWS"
                    )
                    repository.insertInboxMessage(expiredMsg)
                    wrestler.copy(companyId = 0, isContracted = false)
                } else {
                    // Warn player if contract has 2 weeks left
                    val matchingContractOld = currentContracts.find { it.wrestlerId == wrestler.id }
                    if (wrestler.companyId == 1 && matchingContractOld != null) {
                        if (matchingContractOld.weeksRemaining == 3) {
                            val warnMsg = InboxMessage(
                                subject = "CONTRACT RUNNING OUT: ${wrestler.name}",
                                sender = "HR Department",
                                body = "Warning commissioner: ${wrestler.name}'s active contract runs out in 2 weeks! Check the Contracts subtab to negotiate extensions before they leave as a Free Agent.",
                                weekReceived = nextState.currentWeek,
                                type = "NEWS"
                            )
                            repository.insertInboxMessage(warnMsg)
                        }
                    }
                    wrestler
                }
            }

            // Scout progress updating
            val currentScouts = repository.getScoutAssignmentsSync()
            val fullyScoutedWrestlerIds = mutableListOf<Int>()

            currentScouts.forEach { scout ->
                val nextWeeks = scout.weeksRemaining - 1
                if (nextWeeks <= 0) {
                    fullyScoutedWrestlerIds.add(scout.wrestlerId)
                    val scoutReportMail = InboxMessage(
                        subject = "SCOUT DOSSIER: ${scout.wrestlerName}",
                        sender = scout.scoutName,
                        body = "Dossier compiled successfully, Commissioner!\n\n${scout.wrestlerName} background evaluation is complete. Focus area selected: ${scout.focusType}.\n\nFull stats, financial wage expectations, and loyalty parameters can now be observed in the Transfer Market screens.",
                        weekReceived = nextState.currentWeek,
                        type = "NEWS"
                    )
                    repository.insertInboxMessage(scoutReportMail)
                    repository.deleteScoutAssignment(scout)
                } else {
                    repository.insertScoutAssignment(scout.copy(weeksRemaining = nextWeeks))
                }
            }

            val updatedWithScoutsRoster = updatedWithContractsRoster.map { wrestler ->
                if (fullyScoutedWrestlerIds.contains(wrestler.id)) {
                    wrestler.copy(scoutingProgress = 100)
                } else wrestler
            }

            // News / Rumour generation
            val randomRumours = listOf(
                "Rumours: GWG is reportedly investigating buyouts for several technician category stars in the market.",
                "RUMOUR: Tokyo Wrestling Alliance is considering a mega bid to lock in free-agent Bryan Golden.",
                "Backstage Whispers: Several midcard competitors at GPW are complaining about their low alignment matches.",
                "Financial Gossip: GPW owner is seeking to sell some contracts to free up payroll spaces.",
                "Insider Report: Wrestlers in Free Agency are reportedly considering lowering their wage expectations as weeks progress.",
                "League Rumours: Apex Pro Wrestling is expected to make visual additions to their roster scouts this month."
            )
            val generatedText = randomRumours.random()
            val newRumour = Rumour(text = generatedText, weekGenerated = nextState.currentWeek)
            repository.insertRumour(newRumour)

            // 3. Persist reports & update general values
            repository.insertShowReport(report)
            repository.insertWrestlers(updatedWithScoutsRoster)
            repository.insertInboxMessage(nextMail)
            repository.clearDraftMatches()
            repository.insertLeagueStats(nextStats)
            repository.updateGameState(nextState)
            repository.saveAutosave()

            // 4. Periodically insert or refresh Free Agency to keep options lively
            if (nextState.currentWeek % 2 == 0) {
                val newAgent = WrestleSimulator.generateFreeAgent(nextState.currentWeek)
                repository.insertWrestler(newAgent)
            }

            onCompleted(report)
        }
        return true
    }

    // --- Club Section Operations ---

    fun changeTVDeal(dealType: String) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            val prestigeReq = when (dealType) {
                "FAST_STREAM" -> 40
                "PRESTIGE_PREMIUM" -> 75
                else -> 0
            }
            if (currentState.prestige < prestigeReq) {
                return@launch
            }
            val updatedState = currentState.copy(tvDeal = dealType)
            repository.updateGameState(updatedState)
        }
    }

    fun upgradeStaff(staffType: String) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            var cost = 0.0
            var newPhysician = currentState.physicianLevel
            var newScout = currentState.scoutLevel
            var newCreative = currentState.creativeLevel

            when (staffType) {
                "PHYSICIAN" -> {
                    if (currentState.physicianLevel < 3) {
                        cost = if (currentState.physicianLevel == 1) 25000.0 else 60000.0
                        newPhysician += 1
                    }
                }
                "SCOUT" -> {
                    if (currentState.scoutLevel < 3) {
                        cost = if (currentState.scoutLevel == 1) 20000.0 else 50000.0
                        newScout += 1
                    }
                }
                "CREATIVE" -> {
                    if (currentState.creativeLevel < 3) {
                        cost = if (currentState.creativeLevel == 1) 30000.0 else 75000.0
                        newCreative += 1
                    }
                }
            }

            if (currentState.cash >= cost && cost > 0.0) {
                val updatedState = currentState.copy(
                    cash = currentState.cash - cost,
                    physicianLevel = newPhysician,
                    scoutLevel = newScout,
                    creativeLevel = newCreative
                )
                repository.updateGameState(updatedState)
            }
        }
    }

    fun upgradeFacility(facilityType: String) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            var cost = 0.0
            var newGym = currentState.facilityGym
            var newPyro = currentState.facilityPyro
            var newInfirmary = currentState.facilityInfirmary

            when (facilityType) {
                "GYM" -> {
                    if (currentState.facilityGym < 3) {
                        cost = if (currentState.facilityGym == 1) 50000.0 else 120000.0
                        newGym += 1
                    }
                }
                "PYRO" -> {
                    if (currentState.facilityPyro < 3) {
                        cost = if (currentState.facilityPyro == 1) 35000.0 else 85000.0
                        newPyro += 1
                    }
                }
                "INFIRMARY" -> {
                    if (currentState.facilityInfirmary < 3) {
                        cost = if (currentState.facilityInfirmary == 1) 80000.0 else 180000.0
                        newInfirmary += 1
                    }
                }
            }

            if (currentState.cash >= cost && cost > 0.0) {
                val updatedState = currentState.copy(
                    cash = currentState.cash - cost,
                    facilityGym = newGym,
                    facilityPyro = newPyro,
                    facilityInfirmary = newInfirmary
                )
                repository.updateGameState(updatedState)
            }
        }
    }

    fun updateBrandIdentity(brand: String) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            val matchingShowStyle = when (brand) {
                "Classic" -> "Technical"
                else -> brand
            }
            val updatedState = currentState.copy(
                identityBrand = brand,
                showStyle = matchingShowStyle
            )
            repository.updateGameState(updatedState)
        }
    }

    fun upgradeTitlePrestige(titleId: Int) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            val cost = 15000.0
            if (currentState.cash >= cost) {
                val allTitles = titles.value
                val matchingTitle = allTitles.find { it.id == titleId }
                if (matchingTitle != null && matchingTitle.prestige < 100) {
                    val updatedTitle = matchingTitle.copy(prestige = minOf(matchingTitle.prestige + 5, 100))
                    repository.insertTitles(listOf(updatedTitle))
                    
                    val updatedState = currentState.copy(cash = currentState.cash - cost)
                    repository.updateGameState(updatedState)
                }
            }
        }
    }

    fun createNewTitle(titleName: String, division: String) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            val cost = 40000.0
            if (currentState.cash >= cost && titleName.isNotBlank()) {
                val nextId = (titles.value.maxOfOrNull { it.id } ?: 0) + 1
                val newTitle = Title(
                    id = nextId,
                    titleName = titleName,
                    prestige = 65,
                    division = division
                )
                repository.insertTitles(listOf(newTitle))
                val updatedState = currentState.copy(cash = currentState.cash - cost)
                repository.updateGameState(updatedState)
            }
        }
    }

    fun claimObjectiveReward(objectiveIdx: Int, rewardCash: Double, rewardPrestige: Int) {
        viewModelScope.launch {
            val currentState = gameState.value ?: return@launch
            val mask = 1 shl objectiveIdx
            if ((currentState.completedObjMask and mask) == 0) {
                val updatedState = currentState.copy(
                    cash = currentState.cash + rewardCash,
                    prestige = minOf(currentState.prestige + rewardPrestige, 100),
                    completedObjMask = currentState.completedObjMask or mask
                )
                repository.updateGameState(updatedState)
                
                val rewardMessage = InboxMessage(
                    subject = "OBJECTIVE COMPLETED: \$${rewardCash.toInt()} Budget Reward!",
                    sender = "Board of Directors",
                    body = "Excellent work, commissioner! You successfully satisfied our strategic Objective #${objectiveIdx + 1}.\n\nAs promised, we've disbursed an incentive bonus of \$${String.format("%,.2f", rewardCash)} directly to your corporate accounting balance and increased our operational prestige score.",
                    weekReceived = currentState.currentWeek,
                    type = "NEWS"
                )
                repository.insertInboxMessage(rewardMessage)
            }
        }
    }

    // --- Save & Database Operations ---

    fun saveToSlot(slotId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.saveToSlot(slotId)
            onResult(result)
        }
    }

    fun loadFromSlot(slotId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.loadFromSlot(slotId)
            onResult(result)
        }
    }

    fun getSlotInfo(slotId: Int, onResult: (GameRepository.SlotInfo) -> Unit) {
        viewModelScope.launch {
            val info = repository.getSlotInfo(slotId)
            onResult(info)
        }
    }

    fun runScenario(scenarioKey: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.startNewGame(scenarioKey)
            onResult(result)
        }
    }

    fun importRoster(jsonStr: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.importRosterFromJson(jsonStr)
            onResult(success, msg)
        }
    }

    fun exportRoster(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportRosterToJson()
            onResult(json)
        }
    }

    fun loadAutosave(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.loadAutosave()
            onResult(result)
        }
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
