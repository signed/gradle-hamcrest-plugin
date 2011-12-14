package com.github.signed.gradle.plugin.hamcrest;


import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.plugins.JavaPlugin

class HamcrestPlugin implements Plugin<Project> {
    def void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class)
        project.extensions.hamcrest = new HamcrestPluginExtension()
        project.tasks.add('generate-sugar', GenerateSugarTask.class)
        project.tasks.'generate-sugar'.description = 'Collects all factory methods and puts them in a single class'
        project.getTasks().withType(AbstractCompile.class, new Action<Task>() {
            @Override
            public void execute(Task compileTask) {
                compileTask.dependsOn(project.tasks.'generate-sugar');
            }
        });
    }
}