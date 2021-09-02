package com.acech.demo.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * json工具类
 * @author wahcen@163.com
 * @date 2021/9/2 22:09
 */
@Slf4j
@UtilityClass
public class JsonUtil {
    public static ObjectMapper objectMapper = null;

    private static final String JSON_PATH_ROOT = "$";
    private static final String JSON_PATH_SEPARATOR = ".";
    private static final String JSON_PATH_ARRAY_INDICATOR = "[*]";

    static {
        objectMapper = SpringUtil.getBean(ObjectMapper.class);
    }

    @SneakyThrows
    public List<String> extractAllJsonPathFromRawJsonStr(String rawJsonStr) {
        JSONObject jsonObject = new JSONObject(rawJsonStr);
        List<String> jsonPaths = new ArrayList<>();
        jsonPathHelper(JSON_PATH_ROOT, jsonObject, jsonPaths);
        return jsonPaths;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void jsonPathHelper(String parent, JSONObject current, List<String> paths) {
        paths.add(parent);
        if (!current.keys().hasNext()) {
            log.warn("No attributes defined in {}", parent);
            return;
        }

        Iterator<String> keyIterator = current.keys();
        while (keyIterator.hasNext()) {
            String attrKey = keyIterator.next();
            Object attrVal = current.get(attrKey);

            String extractedKey = StringUtil.join(JSON_PATH_SEPARATOR, parent, attrKey);
            if (attrVal instanceof JSONObject) {
                JSONObject subJsonObject = current.getJSONObject(attrKey);
                jsonPathHelper(extractedKey, subJsonObject, paths);
            } else if (attrVal instanceof JSONArray) {
                JSONArray subJsonArray = current.getJSONArray(attrKey);
                if (subJsonArray.length() > 0) {
                    Object firstObj = subJsonArray.get(0);
                    if (firstObj instanceof JSONObject) {
                        jsonPathHelper(extractedKey + JSON_PATH_ARRAY_INDICATOR, (JSONObject) firstObj, paths);
                    } else {
                        paths.add(extractedKey + JSON_PATH_ARRAY_INDICATOR);
                    }
                } else {
                    log.warn("No elements in attr {}, attr will be treated as primitive-type array", extractedKey + JSON_PATH_ARRAY_INDICATOR);
                    paths.add(extractedKey + JSON_PATH_ARRAY_INDICATOR);
                }
            } else {
                paths.add(extractedKey);
            }
        }
    }
}
