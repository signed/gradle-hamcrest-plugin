package com.github.signed.matchers.generator;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tools.ant.filters.StringInputStream;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FactoryMethodParameters_Test {
    private final FactoryMethodBuilder builder = mock(FactoryMethodBuilder.class);

    @Test
    public void extractFirstParameterName() throws Exception {
        FactoryMethodContext context = createContext();

        new FactoryMethodParameters().performStep(builder, context);
        verify(builder).withParameter(Mockito.any(String.class), Mockito.eq("dependency"));
    }

    @Test
    public void codeModelFun() throws Exception {

        JCodeModel model = new JCodeModel();

        JClass toMatch = model.ref(File.class);

        JDefinedClass theClass = model._class("com.github.signed.matchers.generator.samplematchers.IsADirectory");
        theClass._extends(model.ref(TypeSafeMatcher.class).narrow(toMatch));


        JMethod method = theClass.method(JMod.STATIC|JMod.PUBLIC, int.class, "aCreationMethod");
        method.annotate(Factory.class);
        method.body()._return(JExpr._null());
        method.generify("First");
        method._throws(IllegalStateException.class);
        method._throws(NullPointerException.class);

        method.javadoc().add(0, "Some JavaDoc");

        JClass aDependency = model.directClass("com.github.signed.matchers.generator.samplematchers.ADependency");
        method.param(JMod.NONE, aDependency, "dependency");

        ToStringCodeWriter writer = new ToStringCodeWriter();
        model.build(writer);

        String content = writer.getContent();
        System.out.println(content);

        assertThat(content, is(new JavaCompilationUnitBuilder().writeIt()));

    }


    private FactoryMethodContext createContext() throws ParseException {
        StringInputStream inputStream = new StringInputStream(new JavaCompilationUnitBuilder().writeIt());
        CompilationUnit parse = JavaParser.parse(inputStream);
        TypeDeclaration type = parse.getTypes().get(0);
        MatcherFactoryMethodExtractor matcherFactoryMethodExtractor = new MatcherFactoryMethodExtractor();
        type.accept(matcherFactoryMethodExtractor, null);
        return new FactoryMethodContext(parse, type, methodDeclaration(matcherFactoryMethodExtractor));
    }

    private MethodDeclaration methodDeclaration(MatcherFactoryMethodExtractor matcherFactoryMethodExtractor) {
        return matcherFactoryMethodExtractor.iterator().next();
    }

    private static class ToStringCodeWriter extends CodeWriter {
        private final ByteArrayOutputStream content = new ByteArrayOutputStream();
        private final PrintStream out = new PrintStream(content);

        public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
            return new FilterOutputStream(out) {
                public void close() {
                    // don't let this stream close
                }
            };
        }

        public void close() throws IOException {
            out.close();
        }

        public String getContent(){
            try {
                return content.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
