# java-kanban

## Описание

**Java-kanban (Трекер задач) - программа, отвечающая за формирование модели данных
для совместной работы над задачами**

## Функциональность

### Программа позволяет

*1* - разбивать планируемые дела на 3 типа :

- Задачи (ru.practicum.kanban.model.Task)
- Эпики  (ru.practicum.kanban.model.Epic)
- Подзадачи (входящие в свой конкретный эпик) (ru.practicum.kanban.model.SubTask)

*2* - в зависимости от выполнения задач, присваивать каждому из типов один из трёх статусов :

- **NEW**
- **IN_PROGRESS**
- **DONE**

*3* - между ru.practicum.kanban.model.Epic и ru.practicum.kanban.model.SubTask существует связь как один-ко-многим,
статус ru.practicum.kanban.model.Epic меняется в зависимости от статуса
входящих в него подзадач. ru.practicum.kanban.model.Epic и ru.practicum.kanban.model.Task могут существовать как
самостоятельные единицы.

*4* - реализована возможность:

- Создать задачу
- Получить задачу
- Получить список задач
- Обновить задачу
- Удалить список задач

