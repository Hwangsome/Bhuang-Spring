package com.bhuang.model;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;


public class TestBean {
    private String name;


    public void init() {
        System.out.println("TestBean init.. init");
    }

    public void destroy() {
        System.out.println("TestBean destroy.. destroy");
    }

    @PostConstruct
    public void init2() {
        System.out.println("TestBean init.. init2");
    }

    @PreDestroy
    public void destroy2() {
        System.out.println("TestBean destroy2.. destroy2");
    }

    public TestBean(String name) {
        System.out.println("TestBean construct.. 实例化");
        this.name = name;
    }
    public TestBean() {
        System.out.println("TestBean construct.. 实例化");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("TestBean set ... 初始化");
        this.name = name;
    }
}
