package com.github.signed.matchers.generator;

import japa.parser.ast.body.MethodDeclaration;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodName_Test {
    private FactoryMethodContext context;
    private final FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);


    @Test
    public void extractTheMethodNameFromTheMethodDeclaration() throws Exception {
        declareAFactoryMethodWithName("theFactoryMethodsName");

        new FactoryMethodName().performStep(builder, context);

        verify(builder).isNamed("theFactoryMethodsName");
    }

    private void declareAFactoryMethodWithName(String methodName) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName(methodName);
        context = new FactoryMethodContext(null, null, methodDeclaration);
    }
}
