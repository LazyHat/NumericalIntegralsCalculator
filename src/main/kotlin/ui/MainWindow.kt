package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainWindow(state: MainWindowState) {
    Window(
        onCloseRequest = state.exit,
        state = state.window,
        title = "NumericalIntegrating"
    ) {
        Scaffold(bottomBar = { BottomBar() }) { padding ->
            Row {
                Surface(modifier = Modifier.weight(2f)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ColumnHorizontalCenteredContent(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.align(Alignment.Center)
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(expanded,
                                { expanded = it }) {
                                TextField(value = state.method.collectAsState().value.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("method") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded
                                        ) { expanded = !expanded }
                                    })
                                DropdownMenu(expanded, { expanded = false }) {
                                    DropdownMenuItem({
                                        state.method.value =
                                            Method.LeftRectangle; expanded =
                                        false
                                    }) {
                                        Text("Left Rectangle")
                                    }
                                }
                            }
                            val error =
                                state.functionInputTextError.collectAsState().value
                            TextField(
                                value = state.functionInputText.collectAsState().value,
                                onValueChange = {
                                    state.functionInputText.value = it
                                },
                                label = { Text("function by x") },
                                isError = error != null
                            )
                            Text(
                                error ?: "", color = MaterialTheme.colors.error
                            )
                            TextField(
                                value = state.subIntegralsInputText.collectAsState().value,
                                onValueChange = {
                                    state.subIntegralsInputText.value = it
                                },
                                label = { Text("count subIntegrals") },
                                isError = state.subIntegralsInputTextError.collectAsState().value
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.weight(3f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text("Output: ${state.integralOutput.collectAsState().value}")
                    }
                }
            }
        }
    }
}
//item {
//
//                    }
//                    item {
//                        TextField(value = state.functionText.collectAsState().value,
//                            onValueChange = { state.functionText.value = it },
//                            label = { Text("function by x") })
//                    }
//                    item {
//                        TextField(value = state.functionText.collectAsState().value,
//                            onValueChange = { state.functionText.value = it },
//                            label = { Text("start") })
//                    }
//                    item {
//                        TextField(value = state.functionText.collectAsState().value, onValueChange = { state.functionText.value = it }, label = { Text("end") })
//                    }
//                    item {
//                        TextField(value = state.functionText.collectAsState().value, onValueChange = { state.functionText.value = it }, label = {
//                            Text("number of parts")
//                        })
//                    }