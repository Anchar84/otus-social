# Ученое задение "Проблемы высоких нагрузок"

### Технологический стек:
* jvm 17+
* Postgres
* Kotlin
* Spring, Spring boot
* Spring webflux (https://docs.spring.io/spring-boot/docs/3.1.3/reference/htmlsingle/index.html#web.reactive)
* R2DB (драйвер для реактивного взаимодействия с базой, https://docs.spring.io/spring-boot/docs/3.1.3/reference/htmlsingle/index.html#data.sql.r2dbc)
* Flyway (для миграции базы данных)

### Инструкция по запуску 

Перед запуском необходимо:
* создавть в postgres базу social
* настроить подключение к базк (/src/main/resources/application.yaml)

Для запуска:
* Скопилировать проект командой ` ./gradlew build`
* Запустить сервер командой ` java -jar ./build/libs/social-0.0.1-SNAPSHOT.jar`


### Полезные ссылки
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)


