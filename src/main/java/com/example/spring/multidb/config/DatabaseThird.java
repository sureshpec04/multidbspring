package com.example.spring.multidb.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spring.multidb.utils.MBeanConfigUtils;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
//@PropertySource("file:./database.properties")
//@Profile("prod")
public class DatabaseThird {

   /* @Value("${third.db.driver}")
    private String driver;
    @Value("${third.db.url}")
    private String url;
    @Value("${third.db.username}")
    private String username;
    @Value("${third.db.password}")
    private String password;
   */
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.show_sql}")
    private boolean showSQL;
    @Value("${hibernate.format_sql}")
    private boolean formatSQL;
    @Value("${entitymanager.packages.to.scan}")
    private String packageScan;
    @Value("${connection.release_mode}")
    private String releaseMode;
    @Value("${hibernate.ddl-auto}")
    private String ddlAuto;

    @Autowired
    private MBeanConfigUtils mbeanUtil;
    
    /*
    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource() {
      
    	
    	HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(10);
        ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        ds.addDataSourceProperty("url", url);
        ds.addDataSourceProperty("user", username);
        ds.addDataSourceProperty("password", password);
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);
        ds.setPoolName("ThirdDataBaseHikariPool");
        //ds.setMinimumIdle(1);
        ds.setIdleTimeout(5000);
        return ds;
    }
    */
    
    @Bean(name = "thirdDataSource")
    @ConditionalOnMissingBean(name="thirdDataSource")
    @Profile({ "dev" , "default"  })
    public DataSource thirdDataSource ( @Value("${third.db.driver}") String driver , @Value("${third.db.url}") String url ,    
    		@Value("${third.db.username}")  String username ,  @Value("${third.db.password}") String password ) {
       
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(20);
        ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        ds.addDataSourceProperty("url", url);
        ds.addDataSourceProperty("user", username);
        ds.addDataSourceProperty("password", password);
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);
        ds.setPoolName("ThirdDataBaseHikariPool");
        ds.setIdleTimeout(5000);
        return ds;
    }
    
    @Profile( { "test"  , "eap7" })
    @Bean( name = "thirdDataSource", destroyMethod = "" )
    @ConditionalOnMissingBean(name= "thirdDataSource")
    public DataSource getThirdDataSource(@Value("${third.db.jndi}") String jndiName ) throws Exception
    {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        dataSourceLookup.setResourceRef(true);
        DataSource dataSource = dataSourceLookup.getDataSource( jndiName );
        mbeanUtil.excludeMBeanIfNecessary(dataSource, "thirdDataSource"); //Needed for Tomcat 9 deployment as MBean is registered twice
        return dataSource;
    }

    @Bean(name = "thirdEntityManager")
    public LocalContainerEntityManagerFactoryBean thirdEntityManager(@Qualifier("thirdDataSource") DataSource thirdDataSource ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(thirdDataSource);
        em.setPackagesToScan(new String[] { packageScan });
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPersistenceUnitName("ThirdDatabase/tenant_c");
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /*
    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(thirdEntityManager().getObject());
        return transactionManager;
    }
    */
    
    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager secondTransactionManager(@Qualifier("thirdEntityManager") 
    EntityManagerFactory thirdEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(thirdEntityManager);
        return transactionManager;
    }

   /* @Bean(name = "thirdSessionFactory")
    public LocalSessionFactoryBean thirdSessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(thirdDataSource());
        sessionFactoryBean.setPackagesToScan(packageScan);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        return sessionFactoryBean;
    }*/

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.show_sql",showSQL);
        properties.put("hibernate.format_sql",formatSQL);
        properties.put("entitymanager.packages.to.scan",packageScan);
        properties.put("connection.release_mode",releaseMode);
        return properties;
    }
}
