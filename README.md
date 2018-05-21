# Spring Boot 2 application with multiple databases configuration

This project is an example code to help all of the folks who need to use multiple databases on the same spring boot project.

Run this application using mvn springboot: run

To run this project in local/dev, use the profile "dev".

To deploy this application to appserver such as Tomcat 8/9, use the profile: "test", For Red Hat EAP 7, use the profile : "eap7".

REST Endpoint:
http://localhost:8080/multidbspring/multiperson/db/third/single/1



Data Source configurations in Tomcat:

Server.xml:

<!-- Global JNDI resources
       Documentation at /docs/jndi-resources-howto.html
  -->
	<GlobalNamingResources>
		<!-- Editable user database that can also be used by
         UserDatabaseRealm to authenticate users
    -->
		<Resource name="jdbc/GeicoDB" 
      auth="Container" 
      type="javax.sql.DataSource" 
      driverClassName="com.mysql.jdbc.Driver" 
      url="jdbc:mysql://127.0.0.1/tenant_a?useSSL=false" 
      username="YourUserName" 
      password="secret" 
      maxTotal="100" 
      maxIdle="20" 
      minIdle="5" 
      maxWaitMillis="10000"/>
  
   	<Resource name="jdbc/UsaaDB" 
      auth="Container" 
      type="javax.sql.DataSource" 
      driverClassName="com.mysql.jdbc.Driver" 
      url="jdbc:mysql://127.0.0.1/tenant_b?useSSL=false" 
      username="devuser" 
      password="devuser1" 
      maxTotal="100" 
      maxIdle="20" 
      minIdle="5" 
      maxWaitMillis="10000"/>

	<Resource name="jdbc/NationwideDB" 
      auth="Container" 
      type="javax.sql.DataSource" 
      driverClassName="com.mysql.jdbc.Driver" 
      url="jdbc:mysql://127.0.0.1/tenant_c?useSSL=false" 
      username="YourUserName" 
      password="secret" 
      maxTotal="100" 
      maxIdle="20" 
      minIdle="5" 
      maxWaitMillis="10000"/>

	
 </GlobalNamingResources>
      
      
  Context.xml:
  
  <Context>

   	<ResourceLink name="jdbc/GeicoDB"
                global="jdbc/GeicoDB"
                auth="Container"
                type="javax.sql.DataSource" />
	<ResourceLink name="jdbc/UsaaDB"
                global="jdbc/UsaaDB"
                auth="Container"
                type="javax.sql.DataSource" />
	<ResourceLink name="jdbc/NationwideDB"
                global="jdbc/NationwideDB"
                auth="Container"
                type="javax.sql.DataSource" />
</Context>

Data Source configuration in EAP 7 standalone.xml:
<subsystem xmlns="urn:jboss:domain:datasources:5.0">
            <datasources>
                <datasource jndi-name="java:jboss/datasources/GeicoDB" pool-name="GeicoDB" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/tenant_a</connection-url>
                    <driver>mySql</driver>
                    <security>
                        <user-name>devuser</user-name>
                        <password>devuser1</password>
                    </security>
                </datasource>
  </datasources>
  
  Add a new module.xml file for my sql if not already done:
  
  <module xmlns="urn:jboss:module:1.5" name="com.mysql">
    <properties>
        <property name="jboss.api" value="unsupported"/>
    </properties>

    <resources>
        <resource-root path="mysql-connector-java-5.1.46-bin.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>

Note: Ensure mysql connector jar is available in module path based on the mysql version. Same is the case for Tomcat app server. It should be in $tomcat_home/lib.
