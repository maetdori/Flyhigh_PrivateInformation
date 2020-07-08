package com.prj.dao;

import com.prj.dto.Param;

public interface DBMapper {
    public void insertRow (Param param);
    public Param selectById (String num);
}
