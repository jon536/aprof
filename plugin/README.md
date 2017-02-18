для сборки maven нужно прописать -Didea.home=путь до расположения идеи,  
например: mvn -Dmaven.test.skip=true -Didea.home=/home/eugene/java/idea -Didea.version=2016.1 package

для установки плагина с диска появится ./plugin/target/plugin-31-SNAPSHOT-release.zip

для профилирования текущего проекта должен быть файл `aprof.config`  
в котором прописывается порт на котором Aprof будет слушать  
пример файла aprof.config:  
port=7777

после того как создали файл запускаем проект с помощью новой кнопки (зеленая с красной буквой A)  
Текущей проект будет запущен с опцией -javaagent:aprof.jar

для визуализации нажимаем `Tools` в меню IDEA далее `Aprof: Connect to host`  
вводим адресс вида `host:port`, port который прописали в aprof.config  
например: localhost:7777

у нас есть три кнопки `Run/Stop`, `Refresh`, `Toggle Mode`,  
Run/Stop - запускает/останавливает автоматическое обновление данных  
Refresh - ручное обновление  
Toggle Mode - переключается между режимами визуализации  
(текущей режим прописывается в названии первой колонки: Classes (From class to allocated location))

Двойное нажатие по методу/класса - переход к методу/классу  
доступен поиск по дереву  
раскрывать вершины можно по нажатию Enter  

screencast: https://youtu.be/qmuibo3ShKQ
