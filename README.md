# Spring Boot Blog App RESTful API

## Features
- User authentication with JWT (Register and Login using email and password)
- User Roles (Admin and Normal user)
- Create, Update, Delete and View Posts based on authentication and user roles
- Create, Update, Delete and View Comments based on authentication and user roles
- Search Posts by Title and description
- Pagination with user defined parameters
- Data validation and error handling
- [Swagger integration](#swagger)


## Prerequisites
- Java 11 or above
- Postgresql - make sure the instance is up and running at port `5432` and the DB called `blog_app` already exist.
- User roles - After the application is started and tables are created, add roles `ROLE_ADMIN` and `ROLE_USER` to the `roles` table.
  <p><img src="https://i.imgur.com/lyLdaZe.png"></img></p>


## Running the project locally in IntelliJ CE
Go to Edit Configurations → Add New → Application (add the configuration below)
<p><img src="https://i.imgur.com/xHTvDli.png"></img></p>


## Swagger
Location `http://localhost:8080/swagger-ui/`
<p><img src="https://i.imgur.com/RHJAhkK.png"></img></p>

## Postman collection
The exported Postman collection with all the API endpoints can be found in `postman_collection` directory.

## Package and run project as a JAR file

+ **Step 1:** Building the project.<br />
  In the Maven tab, select `clean` and `install` options and click on `Run Maven Build` option. JAR file will be created in the `taeget` directory. 
  <p><img src="https://i.imgur.com/QMRkFui.png"></img></p>
  
+ **Step 2:** Running the project using JAR.<br />
  Once the project is build and JAR is generated successfully, expand the `target` folder and run the project.
  <p><img src="https://i.imgur.com/PyXb580.png"></img></p>
  

  The Project should run in `http://localhost:8080/`

## Test case execution

#### Prerequisites
* Docker up and running (to spin up a PostgreSQL DB server instead of H2 since the project consist of Postgres queries).


#### What is covered
The test cases are located in `src/test`. The following tests have been covered.
* WebLayer Testing (mock service layer & etc).
* Entity and Repository Layer isolated testing.
* Integration testing (complete E2E flow by calling the Rest APIs which includes authentication, service layers, repository layers & etc).

#### Running test cases
```mvn test```

or run test cases individually by going into the test scripts.

#### Test reporting
The test reports generated by Surefire and Jacoco will be saved into the target directory. To generate reports run
```mvn clean install```
<p><img src="https://i.imgur.com/KhPvhlN.png"></img></p>

Test report location:
<p><img src="https://i.imgur.com/cgds4AG.png"></img></p>
<p><img src="https://i.imgur.com/SJ1Ppgb.png"></img></p>
<p><img src="https://i.imgur.com/BklQvjt.png"></img></p>




