[По-русски](readme_files%2FREADME_rus.md)

# ShareIt

--- 

*Backend service for renting or sharing\* any item, tool, device, etc. with the ability to book and create a request.*

---

How often does it happen that some item you need so much here and now is not available? You can try to solve the problem 
by buying a new item, repairing a broken one or searching for analogues from friends - but often this is bad idea. 
How great it would be if there was a service where users share things!
![shareit-use.jpg](readme_files%2Fshareit-use.jpg)

---

This application provides to users the abilities to:

1. Show what things they are willing to share;
2. Find the right thing and rent it (or may be get it for free) for some time;
3. Leave requests for the thing they need;
4. Book an item for certain dates, while blocking access to it from others during the booking period;
5. Send requests for an item, if the required item is not available on the service;
6. Add new things for sharing by the request of another user;
7. View a list of your items saved in the service, as well as the last and next bookings for them;
8. View the list of bookings they have made for items;
9. View a list of bookings for their items with various selection options: 
  - ALL - all bookings; 
  - CURRENT - current bookings; 
  - PAST - past bookings; 
  - FUTURE - upcoming bookings; 
  - WAITING - waiting for a response; 
  - REJECTED - rejected bookings;
10. Leave a comment about the item after use.

---

Java version - 11;

The application is based on the Spring Boot v. 2.7.9;

Build system - Apache Maven;

Database - PostgreSQL;

Accessing the database and mapping entities - spring-boot-starter-data-jpa, hibernate;

Testing - JUnit, Mockito;

Containerization system - Docker + docker-compose.

The service is divided into 2 modules:

gateway - accepts requests from user, carries out preliminary validation of input data, sends valid requests to the server 
and then broadcasts the response.

server - accepts requests from gateway, performs logical validation, and performs CRUD operations in the database.

---

Instructions for running the application locally:
1. Software is required to run the application
- Git (installation guide option - https://learn.microsoft.com/ru-ru/devops/develop/git/install-and-set-up-git);
- JDK (java SE11+, version of the installation guide - https://blog.sf.education/ustanovka-jdk-poshagovaya-instrukciya-dlya-novichkov/);
- Apache Maven (installation guide option on Windows - https://byanr.com/installation-guides/maven-windows-11/);
- Docker (& docker-compose) - to work in Windows you will need a virtual machine running Linux - 
a version of the guide for installing it is https://learn.microsoft.com/ru-ru/windows/wsl/install.
2. Once launched, the application will accept http requests in accordance with the API (see below) to port 8080 
(http://localhost:8080/), make sure it is free, otherwise you will need to change the corresponding settings 
in the files application.properties and docker-compose.yml.
3. Launch terminal/command line/PowerShell, execute the commands one by one, waiting for each one to complete:
```
cd {destination directory to download the project}

git clone git@github.com:RuslanYapparov/java-shareit.git

cd java-shareit/

mvn package

docker-compose up
```
4. To run a test script, you can use the test collection (see below). Tests are sensitive to time zone - if your 
time zone differs from UTC+0, then to pass some tests you will need to transfer the server module to the container TZ variable 
with your time zone.

---

API Description (OpenAPI):

[shareit-server&gateway-API.json](shareit-server%26gateway-API.json)

to view you need to copy and open the content in Swagger editor

---

Test collection (Postman):

[shareIt.postman_collection.json](shareIt.postman_collection.json)

import a collection by copying the contents into the field as Raw text

---

The application database is designed in accordance with the ER diagram (light version, created using dbdiagram.io):

![shareit-er-diagram.jpg](readme_files%2Fshareit-er-diagram.jpg)

---

The application is written in Java. Sample code:

```java

public class ShareIt { 

    public static void main(String[] args) { 
        System.out.println("Let's start sharing items!"); 
    }
    
}
```