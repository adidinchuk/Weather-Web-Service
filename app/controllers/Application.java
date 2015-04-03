package controllers;

import play.*;
import play.mvc.*;

import support.Day;
import support.SOAPController;
import views.html.*;

public class Application extends Controller {

    public static Result index() {

        return ok(index.render("Weather API Service - https://github.com/adidinchuk/Weather-Web-Service"));
    }

    public static Result weather(){

        SOAPController weather = new SOAPController();
        Day[] days = weather.getAllData();

        String reply = "[";

        for(int i = 0; i < days.length; i++){
            reply += days[i].getJson();
            if(i>0){
                reply += ", ";
            }
        }

        reply += "]";

        return ok(reply);
    }

    public static Result getIP(){

        return ok(request().remoteAddress());

    }

    public static Result getWeatherByIp(String days){

        SOAPController weather = new SOAPController(days);
        weather.getLatLongByIp(request().remoteAddress());
        Day[] result = weather.getAllData();

        String reply = "[";

        for(int i = 0; i < result.length; i++){
            reply += result[i].getJson();
            if(i>0){
                reply += ", ";
            }
        }

        reply += "]";

        return ok(reply);
    }

    public static Result getWeatherDefault(String days){

        SOAPController weather = new SOAPController(days);

        Day[] result = weather.getAllData();

        String reply = "[";

        for(int i = 0; i < result.length; i++){
            reply += result[i].getJson();
            if(i>0){
                reply += ", ";
            }
        }

        reply += "]";

        return ok(reply);
    }

}
