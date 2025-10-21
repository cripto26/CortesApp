package com.quirozsolucions.cortesapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.quirozsolucions.cortesapp.algo.ShelfGuillotine
import com.quirozsolucions.cortesapp.model.*

class OptimizerViewModel : ViewModel() {

    // Estado del formulario (cm hacia afuera, mm adentro)
    var sheetWidthCm by mutableStateOf("215")
    var sheetHeightCm by mutableStateOf("244")
    var kerfMm by mutableStateOf("3")

    data class CutRow(
        var widthCm: String = "",
        var heightCm: String = "",
        var qty: String = "1",
        var rot: Boolean = true,
        var label: String = ""
    )

    var rows by mutableStateOf(
        listOf(
            CutRow("100", "50", "1"),
            CutRow("80", "40", "2"),
            CutRow("120", "80", "3"),
            CutRow("80", "40", "4"),
            CutRow("120", "20", "2")
        )
    )
        private set

    fun addRow() { rows = rows + CutRow() }
    fun removeRow(index: Int) { rows = rows.toMutableList().also { if (it.indices.contains(index)) it.removeAt(index) } }

    // ---- setters usados por la UI ----
    fun setWidth(i: Int, v: String) {
        rows = rows.toMutableList().also { if (i in it.indices) it[i] = it[i].copy(widthCm = v) }
    }
    fun setHeight(i: Int, v: String) {
        rows = rows.toMutableList().also { if (i in it.indices) it[i] = it[i].copy(heightCm = v) }
    }
    fun setQty(i: Int, v: String) {
        rows = rows.toMutableList().also { if (i in it.indices) it[i] = it[i].copy(qty = v) }
    }
    fun setRot(i: Int, checked: Boolean) {
        rows = rows.toMutableList().also { if (i in it.indices) it[i] = it[i].copy(rot = checked) }
    }
    // (opcional)
    fun setLabel(i: Int, label: String) {
        rows = rows.toMutableList().also { if (i in it.indices) it[i] = it[i].copy(label = label) }
    }


    var plan by mutableStateOf<PlanResult?>(null)
        private set

    fun optimize() {
        val wMm = sheetWidthCm.toIntOrNull()?.times(10) ?: 0
        val hMm = sheetHeightCm.toIntOrNull()?.times(10) ?: 0
        val kerf = kerfMm.toIntOrNull() ?: 0
        val sheet = SheetSpec(widthMm = wMm, heightMm = hMm, kerfMm = kerf)

        val specs = rows.mapNotNull { r ->
            val w = r.widthCm.toIntOrNull()?.times(10) ?: return@mapNotNull null
            val h = r.heightCm.toIntOrNull()?.times(10) ?: return@mapNotNull null
            val q = r.qty.toIntOrNull() ?: 1
            RectSpec(widthMm = w, heightMm = h, quantity = q, label = r.label, canRotate = r.rot)
        }

        plan = ShelfGuillotine.optimize(sheet, specs)
    }
}
