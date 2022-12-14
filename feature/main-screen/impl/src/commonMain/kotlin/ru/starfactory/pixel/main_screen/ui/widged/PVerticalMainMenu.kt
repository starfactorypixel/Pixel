package ru.starfactory.pixel.main_screen.ui.widged

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ru.starfactory.core.uikit.theme.PixelTheme

private val ADDITIONAL_PADDING = 4.dp

@Composable
internal fun <ID> PVerticalMainMenu(
    items: List<PVerticalMenuItem<ID>>,
    modifier: Modifier = Modifier,
    selectedItemId: ID? = null,
    onClickItem: (ID) -> Unit = {},
    isShowTitle: Boolean = true,
) {
    Row(modifier.height(IntrinsicSize.Max)) {
        // Column with icons
        Column(
            Modifier
                .clip(RoundedCornerShape(percent = 100))
                .background(PixelTheme.colors.background)
        ) {
            items.forEachIndexed { index, menuItem ->
                ItemIconContent(
                    menuItem.icon,
                    isSelected = menuItem.id == selectedItemId,
                    onClickItem = { onClickItem(menuItem.id) },
                    isFirst = index == 0,
                    isLast = index == items.size - 1,
                )
            }
        }

        // Column with text
        if (isShowTitle) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = ADDITIONAL_PADDING)
                    .width(IntrinsicSize.Max)
            ) {
                items.forEach { menuItem ->
                    ItemDescriptionContent(menuItem.text, menuItem.id == selectedItemId, onClickItem = { onClickItem(menuItem.id) })
                }
            }
        }
    }
}

@Composable
private fun ItemIconContent(
    icon: ImageVector,
    isSelected: Boolean,
    onClickItem: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
) {
    val targetColor = if (isSelected) PixelTheme.colors.primary else Color.Transparent
    val backgroundColor by animateColorAsState(targetColor)

    val topPadding = 12.dp + if (isFirst) ADDITIONAL_PADDING else 0.dp
    val bottomPadding = 12.dp + if (isLast) ADDITIONAL_PADDING else 0.dp

    Icon(
        icon, null,
        Modifier
            .background(backgroundColor)
            .clickable { onClickItem() }
            .padding(horizontal = 12.dp)
            .padding(top = topPadding, bottom = bottomPadding)
            .size(20.dp)
    )
}

@Composable
private fun ColumnScope.ItemDescriptionContent(text: String, isSelected: Boolean, onClickItem: () -> Unit) {
    val targetColor = if (isSelected) PixelTheme.colors.primary else MaterialTheme.colors.onSurface
    val textColor by animateColorAsState(targetColor)
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .weight(1f)
            .clickable(interactionSource, indication = null) { onClickItem() }
    ) {
        Text(
            text,
            Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 16.dp),
            color = textColor
        )
    }
}

internal data class PVerticalMenuItem<ID>(val id: ID, val icon: ImageVector, val text: String)
