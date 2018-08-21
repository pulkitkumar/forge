package com.pulkit.android.forge.compiler;

import javax.lang.model.element.Element;

class FactoryInjectException extends Throwable {

  public final String message;
  public final Element element;

  public FactoryInjectException(String message, Element element) {
    this.message = message;
    this.element = element;
  }
}
