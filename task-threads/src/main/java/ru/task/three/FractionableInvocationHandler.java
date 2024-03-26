package ru.task.three;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FractionableInvocationHandler<T> implements InvocationHandler {
    private final T obj;

    // Константа - количество аргументов в методе.
    // В нашем примере не более одного, но можем указать и больше:.
    private static final int NUMBER_OF_ARGS_IN_METHOD = 3;
    private ConcurrentHashMap<StateObject, Object> objectCache = new ConcurrentHashMap<>();

    public FractionableInvocationHandler(T obj) {
        this.obj = obj;
    }

    //Выполним глубокое копирование объекта, с помощью методов:
    public static Object reflectionObject(Object original) {
        try {
            Constructor<?>[] constructors = original.getClass().getDeclaredConstructors();
            Field[] fields = original.getClass().getDeclaredFields();
            List<Object> valueFieldList = new ArrayList<>();
            Class[] paramTypes = new Class[NUMBER_OF_ARGS_IN_METHOD];
            for (Constructor constructor : constructors) {
                paramTypes = constructor.getParameterTypes();
                for (Field field : fields) {
                    field.setAccessible(true);
                    valueFieldList.add(field.get(original));
                }
            }
            return original.getClass().getDeclaredConstructor(paramTypes).newInstance(valueFieldList.toArray());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object copy(Object original) {
        try {
            Object clone = reflectionObject(original);
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(original) == null || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                if (field.getType().isPrimitive()
                        || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Byte.class)) {
                    field.set(clone, field.get(original));
                } else {
                    Object ch = field.get(original);
                    if (ch == original) {
                        field.set(clone, clone);
                    } else {
                        field.set(clone, copy(field.get(original)));
                    }
                }
            }
            return clone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Метод для проверки - можем ли получить результат из кэша.
    public static boolean check(String s, ConcurrentHashMap<StateObject, Object> objectCache) {
        if (objectCache.isEmpty()) return false;
        for (Map.Entry<StateObject, Object> entry : objectCache.entrySet()) {
            if (s.equals(entry.getKey().getObject())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        Object objCopy = copy(obj);
        Method m = obj.getClass().getMethod(method.getName(), method.getParameterTypes());

        StateObject stateObject = new StateObject(objCopy.toString(), System.currentTimeMillis());

        new Task().start();

        if (m.isAnnotationPresent(Mutator.class)) {
            result = method.invoke(obj, args);
            objCopy = copy(obj);
        }

        if (m.isAnnotationPresent(Cache.class)) {
            long lifeTime = m.getDeclaredAnnotation(Cache.class).time();
            if (check(stateObject.getObject().toString(), objectCache)) {
                result = objectCache.get(stateObject);
                stateObject.setStartTime(System.currentTimeMillis() + lifeTime);
            } else {
                result = method.invoke(obj, args);
                stateObject.setStartTime(System.currentTimeMillis() + lifeTime);
                objectCache.put(stateObject, result);
            }
        } else {
            result = method.invoke(obj, args);
        }

        //System.out.println("objectCache " + objectCache + "; currentTime " + System.currentTimeMillis());
        //System.out.println("result " + result);

        return result;
    }

    private class Task extends Thread {
        @Override
        public void run() {
            objectCache.entrySet().removeIf(entry -> entry.getKey().getStartTime() < System.currentTimeMillis());

            //System.out.println("clearing the cache");
        }
    }
}
