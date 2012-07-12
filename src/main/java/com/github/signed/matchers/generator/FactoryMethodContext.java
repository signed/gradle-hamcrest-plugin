package com.github.signed.matchers.generator;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.Type;

public class FactoryMethodContext {
    public final CompilationUnit compilationUnit;
    public final MethodDeclaration methodDeclaration;
    public final TypeDeclaration typeDeclaration;

    public FactoryMethodContext(CompilationUnit compilationUnit, TypeDeclaration typeDeclaration, MethodDeclaration methodDeclaration) {
        this.compilationUnit = compilationUnit;
        this.typeDeclaration = typeDeclaration;
        this.methodDeclaration = methodDeclaration;
    }

    public String getFullQualifiedTypeFromImports(String typeName) {
        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            StringBuilder fullQualifiedNameOfImport = new StringBuilder();
            importDeclaration.accept(new FullQualifiedNameExtractor(), fullQualifiedNameOfImport);
            if (fullQualifiedNameOfImport.toString().endsWith("." + typeName)) {
                return fullQualifiedNameOfImport.toString();
            }
        }

        String javaLangType = "java.lang." + typeName;

        try {
            Class.forName(javaLangType);
            return javaLangType;
        } catch (ClassNotFoundException e) {
            return thePackage()+"."+typeName;
        }
    }

    public String getMethodReturnType(MethodDeclaration declaration) {
        Type type = declaration.getType();
        StringBuilder className = new StringBuilder();
        ClassNameExtractor extract = new ClassNameExtractor();
        type.accept(extract, className);

        return getFullQualifiedTypeFromImports(className.toString());
    }

    public String getFullQualifiedTypeFromImports(Type type) {
        return getFullQualifiedTypeFromImports(type.toString());
    }

    public String thePackage() {
        return compilationUnit.getPackage().toString().replaceAll(";", "").replaceAll("package", "").trim();
    }
}
