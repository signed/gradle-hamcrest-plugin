package com.github.signed.gradle.plugin.hamcrest;


import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.AbstractCompile
import org.hamcrest.generator.HamcrestFactoryWriter
import org.hamcrest.generator.QuickReferenceWriter
import org.hamcrest.generator.SugarGenerator
import org.hamcrest.generator.config.SourceXmlConfigurator
import org.xml.sax.InputSource

class HamcrestPlugin implements Plugin<Project> {
    def void apply(Project project) {
        project.extensions.hamcrest = new HamcrestPluginExtension()
        project.getPlugins().apply(JavaPlugin.class)
        project.task('generate-sugar') << {
            if (project.plugins.hasPlugin('java')) {
                generateSyrup(project)
            }
        }
        project.tasks.'generate-sugar'.description = 'Collects all factory methods and puts them in a single class'
        project.tasks.'generate-sugar'.conventionMapping.matcherClassPackage = {project.group}

        project.getTasks().withType(AbstractCompile.class, new Action<Task>() {
            @Override
            public void execute(Task compileTask) {
                compileTask.dependsOn(project.tasks.'generate-sugar');
            }
        });

    }

    private def generateSyrup(Project project) {
        def configuration = project.hamcrest
        String configFile = new File(configuration.configurationFile).getAbsolutePath()
        String srcDirs = project.sourceSets*.java.srcDirs.flatten().join(',')
        String fullClassName = configuration.matcherClassPackage+'.'+configuration.matcherClass;
        File outputDir = new File(project.buildDir, 'generated-src')

        String fileName = fullClassName.replace((char) '.', File.separatorChar) + ".java";
        int dotIndex = fullClassName.lastIndexOf(".");
        String packageName = dotIndex == -1 ? "" : fullClassName.substring(0, dotIndex);
        String shortClassName = fullClassName.substring(dotIndex + 1);

        outputDir.mkdirs()
        File outputFile = new File(outputDir, fileName);
        outputFile.getParentFile().mkdirs();

        SugarGenerator sugarGenerator = new SugarGenerator();
        try {
            sugarGenerator.addWriter(new HamcrestFactoryWriter(packageName, shortClassName, new FileWriter(outputFile)));
            sugarGenerator.addWriter(new QuickReferenceWriter(System.out));

            SourceXmlConfigurator xmlConfigurator = new SourceXmlConfigurator(sugarGenerator)
            if (srcDirs.trim().length() > 0) {
                for (String srcDir: srcDirs.split(",")) {
                    xmlConfigurator.addSourceDir(new File(srcDir));
                }
            }
            xmlConfigurator.load(new InputSource(configFile));

            System.out.println("Generating " + fullClassName);
            sugarGenerator.generate();
        } finally {
            sugarGenerator.close();
        }

    }
}