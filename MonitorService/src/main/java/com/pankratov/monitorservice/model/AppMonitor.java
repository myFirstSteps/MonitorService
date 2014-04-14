package com.pankratov.monitorservice.model;

import java.util.concurrent.*;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.http.*;

/* Данный класс содержит в своей хеш-карте объекты AppGroup, представляющие собой 
 сгруппированные записи состояний инстансов определенного типа приложения. Ключем 
 карты является имя приложения. 
 */
public class AppMonitor {

    static private ConcurrentHashMap<String, AppGroup> apps = new ConcurrentHashMap<>(135);

    /*Регистрация статуса приложения*/
    public static void doRegistration(SingleApp app) {
        if (app == null) {
            return;
        }
        apps.putIfAbsent(app.getName(), new AppGroup(app.getName()));
        apps.get(app.getName()).doRegister(app);
    }

    /*В  методе обрабатываются стандартные 6 запросов из тех. задания*/
    public static ArrayList<String> getStrictVizualization(HttpServletRequest request) throws Exception {
        ArrayList<SingleApp> apps = new ArrayList<>();
        ArrayList<String> ret = new ArrayList(apps.size());
        String appName = request.getParameter("appName");
        String number = (request.getParameter("n") != null) ? request.getParameter("n") : "";

        switch (number) {
            case "1":
                if (AppMonitor.apps.get(appName) != null) {
                    apps.addAll(AppMonitor.apps.get(appName).getGroup());
                }
                Collections.sort(apps, new FieldValueComparator<Integer>("handlingTime"));
                break;

            case "2":
                for (AppGroup agr : AppMonitor.apps.values()) {
                    apps.addAll(agr.getGroup());
                } 
                apps = (ArrayList) new CollectionReaper("status").eqReap(apps, "UP", true);
                Collections.sort(apps, new FieldValueComparator<Integer>("cpu"));
                break;
            case "6":
                for (AppGroup agr : AppMonitor.apps.values()) {
                    apps.addAll(agr.getGroup());
                } 
                apps = (ArrayList) new CollectionReaper("status").eqReap(apps, "UP", true);
                Collections.sort(apps, new FieldValueComparator<Integer>("regTime"));
                Collections.reverse(apps);
                break;
            case "3":
                ret.addAll(AppMonitor.apps.keySet());
                Collections.sort(ret);
                break;
            case "4":
                if (AppMonitor.apps.get(appName) != null) {
                    ret.add(Integer.toString(AppMonitor.apps.get(appName).getGroup().size()));
                } else {
                    ret.add(new Integer(0).toString());
                }
                break;
            case "5":
                  for (AppGroup agr : AppMonitor.apps.values()) {
                apps.addAll(agr.getGroup());
                }
                
                    apps = (ArrayList) new CollectionReaper("cpu").bwReap(apps, request.getParameter("minCpu"), request.getParameter("maxCpu"));
                
                
                break;

        }

        for (SingleApp a : apps) {
            ret.add(a.toString());
        }

        return ret;
    }

    /*  Метод позволяет формировать список активностей из "гибких запросов". Этого не было в т.з.,
     однако, в процессе разработки приложения удалось получить масштабируемую структуру с возможностью
     осуществлять отбор элементов SingleApp по значению произвольного поля. При этом поле может быть любого
     типа, приводимого к числовым оберточным классам(Integer, Long...) или типа String. У запрашиваемого поля должен
     быть публичный getter. 
     Для заданного в запросе приложения (параметр "appName") выбираются все зарегестрированные
     активности, значения поля c именем (параметр "likeParam") которых равны значению переданному в параметре "like".
     Если необходимо отобрать активности, значения определенного параметра которых лежат в заданном диапазоне (или '>','<'),
     в параметре "borderParam" отправляется имя поля экземпляра SingleApp, по которому ведется отбор. В  max и min
     передаются граничные значения.
     Полученый список может быть упорядочен по возрастанию или убыванию  значения поля с именем переданным
     в "orderParam". 
     */
    public static ArrayList<SingleApp> getFlexibleVisualization(HttpServletRequest request) throws Exception {
        String appName = request.getParameter("appName");
        ArrayList<SingleApp> apps = new ArrayList<>();
        if (appName != null && !appName.isEmpty()) {
            if (AppMonitor.apps.get(appName) != null) {
                apps.addAll(AppMonitor.apps.get(appName).getGroup());
            }
        } else {
            for (AppGroup agr : AppMonitor.apps.values()) {
                apps.addAll(agr.getGroup());
            }
        }
        if (apps.isEmpty())return apps;
        String likePar = request.getParameter("likeParam");
        String like = request.getParameter("like");
        String ul = request.getParameter("unlike");
        boolean unlike = !(ul == null || ul.isEmpty());
        if (likePar != null && !likePar.isEmpty() && ((like != null) && (!like.isEmpty()))) {
            apps = (ArrayList) new CollectionReaper(likePar).eqReap(apps, like, !unlike);
        }

        String borderParam = request.getParameter("borderParam");
        String max = request.getParameter("max");
        String min = request.getParameter("min");
        if (borderParam != null&& !borderParam.isEmpty()) {
            if (max != null && !max.isEmpty()) {
                if (min != null && !min.isEmpty()) {
                    apps = (ArrayList) new CollectionReaper(borderParam).bwReap(apps, min, max);
                } else {
                    apps = (ArrayList) new CollectionReaper(borderParam).ltReap(apps, max);
                }

            } else {
                if (min != null && !min.isEmpty()) {
                    apps = (ArrayList) new CollectionReaper(borderParam).gtReap(apps, min);
                }
            }
        }
        String ordereParam = request.getParameter("orderParam");
        if (ordereParam != null && !ordereParam.isEmpty()) {
            Collections.sort(apps, new FieldValueComparator<Integer>(ordereParam));
            if (request.getParameter("inverse") != null) {
                Collections.reverse(apps);
            }

        }
        return apps;
    }

    /* Класс позволяет отбирать из коллекции элементы SingleApp, значения полей которых удовлетворяют
     заданным значениям*/
    static class CollectionReaper {

        private Method getter;
        /* В качестве аргумента конструктору передается имя поля, по значению которого будет осуществляться выборка элементов*/

        public CollectionReaper(String fieldName) throws NoSuchMethodException {
            getter = SingleApp.class.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
        }

        /*Метод возвращает элементы колекции, значения поля которых равно (или не равно equality==false ) значению value*/
        public <T extends Comparable> List eqReap(Collection<SingleApp> c, String value, boolean equality) throws IllegalAccessException,
                InvocationTargetException {
            List res = new ArrayList(c.size());
            SingleApp element = c.iterator().next();
            T val;
            try {
                val = (T) doWrapp((Comparable) getter.invoke(element), value);
            } catch (NoSuchMethodException e) {
                val = (T) value;
            }

            for (SingleApp sa : c) {

                if ((((Comparable) getter.invoke(sa)).compareTo(val) == 0) == equality) {
                    res.add(sa);
                }
            }
            return res;
        }
        /*Метод возвращает элементы колекции, значение поля которых меньше  значения maxValue*/

        public <T extends Comparable> List ltReap(Collection<SingleApp> c, String maxValue) throws IllegalAccessException,
                InvocationTargetException {
            List res = new ArrayList(c.size());
            SingleApp element = c.iterator().next();
            T max;
            try {
                max = (T) doWrapp((Comparable) getter.invoke(element), maxValue);
            } catch (NoSuchMethodException e) {
                max = (T) maxValue;
            }
            for (SingleApp sa : c) {
                T value = ((T) getter.invoke(sa));
                if (value.compareTo(max) > 0) {
                    continue;
                }
                res.add(sa);
            }
            return res;
        }
        /*Метод возвращает элементы колекции, значение поля которых больше  значения minValue*/

        public <T extends Comparable> List gtReap(Collection<SingleApp> c, String minValue) throws IllegalAccessException,
                InvocationTargetException {
            List res = new ArrayList(c.size());
            SingleApp element = c.iterator().next();
            T min;
            try {
                min = (T) doWrapp((Comparable) getter.invoke(element), minValue);
            } catch (NoSuchMethodException e) {
                min = (T) minValue;
            }
            for (SingleApp sa : c) {
                T value = ((T) getter.invoke(sa));
                if (value.compareTo(min) < 0) {
                    continue;
                }
                res.add(sa);
            }
            return res;
        }
        /*Метод возвращает элементы колекции, значение поля которых в интервале между minValue и maxValue*/

        public <T extends Comparable> List bwReap(Collection<SingleApp> c, String min, String max) throws Exception {
            ArrayList res = new ArrayList(c.size());
            SingleApp element = c.iterator().next();
            T amin = (T) doWrapp((Comparable) getter.invoke(element), min);
            T amax = (T) doWrapp((Comparable) getter.invoke(element), max);
            for (SingleApp sa : c) {
                T value = ((T) getter.invoke(sa));
                if (value.compareTo(amin) < 0 || value.compareTo(amax) > 0) {
                    continue;
                }
                res.add(sa);
            }
            res.trimToSize();
            return res;
        }

        /*Метод оборачивает аргумент s в класс(обертку числовых классов) указаный в параметре type */
        public <V extends Comparable> V doWrapp(V type, String s) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            return (V) type.getClass().getMethod("valueOf", String.class).invoke(type, s);
        }
    }

    /* Класс позволяет реализовать обобщенный компаратор для сортировки по значению произвольного поля SingleApp, 
     получаемому при помощи рефлексии.
     */
    static class FieldValueComparator<T extends Comparable> implements Comparator<SingleApp> {

        private Method getter;

        public FieldValueComparator(String fieldName) throws NoSuchMethodException {
            String sgetter = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            getter = SingleApp.class.getMethod(sgetter);
        }

        @Override
        public int compare(SingleApp a, SingleApp b) {

            try {
                return ((T) getter.invoke(a)).compareTo((T) getter.invoke(b));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
            return 0;

        }
    }
}
