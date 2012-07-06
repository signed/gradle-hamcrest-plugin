package com.github.signed.matchers.generator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
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

import static com.google.common.collect.Lists.newArrayList;

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

    public Iterator<FactoryMethod> testName() {
        if (null == cu) {
            return Collections.<FactoryMethod>emptyList().iterator();
        }
        return Iterables.transform(readFromSource(), new Function<FactoryMethodBuilder, FactoryMethod>() {
            @Override
            public FactoryMethod apply(FactoryMethodBuilder input) {
                return input.create();
            }
        }).iterator();
    }

    public static class FactoryMethodContext {
        public static CompilationUnit cu;
        public static MethodDeclaration methodDeclaration;
        public final TypeDeclaration typeDeclaration;

        public FactoryMethodContext(CompilationUnit cu, TypeDeclaration typeDeclaration, MethodDeclaration methodDeclaration) {
            FactoryMethodContext.cu = cu;
            this.typeDeclaration = typeDeclaration;
            FactoryMethodContext.methodDeclaration = methodDeclaration;
        }

        public static String getFullQualifiedTypeFromImports(String typeName) {
            for (ImportDeclaration importDeclaration : cu.getImports()) {
                StringBuilder fullQualifiedNameOfImport = new StringBuilder();
                importDeclaration.accept(new FullQualifiedNameExtractor(), fullQualifiedNameOfImport);
                if (fullQualifiedNameOfImport.toString().endsWith("." + typeName)) {
                    return fullQualifiedNameOfImport.toString();
                }
            }
            return "java.lang." + typeName;
        }

        public static String getMethodReturnType(MethodDeclaration declaration) {
            Type type = declaration.getType();
            StringBuilder className = new StringBuilder();
            ClassNameExtractor extract = new ClassNameExtractor();
            type.accept(extract, className);

            return getFullQualifiedTypeFromImports(className.toString());
        }
    }

    public static interface FactoryMethodPart {
        void performStep(FactoryMethodBuilder builder, FactoryMethodContext context);
    }

    private List<FactoryMethodBuilder> readFromSource() {
        List<FactoryMethodBuilder> factoryMethods = newArrayList();
        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            MatcherFactoryMethodExtractor matcherFactoryMethodExtractor = new MatcherFactoryMethodExtractor();
            typeDeclaration.accept(matcherFactoryMethodExtractor, null);


            for (MethodDeclaration methodDeclaration : matcherFactoryMethodExtractor) {
                FactoryMethodContext context = new FactoryMethodContext(cu, typeDeclaration, methodDeclaration);
                FactoryMethodBuilder builder = new FactoryMethodBuilder();

                new FactoryMethodContainingClass().performStep(builder, context);
                new FactoryMethodGenericTypeParameter().performStep(builder, context);
                new FactoryMethodReturnType().performStep(builder, context);
                new FactoryMethodReturnTypesGenericType().performStep(builder, context);
                new FactoryMethodName().performStep(builder, context);
                new FactoryMethodExceptions().performStep(builder, context);

                factoryMethods.add(builder);
            }
        }
        return factoryMethods;
    }

    @Override
    public Iterator<FactoryMethod> iterator() {
        return testName();
    }

    private class FactoryMethodContainingClass implements FactoryMethodPart {
        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            String thePackage = cu.getPackage().toString().replaceAll(";", "").replaceAll("package", "").trim();
            String className = context.typeDeclaration.getName();

            builder.isInClass(thePackage + "." + className);
        }
    }

    private class FactoryMethodGenericTypeParameter implements FactoryMethodPart {

        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            List<TypeParameter> typeParameters = context.methodDeclaration.getTypeParameters();
            for (TypeParameter typeParameter : typeParameters) {
                builder.withGenericTypeParameter(typeParameter.getName());
            }
        }
    }

    private class FactoryMethodReturnType implements FactoryMethodPart {
        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            builder.withReturnType(FactoryMethodContext.getMethodReturnType(context.methodDeclaration));
        }
    }

    private class FactoryMethodReturnTypesGenericType implements FactoryMethodPart {

        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            Type reference = context.methodDeclaration.getType();
            ArrayList<Type> typeArgs = new ArrayList<>();
            reference.accept(new VoidVisitorAdapter<List<Type>>() {
                @Override
                public void visit(ClassOrInterfaceType n, List<Type> arg) {
                    List<Type> typeArgs = n.getTypeArgs();
                    arg.addAll(typeArgs);
                }
            }, typeArgs);


            for (Type typeArgument : typeArgs) {
                StringBuilder doIt = new StringBuilder();
                typeArgument.accept(new ClassNameExtractor(), doIt);
                List<String> transformed = Lists.transform(context.methodDeclaration.getTypeParameters(), new Function<TypeParameter, String>() {
                    @Override
                    public String apply(TypeParameter input) {
                        return input.getName();
                    }
                });
                boolean isAGenericParameterOfTheMethod = transformed.contains(doIt.toString());
                String typeArg = doIt.toString();
                if (!isAGenericParameterOfTheMethod) {
                    typeArg = FactoryMethodContext.getFullQualifiedTypeFromImports(typeArg);
                }
                builder.withGenericReturnType(typeArg);
            }
        }
    }

    private class FactoryMethodName implements FactoryMethodPart {
        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            builder.isNamed(context.methodDeclaration.getName());
        }
    }

    private class FactoryMethodExceptions implements FactoryMethodPart {

        @Override
        public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
            List<NameExpr> aThrows = context.methodDeclaration.getThrows();
            for (NameExpr aThrow : aThrows) {
                String name = aThrow.getName();
                String exception = FactoryMethodContext.getFullQualifiedTypeFromImports(name);
                builder.throwsAn(exception);
            }
        }
    }
}