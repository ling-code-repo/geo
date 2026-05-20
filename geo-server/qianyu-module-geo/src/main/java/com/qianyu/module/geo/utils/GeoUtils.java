package com.qianyu.module.geo.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Component
@Slf4j
public class GeoUtils {

    public static final String COMMA = ",";


    public static final RandomGenerator GENERATOR = RandomGenerator.getDefault();


    public static GeoUtils instance;

    public static volatile boolean RUN_FLAG = true;

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    //
    public static ThreadPoolExecutor getPool(String nameFormat, int core) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(core
                , core
                , 0L
                , TimeUnit.SECONDS
                , new LinkedBlockingQueue<Runnable>(AVAILABLE_PROCESSORS*8)
                , new ThreadFactoryBuilder().setNameFormat(nameFormat).build()
                , new ThreadPoolExecutor.CallerRunsPolicy());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                if (!threadPoolExecutor.isShutdown()) {
                    threadPoolExecutor.shutdownNow();
                }
            }catch (Exception e){

            }
        }));

        return threadPoolExecutor;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    @PreDestroy
    public void destroy(){
        RUN_FLAG = false;
    }


    public static long currentTimeNanos() {
        Instant now = Instant.now();
        long epochNanos = now.getEpochSecond() * 1_000_000_000L + now.getNano();
        return epochNanos;
    }


    public static LocalDateTime ofMilli(long epochNanos){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochNanos), ZoneId.systemDefault());
    }


    private static GeoUtils obj() {
        return instance;
    }

}
