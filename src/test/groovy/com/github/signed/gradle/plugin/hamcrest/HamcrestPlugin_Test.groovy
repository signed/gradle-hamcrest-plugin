package com.github.signed.gradle.plugin.hamcrest;


import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

public class HamcrestPlugin_Test extends Specification {
    private Project project = ProjectBuilder.builder().build()

    def addsGenerateSugarTask() {
        when:
        project.apply plugin: 'hamcrest'

        then:
        project.tasks.'generate-sugar' != null
    }

    def addExtensionForHamcrest(){
        when:
        project.apply plugin : 'hamcrest'

        then:
        project.extensions.hamcrest != null
    }
}