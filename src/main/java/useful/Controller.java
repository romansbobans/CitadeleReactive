package useful;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import utilities.Threader;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Controller {

    public static void main(String[] args) {
        Single<String> callOtherServiceOrDefault = networkRequest()
                .ambWith(Single.timer(2000, TimeUnit.MILLISECONDS, local).map(i -> "Returned local response"));

        Observable.range(0, 10)
                .flatMapSingle(i -> callOtherServiceOrDefault).blockingSubscribe(System.out::println);

    }

    static Scheduler network = Threader.scheduler("Generic network");
    static Scheduler local = Threader.scheduler("Generic local I/O");

    private static Single<String> networkRequest() {
        return Single.fromCallable(() -> new Random().nextInt(9) * 500 + 500)
                .flatMap(delay -> Single.timer(delay, TimeUnit.MILLISECONDS, network).map(e -> {
                    if (new Random().nextInt(9) < 4) {
                        throw new IllegalArgumentException("Bad request");
                    }
                    return "Received 200 OK";
                })).onErrorReturn(e -> "Unknown response");
    }
}
