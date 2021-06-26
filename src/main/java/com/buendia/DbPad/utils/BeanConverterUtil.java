package com.buendia.DbPad.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class BeanConverterUtil<T> {

    public T convertBean(Object source, Class<T> target) {
        try {
            T targetInstance = target.newInstance();
            if (source == null) {
                return targetInstance;
            }
            copyProperties(source, targetInstance, null);
            return targetInstance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T convertBeanWithIgnore(Object source, Class<T> target, String[] ignore) {
        try {
            T targetInstance = target.newInstance();
            if (source == null) {
                return targetInstance;
            }
            copyProperties(source, targetInstance, ignore);
            return targetInstance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void copyProperties(Object source, Object target, String[] ignoreProperties) {
        Assert.notNull(source, "source must not be null");
        Assert.notNull(target, "target must not be null");
        Class<?> targetClass = target.getClass();
        List<String> ignoreList = ignoreProperties == null ? null : Arrays.asList(ignoreProperties);
        PropertyDescriptor[] targetProperties = BeanUtils.getPropertyDescriptors(targetClass);
        for (PropertyDescriptor targetProperty : targetProperties) {
            Method writeMethod = targetProperty.getWriteMethod();
            if (writeMethod != null && (ignoreProperties != null && !ignoreList.contains(targetProperty.getName()))) {

                PropertyDescriptor sourceProperty = BeanUtils.getPropertyDescriptor(source.getClass(), targetProperty.getName());
                if (sourceProperty != null) {
                    Method readMethod = sourceProperty.getReadMethod();
                    if (readMethod != null) {
                        try {
                            Object value = readMethod.invoke(source);
                            if (value instanceof String) {
                                writeMethod.invoke(target, value);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
