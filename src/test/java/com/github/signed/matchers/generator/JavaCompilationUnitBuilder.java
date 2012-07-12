package com.github.signed.matchers.generator;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class JavaCompilationUnitBuilder {

    public String createIt(){
        try {
            return internal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String internal() throws JClassAlreadyExistsException, IOException {
        JCodeModel model = new JCodeModel();
        JClass toMatch = model.ref(File.class);
        JDefinedClass theClass = model._class("com.github.signed.matchers.generator.samplematchers.IsADirectory");
        theClass._extends(model.ref(TypeSafeMatcher.class).narrow(toMatch));

        JClass matcher = model.ref(Matcher.class);

        JMethod method = theClass.method(JMod.STATIC | JMod.PUBLIC, matcher, "aCreationMethod");
        method.annotate(Factory.class);
        method.body()._return(JExpr._null());
        method.generify("First");
        JTypeVar first = method.typeParams()[0];
        method.type(matcher.narrow(first));
        method._throws(IllegalStateException.class);
        method._throws(NullPointerException.class);

        method.javadoc().add(0, "Some JavaDoc");


        JClass aDependency = model.directClass("com.github.signed.matchers.generator.samplematchers.ADependency");
        method.param(JMod.NONE, aDependency, "dependency");

        ToStringCodeWriter writer = new ToStringCodeWriter();
        model.build(writer);

        return writer.getContent();
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
