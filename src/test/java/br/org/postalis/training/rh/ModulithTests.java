package br.org.postalis.training.rh;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;


public class ModulithTests {


    @Test
    void generateDocumentation() {
        ApplicationModules modules = ApplicationModules.of(RhSystemApplication.class);
        new Documenter(modules).writeDocumentation();
    }
}
