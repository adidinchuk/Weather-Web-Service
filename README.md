# Weather-Web-Service

This web services uses Artom's Libraries to access USA weather information

#Function

#gets a default range od days for the default ip (testing purposes)
GET        /weather             controllers.Application.weather()

#gets and returns your current ip
GET        /ip                  controllers.Application.getIP()

#get weather using the request's ip address. argument contains the number of days to get
GET        /weatherByIp/:days   controllers.Application.getWeatherByIp(days: String)

#get weather using the default ip address (testing purposes). argument contains the number of days to get
GET        /weatherByDefault/:days   controllers.Application.getWeatherDefault(days: String)

Play Framework Weather-Web-Service
