package com.example.lambda_aaa.model

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
enum class ReservationView(val label: String) {
    ACTIVE("Active"),
    PAST("Past"),
    CANCELLED("Cancelled")
}

data class ReservationItem(
    val reservation: Reservation,
    val entrantName: String
)

data class CampsiteReservationsUiState(
    val isLoading: Boolean = true,
    val selectedView: ReservationView = ReservationView.ACTIVE,
    val grouped: OwnerReservations? = null,
    val names: Map<String, String> = emptyMap(),
    val error: String? = null
) {
    // The cards for whichever view is selected
    val visible: List<ReservationItem>
        get() {
            val groups = grouped ?: return emptyList()
            val list = when (selectedView) {
                ReservationView.ACTIVE -> groups.active
                ReservationView.PAST -> groups.past
                ReservationView.CANCELLED -> groups.cancelled + groups.rejected
            }
            return list.map { ReservationItem(it, names[it.entrantUid] ?: "Unnamed entrant") }
        }
}

class OwnerCampsiteReservationsViewModel(
    private val reservationRepo: ReservationRepository = ReservationRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CampsiteReservationsUiState())
        private set

    fun load(ownerId: String, campsiteId: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        reservationRepo.getCampsiteReservations(ownerId, campsiteId) { ok, grouped, message ->
            if (!ok || grouped == null) {
                uiState = uiState.copy(isLoading = false, error = message ?: "Couldn't load reservations")
                return@getCampsiteReservations
            }

            val uids = (grouped.active + grouped.past + grouped.cancelled + grouped.rejected)
                .map { it.entrantUid }
                .toSet()

            if (uids.isEmpty()) {
                uiState = uiState.copy(isLoading = false, grouped = grouped)
                return@getCampsiteReservations
            }

            userRepo.getNames(uids) { names ->
                uiState = uiState.copy(isLoading = false, grouped = grouped, names = names)
            }
        }
    }

    fun selectView(view: ReservationView) {
        uiState = uiState.copy(selectedView = view)
    }
}