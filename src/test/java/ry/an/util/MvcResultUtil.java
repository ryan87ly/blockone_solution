package ry.an.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static ry.an.util.JsonUtil.toJsonObject;

public final class MvcResultUtil {

    public static JsonNode getContentAsJson(MvcResult result) {
        try {
            return toJsonObject(result.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    public static<T> T getContentAsObject(MvcResult result, Class<T> clazz) {
        try {
            return JsonUtil.fromJsonString(result.getResponse().getContentAsString(), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }
}
