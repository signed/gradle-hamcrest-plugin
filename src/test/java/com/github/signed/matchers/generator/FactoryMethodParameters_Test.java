package com.github.signed.matchers.generator;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodParameters_Test {
    private final FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);
    private JavaCompilationUnitBuilder classWithFactoryMethods = new JavaCompilationUnitBuilder();

    @Test
    public void extractFirstParameterName() throws Exception {

        HamcrestFactoryMethodBuilder method = classWithFactoryMethods.addFactoryMethod();

        FactoryMethodContext context = createContext();

        new FactoryMethodParameters().performStep(builder, context);

        verify(builder).withParameter(Mockito.any(String.class), Mockito.eq("dependency"));
    }

    private FactoryMethodContext createContext() throws ParseException {
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
