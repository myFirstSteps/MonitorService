<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2>Форма гибкого запроса</h2>
        <p size ="3"> 
            <font size="2"> В форме задаются параметры по которым производится отбор активностей приложений.
                При заполнении формы по умолчанию  будет выведен упорядоченый по времени регистрации список активностей приложения GSHOCK, у которых загрузка cpu меньше 101%,
                a статус равен "up". Меняя значения полей формы можно формировать гибкие запросы. 
            </font></p>
        <div align='left'>
            <form  action="req.svl">
                <input type='hidden' name='key' value="f">
                Приложение: <input size='6' value='GSHOCK' name='appName'><br>
                Где параметр: <select size='1' name='likeParam'>
                    <option>status
                    <option>uuid
                    <option>handlingTime
                    <option>cpu
                    <option>processed     
                    <option>
                </select>
                <select size ='1' name='unlike'>
                    <option value="">=
                    <option value='1'> &#x2260   
                </select>
                <input size='6'  value='UP' name='like'>
                <br>
                <input size='3' name='min'>&lt
                <select size='1' name='borderParam'>
                    <option>cpu
                    <option>handlingTime
                    <option>processed  
                    <option>
                </select>        
                &lt<input size='3' name='max' value ='100'><br>
                Упорядочить по:
                <select size='1' name='orderParam'>
                    <option>regTime
                    <option>cpu
                    <option> handlingTime
                    <option>processed 
                    <option>
                </select>
                <sub> по убыванию </sub><input name='inverse' type='checkbox'><br>
                <input align="right" type="submit" value="Запросить">
            </form>
            <br>
            <hr>
            <h2>Стандартные запросы:</h2>
            <p size ="3"> 
                <font size="2"> В форме задаются стандартные запросы из т.з. <br>
                    1- Для заданного имени приложения (обычный параметр в GET запросе)  выдавать список приложений упорядоченных от минимального значения [среднего времени обработки одного запроса] до максимального значения [среднего времени обработки одного запроса]<br>
                    2- Список приложений упорядоченных от менее загруженных к более загруженным(параметр CPU) и упорядоченных так же по времени от недавно зарегистрированнх к давно зарегистрированным(запрос 6), при этом у приложения должен быть статус UP;<br>
                    3- Список только имен всех приложений отсортированных в алфавитном порядке.<br>
                    4- Количество инстансов для данного приложения - передается в GET запросе имя приложения.<br>
                    5- Список приложений, у которых загрузка CPU  попадает в заданный интервал. Интервал задается значениями GET параметров: minCpu и maxCpu.<br> 
                    6- см. запрос 2.<br>
                    Номер запроса указывается в параметре тип запроса. Если запрос требует дополнительных параметров (например, имя приложения), они указываются в соответствующих полях.<br>
                    
                    
                </font></p>
            <form action="req.svl">
                <input name="key" type="hidden" value="s">
                тип запроса:
                <select name="n" size='1'>
                    <option>1
                    <option>2
                    <option>3
                    <option>4
                    <option>5   
                    <option>6
                </select>
                приложение:<input name='appName' size="4" value="GSHOCK">
                minCpu:<input name='minCpu' size="2" value="100"> 
                maxCpu: <input name='maxCpu' size="2" value="100">    
                <input type="submit" value="Отправить запрос">
            </form>
            <br>
            <hr>
            <br>
            <hr>
            <h2>Форма регистрации</h2>
            <p size ="3"> 
                <font size="2"> 
                    При каждом нажатии кнопки регистрируется активность инстанса с заданными значениями параметров. Следует задавать параметрам корректные 
                    значения (подобные заданным) или регистрации не произойдет.
                </font></p>
            <form method='Post' action="req.svl">
                Приложение: <input size='6' name='appName' value='GSHOCK'><br>
                UUID: <input size='30' name='uuid' value='32e84447-dc66-4e80-ab60-c1ba0d5c15d0'><br>
                Статус:<select name="status" size='1'>
                    <option value="UP">"UP"
                    <option value="STARTING">"STARTING"
                    <option value='PROCESSING'>"PROCESSING"
                </select>
                cpuCapacity: <input size='1' name='cpu' value='13'>%<br>
                handlingTime: <input  size='1' name='handlingTime' value='120'>ms &nbsp &nbsp
                processed:<input size='2' name='processed' value='1221'><br>
                <input align="right" type="submit" value="Зарегистрировать">
            </form>

        </div>
        <div align='right'>
            <form method='post' action="req.svl"></form>
        </div>
    </body>
</html>
