package com.github.signed.gradle.plugin.hamcrest;


import org.gradle.api.Plugin
import org.gradle.api.Project

class HamcrestPlugin implements Plugin<Project> {
    def void apply(Project project) {
        project.task('hello') << {
            if (project.plugins.hasPlugin('java')) {
                println 'java plugin applied'
                def sourcePaths = project.sourceSets*.java.srcDirs.flatten().join(',')
                println sourcePaths
                def destinationDir = new File(project.buildDir, 'generated-src')
                //destinationDir.mkdirs()
                println destinationDir
                def configurationFile = new File("file-matchers.xml")
                println "${configurationFile} exists: ${configurationFile.exists()}"
            }

            if (project.plugins.hasPlugin('groovy')) {
                println 'groovy plugin applied'
//                project.sourceSets.all.groovy.srcDirs*.each createDirs
            }

        }
        project.tasks.hello.description = 'Collects all factory methods and puts them in a single class'
    }
}