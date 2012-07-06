package com.github.signed.matchers.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class FactoryMethodReturnTypesGenericType implements FactoryMethodPart {

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
                typeArg = context.getFullQualifiedTypeFromImports(typeArg);
            }
            builder.withGenericReturnType(typeArg);
        }
    }
}
