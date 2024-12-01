package com.springboot.testing.archunit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packagesOf = ArchunitApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchUnitApplicationTests {


    //region Package Dependency Checks
    @ArchTest
    static final ArchRule servicesAndRepositoriesShouldNotDependOnWebLayer = noClasses()
            .that().resideInAnyPackage("..service..")
            .or().resideInAnyPackage("..repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..controller..")
            .because("Services and repositories should not depend on web layer");

    @ArchTest
    static final ArchRule repositoriesMustResideInRepositoryPackage =
            classes().that().haveNameMatching(".*Repository").should().resideInAPackage("..repository..")
                    .as("Repositories should reside in a package '..repository..'");

    @ArchTest
    static final ArchRule domainClassesShouldBePublic =
            classes()
                    .that().resideInAPackage("..entity..")
                    .should()
                    .bePublic();

    @ArchTest
    static final ArchRule domainClassesShouldBeSerializable =
            classes()
                    .that().resideInAPackage("..entity..")
                    .should()
                    .beAssignableTo(Serializable.class);


    @ArchTest
    static final ArchRule interfacesShouldNotHaveNamesEndingWithTheWordInterface =
            noClasses().that().areInterfaces().should().haveNameMatching(".*Interface");
    //endregion


    //region Class Dependency Checks
    @ArchTest
    static final ArchRule serviceClassesShouldOnlyBeAccessedByController = classes()
            .that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");
    //endregion


    //region Naming convention
    @ArchTest
    static final ArchRule serviceClassesNaming = classes()
            .that().resideInAPackage("..service..")
            .should().haveSimpleNameEndingWith("Service")
            .orShould().haveSimpleNameEndingWith("ServiceImpl")
            .orShould().haveSimpleNameEndingWith("Component");

    @ArchTest
    static final ArchRule repositoryClassesNaming = classes()
            .that().resideInAPackage("..repository..")
            .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static final ArchRule controllerClassesNaming = classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller");
    //endregion


    //region Annotation check
    @ArchTest
    static final ArchRule fieldInjectionNotUseAutowiredAnnotation = noFields()
            .should().beAnnotatedWith(Autowired.class);


    @ArchTest
    static final ArchRule repositoryClassesShouldHaveSpringRepositoryAnnotation = classes()
            .that().resideInAPackage("..repository..")
            .should().beAnnotatedWith(Repository.class);


    @ArchTest
    static final ArchRule serviceClassesShouldHaveSpringServiceAnnotation = classes()
            .that().resideInAPackage("..service..")
            .and().haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule controllerClassesAnnotations = classes()
            .that().resideInAPackage("..controller..")
            .should().beAnnotatedWith(RestController.class).orShould().beAnnotatedWith(RequestMapping.class);
    //endregion


    //region Layer Dependency Rules Test
    @ArchTest
    static final ArchRule layeredArchitecture = Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");
    //endregion


}
