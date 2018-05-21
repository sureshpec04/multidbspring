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
//@Profile("stage")
public class DatabaseSecond {

    /*@Value("${second.db.driver}")
    private String driver;
    @Value("${second.db.url}")
    private String url;
    @Value("${second.db.username}")
    private String username;
    @Value("${second.db.password}")
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
    
    /*@Bean(name = "secondDataSource")
    public DataSource secondDataSource() {
      
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
        ds.setPoolName("SecondDataBaseHikariPool");
       // ds.setMinimumIdle(1);
        ds.setIdleTimeout(5000);
        return ds;
    }*/
    
    @Bean(name = "secondDataSource")
    @ConditionalOnMissingBean(name="secondDataSource")
    @Profile({ "dev" , "default"  })
    public DataSource secondDataSource ( @Value("${second.db.driver}") String driver , @Value("${second.db.url}") String url ,    
    		@Value("${second.db.username}")  String username ,  @Value("${second.db.password}") String password ) {
       
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
        ds.setPoolName("SecondDataBaseHikariPool");
        ds.setIdleTimeout(5000);
        return ds;
    }
    
    @Profile( { "test"  , "eap7" })
    @Bean( name = "secondDataSource", destroyMethod = "" )
    @ConditionalOnMissingBean(name= "secondDataSource")
    public DataSource getSecondDataSource(@Value("${second.db.jndi}") String jndiName ) throws Exception
    {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        dataSourceLookup.setResourceRef(true);
        DataSource dataSource = dataSourceLookup.getDataSource( jndiName );
        mbeanUtil.excludeMBeanIfNecessary(dataSource, "secondDataSource"); //Needed for Tomcat 9 deployment as MBean is registered twice
        return dataSource;
    }

    @Bean(name = "secondEntityManager")
    public LocalContainerEntityManagerFactoryBean secondEntityManager(@Qualifier("secondDataSource") DataSource secondDataSource ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(secondDataSource);
        em.setPackagesToScan(new String[] { packageScan });
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPersistenceUnitName("Second Database/tenant_b");
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager secondTransactionManager(@Qualifier("secondEntityManager") 
    EntityManagerFactory secondEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(secondEntityManager);
        return transactionManager;
    }

  /*  @Bean(name = "secondSessionFactory")
    public LocalSessionFactoryBean secondSessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(secondDataSource());
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

