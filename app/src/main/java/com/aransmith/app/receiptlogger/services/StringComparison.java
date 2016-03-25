package com.aransmith.app.receiptlogger.services;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Created by Aran on 3/15/2016.
 * This StringComparison service is made to be used by any service
 * or Activity to make a comparison between two strings and return
 * a score on the comparison.
 */
public class StringComparison {
    /**
     *
     * @param first String from OCR.
     * @param second String to be tested against.
     * @return returns the levenshtein score between the first and
     * the second Strings. -1 is returned when the strings are deemed
     * too dissimilar. This happens when the amount of changes need to
     * turn first to second is second.length()-1 steps.
     */

    private final int ACCURACY = 4;

    public int stringCompare(String first, String second){

        int firstLength = second.length();
        int flexibility = firstLength/ACCURACY;

        return getLevenshteinDistance(first, second, flexibility);
    }
}
