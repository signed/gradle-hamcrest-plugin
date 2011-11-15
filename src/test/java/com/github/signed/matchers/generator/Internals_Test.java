package com.github.signed.matchers.generator;

import com.github.signed.matchers.generator.samplematchers.IsADirectory;
import org.junit.Test;

import java.lang.reflect.Method;

public class Internals_Test {

    @Test
    public void testName() throws Exception {
        Method factory = IsADirectory.class.getMethod("aDirectory");
        System.out.println(factory.getDeclaringClass().getName());
    }

//    FactoryMethod result = new FactoryMethod(
//                javaMethod.getDeclaringClass().getName(),
//                javaMethod.getName(),
//                javaMethod.getReturnType().getName());
}
