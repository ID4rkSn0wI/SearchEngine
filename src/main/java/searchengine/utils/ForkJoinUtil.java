package searchengine.utils;

import org.springframework.stereotype.Service;

import java.util.concurrent.ForkJoinPool;

@Service
public class ForkJoinUtil {
    public static ForkJoinPool forkJoinPool;

    static {
        forkJoinPool = new ForkJoinPool();
    }

    public static void refreshForkJoinPool(){
        forkJoinPool = new ForkJoinPool();
    }
}
