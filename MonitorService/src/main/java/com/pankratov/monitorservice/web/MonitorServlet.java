package com.pankratov.monitorservice.web;

import com.pankratov.monitorservice.model.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class MonitorServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        try {
            PrintWriter resp = response.getWriter();
            try {
                //регистрация статуса приложения
                AppMonitor.doRegistration(new SingleApp(request));
                resp.println("OK");
            } catch (BadParamException e) {
                /* В этом месте можно отправить respone "Wrong Request", но согласно т.з. ответ всегда "ок"*/
                //  resp.println("Wrong Request");
                resp.println("OK");
            }
        } catch (IOException e) {
        }

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)  {
        List list = new ArrayList();
        response.setContentType("text/plain");
        try {
            PrintWriter pw = response.getWriter();
            try {
                //Параметр key определяет какой запрос (s-строгий, как в т.з. / f-гибкий) нужно обработать.
                switch (request.getParameter("key")) {
                    case "f":
                        list = AppMonitor.getFlexibleVisualization(request);
                        break;
                    case "s":
                        list = AppMonitor.getStrictVizualization(request);
                }
                for (Object app : list) {
                    pw.println(app);
                }
            } catch (Exception e) {
                String s = (e instanceof java.lang.reflect.InvocationTargetException) ? "Wrong parameter type" : "";
                pw.println("Invalid request." + s); 
            }
        } catch (IOException e) { 
        }

    }

}
