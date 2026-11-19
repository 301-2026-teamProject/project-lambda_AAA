package com.example.lambda_aaa.model

import androidx.lifecycle.ViewModel


// create lottery screen uses the viewModel to save state of form
class LotteryViewModel(
    private val repository: LotteryRepository = LotteryRepository()
): ViewModel() {
    fun createLottery(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        repository.create(lottery, onResult)
    }

    fun editLottery(lottery: Lottery, lotteryID: String, onResult: (Boolean, String?) -> Unit) {
        repository.updateLottery(lottery, lotteryID, onResult)
    }
}