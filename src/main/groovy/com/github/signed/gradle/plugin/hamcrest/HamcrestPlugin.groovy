package com.github.signed.gradle.plugin.hamcrest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class HamcrestPlugin implements Plugin<Project> {
    def void apply(Project project) {
        project.task('hello') << {
            println "Hello from the GreetingPlugin"
        }
    }
}