package ru.starfactory.pixel.dashboard_screen.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import ru.starfactory.core.uikit.view.POutlinedFloatingActionButton
import ru.starfactory.pixel.dashboard_screen.ui.dashboardiconpack.DashboardCar
import kotlin.math.max
import kotlin.math.min

@Composable
fun CarStatusView(
    indicators: List<CarStatusIndicator>,
    modifier: Modifier = Modifier,
    carIcon: ImageVector = Icons.DashboardCar,
    maxLineWidth: Dp = 150.dp,
    dotSize: Dp = 4.dp,
    lineWidth: Dp = 2.dp,
) {

    val density = LocalDensity.current
    val maxLineWidthPx = with(density) { maxLineWidth.toPx().toInt() }

    var canvasDraws by remember { mutableStateOf(mutableListOf<CanvasDrawInfo>()) }

    val groupedIndicators = indicators.groupBy {
        it.position
    }


    val carAspect = carIcon.viewportWidth / carIcon.viewportHeight


    val carIconContent = @Composable {
        Image(
            carIcon,
            null,
            Modifier
                .background(Color.Green.copy(alpha = .2f)),
        )
    }


    val content = @Composable {
        carIconContent()
        CanvasContent(
            drawInfo = canvasDraws,
            dotSize = dotSize,
            lineWidth = lineWidth,
        )

        groupedIndicators[IndicatorPosition.START]?.forEach { indicator ->
            CarStatusIndicatorContent(indicator.icon)
        }
        groupedIndicators[IndicatorPosition.CENTER]?.forEach { indicator ->
            CarStatusIndicatorContent(indicator.icon)
        }
        groupedIndicators[IndicatorPosition.END]?.forEach { indicator ->
            CarStatusIndicatorContent(indicator.icon)
        }

        Unit
    }

    Layout(content, modifier) { measurables: List<Measurable>, constraints: Constraints ->
        // Step 0: get all measurables
        var position = 0
        val carMeasurable = measurables[position++]
        val canvasMeasurable = measurables[position++]
        val startIndicatorsMeasurables = measurables.subList(position, position + (groupedIndicators[IndicatorPosition.START]?.size ?: 0))
        position += startIndicatorsMeasurables.size
        val centerIndicatorsMeasurables = measurables.subList(position, position + (groupedIndicators[IndicatorPosition.CENTER]?.size ?: 0))
        position += centerIndicatorsMeasurables.size
        val endIndicatorsMeasurables = measurables.subList(position, position + (groupedIndicators[IndicatorPosition.END]?.size ?: 0))
        position += endIndicatorsMeasurables.size
        // ****************************

        val zeroMinSizeConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val containerSize = IntSize(constraints.maxWidth, constraints.maxHeight)


        // Step 1: measure START indicators
        val startIndicatorsPlaceable = startIndicatorsMeasurables.map { it.measure(zeroMinSizeConstraints) }
        val startIndicatorsWidth = startIndicatorsPlaceable.maxOfOrNull { it.measuredWidth } ?: 0

        // Step 2: measure END indicators
        val endIndicatorsPlaceable = endIndicatorsMeasurables.map { it.measure(zeroMinSizeConstraints) }
        val endIndicatorsWidth = startIndicatorsPlaceable.maxOfOrNull { it.measuredWidth } ?: 0

        // Step 3: measure car
        val carFrame = IntSize(containerSize.width - startIndicatorsWidth - endIndicatorsWidth, containerSize.height)
        val carFrameAspect = carFrame.width.toFloat() / carFrame.height
        val carConstraints = if (carAspect > carFrameAspect) {
            Constraints.fixed(width = carFrame.width, height = (carFrame.width / carAspect).toInt())
        } else {
            Constraints.fixed(width = (carFrame.height * carAspect).toInt(), height = carFrame.height)
        }
        val carPlaceable = carMeasurable.measure(carConstraints)

        // Step 4: measure CENTER indicators
        val centerIndicatorsPlaceable = centerIndicatorsMeasurables.map { it.measure(zeroMinSizeConstraints) }
        val centerIndicatorsWidth = centerIndicatorsPlaceable.maxOfOrNull { it.measuredWidth } ?: 0

        // measure canvas
        val canvasPlaceable = canvasMeasurable.measure(constraints)


        layout(containerSize.width, containerSize.height) {
            canvasDraws.clear()

            val carOffset = IntOffset((containerSize.width - carPlaceable.width) / 2, (containerSize.height - carPlaceable.height) / 2)
            carPlaceable.place(carOffset)

            canvasPlaceable.place(0, 0)

            startIndicatorsPlaceable.forEachIndexed { i, it ->
                val indicator = groupedIndicators[IndicatorPosition.START]!![i]

                val x = max(0, carOffset.x - maxLineWidthPx - it.height)
                val y = (carOffset.y + carPlaceable.height * indicator.y - it.height / 2f).toInt()

                canvasDraws += CanvasDrawInfo(
                    lineStart = Offset(
                        x = x.toFloat() + it.width,
                        y = y + it.height / 2f,
                    ),
                    dot = Offset(
                        x = carOffset.x + carPlaceable.width * indicator.x,
                        y = carOffset.y + carPlaceable.height * indicator.y,
                    ),
                    color = Color.White
                )

                it.place(x, y)
            }

            endIndicatorsPlaceable.forEachIndexed { i, it ->
                val indicator = groupedIndicators[IndicatorPosition.END]!![i]

                val x = min(containerSize.width - it.width, carOffset.x + carPlaceable.width + maxLineWidthPx)
                val y = (carOffset.y + carPlaceable.height * indicator.y - it.width / 2f).toInt()

                canvasDraws += CanvasDrawInfo(
                    lineStart = Offset(
                        x = x.toFloat(),
                        y = y + it.height / 2f,
                    ),
                    dot = Offset(
                        x = carOffset.x + carPlaceable.width * indicator.x,
                        y = carOffset.y + carPlaceable.height * indicator.y,
                    ),
                    color = Color.White
                )

                it.place(x, y)
            }

            centerIndicatorsPlaceable.forEachIndexed { i, it ->
                val indicator = groupedIndicators[IndicatorPosition.CENTER]!![i]
                it.place(
                    x = (carOffset.x + carPlaceable.width * indicator.x - it.width / 2f).toInt(),
                    y = (carOffset.y + carPlaceable.height * indicator.y - it.height / 2f).toInt(),
                )
            }
        }
    }
}

@Composable
private fun CarStatusIndicatorContent(icon: ImageVector) {
    POutlinedFloatingActionButton(onClick = {}) {
        Icon(icon, null)
    }
}

@Composable
private fun CanvasContent(
    drawInfo: List<CanvasDrawInfo>,
    dotSize: Dp,
    lineWidth: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {

        drawInfo.forEach { info ->

            drawPoints(
                listOf(info.dot),
                pointMode = PointMode.Points,
                color = info.color,
                strokeWidth = dotSize.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = info.color,
                start = info.lineStart,
                end = info.dot,
                strokeWidth = lineWidth.toPx()
            )
        }
    }
}

data class CanvasDrawInfo(
    val lineStart: Offset,
    val dot: Offset,
    val color: Color,
)

data class CarStatusIndicator(
    val x: Float, // %
    val y: Float, // %
    val icon: ImageVector,
)

private val CarStatusIndicator.position: IndicatorPosition
    get() = when (x) {
        in 0f..0.45f -> IndicatorPosition.START
        in 0.45f..0.55f -> IndicatorPosition.CENTER
        in 0.55f..1f -> IndicatorPosition.END
        else -> throw IllegalStateException("x coordinate must be in range 0..1, x=$x")
    }

private enum class IndicatorPosition {
    START, CENTER, END
}