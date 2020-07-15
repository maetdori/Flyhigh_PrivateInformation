package com.web.service;
 
import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
 
import com.web.domain.CertVO;
import com.web.mapper.CertMapper;
 
@Service("com.web.service.CertService")
public class CertService {
 
    @Resource(name="com.web.mapper.CertMapper")
    CertMapper certMapper;
    
    public CertVO certSearchService(String co_name) throws Exception {
    	
    	return certMapper.certSearch(co_name);
    }
    
    public void certInsertService(CertVO cert) throws Exception {
        
        certMapper.certInsert(cert);
    }
     
    public void certUpdateService(String co_name, String co_cert_pw) throws Exception {
    	
    	certMapper.certUpdate(co_cert_pw, co_name);
    }
}
 