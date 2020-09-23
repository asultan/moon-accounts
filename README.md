# ACCOUNTS SERVICE 

# Stack

![](https://img.shields.io/badge/java_11-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
![](https://img.shields.io/badge/spring_security-✓-blue.svg)
![](https://img.shields.io/badge/mysql-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger_2-✓-blue.svg)

# Storage Details
This service is using a MySQL database, configured to run locally. A default user can be created when the app starts. 
To enable this change the `accounts.database.DefaultDataInitializer.initData` property to `true`.
To start the mysql server run:
```sh
docker run --name mysql -p 3306:3306 -v <your_local_storage_folder>:/var/lib/mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=1 -d mysql
```
E.g.:
```sh
docker run --name mysql -p 3306:3306 -v D:\dev\mysql-data:/var/lib/mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=1 -d mysql
```

# How to push image to docker registry

```sh
$ docker login -u asultandev
$ scripts/build.sh
```

# How to use the service with Docker

```sh
$ docker pull asultandev/accounts:1.0.0
$ docker run --name accounts -p 8081:8081 -d asultandev/accounts:1.0.0
```

# How to use the service without Docker

Make sure you have [Java 8](https://www.java.com/download/) and [Maven](https://maven.apache.org) installed and the `JAVA_HOME` is set to point to your JDK installation folder.

Fork this accounts.repository and clone it
```sh
$ git clone https://<your_user>@bitbucket.org/d-menu/accounts.git
```

Navigate into the folder  
```sh
$ cd accounts
```

Install dependencies
```sh
$ mvn install
```

Run the project
```sh
$ mvn spring-boot:run
```

Navigate to [Service API UI](http://localhost:8081/accounts) in your browser to check everything is working correctly and to understand the API. You can change the default port in the following `application.properties` file
```
server.por=8081
```

Make a GET request to `/accounts/whoami` to check you're not authenticated. You should receive a response with a `403` with an `Access Denied` message since you haven't set your valid JWT token yet
```sh
$ curl -X GET http://localhost:8081/accounts/whoami
```

Make a POST request to `/accounts/security/login` with the default admin user (that we programmatically created) to get a valid JWT token
```sh
$ curl -X POST 'http://localhost:8081/accounts/login
body: 
{
    "email": "god@email.com",
    "password": "god!"
}

```

Add the JWT token as a Header parameter and make the initial GET request to `/accounts/security/whoami` again
```
$ curl -X GET http://localhost:8081/accounts/whoami -H 'Authorization: Bearer <JWT_TOKEN>'
```

And that's it, congrats! You should get a similar response to this one, meaning that you're now authenticated
```javascript
{
  "id": 1,
  "email": "god@email.com",
  "role": {
    "id": 1,
    "authority": "GOD"    
   }
}
```