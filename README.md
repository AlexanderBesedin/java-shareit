# ShareIt
### Бэкенд микросервисного приложения для поиска и шеринга вещей
> ### Технологии:
> ### SpringBoot | Spring Data JPA | Hibernate | Maven | Lombok |
> ### PostgreSQL | H2 | Docker | REST API | JUnit | Mockito

### Основные объекты взаимодействия
- Бронирования `Booking`, их сроки, статусы и арендаторы и предмет бронирования.
- Запросы `ItemRequest` на добавление **отсутствующих** вещей, их создатель и дата создания.
- Пользователи `User`, которые могут предлагать, арендовать вещи `Item` и оставлять на них отзывы `Comment`.

### Функционал и команды
<details>
<summary><b>Для управления бронированиями доступно:</b></summary>

- Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем по эндпоинту `POST /bookings`, 
а затем подтверждён владельцем вещи. После создания запрос находится в статусе `WAITING` — *«ожидает подтверждения»*.
- Подтверждение или отклонение запроса на бронирование. **Может быть выполнено только владельцем вещи.** 
Затем статус бронирования становится либо `APPROVED`, либо `REJECTED`.
Эндпоинт — `PATCH /bookings/{bookingId}?approved={approved}`, параметр `approved` может принимать значения `true` или `false`.
- Получение данных о конкретном бронировании по эндпоинту `GET /bookings/{bookingId}`. 
Может быть выполнено **либо автором бронирования, либо владельцем вещи**, к которой относится бронирование.
- Получение списка всех бронирований текущего пользователя по эндпоинту `GET /bookings?state={state}&from={from}&size={size}`. 
Параметр `state` необязательный и может принимать значения:
   - `ALL` (*англ. «все»*) - устанавливается по умолчанию.
   - `CURRENT` (*англ. «текущие»*).
   - `PAST` (*англ. «завершённые»*).
   - `FUTURE` (*англ. «будущие»*).
   - `WAITING` (*англ. «ожидающие подтверждения»*).
   - `REJECTED` (*англ. «отклонённые»*).

  Бронирования возвращаются постранично (с использованием *пагинации*), отсортированными по дате от более новых к более старым,
где `from` — индекс первого элемента, начиная с 0, и `size` — количество элементов для отображения (по умолчанию равно 10).
- Получение списка бронирований для всех вещей пользователя-владельца 
по эндпоинту `GET /bookings/owner?state={state}&from={from}&size={size}`. Этот запрос имеет смысл для владельца 
хотя бы одной вещи. Работа параметров `state`, `from` и `size` аналогична сценарию предыдущего эндпоинта.
</details>

<details>
<summary><b>Для управления запросами на добавления вещей доступно:</b></summary>

- Добавить новый запрос вещи по эндпоинту `POST /requests`. Основная часть запроса — текст запроса, 
где пользователь описывает, какая именно вещь ему нужна.
- Получить список своих запросов вместе с данными об ответах на них по эндпоинту `GET /requests` 
в отсортированном порядке по дате создания от более новых к более старым. Для каждого запроса должен указываться список 
ответов в формате `requestId запроса — вещь Item`, с ее статусом доступности `available`, названием и описанием.
- Получить данные об одном конкретном запросе по эндпоинту `GET /requests/{requestId}` вместе с данными об ответах на него
в том же формате, что и в предыдущем эндпоинте. Посмотреть данные об отдельном запросе может любой пользователь.
- Получить список запросов по эндпоинту `GET /requests/all?from={from}&size={size}`, созданных другими пользователями, 
на которые можно ответить. Запросы сортируются по дате создания: от более новых к более старым, и возвращаются постранично 
с использованием *пагинации*, где `from` — индекс первого элемента, начиная с 0, 
и `size` — количество элементов для отображения (по умолчанию равно 10).
</details>

<details>
<summary><b>Для управления вещами доступно:</b></summary>

- Добавить новую вещь по эндпоинту `POST /items`, где `userId` в заголовке `X-Sharer-User-Id` — это идентификатор 
пользователя владельца вещи, `requestId` в теле запроса — идентификатор **запроса на добавление**, 
в ответ на который создаётся нужная вещь. При этом имеется возможность добавить вещь и без указания `requestId`.
Идентификатор владельца `userId` будет поступать на вход в каждом из запросов, рассмотренных далее.
- Редактировать вещь по эндпоинту `PATCH /items/{itemId}`. Изменить можно название, описание и статус доступа к аренде. 
**Редактировать вещь может только её владелец.**
- Добавить **пользователем-арендатором** комментарий на вещь по эндпоинту `POST /items/{itemId}/comment` **после её в аренды**.
- Просмотр информации **любым пользователем** о конкретной вещи по её идентификатору по эндпоинту `GET /items/{itemId}`.
- Просмотр **владельцем** списка всех его вещей по эндпоинту `GET /items` с указанием названия, описания, комментариями 
и датами последнего и ближайшего следующего бронирования для каждой вещи.
- Поиск вещи потенциальным арендатором по эндпоинту `GET /items/search?text={text}`, где в `text` передаётся текст для поиска, 
по которому система ищет вещи, содержащие его в названии или описании. Возвращаются только доступные для аренды вещи.
</details>

<details>
<summary><b>Для управления пользователями доступно:</b></summary>

- Добавить пользователя по эндпоинту `POST /users`. Пользователь должен иметь имя или логин `name` и уникальный `email`.
- Обновить пользователя по эндпоинту `PATCH /users/{userId}`.
- Получить пользователя по эндпоинту `GET /users/{userId}`.
- Получить всех пользователей по эндпоинту `GET /users`.
- Удалить пользователя по эндпоинту `DELETE /users/{userId}`.
</details>

### Особенности архитектуры
Приложение разделено на два сервиса, — `shareit-server` и `shareit-gateway`, взаимодействующих друг с другом через REST.<br>
В сервис `shareit-gateway` вынесена вся логика валидации входных данных, поступающих от клиента, в сервисе `shareit-server`
реализована основная бизнес-логика приложения.<br>
Каждый из сервисов приложения и база данных PostgreSQL запускаются в своем Docker-контейнере, взаимодействие которых 
настроено через Docker Compose.<br> Настройки развёртывания контейнеров выполнены в файле 
[docker-compose.yml](docker-compose.yml) в корне проекта.

### Запуск и тестирование
<details>
<summary><b>Деплой и запуск приложения</b></summary>

1. Зайдите на [официальный сайт проекта Docker](https://www.docker.com/products/docker-desktop/), скачайте установочный файл
для вашей операционной системы и установите Docker Desktop.
2. Выполните клонирование репозитория:
   - используя web URL: `https://github.com/AlexanderBesedin/java-shareit`
   - используя SSH-ключ: `git@github.com:AlexanderBesedin/java-shareit.git`
   - или просто скачайте zip-архив 
   по [ссылке](https://github.com/AlexanderBesedin/java-shareit/archive/refs/heads/main.zip).
3. Проверьте порты 8080, 9090, 6541 - они должны быть свободны от посторонних процессов.
4. В терминале IDE в корневой директории приложения выполните команду `mvn clean package` для создания jar-файлов сервисов приложения.
5. В терминале IDE в корневой директории приложения выполните команду `docker compose up`, которая соберет 
и запустит все контейнеры, определенные в файле `docker-compose.yml`:
   - `gateway_container` с сервисом `shareit-gateway` на портах 8080:8080
   - `server_container` с сервисом `shareit-server` на портах 9090:9090
   - `db_container` c базой данных PostgreSQL на портах 6541:5432

6. Для остановки работы приложения выполните команду `docker compose stop`, для повторного старта - `docker compose start`,
для остановки и удаления контейнеров - `docker compose down`.
7. Приложение работает по следующим базовым URL:
  - `http://localhost:8080/bookings`.
  - `http://localhost:8080/items`.
  - `http://localhost:8080/requests`.
  - `http://localhost:8080/users`.
</details>

<details>
<summary><b>Тестирование приложения</b></summary>

В проекте реализованы:
- модульные тесты с использованием библиотек `JUnit` и `Mockito`.
- интеграционные тесты, проверяющие взаимодействие с базой данных, c использованием аннотации `@SpringBootTest`.
- тесты для REST-эндпоинтов с использованием `MockMVC`.
- тесты для слоя репозиториев с использованием аннотации `@DataJpaTest`.
- тесты для работы с JSON для DTO с использованием аннотации `@JsonTest`.
- API-тесты с использованием [json-коллекции](postman/shareIt-testAPI.json).

**Суммарное количество тестов - 114, обеспеченное покрытие кода тестами - 98%.**

Запустить тесты можно в терминале IDE c помощью команды `mvn clean package` и затем проверить покрытие кода, 
открыв в браузере файл по пути `server/target/site/jacoco/index.html`.

Перед запуском API-тестов необходимо выполнить шаги 1-6 пункта **«Деплой и запуск приложения»**, 
затем импортировать json-коллекцию в выбранном вами testAPI-клиенте и запустить тесты, **соблюдая их хронологический порядок**.
</details>

### Статус проекта
На данный момент проект завершен, возможен небольшой рефакторинг методов, структуры для приведения
в соответствие лучшим практикам.