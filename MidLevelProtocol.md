# Описание протокола взаимодействия между 2-ым уровнем (ECU) и 3-им уровнем (APP)

## Модель данных передаваемых между 2ым и 3им уровнем

Перед тем как описывать протокол, для начала стоит понять какого рода данные точно будут передаваться, а так же какие мы
можем захотеть передавать в будущем.

Итак, какие данные мы будем передавать:

1. Запрос 1/2/4/8 байтных значений (скорость, заряд итд. Таких значений в будущем могут быть **сотни**)
2. Ответ на запрос с 1/2/4/8 байтными значением
3. Запрос сразу нескольких 1/2/4/8 байтных значений (для увеличения частоты обновления, уменьшения % служебный данных)
4. Ответ на запрос сразу нескольких 1/2/4/8 байтных значений
5. Запрос на установку 1/2/4/8 байтного значения (включение стоп-сигналов, света итд)
6. Подтверждение установки 1/2/4/8 байтного значения
7. Чтение и запись больших объемов данных (конфигурации ECU, контроллеров мотор колес итд)
8. Запись прошивки как самого ECU так и обновляемых контроллеров на шине CAN
9. Трансляция debug логов с ECU на третий уровень

Я не говорю, что мы обязательно поддержим все пункты из списка, но хотелось бы заложить потенциальную возможность
передачи любых из этих данных.

## Ограничения накладываемые микроконтроллером.

Да современные микроконтроллеры обладают хорошим быстродействием и значительным объемом памяти, однако хотелось бы по
возможности уменьшить накладные расходы связанные с обработкой пакетов

Что хотелось бы учесть в протоколе:

1. Избежать аллокации буферов динамического размера введя максимальный размер одного пакета
2. Избежать большого объема передаваемой информации путем введения динамического размера пакетов (не больше
   максимального)
3. По возможность минимизировать копирование участков памяти (zero copy), как решение тут не копировать payload если он
   полностью помещается в один пакет
4. Сохранить возможность передачи объема информации превышающий максимальный размер полезной нагрузки, путем возможности
   разбиения большого пакета на маленькие

## Почему нам не подходят готовые протоколы

А вот тут не знаю, я не нашел ничего, что удовлетворяло бы всем пунктам выше.

# Предлагаемая реализация протокола

Возможная структура псевдокодом (нет это не с/с++) (ниже будет описание каждого поля):

```
struct packet {
    uint8 {
        [0-3] - protocol version
        [4-7] - reserved   
    }
    uint16 functionId; // или 8 байт хватит? (нюансы ниже)
    uint8 packetId {
        [0] - initiatorSide
        [1] - needResponse
        [2-7] - id (это 64 возможных id будет)
    }
    uint8 payloadSize;
    uint8[payloadSize] payload;
    uint16 crc16;
}
```

### Итак по порядку:

1. Поле `protocolVersion` - тут думаю все понятно, если мы захотим когда-нибудь в будущем поменять версию протокола
   сохранив
   обратную совместимость, то мы сможем использовать до 16 разных версий инкриминируя это поле.
2. Поле `functionId` нужно для определения функции обработчика которой будет передан пакет. Такая функция всегда должна
   иметь сигнатуру `void handler(packet*)`, это позволит создать массив с указателями на функции обработчики где
   functionId будет являться индексом в массиве. Такая структура позволит легко добавлять новые функции обработчики, а
   массив для сопоставления functionId и указателя на функцию будет известен на этапе компиляции, а так же будет
   статическим, что позволит не тратить оперативную память на его хранение, а хранить его в коде прошивки.
3. Поле `packetId` - комплексное поле, необходимо для возможности параллельной обработки пакетов, по этому полю можно
   определить на какой запрос пришел ответ.
    1. `initiatorSide` - `false` для ECU, `true` для APP. Это поле нужно, что бы не перепутать запрос и ответ: запрос с
       id=1 со стороны ECU может быть воспринят как ответ на ранее отправленный запрос от APP с тем же id=1
    2. `needResponse` - ожидаем ли мы получить ответ на данный пакет (это будет определять некоторые нюансы обработки, о
       которых ниже)
    3. `id` - инкрементальное поле, при условии что выставлен флаг `needResoinse` каждый следующий отправленный пакет
       инкриминирует это значение, предварительно
       проверив, что в таблице сопоставления id пакета и функции обработчика (о таблице ниже) этот id не занят. Если id
       занят проверяет таймауты и либо пробует следующий, либо заменяет обработчик (предварительно корректно очистив
       данные) если таймауты истекли. Если `needResponse` не выставлен, то это поле может быть любым и будет
       проигнорировано
4. `payloadSize` - размер полезной нагрузки, может быть 0.
5. `crc16` - контрольная сумма.

### Описание нюансов:

1. Максимальный размер пакета задаем как N (известное на момент компиляции число, которое впрочем сможем поменять если
   захотим, естественно до выхода в прод, потом уже поздно будет). Это позволит заранее знать размер буфера в который
   точно влезет пакет. Начать предлагаю с 32 байт.
2. Максимальный размер полезной нагрузки соответственно N - размер служебных данных.
3. Структура packet легко может быть отображена на прямую в память. И все поля кроме crc16 будут иметь четкую позицию в
   памяти.
4. Прямой доступностью поля я пожертвовал crc16 сознательно. Расположение контрольной суммы в конце пакета позволяет
   передавать пакет одновременно с вычислением контрольной суммы, что позволяет экономить вычислительные ресурсы.
5. Память выделенную под packet можно переиспользовать. Считаем что после возврата из функции handler packet можно
   переиспользовать повторно. Это означает, что handler не должен сохранять packet, если какие-либо данные нужно
   сохранить, то их нужно будет скопировать в другой регион памяти.
6. Для передачи данных объемом больше чем размер payload будет использоваться специальная функция для склейки пакетов (
   о ней подробнее ниже)
7. Я не предлагаю делать отдельный обработчик на каждый простой параметр по типу скорость/заряд/итд. С обработкой
   всех этих параметров вполне справится один обработчик. Если делать именно один обработчик на пачку значений, то нам
   хватит 256 возможных обработчиков и можно будет юзать 1 байт на functionID

### Таблица сопоставления packetId и функции обработчика

1. представляет из себя кусок памяти последовательно содержащий структуры:

```
struct {
    uint32 experationTime;
    uint32 handlerRefetence;
    uint32 dataReference;
}
```

2. При каждой отправке запроса, при условии, что выставлен флаг `needResponse`. Мы должны передать указатель на функцию
   обработчик ответа, таймаут ожидания ответа, указатель на доп данные (о них ниже).
3. Функция обработчик должна иметь сигнатуру `int handler(packet*, void* dataReference)`. Возвращает функция новый
   таймаут, или 0 если больше пакетов не будет.
4. `dataReference` - поле указатель на произвольные данные. Поле опциональное, если не нужны ставим в 0. Тут можно
   хранить состояние функции обработчика.
5. Если произошел таймаут нужно вызвать функцию `handler` передав ей 0 указатель на пакет. Это означает что функция
   обработчик должна деалоцировать `dataReference` во избежание утечки памяти.
6. Для ресурсов микроконтроллера может быть накладно держать такой массив сразу для всех возможных id, поэтому
   микроконтроллер может дополнительно ограничить размер массива.

### Обработка ошибок и защита от рассинхронизации буферов о которой я спрашивал на собрании

1. В случае если на шине нет новых данных в течении N ms, сбрасываем текущий буфер
2. В случае невалидной crc отправляем запрос со специальным functionID (обработчик ошибки). Вторая сторона после
   получения запроса на обработчик ошибки должна прекратить передачу не менее чем на N ms (это позволит гарантировать
   сброс буфера), а так же очистить таблицу сопоставления packetId.

### Функция для склейки пакетов

1. функций которым нужно за раз принять/передать много данных не так много. Но для реализации таких мы можем
   использовать флаг `needResponse`. Запрашиваем функцию с этим флагом, она отвечает нам таким же флагом. Таким образом
   получаем двухсторонний канал по которому можем передавать любые объемы данных 