package org.example.lab7.domain;

/**
 * Uses the generic Pair, concrete pair between 2 Strings
 */
public class StringPair extends Pair<String, String> {

    public StringPair(String first, String second) {
        if (first.compareTo(second) > 0) {
            String aux = first;
            first = second;
            second = aux;
        }
        super.setFirst(first);
        super.setSecond(second);
    }

}
