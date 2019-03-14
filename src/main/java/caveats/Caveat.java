package caveats;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import utilities.Threader;

public class Caveat {

    private static Observable<Integer> observable = Observable.just(1, 2, 3);

    public static void main(String[] args) throws InterruptedException {

        observable.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMapCompletable(e -> {
                    System.out.println(String.format("Received event %s", e));
                    return Completable.complete();
                })
                .subscribe(() -> System.out.println(String.format("Completed on %s", Threader.threadName())));
        Thread.sleep(100);

    }
}
