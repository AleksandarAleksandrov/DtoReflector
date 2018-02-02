package com.a.a.dtoreflector.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface DtoBigDecimalFromStr {
	String decimalSeparator() default ".";
	String groupingSeparator() default "";
}
