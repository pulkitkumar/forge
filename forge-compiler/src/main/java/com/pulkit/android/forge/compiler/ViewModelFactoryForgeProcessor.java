package com.pulkit.android.forge.compiler;

import com.google.auto.service.AutoService;
import com.pulkit.android.forge.api.Forge;
import com.pulkit.android.forge.api.Type;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by user on 29/12/17.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.pulkit.android.forge.api.Forge")
public class ViewModelFactoryForgeProcessor extends AbstractProcessor {

  private Messager messager;
  private Filer filer;
  private Elements elementUtils;

  public ViewModelFactoryForgeProcessor() {
    super();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Forge.class)) {
        checkForValidAnnotationUse(annotatedElement);
        ExecutableElement typeElement = (ExecutableElement) annotatedElement;
        Type type = annotatedElement.getAnnotation(Forge.class).type();
        FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(typeElement,
            (TypeElement) annotatedElement.getEnclosingElement(), filer, elementUtils, type);
        factoryAnnotatedClass.generateCode();
      }
    } catch (ForgeException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element);
    } catch (IOException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), null);
    }
    return true;
  }

  private void checkForValidAnnotationUse(Element annotatedElement) throws ForgeException {
    if (annotatedElement.getKind() != ElementKind.CONSTRUCTOR) {
      throw new ForgeException(String.format("Only constructors can be annotated with @%s",
          Forge.class.getSimpleName()), annotatedElement);
    }
    TypeElement e = (TypeElement) annotatedElement.getEnclosingElement();
    if (!e.getSuperclass().toString().contains("ViewModel")) {
      throw new ForgeException(
          String.format("Only subclasses of ViewModel should have constructors annotated with @%s",
              Forge.class.getSimpleName()), annotatedElement);
    }
  }
}
