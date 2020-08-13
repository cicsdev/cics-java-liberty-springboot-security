# cics-java-liberty-springboot-security

This sample project demonstrates how you can write a Spring Boot application to integrate with CICS security when you deploy it to a CICS Liberty JVM server.       

The application uses Java EE form authentication, validating users credentials using the Liberty user registry. 
User roles are also determined by Liberty configuration. The user ID and roles are then passed to Spring Security, where they can be used for any authorization checks it wants to make. 
CICS also uses the user ID when attaching the CICS task and for any CICS resource security checks. 

The sample was built by combining the following existing Spring samples:
- [Spring Boot secure web smoke test](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-tests/spring-boot-smoke-tests/spring-boot-smoke-test-web-secure)  
  
  [This article](https://spring.io/guides/gs/securing-web/) describes how some of this code works (although is based on a different sample project)
- [Spring Security pre-authentication sample](https://github.com/spring-projects/spring-security/tree/master/samples/javaconfig/preauth)

  Java EE pre-authentication is described in the [Spring security documentation](https://docs.spring.io/spring-security/site/docs/5.2.0.RELEASE/reference/html/jc-authentication.html#java-ee-container-authentication)


## Prerequisites

  - CICS TS V5.3 or later
  - A configured Liberty JVM server in CICS
  - Java SE 1.8 on the z/OS system
  - Java SE 1.8 on the workstation
  - An Eclipse development environment on the workstation (optional)
  - Either Gradle or Apache Maven on the workstation (optional if using Wrappers)

## Reference

More information about the development of this sample can be found in the accompanying tutorial [Spring Boot Java applications for CICS - Part 2 - Security](blog/blog.md)

## Downloading

- Clone the repository using your IDEs support, such as the Eclipse Git plugin
- **or**, download the sample as a [ZIP](https://github.com/cicsdev/cics-java-liberty-springboot-security/archive/master.zip) and unzip onto the workstation

>*Tip: Eclipse Git provides an 'Import existing Projects' check-box when cloning a repository.*


### Check dependencies
 
Before building this sample, you should verify that the correct CICS TS bill of materials (BOM) is specified for your target release of CICS. 
The BOM specifies a consistent set of artifacts, and adds information about their scope. In the example below the version specified is compatible with CICS TS V5.5 with JCICS APAR PH25409, or newer. 
That is, the Java byte codes built by compiling against this version of JCICS will be compatible with later CICS TS versions and subsequent JCICS APARs. 
You can browse the published versions of the CICS BOM at [Maven Central.](https://mvnrepository.com/artifact/com.ibm.cics/com.ibm.cics.ts.bom)
 
Gradle (build.gradle): 

`compileOnly enforcedPlatform("com.ibm.cics:com.ibm.cics.ts.bom:5.5-20200519131930-PH25409")`

Maven (POM.xml):

``` xml    
<dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>com.ibm.cics</groupId>
        <artifactId>com.ibm.cics.ts.bom</artifactId>
        <version>5.5-20200519131930-PH25409</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  ```

## Building 

You can build the sample using an IDE of your choice, or you can build it from the command line. For both approaches, using the supplied Gradle or Maven wrapper is the recommended way to get a consistent version of build tooling. 

On the command line, you simply swap the Gradle or Maven command for the wrapper equivalent, `gradlew` or `mvnw` respectively.
  
For an IDE, taking Eclipse as an example, the plug-ins for Gradle *buildship* and Maven *m2e* will integrate with the "Run As..." capability, allowing you to specify whether you want to build the project with a Wrapper, or a specific version of your chosen build tool.

The required build-tasks are typically `clean bootWar` for Gradle and `clean package` for Maven. Once run, Gradle will generate a WAR file in the `build/libs` directory, while Maven will generate it in the `target` directory.

**Note:** When building a WAR file for deployment to Liberty it is good practice to exclude Tomcat from the final runtime artifact. We demonstrate this in the pom.xml with the *provided* scope, and in build.gradle with the *providedRuntime()* dependency.

**Note:** If you import the project to your IDE, you might experience local project compile errors. To resolve these errors you should run a tooling refresh on that project. For example, in Eclipse: right-click on "Project", select "Gradle -> Refresh Gradle Project", **or** right-click on "Project", select "Maven -> Update Project...".

>Tip: *In Eclipse, Gradle (buildship) is able to fully refresh and resolve the local classpath even if the project was previously updated by Maven. However, Maven (m2e) does not currently reciprocate that capability. If you previously refreshed the project with Gradle, you'll need to manually remove the 'Project Dependencies' entry on the Java build-path of your Project Properties to avoid duplication errors when performing a Maven Project Update.*  

#### Gradle Wrapper (command line)

Run the following in a local command prompt:

On Linux or Mac:

```shell
./gradlew clean bootWar
```
On Windows:

```shell
gradlew.bat clean bootWar
```

This creates a WAR file inside the `build/libs` directory.

#### Maven Wrapper (command line)


Run the following in a local command prompt:

On Linux or Mac:

```shell
./mvnw clean package
```

On Windows:

```shell
mvnw.cmd clean package
```

This creates a WAR file inside the `target` directory.



## Deploying

1. Ensure you have a SAF registry configured in `server.xml`.    
1. Ensure you have the following features defined in your Liberty `server.xml`:           
    - `<servlet-3.1>` or `<servlet-4.0>` depending on the version of Java EE in use.  
    - `<cicsts:security-1.0>`        
    > **Note:** `servlet-4.0` will only work for CICS TS V5.5 or later as this requires Java EE 8 support    
1. Manually upload the WAR file from your *target* or *build/libs* directory to zFS 
1. Add an `<application>` element to the Liberty `server.xml`. If you want to use Java EE security add an `<application-bnd>` sub-element specifying the userid to be granted access to the ROLE_USER. 

For example the following application element can be used to grant the userid *MYUSER* access to the required role `ROLE_USER`.
 
``` XML
<application id="cics-java-liberty-springboot-security-0.1.0"  
    location="${server.config.dir}/springapps/cics-java-liberty-springboot-security-0.1.0.war"  
    name="cics-java-liberty-springboot-security-0.1.0" type="war">
    <application-bnd>
            <security-role name="ROLE_USER">
                <user name="MYUSER"/>
         </security-role>
      </application-bnd> 
</application>
```

Alternatively, you can use Liberty SAF authorization using EJBROLE profiles to give users access to the ROLE_USER role. 


> **Note:** If you set the CICS SIT parameter `SEC=YES` and use autoconfiguration, CICS generates a server.xml including both the `cicsts:security-1.0` feature and a SAF registry. For more information, see [Configuring a Liberty JVM server](https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.5.0/configuring/java/config_jvmserver_liberty.html) in the CICS Knowledge Center.
       
## Trying out the sample

1. Ensure the web application started successfully in Liberty by checking for message `CWWKT0016I` in the Liberty messages.log:
    - `A CWWKT0016I: Web application available (default_host): http://myzos.mycompany.com:httpPort/cics-java-liberty-springboot-security-0.1.0`
    - `I SRVE0292I: Servlet Message - [com.ibm.cicsdev.springboot.security-0.1.0]:.Initializing Spring embedded WebApplicationContext`

2. Copy the context root from message CWWKT0016I e.g. `http://myzos.mycompany.com:32000/cics-java-liberty-springboot-security-0.1.0/`. 

3. Visit the URL from the browser and log in using the user ID that was granted access to ROLE_USER.

You should now be able to access the homepage for the applicaition.

## License
This project is licensed under [Eclipse Public License - v 2.0](LICENSE). 

