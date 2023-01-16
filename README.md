# Star Wars Java API

## Introduction

This is a Java API that deliveries information about the Star Wars planets. It consumes data from
the [Star Wars API](https://swapi.co/).

## How to use

### How to run

To run the API you need java 17 installed. You can download
it [here](https://www.oracle.com/java/technologies/downloads/#java17).

After that, you can run the API using the following command:

```bash
make run
```

### How to test

To run the tests, you can use the following command:

```bash
make test
```

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

### How to access

Once running the API, you can access its documentation using the following [link](http://localhost:8080/docs/index.html).
