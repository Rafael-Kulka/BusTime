package dev.rafaelkulka.BusTimeAPI.models.BusTimes;

import java.util.ArrayList;

public class BusTime {
    public BusTime(){
        linhas = new ArrayList<>();
    }
    public ArrayList<Linhas> linhas;
}