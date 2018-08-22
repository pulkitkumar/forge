package com.pulkit.android.forge.compiler;

import javax.lang.model.element.Element;

class ForgeException extends Throwable {

  public final String message;
  public final Element element;

  public ForgeException(String message, Element element) {
    this.message = message;
    this.element = element;
  }
}
