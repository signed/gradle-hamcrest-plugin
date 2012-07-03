package com.github.signed.matchers.generator;

import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class ClassNameExtractor extends VoidVisitorAdapter<StringBuilder> {

    @Override
    public void visit(ReferenceType n, StringBuilder arg) {
        Type theTypeDeclaration = n.getType();

        theTypeDeclaration.accept(this, arg);
    }

    @Override
    public void visit(ClassOrInterfaceType n, StringBuilder arg) {
        n.getTypeArgs();
        arg.append(n.getName());
    }
}
