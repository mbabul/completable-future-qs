package pl.britenet.qs.completablefuture;

import lombok.SneakyThrows;

import java.util.concurrent.Executors;

public class Application {

    @SneakyThrows
    public static void main(String ...args) {
        var service = new Service();
        var executor = Executors.newFixedThreadPool(3, factory -> {
            var thread = new Thread(factory);
            thread.setDaemon(false); //a tak możemy ustawić wątki z exekutora jako daemony bądz nie
            return thread;
        });
        var sync = new Synchronous(service);
        var async = new Asynchronous(service, executor);

        sync.runCombined();
        sync.runCombined2();
        sync.runSequentially();

        async.runCombined();
        async.runCombined2();
        async.runCombined3();
        async.runSequentially();



        //jeżeli używasz ForkJoinPool, to tam sa wątki typu daemon, jak sie skonczy wątek główny to zostaną usunięte,
        //z Executors wątki nie są daemonami, wiec głowny wątek nie zostanie zakończony, gdyż pola żyje i ma się dobrze :)
//        Thread.sleep(3000);
    }
}
