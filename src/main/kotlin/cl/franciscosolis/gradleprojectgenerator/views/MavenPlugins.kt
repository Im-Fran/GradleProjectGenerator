package cl.franciscosolis.gradleprojectgenerator.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cl.franciscosolis.gradleprojectgenerator.BottomNav

data class MavenPlugin(val id: String, val version: String)

@Composable
fun MavenPlugins(nav: MutableState<Int>, items: List<MavenPlugin>, onAddClick: (dependency: MavenPlugin) -> Unit, onRemoveClick: (id: Int) -> Unit) {
    val lazyState = rememberLazyListState()
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxHeight(.75f).fillMaxWidth()){
                LazyColumn(state = lazyState, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                    itemsIndexed(items){ index, dependency ->
                        Item(index, dependency, onRemoveClick)
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
                title = "Plugin Creator"
            ) {
                PluginCreator(onAddClick){
                    showCreatorDialog = false
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { showCreatorDialog = !showCreatorDialog }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1BD324),
                    contentColor = Color.White
                ), modifier = Modifier.padding(5.dp).fillMaxWidth()) {
                    Text("Add Plugin")
                }
            }
        }

        BottomNav(nav)
    }
}

@Composable
fun Item(index: Int, dependency: MavenPlugin, onRemoveClick: (id: Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = dependency.id + if(dependency.version.isNotBlank()) " - v${dependency.version}" else "",
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
fun PluginCreator(onAddClick: (dependency: MavenPlugin) -> Unit, onDialogClose: () -> Unit) {
    var error by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(10.dp)){
        var pluginId by remember { mutableStateOf("") }
        var version by remember { mutableStateOf("") }

        TextField (
            value = pluginId,
            onValueChange = { pluginId = it },
            label = { Text("Plugin Id") },
            modifier = Modifier.fillMaxWidth().padding(5.dp),
        )
        TextField (
            value = version,
            onValueChange = { version = it },
            label = { Text("Version (Leave blank if not needed)") },
            modifier = Modifier.fillMaxWidth().padding(5.dp),
        )

        Row (horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onDialogClose, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ), modifier = Modifier.padding(5.dp)) {
                Text("Close")
            }
            Button(onClick = {
                error = if (pluginId.isBlank()) {
                    "Plugin Id cannot be empty"
                } else {
                    onAddClick(MavenPlugin(pluginId, version))
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

    Dialog(
        visible = error.isNotBlank(),
        title = "Error!",
        onCloseRequest = { error = "" },
        state = rememberDialogState(size = DpSize(200.dp, 100.dp)),
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
}