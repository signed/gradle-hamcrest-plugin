package com.github.signed.matchers.generator;

import com.github.signed.matchers.generator.samplematchers.ADependency;
import japa.parser.ParseException;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodParameters_Test {
    private final FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);
    private final ContextBuilder contextBuilder = new ContextBuilder();
    private HamcrestFactoryMethodBuilder method = contextBuilder.addFactoryMethod();

    @Test
    public void extractFirstParameterName() throws Exception {
        method.addParameter(ADependency.class, "parameterName");
        passToPartToExtractInformation();

        verify(builder).withParameter(any(String.class), eq("parameterName"));
    }

    @Test
    public void extractSecondParameterName() throws Exception {
        method.addParameter(ADependency.class, "doNotCare");
        method.addParameter(ADependency.class, "anotherArgument");
        passToPartToExtractInformation();

        verify(builder).withParameter(any(String.class), eq("anotherArgument"));
    }

    @Test
    public void extractFirstParameterType() throws Exception {
        method.addParameter(String.class, "doNotCare");
        passToPartToExtractInformation();

        verify(builder).withParameter(eq("java.lang.String"), any(String.class));
    }

    private void passToPartToExtractInformation() throws ParseException {
        FactoryMethodContext context = contextBuilder.createContext();
        new FactoryMethodParameters().performStep(builder, context);
    }
}
