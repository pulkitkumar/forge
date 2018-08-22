package com.pulkit.android.forge.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     To be used on the constructor that needs a view model factory.
 * </p>
 */
@Target({ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Forge {
  Type type() default Type.DEFAULT;
}
