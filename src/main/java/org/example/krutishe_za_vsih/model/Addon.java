package org.example.krutishe_za_vsih.model;

public class Addon {
    private String name;
    private int price;

    public Addon(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
}