package com.github.signed.matchers.generator;

import org.hamcrest.generator.FactoryMethod;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class FactoryMethodBuilder {


    private String fullQualifiedClassName;
    private String methodName;
    private String returnType;
    private String genericPartOfReturnType;
    private final List<String> thrownExceptions = newArrayList();
    private final List<String> methodTypeParameters = newArrayList();
    private final List<FactoryMethod.Parameter> parameters = new ArrayList<>();

    public FactoryMethodBuilder isInClass(String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
        return this;
    }

    public FactoryMethodBuilder isNamed(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public FactoryMethodBuilder withReturnType(String fullQualifiedClassName) {
        this.returnType = fullQualifiedClassName;
        return this;
    }

    public FactoryMethodBuilder withGenericReturnType(String typeArg) {
        this.genericPartOfReturnType = typeArg;
        return this;
    }

    public FactoryMethod create() {
        final FactoryMethod factoryMethod = new FactoryMethod(this.fullQualifiedClassName, this.methodName, returnType);
        factoryMethod.setGenerifiedType(genericPartOfReturnType);
        for (String thrownException : thrownExceptions) {
            factoryMethod.addException(thrownException);
        }
        for (String methodTypeParameter : methodTypeParameters) {
            factoryMethod.addGenericTypeParameter(methodTypeParameter);
        }

        for (FactoryMethod.Parameter parameter : parameters) {
            factoryMethod.addParameter(parameter.getType(), parameter.getName());
        }

        return factoryMethod;
    }

    public FactoryMethodBuilder throwsAn(String exception) {
        thrownExceptions.add(exception);
        return this;
    }

    public FactoryMethodBuilder withGenericTypeParameter(String typeParameter) {
        methodTypeParameters.add(typeParameter);
        return this;
    }

    public FactoryMethodBuilder withParameter(String fullQualifiedClassName, String name) {
        this.parameters.add(new FactoryMethod.Parameter(fullQualifiedClassName, name));
        return this;
    }
}
