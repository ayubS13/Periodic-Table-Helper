/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ayubs
 */
import java.io.Serializable;
public class Element implements Serializable{
    private String name, symbol;
    private int number;
    private double weight;
    private double melting;
    private double boiling;
    
    Element next;
    Element previous;
    
    public Element(String name, String symbol, int number, double weight, double melting, double boiling){
        this.name = name;
        this.symbol = symbol; 
        this.number = number;
        this.weight = weight;
        this.melting = melting;
        this.boiling = boiling;
        
        next = null;
        previous = null;
    }
    
    String getName(){
        return name;
    }
    
    int getNumber(){
        return number;
    }
    
    String getSymbol(){
        return symbol;
    }
    
    double getWeight(){
        return weight;
    }
    
    double getMelting(){
        return melting;
    }
    
    double getBoiling(){
        return boiling;
    }
    
    
}
