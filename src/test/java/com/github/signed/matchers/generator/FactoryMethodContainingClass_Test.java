package com.github.signed.matchers.generator;

import japa.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodContainingClass_Test {
    private final ContextBuilder contextBuilder = new ContextBuilder();
    private final FactoryMethodBuilder factoryMethod = mock(FactoryMethodBuilder.class);

    @Before
    public void addAnyFactoryMethod() throws Exception {
        contextBuilder.addFactoryMethod();
    }

    @Test
    public void passTheFullQualifiedNameOfTheContainingClass() throws Exception {
        contextBuilder.theFactoryMethodsAreInClass("the.type.containing.the.factory.Method");
        createContextAndAnalyseSource();

        verify(factoryMethod).isInClass("the.type.containing.the.factory.Method");
    }

    private void createContextAndAnalyseSource() throws ParseException {
        FactoryMethodContext context = contextBuilder.createContext();
        new FactoryMethodContainingClass().performStep(factoryMethod, context);
    }
}
