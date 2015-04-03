package com.github.baev;

import com.github.baev.workaround.WorkAroundVisitor;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 03.04.15
 */
@RunWith(Parameterized.class)
public class QdoxWithWorkaroundTest {

    private String className;

    private JavaProjectBuilder builder;

    public QdoxWithWorkaroundTest(String className) {
        this.className = className;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{"com.github.baev.ConstantWithFullClassName"},
                new Object[]{"com.github.baev.ConstantWithoutPackage"},
                new Object[]{"com.github.baev.ConstantWithImport"},
                new Object[]{"com.github.baev.annotations.MyAnnotation"}
        );
    }

    @Before
    public void setUp() throws Exception {
        builder = new JavaProjectBuilder();
        builder.addClassLoader(getClass().getClassLoader());
        builder.addSourceTree(new File("src/main/java"));
    }

    @Test
    public void resolveConstantField() throws Exception {
        JavaClass javaClass = builder.getClassByName(className);

        List<JavaAnnotation> annotations = javaClass.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            AnnotationValue annotationValue = annotation.getProperty("value");
            annotationValue.accept(new WorkAroundVisitor(javaClass.getPackageName() + '.'));
        }
    }
}
