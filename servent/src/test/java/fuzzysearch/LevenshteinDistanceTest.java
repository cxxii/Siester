package fuzzysearch;

import org.cxxii.search.LevenshteinDistance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LevenshteinDistanceTest {

    @Test
    void testEqualStrings() {
        String s1 = "hello";
        String s2 = "hello";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(0, result, "Equal strings should have a Levenshtein distance of 0");
    }

    @Test
    void testCompletelyDifferentStrings() {
        String s1 = "abc";
        String s2 = "xyz";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(3, result, "Completely different strings of the same length should have a distance equal to the length");
    }

    @Test
    void testOneEmptyString() {
        String s1 = "";
        String s2 = "abc";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(3, result, "Levenshtein distance between an empty string and a non-empty string should be the length of the non-empty string");
    }

    @Test
    void testOneEmptyStringReversed() {
        String s1 = "abc";
        String s2 = "";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(3, result, "Levenshtein distance between a non-empty string and an empty string should be the length of the non-empty string");
    }

    @Test
    void testSingleCharacterSubstitution() {
        String s1 = "kitten";
        String s2 = "sitten";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(1, result, "Levenshtein distance for a single substitution should be 1");
    }

    @Test
    void testSingleCharacterInsertion() {
        String s1 = "flaw";
        String s2 = "flaws";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(1, result, "Levenshtein distance for a single insertion should be 1");
    }

    @Test
    void testSingleCharacterDeletion() {
        String s1 = "flaws";
        String s2 = "flaw";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(1, result, "Levenshtein distance for a single deletion should be 1");
    }

    @Test
    void testCaseSensitiveDifference() {
        String s1 = "Hello";
        String s2 = "hello";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(1, result, "Levenshtein distance between two strings differing only in case should be 1");
    }

    @Test
    void testComplexDifference() {
        String s1 = "intention";
        String s2 = "execution";

        int result = LevenshteinDistance.calculate(s1, s2);

        assertEquals(5, result, "Levenshtein distance between 'intention' and 'execution' should be 5");
    }
}
