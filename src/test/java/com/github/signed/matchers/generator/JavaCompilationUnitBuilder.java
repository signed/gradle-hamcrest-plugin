package com.github.signed.matchers.generator;

public class JavaCompilationUnitBuilder {

    public String writeIt() {

        return "package com.github.signed.matchers.generator.samplematchers;\n" +
                "\n" +
                "import org.hamcrest.Description;\n" +
                "import org.hamcrest.Factory;\n" +
                "import org.hamcrest.Matcher;\n" +
                "import org.hamcrest.TypeSafeMatcher;\n" +
                "\n" +
                "import java.io.File;\n" +
                "\n" +
                "public class IsADirectory extends TypeSafeMatcher<File>{\n" +
                "\n" +
                "    /**\n" +
                "     * Some JavaDoc\n" +
                "     */\n" +
                "    @Factory\n" +
                "    public static <First> Matcher<First> aCreationMethod(ADependency dependency) throws IllegalStateException, NullPointerException {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected boolean matchesSafely(File item) {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void describeTo(Description description) {\n" +
                "        //nothing to do, just a matcher to parse\n" +
                "    }\n" +
                "}";

    }
}
