package com.web.service;
 
import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
 
import com.web.domain.CertVO;
import com.web.mapper.CertMapper;
 
@Service("com.web.service.CertService")
public class CertService {
 
    @Resource(name="com.web.mapper.CertMapper")
    CertMapper certMapper;
    
    public int certInsertService(CertVO cert) throws Exception{
        
        return certMapper.certInsert(cert);
    }
     
}
 