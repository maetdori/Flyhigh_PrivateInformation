package com.prj.dao;

import java.util.Map;

public interface DBMapper {
    public void insertRow (Map<String,Object> map);
    public Map<String,Object> selectById (String id);
}
