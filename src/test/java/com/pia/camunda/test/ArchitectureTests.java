package com.pia.camunda.test;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import com.tngtech.archunit.library.ProxyRules;
import org.springframework.cache.annotation.Cacheable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * @author Mustafa Ulu
 */
@AnalyzeClasses(
    packages = "com.pia.camunda.test",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTests {

  @ArchTest
  static final ArchRule noClasses_shouldAccessStandardStreams =
      GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

  @ArchTest
  static final ArchRule noClasses_shouldThrowGenericExceptions =
      GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

  @ArchTest
  static final ArchRule noClasses_shouldUseFieldInjection =
      GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

  @ArchTest
  static final ArchRule fields_shouldNotHaveNameLogger =
      fields().should().notHaveName("logger");

  @ArchTest
  static final ArchRule noClasses_shouldDirectlyCallOtherMethodsDeclaredInTheSameClassThatAreAnnotatedWithCacheable =
      ProxyRules.no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with(
          Cacheable.class);

}
