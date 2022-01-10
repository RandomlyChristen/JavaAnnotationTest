package com.random.processor;

import com.random.annotation.MyAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class MyAnnotationProcessor extends AbstractProcessor {
    private final ArrayList<MethodSpec> methodSpecList = new ArrayList<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(){
            {
                add(MyAnnotation.class.getCanonicalName());
            }
        };
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        System.out.println("init");
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("process");
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);

        for (Element element : elements) {
            methodSpecList.add(getStringGetter(element));
            System.out.println(element.getSimpleName().toString()
                    + " : " + element.getAnnotation(MyAnnotation.class).value());
        }

        if (roundEnv.processingOver()) {
            try {
                genJava(methodSpecList);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private MethodSpec getStringGetter(Element element) {
        return MethodSpec
                .methodBuilder(element.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(String.class))
                .addStatement("return new $T(\"$L\")", ClassName.get(String.class),
                        element.getAnnotation(MyAnnotation.class).value())
                .build();
    }

    private void genJava(List<MethodSpec> methodSpecList) throws IOException {
        final TypeSpec.Builder builder = TypeSpec.classBuilder("StringGetter");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        for (MethodSpec methodSpec : methodSpecList) {
            builder.addMethod(methodSpec);
        }

        final TypeSpec typeSpec = builder.build();

        JavaFile.builder("com.random", typeSpec)
                .build()
                .writeTo(processingEnv.getFiler());
    }
}
