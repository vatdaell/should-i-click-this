# Should I click this? Backend

## Requirements

* Java 17 or newer
* Maven 3.9.5 or newer
* KeyDB instance
* Docker (optional)

## Setup

### KeyDB

#### KeyDB on Docker

There is a docker-compose.yml file that can be used to host a KeyDB server locally. Run the following command in the root directory of the project.

```
$ docker-compose up
```

You'll have a KeyDB instance up locally and running on port 6379

#### Other KeyDB instances

If you have a KeyDB instace setup any other way or have a KeyDB instance located externally. You'll need to update the `spring.data.KeyDB.url` variable in application-local.yml to the KeyDB instance url.

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
