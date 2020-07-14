package com.example.module.crypto;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DecryptModule {
    private static void dfs(String s, JSONArray parent, AES128Util aes)
            throws GeneralSecurityException, JSONException {
        int pos = s.indexOf("/");
        if(pos == -1) {
            for(int i = 0 ; i < parent.length();i++) {
                JSONObject responseJSON = parent.getJSONObject(i);
                String tmp = parent.getJSONObject(i).getString(s);
                responseJSON.remove(s);
                responseJSON.accumulate(s,aes.decrypt(android.util.Base64.
                        decode(tmp.getBytes(), android.util.Base64.NO_WRAP)));
            }
        } else {
            String token = s.substring(0,pos);
            String ss = s.substring(pos + 1);
            for(int i = 0;i < parent.length();i++) {
                JSONObject child = parent.getJSONObject(i);
                Object childElement = child.get(token);
                if(childElement instanceof JSONArray)
                    dfs(ss, (JSONArray) childElement, aes);
                else {
                    JSONArray arr = new JSONArray();
                    arr.put(childElement);
                    dfs(ss,arr,aes);
                }
            }
        }
    }
    public static void Decrypt(String element, JSONObject json,AES128Util aes) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json);
        try {
            Log.d("Decrypt",element);
            dfs(element,jsonArray,aes);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
