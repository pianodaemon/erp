package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.persistence.WareHouseMgmt;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {
        vertx.setPeriodic(10000, id -> {
            try {

                logger.info("Zzz...");

                Thread.sleep(8000);
                logger.info("Up! #{}", WareHouseMgmt.getInstance().up());
            } catch (InterruptedException e) {
                logger.error("Woops", e);
            }
        });
    }

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
