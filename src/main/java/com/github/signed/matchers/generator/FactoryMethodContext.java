package com.github.signed.matchers.generator;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.Type;

public class FactoryMethodContext {
    public final CompilationUnit cu;
    public final MethodDeclaration methodDeclaration;
    public final TypeDeclaration typeDeclaration;

    public FactoryMethodContext(CompilationUnit cu, TypeDeclaration typeDeclaration, MethodDeclaration methodDeclaration) {
        this.cu = cu;
        this.typeDeclaration = typeDeclaration;
        this.methodDeclaration = methodDeclaration;
    }

    public String getFullQualifiedTypeFromImports(String typeName) {
        for (ImportDeclaration importDeclaration : cu.getImports()) {
            StringBuilder fullQualifiedNameOfImport = new StringBuilder();
            importDeclaration.accept(new FullQualifiedNameExtractor(), fullQualifiedNameOfImport);
            if (fullQualifiedNameOfImport.toString().endsWith("." + typeName)) {
                return fullQualifiedNameOfImport.toString();
            }
        }
        return "java.lang." + typeName;
    }

    public String getMethodReturnType(MethodDeclaration declaration) {
        Type type = declaration.getType();
        StringBuilder className = new StringBuilder();
        ClassNameExtractor extract = new ClassNameExtractor();
        type.accept(extract, className);

        return getFullQualifiedTypeFromImports(className.toString());
    }
}
