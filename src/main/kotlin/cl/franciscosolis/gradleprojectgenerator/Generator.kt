package cl.franciscosolis.gradleprojectgenerator

import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists

fun generateKTS(folder: File, props: Map<String, String>){
    val plugins = (props["plugins"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }
    val dependencies = (props["dependencies"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }
    val repos = (props["repos"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }

    val settingsKts = File(folder, "settings.gradle.kts")
    if(settingsKts.exists()) settingsKts.delete()
    settingsKts.createNewFile()
    settingsKts.writeText("rootProject.name = \"${props["name"]}\"")

    val buildGradleKts = File(folder, "build.gradle.kts")
    if(buildGradleKts.exists()) buildGradleKts.delete()
    buildGradleKts.createNewFile()
    buildGradleKts.appendText("plugins {")
    plugins.forEach {
        val id = it.split(":")[0]
        val version = it.split(":")[1]
        buildGradleKts.appendText("\n")
        if(version.isBlank()){
            buildGradleKts.appendText("\tid(\"$id\")")
        }else{
            buildGradleKts.appendText("\tid(\"$id\") version \"$version\" ")
        }
    }
    buildGradleKts.appendText("\n}\n\n")
    buildGradleKts.appendText(
        """
            group = "${props["groupId"]}"
            version = "${props["version"]}"
            description = "${props["description"]}"
        """.trimIndent()
    )


    if(repos.isNotEmpty()){
        buildGradleKts.appendText("\n\nrepositories {")
        repos.forEach {
            buildGradleKts.appendText("\n")
            if(it.startsWith("http")){
                buildGradleKts.appendText("\tmaven(\"$it\")")
            }else{
                buildGradleKts.appendText("\t$it()")
            }
        }
        buildGradleKts.appendText("\n}\n\n")
    }

    if(dependencies.isNotEmpty()){
        buildGradleKts.appendText(
            """
            dependencies {
        """.trimIndent()
        )
        dependencies.forEach {
            val groupId = it.split(":")[0]
            val artifactId = it.split(":")[1]
            val version = it.split(":")[2]
            val type = if(it.split(":").size > 3) it.split(":")[3] else "implementation"
            buildGradleKts.appendText("\n")
            if(version.isBlank()){
                buildGradleKts.appendText("\t$type(\"$groupId:$artifactId\")")
            }else{
                buildGradleKts.appendText("\t$type(\"$groupId:$artifactId:$version\")")
            }
        }
        buildGradleKts.appendText("\n}\n\n")
    }
}

fun generateGroovy(folder: File, props: Map<String, String>){
    val plugins = (props["plugins"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }
    val dependencies = (props["dependencies"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }
    val repos = (props["repos"] ?: "").split(":split:").filter{ it.isNotBlank() }.map { String(Base64.getDecoder().decode(it)) }

    val settingsKts = File(folder, "settings.gradle")
    if(settingsKts.exists()) settingsKts.delete()
    settingsKts.createNewFile()
    settingsKts.writeText("rootProject.name = '${props["name"]}'")

    val buildGradleKts = File(folder, "build.gradle")
    if(buildGradleKts.exists()) buildGradleKts.delete()
    buildGradleKts.createNewFile()
    buildGradleKts.appendText("plugins {")
    plugins.forEach {
        val id = it.split(":")[0]
        val version = it.split(":")[1]
        buildGradleKts.appendText("\n")
        if(version.isBlank()){
            buildGradleKts.appendText("\tid '$id' ")
        }else{
            buildGradleKts.appendText("\tid '$id' version '$version' ")
        }
    }
    buildGradleKts.appendText("\n}\n\n")
    buildGradleKts.appendText(
        """
            group = '${props["groupId"]}'
            version = '${props["version"]}'
            description = '${props["description"]}'
            
        """.trimIndent()
    )

    if(repos.isNotEmpty()){
        buildGradleKts.appendText("\n\nrepositories {")
        repos.forEach {
            buildGradleKts.appendText("\n")
            if(it.startsWith("http")){
                buildGradleKts.appendText("\tmaven { url '$it'}")
            }else{
                buildGradleKts.appendText("\t$it()")
            }
        }
        buildGradleKts.appendText("\n}\n\n")
    }

    if(dependencies.isNotEmpty()){
        buildGradleKts.appendText(
            """
            dependencies {
        """.trimIndent()
        )
        dependencies.forEach {
            val groupId = it.split(":")[0]
            val artifactId = it.split(":")[1]
            val version = it.split(":")[2]
            val type = if(it.split(":").size > 3) it.split(":")[3] else "implementation"
            buildGradleKts.appendText("\n")
            if(version.isBlank()){
                buildGradleKts.appendText("\t$type '$groupId:$artifactId'")
            }else{
                buildGradleKts.appendText("\t$type '$groupId:$artifactId:$version'")
            }
        }
        buildGradleKts.appendText("\n}\n\n")
    }


}

fun generateWrapper(folder: File, gradleVersion: String){
    val gradleBin = if(System.getProperty("os.name").lowercase().contains("windows")) "gradle-$gradleVersion/bin/gradle.bat" else "gradle-$gradleVersion/bin/gradle"
    val process = ProcessBuilder(gradleBin, "wrapper").directory(folder).start()
    process.waitFor()
    deleteRecursive(File(folder, "gradle-$gradleVersion-bin.zip"))
    deleteRecursive(File(folder, "gradle-$gradleVersion/"))
}

fun deleteRecursive(fileOrDirectory: File) {
    if (fileOrDirectory.isDirectory)
        fileOrDirectory.listFiles()?.forEach { deleteRecursive(it) }
    if (fileOrDirectory.exists())
        fileOrDirectory.toPath().deleteIfExists()
}