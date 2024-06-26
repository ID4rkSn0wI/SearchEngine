<h1 align="center">SearchEngine</h1>
Данный проект реализует поисковый движок, предоставляющий пользователю специальный
API со следующими основными функциями:
<li>Индексирование сайтов;</li>
<li>Выдача статистики по сайтам;</li>
<li>Поиск ключевых слов в проиндексированных сайтах и предоставление их пользователю.</li>

## Веб-интерфейс
<p>
В проект также входит веб-интерфейс, который позволяет управлять процессами, реализованными
в проекте.
</p>
Страница содержит три вкладки.

### Вкладка DASHBOARD
Эта вкладка открывается по умолчанию. На ней
отображается общая статистика по всем сайтам, а также детальная
статистика и статус по каждому из сайтов (статистика, получаемая по
запросу /api/statistics).

<img src="./src/main/resources/readme_support_data/DASHBOARD.png" width="80%"/><p>

### Вкладка MANAGEMENT
На этой вкладке находятся инструменты управления
поисковым движком — запуск и остановка полной индексации
(переиндексации), а также возможность добавить (обновить) отдельную
страницу по ссылке:

<img src="./src/main/resources/readme_support_data/management.png" width="80%"/><p>

### Вкладка SEARCH
Эта страница предназначена для тестирования поискового
движка. На ней находится поле поиска, выпадающий список с выбором
сайта для поиска, а при нажатии на кнопку «Найти» выводятся
результаты поиска (по API-запросу /api/search):

<img src="./src/main/resources/readme_support_data/search.png" width="80%"/><p>

## Файл настройки - application.yaml
Данное приложение работает с СУБД MySQL.

### Раздел server
<p>
В этом разделе задаётся параметр <i>port</i> — порт, через который можно открыть веб-интерфейс.
</p>

### Раздел spring
<p>
Здесь задаются параметры СУБД, в которой приложение хранит 
данные конфигурации, такие как путь к схеме базы данных, имя пользователя пароль и другие параметры.
</p>
<p>
Следует отметить важность параметра <i>spring.jpa.hibernate.ddl-auto</i>. При значении <i>create</i>
база данных пересоздаётся и одержимое всех таблиц удаляется, в следующих запусках следует установить знаечение
<i>update</i>, чтобы данные сохранялись.
</p>

### Раздел config
На режим индексации влияют следующие параметры:
<li>
<i>request-properties.referrer</i> - url источник запроса к странице.
</li>
<li>
<i>time-between-requests</i> - промежуток времени между запросами к страницам.
</li>
<li>
<i>amount-of-words</i> - количество слов в сниппете, выдаваемом при поиске.
</li>


### Раздел <i>indexing-settings.sites</i>
Здесь приведён список сайтов, которые программа будет индексировать со следующими параметрами:
<li>
<i>url</i> — адрес сайта.
</li>
<li>
<i>name</i> — имя сайта.
</li>

## Используемые технологии

<li>
Приложение построено на платформе <i>Spring Boot</i>.
Необходимые компоненты собираются с помощью фреймворка Maven.
Maven подключает следующие относящиеся к <i>Spring Boot</i> стартеры:
</li>
<ul>
    <li>
    <i>spring-boot-starter-web</i> — подтягивает в проект библиотеки, 
    необходимые для выполнения Spring-MVC функций приложения. При этом обмен
    данными между браузером и сервером выполняется по технологии AJAX;
    </li>
    <li>
    <i>spring-boot-starter-data-jpa</i> — отвечает за подключение библиотек,
    требующихся для работы приложения с базой данных;
    </li>
    <li>
    <i>spring-boot-starter-thymeleaf</i> — шаблонизатор веб-страницы программы.
    </li>
</ul>
<li>
Для загрузки и разбора страниц с сайтов используется библиотека <i>jsoup</i>.
</li>
<li>
Данная версия программы работает с СУБД MySQL. Для этого 
подключается зависимость <i>mysql-connector-java</i>.
</li>
<li>
Для удобства написания (и чтения) программного кода и для
расширения функциональности языка Java используется библиотека
Lombok (зависимость <i>lombok</i>).
</li>
<li>
Для удобства работы со строками используется библиотека commons-lang3 в частности класс StringUtils.
</li>

## Запуск программы
Репозиторий с приложением SearchEngine находится по адресу
[https://github.com/vrpanfilov/SearchEngine.git](https://github.com/vrpanfilov/SearchEngine.git).
<p>
Для начала надо загрузить проект на локальный диск и открыт его в компиляторе Intellij IDEA
</p>
<p>
Перед первой компиляцией программы следует выполнить следующие шаги:
</p>
<li>
Установить СУБД MySQL.
</li>
<li>
В базе данных создать схему <i>search_engine</i> обязательно с кодировкой utf8md4. Имя схемы может быть и
другим, но тогда это должно быть отражено в параметре
<i>spring.datasource.url</i> в файле <i>application.yaml</i>.
</li>
<li>
В схеме нужно создать пользователя с паролем и заполнить ими параметры <i>spring.datasource.username</i> и 
<i>spring.datasource.password</i> в файле <i>application.yaml</i>.
</li>
<li>
При первом запуске установить параметр <i>jpa.hibernate.ddl-auto</i> в значение <i>create</i>.
</li>
<li>
Для подключения lucene morphology нужно создать или изменить файл settings.xml по пути 
C://Users/{Имя вашего пользователя}/.m2/repository, чтобы он выглядел так:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
https://maven.apache.org/xsd/settings-1.0.0.xsd">
    
<servers>
        <server>
            <id>skillbox-gitlab</id>
            <configuration>
                <httpHeaders>
                    <property>
                        <name>Private-Token</name>
                        <value>wtb5axJDFX9Vm_W1Lexg</value>
                    </property>
                </httpHeaders>
            </configuration>
        </server>
    </servers>
</settings>
```

<p>
При этом заранее указав в блоке <i>value</i> значение полученное по
 <a href="https://docs.google.com/document/d/1rb0ysFBLQltgLTvmh-ebaZfJSI7VwlFlEYT9V5_aPjc/edit">ссылке</a> 
</p>
</li>