package com.github.signed.matchers.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.generator.FactoryMethod;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JavaParserFactoryReader implements Iterable<FactoryMethod> {
    private CompilationUnit cu;

    public JavaParserFactoryReader(String pathToFile) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(pathToFile);
            cu = JavaParser.parse(in);
        } catch (Exception e) {
            cu = null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private String getFullQualifiedTypeFromImports(CompilationUnit cu, String typeName) {
        for (ImportDeclaration importDeclaration : cu.getImports()) {
            StringBuilder fullQualifiedNameOfImport = new StringBuilder();
            importDeclaration.accept(new FullQualifiedNameExtractor(), fullQualifiedNameOfImport);
            if (fullQualifiedNameOfImport.toString().endsWith("." + typeName)) {
                return fullQualifiedNameOfImport.toString();
            }
        }
        return "java.lang." + typeName;
    }

    private String getMethodReturnType(CompilationUnit cu, MethodDeclaration declaration) {
        Type type = declaration.getType();
        StringBuilder className = new StringBuilder();
        ClassNameExtractor extract = new ClassNameExtractor();
        type.accept(extract, className);

        return getFullQualifiedTypeFromImports(cu, className.toString());
    }

    public Iterator<FactoryMethod> testName() {
        if (null == cu) {
            return Collections.<FactoryMethod>emptyList().iterator();
        }
        return readFromSource().iterator();
    }

    public static class FactoryMethodBuilder {

        private String fullQualifiedClassName;
        private String methodName;
        private String returnType;

        public FactoryMethodBuilder isInClass(String fullQualifiedClassName) {
            this.fullQualifiedClassName = fullQualifiedClassName;
            return this;
        }

        public FactoryMethodBuilder isNamed(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public FactoryMethodBuilder withReturnType(String fullQualifiedClassName) {
            this.returnType = fullQualifiedClassName;
            return this;
        }

        public FactoryMethod create() {
            return new FactoryMethod(this.fullQualifiedClassName, this.methodName, returnType);
        }
    }

    private List<FactoryMethod> readFromSource() {
        List<FactoryMethod> factoryMethods = Lists.newArrayList();
        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            FactoryMethodBuilder theFactoryMethod = new FactoryMethodBuilder();
            MethodVisitor methodVisitor = new MethodVisitor();
            typeDeclaration.accept(methodVisitor, null);
            for (MethodDeclaration methodDeclaration : methodVisitor.getFactoryMethods()) {
                String thePackage = cu.getPackage().toString().replaceAll(";", "").replaceAll("package", "").trim();
                String className = typeDeclaration.getName();
                theFactoryMethod.isInClass(thePackage + "." + className);
                theFactoryMethod.withReturnType(getMethodReturnType(cu, methodDeclaration));
                theFactoryMethod.isNamed(methodDeclaration.getName());
                Type type = methodDeclaration.getType();
                ArrayList<Type> typeArgs = new ArrayList<>();
                type.accept(new VoidVisitorAdapter<List<Type>>() {
                    @Override
                    public void visit(ClassOrInterfaceType n, List<Type> arg) {
                        List<Type> typeArgs = n.getTypeArgs();
                        arg.addAll(typeArgs);
                    }
                }, typeArgs);

                FactoryMethod result = theFactoryMethod.create();

                for (Type typeArgument : typeArgs) {
                    StringBuilder doIt = new StringBuilder();
                    typeArgument.accept(new ClassNameExtractor(), doIt);
                    List<String> transformed = Lists.transform(methodDeclaration.getTypeParameters(), new Function<TypeParameter, String>() {
                        @Override
                        public String apply(TypeParameter input) {
                            return input.getName();
                        }
                    });
                    boolean isAGenericParameterOfTheMethod = transformed.contains(doIt.toString());
                    String typeArg = doIt.toString();
                    if (!isAGenericParameterOfTheMethod) {
                        typeArg = getFullQualifiedTypeFromImports(cu, typeArg);
                    }
                    result.setGenerifiedType(typeArg);
                }

                List<NameExpr> aThrows = methodDeclaration.getThrows();
                for (NameExpr aThrow : aThrows) {
                    String name = aThrow.getName();
                    String exception = getFullQualifiedTypeFromImports(cu, name);
                    result.addException(exception);
                }

                List<TypeParameter> typeParameters = methodDeclaration.getTypeParameters();
                for (TypeParameter typeParameter : typeParameters) {
                    result.addGenericTypeParameter(typeParameter.getName());
                }

                factoryMethods.add(result);
            }
        }
        return factoryMethods;
    }

    @Override
    public Iterator<FactoryMethod> iterator() {
        return testName();
    }
}