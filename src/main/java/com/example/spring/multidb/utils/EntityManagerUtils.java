package com.example.spring.multidb.utils;


import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Component;

@Component
public class EntityManagerUtils {

    @Autowired
    @Qualifier("mainEntityManager")
    private EntityManager mainDatabase;

    @Autowired
    @Qualifier("secondEntityManager")
    private EntityManager secondDatabase;

    @Autowired
    @Qualifier("thirdEntityManager")
    private EntityManager thirdDatabase;
    

    public EntityManager getEntityManager(String url) throws Exception{

        if(url.contains("main")) {
        	System.err.println("Loading Main Database...");
            return mainDatabase;
        }
            else if(url.contains("second")) {
        	System.err.println("Loading second Database...");
        	return secondDatabase;
            }
        	else if(url.contains("third")) {
	        	System.err.println("Loading third Database...");
	        	return thirdDatabase;
        	}
        throw new Exception("No matching Entity Manager found. Please ensure request path contains one of these: [ main, second, third ] ..");
        
    }

    
    public JpaRepositoryFactory getJpaFactory(String url) throws Exception{
        return new JpaRepositoryFactory( getEntityManager (url) );
    }

}
