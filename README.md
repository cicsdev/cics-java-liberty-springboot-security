# cics-java-liberty-springboot-security
[![Build](https://github.com/cicsdev/cics-java-liberty-springboot-security/actions/workflows/build.yaml/badge.svg)](https://github.com/cicsdev/cics-java-liberty-springboot-security/actions/workflows/build.yaml)
[![License](https://img.shields.io/badge/License-EPL%202.0-green.svg)](https://www.eclipse.org/legal/epl-2.0/)

## Overview

This sample demonstrates how to write a Spring Boot application that integrates with CICS security when deployed to a CICS Liberty JVM server.

The application uses Jakarta EE form authentication, validating user credentials using the Liberty SAF user registry. User roles are determined by Liberty configuration and passed to Spring Security for authorization checks. CICS uses the authenticated user ID when attaching the CICS task and for any CICS resource security checks.

**Key Features:**
- Jakarta EE form-based authentication with CICS Liberty SAF registry
- Spring Security pre-authentication using the container-authenticated identity
- Role-based access control using `cicsAllAuthenticated` security role
- Thymeleaf web templates for login and home pages

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Reference](#reference)
3. [Downloading](#downloading)
4. [Check dependencies](#check-dependencies)
5. [Building the Sample](#building-the-sample)
6. [Deploying to a CICS Liberty JVM server](#deploying-to-a-cics-liberty-jvm-server)
7. [Running the Sample](#running-the-sample)
8. [Troubleshooting](#troubleshooting)
9. [License](#license)
10. [Additional Resources](#additional-resources)
11. [Contributing](#contributing)

## Prerequisites

- CICS TS V6.1 or later
- A configured Liberty JVM server in CICS
- Java SE 17 or later on the workstation
- Either Gradle or Apache Maven on the workstation (optional if using wrappers)

## Reference

- IBM Developer tutorial: [Spring Boot Java applications for CICS - Part 2 - Security](https://developer.ibm.com/tutorials/spring-boot-java-applications-for-cics-part-2-security/)
- [Spring Security pre-authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/preauth.html)

## Downloading

- Clone the repository using your IDE's support, such as the Eclipse Git plugin
- **or** download the sample as a [ZIP](https://github.com/cicsdev/cics-java-liberty-springboot-security/archive/main.zip) and unzip onto the workstation

> Tip: Eclipse Git provides an **Import existing Projects** checkbox when cloning a repository.

### Check dependencies

Before building this sample, verify that the correct CICS TS bill of materials (BOM) is specified for your target release of CICS. The BOM specifies a consistent set of artifacts and their scope. You can browse published BOM versions at [Maven Central](https://mvnrepository.com/artifact/com.ibm.cics/com.ibm.cics.ts.bom).

The BOM version is centralised in:

- **Gradle** — `gradle.properties`: `cics.bom.version=6.1-20250812133513-PH63856`
- **Maven** — root `pom.xml` `<properties>`: `<cics.bom.version>6.1-20250812133513-PH63856</cics.bom.version>`

## Building the Sample

You can build the sample using an IDE of your choice, or from the command line. Using the supplied Gradle or Maven wrapper is the recommended approach for a consistent build tool version.

#### Gradle Wrapper

On Linux or Mac:

```shell
./gradlew clean build
```

On Windows:

```shell
gradlew.bat clean build
```

#### Maven Wrapper

On Linux or Mac:

```shell
./mvnw clean verify
```

On Windows:

```shell
mvnw.cmd clean verify
```

Both commands produce a WAR file named `cics-java-liberty-springboot-security.war` — Gradle places it in `cics-java-liberty-springboot-security-app/build/libs/`, Maven in `cics-java-liberty-springboot-security-app/target/`.

## Deploying to a CICS Liberty JVM server

Ensure your Liberty `server.xml` includes the following features:

```xml
<featureManager>
    <feature>servlet-6.0</feature>
    <feature>cicsts:security-1.0</feature>
</featureManager>
```

Also ensure a SAF registry is configured in `server.xml`. A sample configuration is provided in [`etc/config/liberty/server.xml`](etc/config/liberty/server.xml).

### CICS Bundle Plugin Deployment (Gradle/Maven)

The sample includes a CICS bundle module (`cics-java-liberty-springboot-security-cicsbundle`) that uses the CICS Bundle Plugin to package and deploy the application.

1. Build the project as described above — this produces a CICS bundle ZIP in the `cicsbundle` module's output directory.
2. Upload and install the bundle ZIP to CICS using your preferred method (DFHDPLOY, zOS Management Facility, or CICS Explorer).

### CICS Explorer SDK Deployment

A pre-configured Eclipse CICS bundle project (`cics-java-liberty-springboot-security-cicsbundle-eclipse`) is included at the repository root.

1. Import the repository into Eclipse (File → Import → General → Existing Projects into Workspace, select the repository root).
2. Perform a Gradle Refresh or Maven Update Project to resolve dependencies.
3. Right-click `cics-java-liberty-springboot-security-cicsbundle-eclipse` → **Export Bundle Project to z/OS UNIX File System**.

### Direct Liberty Application Deployment

1. Copy `cics-java-liberty-springboot-security.war` to a zFS directory, for example `${server.config.dir}/springapps/`.
2. Add the following to your Liberty `server.xml`, replacing `MYUSER` with a valid SAF user ID:

```xml
<application id="cics-java-liberty-springboot-security"
    location="${server.config.dir}/springapps/cics-java-liberty-springboot-security.war"
    name="cics-java-liberty-springboot-security" type="war">
    <application-bnd>
        <security-role name="cicsAllAuthenticated">
            <user name="MYUSER"/>
        </security-role>
    </application-bnd>
</application>
```

Alternatively, use Liberty SAF authorization with EJBROLE profiles to grant users access to the `cicsAllAuthenticated` role.

## Running the Sample

1. Ensure the web application started successfully in Liberty by checking for message `CWWKT0016I` in `messages.log`:

    ```
    CWWKT0016I: Web application available (default_host): http://myzos.mycompany.com:httpPort/cics-java-liberty-springboot-security
    ```

2. Open the URL in a browser and log in using a user ID that has been granted the `cicsAllAuthenticated` role.

3. You should see the application home page, confirming that authentication and authorization succeeded.

## Troubleshooting

- **`403 Forbidden` after login** — the authenticated user ID has not been granted the `cicsAllAuthenticated` role. Check that the SAF user registry and EJBROLE profiles (or the `<application-bnd>` in `server.xml`) grant the user access to the role.
- **`CWWKS9104A` in `messages.log`** — the SAF registry is not configured. Ensure `cicsts:security-1.0` is enabled and a SAF registry (`<safRegistry>`) is configured in `server.xml`.
- **Login page not displayed** — check for `CWWKT0016I` in `messages.log` to confirm the application started; if absent, review `messages.log` for bundle or WAR deployment errors.

## License

This project is licensed under the [Eclipse Public License - v 2.0](LICENSE).

## Additional Resources

- [Developing Spring Boot applications for CICS](https://www.ibm.com/docs/en/cics-ts/6.1?topic=liberty-developing-spring-boot-applications)
- [Configuring a Liberty JVM server](https://www.ibm.com/docs/en/cics-ts/6.1?topic=liberty-configuring-jvm-server)
- [CICS TS BOM on Maven Central](https://mvnrepository.com/artifact/com.ibm.cics/com.ibm.cics.ts.bom)

## Contributing

This sample is maintained by IBM CICS development. We welcome bug reports and feature requests via GitHub Issues. Contributions are welcome and reviewed on a case-by-case basis — please read the [contributing guidelines](https://github.com/cicsdev/.github/blob/main/CONTRIBUTING.md) before opening a pull request. For CICS product questions, contact IBM Support.
