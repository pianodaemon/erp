package com.immortalcrab.warehouse.persistence;

public class WareHouseMgmt {

    int counter;

    private WareHouseMgmt() {
        this.counter = 0;
    }

    public int up() {
        return ++this.counter;
    }

    public static WareHouseMgmt getInstance() {
        return WareHouseMgmtHolder.INSTANCE;
    }

    private static class WareHouseMgmtHolder {

        private static final WareHouseMgmt INSTANCE = new WareHouseMgmt();
    }
}
