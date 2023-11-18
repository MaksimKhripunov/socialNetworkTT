# socialNetworkTT
# Обзор
Сервис является аналогом мессенджера

Cервис реализует:
Регистрация нового пользователя;

Вход пользователя в систему;

Выход пользователя из системы;

Обновление данных пользователя;

Обновление пароля пользователя;

Удаление аккаунта пользователя;

Отправление сообщений другому пользователю по его никнейму;

Просмотр истории общения с другим пользователем;

Просмотр списка друзей;

Добавление другого пользователя в друзья;

Ограничение получения сообщений своим кругом друзей;

Просмотр списка друзей другого пользователя;

Скрывание списка друзей;

Использование базы данных PostgreSQL для хранения данных о пользователях и переписках между ними;

Документирование запросов через Swagger;

# Использованные технологии
Spring Boot

Spring Web

Spring WebSockets

Spring Data Jpa

Spring Security

Spring Validation

Spring Mail

Postgresgl

Liquibase

Swagger

Docker

Maven

JWT

# Конфигурация
Для корректной работы приложения необходимо настроить Spring Mail. Для этого нужно в файле application.properties указать свои значения в полях:

spring.datasource.url
spring.datasource.username
spring.datasource.password

spring.mail.host
spring.mail.username
spring.mail.password
spring.mail.port
spring.mail.protocol

# Запуск
Склонировать репозиторий, выполнив команду: git clone https://github.com/lofominhili/TrendChat.git

Добавить environment переменные в сервисе app файла docker-compose.yaml.

Написать в терминале команду mvn clean package

Написать в терминале команду docker compose up

Либо скачать изображение и развернуть контейнер в docker:

docker pull makskhripunov/social-back:latest

# Endpoints
Все параметры на вход Rest-запросов и ответы этих запросов можно посмотреть в swagger

http://localhost:8080/swagger-ui/index.html

# Регистрация нового пользователя
Post /api/register

# Вход пользователя в систему
Post /api/login

# Обновление данных пользователя
Patch /api/edit

# Просмотр списка друзей
Get /api/friends

# Добавление другого пользователя в друзья
Get /api/friends/add/{username}

# Просмотр списка друзей другого пользователя
Get /api/{username}/friends

# Скрывание списка друзей
Patch /api/edit/friendsList
