package com.github.signed.matchers.generator;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.tools.ant.filters.StringInputStream;

public class FactoryContextBuilder {

    private JavaCompilationUnitBuilder classWithFactoryMethods = new JavaCompilationUnitBuilder();

    public HamcrestFactoryMethodBuilder addFactoryMethod() {
        return classWithFactoryMethods.addFactoryMethod();
    }


    public FactoryMethodContext createContext() throws ParseException {
        StringInputStream inputStream = new StringInputStream(classWithFactoryMethods.createIt());
        CompilationUnit parse = JavaParser.parse(inputStream);
        TypeDeclaration type = parse.getTypes().get(0);
        MatcherFactoryMethodExtractor matcherFactoryMethodExtractor = new MatcherFactoryMethodExtractor();
        type.accept(matcherFactoryMethodExtractor, null);
        return new FactoryMethodContext(parse, type, methodDeclaration(matcherFactoryMethodExtractor));
    }

    private MethodDeclaration methodDeclaration(MatcherFactoryMethodExtractor matcherFactoryMethodExtractor) {
        return matcherFactoryMethodExtractor.iterator().next();
    }
}
