package com.github.signed.matchers.generator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.io.IOUtils;
import org.hamcrest.generator.FactoryMethod;

import java.io.FileInputStream;
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
                new FactoryMethodParameters().performStep(builder, context);
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

}