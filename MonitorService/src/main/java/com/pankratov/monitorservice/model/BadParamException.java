package com.pankratov.monitorservice.model;
/* Исключение вызывается если конструктору SingleApp переданы некорректные 
 аргументы. В исключение оборачивается причина возникновения 
 */

public class BadParamException extends Exception {

    public BadParamException(Throwable e) {
        super(e);
    }

    public BadParamException() {
        super();
    }

    public BadParamException(String msg, Throwable e) {
        super(msg, e);
    }

    public BadParamException(String msg) {
        super(msg);
    }
}
