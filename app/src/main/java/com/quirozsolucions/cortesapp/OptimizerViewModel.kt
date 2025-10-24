package com.quirozsolucions.cortesapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.quirozsolucions.cortesapp.algo.ShelfGuillotine
import com.quirozsolucions.cortesapp.model.*

class OptimizerViewModel : ViewModel() {

    var board by mutableStateOf(Board(widthCm = 215, heightCm = 244))
        private set

    var kerfMm by mutableStateOf(0)                 // ancho de corte (sierra) en mm
        private set

    var allowRotation by mutableStateOf(false)
        private set

    var pieces by mutableStateOf(
        listOf(
            Piece(1, 100, 50, 1),
            Piece(2, 80, 40, 2),
            Piece(3, 120, 80, 1),
            Piece(4, 80, 40, 3),
            Piece(5, 120, 20, 2),
        )
    )
        private set

    var result by mutableStateOf<LayoutResult?>(null)
        private set

    fun updateBoard(w: Int?, h: Int?) {
        board = board.copy(
            widthCm = w ?: board.widthCm,
            heightCm = h ?: board.heightCm
        )
    }

    fun updateKerf(mm: Int?) { kerfMm = mm ?: kerfMm }
    fun toggleRotation() { allowRotation = !allowRotation }

    fun updatePiece(index: Int, width: Int?, height: Int?, qty: Int?) {
        pieces = pieces.toMutableList().also { list ->
            val p = list[index]
            list[index] = p.copy(
                widthCm = width ?: p.widthCm,
                heightCm = height ?: p.heightCm,
                quantity = qty ?: p.quantity
            )
        }
    }

    fun addRow() {
        val nextId = (pieces.maxOfOrNull { it.id } ?: 0) + 1
        pieces = pieces + Piece(nextId, 10, 10, 1)
    }

    fun optimize() {
        result = ShelfGuillotine.layout(board, pieces, kerfMm = kerfMm, allowRotation = allowRotation)
    }
}
