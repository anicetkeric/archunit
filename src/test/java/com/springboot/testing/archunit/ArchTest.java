package com.springboot.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

public class ArchTest {


    JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.springboot.testing.archunit");


    //    Layer Dependency Rules Test

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


    @Test
    void shouldFollowLayeredArchitecture() {

        layeredArchitecture()
                .layer("Controller").definedBy("..controller..")
                //.layer("Config").definedBy("..config..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                //.whereLayer("Service").mayOnlyBeAccessedByLayers("Config", "Controller")
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(importedClasses);
    }


    //    Naming Convention
    @Test
    void shouldFollowNamingConvention() {
        classes()
                .that().resideInAPackage("com.springboot.testing.archunit.repository")
                .should().haveSimpleNameEndingWith("Repository")
                .check(importedClasses);

        classes()
                .that().resideInAPackage("com.springboot.testing.archunit.service")
                .should().haveSimpleNameEndingWith("Service")
                .check(importedClasses);
    }


    // Package rules
    //    interface rules
    @Test
    public void interfacesShouldNotHaveNamesEndingWithTheWordInterface() {
        noClasses().that().areInterfaces().should().haveNameMatching(".*Interface").check(importedClasses);
    }

    @Test
    public void repositoriesMustResideInRepositoryPackage() {
        classes().that().haveNameMatching(".*Repository").should().resideInAPackage("..repository..")
                .as("Repositories should reside in a package '..repository..'")
                .check(importedClasses);
    }



    @Test
    public void domainClassesShouldBeSerializable() {
        classes()
                .that().resideInAPackage("..domain..")
                .should()
                .beAssignableTo(Serializable.class)
                .check(importedClasses);
    }

    @Test
    void shouldNotUseFieldInjection() {

        noFields()
                .should().beAnnotatedWith(Autowired.class)
                .check(importedClasses);
    }
    @Test
    public void repositoryClassesShouldHaveSpringRepositoryAnnotation() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(Repository.class)
                .check(importedClasses);
    }

}
