package com.quirozsolucions.cortesapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.quirozsolucions.cortesapp.algo.ShelfGuillotine
import com.quirozsolucions.cortesapp.model.*

class OptimizerViewModel : ViewModel() {

    // Tablero base: 215 x 244 cm -> 2150 x 2440 mm
    var board by mutableStateOf(Board(widthMm = 2150, heightMm = 2440))
        private set

    // Unidad visible para el usuario
    var unit by mutableStateOf(LengthUnit.CM)
        private set

    var kerfMm by mutableStateOf(0)
        private set

    var allowRotation by mutableStateOf(false)
        private set

    var pieces by mutableStateOf(
        listOf(
            // Una fila inicial vacía
            Piece(1, 0, 0, 0)
        )
    )
        private set

    /** Resultado en múltiples láminas (páginas) */
    var pages by mutableStateOf<List<LayoutResult>>(emptyList())
        private set

    /** Piezas que definitivamente no caben en una lámina */
    var unfit by mutableStateOf<List<Piece>>(emptyList())
        private set

    // ================== Helpers de conversión ==================

    /** Convierte un valor en mm a la unidad actualmente seleccionada. */
    fun lengthValueInCurrentUnit(mm: Int): Int =
        mm / unit.toMmFactor

    /** Formatea longitud para mostrarla. */
    fun formatLength(mm: Int): String {
        val v = lengthValueInCurrentUnit(mm)
        return "$v${unit.label}"
    }

    /** Convierte un área mm² a la unidad actual. */
    fun areaValueInCurrentUnit(mm2: Int): Int {
        val f = unit.toMmFactor
        return mm2 / (f * f)
    }

    /** Formatea área para mostrarla. */
    fun formatArea(mm2: Int): String {
        val v = areaValueInCurrentUnit(mm2)
        return "$v ${unit.label}²"
    }

    /** Cambia la unidad visible (mm, cm, m). */
    fun changeUnit(newUnit: LengthUnit) {
        if (newUnit == unit) return
        unit = newUnit
    }

    // ================== Actualización desde la UI ==================

    /** Recibe dimensiones en la unidad seleccionada y las guarda en mm. */
    fun updateBoard(w: Int?, h: Int?) {
        val f = unit.toMmFactor
        board = board.copy(
            widthMm = w?.let { it * f } ?: board.widthMm,
            heightMm = h?.let { it * f } ?: board.heightMm
        )
    }

    fun updateKerf(mm: Int?) {
        kerfMm = mm ?: kerfMm
    }

    fun toggleRotation() {
        allowRotation = !allowRotation
    }

    /** Actualiza una pieza; los valores vienen en la unidad seleccionada. */
    fun updatePiece(index: Int, width: Int?, height: Int?, qty: Int?) {
        val f = unit.toMmFactor
        pieces = pieces.toMutableList().also { list ->
            if (index !in list.indices) return
            val p = list[index]
            list[index] = p.copy(
                widthMm = width?.let { it * f } ?: p.widthMm,
                heightMm = height?.let { it * f } ?: p.heightMm,
                quantity = qty ?: p.quantity
            )
        }
    }

    /** Añade una nueva fila de corte. */
    fun addRow() {
        val nextId = (pieces.maxOfOrNull { it.id } ?: 0) + 1
        // Por defecto 10 cm x 10 cm -> 100 mm x 100 mm
        pieces = pieces + Piece(nextId, 100, 100, 1)
    }

    /** Elimina la fila indicada (si hay más de una). */
    fun removeRow(index: Int) {
        if (pieces.size <= 1) return   // siempre dejar al menos una fila
        if (index !in pieces.indices) return
        pieces = pieces.toMutableList().also { list ->
            list.removeAt(index)
        }
    }

    // ================== Optimización ==================

    /** Ejecuta la optimización y reparte automáticamente en n tableros. */
    fun optimizeAll() {
        // Expandir cantidades (cada unidad como pieza independiente)
        var remaining = pieces.flatMap { p -> List(p.quantity) { p.copy(quantity = 1) } }

        val generated = mutableListOf<LayoutResult>()
        val cannotFit = mutableListOf<Piece>()

        // Filtrar las que jamás caben en una sola lámina (considerando rotación)
        remaining.partition {
            it.widthMm <= board.widthMm && it.heightMm <= board.heightMm ||
                    (allowRotation && it.heightMm <= board.widthMm && it.widthMm <= board.heightMm)
        }.also { (fit, out) ->
            remaining = fit
            cannotFit += out
        }

        // Ir creando páginas hasta ubicar todas las que sí caben
        while (remaining.isNotEmpty()) {
            val res = ShelfGuillotine.layout(
                board = board,
                inputPieces = remaining,
                kerfMm = kerfMm,
                allowRotation = allowRotation
            )
            generated += res

            // Si no ubicó ninguna, evitamos bucle infinito
            if (res.placed.isEmpty()) {
                cannotFit += remaining
                break
            }

            remaining = res.unplaced
        }

        pages = generated
        unfit = cannotFit
    }
}
