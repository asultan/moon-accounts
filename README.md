# ACCOUNTS SERVICE 

# Stack

![](https://img.shields.io/badge/java_11-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
![](https://img.shields.io/badge/spring_security-✓-blue.svg)
![](https://img.shields.io/badge/mysql-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger_2-✓-blue.svg)

# How to run the application

In order to to operate the application following the instructions below, you must have the following folder structure on your host:

```sh
moon
 |__ moon-accounts
 |__ moon-deploy
```

1. Create the project root folder called **moon**:

```sh
mkdir moon
```

2. Clone the two repositories from the **moon** folder:

```sh
git clone https://github.com/asultan/moon-accounts.git
git clone https://github.com/asultan/moon-deploy.git
```

Going further, all the coomands must be run from the **moon** folder.

3. Start the shared services (database, etc):

```sh
docker-compose -f moon-accounts/docker/shared-services.yaml up
```

4. Start the moon services:

```sh
$ docker run --name accounts -p 8081:8081 -d asultandev/moon-accounts:1.0.0
```

# How to push image to docker registry

```sh
$ docker login -u asultandev
$ moon-deploy/build.sh
```

# How to use the service without Docker

Make sure you have [Java 11](https://www.java.com/download/) and [Maven](https://maven.apache.org) installed and the `JAVA_HOME` is set to point to your JDK installation folder.

Navigate into the folder moon-accounts folder. Update the `application.properties` file so that `datasource.mysql.host = localhost`

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

Make a GET request to `/accounts/security/whoami` to check you're not authenticated. You should receive a response with a `403` with an `Access Denied` message since you haven't set your valid JWT token yet
```sh
$ curl -X GET http://localhost:8081/accounts/security/whoami
```

Make a POST request to `/accounts/security/login` with the default admin user (that we programmatically created) to get a valid JWT token
```sh
$ curl -X POST 'http://localhost:8081/accounts/security/login
body: 
{
    "email": "admin@moon.io",
    "password": "admin"
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
  "email": "admin@moon.io",
  "role": {
    "id": 1,
    "authority": "ADMIN"    
   }
}
```
