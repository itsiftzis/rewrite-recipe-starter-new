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

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.search.FindImports;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.JavaSourceFile;

import static java.util.stream.Collectors.toList;

@Value
@EqualsAndHashCode(callSuper = true)
public class FinalizeMethodArguments extends Recipe {

    private static final String PACKAGE_PREFIX_NAME = "com.oddschecker.*";

    @Override
    public String getDisplayName() {
        return "Use `final` modifier in method arguments";
    }

    @Override
    public String getDescription() {
        return "Prefer to mark a method argument as final to prevent modifying the original parameter within the method.";
    }

    @Override
    protected List<SourceFile> visit(List<SourceFile> before, ExecutionContext ctx) {
        return before.stream().filter(sourceFile -> sourceFile instanceof JavaSourceFile).collect(toList());
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        // This optimization means that your visitor will only run if the source file
        // has an import of a specific package
        return new FindImports(PACKAGE_PREFIX_NAME).getVisitor();
    }

    // OpenRewrite provides a managed environment in which it discovers, instantiates, and wires configuration into Recipes.
    // This recipe has no configuration and delegates to its visitor when it is run.
    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public MethodDeclaration visitMethodDeclaration(MethodDeclaration methodDeclaration, ExecutionContext executionContext) {
                if (methodDeclaration.getParameters().stream().map(Object::toString).allMatch(param -> param.startsWith("final"))) {
                    return methodDeclaration;
                }
                JavaTemplate addsFinalModifier = JavaTemplate.builder(this::getCursor, methodDeclaration.getParameters().stream().map(p -> "final " + p.toString()).collect(Collectors.joining(", ")))
                    .build();
                methodDeclaration = methodDeclaration.withTemplate(addsFinalModifier,
                    methodDeclaration.getCoordinates().replaceParameters());

                // Safe to assert since we just added a body to the method
                assert methodDeclaration.getBody() != null;

                return methodDeclaration;
            }
        };
    }
}