package info.androidhive.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.MatchersPrinter;
import org.mockito.verification.VerificationMode;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.speechtotext.Common.Common;
import info.androidhive.speechtotext.Helper.Helper;
import info.androidhive.speechtotext.Model.Clouds;
import info.androidhive.speechtotext.Model.Coord;
import info.androidhive.speechtotext.Model.Main;
import info.androidhive.speechtotext.Model.OpenWeatherMap;
import info.androidhive.speechtotext.Model.Rain;
import info.androidhive.speechtotext.Model.Sys;
import info.androidhive.speechtotext.Model.Weather;
import info.androidhive.speechtotext.Model.Wind;

import static org.junit.Assert.*;

/**
 * Created by joeluong on 2/17/17.
 */

public class MainActivityTest {

    @Rule
    public Timeout globalTimeout= new Timeout(1000);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void OnCreateTest() throws Exception {
        MainActivity mainAc = Mockito.mock(MainActivity.class);
        //mainAc.onCreate(Bundle.EMPTY);
        Mockito.doCallRealMethod().when(mainAc).onCreate(Bundle.EMPTY);
    }

    @Test
    public void getWeatherTest() throws Exception {
        MainActivity.GetWeather mainAc = new MainActivity().new GetWeather();
        String stream = null;
        String urlString = "ABC";
        Helper http = new Helper();
        stream = http.getHTTPData(urlString);
        Assert.assertEquals(stream, mainAc.doInBackground("ABC"));

        MainActivity.GetWeather mainAc2 = Mockito.mock(MainActivity.GetWeather.class);
        mainAc2.onPostExecute("s");
        Mockito.verify(mainAc2, Mockito.atLeastOnce()).onPostExecute("s");
    }

    // Failed, throw time out exception
    @Test
    public void turnOnTheLight() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertTrue(mainAc.turnOnTheLight());
    }

    @Test
    public void turnOffTheLight() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertFalse(mainAc.turnOffTheLight());
    }

    @Test
    public void connectToSocketIOTest() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertTrue(mainAc.ConnectToSocketIO());
    }

    @Test
    public void connectTest() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertFalse(mainAc.connect("A", 8888));
    }

    @Test
    public void playMusicTest() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertFalse(mainAc.playMusic());
    }

    // Failed, throw time out exception
    @Test
    public void promptSpeechInput() throws Exception {
        MainActivity mainAc = Mockito.mock(MainActivity.class);
        Assert.assertFalse(mainAc.promptSpeechInput());
    }

    @Test
    public void checkWeatherTest() throws Exception {
        MainActivity mainAc = new MainActivity();
        //Mockito.verify(mainAc, Mockito.doCallRealMethod(MainActivity.GetWeather.execute())).checkWeather();
        Assert.assertFalse(mainAc.checkWeather());
    }

    @Test
    public void toneAnalyzerTest() throws Exception {
        MainActivity mainAc = new MainActivity();
        Assert.assertFalse(mainAc.toneAnalyzer());
    }

    @Test
    public void onActivityResultTest() throws Exception {
        MainActivity mainAc = Mockito.mock(MainActivity.class);

        Mockito.doNothing().when(mainAc).onActivityResult(0,0,new Intent());
        //mainAc.onActivityResult(0,0, intent);
        //Mockito.verify(mainAc, Mockito.atLeastOnce()).onActivityResult(0,0, intent);
        //Assert.assertFalse(mainAc.toneAnalyzer());
    }


    @Test
    public void CloudsTest() throws Exception {
        Clouds clouds = new Clouds(1);
        Assert.assertEquals(1, clouds.getAll());
        clouds.setAll(0);
        Assert.assertEquals(0, clouds.getAll());
    }

    @Test
    public void CoordTest() throws Exception {
        Coord coord = new Coord(1,2);
        Assert.assertEquals(1, coord.getLat(), 0);
        Assert.assertEquals(2, coord.getLon(), 0);
        coord.setLat(3);
        Assert.assertEquals(3, coord.getLat(), 0);
        coord.setLon(4);
        Assert.assertEquals(4, coord.getLon(), 0);
    }

    // Main Class in Model Package
    @Test
    public void MainTest() throws Exception {
        Main main = new Main(1,2,3,4,5);
        Assert.assertEquals(1, main.getTemp(), 0);
        Assert.assertEquals(2, main.getPressure(), 0);
        Assert.assertEquals(3, main.getHumidity(), 0);
        Assert.assertEquals(4, main.getTemp_min(), 0);
        Assert.assertEquals(5, main.getTemp_max(), 0);
        main.setTemp(6);
        Assert.assertEquals(6, main.getTemp(), 0);
        main.setPressure(7);
        Assert.assertEquals(7, main.getPressure(), 0);
        main.setHumidity(8);
        Assert.assertEquals(8, main.getHumidity(), 0);
        main.setTemp_min(9);
        Assert.assertEquals(9, main.getTemp_min(), 0);
        main.setTemp_max(10);
        Assert.assertEquals(10, main.getTemp_max(), 0);
    }

    @Test
    public void RainTest() throws Exception {
        Rain rain = new Rain();
        Assert.assertNotNull(rain);
    }

    // Sys Class in Model package
    @Test
    public void SysTest() throws Exception {
        Sys sys = new Sys(1,2,3, "US", 4,5);
        Assert.assertEquals(1, sys.getType(), 0);
        Assert.assertEquals(2, sys.getId(), 0);
        Assert.assertEquals(3, sys.getMessage(), 0);
        Assert.assertEquals("US", sys.getCountry());
        Assert.assertEquals(4, sys.getSunrise(), 0);
        Assert.assertEquals(5, sys.getSunset(), 0);
        sys.setType(6);
        Assert.assertEquals(6, sys.getType(), 0);
        sys.setId(7);
        Assert.assertEquals(7, sys.getId(), 0);
        sys.setMessage(8);
        Assert.assertEquals(8, sys.getMessage(), 0);
        sys.setCountry("MEX");
        Assert.assertEquals("MEX", sys.getCountry());
        sys.setSunrise(9);
        Assert.assertEquals(9, sys.getSunrise(), 0);
        sys.setSunset(10);
        Assert.assertEquals(10, sys.getSunset(), 0);
    }

    @Test
    public void WeatherTest() throws Exception {
        Weather weather = new Weather(1, "A", "B", "C");
        Assert.assertEquals(1, weather.getId());
        Assert.assertEquals("A", weather.getMain());
        Assert.assertEquals("B", weather.getDescription());
        Assert.assertEquals("C", weather.getIcon());
        weather.setId(2);
        Assert.assertEquals(2, weather.getId());
        weather.setMain("D");
        Assert.assertEquals("D", weather.getMain());
        weather.setDescription("E");
        Assert.assertEquals("E", weather.getDescription());
        weather.setIcon("F");
        Assert.assertEquals("F", weather.getIcon());
    }

    @Test
    public void WindTest() throws Exception {
        Wind wind = new Wind(1,2);
        Assert.assertEquals(1, wind.getSpeed(), 0);
        Assert.assertEquals(2, wind.getDeg(), 0);
        wind.setSpeed(3);
        Assert.assertEquals(3, wind.getSpeed(), 0);
        wind.setDeg(4);
        Assert.assertEquals(4, wind.getDeg(), 0);
    }

    @Test
    public void OpenWeatherMapTest() throws Exception {
        OpenWeatherMap opwm = new OpenWeatherMap();
        opwm.setId(1);
        Assert.assertEquals(1, opwm.getId());
        opwm.setBase("A");
        Assert.assertEquals("A", opwm.getBase());
        opwm.setCod(2);
        Assert.assertEquals(2, opwm.getCod());
        opwm.setDt(3);
        Assert.assertEquals(3, opwm.getDt());
        opwm.setName("B");
        Assert.assertEquals("B", opwm.getName());
        Clouds cloud;
        opwm.setClouds(cloud = new Clouds(4));
        Assert.assertEquals(cloud, opwm.getClouds());
        Coord coord;
        opwm.setCoord(coord = new Coord(5,6));
        Assert.assertEquals(coord, opwm.getCoord());
        Main main;
        opwm.setMain(main = new Main(1,2,3,4,5));
        Assert.assertEquals(main, opwm.getMain());
        Wind wind;
        opwm.setWind(wind = new Wind(7,8));
        Assert.assertEquals(wind, opwm.getWind());
        Rain rain;
        opwm.setRain(rain = new Rain());
        Assert.assertEquals(rain, opwm.getRain());
        Sys sys;
        opwm.setSys(sys = new Sys(1,2,3, "US", 4,5));
        Assert.assertEquals(sys, opwm.getSys());
        List<Weather> weather;
        opwm.setWeather(weather= new ArrayList<>());
        weather.add(new Weather(1, "A", "B", "C"));
        Assert.assertEquals(weather, opwm.getWeather());
        OpenWeatherMap opwm2 = new OpenWeatherMap(coord, weather, "A", main, wind, rain, cloud, 1, sys, 2, "B", 3);
        Assert.assertNotNull(opwm2);
    }

    // Common class in Common package
    @Test
    public void CommonTest() throws Exception {
        Common common = new Common();
        StringBuilder sb = new StringBuilder(common.API_LINK);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric","3","5",common.API_KEY));
        Assert.assertEquals(sb.toString(),common.apiRequest("3","5"));
        String icon = String.format("http://openweathermap.org/img/w/%s.png","7");
        Assert.assertEquals(icon, common.getImage("7"));
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        Assert.assertEquals(dateFormat.format(date), common.getDateNow());
    }

    @Test
    public void HelperTest() throws Exception {
        Helper helper = new Helper();
        Assert.assertNotNull(helper);
    }

    @Test
    public void onActivityResult() throws Exception {

    }

    @Test
    public void connect() throws Exception {

    }

    @Test
    public void onCreateOptionsMenu() throws Exception {

    }

}