# Руководство разработчика

## [Javadocs](https://gaucho-matrero.github.io/altoclef/)

## Запуск (IDE)

### Руководство по настройке от James Green!

[![Видео настройки IntelliJ от James Green на YouTube](https://img.youtube.com/vi/zZ1upxZ43Sg/0.jpg)](https://www.youtube.com/watch?v=zZ1upxZ43Sg)

### Текстовое руководство

Клонируйте проект и импортируйте. Я бы предложил использовать IntelliJ от JetBrains для импорта проекта. Убедитесь, что вы используете JDK 21 в настройках проекта и Gradle!

1) Откройте IntelliJ
2) Нажмите `File > New > Project from Version Control...` ![image](https://user-images.githubusercontent.com/13367955/146222866-42fa307b-016e-40a6-98bc-6e2428cde2dc.png)

3) Скопируйте и вставьте URL клонирования ChatClef и нажмите Clone ![image](https://user-images.githubusercontent.com/13367955/146223264-0cc436c0-4c08-4adc-b948-0ca3da4fbd6f.png)
4) Перейдите в `File > Settings`, найдите `Gradle` и убедитесь, что ваша Gradle JVM установлена на JDK версии 21 (IntelliJ позволяет загружать открытые JDK, любой из них должен подойти) ![image](https://user-images.githubusercontent.com/13367955/146223463-2cfe8671-5504-430f-93d4-bb5312b2b540.png)
5) Перейдите в `File > Project Structure`, затем в разделе `Project Settings/Project` убедитесь, что "Project SDK" установлен на версию 21 ![image](https://user-images.githubusercontent.com/13367955/146223634-dc4d9eb3-293a-4e70-b5fa-29f44145e02c.png)
6) На правой стороне экрана откройте вкладку gradle и перейдите к `Tasks/fabric/runClient`. Нажмите `runClient` ![image](https://user-images.githubusercontent.com/13367955/146223786-243c63e9-790f-48d7-b627-4e9191a84f22.png)

Если вкладка gradle не существует, попробуйте `View > Tool Windows > Gradle`

## Запуск (Командная строка)

1) Клонируйте проект через Git
2) Перейдите в клонированный локальный репозиторий командой `cd`
3) `sudo / doas chmod +x gradlew` (пропустите этот шаг, если вы на Windows)
4) `./gradlew build` или `./gradlew runClient`

## Изменение Baritone (режим разработки)

ChatClef использует кастомный форк Baritone, который дает вам больше контроля над тем, как работает Baritone.
Если вы хотите внести изменения в этот форк, вы можете сделать это локально, следуя этим шагам:

1) Клонируйте [Форк Baritone](https://github.com/gaucho-matrero/baritone) в ту же директорию, содержащую `chatclef`.
   Например, если вы клонировали `chatclef` на рабочий стол, `baritone` также должен быть на рабочем столе.
2) Запустите `gradle build` в форке, который вы только что клонировали. Вы можете открыть папку в IDE и запустить задачу `build`.
3) Теперь должны быть различные `.jar` файлы, начинающиеся с `baritone`, в следующей папке: `baritone/build/libs`
4) Теперь в `chatclef`, передайте `-Paltoclef.development` как параметр при запуске `gradle build`
   (В IntelliJ, перейдите в выпадающее меню build -> `Edit Configurations`, затем дублируйте конфигурацию `chatclef [build]`.
   В этом дубликате вставьте `-Paltoclef.development` в текстовое поле Arguments.)
5) Когда вы собираете и передаете `-Paltoclef.development`, ChatClef теперь должен использовать jar файл из
   вашего кастомного форка `baritone` вместо загрузки из интернета. Это позволяет быстро тестировать локальные изменения в Baritone.

## Руководства по разработке задач

### Обучающий стрим по программированию задач

[![Более новый VOD программирования задач ChatClef](https://img.youtube.com/vi/uROEqwyzn3o/0.jpg)](https://www.youtube.com/watch?v=uROEqwyzn3o)

### Старый (после стрима) обучающий VOD

[![Грубый обучающий VOD ChatClef](https://img.youtube.com/vi/giBjHDZ7HvY/0.jpg)](https://www.youtube.com/watch?v=giBjHDZ7HvY)