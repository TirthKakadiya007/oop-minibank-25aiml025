package minibank.util;

import minibank.model.annotation.MaxLength;
import minibank.model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection-based entity validator (Practical 7).
 * Reads custom annotations (@Positive, @MaxLength) at runtime and inspects field values.
 */
public final class AnnotationValidator {

    private AnnotationValidator() {
        // Prevent instantiation
    }

    /**
     * Inspects obj and its superclass fields by reflection and returns an array of error messages.
     * @param obj object to validate
     * @return String[] containing validation errors, or empty array if all valid.
     */
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Object to validate cannot be null"};
        }

        List<String> errors = new ArrayList<>();
        Class<?> currentClass = obj.getClass();

        // Inspect current class and superclasses (up to Object)
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    // Check @Positive annotation
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive posAnn = field.getAnnotation(Positive.class);
                        if (value == null) {
                            errors.add(field.getName() + ": value is null (" + posAnn.message() + ")");
                        } else if (value instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add(field.getName() + " [value=" + value + "]: " + posAnn.message());
                            }
                        }
                    }

                    // Check @MaxLength annotation
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength lenAnn = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str) {
                            if (str.length() > lenAnn.value()) {
                                errors.add(field.getName() + ": length " + str.length() + " exceeds maximum allowed " + lenAnn.value());
                            }
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Failed to access field " + field.getName() + " by reflection: " + e.getMessage());
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
