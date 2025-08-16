# Миграция с Player2APIService на OpenAI/LiteLLM API

## 📋 Обзор изменений

Проект был успешно мигрирован с кастомного Player2APIService на OpenAI-совместимый LiteLLM Proxy API.

### 🔧 Конфигурация нового API

- **Base URL**: `http://193.233.114.29:4000/v1`
- **API Key**: `sk-OkYhcpYUhwINidb2I_9yaA`
- **Модель**: `gpt-oss-120b`
- **TTS Модель**: `tts-1`
- **STT Модель**: `whisper-1`

## 🔄 Маппинг эндпоинтов

| **Старый Player2API** | **Новый OpenAI API** | **Статус** |
|----------------------|---------------------|------------|
| `/v1/chat/completions` | `/chat/completions` | ✅ Мигрирован |
| `/v1/tts/speak` | `/audio/speech` | ✅ Мигрирован |
| `/v1/stt/start` + `/v1/stt/stop` | `/audio/transcriptions` | ✅ Переработан |
| `/v1/selected_characters` | Локальная логика | ✅ Удален |
| `/v1/health` | Не требуется | ✅ Удален |

## 📁 Структура новых файлов

### Новые файлы:
- `OpenAIService.java` - Основной сервис для работы с OpenAI API
- `OpenAISTTService.java` - Расширенный сервис для Speech-to-Text
- `MIGRATION_TO_OPENAI.md` - Данная документация

### Измененные файлы:
- `AICommandBridge.java` - Обновлен для использования OpenAI API
- `ConversationHistory.java` - Обновлен вызов API
- `Character.java` - Добавлены методы для работы с OpenAI голосами

### Архивированные файлы:
- `Player2APIService_OLD.java` - Старый сервис (сохранен для истории)

## 🚀 Новые возможности

### 1. **Улучшенная обработка чата**
```java
// Новый формат запроса с параметрами OpenAI
JsonObject requestBody = new JsonObject();
requestBody.add("messages", messagesArray);
requestBody.addProperty("model", "gpt-oss-120b");
requestBody.addProperty("max_tokens", 1000);
requestBody.addProperty("temperature", 0.7);
```

### 2. **OpenAI-совместимый TTS**
```java
// Поддержка стандартных OpenAI голосов
JsonObject requestBody = new JsonObject();
requestBody.addProperty("model", "tts-1");
requestBody.addProperty("input", message);
requestBody.addProperty("voice", "alloy"); // alloy, echo, fable, onyx, nova, shimmer
requestBody.addProperty("response_format", "mp3");
```

### 3. **Файловый STT**
```java
// Новая архитектура с записью в файл и отправкой на сервер
OpenAISTTService.startSTT(); // Начинает запись в файл
String result = OpenAISTTService.stopSTT(); // Останавливает и отправляет на транскрипцию
```

## 🎯 Ключевые преимущества

### ✅ **Стандартизация**
- Полная совместимость с OpenAI API
- Стандартные форматы запросов и ответов
- Поддержка всех OpenAI параметров

### ✅ **Упрощение**
- Убраны кастомные эндпоинты
- Стандартная аутентификация через Bearer token
- Нет необходимости в heartbeat

### ✅ **Расширяемость**
- Легко добавить новые OpenAI функции
- Совместимость с любыми OpenAI-совместимыми провайдерами
- Простая смена моделей

## ⚠️ **Важные изменения**

### 1. **STT архитектура**
- **Было**: Start/Stop streaming
- **Стало**: Запись в файл + отправка файла

### 2. **Персонажи**
- **Было**: Загрузка с сервера
- **Стало**: Локальное управление через `getDefaultCharacter()`

### 3. **Голоса TTS**
- **Было**: Кастомные voice_ids
- **Стало**: Стандартные OpenAI голоса (alloy, echo, fable, onyx, nova, shimmer)

## 🔧 **Настройка**

### Изменение API параметров:
```java
// В OpenAIService.java
private static final String BASE_URL = "http://193.233.114.29:4000/v1";
private static final String API_KEY = "sk-OkYhcpYUhwINidb2I_9yaA";
private static final String MODEL = "gpt-oss-120b";
```

### Настройка персонажа:
```java
// В OpenAIService.getDefaultCharacter()
return new Character(
    "Ваше имя персонажа", 
    "Короткое имя", 
    "Текст приветствия", 
    "Описание персонажа",
    new String[]{"alloy"} // Голос OpenAI
);
```

## 🧪 **Тестирование**

Для проверки работоспособности:

1. **Chat API**: Отправьте сообщение боту
2. **TTS**: Проверьте озвучивание ответов
3. **STT**: Попробуйте голосовые команды
4. **Персонаж**: Убедитесь в корректной загрузке дефолтного персонажа

## 📞 **Поддержка**

При возникновении проблем проверьте:
- Доступность LiteLLM Proxy по адресу `http://193.233.114.29:4000/`
- Корректность API ключа
- Поддержку нужных эндпоинтов в LiteLLM

---

**Миграция завершена успешно!** 🎉

Все основные функции сохранены и адаптированы под новый API.
