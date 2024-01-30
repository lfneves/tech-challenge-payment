# MVP - Tech challenge payment-ws

## 💡 Requirements

- Java 17 or later - [SDKMAN - Recommendation](https://sdkman.io/install)
- Gradle 7.6.1 or later - [Gradle build tool Installation](https://gradle.org/install/)
- Docker 24.0.2 or later - [How to install Docker](https://docs.docker.com/engine/install/)
- Docker Compose 1.29.2 or later - [Reference guide](https://docs.docker.com/compose/install/)
- The project runs on port 8099 (http://localhost:8098).

<!-- GETTING STARTED -->
## Getting Started

```sh
# Get the latest version

git clone https://github.com/lfneves/tech-challenge-order
```
---

### Prerequisites
Check versions:
* Java 17+
  ```sh
  java --version
  ```

* Docker
  ```sh
  docker -v
  ```

* Docker Compose
  ```sh
  docker-compose --version
  ```

## Installation
This is an example of how to use the software and how to install it.

### Build artifact project first
```sh
./gradlew clean bootjar
```

### Docker

In the main project directory:

Docker build and start applications:
  ```sh 
  $ docker-compose up --build
  ```

To recreate the application in case of problems, use the command:

  ```
  $ docker-compose down
  ```
---

Distributed under the MIT License. See LICENSE.txt for more information.
