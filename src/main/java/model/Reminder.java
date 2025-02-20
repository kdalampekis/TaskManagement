package model;

import java.time.LocalDate;

public class Reminder {
    private Task task;
    private ReminderType type;
    private LocalDate specificDate; // Only used for specific date reminders

    public Reminder(Task task, ReminderType type, LocalDate specificDate) {
        this.task = task;
        this.type = type;
        this.specificDate = specificDate;
    }

    public Task getTask() { return task; }
    public ReminderType getType() { return type; }
    public LocalDate getSpecificDate() { return specificDate; }
    public void setType(ReminderType type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "Reminder for Task: " + task.getTitle() + " - " + type;
    }
}
