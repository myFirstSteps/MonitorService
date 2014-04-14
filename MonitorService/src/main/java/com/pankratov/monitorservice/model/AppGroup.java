/*Группа записей состояний инстансов (SingleApp) одного типа приложения. 
 Объект представляет собой карту, хранящую записи всех инстансов одного типа приложений.
 Ключом карты является UUID. 
 */
package com.pankratov.monitorservice.model;

import java.util.concurrent.*;
import java.util.*;

public class AppGroup {

    final private String appName;
    final private ConcurrentHashMap<String, SingleApp> instances = new ConcurrentHashMap<>(135);

    public AppGroup(String appName) {
        this.appName = appName;
    }

    public void doRegister(SingleApp app) {
        instances.put(app.getUuid(), app);
    }

    public Collection<SingleApp> getGroup() {
        return instances.values();
    }
}
