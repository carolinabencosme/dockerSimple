package org.wornux.urlshortener.core.routing.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GET {
  String path() default "";
}
