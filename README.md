# Should I click this? Backend

## Requirements

* Java 17 or newer
* Maven 3.9.5 or newer
* KeyDB instance
* Docker (optional)

## Setup

### Environment Variables (Required for deployment, Optional for local development)

```
REDIS_PRIVATE_URL=redis:redis://localhost:6379/1 //Keydb Url
AUTH_PASSWORD=password //Password for basic auth
AUTH_USERNAME=username //Username for basic auth  
UNBLOCK_LIST= http://localhost:5173,http://127.0.0.1:5173 //list of urls that are allowed to call the api
```

### KeyDB

#### KeyDB on Docker

There is a docker-compose.yml file that can be used to host a KeyDB server locally. Run the following command in the root directory of the project.

```
$ docker-compose up
```

You'll have a KeyDB instance up locally and running on port 6379

#### Other KeyDB instances

If you have a KeyDB instace setup any other way or have a KeyDB instance located externally. You'll need to update the `spring.data.redis.url` variable in application-local.yml to the KeyDB instance url.

## Running the application

Clean up and install all the dependencies using the following command in the project root directory.

```
$ mvn clean install
```

To run the application, run the following Maven command in the project root directory.

```
$ mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Important Note

Many editors such as IntelliJ have built in Maven integration which is helpful for running the application and can help you setup the project better. Please check with your editor's documentation on how to import and run Maven projects.

## Release Notes

* [Version 2.0.0](Release%20Notes/Version-2.0.0.md)
* [Version 1.0.0](Release%20Notes/Version-1.0.0.md)
