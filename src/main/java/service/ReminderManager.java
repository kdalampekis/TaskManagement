package service;

import model.Reminder;
import model.Task;
import model.TaskStatus;
import model.ReminderType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderManager {
    private List<Reminder> reminders;

    public ReminderManager() {
        this.reminders = new ArrayList<>();
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = new ArrayList<>(reminders);
    }

    public void addReminder(Task task, ReminderType type, LocalDate specificDate) {
        if (task.getStatus() == TaskStatus.COMPLETED) {
            System.out.println("Cannot add a reminder to a completed task.");
            return;
        }
        reminders.add(new Reminder(task, type, specificDate));
    }

    public void editReminder(Reminder oldReminder, Reminder newReminder) {
        int index = reminders.indexOf(oldReminder);
        if (index != -1) {
            reminders.set(index, newReminder);
        }
    }
    public void deleteReminder(Reminder reminder) {
        reminders.remove(reminder);
    }

    public List<Reminder> getRemindersForTask(Task task) {
        List<Reminder> taskReminders = new ArrayList<>();
        for (Reminder reminder : reminders) {
            if (reminder.getTask().equals(task)) {
                taskReminders.add(reminder);
            }
        }
        return taskReminders;
    }

    public List<Reminder> getAllReminders() {
        return reminders;
    }
}
