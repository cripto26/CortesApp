package com.quirozsolucions.cortesapp.model

data class Board(val widthCm: Int, val heightCm: Int)

data class Piece(
    val id: Int,
    val widthCm: Int,
    val heightCm: Int,
    val quantity: Int = 1
)

data class PlacedPiece(
    val id: Int,
    val index: Int,              // índice visible (1,2,3…)
    val xCm: Int,
    val yCm: Int,
    val widthCm: Int,
    val heightCm: Int,
    val shelfIndex: Int
)

data class LayoutResult(
    val placed: List<PlacedPiece>,
    val unplaced: List<Piece>,
    val usedAreaCm2: Int,
    val wasteAreaCm2: Int,
    val utilization: Float
)
