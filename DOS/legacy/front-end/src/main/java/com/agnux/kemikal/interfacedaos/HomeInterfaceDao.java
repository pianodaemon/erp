package com.agnux.kemikal.interfacedaos;

import java.util.HashMap;

public interface HomeInterfaceDao {
    public HashMap<String, String> getUserByName(String name);
    public HashMap<String, String> getUserById(Integer id_user);
}
