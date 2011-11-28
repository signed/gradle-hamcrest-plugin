package com.github.signed.matchers.generator;

import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class FullQualifiedNameExtractor extends VoidVisitorAdapter<StringBuilder> {

    @Override
    public void visit(QualifiedNameExpr n, StringBuilder arg) {
        n.getQualifier().accept(this, arg);
        arg.append("." + n.getName());
    }

    @Override
    public void visit(NameExpr n, StringBuilder arg) {
        arg.append(n.getName());
    }
}
