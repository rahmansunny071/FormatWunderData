/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatairportdata;

/**
 *
 * @author srahman7
 */
public class AirData {
    int day;
    String year;
    String month;
    String dayOfMonth;
    String dayOfWeek;
    String weekOfYear;
    String carrier;
    String actualElapsedTime;
    String CRSelapsedTime;
    String arrival;
    String departure;
    String origin;
    String destination;
    
    
    public AirData(int day, String year, String month, String dayOfMonth, String dayOfWeek, String weekOfYear, String carrier, String actualElapsedTime,
            String CRSelapsedTime, String arrival, String departure, String origin, String destination) 
    {
        this.day = day;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.carrier = carrier;
        this.actualElapsedTime = actualElapsedTime;
        this.CRSelapsedTime = CRSelapsedTime;
        this.arrival = arrival;
        this.departure = departure;
        this.origin = origin;
        this.destination = destination;
    
    }
}
