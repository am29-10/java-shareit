<p align="center">
  <img src="https://github.com/am29-10/java-shareit/blob/main/images/shareIt%20log.png">
</p>

## Назначение:
**ShareIt** — приложение в котором можно обмениваться с друзьями вещами на время: инструментами, книгами, гаджетами и так далее. Как каршеринг, только для вещей.

## Инструкция по развертыванию проекта:
1. Скачать данный репозиторий
2. mvn clean
3. mvn package
4. docker-compose build
5. docker-compose up -d

## Сервисы:
* **Gateway** — содержит контроллеры, с которыми непосредственно работают пользователи, вместе с валидацией входных данных.  
Порт: 8080
* **Main** — содержит всю основную логику приложения.  
Порт: 9090  
    * API для работы с пользователями
    * API для работы с вещами
    * API для работы с заказами
    * API для работы с запросами
    * API для работы с комментариями



## Схема архитектуры проекта:
<p align="center">
  <img src="https://github.com/am29-10/java-shareit/blob/main/images/shareit%20architecture.png">
</p>

## Схема базы данных:
<p align="center">
  <img src="https://github.com/am29-10/java-shareit/blob/main/images/shareItd%20db.png">
</p>
