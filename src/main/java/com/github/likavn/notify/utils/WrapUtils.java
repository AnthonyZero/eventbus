package com.github.likavn.notify.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.likavn.notify.domain.MsgRequest;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TimeZone;

/**
 * 包装转换工具
 *
 * @author likavn
 * @since 2023/01/01
 */
@Slf4j
public class WrapUtils {

    private WrapUtils() {
    }

    /**
     * ObjectMapper
     */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        // ObjectMapper init
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    /**
     * toJson
     *
     * @param value object
     * @return string
     */
    @SuppressWarnings("all")
    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            log.error("WrapUtils.toJson", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * json string to object
     *
     * @param requestBytes object
     * @return string
     */
    @SuppressWarnings("all")
    public static <T> T readJson(byte[] requestBytes, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(requestBytes, valueType);
        } catch (Exception ex) {
            log.error("WrapUtils.readJson", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 二进制数据转换为实体数据类型
     *
     * @param requestBytes bytes
     * @return bean
     */
    @SuppressWarnings("all")
    public static MsgRequest convertByBytes(String requestBytes) {
        return convertByBytes(requestBytes.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 二进制数据转换为实体数据类型
     *
     * @param requestBytes bytes
     * @return bean
     */
    @SuppressWarnings("all")
    public static MsgRequest convertByBytes(byte[] requestBytes) {
        MsgRequest request = readJson(requestBytes, MsgRequest.class);
        Object body = request.getBody();
        if (body instanceof Map) {
            try {
                request.setBody(OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(body), request.getBodyClass()));
                return request;
            } catch (Exception e) {
                log.error("WrapUtils.convertByBytes", e);
            }
        }
        return request;
    }

}
