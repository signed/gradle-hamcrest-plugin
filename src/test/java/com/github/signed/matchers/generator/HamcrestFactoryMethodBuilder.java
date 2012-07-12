package com.github.signed.matchers.generator;

import com.google.common.collect.Lists;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;

public class HamcrestFactoryMethodBuilder {
    private final JCodeModel model;
    private final List<Parameter> parameters = Lists.newArrayList();

    public HamcrestFactoryMethodBuilder(JCodeModel model) {
        this.model = model;
    }

    public void writeInto(JDefinedClass theClass) {
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
        for (Parameter parameter : parameters) {
            method.param(JMod.NONE, parameter.type, parameter.name);
        }
    }

    public HamcrestFactoryMethodBuilder addParameter(Class<?> type, String name) {
        JType modelType = model.ref(type);
        parameters.add(new Parameter(modelType, name));
        return this;
    }

    private static class Parameter{
        public final JType type;
        public final String name;

        private Parameter(JType type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
