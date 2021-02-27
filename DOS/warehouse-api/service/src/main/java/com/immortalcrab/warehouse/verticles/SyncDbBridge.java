package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.persistence.PgsqlConnPool;
import com.immortalcrab.warehouse.persistence.WarehouseInteractions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {

        EventBus bus = vertx.eventBus();
        bus.<JsonObject>consumer(EXISTANCE_PER_PRESENTATION, message -> {
            JsonObject body = message.body();

            try {
                Pair<Double, Integer> answer = WarehouseInteractions.requestExistancePerPresentation(
                        body.getInteger("productId"),
                        body.getInteger("presentationId"),
                        body.getInteger("warehouseId"),
                        PgsqlConnPool.getInstance().getConnection(),
                        logger);

                //Shapping the json object reply (AKA the jor)
                {
                    JsonObject jor = new JsonObject();
                    jor.put("existance", answer.getValue0());
                    jor.put("digits", answer.getValue1());
                    message.reply(jor);
                }

            } catch (SQLException | NoSuchElementException ex) {
                this.logger.error(ex.getMessage());
                if (ex instanceof SQLException) {
                    message.fail(502, ex.getMessage());
                }
                if (ex instanceof NoSuchElementException) {
                    message.fail(404, ex.getMessage());
                }
            }
        });
    }

    public static String EXISTANCE_PER_PRESENTATION = String.format("%s.%s", SyncDbBridge.class.getSimpleName().toLowerCase(), "existance-per-presentation");

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
