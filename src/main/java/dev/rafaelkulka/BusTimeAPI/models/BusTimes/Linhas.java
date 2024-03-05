package dev.rafaelkulka.BusTimeAPI.models.BusTimes;

import java.util.ArrayList;

public class Linhas {
    public Linhas(){
        horarios = new ArrayList<Horario>();
    }
    public String name;
    public ArrayList<Horario> horarios;
}
