package com.github.signed.matchers.generator;

import japa.parser.ast.body.Parameter;

import java.util.List;

public class FactoryMethodParameters implements  FactoryMethodPart{
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        List<Parameter> parameters = context.methodDeclaration.getParameters();
        for (Parameter parameter : parameters) {
            String fullQualifiedType = context.getFullQualifiedTypeFromImports(parameter.getType());
            builder.withParameter(fullQualifiedType, parameter.getId().getName());
        }
    }
}
