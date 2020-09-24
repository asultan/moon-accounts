# ACCOUNTS SERVICE 

# Stack

![](https://img.shields.io/badge/java_11-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
![](https://img.shields.io/badge/spring_security-✓-blue.svg)
![](https://img.shields.io/badge/mysql-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger_2-✓-blue.svg)

# How to run the application

In order to to operate the application (run and/or debug) you need to follow the instructions below:

1. Create the project root folder called **moon**:

```sh
mkdir moon
```

2. Clone the two repositories from the **moon** folder:

```sh
cd moon
git clone https://github.com/asultan/moon-accounts.git
git clone https://github.com/asultan/moon-deploy.git
```

Once you're done, your folder structure should be:

```sh
moon
 |__ moon-accounts
 |__ moon-deploy
```

Going further, all the coomands must be run from the **moon** folder.

3. Start the Moon application (database & accounts service):

```sh
./moon-deploy/start-moon.sh
```

4. Start the moon services:

```sh
./moon-deploy/start-service.sh moon-accounts 8081
```

# How to use the service without Docker

Make sure you have [Java 11](https://www.java.com/download/) and [Maven](https://maven.apache.org) installed and the `JAVA_HOME` is set to point to your JDK installation folder.

Start the database:
```sh
./docker-compose -f moon-deploy/shared-service.yaml up -d
```

Navigate to **moon-accounts** folder:
```sh
cd moon-accounts 
```

Install dependencies
```sh
mvn install
```

Run the project
```sh
mvn spring-boot:run
```

Navigate to [Service API UI](http://localhost:8081/accounts) in your browser to check everything is working correctly and to understand the API. You can change the default port in the `application.properties` file
```
server.por=8081
```

Make a GET request to `/accounts/security/whoami` to check you're not authenticated. You should receive a response with a `403` with an `Access Denied` message since you haven't set your valid JWT token yet
```sh
curl -X GET http://localhost:8081/accounts/security/whoami
```

Make a POST request to `/accounts/security/login` with the default admin user (that we programmatically created) to get a valid JWT token
```sh
curl -X POST 'http://localhost:8081/accounts/security/login
body: 
{
    "email": "admin@moon.io",
    "password": "admin"
}

```
Add the JWT token as a Header parameter and make the initial GET request to `/accounts/security/whoami` again
```
curl -X GET http://localhost:8081/accounts/whoami -H 'Authorization: Bearer <JWT_TOKEN>'
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

# The REST API
In the `/moon/moon-accounts/src/test/resources` folder you will find the MOON-ACCOUNTS Postman collection and the LOCAL env setup. Import these 2 files in Postman and you can play with the API. 
Make sure you check the Pre-Request section of request to understand how the request gets authenticated. 

# Is it an asynchronious moon?
- The Users endpoints run asynchronious (for demo purpose) 
- The Tomcat embedded webserver runs using a single thread. 
- The /users (find all) endpoint has an artificial delay of 20s

Go to Postman and perform the following operations and observe what happens:
- Fire a *Find all users* request
- Imediateley do a *Find user by id* request. 
- You will notice that the same thread handles both requests
- And you will also notice that the response from the second request comes back first. 
- After the 20s delay, the first request will return a response.  

# How do I activate a user?
The `/security/register` endpoint returns a verification token. In a prod like app, this would not be part of the response, but it would have been sent via email, in order to build a URL that the user can click to activate the account.
Having only the REST API, one would need to pass the verification token in the request body of the `security/activate` request. 

# Do you have questions?
Feel free to contact me at sultanalex@gmail.com
