package com.github.signed.matchers.generator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class HamcrestFactoryMethodBuilder {
    private JCodeModel model;

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
        JClass aDependency = model.directClass("com.github.signed.matchers.generator.samplematchers.ADependency");
        method.param(JMod.NONE, aDependency, "dependency");
    }
}
