package com.example.allergydetect.models;

public class Allergen {

    private String AllergenCode;
    private String AllergenName;

    public Allergen(String allergenCode, String allergenName){
        this.AllergenCode = allergenCode;
        this.AllergenName = allergenName;
    }

    public String getAllergenCode() {
        return AllergenCode;
    }

    public void setAllergenCode(String allergenCode) {
        AllergenCode = allergenCode;
    }

    public String getAllergenName() {
        return AllergenName;
    }

    public void setAllergenName(String allergenName) {
        AllergenName = allergenName;
    }
}
