package com.quirozsolucions.cortesapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons               // 👈 IMPORT para íconos
import androidx.compose.material.icons.filled.ArrowBack  // 👈 Icono de “volver”
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.quirozsolucions.cortesapp.OptimizerViewModel
import com.quirozsolucions.cortesapp.model.LayoutResult
import kotlin.math.max

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class   // 👈 algunos artefactos muestran warning sin este opt-in
)
@Composable
fun ResultScreen(vm: OptimizerViewModel, onBack: () -> Unit) {
    val pages = vm.pages
    val pagerState = rememberPagerState(pageCount = { max(1, pages.size) })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gráficos de corte") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (pages.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No hay piezas que ubicar.") }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(padding)) {

            // Paginador de tableros
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val res: LayoutResult = pages[page]

                var scale by remember { mutableFloatStateOf(1f) }   // 👈 state recomendado
                val transformState = remember {
                    TransformableState { zoomChange, _, _ ->
                        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
                    }
                }

                // Fondo suave
                val grad = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.colorScheme.surface
                    )
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(grad)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                vm.board.widthCm.toFloat() /
                                        vm.board.heightCm.toFloat()
                            )
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .transformable(transformState)
                    ) {
                        ResultBoardCanvas(vm = vm, pageResult = res)
                    }
                }
            }

            // Indicadores / Resumen
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Página ${pagerState.currentPage + 1} de ${pages.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                val res = pages[pagerState.currentPage]
                val utilPct = (res.utilization * 100f)
                Text(
                    "Rendimiento: ${"%.1f".format(utilPct)}% | Desperdicio: ${res.wasteAreaCm2} cm²"
                )
            }

            if (vm.unfit.isNotEmpty()) {
                Text(
                    "Piezas que no caben en una lámina: ${vm.unfit.size}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
