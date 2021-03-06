# [ktor](https://github.com/Kotlin/ktor) with [swaggerUi](https://swagger.io/)


[![Bintray](https://img.shields.io/badge/dynamic/json.svg?label=latest%20release&url=https%3A%2F%2Fapi.bintray.com%2F%2Fpackages%2Fjustwrote%2Fmaven%2Fktor-swagger%2Fversions%2F_latest&query=name&colorB=328998&style=flat)](https://bintray.com/justwrote/maven/ktor-swagger)
![GitHub Build Status](https://img.shields.io/github/workflow/status/justwrote/ktor-swagger/CI/master?style=flat)


This project provides a library that allows you you to integrate the
 [swaggerUi](https://swagger.io/) with [ktor](https://github.com/Kotlin/ktor)

## Installation

This library is available from JCenter.

```kotlin
repositories {
  jcenter()
}

dependencies {
  implementation("it.justwrote:ktor-swagger:<version>")
}
```

See the button above or [releases/latest](https://github.com/justwrote/ktor-swagger/releases/latest) for the current version number.

## When using this with Jackson

By default, Jackson includes fields with null values in the JSON output that it generates. This results in `swagger.json` and `openapi.json` files that cannot be processed by Swagger UI properly, leading to error messages while parsing the type and format info of parameters. To prevent this, install Jackson content negotiation as follows:

```kotlin
install(ContentNegotiation) {
    jackson {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        // (You can add additional Jackson config stuff here, such as registerModules(JavaTimeModule()), etc.)
    }
}
```

## Example Usage

This library adds some extension function that build on the ktor routing feature to provide an API
that allows this feature to automatically generate a `swagger.json` file for your webserver.

```kotlin
routing {
    get<pets>("all".responds(ok<PetsModel>())) {
        call.respond(data)
    }
    post<pets, PetModel>("create".responds(created<PetModel>())) { _, entity ->
        call.respond(Created, entity.copy(id = newId()).apply {
            data.pets.add(this)
        })
    }
    get<pet>("find".responds(ok<PetModel>(), notFound())) { params ->
        data.pets.find { it.id == params.id }
            ?.let {
                call.respond(it)
            }
    }
}
```

## Project Status

This project is a proof of concept built on a library to support this functionality.

There is an open proposal to include this project as an official Ktor feature
[here](https://github.com/ktorio/ktor/issues/453).

## Other Similar Projects

[Ktor-OpenAPI-Generator](https://github.com/papsign/Ktor-OpenAPI-Generator)
