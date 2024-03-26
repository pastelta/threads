package ru.task.three;

import java.lang.reflect.Proxy;

public class Utils {
    public static <T> T cache(T obj) {

        Class cl = obj.getClass();
        Class[] interfaces = obj.getClass().getInterfaces();

        return (T) Proxy.newProxyInstance(cl.getClassLoader()
                , interfaces
                , new FractionableInvocationHandler(obj));
    }
}
