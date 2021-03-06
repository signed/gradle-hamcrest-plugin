package com.github.signed.matchers.generator;

import com.github.signed.matchers.generator.samplematchers.IsADirectory;
import org.hamcrest.generator.FactoryMethod;
import org.hamcrest.generator.QDox;
import org.hamcrest.generator.QDoxFactoryReader;
import org.hamcrest.generator.ReflectiveFactoryReader;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Parser_Test {

    private ReflectiveFactoryReader reflectiveFactoryReader = new ReflectiveFactoryReader(IsADirectory.class);
    private QDoxFactoryReader qdox = new QDoxFactoryReader(reflectiveFactoryReader, new QDox(), IsADirectory.class.getName());
    private FactoryMethod expected;
    private final JavaParserFactoryReader reader = new JavaParserFactoryReader("src/test/java/com/github/signed/matchers/generator/samplematchers/IsADirectory.java");

    private FactoryMethod actual;

    @Before
    public void loadActualWithStaticCodeAnalysis() throws Exception {
        Iterator<FactoryMethod> iteratorActual = reader.testName();
        iteratorActual.hasNext();
        actual = iteratorActual.next();
    }

    @Before
    public void loadExpectedWithHamcrestReflectiveFactory() throws Exception {
        Iterator<FactoryMethod> iteratorExpected = qdox.iterator();
        iteratorExpected.hasNext();
        expected = iteratorExpected.next();
    }

    @Test
    public void sameMethodName() throws Exception {
        assertThat(actual.getName(), is(expected.getName()));
    }

    @Test
    public void sameReturnType() throws Exception {
        assertThat(actual.getReturnType(), is(expected.getReturnType()));
    }

    @Test
    public void sameMatcherClass() throws Exception {
        assertThat(actual.getMatcherClass(), is(expected.getMatcherClass()));
    }

    @Test
    public void sameGenerifiedType() throws Exception {
        assertThat(actual.getGenerifiedType(), is(expected.getGenerifiedType()));
    }

    @Test
    public void sameGenerifiedTypeParameters() throws Exception {
        assertThat(actual.getGenericTypeParameters(), is(expected.getGenericTypeParameters()));
    }

    @Test
    public void takeOverJavaDocFromFactoryMethod() throws Exception {
        assertThat(actual.getJavaDoc(), is(expected.getJavaDoc()));
    }

    @Test
    public void takeTheExceptionsThrownByTheFactoryMethod(){
        assertThat(actual.getExceptions(), is(expected.getExceptions()));
    }

    @Test
    public void takeTheDeclaredParameters() {
        assertThat(actual.getParameters().get(0).getType(), is(expected.getParameters().get(0).getType()));
    }
}