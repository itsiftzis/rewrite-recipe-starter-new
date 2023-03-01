/**
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 **/
package org.openrewrite.java.cleanup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.Empty;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.J.Modifier;
import org.openrewrite.java.tree.J.Modifier.Type;
import org.openrewrite.java.tree.J.VariableDeclarations;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.Space;
import org.openrewrite.java.tree.Statement;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;

import static java.util.Collections.emptyList;

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

                if (isEmpty(declarations.getParameters()) || hasFinalModifiers(declarations.getParameters())) {
                    return declarations;
                }
                List<Statement> list = new ArrayList<>();
                declarations.getParameters().forEach(p -> {
                    if (p instanceof VariableDeclarations) {
                        VariableDeclarations variableDeclarations = (VariableDeclarations) p;
                        if (variableDeclarations.getModifiers().isEmpty()) {
                            variableDeclarations = variableDeclarations.withModifiers(Collections.singletonList(new Modifier(Tree.randomId(),
                                Space.build("",
                                    emptyList()),
                                Markers.EMPTY,
                                Type.Final,
                                emptyList())));
                            variableDeclarations = variableDeclarations.withTypeExpression(variableDeclarations.getTypeExpression().withPrefix(Space.build(" ", emptyList())));
                            list.add(variableDeclarations);
                        }
                    }
                });
                declarations = declarations.withParameters(list);
                return declarations;
            }

            @Override
            public boolean isAcceptable(final SourceFile sourceFile, final ExecutionContext executionContext) {
                return sourceFile instanceof JavaSourceFile;
            }

        };
    }

    private boolean hasFinalModifiers(final List<Statement> parameters) {
        return parameters.stream().allMatch(p -> {
            if (p instanceof VariableDeclarations) {
                if (!((VariableDeclarations) p).getModifiers().isEmpty()
                    && ((VariableDeclarations) p).getModifiers().stream()
                    .allMatch(m -> m.getType().equals(Type.Final))) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean isEmpty(final List<Statement> parameters) {
        return parameters.size() == 1 && (parameters.get(0) instanceof Empty);
    }
}