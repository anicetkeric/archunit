package com.springboot.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchunitApplicationTests {

    private JavaClasses importedClasses;

    @BeforeEach
    public void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.springboot.testing.archunit");
    }

    /*    Package Dependency Checks*/

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        noClasses()
                .that().resideInAnyPackage("com.springboot.testing.archunit.service..")
                .or().resideInAnyPackage("com.springboot.testing.archunit.repository..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.springboot.testing.archunit.controller..")
                .because("Services and repositories should not depend on web layer")
                .check(importedClasses);
    }

    /* Class Dependency Checks*/

    @Test
    void serviceClassesShouldOnlyBeAccessedByController() {
        classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage("..service..", "..controller..")
                .check(importedClasses);
    }


    /* naming convention */

    @Test
    void serviceClassesShouldBeNamedXServiceOrXComponentOrXServiceImpl() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("ServiceImpl")
                .orShould().haveSimpleNameEndingWith("Component")
                .check(importedClasses);
    }

    @Test
    void repositoryClassesShouldBeNamedXRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(importedClasses);
    }

    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(importedClasses);
    }


    /* Annotation check*/

    @Test
    void fieldInjectionNotUseAutowiredAnnotation() {

        noFields()
                .should().beAnnotatedWith(Autowired.class)
                .check(importedClasses);
    }

    @Test
    void repositoryClassesShouldHaveSpringRepositoryAnnotation() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(Repository.class)
                .check(importedClasses);
    }

    @Test
    void serviceClassesShouldHaveSpringServiceAnnotation() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(Service.class)
                .check(importedClasses);
    }

    /*    Layer Dependency Rules Test*/
    @Test
    void layeredArchitectureShouldBeRespected() {

        layeredArchitecture()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(importedClasses);
    }

    @Test
    void repositoriesMustResideInRepositoryPackage() {
        classes().that().haveNameMatching(".*Repository").should().resideInAPackage("..repository..")
                .as("Repositories should reside in a package '..repository..'")
                .check(importedClasses);
    }

    @Test
    void domainClassesShouldBeSerializable() {
        classes()
                .that().resideInAPackage("..domain..")
                .should()
                .beAssignableTo(Serializable.class)
                .check(importedClasses);
    }

    @Test
    void interfacesShouldNotHaveNamesEndingWithTheWordInterface() {
        noClasses().that().areInterfaces().should().haveNameMatching(".*Interface").check(importedClasses);
    }

    @Test
    void domainClassesShouldBePublic() {
        classes()
                .that().resideInAPackage("..domain..")
                .should()
                .bePublic()
                .check(importedClasses);
    }

}
