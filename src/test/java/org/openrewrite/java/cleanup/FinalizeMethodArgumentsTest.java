package org.openrewrite.java.cleanup;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class FinalizeMethodArgumentsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new FinalizeMethodArguments()).cycles(2);
    }

    //    @Disabled
    @Test
    void replaceWithFinalModifier() {
        rewriteRun(
            java(
                """
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