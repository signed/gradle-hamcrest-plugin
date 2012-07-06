package com.github.signed.matchers.generator;

import japa.parser.ast.TypeParameter;

import java.util.List;

public class FactoryMethodGenericTypeParameter implements FactoryMethodPart {

    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        List<TypeParameter> typeParameters = context.methodDeclaration.getTypeParameters();
        for (TypeParameter typeParameter : typeParameters) {
            builder.withGenericTypeParameter(typeParameter.getName());
        }
    }
}
