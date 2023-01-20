# Star Wars Java API

## Introduction

This is a Java API that deliveries information about the Star Wars planets. It consumes data from
the [Star Wars API](https://swapi.co/).

## How to use


### How to test

To run the tests, you can use the following command:

```bash
make test
```

The test will generate a coverage report in the `build/reports/jacoco/test/html/index.html` folder.

### How to build

To build the API, you can use the following command:

```bash
make build
```

It will create a docker image with the name `star-wars-api`. To run that image you should export the database
connections variables:

```bash
export DATABASE_URL=<database_url>;
export DATABASE_USERNAME=<database_username>;
export DATABASE_PASSWORD=<database_password>;
```

Then, you can use the following command:

```bash
docker run -p 8080:8080 -e DATABASE_URL -e DATABASE_USER -e DATABASE_PASSWORD star-wars-api
```

### How to run

#### Using local machine

To run the API you need java 17 installed. You can download
it [here](https://www.oracle.com/java/technologies/downloads/#java17).

And you need to have a local instance of PostgreSQL running. You can download
it [here](https://www.postgresql.org/download/). With an instance running, you need an user called `root` with
password `postgres` with access to a database called `star_wars`.

After that, you can run the API using the following command:

```bash
make run
```

#### Using Docker

To run the API using Docker, you need to have Docker installed. You can download
it [here](https://docs.docker.com/get-docker/).

After that, you can run the API using the following command:

```bash
make docker-run
```
### How to access

Once running the API, you can access its documentation using the following [link](http://localhost:8080/docs/index.html)
.
