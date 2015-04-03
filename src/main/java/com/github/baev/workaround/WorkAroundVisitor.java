package com.github.baev.workaround;

import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.FieldRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 03.04.15
 */
public class WorkAroundVisitor extends EvaluatingVisitor {

    private String prefix;

    public WorkAroundVisitor(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Object visit(FieldRef fieldRef) {
        try {
            Method getClassLibrary = fieldRef.getClass().getDeclaredMethod("getClassLibrary");
            getClassLibrary.setAccessible(true);

            Field field = fieldRef.getClass().getDeclaredField("field");
            field.setAccessible(true);

            Field fieldIndex = FieldRef.class.getDeclaredField("fieldIndex");
            fieldIndex.setAccessible(true);

            ClassLibrary library = (ClassLibrary) getClassLibrary.invoke(fieldRef);
            if (fieldRef.getField() == null) {
                if (library != null) {
                    for (int i = 0; i < fieldRef.getPartCount(); ++i) {
                        String className = prefix + fieldRef.getNamePrefix(i);

                        if (library.hasClassReference(className)) {
                            JavaClass javaClass = library.getJavaClass(className);
                            fieldIndex.set(fieldRef, i + 1);
                            field.set(fieldRef, resolveField(fieldRef, javaClass, i + 1, fieldRef.getPartCount()));
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return super.visit(fieldRef);
    }

    protected JavaField resolveField(FieldRef ref, JavaClass javaClass, int start, int end) {
        JavaField field = null;

        for (int i = start; i < end; ++i) {
            field = javaClass.getFieldByName(ref.getNamePart(i));

            if (field == null) {
                break;
            }
        }

        return field;
    }

    @Override
    protected Object getFieldReferenceValue(JavaField javaField) {
        return javaField;
    }
}
