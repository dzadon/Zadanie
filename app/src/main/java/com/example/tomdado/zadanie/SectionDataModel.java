package com.example.tomdado.zadanie;

import java.util.ArrayList;

public class SectionDataModel {

    private ArrayList<SingleItemModel> allItemInSection;

    public SectionDataModel() {
    }

    public ArrayList<SingleItemModel> getAllItemInSection() {
        return allItemInSection;
    }

    public void setAllItemInSection(ArrayList<SingleItemModel> allItemInSection) {
        this.allItemInSection = allItemInSection;
    }
}