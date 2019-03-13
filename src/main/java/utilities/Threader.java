package utilities;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class Threader {

    public static Scheduler scheduler(String name) {
        return Schedulers.from(spawnNewThread(name));
    }

    public static Executor spawnNewThread(String name) {
        return command -> new Thread(command, name).start();
    }
    public static String threadName() {
        return Thread.currentThread().getName();
    }
}
