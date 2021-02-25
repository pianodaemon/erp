package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.persistence.PgsqlConnPool;
import com.immortalcrab.warehouse.persistence.WarehouseInteractions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import java.sql.SQLException;
import java.util.logging.Level;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDbBridge extends AbstractVerticle {

    @Override
    public void start() {

        EventBus bus = vertx.eventBus();
        bus.consumer("ping-address", message -> {

            System.out.println("Received message: " + message.body());
            try {
                Pair<Double, Integer> result = WarehouseInteractions.requestExistancePerPresentation(3162, 10, 1, PgsqlConnPool.getInstance().getConnection(), logger);
                message.reply("pong " + result.getValue0() );
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(SyncDbBridge.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SyncDbBridge.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        });

    }

    private final Logger logger = LoggerFactory.getLogger(SyncDbBridge.class);
}
