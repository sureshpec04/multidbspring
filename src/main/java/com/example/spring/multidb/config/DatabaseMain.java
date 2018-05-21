package com.example.spring.multidb.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.spring.multidb.utils.MBeanConfigUtils;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.example")
//@PropertySource("file:./database.properties")
//@ActiveProfiles({ "test" , "default " })
@EnableJpaRepositories(
        basePackages = "com.example",
        entityManagerFactoryRef = "mainEntityManager",
        transactionManagerRef = "mainTransactionManager")
public class DatabaseMain {

    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.ddl-auto}")
    private String ddlAuto;
    @Value("${hibernate.show_sql}")
    private boolean showSQL;
    @Value("${hibernate.format_sql}")
    private boolean formatSQL;
    @Value("${entitymanager.packages.to.scan}")
    private String packageScan;
    @Value("${connection.release_mode}")
    private String releaseMode;
   
    
    @Autowired
    private MBeanConfigUtils mbeanUtil;

    @Bean(name = "mainDataSource")
    @Primary
    @ConditionalOnMissingBean(name="mainDataSource")
    @Profile({ "dev" , "default " })
    public DataSource mainDataSource ( @Value("${main.db.driver}") String driver , @Value("${main.db.url}") String url ,    
    		@Value("${main.db.username}")  String username ,  @Value("${main.db.password}") String password ) {
        /*DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);*/
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
        ds.setPoolName("MainDataBaseHikariPool");
        //ds.setMinimumIdle(1);
        ds.setIdleTimeout(5000);
        return ds;
    }
    
    @Primary
    @Profile( { "test"  , "eap7" })
    @Bean( name = "mainDataSource", destroyMethod = "" )
    @ConditionalOnMissingBean(name= "mainDataSource")
    public DataSource getMainDataSource(@Value("${main.db.jndi}") String jndiName ) throws Exception
    {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        dataSourceLookup.setResourceRef(true);
        DataSource dataSource = dataSourceLookup.getDataSource( jndiName );
        mbeanUtil.excludeMBeanIfNecessary(dataSource, "mainDataSource"); //Needed for Tomcat 9 deployment as MBean is registered twice
        return dataSource;
    }

    @Bean(name = "mainEntityManager")
    @ConditionalOnBean(name = "mainDataSource")
    @Primary
    public LocalContainerEntityManagerFactoryBean mainEntityManager(@Qualifier("mainDataSource") DataSource mainDataSource ) {
        
    	HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        
    	LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource);
        em.setPackagesToScan(new String[] { packageScan });
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPersistenceUnitName("mainDatabase/Tenant A");
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    @Bean(name = "mainTransactionManager")
    @Primary
    public PlatformTransactionManager mainTransactionManager(@Qualifier("mainEntityManager") 
    EntityManagerFactory mainEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mainEntityManager);
        return transactionManager;
    }

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
