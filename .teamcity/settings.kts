import integrationConfig.Integration
import integrationConfig.ReleaseToPypi
import jetbrains.buildServer.configs.kotlin.*

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {
    params {
        param("jelastic.version", "7.0.3")
    }

    val dockerToolsTag = "3bd97369"

    // TODO: add weekly trigger to the Integration config
    val integrationBuild = Integration(dockerTag = dockerToolsTag)
    // TODO: test this config
    val releaseBuild = ReleaseToPypi(dockerTag = dockerToolsTag)

    buildType(integrationBuild)
    buildType(releaseBuild)
}
