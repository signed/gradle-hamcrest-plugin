package com.github.signed.matchers.generator;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class FactoryMethodExceptions_Test {

    private FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);
    private ContextBuilder contextBuilder = new ContextBuilder();
    private final HamcrestFactoryMethodBuilder hamcrestFactoryMethodBuilder = contextBuilder.addFactoryMethod();


    @Test
    public void ifThereAreNoExceptionsDoNotReportAny() throws Exception {
        new FactoryMethodExceptions().performStep(builder, contextBuilder.createContext());
        verifyZeroInteractions(builder);
    }

    @Test
    public void passAllExceptionsToTheBuilder() throws Exception {
        hamcrestFactoryMethodBuilder.thatThrows(IllegalStateException.class);
        hamcrestFactoryMethodBuilder.thatThrows(RuntimeException.class);

        new FactoryMethodExceptions().performStep(builder, contextBuilder.createContext());

        verify(builder).throwsAn("java.lang.RuntimeException");
        verify(builder).throwsAn("java.lang.IllegalStateException");
    }
}
