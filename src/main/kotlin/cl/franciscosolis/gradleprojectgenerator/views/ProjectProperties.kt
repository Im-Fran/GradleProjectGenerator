package cl.franciscosolis.gradleprojectgenerator.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.franciscosolis.gradleprojectgenerator.BottomNav

@Composable
fun StringPropertiesEditor(nav: MutableState<Int>, props: List<Pair<String, MutableState<String>>> = listOf()) {
    val lazyState = rememberLazyListState()
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Box(modifier = Modifier.fillMaxHeight(.75f).fillMaxWidth()){
            LazyColumn (state = lazyState, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                items(props) { prop ->
                    TextField(
                        value = prop.second.value,
                        onValueChange = { prop.second.value = it },
                        label = { Text("${prop.first}:") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(5.dp)
                    )
                    Divider(modifier = Modifier.padding(2.dp))
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyState),
            )
        }

        BottomNav(nav)
    }
}

@Composable
fun BooleanPropertiesEditor(nav: MutableState<Int>, props: List<Pair<String, MutableState<Boolean>>> = listOf()) {
    val lazyState = rememberLazyListState()
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Box(modifier = Modifier.fillMaxHeight(.75f).fillMaxWidth()){
            LazyColumn  (state = lazyState, horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                items(props) { prop ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "${prop.first}:")
                        Switch(
                            checked = prop.second.value,
                            onCheckedChange = { prop.second.value = it },
                        )
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyState),
                modifier = Modifier.padding(2.dp).fillMaxHeight()
            )
        }

        BottomNav(nav)
    }
}

@Composable
fun StringListEditor(nav: MutableState<Int>, items: List<String>, onAddClick: (url: String) -> Unit, onRemoveClick: (id: Int) -> Unit, addLabel: String) {
    val lazyState = rememberLazyListState()
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxHeight(.75f).fillMaxWidth()){
                LazyColumn(state = lazyState, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    itemsIndexed(items){ index, repo ->
                        Item(index, repo, onRemoveClick)
                        Divider(modifier = Modifier.padding(2.dp))
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyState),
                    modifier = Modifier.padding(2.dp).fillMaxHeight()
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                var repoUrl by remember { mutableStateOf("") }

                TextField (
                    value = repoUrl,
                    onValueChange = { repoUrl = it },
                    label = { Text(addLabel) },
                    modifier = Modifier.fillMaxWidth(.75f).padding(5.dp),
                    singleLine = true
                )
                Button(onClick = {
                    onAddClick(repoUrl)
                    repoUrl = ""
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1BD324),
                    contentColor = Color.White
                ), modifier = Modifier.padding(5.dp)) {
                    Text("+")
                }
            }
        }

        BottomNav(nav)
    }
}

@Composable
private fun Item(index: Int, item: String, onRemoveClick: (id: Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = item,
            modifier = Modifier.fillMaxWidth(.75f).padding(5.dp),
            style = TextStyle(
                fontSize = 15.sp,
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