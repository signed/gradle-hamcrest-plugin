package com.github.signed.matchers.generator.samplematchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;

public class IsADirectory extends TypeSafeMatcher<File>{

    /**
     * Some JavaDoc
     */
    @Factory
    public static <First> Matcher<First> aCreationMethod(ADependency dependency) throws IllegalStateException, NullPointerException {
        return null;
    }

    @Override
    protected boolean matchesSafely(File item) {
        return false;
    }

    @Override
    public void describeTo(Description description) {
        //nothing to do, just a matcher to parse
    }
}