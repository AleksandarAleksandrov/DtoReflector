package com.a.a.dtoreflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoIgnore;
import com.a.a.dtoreflector.annotation.DtoIgnoreIfHasValue;

public class DtoReflector {

	public static <S, D> D transferToDto(S source, D dtoDestination) {
		for (Field dtoField : FieldUtils.getAllFields(dtoDestination.getClass())) {
			// make the field accessible
			dtoField.setAccessible(true);
			Annotation[] dtoFieldAnnotations = dtoField.getAnnotations();

			// if this field is marked as an ignore field, just continue
			if (isDtoIgnored(dtoFieldAnnotations)) {
				continue;
			}

			if (isDtoIgnoreIfHasValue(dtoFieldAnnotations)) {
				try {
					Object value = dtoField.get(dtoDestination);
					if (value != null) {
						continue;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// this means the value of the field was null
					// and we should not ignore it!
				}
			}

			String dtoToSourceFieldName = getDtoToSourceFieldName(dtoField);

			try {
				Field sourceField = FieldUtils.getField(source.getClass(), dtoToSourceFieldName, true);
				sourceField.setAccessible(true);

				if (ClassUtils.isPrimitiveOrWrapper(sourceField.getType()) || sourceField.getType().equals(String.class)) {
					dtoField.set(dtoDestination, sourceField.get(source));
				} else {
					// TODO: handle collections
					Object value = dtoField.get(dtoDestination);
					if(value == null) {
						value = dtoField.getType().newInstance();
					}
					transferToDto(sourceField.get(source), value);
					dtoField.set(dtoDestination, value);
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return dtoDestination;
	}

	private static boolean isDtoIgnoreIfHasValue(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoIgnoreIfHasValue.class);
	}

	private static boolean isDtoIgnored(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoIgnore.class);
	}

	private static <T extends Annotation> boolean isAnnotationPresent(Annotation[] dtoFieldAnnotations,
			Class<T> annotationClazz) {
		for (Annotation annotation : dtoFieldAnnotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.equals(annotationClazz))
				return true;
		}
		return false;
	}

	private static String getDtoToSourceFieldName(Field dtoField) {
		String dtoToSourceFieldName = null;
		DtoField annotation = dtoField.getAnnotation(DtoField.class);

		if (annotation != null && annotation.name() != null && !"".equals(annotation.name())) {
			dtoToSourceFieldName = annotation.name();
		} else {
			dtoToSourceFieldName = dtoField.getName();
		}
		return dtoToSourceFieldName;
	}

	public static <S, D> D transferToDto(S source, Class<D> dtoClazz) {
		try {
			D dtoDestination = dtoClazz.newInstance();
			return transferToDto(source, dtoDestination);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
