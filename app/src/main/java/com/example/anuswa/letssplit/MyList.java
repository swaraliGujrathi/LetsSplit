package com.example.anuswa.letssplit;

public class MyList {


    String person ;
    int cost;
    String num;

    public MyList(String person, int cost ,String num) {
        this.person = person;
        this.cost = cost;
        this.num = num;
    }

    public MyList() {
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}