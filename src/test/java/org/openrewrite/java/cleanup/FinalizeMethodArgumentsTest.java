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

    @Test
    void replaceWithFinalModifierNoArguments() {
        rewriteRun(
            java(
                """
                        package com.test;
                        
                        class TestClass {
                        
                            private void getAccaCouponData() {
                             
                            }
                        }
                    """
            )
        );
    }

    @Test
    void doNotReplaceWithFinalModifier() {
        rewriteRun(
            java(
                """
                        package com.oddschecker.responsive.utils.subevent;
                         
                         import com.oddschecker.responsive.enums.subevent.SubeventTypes;
                         import com.oddschecker.responsive.model.dto.card.SubEvent;
                         import java.util.List;
                         import org.springframework.beans.factory.annotation.Value;
                         import org.springframework.stereotype.Component;
                         
                         import static com.oddschecker.responsive.enums.matchdata.MatchDataTitleSeparator.AT;
                         import static com.oddschecker.responsive.enums.matchdata.MatchDataTitleSeparator.VS;
                         import static java.lang.String.format;
                         import static org.apache.commons.lang3.StringUtils.splitByWholeSeparator;
                         
                         /**
                          * Created by mza05 on 13/10/2017.
                          */
                         @Component
                         public class SubeventUtils {
                         
                             private static final String SUBEVENT_FORMAT = "%s%s%s";
                             private final List<Integer> categoryGroupIdForChangeSubeventName;
                         
                             public SubeventUtils(
                                     @Value("#{'${com.oddschecker.responsive.category.group.id.change.subevent.name}'.split(',')}")  final List<Integer> categoryGroupIdForChangeSubeventName) {
                                 this.categoryGroupIdForChangeSubeventName = categoryGroupIdForChangeSubeventName;
                             }
                         
                             public static boolean isSubeventOfSpecifiedType(final SubEvent subEvent, final List<SubeventTypes> requiredTypes) {
                         
                                 if (subEvent.getType() == null) {
                                     return false;
                                 }
                                 return requiredTypes.stream()
                                         .anyMatch(requiredType -> requiredType.getType().equalsIgnoreCase(subEvent.getType()));
                         
                             }
                         
                             /**
                              * Change SubeventName by CategoryGroupId and rebub
                              * @param subeventName
                              * @param categoryGroupId
                              * @return
                              */
                             public String getSubeventNameForCategoryGroupId(final String subeventName, final Integer categoryGroupId) {
                         
                                 if  (subeventName != null && categoryGroupId != null
                                         && subeventName.contains(AT.getSeparator())
                                         && categoryGroupIdForChangeSubeventName.contains(categoryGroupId)) {
                                     final var subeventTeamSplit = splitByWholeSeparator(subeventName, AT.getSeparator());
                                     if (subeventTeamSplit.length > 0) {
                                         return format(SUBEVENT_FORMAT, subeventTeamSplit[0], VS.getSeparator(), subeventTeamSplit[1]);
                                     }
                                 }
                         
                                 return subeventName;
                             }
                         }
                    """
            )
        );
    }
}