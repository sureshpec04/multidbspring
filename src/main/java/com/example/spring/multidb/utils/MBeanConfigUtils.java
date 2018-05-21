package com.example.spring.multidb.utils;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
//@Profile("server")
@Slf4j
public class MBeanConfigUtils {

	private final ApplicationContext context;
	
	public MBeanConfigUtils (ApplicationContext context) {
		this.context = context;
	}
	
	public void excludeMBeanIfNecessary(Object candidate, String beanName) {
		try {
			MBeanExporter mbeanExporter = this.context.getBean(MBeanExporter.class);
			if (JmxUtils.isMBean(candidate.getClass())) {
				mbeanExporter.addExcludedBean(beanName);
				log.info("Excluding the bean : {} from MBean Exporter.." , beanName );
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// No exporter. Exclusion is unnecessary
		}
	}
}
