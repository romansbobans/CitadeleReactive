package burgers;

import burgers.dto.*;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import javafx.collections.ObservableList;
import utilities.Threader;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SupplyLine {

    static Observable<Lamb> farm1 =
            Observable.interval(1, TimeUnit.SECONDS, Threader.scheduler("Lamb Farm #1"))
                    .map(e -> new Lamb("Farm #1"));
    static Observable<Lamb> farm2 =
            Observable.interval(1500, TimeUnit.MILLISECONDS, Threader.scheduler("Lamb Farm #2"))
                    .map(e -> new Lamb("Farm #2"));

    static Observable<List<Patty>> cropField =
            Observable.interval(100, TimeUnit.MILLISECONDS, Threader.scheduler("Patty Farm"))
            .map(e -> new Patty()).buffer(13);

    static Observable<List<Tomato>> tomatoGreenHouse =
            Observable.interval(140, TimeUnit.MILLISECONDS, Threader.scheduler("Greenhouse"))
                    .map(e -> new Tomato()).buffer(8);

    static final MeatPlace meatPlace = new MeatPlace();
    static final KotletesPlace kotletesPlace = new KotletesPlace();
    static final McDonalds mcDonalds = new McDonalds();

    public static void main(String[] args) throws InterruptedException {

        Observable<List<Lamb>> farmResult = farm1.mergeWith(farm2).buffer(2);
/*
        long startTime = System.currentTimeMillis();

        farmResult.subscribe(moos -> System.out.println(String.format("Received %s Mooos after %s", moos.size(),
                System.currentTimeMillis() - startTime)));*/


        Observable<List<Kotlete>> kotleteSupply =
                farmResult.map(meatPlace::process)
                        .flatMapSingle(kotletesPlace::process);

        Observable.zip(kotleteSupply, cropField, tomatoGreenHouse, mcDonalds::process)
                .observeOn(Threader.scheduler("Cashier"))
                .subscribe(e -> System.out.println(String.format("Received burger on %s", Threader.threadName())));
        Thread.sleep(100000);
    }


    static class MeatPlace {
        List<RawMeat> process(List<Lamb> lamb) {
            System.out.println(String.format("Received meat on %s from %s", Threader.threadName(), lamb.stream().map(l -> l.farm).collect(Collectors.toList())));
            return IntStream.of(10).mapToObj(i -> new RawMeat()).collect(Collectors.toList());
        }
    }

    static class KotletesPlace {

        private Scheduler kotletesPlaceScheduler = Threader.scheduler("Kotletes place");

        Single<List<Kotlete>> process(List<RawMeat> meatMMM) {
            return Single.just(meatMMM)
                    .zipWith(Single.timer(new Random().nextInt(4), TimeUnit.SECONDS, kotletesPlaceScheduler),
                            (kotlete, time) -> generateKotletes())
                    .subscribeOn(kotletesPlaceScheduler);

        }

        private List<Kotlete> generateKotletes() {
            System.out.println(String.format("Starting to cook Kotletes on %s", Threader.threadName()));
            return IntStream.of(new Random().nextInt(10)).mapToObj(i -> new Kotlete()).collect(Collectors.toList());
        }
    }


    static class McDonalds {
        Observable<Burger> process(List<Kotlete> kotletes, List<Patty> maizites, List<Tomato> tomatoes) {
            System.out.println(String.format("Assembling burgers on %s", Threader.threadName()));
            return Observable.fromCallable(() -> IntStream.of(100).mapToObj(i -> new Burger()).collect(Collectors.toList())).flatMap(Observable::fromIterable);
        }
    }

    static class TooFewKotletesException extends RuntimeException {
        @Override
        public String getMessage() {
            return "There were too few kotletes to bother";
        }
    }


}
