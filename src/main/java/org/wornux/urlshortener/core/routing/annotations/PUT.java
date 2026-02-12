package org.wornux.urlshortener.core.routing.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PUT {
  String path() default "";
}
