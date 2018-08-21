package com.pulkit.android.forge.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

class FactoryAnnotatedClass {

  private static final String SUFFIX = "_Factory";
  public static final String CLASS_PARAM_NAME = "c";
  private static final String COMMA = ", ";
  private final TypeElement classElement;
  private final Filer filer;
  private final Elements elementUtils;
  private final Messager messager;
  private ExecutableElement constructorElement;

  public FactoryAnnotatedClass(TypeElement typeElement, Filer filer, Elements elementUtils,
      Messager messager) {
    this.classElement = typeElement;
    this.filer = filer;
    this.elementUtils = elementUtils;
    this.messager = messager;
  }

  public void generateCode() throws IOException {
    String factoryClassName = classElement.getSimpleName() + SUFFIX;
    String qualifiedClassName = classElement.getQualifiedName() + SUFFIX;
    PackageElement pkg = elementUtils.getPackageOf(classElement);
    String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

    classElement.getEnclosedElements().forEach(it -> {
      if (it.getKind() == ElementKind.CONSTRUCTOR) {
        constructorElement = (ExecutableElement) it;

      }
    });

    // check for constructor element null.

    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC);

    constructorElement.getParameters().forEach(it -> {
      constructorBuilder.addParameter(getParamSpec(it));
    });

    constructorElement.getParameters().forEach(it -> {
      constructorBuilder.addCode(CodeBlock.builder()
          .addStatement(String.format("this.%s = %s", it.getSimpleName(), it.getSimpleName()))
          .build());
    });

    List<FieldSpec> fields = new ArrayList<>();
    constructorElement.getParameters().forEach(it -> {
      fields.add(getFieldSpec(it));
    });

    TypeVariableName t = TypeVariableName.get("T").withBounds(ClassName.get("android.arch.lifecycle", "ViewModel"));

    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create")
        .addAnnotation(Override.class)
        .addTypeVariable(t)
        .returns(t)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(getClassParameter(t));

    StringBuilder params = new StringBuilder();
    fields.forEach(it -> {
      params.append(it.name).append(COMMA);
    });
    String paramString = params.toString().substring(0, params.lastIndexOf(COMMA));

    methodBuilder.beginControlFlow(String.format(String.format("if (%s.isAssignableFrom(%s.class))", CLASS_PARAM_NAME, classElement.getSimpleName())))
        .addStatement(String.format("return (T) new %s (%s)", classElement.getSimpleName(), paramString))
        .nextControlFlow("else")
        .addStatement("throw new RuntimeException(\"Unknown class name\")")
        .endControlFlow();


    TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ClassName.get("android.arch.lifecycle.ViewModelProvider", "Factory"))
        .addFields(fields)
        .addMethod(constructorBuilder.build())
        .addMethod(methodBuilder.build())
        .build();

    JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
  }

  private FieldSpec getFieldSpec(VariableElement param) {
    Set<Modifier> modifiers = new HashSet<>();
    modifiers.add(Modifier.PRIVATE);
    modifiers.add(Modifier.FINAL);
    return FieldSpec.builder(TypeName.get(param.asType()),
        param.getSimpleName().toString(),
        modifiers.toArray(new Modifier[modifiers.size()])).build();
  }

  private ParameterSpec getParamSpec(VariableElement parameterElement) {
    return ParameterSpec.builder(TypeName.get(parameterElement.asType()),
        parameterElement.getSimpleName().toString()).build();
  }

  private ParameterSpec getClassParameter(TypeVariableName t) {
    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Class.class), t);
    return ParameterSpec.builder(parameterizedTypeName, CLASS_PARAM_NAME).build();
  }

}
