package org.nrg.xnatx.dicomweb.entity.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityProperties
{
	public static Map<String,Object> newPropsMap(String[] keys, Object[] values)
	{
		Map<String,Object> props = new HashMap<>();
		for (int i = 0; i < keys.length; i++)
		{
			props.put(keys[i], values[i]);
		}

		return props;
	}

	public static <E> boolean setExampleProps(E example, Map<String,Object> props)
	{
		for (String key: props.keySet())
		{
			if (!setExampleProp(example, key, props.get(key)))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean setExampleProp(Object object, String fieldName,
		Object fieldValue)
	{
		// Set field with Reflection
		Class<?> clazz = object.getClass();
		while (clazz != null)
		{
			try
			{
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(object, fieldValue);
				return true;
			}
			catch (NoSuchFieldException e)
			{
				clazz = clazz.getSuperclass();
			}
			catch (Exception e)
			{
				return false;
				// throw new IllegalStateException(e);
			}
		}
		return false;
	}
}
