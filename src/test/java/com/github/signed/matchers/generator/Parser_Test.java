package com.github.signed.matchers.generator;

import com.github.signed.matchers.generator.samplematchers.IsADirectory;
import com.google.common.collect.Lists;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.generator.FactoryMethod;
import org.hamcrest.generator.ReflectiveFactoryReader;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Parser_Test {
    private ReflectiveFactoryReader reflectiveFactoryReader = new ReflectiveFactoryReader(IsADirectory.class);
    private FactoryMethod expected;
    private FactoryMethod actual;

    @Before
    public void setUp() throws Exception {
        Iterator<FactoryMethod> iteratorExpected = reflectiveFactoryReader.iterator();
        iteratorExpected.hasNext();
        expected = iteratorExpected.next();

        Iterator<FactoryMethod> iteratorActual = testName("src/test/java/com/github/signed/matchers/generator/samplematchers/IsADirectory.java");
        iteratorActual.hasNext();
        actual = iteratorActual.next();
    }

    @Test
    public void sameMethodName() throws Exception {
        assertThat(actual.getName(), is(expected.getName()));
    }

    @Test
    public void sameReturnType() throws Exception {
        assertThat(actual.getReturnType(), is(expected.getReturnType()));
    }

    @Test
    public void sameMatcherClass() throws Exception {
        assertThat(actual.getMatcherClass(), is(expected.getMatcherClass()));
    }

    @Test
    public void sameGenerifiedType() throws Exception {
        assertThat(actual.getGenerifiedType(), is(expected.getGenerifiedType()));
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        private List<MethodDeclaration> factoryMethods = new ArrayList<MethodDeclaration>();

        public Iterable<MethodDeclaration> getFactoryMethods() {
            return factoryMethods;
        }

        @Override
        public void visit(MethodDeclaration methodDeclaration, Object arg) {
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
    }

    public Iterator<FactoryMethod> testName(String pathToFile) {
        // creates an input stream for the file to be parsed
        CompilationUnit cu;
        FileInputStream in = null;
        try {
            in = new FileInputStream(pathToFile);
            // parse the file
            cu = JavaParser.parse(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        List<FactoryMethod> factoryMethods = Lists.newArrayList();
        String path = "org.hamcrest.Factory";

        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            MethodVisitor methodVisitor = new MethodVisitor();
            typeDeclaration.accept(methodVisitor, null);
            for (MethodDeclaration methodDeclaration : methodVisitor.getFactoryMethods()) {
                String thePackage = cu.getPackage().toString().replaceAll(";", "").replaceAll("package", "").trim();
                String className = typeDeclaration.getName();
                String methodName = methodDeclaration.getName();
                String fullQualifiedClassName = thePackage + "." + className;
                String methodReturnType = getMethodReturnType(cu, methodDeclaration);
                Type type = methodDeclaration.getType();
                ArrayList<Type> typeArgs = new ArrayList<Type>();
                type.accept(new VoidVisitorAdapter<List<Type>>() {
                    @Override
                    public void visit(ClassOrInterfaceType n, List<Type> arg) {
                        List<Type> typeArgs = n.getTypeArgs();
                        arg.addAll(typeArgs);
                    }
                }, typeArgs);

                FactoryMethod result = new FactoryMethod(fullQualifiedClassName, methodName, methodReturnType);
                factoryMethods.add(result);


                for(Type typeArgument:typeArgs){
                    StringBuilder doIt = new StringBuilder();
                    typeArgument.accept(new ClassNameExtractor(), doIt);
                    String typeArg = getFullQualifiedTypeFromImports(cu, doIt.toString());
                    result.setGenerifiedType(typeArg);
                }
            }
        }
        return factoryMethods.iterator();
    }

    public static class FullQualifiedNameExtractor extends VoidVisitorAdapter<StringBuilder> {

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

    private String getMethodReturnType(CompilationUnit cu, MethodDeclaration declaration) {
        Type type = declaration.getType();
        StringBuilder className = new StringBuilder();
        ClassNameExtractor extract = new ClassNameExtractor();
        type.accept(extract, className);
        return getFullQualifiedTypeFromImports(cu, className.toString());
    }

    private String getFullQualifiedTypeFromImports(CompilationUnit cu, String typeName) {
        for (ImportDeclaration importDeclaration : cu.getImports()) {
            StringBuilder fullQualifiedNameOfImport = new StringBuilder();
            importDeclaration.accept(new FullQualifiedNameExtractor(), fullQualifiedNameOfImport);
            if (fullQualifiedNameOfImport.toString().endsWith("." + typeName)) {
                return fullQualifiedNameOfImport.toString();
            }
        }
        throw new RuntimeException("no import found '"+typeName+"'");
    }

    private static class ClassNameExtractor extends VoidVisitorAdapter<StringBuilder> {

        @Override
        public void visit(ReferenceType n, StringBuilder arg) {
            Type theTypeDeclaration = n.getType();
            theTypeDeclaration.accept(this, arg);
        }

        @Override
        public void visit(ClassOrInterfaceType n, StringBuilder arg) {
            arg.append(n.getName());
        }
    }
}