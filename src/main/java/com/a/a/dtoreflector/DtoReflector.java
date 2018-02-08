package com.a.a.dtoreflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.a.a.dtoreflector.annotation.DtoBigDecimalFromStr;
import com.a.a.dtoreflector.annotation.DtoBigIntegerFromStr;
import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoIgnore;
import com.a.a.dtoreflector.annotation.DtoIgnoreIfHasValue;
import com.a.a.dtoreflector.annotation.DtoShallowCopy;

public class DtoReflector {

	/**
	 * Copies the data from the source parameter into the dtoDestination parameter.
	 * This method considers all the Dto annotations applied to the dto object.
	 * 
	 * @param source
	 * @param dtoDestination
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <S, D> D toDto(S source, D dtoDestination)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		for (Field dtoField : FieldUtils.getAllFields(dtoDestination.getClass())) {
			// make the field accessible
			dtoField.setAccessible(true);
			Annotation[] dtoFieldAnnotations = dtoField.getAnnotations();

			// if this field is marked as an ignore field, just continue
			if (isDtoIgnored(dtoFieldAnnotations)) {
				continue;
			}

			// if this annotation is present on the field
			// and it has a non null value, then the data for that field
			// from the source object is not copied to the dto
			if (isDtoIgnoreIfHasValue(dtoFieldAnnotations)) {
				Object value = dtoField.get(dtoDestination);
				if (value != null) {
					continue;
				}
			}

			// get the name of the field of the source this dto is targeted to
			// it can be different using the @DtoField(name="...")
			String dtoToSourceFieldName = getDtoToSourceFieldName(dtoField);

			// prepare the source field
			Field sourceField = FieldUtils.getField(source.getClass(), dtoToSourceFieldName, true);
			sourceField.setAccessible(true);

			Class<?> sourceFieldType = sourceField.getType();
			if (isDtoShallowCopy(dtoFieldAnnotations)) 
			{
				dtoField.set(dtoDestination, sourceField.get(source));
			} 
			else if (isStringType(sourceFieldType) && isDtoToBigInteger(dtoFieldAnnotations)) 
			{
				handleStrToBigIntegerFieldValue(source, dtoDestination, dtoField, sourceField);
			} 
			else if (isStringType(sourceFieldType) && isDtoToBigDecimal(dtoFieldAnnotations)) 
			{
				handleStrToBigDecimalFieldValue(source, dtoDestination, dtoField, sourceField);
			} 
			else if (isImmutableClass(sourceFieldType)) 
			{
				dtoField.set(dtoDestination, sourceField.get(source));
			} 
			else if (sourceFieldType.isArray()) {
				handleArrayFieldValue(source, dtoDestination, dtoField, sourceField);
			} 
			else if (isCollectionAssignableType(sourceFieldType)) 
			{
				handleCollectionFieldValue(source, dtoDestination, dtoField, sourceField);
			} 
			else if (isMapAssignableType(sourceFieldType)) 
			{
				handleMapFieldValue(source, dtoDestination, dtoField, sourceField);
			} 
			else 
			{
				handleObjectFieldValue(source, dtoDestination, dtoField, sourceField);
			}

		}
		return dtoDestination;
	}

	private static <S, D> void handleStrToBigIntegerFieldValue(S source, D dtoDestination, Field dtoField,
			Field sourceField) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String str = (String) sourceField.get(source);
		if (str == null)
			return;
		BigInteger bi = new BigInteger(str);
		dtoField.set(dtoDestination, bi);
	}

	private static <S, D> void handleStrToBigDecimalFieldValue(S source, D dtoDestination, Field dtoField,
			Field sourceField) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String str = (String) sourceField.get(source);
		if (str == null)
			return;

		DtoBigDecimalFromStr annotation = dtoField.getAnnotation(DtoBigDecimalFromStr.class);

		String groupingSeparator = annotation.groupingSeparator();
		if (groupingSeparator != null && !"".equals(groupingSeparator)) {
			str = str.replace(groupingSeparator, "");
		}

		String decimalSeparator = annotation.decimalSeparator();
		if (decimalSeparator != null && !"".equals(decimalSeparator)) {
			str = str.replace(decimalSeparator, ".");
		}

		// parse the string
		BigDecimal bigDecimal = new BigDecimal(str);
		dtoField.set(dtoDestination, bigDecimal);
	}

	private static <S, D> void handleArrayFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		Object sourceArray = sourceField.get(source);
		if (sourceArray == null) {
			return;
		}
		int sourceArrayLength = Array.getLength(sourceArray);

		Class<?> dtoArrayFieldClass = dtoField.getType();
		Object dtoArray = Array.newInstance(dtoArrayFieldClass.getComponentType(), sourceArrayLength);

		if (isImmutableClass(sourceField.getType())) {
			for (int i = 0; i < sourceArrayLength; i++) {
				Array.set(dtoArray, i, Array.get(sourceArray, i));
			}
		} else {
			for (int i = 0; i < sourceArrayLength; i++) {
				Array.set(dtoArray, i, toDto(Array.get(sourceArray, i), dtoArrayFieldClass.getComponentType()));
			}
		}
		dtoField.set(dtoDestination, dtoArray);
	}

	private static <S, D> void handleMapFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		Map sourceMap = (Map) sourceField.get(source);
		if (sourceMap == null) {
			// do not copy anything if the source map is null
			return;
		}

		Class<?> dtoMapClazz = dtoField.getType();
		Map dtoMap = null;
		if (dtoField.get(dtoDestination) != null) {
			// if the destination map is already initialized
			dtoMap = (Map) dtoField.get(dtoDestination);
		} else {
			// if the destination map is not initialized
			dtoMap = assignMapType(dtoMapClazz);
		}

		boolean isSourceMapKeyTypeImmutable = isImmutableClass(getParametrizedMapKeyType(sourceField));
		boolean isSourceMapValueTypeImmutable = isImmutableClass(getParametrizedMapValueType(sourceField));

		Class<?> dtoMapKeyType = getParametrizedMapKeyType(dtoField);
		Class<?> dtoMapValueType = getParametrizedMapValueType(dtoField);

		Set mapEntrySet = sourceMap.entrySet();
		for (Object setElem : mapEntrySet) {
			Map.Entry entry = (Map.Entry) setElem;
			Object dtoMapKey = getDtoMapKeyOrValue(isSourceMapKeyTypeImmutable, dtoMapKeyType, entry.getKey());
			Object dtoMapValue = getDtoMapKeyOrValue(isSourceMapValueTypeImmutable, dtoMapValueType, entry.getValue());
			dtoMap.put(dtoMapKey, dtoMapValue);
		}
		dtoField.set(dtoDestination, dtoMap);
	}

	private static Object getDtoMapKeyOrValue(boolean isSourceMapKeyTypeImmutable, Class<?> dtoMapKeyOrValueType,
			Object keyOrValue) throws IllegalAccessException, InstantiationException {
		Object dtoMapKeyOrValue = null;
		if (isSourceMapKeyTypeImmutable) {
			dtoMapKeyOrValue = keyOrValue;
		} else {
			dtoMapKeyOrValue = toDto(keyOrValue, dtoMapKeyOrValueType);
		}
		return dtoMapKeyOrValue;
	}

	private static boolean isImmutableClass(Class<?> clazz) {
		return ClassUtils.isPrimitiveOrWrapper(clazz) || isStringType(clazz);
	}

	private static Map assignMapType(Class<?> mapClazz) throws InstantiationException, IllegalAccessException {
		if (Map.class.equals(mapClazz)) {
			return new HashMap<>();
		} else {
			return (Map) mapClazz.newInstance();
		}
	}

	/**
	 * Handles the data transfer to a dto filed type that is of collection type.
	 * 
	 * @param source
	 * @param dtoDestination
	 * @param dtoField
	 * @param sourceField
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static <S, D> void handleCollectionFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		Collection sourceCollection = (Collection) sourceField.get(source);
		if (sourceCollection == null) {
			// do not copy anything if the source collection is null
			return;
		}

		Class<?> dtoCollectionClazz = dtoField.getType();
		Collection dtoCollection = null;

		if (dtoField.get(dtoDestination) != null) {
			// if the destination collection is already initialized
			dtoCollection = (Collection) dtoField.get(dtoDestination);
		} else {
			// if the destination collection is not initialized
			dtoCollection = assignCollectionType(dtoCollectionClazz);
		}

		// get the generic type of the source collection
		Class<?> sourceCollectionType = getParametrizedCollectionType(sourceField);

		// if the generic type is immutable just copy the collection data
		if (isImmutableClass(sourceCollectionType)) {
			dtoCollection.addAll(sourceCollection);
			dtoField.set(dtoDestination, dtoCollection);
		} else {
			// if it is mutable, recursively copy the data
			for (Object sourceElement : sourceCollection) {
				Class<?> pType = getParametrizedCollectionType(dtoField);
				Object dto = toDto(sourceElement, pType);
				dtoCollection.add(dto);
			}
			dtoField.set(dtoDestination, dtoCollection);

		}
	}

	/**
	 * Based on the class of the collection, returns a new instance. If the passed
	 * collection class is one of the main interfaces, the following creation
	 * mapping is made: <br />
	 * <b>List -> ArrayList </b><br />
	 * <b>Set -> HashSet</b><br />
	 * <b>Queue -> PriorityQueue</b><br />
	 * In all other cases a new instance is tried to be created using the passed
	 * class object.
	 * 
	 * @param collectionClazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
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

	/**
	 * Can this class be assigned to Collection ?
	 * 
	 * @param type
	 * @return
	 */
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

	/**
	 * Returns the type of the elements declared to be contained in the passed field
	 * object.
	 * 
	 * @param field
	 * @return
	 */
	private static Class<?> getParametrizedCollectionType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return collectionGenericType;
	}

	/**
	 * Can this class be assigned to Map ?
	 * 
	 * @param type
	 * @return
	 */
	private static boolean isMapAssignableType(Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}

	/**
	 * Returns the type of the key of the key-value pair of a field that is of type
	 * Map.
	 * 
	 * @param field
	 * @return
	 */
	private static Class<?> getParametrizedMapKeyType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return collectionGenericType;
	}

	/**
	 * Returns the type of the value of the key-value pair of a field that is of
	 * type Map.
	 * 
	 * @param field
	 * @return
	 */
	private static Class<?> getParametrizedMapValueType(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Class<?> collectionGenericType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
		return collectionGenericType;
	}

	/**
	 * Handles the case when a source to dto fields is a complex object. Meaning
	 * it's not a primitive, wrapper around primitive or string.
	 * 
	 * @param source
	 * @param dtoDestination
	 * @param dtoField
	 * @param sourceField
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static <S, D> void handleObjectFieldValue(S source, D dtoDestination, Field dtoField, Field sourceField)
			throws IllegalAccessException, InstantiationException {
		Object sourceValue = sourceField.get(source);
		if (sourceValue == null) {
			dtoField.set(dtoDestination, null);
		} else {
			Object dtoFieldValue = toDto(sourceValue, dtoField.getType());
			dtoField.set(dtoDestination, dtoFieldValue);
		}
	}

	/**
	 * Checks if the DtoIgnoreIfHasValue annotation is present on a field.
	 * 
	 * @param dtoFieldAnnotations
	 * @return
	 */
	private static boolean isDtoIgnoreIfHasValue(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoIgnoreIfHasValue.class);
	}

	/**
	 * Checks if the DtoIgnore annotation is present on a field.
	 * 
	 * @param dtoFieldAnnotations
	 * @return
	 */
	private static boolean isDtoIgnored(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoIgnore.class);
	}

	private static boolean isDtoToBigInteger(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoBigIntegerFromStr.class);
	}

	private static boolean isDtoToBigDecimal(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoBigDecimalFromStr.class);
	}

	private static boolean isDtoShallowCopy(Annotation[] dtoFieldAnnotations) {
		return isAnnotationPresent(dtoFieldAnnotations, DtoShallowCopy.class);
	}

	/**
	 * Checks if a certain annotation is present on a field.
	 * 
	 * @param dtoFieldAnnotations
	 * @param annotationClazz
	 * @return
	 */
	private static <T extends Annotation> boolean isAnnotationPresent(Annotation[] dtoFieldAnnotations,
			Class<T> annotationClazz) {
		for (Annotation annotation : dtoFieldAnnotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.equals(annotationClazz))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the dto field is annotated with DtoField and if has been given a
	 * name and if true uses that name.
	 * 
	 * @param dtoField
	 * @return
	 */
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

	/**
	 * Creates a new instance of the dto class and transfers the information from
	 * the source object to the new instance. his method considers all the Dto
	 * annotations applied to the dto object.
	 * 
	 * @param source
	 * @param dtoClazz
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <S, D> D toDto(S source, Class<D> dtoClazz)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		D dtoDestination = dtoClazz.newInstance();
		return toDto(source, dtoDestination);

	}

	/**
	 * Creates a list containing new instances of the dto classes
	 * passed to this method. All the data that is copied comes from
	 * the source object.
	 * @param source
	 * @param dtoClasses
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <S> List<?> toDtos(S source, List<Class<?>> dtoClasses)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		if (dtoClasses == null) {
			throw new IllegalStateException("Passed list of dto classes was null!");
		}
		List<Object> dtoInstances = new ArrayList<>();
		for (Class<?> dtoClazz : dtoClasses) {
			Object dto = toDto(source, dtoClazz);
			dtoInstances.add(dto);
		}
		return dtoInstances;
	}

}
