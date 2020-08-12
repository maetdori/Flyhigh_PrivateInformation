package com.private_information;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;

class JsonDecryptModule {
    private static void dfs(String s, JSONArray parent, AES256Util aes)
            throws APIException {
        try {
            int pos = s.indexOf("/");
            if (pos == -1) {
                for (int i = 0; i < parent.length(); i++) {
                    JSONObject responseJSON = parent.getJSONObject(i);
                    String tmp = parent.getJSONObject(i).getString(s);
                    if(tmp != null && !tmp.equals("null")) {//jsonObject가 null을 읽으면 "null"이된다
                        Log.d("JSonDecryptModule","tmp: "  +tmp);
                        responseJSON.remove(s);
                        String dec = new String(aes.decrypt(android.util.Base64.
                                decode(tmp.getBytes(), android.util.Base64.NO_WRAP)));
                        Log.d("JsonDecryptModule","dec: " + dec);
                        responseJSON.accumulate(s,dec );
                    }
                }
            } else {
                String token = s.substring(0, pos);
                String ss = s.substring(pos + 1);
                for (int i = 0; i < parent.length(); i++) {
                    JSONObject child = parent.getJSONObject(i);
                    Object childElement = child.get(token);
                    if (childElement instanceof JSONArray)
                        dfs(ss, (JSONArray) childElement, aes);
                    else {
                        JSONArray arr = new JSONArray();
                        arr.put(childElement);
                        dfs(ss, arr, aes);
                    }
                }
            }
        } catch(JSONException e) {
            throw new APIException("Decrypt Response : Can't get JSON Element",APIException.DFS | APIException.JSON_GET_ERR,e);
        }
    }
    static void Decrypt(String element, JSONObject json,AES256Util aes) throws APIException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json);

        Log.d("JsonDecryptModule", "decrypt: " + element);
        dfs(element,jsonArray,aes);
    }
}
