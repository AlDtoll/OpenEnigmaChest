package com.example.pusika.openenigmachest;

public class Enigma {

    private String word;
    private String describe;

    public Enigma(String word, String describe) {
        this.word = word;
        this.describe = describe;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
