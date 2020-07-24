package com.web.service;
 
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
 
import com.web.domain.CertVO;
import com.web.mapper.CertMapper;
 
@Service("com.web.service.CertService")
public class CertService {
 
    @Resource(name="com.web.mapper.CertMapper")
    CertMapper certMapper;
    
    public List<CertVO> getCertList() {
    	return certMapper.certList();
    }
    
    public CertVO certSearchService(String co_name) throws Exception {
    	
    	return certMapper.certSearch(co_name);
    }
    
    public void certInsertService(CertVO cert) throws Exception {
        
        certMapper.certInsert(cert);
    }
     
    public void certUpdateService(CertVO cert) throws Exception {
    	certMapper.certUpdate(cert);
    }
    
    public void certDeleteService(String co_name) throws Exception {
    	certMapper.certDelete(co_name);
    }
}
 