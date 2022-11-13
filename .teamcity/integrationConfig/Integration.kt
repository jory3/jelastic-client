package integrationConfig

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import publishCommitShortSha

object Integration : BuildType({
    id("Integration")
    name = "Integration"

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
        }
    }

    steps {
        publishCommitShortSha()
        script {
            name = "Build"
            scriptContent = """
                #! /bin/sh
                
                poetry build
            """.trimIndent()
        }
        script {
            name = "Test"
            scriptContent = """
                #! /bin/sh
                
                pyenv local 3.8.12 3.9.10 3.10.2
                tox -- -s -v --cov --cov-report term-missing --cov-report html --teamcity --cov-append test -n 4 --api-token=%system.jelastic.access-token% --jelastic-version=%jelastic.version% --commit-sha=%build.vcs.number% --jelastic-user-email=%system.jelastic.user-email%       
            """.trimIndent()
            dockerImage = "%system.docker-registry.group%/docker-tools/python-tests:a63df625"
            dockerPull = true
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
        }
        // TODO: we also need a separate build config that will push the wheel on tagging
        // TODO: we want to publish to pypi.org too, but only the non-dev versions
        script {
            name = "Publish"
            scriptContent = """
                #! /bin/sh
                
                set -e
                
                poetry config repositories.pypi-hosted https://%system.pypi-registry.hosted%/
                poetry config http-basic.pypi-hosted %system.package-manager.deployer.username% %system.package-manager.deployer.password%
                poetry publish -r pypi-hosted
            """.trimIndent()
        }
    }

    features {
        perfmon {
        }

        dockerSupport {
            cleanupPushedImages = true
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_3"
            }
        }
    }

    params {
        param("teamcity.vcsTrigger.runBuildInNewEmptyBranch", "true")
    }
})