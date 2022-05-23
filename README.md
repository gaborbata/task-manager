# Task Manager

Simple application that manages users and tasks for those users via REST endpoints.

## Technology stack

* Java 11
* Spring Boot 
  * WebFlux/Reactor - reactive programming support for web applications
  * Flyway - database migration
  * R2DBC - reactive relational database connectivity
  * Validation - for validating DTOs
  * MapStruct - to generate mapping logic between DTOs and entities
* MariaDB
* Docker

## How to run the application

Start via Docker

```sh
docker-compose up
```

## Configuration

The application can be configured with the following environment variables (which have default values in the `.env` file):

```
APP_PORT=8080
DB_DATABASE=taskmanager
DB_USER=test
DB_PASSWORD=example
DB_ROOT_PASSWORD=secret
DB_HOST=mariadb
DB_PORT=3306
```

## Implemented endpoints

### Create user

```sh
curl -i -H 'Content-Type: application/json' -X POST -d "{'username':'jsmith','first_name' : 'John', 'last_name' : 'Smith'}"http://localhost:8080/api/user
```

### Update user

```
curl -i -H 'Content-Type: application/json' -X PUT -d "{'first_name' : 'John', 'last_name':'Doe'}" http://localhost:8080/api/user/{id}
```

### List all users

```sh
curl -i -H 'Accept: application/json' -H 'Content-Type: application/json' -X GET
http://localhost:8080/api/user
```

### Get User info

```sh
curl -i -H 'Accept: application/json' -H 'Content-Type: application/json' -X GET
http://localhost:8080/api/user/{id}
```

Expecting this structure (for the User):

```
{
  'id': 1,
  'username': 'jsmith',
  'first_name': 'James',
  'last_name': 'Smith'
}
```

### Create Task

```sh
curl -i -H 'Content-Type: application/json' -X POST -d "{'name':'My task','description' :
'Description of task', 'date_time' : '2016-05-25 14:25:00'}"
http://localhost:8080/api/user/{user_id}/task
```

### Update Task

```sh
curl -i -H 'Content-Type: application/json' -X PUT -d "{'name':'My updated task'}"
http://localhost:8080/api/user/{user_id}/task/{task_id}
```

### Delete Task

```sh
curl -i -H 'Content-Type: application/json' -X DELETE
http://localhost:8080/api/user/{user_id}/task/{task_id}
```

### Get Task Info

```sh
curl -i -H 'Accept: application/json' -H 'Content-Type: application/json' -X GET
http://localhost:8080/api/user/{user_id}/task/{task_id}
```

### List all tasks for a user

```sh
curl -i -H 'Accept: application/json' -H 'Content-Type: application/json' -X GET
http://localhost:8080/api/user/{user_id}/task
```
