package com.hm.ieam.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by night on 2017/11/9.
 */

public class GsonUtil {
    public static <T> T parseJSON(String json, Class<T> clazz) {
        Gson gson = new Gson();
        T info = gson.fromJson(json, clazz);
        return info;
    }
    public static <T> T parseJSON(String json, Type type) {
        Gson gson = new Gson();
        T info = gson.fromJson(json, type);
        return info;
    }

    public static <T> List<T> jsonToList(String json, Class<? extends T[]> clazz)
    {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
    {
        Type type = new TypeToken<ArrayList<JsonObject>>()
        {}.getType();
        JsonReader jsonReader = new JsonReader(new StringReader(json));//其中jsonContext为String类型的Json数据
        jsonReader.setLenient(true);

        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects)
        {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    private GsonUtil(){}

}
