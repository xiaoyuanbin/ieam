package com.hm.ieam.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonUtils {
    public static HashMap<String, ArrayList<HashMap<String, String>>> stringToJson(String json)
    {
        HashMap<String, ArrayList<HashMap<String, String>>> tableMap = new HashMap<>(); //对象存放

        int tableInt = 0;

        ArrayList<HashMap<String, String>> arr = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(json);
        for(int i=0; i<jsonArray.size(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);

            if(jsonObj.containsKey("__dsname__"))
            {
                if(tableInt != 0)
                {
                    tableMap.put("ds"+tableInt, arr);
                    arr = new ArrayList<>();
                }
                tableInt = tableInt + 1;
            }else {
                HashMap<String, String> hsmap = new HashMap<>();

                for(String key: jsonObj.keySet())
                {
                    hsmap.put(key, jsonObj.getString(key));
                }

                arr.add(hsmap);
            }

        }

        if(tableInt != 0)
        {
            tableMap.put("ds"+tableInt, arr);
        }

        //System.out.println(tableMap);
        return tableMap;
    }


}
