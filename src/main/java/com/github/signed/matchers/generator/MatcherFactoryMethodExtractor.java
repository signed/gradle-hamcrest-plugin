package com.github.signed.matchers.generator;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class MatcherFactoryMethodExtractor extends VoidVisitorAdapter<Void> implements Iterable<MethodDeclaration>{
    private List<MethodDeclaration> factoryMethods = new ArrayList<MethodDeclaration>();

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        boolean isStatic = ModifierSet.hasModifier(methodDeclaration.getModifiers(), Modifier.STATIC);
        boolean isPublic = ModifierSet.hasModifier(methodDeclaration.getModifiers(), Modifier.PUBLIC);

        if (isPublic && isStatic) {
            List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();
            for (AnnotationExpr annotation : annotations) {
                if ("Factory".equals(annotation.getName().getName())) {
                    factoryMethods.add(methodDeclaration);
                    return;
                }
            }
        }
    }

    @Override
    public Iterator<MethodDeclaration> iterator() {
        return factoryMethods.iterator();
    }
}
