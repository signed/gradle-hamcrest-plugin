package com.github.signed.matchers.generator;

import japa.parser.ast.body.MethodDeclaration;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class FactoryMethodGenericTypeParameter_Test {

    @Test
    public void copeWithMethodsThatDoNotHaveTypeParameters() throws Exception {
        FactoryMethodGenericTypeParameter factory = new FactoryMethodGenericTypeParameter();
        FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);
        FactoryMethodContext context = new FactoryMethodContext(null, null, new MethodDeclaration());
        factory.performStep(builder, context);
        verifyZeroInteractions(builder);
    }
}
