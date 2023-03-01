/**
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
 **/
package org.openrewrite.java.cleanup;

import java.util.stream.Collectors;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.JavaSourceFile;

/**
 * Finalize method arguments v2
 */
public class FinalizeMethodArguments extends Recipe {

    @Override
    public String getDisplayName() {
        return "Finalize method arguments";
    }

    @Override
    public String getDescription() {
        return "Adds the `final` modifier keyword to method parameters.";
    }

    @Override
    public JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public MethodDeclaration visitMethodDeclaration(MethodDeclaration methodDeclaration, ExecutionContext executionContext) {
                MethodDeclaration declarations = super.visitMethodDeclaration(methodDeclaration, executionContext);

                if (declarations.getParameters().stream().map(Object::toString).allMatch(param -> param.contains("final"))) {
                    return declarations;
                }
                JavaTemplate addsFinalModifier = JavaTemplate.builder(this::getCursor, declarations.getParameters().stream().map(p -> "final " + p.toString()).collect(Collectors.joining(", ")))
                    .build();
                declarations = declarations.withTemplate(addsFinalModifier,
                    declarations.getCoordinates().replaceParameters());

                return declarations;
            }

            @Override
            public boolean isAcceptable(final SourceFile sourceFile, final ExecutionContext executionContext) {
                return sourceFile instanceof JavaSourceFile;
            }

        };
    }
}