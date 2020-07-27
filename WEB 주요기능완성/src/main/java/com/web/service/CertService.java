package com.web.service;
 
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
 
import com.web.domain.CertVO;
import com.web.mapper.CertMapper;
 
@Service("com.web.service.CertService")
public class CertService {
 
    @Resource(name="com.web.mapper.CertMapper")
    CertMapper certMapper;
    
    public List<CertVO> getCertList() throws DataAccessException{
    	return certMapper.certList();
    }
    
    public CertVO certSearchService(String co_name) throws DataAccessException{
    	return certMapper.certSearch(co_name);
    }
    
    public void certInsertService(CertVO cert) throws DataAccessException{
        
        certMapper.certInsert(cert);
    }
     
    public void certUpdateService(CertVO cert) throws DataAccessException {
    	certMapper.certUpdate(cert);
    }
    
    public void certDeleteService(String co_name) throws DataAccessException {
    	certMapper.certDelete(co_name);
    }
}
 