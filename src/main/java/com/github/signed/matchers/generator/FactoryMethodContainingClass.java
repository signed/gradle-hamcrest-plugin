package com.github.signed.matchers.generator;

public class FactoryMethodContainingClass implements FactoryMethodPart {
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        String thePackage = context.cu.getPackage().toString().replaceAll(";", "").replaceAll("package", "").trim();
        String className = context.typeDeclaration.getName();

        builder.isInClass(thePackage + "." + className);
    }
}
