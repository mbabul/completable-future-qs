package pl.britenet.qs.completablefuture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Service {

    @SneakyThrows
    String get(String id) {
        var result = "" + id;

        Thread.sleep(1000);

        log.info("get for {}", result);

        return result;
    }

    @SneakyThrows
    void run(String data) {
        Thread.sleep(1000);

        log.info("run with {}", data);
    }

    String failure() {
        throw new RuntimeException("Sth fucked up");
    }

}
