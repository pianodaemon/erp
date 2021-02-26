package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.persistence.PgsqlConnPool;
import com.immortalcrab.warehouse.persistence.WarehouseInteractions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.sql.SQLException;
import java.util.logging.Level;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {

        EventBus bus = vertx.eventBus();
        bus.<JsonObject>consumer("ping-address", message -> {
            JsonObject body = message.body();

            try {
                Pair<Double, Integer> result = WarehouseInteractions.requestExistancePerPresentation(
                        body.getInteger("productId"),
                        body.getInteger("presentationId"),
                        body.getInteger("warehouseId"),
                        PgsqlConnPool.getInstance().getConnection(),
                        logger);
                JsonObject x = new JsonObject();
                x.put("existance", result.getValue0());
                x.put("digits", result.getValue1());
                message.reply(x);
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(SyncDbBridge.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SyncDbBridge.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
