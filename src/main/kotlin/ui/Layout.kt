package ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ColumnHorizontalCenteredContent(
    modifier: Modifier = Modifier, verticalArrangement: Arrangement.Vertical = Arrangement.Center, content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = verticalArrangement, content = content)
}

@Composable
fun RowVerticalCenteredContent(
    modifier: Modifier = Modifier, horizontalArrangement: Arrangement.Horizontal = Arrangement.Center, content: @Composable RowScope.() -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = horizontalArrangement, content = content)
}