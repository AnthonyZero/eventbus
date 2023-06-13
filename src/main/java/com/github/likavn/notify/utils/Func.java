package com.github.likavn.notify.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.github.likavn.notify.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 包装转换工具
 *
 * @author likavn
 * @since 2023/01/01
 */
@Slf4j
public class Func {

    /**
     * 代理 class 的名称
     */
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
            // cglib
            , "org.springframework.cglib.proxy.Factory"
            , "javassist.util.proxy.ProxyObject"
            // javassist
            , "org.apache.ibatis.javassist.util.proxy.ProxyObject");

    private static final Map<Class<?>, Class<?>> CURRENT_MODEL = new ConcurrentHashMap<>();

    private Func() {
    }

    /**
     * toJson
     *
     * @param value object
     * @return string
     */
    public static String toJson(Object value) {
        return JSON.toJSONString(value);
    }

    /**
     * 二进制数据转换为实体数据类型
     *
     * @param requestBytes bytes
     * @return bean
     */
    @SuppressWarnings("all")
    public static Request convertByBytes(String requestStr) {
        return convertByBytes(requestStr.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 二进制数据转换为实体数据类型
     *
     * @param requestBytes bytes
     * @return bean
     */
    @SuppressWarnings("all")
    public static Request convertByBytes(byte[] requestBytes) {
        return JSON.parseObject(requestBytes, Request.class);
    }

    /**
     * @param body  数据对象
     * @param clazz 数据实体class
     * @return 转换对象
     */
    @SuppressWarnings("all")
    public static <T> T parseObject(T body, Class<T> clazz) {
        if (body instanceof String) {
            String bodyStr = body.toString();
            if (!JSONValidator.from(bodyStr).validate()) {
                return (T) bodyStr;
            }
            return JSON.parseObject(bodyStr, clazz);
        }
        return JSON.parseObject(JSON.toJSONString(body), clazz);
    }

    /**
     * 获取监听器泛型实体类
     *
     * @return 泛型实体类
     */
    @SuppressWarnings("all")
    public static Class currentModelClass(final Class<?> clazz, final Class<?> genericIfc) {
        return CURRENT_MODEL.computeIfAbsent(clazz, key -> {
            Class[] classes = GenericTypeResolver.resolveTypeArguments(isProxy(clazz) ? clazz.getSuperclass() : clazz, genericIfc);
            return null != classes ? classes[0] : null;
        });
    }

    /**
     * 判断是否为代理对象
     *
     * @param clazz 传入 class 对象
     * @return 如果对象class是代理 class，返回 true
     */
    private static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                if (PROXY_CLASS_NAMES.contains(cls.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 线程重新命名
     *
     * @param name 新名称
     * @return old thread name
     */
    public static String reThreadName(String name) {
        Thread thread = Thread.currentThread();
        String oldName = thread.getName();
        thread.setName(name + oldName.substring(oldName.lastIndexOf("-")));
        return oldName;
    }

    @SuppressWarnings("all")
    public static void resetPool(ThreadPoolExecutor poolExecutor) {
        for (Future f : poolExecutor.getQueue().toArray(new Future[0])) {
            f.cancel(true);
        }
        poolExecutor.purge();
    }
}
