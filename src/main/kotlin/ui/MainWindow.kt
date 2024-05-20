package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window

@OptIn(ExperimentalMaterial3Api::class)
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
                                TextField(modifier = Modifier.menuAnchor(),
                                    value = state.method.collectAsState().value.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("method") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded
                                        )
                                    })
                                DropdownMenu(expanded, { expanded = false }) {
                                    Method.entries.forEach {
                                        DropdownMenuItem(onClick = {
                                            state.method.value = it; expanded =
                                            false
                                        }, text = {
                                            Text(it.name)
                                        })
                                    }
                                }
                            }
                            val error =
                                state.functionError.collectAsState().value
                            TextField(value = state.functionInputText.collectAsState().value,
                                onValueChange = {
                                    state.functionInputText.value = it
                                },
                                label = { Text("function by x") },
                                isError = error != null,
                                supportingText = {
                                    AnimatedVisibility(error != null) {
                                        Text(
                                            error.orEmpty(),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                })
                            TextField(
                                value = state.subIntegralsString.collectAsState().value,
                                onValueChange = {
                                    state.subIntegralsString.value = it
                                },
                                label = { Text("count subIntegrals") },
                                isError = state.subIntegralsError.collectAsState().value
                            )
                            TextField(
                                value = state.fromString.collectAsState().value,
                                onValueChange = {
                                    state.fromString.value = it
                                },
                                label = { Text("From") },
                                isError = state.fromError.collectAsState().value
                            )
                            TextField(
                                value = state.toString.collectAsState().value,
                                onValueChange = {
                                    state.toString.value = it
                                },
                                label = { Text("To") },
                                isError = state.toError.collectAsState().value
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