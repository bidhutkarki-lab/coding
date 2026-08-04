import java.util.*;

/*
Design and Implement task scheduler that supports
- Scheduling a task to run at a given timestamp
- Cancelling a scheduled task
- Executing all tasks due at or before the current time
*/

public class TaskScheduler {

    private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>((a, b) -> {

        int timestampComparision = Long.compare(a.executionTimestamp, b.executionTimestamp);

        if(timestampComparision != 0) {
            return timestampComparision;
        }

        return Long.compare(a.sequenceNumber, b.sequenceNumber);
    });

    private final Map<String, ScheduledTask> scheduledTask = new HashMap<>();
    private long nextSequenceNumber;

    public void schedule(
        String taskId,
        long executionTimestamp,
        Runnable action
    ) {
        if(taskId == null || action == null) {
            throw new IllegalArgumentException("Task id and action cannot be null");
        }

        if(scheduledTask.containsKey(taskId)) {
            throw new IllegalArgumentException("Task already exisit");
        }

        ScheduledTask task = new ScheduledTask(
            taskId,
            executionTimestamp,
            nextSequenceNumber++,
            action
        );

        scheduledTask.put(taskId, task);
        queue.offer(task);
    }

    public boolean cancel(String taskId) {
        return scheduledTask.remove(taskId) != null;
    }

    public void executeDueTasks(long currentTimestamp) {
        while(!queue.isEmpty() && queue.peek().executionTimestamp <= currentTimestamp) {
            ScheduledTask task = queue.poll();

            // The task may have been cancelled
            if(scheduledTask.get(task.taskId) != task) {
                continue;
            }

            scheduledTask.remove(task.taskId);

            try {
                task.action.run();
            } catch(RuntimeException e) {
                handleFailure(task, e);
            }

        }
    }

    private void handleFailure(
        ScheduledTask task,
    RuntimeException e) {
        System.err.println("Task failed: " + task.taskId + ", error: " + e.getMessage());
    }


    private static class ScheduledTask {

        private final String taskId;
        private final long executionTimestamp;
        private final long sequenceNumber;
        private final Runnable action;

        private ScheduledTask(
            String taskId,
            long executionTimestamp,
            long sequenceNumber,
            Runnable action
        ) {
            this.taskId = taskId;
            this.executionTimestamp = executionTimestamp;
            this.sequenceNumber = sequenceNumber;
            this.action = action;
        }
    }

    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();

        scheduler.schedule(
                "task-1",
                1_000,
                () -> System.out.println("Task 1 executed")
        );

        scheduler.schedule(
                "task-2",
                2_000,
                () -> System.out.println("Task 2 executed")
        );

        scheduler.schedule(
                "task-3",
                1_500,
                () -> System.out.println("Task 3 executed")
        );

        scheduler.cancel("task-3");

        scheduler.executeDueTasks(1_200); // Executes task-1
        scheduler.executeDueTasks(3_000); // Executes task-2
    }
}
