package org.hamcrest.generator.config;

import org.hamcrest.generator.QDox;
import org.hamcrest.generator.QDoxFactoryReader;
import org.hamcrest.generator.ReflectiveFactoryReader;
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
import java.util.List;

public class SourceXmlConfigurator {

    private final SugarConfiguration sugarConfiguration;
    private List<File> sourceSetRoots;
    private final SAXParserFactory saxParserFactory;
    private final QDox qdox;

    public SourceXmlConfigurator(SugarConfiguration sugarConfiguration, List<File> sourceSetRoots) {
        this.sugarConfiguration = sugarConfiguration;
        this.sourceSetRoots = sourceSetRoots;
        saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        qdox = new QDox();
    }

    public void addSourceDir(File sourceDir) {
        qdox.addSourceTree(sourceDir);
    }

    public void load(InputSource inputSource)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(inputSource, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (localName.equals("factory")) {
                    String className = attributes.getValue("class");
                    try {
                        addClass(className);
                    } catch (ClassNotFoundException e) {
                        throw new SAXException("Cannot find Matcher class : " + className);
                    }
                }
            }
        });
    }

    private void addClass(String className) throws ClassNotFoundException {
        Class<?> cls = classLoader.loadClass(className);
        sugarConfiguration.addFactoryMethods(new QDoxFactoryReader(new ReflectiveFactoryReader(cls), qdox, className));
    }
}