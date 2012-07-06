package com.github.signed.matchers.generator;

import japa.parser.ast.expr.NameExpr;

import java.util.List;

public class FactoryMethodExceptions implements FactoryMethodPart {

    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        List<NameExpr> aThrows = context.methodDeclaration.getThrows();
        for (NameExpr aThrow : aThrows) {
            String name = aThrow.getName();
            String exception = context.getFullQualifiedTypeFromImports(name);
            builder.throwsAn(exception);
        }
    }
}
