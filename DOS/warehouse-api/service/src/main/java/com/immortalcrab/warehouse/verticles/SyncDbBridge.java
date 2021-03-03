package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.applications.Transfers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {

        EventBus bus = vertx.eventBus();
        bus.<JsonObject>consumer(Transfers.EXISTANCE_PER_PRESENTATION, message -> {
            Transfers.actOnExistancePerPresentation(message, this.logger);
        });

        bus.<JsonObject>consumer(Transfers.WAREHOUSES, message -> {
            Transfers.actOnWarehouses(message, this.logger);
        });
    }

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
