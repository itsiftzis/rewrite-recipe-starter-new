package com.oddschecker;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class FinalizeMethodArgumentsTest implements RewriteTest {

    //Note, you can define defaults for the RecipeSpec and these defaults will be used for all tests.
    //In this case, the recipe and the parser are common. See below, on how the defaults can be overridden
    //per test.
//    @Override
//    public void defaults(RecipeSpec spec) {
//        spec.recipe(new FinalizeMethodArguments())
//            .parser(JavaParser.fromJavaVersion()
//                .logCompilationWarningsAndErrors(true));
//    }

    @Test
    void replaceWithFinalModifier() {
        rewriteRun(
            //There is an overloaded version or rewriteRun that allows the RecipeSpec to be customized specifically
            //for a given test. In this case, the parser for this test is configured to not log compilation warnings.
//            spec -> spec
//                .parser(JavaParser.fromJavaVersion()
//                    .logCompilationWarningsAndErrors(false)),
            java("""
                        package com.test;
                        
                        class TestClass {
                        
                            private void getAccaCouponData(Object responsiveRequestConfig, Object card) {
                             
                            }
                        }
                    """,
                """
                        package com.test;
                        
                        class TestClass {
                        
                            private void getAccaCouponData(final Object responsiveRequestConfig, final Object card) {
                             
                            }
                        }
                    """
            )
        );
    }
}