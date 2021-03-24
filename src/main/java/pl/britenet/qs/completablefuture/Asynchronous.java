package pl.britenet.qs.completablefuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public class Asynchronous {

    private final Service service;
    private final ExecutorService executor;

    void runCombined() {
        long start = System.currentTimeMillis();

        List<CompletableFuture<String>> futures = List.of(get("1"), get("2"), get("3"));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(result -> futures.stream()
                    .map(CompletableFuture::join)
                    .reduce("", (curr, next) -> curr + next))
                .thenAccept(service::run)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("error: {}", ex.getMessage());
                    } else {
                        log.info("asynchronous combined finished in: {}", System.currentTimeMillis() - start);
                    }
                });
    }

    void runCombined2() {
        long start = System.currentTimeMillis();

        get("1").thenCompose(get1 -> get(get1 + 2))
                .thenCompose(get12 -> get(get12 + 3))
                .thenAccept(this::run)
//                .thenAccept(get123 -> service.run(get123))
                .thenRun(() -> log.info("asynchronous combined3 finished in: {}", System.currentTimeMillis() - start))
                .exceptionally(ex -> {
                    log.error("error: {}", ex.getMessage());
                    return null;
                });

//        CompletableFuture.supplyAsync(() -> service.get("1"), executor)
//                .thenComposeAsync(get1 -> CompletableFuture.supplyAsync(() -> service.get(get1 + 2), executor), executor)
//                .thenComposeAsync(get12 -> CompletableFuture.supplyAsync(() -> service.get(get12 +3), executor), executor)
//                .thenAcceptAsync(get13 -> service.run(get13), executor)
//                .thenRun(() -> log.info("asynchronous combined3 finished in: {}", System.currentTimeMillis() - start))
//                .exceptionally(ex -> {
//                    log.error("error: {}", ex.getMessage());
//                    return null;
//                });

    }

    void runCombined3() {
        long start = System.currentTimeMillis();

        get("1").thenCombine(get("2"), (get1, get2) -> get1 + get2)
                .thenCombine(get("3"), (get12, get3) -> get12 + get3)
                .thenAccept(service::run)
                .thenRun(() -> log.info("asynchronous combined2 finished in: {}", System.currentTimeMillis() - start));
    }


    void runSequentially() {
        long start = System.currentTimeMillis();

        var future1 = get("1").thenAccept(this::run); // ta akcja może wykonac się po thenRun, gdyż zostanie oddelegowwana do innego wątku
        var future2 = get("2").thenAccept(service::run); //ta akcja wykona sie zawsze prze thenRun, bo bedziemy na nią czekać
        var future3 = get("3").thenAccept(service::run);

        CompletableFuture.allOf(future1, future2, future3)
                .thenRun(() -> log.info("asynchronous combined3 finished in: {}", System.currentTimeMillis() - start));
    }

    private CompletableFuture<String> get(String id) {
        return CompletableFuture.supplyAsync(() -> service.get(id), executor);
    }

    //akcje na CF wykona sie na innym wątku, gdyż używamy executor
    private CompletableFuture<Void> run(String data) {
        return CompletableFuture.runAsync(() -> service.run(data), executor);
    }

}
