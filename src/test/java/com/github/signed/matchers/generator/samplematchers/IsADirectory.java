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
    public static Matcher<File> aDirectory() throws RuntimeException{
        return new IsADirectory();
    }

    @Override
    protected boolean matchesSafely(File file) {
        return file.isDirectory();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a directory");
    }

    @Override
    protected void describeMismatchSafely(File item, Description mismatchDescription) {
        mismatchDescription.appendValue(item);
        if(item.exists()){
            mismatchDescription.appendText(" is a file");
        }else {
            mismatchDescription.appendText(" does not exist");
        }
    }
}