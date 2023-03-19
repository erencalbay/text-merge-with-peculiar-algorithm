package dev.erencalbay.merge;

import java.util.ArrayList;
import java.util.Arrays;

public class User {


    private String[] Texts;

    public String[] getTexts() {
        return Texts;
    }

    public void setTexts(String[] texts) {
        Texts = texts;
    }

    @Override
    public String toString() {
        return "User{" +
                "Texts=" + Arrays.toString(Texts) +
                '}';
    }
}
