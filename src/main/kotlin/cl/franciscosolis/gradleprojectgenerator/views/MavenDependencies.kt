package cl.franciscosolis.gradleprojectgenerator.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cl.franciscosolis.gradleprojectgenerator.BottomNav

data class MavenDependency(val groupId: String, val artifactId: String, val version: String, val type: String)

@Composable
fun MavenDependencies(nav: MutableState<Int>, items: List<MavenDependency>, onAddClick: (dependency: MavenDependency) -> Unit, onRemoveClick: (id: Int) -> Unit) {
    val lazyState = rememberLazyListState()
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxHeight(.75f).fillMaxWidth()){
                LazyColumn(state = lazyState, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    itemsIndexed(items){ index, dependency ->
                        Dependency(index, dependency, onRemoveClick)
                        Divider(modifier = Modifier.padding(2.dp))
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyState),
                    modifier = Modifier.padding(2.dp).fillMaxHeight()
                )
            }

            var showCreatorDialog by remember { mutableStateOf(false) }

            Dialog(
                onCloseRequest = { showCreatorDialog = false },
                visible = showCreatorDialog,
                title = "Dependency Creator",
                resizable = true,
                state = rememberDialogState(
                    size = DpSize(400.dp, 400.dp)
                )
            ) {
                DependencyCreator(onAddClick){
                    showCreatorDialog = false
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { showCreatorDialog = !showCreatorDialog }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1BD324),
                    contentColor = Color.White
                ), modifier = Modifier.padding(5.dp).fillMaxWidth()) {
                    Text("Add Dependency")
                }
            }
        }

        BottomNav(nav)
    }
}

@Composable
fun Dependency(index: Int, dependency: MavenDependency, onRemoveClick: (id: Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${dependency.type} - ${dependency.groupId}:${dependency.artifactId}" + if(dependency.version.isNotEmpty()) ":${dependency.version}" else "",
            modifier = Modifier.fillMaxWidth(.75f).padding(5.dp),
            style = TextStyle(
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
        )
        Button(onClick = { onRemoveClick(index) }, colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Red,
            contentColor = Color.White
        ), modifier = Modifier.padding(5.dp)) {
            Text("-")
        }
    }
}

@Composable
fun DependencyCreator(onAddClick: (dependency: MavenDependency) -> Unit, onDialogClose: () -> Unit) {
    var error by remember { mutableStateOf("") }

    Dialog(
        visible = error.isNotBlank(),
        title = "Error!",
        onCloseRequest = { error = "" },
        state = rememberDialogState(size = DpSize(width = 200.dp, height = 100.dp)),
        resizable = false,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()){
            Text(text = error, modifier = Modifier.padding(5.dp), style = TextStyle(color = Color.Red, fontSize = 15.sp))
            Button(onClick = { error = "" }, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF1BD324),
                contentColor = Color.White
            ), modifier = Modifier.padding(5.dp)) {
                Text("Ok!")
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(10.dp)){
        val groupId = remember { mutableStateOf("") }
        val artifactId = remember { mutableStateOf("") }
        val version = remember { mutableStateOf("") }
        val type = remember { mutableStateOf("implementation") }

        OutlinedTextField (
            value = groupId.value,
            onValueChange = { groupId.value = it },
            label = { Text("Group Id") },
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField (
            value = artifactId.value,
            onValueChange = { artifactId.value = it },
            label = { Text("Artifact Id") },
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField (
            value = version.value,
            onValueChange = { version.value = it },
            label = { Text("Version") },
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(5.dp))

        val dropdownItems = listOf("implementation", "testImplementation", "api", "testApi", "compile", "runtime", "system", "provided")
        var showDropdown by remember { mutableStateOf(false) }
        var currentIndex by remember { mutableStateOf(0) }
        DropdownMenu(
            expanded = showDropdown,
            selectedIndex = currentIndex,
            items = dropdownItems,
            onSelect = {
                currentIndex = it
                type.value = dropdownItems[it]
            },
            onDismissRequest = { showDropdown = false },
        ){
            Column {
                Text("Dependency Type:")
                Button(onClick = { showDropdown = true }) {
                    Text(
                        text = dropdownItems[currentIndex],
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row (horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onDialogClose, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ), modifier = Modifier.padding(5.dp)) {
                Text("Close")
            }
            Button(onClick = {
                error = if (groupId.value.isBlank() || artifactId.value.isBlank()) {
                    "All fields are required!"
                } else {
                    onAddClick(MavenDependency(groupId.value, artifactId.value, version.value, type.value))
                    groupId.value = ""
                    artifactId.value = ""
                    version.value = ""
                    ""
                }
            }, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF1BD324),
                contentColor = Color.White
            ), modifier = Modifier.padding(5.dp)) {
                Text("Add")
            }
        }
    }
}


@Composable
fun DropdownMenu(
    expanded: Boolean,
    selectedIndex: Int,
    items: List<String>,
    onSelect: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()){
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            content()
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth(.75f)
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                items.forEachIndexed { index, s ->
                    if (selectedIndex == index) {
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colors.primary,
                                ),
                            onClick = { onSelect(index) }
                        ) {
                            Text(
                                text = s,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSelect(index); onDismissRequest() }
                        ) {
                            Text(
                                text = s,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}