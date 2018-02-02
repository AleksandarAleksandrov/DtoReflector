package com.a.a.dtoreflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoIgnore;
import com.a.a.dtoreflector.annotation.DtoIgnoreIfHasValue;

public class DtoReflector {

	public static <S, D> D toDto(S source, D dtoDestination) {
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

				if (ClassUtils.isPrimitiveOrWrapper(sourceField.getType()) || isStringType(sourceField.getType())) {
					dtoField.set(dtoDestination, sourceField.get(source));
				} else if (isCollectionAssignableType(sourceField.getType())) {
					handleCollectionFieldValue(source, dtoDestination, dtoField, sourceField);
				} else {
					handleObjectFieldValue(source, dtoDestination, dtoField, sourceField);
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return dtoDestination;
	}

	private static <S, D> void handleCollectionFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Class<?> sourceCollectionType = getParametrizedCollectionType(sourceField);
		if (ClassUtils.isPrimitiveOrWrapper(sourceCollectionType) || isStringType(sourceCollectionType)) {
			dtoField.set(dtoDestination, sourceField.get(source));
		} else {
			Class<?> sourceCollectionClazz = sourceField.getType();
			Collection sourceCollection = (Collection) sourceField.get(source);
			if (sourceCollection == null) {
				// do nothing if the source is null
			} else {
				Class<?> dtoCollectionClazz = dtoField.getType();
				Collection dtoCollection = assignCollectionType(dtoCollectionClazz);
				sourceCollection.forEach(sourceElement -> {
					Class<?> pType = getParametrizedCollectionType(dtoField);
					Object dto = toDto(sourceElement, pType);
					dtoCollection.add(dto);
				});
				dtoField.set(dtoDestination, dtoCollection);
			}
		}
	}

	private static Collection assignCollectionType(Class<?> collectionClazz)
			throws InstantiationException, IllegalAccessException {
		if (isListType(collectionClazz)) {
			return new ArrayList<>();
		} else if (isSetType(collectionClazz)) {
			return new HashSet<>();
		} else if (isQueueType(collectionClazz)) {
			return new PriorityQueue<>();
		} else {
			return (Collection) collectionClazz.newInstance();
		}
	}

	private static boolean isStringType(Class<?> type) {
		return type.equals(String.class);
	}

	private static boolean isCollectionAssignableType(Class<?> type) {
		return Collection.class.isAssignableFrom(type);
	}

	private static boolean isListType(Class<?> type) {
		return type.equals(List.class);
	}

	private static boolean isSetType(Class<?> type) {
		return type.equals(Set.class);
	}

	private static boolean isQueueType(Class<?> type) {
		return type.equals(Queue.class);
	}

	private static Class<?> getParametrizedCollectionType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return collectionGenericType;
	}

	private static boolean isMapType(Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	private static Class<?> getParametrizedMapKeyType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return collectionGenericType;
	}

	private static Class<?> getParametrizedMapValueType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
		return collectionGenericType;
	}

	private static <S, D> void handleObjectFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalAccessException, InstantiationException {
		Object value = dtoField.get(dtoDestination);
		if (value == null) {
			value = dtoField.getType().newInstance();
		}
		Object sourceValue = sourceField.get(source);
		if (sourceValue == null) {
			dtoField.set(dtoDestination, null);
		} else {
			toDto(sourceValue, value);
			dtoField.set(dtoDestination, value);
		}
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

	public static <S, D> D toDto(S source, Class<D> dtoClazz) {
		try {
			D dtoDestination = dtoClazz.newInstance();
			return toDto(source, dtoDestination);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
