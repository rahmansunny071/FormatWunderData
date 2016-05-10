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
class AirPort {
    String name;
    String city;
    String state;
    String lati;
    String longi;
    
    public AirPort(String name,String city,String state,String lati,String longi)
    {
        if(city.equals("NA"))
            city = "";
        if(state.equals("NA"))
            state = "";
        this.name = name+","+state;
        this.city = city+","+state;
        this.state = state;
        this.lati = lati;
        this.longi = longi;
    
    }
}
