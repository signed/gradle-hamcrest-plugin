package com.github.signed.matchers.generator;

import com.github.signed.matchers.generator.samplematchers.ADependency;
import japa.parser.ParseException;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodParameters_Test {
    private final FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);
    private final FactoryContextBuilder factoryContextBuilder = new FactoryContextBuilder();
    private HamcrestFactoryMethodBuilder method = factoryContextBuilder.addFactoryMethod();

    @Test
    public void extractFirstParameterName() throws Exception {
        method.addParameter(ADependency.class, "dependency");
        passToPartToExtractInformation();

        verify(builder).withParameter(Mockito.any(String.class), Mockito.eq("dependency"));
    }

    @Test
    public void extractSecondParameterName() throws Exception {
        method.addParameter(ADependency.class, "dependency");
        method.addParameter(ADependency.class, "anotherDependency");
        passToPartToExtractInformation();

        verify(builder).withParameter(Mockito.any(String.class), Mockito.eq("anotherDependency"));
    }

    @Test
    public void extractFirstParameterType() throws Exception {
        method.addParameter(String.class, "doNotCare");
        passToPartToExtractInformation();

        verify(builder).withParameter(Mockito.eq("java.lang.String"), Mockito.any(String.class));
    }

    private void passToPartToExtractInformation() throws ParseException {
        FactoryMethodContext context = factoryContextBuilder.createContext();
        new FactoryMethodParameters().performStep(builder, context);
    }
}
