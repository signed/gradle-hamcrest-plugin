package com.github.signed.matchers.generator;

public class FactoryMethodContainingClass implements FactoryMethodPart {
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        String thePackage = context.thePackage();
        String className = context.typeDeclaration.getName();

        builder.isInClass(thePackage + "." + className);
    }

}
