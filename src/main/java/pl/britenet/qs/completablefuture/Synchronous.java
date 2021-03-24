package pl.britenet.qs.completablefuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
class Synchronous {

    private final Service service;

    void runCombined() {
        long start = System.currentTimeMillis();

        var data = Stream.of(service.get("1"), service.get("2"), service.get("3"))
                .reduce("", (curr, next) -> curr + next);

        service.run(data);

        long end = System.currentTimeMillis();

        log.info("Synchronous combined finished in: {}", end - start);
    }

    void runCombined2() {
        long start = System.currentTimeMillis();

        service.run(service.get(service.get(service.get("1") + 2) + 3));

        long end = System.currentTimeMillis();

        log.info("Synchronous combined2 finished in: {}", end - start);
    }

    void runSequentially() {
        long start = System.currentTimeMillis();

        service.run(service.get("1"));
        service.run(service.get("2"));
        service.run(service.get("3"));

        long end = System.currentTimeMillis();

        log.info("Synchronous sequentially finished in: {}", end - start);
    }

}
