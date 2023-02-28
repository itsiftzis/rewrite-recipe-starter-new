/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oddschecker;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.J.Modifier;
import org.openrewrite.java.tree.J.Modifier.Type;

import static java.util.stream.Collectors.toList;

@Value
@EqualsAndHashCode(callSuper = true)
public class FinalizeMethodArguments extends Recipe {

    @Override
    public String getDisplayName() {
        return "Use `final` modifier in method arguments";
    }

    @Override
    public String getDescription() {
        return "Prefer to mark a method argument as final to prevent modifying the original parameter within the method.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        // Any change to the AST made by the applicability test will lead to the visitor returned by Recipe.getVisitor() being applied
        // No changes made by the applicability test will be kept
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public MethodDeclaration visitMethodDeclaration(final MethodDeclaration method, final ExecutionContext executionContext) {
                return super.visitMethodDeclaration(method, executionContext);
            }
        };
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        // To avoid stale state persisting between cycles, getVisitor() should always return a new instance of its visitor
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public MethodDeclaration visitMethodDeclaration(final MethodDeclaration method, final ExecutionContext executionContext) {
                return method.withModifiers(method.getModifiers().stream()
                    .map(this::addFinalModifier)
                    .collect(toList()));
            }

            private Modifier addFinalModifier(final Modifier modifier) {
                if (!modifier.getType().equals(Type.Final)) {
                    return new Modifier(modifier.getId(), modifier.getPrefix(), modifier.getMarkers(), Type.Final, modifier.getAnnotations());
                }
                return modifier;
            }
        };
    }
}