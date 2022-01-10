package com.random;

import com.random.annotation.MyAnnotation;

public class Main {
    public static void main(String[] args) {
        System.out.println(StringGetter.testStr1());
        System.out.println(StringGetter.testStr2());
    }
}

class TestType {
    @MyAnnotation("Hello") String testStr1;
    @MyAnnotation("World") String testStr2;
}
