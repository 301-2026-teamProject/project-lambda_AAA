package com.example.lambda_aaa.model

import androidx.lifecycle.ViewModel

class LotteryViewModel(
    private val repository: LotteryRepository = LotteryRepository()
): ViewModel() {
    fun createLottery(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        repository.create(lottery, onResult)
    }
}