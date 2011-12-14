package com.github.signed.gradle.plugin.hamcrest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.hamcrest.generator.HamcrestFactoryWriter
import org.hamcrest.generator.QuickReferenceWriter
import org.hamcrest.generator.SugarGenerator
import org.hamcrest.generator.config.SourceXmlConfigurator
import org.xml.sax.InputSource

class GenerateSugarTask extends DefaultTask {

    @TaskAction
    public generateSugar() {
        String configFile = new File(project.hamcrest.configurationFile).getAbsolutePath()
        String srcDirs = project.sourceSets*.java.srcDirs.flatten().join(',')
        String fullClassName = getFullQualifiedNameForMatchersClass();
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

    private String getFullQualifiedNameForMatchersClass() {
        return getPackageForMatchersClass() + '.' + project.hamcrest.nameForMatcherClass
    }

    private getPackageForMatchersClass() {
        project.hamcrest.packageForMatcherClass?:project.group
    }
}