# Spring Boot Blog App RESTful API

## Features
- User authentication (JWT, Register and Login using email and password)
- User Roles (Admin and Normal user)
- Create, Update, Delete and View Posts based on authetication and user roles
- Create, Update, Delete and View Comments based on authetication and user roles
- Data validation and error handling
- [Swagger integration](#swagger)


## Prerequisites
- Java 11 or above
- Postgresql - make sure the instace is up and running at port `5432` and the DB called `blog_app` already exist.


## Running the project locally in IntelliJ CE
Go to Edit Configurations → Add New → Application (add the configuration below)
<p><img src="https://i.imgur.com/xHTvDli.png"></img></p>


## Swagger
Location `http://localhost:8080/swagger-ui/`
<p><img src="https://i.imgur.com/RHJAhkK.png"></img></p>

