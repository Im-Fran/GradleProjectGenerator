package cl.franciscosolis.gradleprojectgenerator.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.franciscosolis.gradleprojectgenerator.BottomNav

@Composable
fun LastStep(nav: MutableState<Int>, startOver: () -> Unit, onGenerateClick: () -> Boolean, onGenerateTemplate: () -> Boolean) {
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        var showFinalDialog by remember { mutableStateOf(false) }
        var generating by remember { mutableStateOf(false) }
        var finished by remember { mutableStateOf(false) }
        var startOverWarn by remember { mutableStateOf(false) }
        var template by remember { mutableStateOf(false) }

        Dialog(
            visible = showFinalDialog,
            title = if(finished) "${if(template) "Template" else "Project"} Generated!" else if (generating) "Generating ${if(template) "Template" else "Project"}..." else "",
            onCloseRequest = { showFinalDialog = false}
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(10.dp)){
                if(finished) {
                    Text("${if(template) "Template" else "Project"} Generated!", style = TextStyle(fontSize = 20.sp, color = Color.Black))
                    Button(onClick = {
                        showFinalDialog = false
                        generating = false
                        finished = true
                        template = false
                    }, modifier = Modifier.padding(10.dp)) {
                        Text("Continue")
                    }
                } else if (generating) {
                    Text("Generating ${if(template) "Template" else "Project"}...", style = TextStyle(fontSize = 20.sp, color = Color.Black))
                    Thread {
                        if(if(template) onGenerateTemplate() else onGenerateClick()){
                            finished = true
                            template = false
                        }else{
                            showFinalDialog = false
                            generating = false
                            finished = false
                            startOverWarn = false
                            template = false
                        }
                    }.start()
                }
            }
        }

        Dialog(
            visible = startOverWarn,
            title = "Start Over?",
            onCloseRequest = { startOverWarn = false }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(10.dp)){
                Text("This is a destructive action. Are you sure you want to start over?")
                Button(onClick = { startOver() }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red
                )) {
                    Text("Yes, Start over!")
                }
            }
        }

        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            BottomNav(
                nav = nav,
                left = {
                    if(!finished) {
                        Button(onClick = {
                            if(nav.value > 0){
                                nav.value = nav.value.dec()
                            }
                        }, enabled = !generating) {
                            Text(text = "Back")
                        }
                    }
                },
                right = {
                    if(!finished){
                        Button(onClick = { generating = true;showFinalDialog=true;template = false; }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1BD324),
                            contentColor = Color.White
                        ), enabled = !generating){
                            Text(text = "Generate Project!")
                        }

                        Button(onClick = { generating = true;showFinalDialog=true;template=true; }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1BD324),
                            contentColor = Color.White
                        ), enabled = !generating){
                            Text(text = "Generate Template!")
                        }
                    }else{
                        Button(onClick = { startOverWarn = true;template = false; }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red
                        ), enabled = finished){
                            Text(text = "Start Over!")
                        }
                    }
                },
                horizontal = false,
            )
        }
    }
}