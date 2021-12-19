package cl.franciscosolis.gradleprojectgenerator

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import cl.franciscosolis.gradleprojectgenerator.views.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.lingala.zip4j.ZipFile
import java.io.File
import java.net.URL
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.deleteIfExists

fun main() = application {
    val windowState = rememberWindowState(width = 600.dp, height = 600.dp)
    val currentItem = remember { mutableStateOf(0) }

    // Basic
    val parentFolder = remember { mutableStateOf(System.getProperty("user.home") + File.separator + "Documents") }
    val projectName = remember { mutableStateOf("MyProject") }
    val groupId = remember { mutableStateOf("com.example") }
    val version = remember { mutableStateOf("0.1.0-SNAPSHOT") }
    val description = remember { mutableStateOf("This is my first gradle project!") }

    // Plugins
    val kotlinDsl = remember { mutableStateOf(true) }

    // Maven
    val repos = remember { mutableStateListOf(
        "mavenLocal",
        "mavenCentral",
        "https://jitpack.io",
        "https://oss.sonatype.org/content/repositories/snapshots/",
        "https://oss.sonatype.org/content/repositories/releases/",
        "https://oss.sonatype.org/content/groups/public/"
    ) }
    val dependencies = remember { mutableStateListOf<MavenDependency>() }
    val plugins = remember { mutableStateListOf(
        MavenPlugin("org.jetbrains.kotlin.jvm", "1.6.0"),
        MavenPlugin("maven-publish", ""),
        MavenPlugin("com.github.johnrengelman.shadow", "7.1.0"),
        MavenPlugin("org.jetbrains.dokka", "1.6.0"),
    ) }


    Window(onCloseRequest = ::exitApplication, title = "Gradle Project Generator - JB Compose", state = windowState) {
        var error by remember { mutableStateOf("") }

        Dialog(
            visible = error.isNotBlank(),
            title = "Error!",
            onCloseRequest = { error = "" },
            state = rememberDialogState(size = DpSize(250.dp, 150.dp)),
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

        when (currentItem.value) {
            0 -> SelectLocation(currentItem, parentFolder){ file ->
                val json = JsonParser.parseString(file.readText()).asJsonObject
                parentFolder.value = if(json.has("parentFolder")) json.get("parentFolder").asString else System.getProperty("user.home") + File.separator + "Documents"
                projectName.value = json.get("name").asString
                groupId.value = json.get("groupId").asString
                version.value = json.get("version").asString
                description.value = json.get("description").asString
                kotlinDsl.value = json.get("kotlin-dsl").asBoolean
                repos.clear()
                json.get("repos").asJsonArray.map { it.asString }.forEach(repos::add)
                dependencies.clear()
                json.get("dependencies").asJsonArray.map { it.asJsonObject }.forEach {
                    dependencies.add(MavenDependency(it.get("groupId").asString, it.get("artifactId").asString, it.get("version").asString, it.get("type").asString))
                }
                plugins.clear()
                json.get("plugins").asJsonArray.map { it.asJsonObject }.forEach {
                    plugins.add(MavenPlugin(it.get("id").asString, it.get("version").asString))
                }
                currentItem.value = 6
            }
            1 -> StringPropertiesEditor(currentItem, listOf(
                "Project Name" to projectName,
                "Group ID" to groupId,
                "Version" to version,
                "Description" to description
            ))
            2 -> BooleanPropertiesEditor(currentItem, listOf(
                "Kotlin DSL (Disable for Groovy DSL)" to kotlinDsl,
            ))
            3 -> StringListEditor(
                nav = currentItem,
                items = repos,
                onAddClick = {
                    if(!repos.contains(it)) {
                        repos.add(it)
                    }
                },
                onRemoveClick = repos::removeAt,
                addLabel = "Add Repository",
            )
            4 -> MavenDependencies(
                nav = currentItem,
                items = dependencies,
                onAddClick = dependencies::add,
                onRemoveClick = dependencies::removeAt
            )
            5 -> MavenPlugins(
                nav = currentItem,
                items = plugins,
                onAddClick = { pl ->
                    if(plugins.firstOrNull { it.id == pl.id } == null) {
                        plugins.add(pl)
                    }
                },
                onRemoveClick = plugins::removeAt
            )
            6 -> LastStep(
                nav = currentItem,
                startOver = {
                    parentFolder.value = System.getProperty("user.home") + File.separator + "Documents"
                    projectName.value = "MyProject"
                    groupId.value = "com.example"
                    version.value = "0.1.0-SNAPSHOT"
                    description.value = "This is my first gradle project!"
                    kotlinDsl.value = true
                    repos.clear()
                    repos.addAll(listOf(
                        "mavenLocal",
                        "https://repo.maven.apache.org/maven2",
                        "https://jitpack.io",
                        "https://oss.sonatype.org/content/repositories/snapshots/",
                        "https://oss.sonatype.org/content/repositories/releases/",
                        "https://oss.sonatype.org/content/groups/public/",
                    ))
                    dependencies.clear()
                    plugins.clear()
                    plugins.addAll(listOf(
                        MavenPlugin("org.jetbrains.kotlin.jvm", "1.6.0"),
                        MavenPlugin("maven-publish", ""),
                        MavenPlugin("com.github.johnrengelman.shadow", "7.1.0"),
                        MavenPlugin("org.jetbrains.dokka", "1.6.0"),
                    ))
                    currentItem.value = 0
                },
                onGenerateClick = {
                    val projectFolder = File(File(parentFolder.value), projectName.value)
                    if(projectFolder.exists()) {
                        error = "Project folder already exists!"
                        return@LastStep false
                    }

                    projectFolder.mkdirs()

                    val props = mapOf(
                        "name" to projectName.value,
                        "groupId" to groupId.value,
                        "version" to version.value,
                        "description" to description.value,
                        "repos" to repos.joinToString(":split:") {
                            Base64.getEncoder().encodeToString(it.toByteArray())
                        },
                        "dependencies" to dependencies.joinToString(":split:") {
                            Base64.getEncoder().encodeToString("${it.groupId}:${it.artifactId}:${it.version}:${it.type}".toByteArray())
                        },
                        "plugins" to plugins.joinToString(":split:") {
                            Base64.getEncoder().encodeToString("${it.id}:${it.version}".toByteArray())
                        },
                    )

                    if(kotlinDsl.value){
                        generateKTS(projectFolder, props)
                    }else{
                        generateGroovy(projectFolder, props)
                    }

                    try{
                        val latestGradleVersion = props["gradle-version"] ?: JsonParser.parseString(URL("https://api.github.com/repos/gradle/gradle/releases").readText()).asJsonArray.first().asJsonObject.get("name").asString
                        val latestGradleBin = URL("https://services.gradle.org/distributions/gradle-$latestGradleVersion-bin.zip").readBytes()
                        val onlineChecksum = URL("https://services.gradle.org/distributions/gradle-$latestGradleVersion-bin.zip.sha256").readText()
                        val localChecksumBytes = MessageDigest.getInstance("SHA-256").digest(latestGradleBin)
                        val localChecksumString = localChecksumBytes.joinToString("") { "%02x".format(it) }
                        if(localChecksumString != onlineChecksum) {
                            error = "Gradle binary checksum mismatch! (Online: $onlineChecksum, Local: $localChecksumString)"
                            return@LastStep false
                        }
                        File(projectFolder, "gradle-$latestGradleVersion-bin.zip").also {
                            it.writeBytes(latestGradleBin)
                            ZipFile(it).extractAll(projectFolder.absolutePath)
                        }
                        generateWrapper(projectFolder, latestGradleVersion)
                    }catch (e: Exception) {
                        error = "Could not download gradle!"
                        e.printStackTrace()
                        return@LastStep false
                    }

                    return@LastStep true
                },
                onGenerateTemplate = {
                    val file = File(File(parentFolder.value), "${projectName.value}.gp-template.json")
                    if(file.exists()) {
                        error = "Project template already exists!"
                        return@LastStep false
                    }
                    val json = JsonObject()
                    json.addProperty("parentFolder", parentFolder.value)
                    json.addProperty("name", projectName.value)
                    json.addProperty("groupId", groupId.value)
                    json.addProperty("version", version.value)
                    json.addProperty("description", description.value)
                    val reposJson = JsonArray()
                    repos.forEach {
                        reposJson.add(it)
                    }
                    json.add("repos", reposJson)
                    val dependenciesJson = JsonArray()
                    dependencies.forEach {
                        val dependencyJson = JsonObject()
                        dependencyJson.addProperty("groupId", it.groupId)
                        dependencyJson.addProperty("artifactId", it.artifactId)
                        dependencyJson.addProperty("version", it.version)
                        dependencyJson.addProperty("type", it.type)
                        dependenciesJson.add(dependencyJson)
                    }
                    json.add("dependencies", dependenciesJson)
                    val pluginsJson = JsonArray()
                    plugins.forEach {
                        val pluginJson = JsonObject()
                        pluginJson.addProperty("id", it.id)
                        pluginJson.addProperty("version", it.version)
                        pluginsJson.add(pluginJson)
                    }
                    json.add("plugins", pluginsJson)
                    json.addProperty("kotlin-dsl", kotlinDsl.value)
                    file.createNewFile()
                    file.writeBytes(json.toString().toByteArray())
                    return@LastStep true
                }
            )
            else -> {
                currentItem.value = currentItem.value-1
            }
        }
    }
}

@Composable
fun BottomNav(nav: MutableState<Int>, left: @Composable () -> Unit = {
    Button(onClick = {
        if(nav.value > 0){
            nav.value = nav.value.dec()
        }
    }) {
        Text(text = "Back")
    }
},  right: @Composable () -> Unit = {
    Button(onClick = { nav.value = nav.value.inc() }) {
        Text(text = "Next")
    }
}, horizontal: Boolean = true) {
    if(horizontal){
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            left()
            right()
        }
    }else{
        Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            left()
            right()
        }
    }
}