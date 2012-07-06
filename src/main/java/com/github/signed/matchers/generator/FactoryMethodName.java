package com.github.signed.matchers.generator;

class FactoryMethodName implements FactoryMethodPart {
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        builder.isNamed(context.methodDeclaration.getName());
    }
}
