package com.infobip.typescript.validation;

import com.infobip.typescript.TypeScriptImportResolver;
import cz.habarta.typescript.generator.Extension;
import cz.habarta.typescript.generator.compiler.ModelCompiler;
import cz.habarta.typescript.generator.compiler.ModelTransformer;
import cz.habarta.typescript.generator.emitter.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.infobip.typescript.validation.CommonValidationMessages.COMMON_VALIDATION_MESSAGES_CLASS_NAME;

public class ClassValidatorDecoratorExtension extends Extension implements TypeScriptImportResolver {

    private static final Set<String> DEFAULT_VALIDATIONS;

    static {
        DEFAULT_VALIDATIONS = Stream.of("@ValidateNested(",
                                        "@IsDefined(",
                                        "@IsNotEmpty(",
                                        "@MaxLength(",
                                        "@MinLength(",
                                        "@Max(",
                                        "@Min(",
                                        "@ArrayMaxSize(",
                                        "@ArrayMinSize(")
                                    .collect(Collectors.toSet());
    }

    private final CompositeBeanValidationToTsDecoratorConverter converter;

    public ClassValidatorDecoratorExtension() {
        this(null);
    }

    public ClassValidatorDecoratorExtension(String customMessageSource) {
        this.converter = new CompositeBeanValidationToTsDecoratorConverter(
                new ValidationMessageReferenceResolver(customMessageSource));
    }

    @Override
    public EmitterExtensionFeatures getFeatures() {
        final EmitterExtensionFeatures features = new EmitterExtensionFeatures();
        features.generatesRuntimeCode = true;
        return features;
    }

    @Override
    public List<TransformerDefinition> getTransformers() {
        return Collections.singletonList(
                new TransformerDefinition(ModelCompiler.TransformationPhase.BeforeEnums,
                                          (ModelTransformer) (symbolTable, model) ->
                                                  model.withBeans(model.getBeans().stream()
                                                                       .map(this::decorateClass)
                                                                       .collect(Collectors.toList())
                                                  ))
        );
    }

    @Override
    public List<String> resolve(String typeScript) {
        String usedValidations = DEFAULT_VALIDATIONS.stream()
                                                    .filter(typeScript::contains)
                                                    .map(validation -> validation.substring(1, validation.length() - 1))
                                                    .collect(Collectors.joining(", "));
        if (!usedValidations.isEmpty()) {
            return resolve(typeScript, usedValidations);
        }

        return Collections.emptyList();
    }

    @NotNull
    private List<String> resolve(String typeScript, String usedValidations) {
        String validationImport = "import { " + usedValidations + " } from 'class-validator';";

        if (typeScript.contains(COMMON_VALIDATION_MESSAGES_CLASS_NAME)) {
            String commonValidationMessagesImport = "import { CommonValidationMessages } from './CommonValidationMessages';";
            return Arrays.asList(validationImport, commonValidationMessagesImport);
        }

        return Collections.singletonList(validationImport);
    }

    private TsBeanModel decorateClass(TsBeanModel bean) {
        return bean.withProperties(bean.getProperties()
                                       .stream()
                                       .map(model -> getDecorators(bean, model))
                                       .collect(Collectors.toList())
        );
    }

    private TsPropertyModel getDecorators(TsBeanModel bean, TsPropertyModel model) {
        return getField(bean, model).map(field -> model.withDecorators(getDecorators(model, field)))
                                    .orElse(model);
    }

    private List<TsDecorator> getDecorators(TsPropertyModel model, Field field) {
        Stream<TsDecorator> newDecorators = Arrays.stream(field.getAnnotations())
                                                  .flatMap(annotation -> converter.convert(field, annotation));
        return Stream.concat(model.getDecorators().stream(), newDecorators).collect(Collectors.toList());
    }

    private Optional<Field> getField(TsBeanModel bean, TsPropertyModel model) {
        try {
            return Optional.of(bean.getOrigin().getDeclaredField(model.getName()));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }
}
