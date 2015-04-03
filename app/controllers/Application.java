package controllers;

import play.*;
import play.mvc.*;

import support.Day;
import support.SOAPController;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result weather(){

        SOAPController weather = new SOAPController();
        Day[] days = weather.getAllData();

        String reply = "[";

        for(int i = 0; i < days.length; i++){
            reply += days[i].getJson();
        }

        reply += "]";

        return ok(reply);
    }

    public static Result getIP(){

        return ok(request().remoteAddress());

    }

}
