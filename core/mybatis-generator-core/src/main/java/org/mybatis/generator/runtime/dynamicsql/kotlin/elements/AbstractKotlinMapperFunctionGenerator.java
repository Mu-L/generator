/*
 *    Copyright 2006-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.runtime.dynamicsql.kotlin.elements;

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.kotlin.KotlinArg;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.runtime.AbstractKotlinFunctionGenerator;
import org.mybatis.generator.runtime.CodeGenUtils;
import org.mybatis.generator.runtime.KotlinFunctionAndImports;

public abstract class AbstractKotlinMapperFunctionGenerator extends AbstractKotlinFunctionGenerator {
    protected final String tableFieldName;
    protected final boolean useSnakeCase;

    protected AbstractKotlinMapperFunctionGenerator(BaseBuilder<?> builder) {
        super(builder);
        tableFieldName = Objects.requireNonNull(builder.tableFieldName);
        useSnakeCase = CodeGenUtils.findTableOrClientPropertyAsBoolean(PropertyRegistry.ANY_USE_SNAKE_CASE_IDENTIFIERS,
                introspectedTable);
    }

    protected void acceptParts(KotlinFunctionAndImports functionAndImports, KotlinFunctionParts functionParts) {
        for (KotlinArg argument : functionParts.getArguments()) {
            functionAndImports.getFunction().addArgument(argument);
        }

        for (String annotation : functionParts.getAnnotations()) {
            functionAndImports.getFunction().addAnnotation(annotation);
        }

        functionAndImports.getFunction().addCodeLines(functionParts.getCodeLines());
        functionAndImports.getImports().addAll(functionParts.getImports());
    }

    protected void addFunctionComment(KotlinFunctionAndImports functionAndImports) {
        commentGenerator.addGeneralFunctionComment(functionAndImports.getFunction(), introspectedTable,
                functionAndImports.getImports());
    }

    public static FieldNameAndImport calculateFieldNameAndImport(String tableFieldName, String supportObjectImport,
                                                                 IntrospectedColumn column, boolean useSnakeCase) {
        String fieldName = column.getJavaProperty();
        if (useSnakeCase) {
            fieldName = StringUtility.convertCamelCaseToSnakeCase(fieldName);
        }
        String importString;
        if (fieldName.equals(tableFieldName)) {
            // name collision, no shortcut generated
            fieldName = tableFieldName + "." + fieldName; //$NON-NLS-1$
            importString = supportObjectImport + "." + tableFieldName; //$NON-NLS-1$
        } else {
            importString = supportObjectImport + "." + fieldName; //$NON-NLS-1$
        }
        return new FieldNameAndImport(fieldName, importString);
    }

    public record FieldNameAndImport(String fieldName, String importString) { }

    public abstract static class BaseBuilder<T extends BaseBuilder<T>> extends AbstractGeneratorBuilder<T> {
        private @Nullable String tableFieldName;

        public T withTableFieldName(String tableFieldName) {
            this.tableFieldName = tableFieldName;
            return getThis();
        }
    }
}
