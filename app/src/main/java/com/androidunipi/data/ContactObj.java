package com.androidunipi.data;

/**
 * Created by ARISTEAA on 10/10/2016.
 */
public class ContactObj extends UsersObj {

    private String image;
    private String active;
    private Double rate;

    public void setImage(String image) { this.image = image;}
    public String getImage() {return image;}

    public void setActive(String active) { this.active = active;}
    public String getActive() {return active;}

    public void setRate(Double rate) { this.rate = rate;}
    public Double getRate() {return rate;}
}
