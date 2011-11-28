package org.hamcrest.generator.config;

import com.github.signed.matchers.generator.JavaParserFactoryReader;
import org.hamcrest.generator.SugarConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SourceXmlConfigurator {

    private final SugarConfiguration sugarConfiguration;
    private List<File> sourceSetRoots = new ArrayList<File>();
    private final SAXParserFactory saxParserFactory;

    public SourceXmlConfigurator(SugarConfiguration sugarConfiguration) {
        this.sugarConfiguration = sugarConfiguration;
        saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
    }

    public void addSourceDir(File sourceDir) {
        sourceSetRoots.add(sourceDir);
    }

    public void load(InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(inputSource, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (localName.equals("factory")) {
                    String className = attributes.getValue("class");

                    for(File sourceRoot:sourceSetRoots){
                        String pathToSource = className.replace('.', File.separatorChar) + ".java";
                        File fileWithFactoryMethod = new File(sourceRoot, pathToSource);
                        addClass(fileWithFactoryMethod);
                    }
                }
            }
        });
    }

    private void addClass(File fileWithFactoryMethod){
        JavaParserFactoryReader reader = new JavaParserFactoryReader(fileWithFactoryMethod.getAbsolutePath());
        sugarConfiguration.addFactoryMethods(reader);
    }
}