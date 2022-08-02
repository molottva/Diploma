[![Build status](https://ci.appveyor.com/api/projects/status/kt9uq5rk9am96hh6?svg=true)](https://ci.appveyor.com/project/molottva/diploma)

# Дипломный проект профессии «Тестировщик»

## Запуск SUT, авто-тестов и генерация репорта

### Подключение SUT к PostgreSQL

1. Запустить Docker Desktop
1. Открыть проект в IntelliJ IDEA
1. В терминале в корне проекта запустить контейнеры:

   `docker-compose up -d`
1. Запустить приложение:

   `java -jar .\artifacts\aqa-shop\aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app`
1. Открыть второй терминал
1. Запустить тесты:

   `.\gradlew clean test -DdbUrl=jdbc:postgresql://localhost:5432/app`
1. Создать отчёт Allure и открыть в браузере

   `.\gradlew allureServe`
1. Закрыть отчёт:

   **CTRL + C -> y -> Enter**
1. Перейти в первый терминал
1. Остановить приложение:

   **CTRL + C**
1. Остановить контейнеры:

   `docker-compose down`
   </a>

### Подключение SUT к MySQL

1. Запустить Docker Desktop
1. Открыть проект в IntelliJ IDEA
1. В терминале в корне проекта запустить контейнеры:

   `docker-compose up -d`
1. Запустить приложение:

   `java -jar .\artifacts\aqa-shop\aqa-shop.jar --spring.datasource.url=jdbc:mysql://localhost:3306/app`
1. Открыть второй терминал
1. Запустить тесты:

   `.\gradlew clean test -DdbUrl=jdbc:mysql://localhost:3306/app`
1. Создать отчёт Allure и открыть в браузере

   `.\gradlew allureServe`
1. Закрыть отчёт:

   **CTRL + C -> y -> Enter**
1. Перейти в первый терминал
1. Остановить приложение:

   **CTRL + C**
1. Остановить контейнеры:

   `docker-compose down`
   </a>

## Документация

1. [Текст задания](docs/Exercise.md)

1. [План автоматизации](docs/Plan.md)

1. [Отчётные документы по итогам тестирования](docs/Report.md)

1. Отчётные документы по итогам автоматизации
   </a>
