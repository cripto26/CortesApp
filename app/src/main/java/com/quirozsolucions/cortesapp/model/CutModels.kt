package com.quirozsolucions.cortesapp.model

/**
 * Todas las longitudes se manejan internamente en milímetros (mm).
 */
data class Board(
    val widthMm: Int,
    val heightMm: Int
)

/**
 * widthMm  = L1 en mm
 * heightMm = Beta en mm
 */
data class Piece(
    val id: Int,
    val widthMm: Int,
    val heightMm: Int,
    val quantity: Int = 1
)

data class PlacedPiece(
    val id: Int,
    val index: Int,      // índice visible (1,2,3…)
    val xMm: Int,
    val yMm: Int,
    val widthMm: Int,
    val heightMm: Int,
    val shelfIndex: Int
)

/**
 * Área en mm². La conversión a cm² o m² se hace en la capa de UI.
 */
data class LayoutResult(
    val placed: List<PlacedPiece>,
    val unplaced: List<Piece>,
    val usedArea: Int,
    val wasteArea: Int,
    val utilization: Float
)

/**
 * Unidad de longitud elegida por el usuario para introducir/ver datos.
 * Internamente TODO es mm, esto solo afecta a cómo se muestran y se leen.
 */
enum class LengthUnit(val label: String, val toMmFactor: Int) {
    MM("mm", 1),
    CM("cm", 10),
    M("m", 1000)
}
