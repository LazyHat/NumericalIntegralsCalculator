package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import utils.BuildConfig

@Composable
fun BottomBar(actions: @Composable (RowScope.() -> Unit)? = null) {
    BottomAppBar(modifier = Modifier.height(30.dp)) {
        RowVerticalCenteredContent(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            RowVerticalCenteredContent(
                horizontalArrangement = Arrangement.spacedBy(
                    20.dp
                )
            ) {
                Text("OS: ${BuildConfig.os}")
                Text("ver: ${BuildConfig.appVersion}")
                if (BuildConfig.debug) {
                    Text("DEBUG-VERSION")
                }
            }
            if (actions != null) RowVerticalCenteredContent(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                content = actions
            )
        }
    }
}