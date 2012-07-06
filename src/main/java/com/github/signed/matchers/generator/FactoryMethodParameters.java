package com.github.signed.matchers.generator;

import com.google.common.collect.Iterables;
import japa.parser.ast.body.Parameter;

import java.util.List;

public class FactoryMethodParameters implements  FactoryMethodPart{
    @Override
    public void performStep(FactoryMethodBuilder builder, FactoryMethodContext context) {
        List<Parameter> parameters = context.methodDeclaration.getParameters();
        Parameter parameter = Iterables.getFirst(parameters, null);

        builder.withParameter("com.github.signed.matchers.generator.samplematchers.ADependency", parameter.getId().getName());
    }
}
