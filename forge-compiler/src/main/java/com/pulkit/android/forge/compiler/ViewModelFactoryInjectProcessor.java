package com.pulkit.android.forge.compiler;

import com.google.auto.service.AutoService;
import com.pulkit.android.forge.api.FactoryInject;
import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by user on 29/12/17.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.pulkit.android.forge.api.FactoryInject")
public class ViewModelFactoryInjectProcessor extends AbstractProcessor {

  private static final String SUFFIX = "_Factory";
  private Messager messager;
  private Filer filer;
  private Types typeUtils;
  private Elements elementUtils;

  public ViewModelFactoryInjectProcessor() {
    super();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(FactoryInject.class)) {
        if (annotatedElement.getKind() != ElementKind.CLASS) {
          throw new FactoryInjectException(String.format("Only classes can be annotated with @%s",
              FactoryInject.class.getSimpleName()), annotatedElement);
        }
        TypeElement typeElement = (TypeElement) annotatedElement;
        FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(typeElement, filer, elementUtils, messager);
        factoryAnnotatedClass.generateCode();
      }
    } catch (FactoryInjectException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element);
    } catch (IOException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), null);
    }
    return true;
  }

}
