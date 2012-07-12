package com.github.signed.matchers.generator;

import com.google.common.collect.Lists;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JavaCompilationUnitBuilder {
    private final List<HamcrestFactoryMethodBuilder> methodBuilders = Lists.newArrayList();
    private final JCodeModel model = new JCodeModel();

    public String createIt() {
        try {
            return internal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String internal() throws JClassAlreadyExistsException, IOException {
        JClass toMatch = model.ref(File.class);
        JDefinedClass theClass = model._class("com.github.signed.matchers.generator.samplematchers.IsADirectory");
        theClass._extends(model.ref(TypeSafeMatcher.class).narrow(toMatch));


        for (HamcrestFactoryMethodBuilder methodBuilder : methodBuilders) {
            methodBuilder.writeInto(theClass);
        }


        ToStringCodeWriter writer = new ToStringCodeWriter();
        model.build(writer);

        return writer.getContent();
    }

    public HamcrestFactoryMethodBuilder addFactoryMethod() {
        HamcrestFactoryMethodBuilder hamcrestFactoryMethodBuilder = new HamcrestFactoryMethodBuilder(model);
        methodBuilders.add(hamcrestFactoryMethodBuilder);
        return hamcrestFactoryMethodBuilder;
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

        public String getContent() {
            try {
                return content.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
