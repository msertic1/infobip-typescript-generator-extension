package com.infobip.typescript;

import java.nio.file.Path;
import java.util.List;

public class CustomValidationSettings {

    private final String rootPackage;
    private final List<Path> customValidatorsPaths;

    public CustomValidationSettings(String rootPackage,
                                    List<Path> customValidatorsPaths) {
        if (rootPackage == null || rootPackage.isEmpty()) {
            throw new IllegalArgumentException("rootPackage parameter cannot be null");
        }

        this.rootPackage = rootPackage;
        this.customValidatorsPaths = customValidatorsPaths;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public List<Path> getCustomValidatorsPaths() {
        return customValidatorsPaths;
    }
}
