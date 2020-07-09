package com.prj.dao;

import java.util.Map;

public interface DBMapper {
    public void insertRow (Map<String,String> map);
    public Map<String,String> selectById (String id);
}
