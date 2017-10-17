package org.myan.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public class Bootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }

}
