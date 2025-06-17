
# Upgrade Java Project

## 🖥️ Project Information
- Project path: /Users/ihcomega/workspace/java-on-azure-day
- Java version: 21
- Build tool: Maven
- Maven path: /opt/homebrew/Cellar/maven/3.9.10/bin

## 🎯 Goals
- Upgrade Java from 8 to 21


## 🔀 Changes

### Test Changes
|     | Total | Passed | Failed | Skipped | Errors |
|-----|-------|--------|--------|---------|--------|
| Before | 90 | 0 | 90 | 0 | 0 |
| After | 90 | 90 | 0 | 0 | 0 |

### Dependency Changes


#### Upgraded Dependencies
| Dependency | Original Version | Current Version | Module |
|------------|------------------|-----------------|--------|
| com.h2database:h2 | 1.4.200 | 2.2.224 | ticket-reservation |

#### Added Dependencies
|   Dependency   | Version | Module |
|----------------|---------|--------|
| org.springframework.boot:spring-boot-starter-web | 3.2.12 | ticket-reservation |
| org.springframework.boot:spring-boot-starter-data-jpa | 3.2.12 | ticket-reservation |
| org.springframework.boot:spring-boot-starter-validation | 3.2.12 | ticket-reservation |
| org.apache.tomcat.embed:tomcat-embed-jasper | 10.1.33 | ticket-reservation |
| jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api | 3.0.2 | ticket-reservation |
| org.glassfish.web:jakarta.servlet.jsp.jstl | 3.0.1 | ticket-reservation |
| org.springframework.boot:spring-boot-starter-test | 3.2.12 | ticket-reservation |

#### Removed Dependencies
|   Dependency   | Version | Module |
|----------------|---------|--------|
| org.springframework:spring-webmvc | 4.3.30.RELEASE | ticket-reservation |
| org.springframework:spring-orm | 4.3.30.RELEASE | ticket-reservation |
| org.springframework:spring-tx | 4.3.30.RELEASE | ticket-reservation |
| org.hibernate:hibernate-core | 5.1.17.Final | ticket-reservation |
| org.hibernate:hibernate-entitymanager | 5.1.17.Final | ticket-reservation |
| org.apache.commons:commons-dbcp2 | 2.8.0 | ticket-reservation |
| javax.servlet:javax.servlet-api | 3.1.0 | ticket-reservation |
| javax.servlet.jsp:javax.servlet.jsp-api | 2.3.3 | ticket-reservation |
| javax.servlet:jstl | 1.2 | ticket-reservation |
| commons-fileupload:commons-fileupload | 1.4 | ticket-reservation |
| org.hibernate:hibernate-java8 | 5.1.17.Final | ticket-reservation |
| org.slf4j:slf4j-api | 1.7.30 | ticket-reservation |
| ch.qos.logback:logback-classic | 1.2.3 | ticket-reservation |
| junit:junit | 4.13.2 | ticket-reservation |

### Code commits( 12 files changed, 160 insertions(+), 319 deletions(-))

- c9f00b3 -- Use OpenRewrite to update the project to Java 21

- a57653f -- Completed migration from Spring MVC 4.3 to Spring Boot 3.2 with Java 21

- e58ebc3 -- Fixed behavioral consistency issues identified during validation

### Potential Issues

#### Behavior Changes
- [EventDAO.java](.github/java-upgrade/20250615173218435/summary.md/src/main/java/com/example/ticketreservation/dao/EventDAO.java)
  - [Severity: **MAJOR**] The date filtering condition (`eventDate > :now`) was accidentally removed from the query, which means expired events with available seats will also be returned. This could lead to users being able to book tickets for past events.
  - [Severity: **MAJOR**] The removal of date and available seats filtering could allow booking of expired events or fully booked events, potentially causing overbooking issues.
  - [Severity: **MAJOR**] The removal of available seats filtering could allow booking of fully booked events, potentially causing overbooking issues.
  - [Severity: **MAJOR**] The removal of available seats filtering could allow booking of fully booked events, potentially causing overbooking issues.
