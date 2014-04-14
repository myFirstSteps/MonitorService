/*Минимальная единица, представляющая собой запись состояния одного инстанса приложения. */
package com.pankratov.monitorservice.model;

import java.util.*;

public class SingleApp {

    private String name; //имя приложения
    private String uuid; // UUID инстанса
    private String status; // статус (UP, STARTING, PROCESSING)
    private int cpu; // загрузка CPU
    private int handlingTime; // время обработки одного запроса приложением (мс)
    private int processed; // общее количество обработанных запросов
    private long regTime = System.currentTimeMillis(); // дата регистрации

    /*Конструктор  разбирает POST запрос на параметры, из которых формирует объект. Если одного из параметров нет в запросе или 
     для него задано некоректное значение, считаем, что регистрация состояния приложения не удалась и выбрасываем исключение  "BadParamException". */
    public SingleApp(javax.servlet.ServletRequest request) throws BadParamException {
        name = request.getParameter("appName");
        uuid = request.getParameter("uuid");
        status = request.getParameter("status"); /*Из т.з. не ясно может ли параметр статус иметь множественные значения
         (одновременно UP и Processing). Исхожу из того, что не может.*/

        try {
            if (name.isEmpty() || status.isEmpty()) {
                throw new BadParamException("Параметы name и value не должны быть пустыми");
            }
            if (uuid.isEmpty()) {
                throw new BadParamException("Invalid UUID");
            }
            cpu = Integer.parseInt(request.getParameter("cpu"));
            handlingTime = Integer.parseInt(request.getParameter("handlingTime"));
            processed = Integer.parseInt(request.getParameter("processed"));
        } catch (NumberFormatException | NullPointerException e) {
            throw new BadParamException("Illegal request parameter", e);
        }
    }

    @Override
    public int hashCode() {
        int result = 5;
        result = 17 * result + name.hashCode();
        result = 17 * result + getUuid().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof SingleApp)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        SingleApp obj = (SingleApp) o;
        return name.equals(obj.getName()) & getUuid().equals(obj.getUuid());
    }

    @Override
    public String toString() {
        return name + ", " + uuid + ", " + status + ", " + cpu + "%, " + handlingTime + "ms, " + processed + ", " + new Date(regTime);
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStatus() {
        return status;
    }

    public int getCpu() {
        return cpu;
    }

    public int getHandlingTime() {
        return handlingTime;
    }

    public int getProcessed() {
        return processed;
    }

    public long getRegTime() {
        return regTime;
    }
}
