import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import utilities.Threader;

public class Ex1 {

    private static Observable<Integer> observable = Observable.just(1, 2, 3);

    public static void main(String[] args) throws InterruptedException {

        observable.subscribeOn(Schedulers.computation())
                .doOnNext(e -> System.out.println(
                        String.format("Emiting %s on %s", e, Threader.threadName())))
                .observeOn(Schedulers.computation())
                .blockingSubscribe(e ->
                        System.out.println(
                                String.format("Receiving %s on %s", e, Threader.threadName())),
                        e -> { },
                        () -> System.out.println(String.format("Completed on %s", Threader.threadName())));

    }
}
