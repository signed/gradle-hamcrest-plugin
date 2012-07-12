package com.github.signed.matchers.generator;

import com.google.common.collect.Lists;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.expr.NameExpr;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FactoryMethodContext_Test {
    private final List<ImportDeclaration> imports = Lists.newArrayList();
    private final CompilationUnit compilationUnit = new CompilationUnit();

    @Test
    public void defaultToJavaLangIfNoImportMatches() throws Exception {
        assertThat(theReturnedFullQualifiedTypeNameFor("String"), is("java.lang.String"));
    }

    @Test
    public void returnToTheImportIfOneIsAvailable() throws Exception {
        addImportFor("own.String");

        assertThat(theReturnedFullQualifiedTypeNameFor("String"), is("own.String"));
    }

    @Test
    public void detectClassesThatAreInTheSamePackage() throws Exception {
        compilationUnit.setPackage(new PackageDeclaration(new NameExpr("same")));
        assertThat(theReturnedFullQualifiedTypeNameFor("ATypeInTheSamePackageAsTheClassContainingTheFactoryMethod"), is("same.ATypeInTheSamePackageAsTheClassContainingTheFactoryMethod"));
    }

    private void addImportFor(String fullQualifiedClassName) {
        ImportDeclaration e = new ImportDeclaration();
        e.setName(new NameExpr(fullQualifiedClassName));
        imports.add(e);
    }

    private String theReturnedFullQualifiedTypeNameFor(String type) {

        compilationUnit.setImports(imports);
        return new FactoryMethodContext(compilationUnit, null, null).getFullQualifiedTypeFromImports(type);
    }
}
