package com.infobip.typescript.showcase.custom.simple;

import com.infobip.typescript.CustomValidationSettings;
import com.infobip.typescript.TypeScriptFileGenerator;
import com.infobip.typescript.showcase.custom.complex.ComplexValidationTypeScriptFileGenerator;
import com.infobip.typescript.showcase.custom.simple.validation.Foo;
import com.infobip.typescript.showcase.hierarchy.exception.UncheckedURISyntaxException;
import cz.habarta.typescript.generator.Input;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class SimpleValidationTypeScriptFileGenerator extends TypeScriptFileGenerator {

    public SimpleValidationTypeScriptFileGenerator(Path basePath) {
        super(basePath);
    }

    @Override
    public Input getInput() {
        return Input.from(Foo.class);
    }

    @Override
    public Path outputFilePath(Path basePath) {
        Path lib = basePath.getParent().getParent().resolve("dist");

        try {
            Files.createDirectories(lib);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return lib.resolve("SimpleValidation.ts");
    }

    @Override
    protected CustomValidationSettings getCustomValidationSettings() {
        String rootPackage = "com.infobip.typescript";
        return new CustomValidationSettings(rootPackage);

    }

    @Override
    protected Path getDecoratorBasePath() {
        return getBasePath().getParent().getParent().resolve("src/main/typescript/decorators");
    }
}
