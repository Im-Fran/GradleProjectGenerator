package cl.franciscosolis.gradleprojectgenerator.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.franciscosolis.gradleprojectgenerator.BottomNav
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

@Composable
fun SelectLocation(nav: MutableState<Int>, parentFolder: MutableState<String>, onLoadTemplate: (File) -> Unit) {
    Column (modifier = Modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = "This will be the parent folder of the project",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                ),
            )

            Text(
                text = "Current Project Location: ${parentFolder.value}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            )
        }

        Button(onClick = {
            val fileDialog = FileDialog(ComposeWindow())
            fileDialog.filenameFilter = FilenameFilter { dir, name -> File(dir,name).name.endsWith(".gp-template.json") }
            fileDialog.mode = FileDialog.LOAD
            fileDialog.directory = System.getProperty("user.home")
            fileDialog.title = "Select the template"
            fileDialog.isVisible = true
            if(fileDialog.file != null) {
                onLoadTemplate(File(fileDialog.directory, fileDialog.file))
            }
        }) {
            Text("Load Project Template")
        }

        BottomNav(
            nav = nav,
            left = {
                Button(onClick = {
                    System.setProperty("apple.awt.fileDialogForDirectories", "true")
                    val fileDialog = FileDialog(ComposeWindow())
                    fileDialog.filenameFilter = FilenameFilter { dir, name -> File(dir,name).isDirectory }
                    fileDialog.mode = FileDialog.LOAD
                    fileDialog.directory = System.getProperty("user.home")
                    fileDialog.title = "Select the project location"
                    fileDialog.isVisible = true
                    if(fileDialog.file != null) {
                        parentFolder.value = fileDialog.directory + fileDialog.file
                    }
                    System.setProperty("apple.awt.fileDialogForDirectories", "false")
                }) {
                    Text("Select new project location")
                }
            },
        )
    }
}