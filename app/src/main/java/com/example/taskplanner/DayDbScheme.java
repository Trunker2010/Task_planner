package com.example.taskplanner;

public class DayDbScheme {
    public static final class DayTable {
        public static final String NAME = "days";

        public static final class Cols {
            public static final String ID = "day_id";
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String HAS_TASKS = "has_task";
        }
    }

    public static final class TaskTable {
        public static final String NAME = "tasks";

        public static final class Cols {
            public static final String ID = "task_id";
            public static final String TASK = "task";
            public static final String DAY_ID = "day_id";
        }
    }

}
