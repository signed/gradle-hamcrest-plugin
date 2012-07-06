package com.github.signed.matchers.generator;

public class FactoryMethodReturnType implements FactoryMethodPart {
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        builder.withReturnType(context.getMethodReturnType(context.methodDeclaration));
    }
}
