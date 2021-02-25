package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.persistence.PgsqlConnPool;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {

        EventBus bus = vertx.eventBus();
        bus.consumer("ping-address", message -> {

            System.out.println("Received message: " + message.body());
            // Now send back reply
            message.reply("pong!");
        });

        vertx.setPeriodic(10000, id -> {
            try {

                logger.info("Zzz...");

                Thread.sleep(8000);
                logger.info("Up! #{}", PgsqlConnPool.getInstance().up());
            } catch (InterruptedException e) {
                logger.error("Woops", e);
            }
        });
    }

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
