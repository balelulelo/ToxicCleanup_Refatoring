package test_utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * A collection of additional comparisons and assertions that may be useful in writing tests.
 */
public class TestUtils {
    /**
     * Protect constructor since it is a static only class
     */
    protected TestUtils() {
    }

    /**
     * Returns iff two Lists are equal regardless of their contents order. List(a, b) is equal to List(b, a)
     *
     * @param expected expected list
     * @param actual actual list
     */
    public static <T> boolean equalsAgnosticCompare(List<T> expected, List<T> actual) {
        List<T> actualCopy = new ArrayList<>(actual);
        for (T item : expected) {
            if (!actualCopy.remove(item)) {
                return false;
            }
        }
        return actualCopy.isEmpty();
    }

    /**
     * Asserts that two Lists are equal regardless of their contents order. List(a, b) is equal to List(b, a)
     *
     * @param missingItemMessage the identifying message for if actual is missing an element for
     * the {@link AssertionError} (<code>null</code> okay)
     * @param extraItemMessage the identifying message for if actual containts an extra a element for 
     * the {@link AssertionError} (<code>null</code> okay)
     * @param expected expected list
     * @param actual actual list
     */
    public static <T> void assertEqualsAgnosticCompare(String missingItemMessage, String extraItemMessage,
            List<T> expected, List<T> actual) {
        List<T> actualCopy = new ArrayList<>(actual);
        for (T item : expected) {
            assertTrue(missingItemMessage, actualCopy.remove(item));
        }
        assertTrue(extraItemMessage, actualCopy.isEmpty());
    }

    /**
     * Asserts that two Lists are equal regardless of their contents order. List(a, b) is equal to List(b, a)
     *
     * @param expected expected list
     * @param actual actual list
     */
    public static <T> void assertEqualsAgnosticCompare(List<T> expected, List<T> actual) {
        assertEqualsAgnosticCompare("Missing Item", "Extra Item", expected, actual);
    }

    /**
     * Returns if the actual value is within a given allowed range of the expected value
     *
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static boolean isInRange(int expected, int actual, int allowedError) {
        return Math.abs(expected - actual) <= allowedError;
    }

    /**
     * Returns iff the actual value is within a given allowed range of the expected value
     *
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static boolean isInRange(double expected, double actual, double allowedError) {
        return Math.abs(expected - actual) <= allowedError;
    }

    /**
     * Asserts that the actual value is within a given allowed range of the expected value
     * 
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static void assertInRange(String message, int expected, int actual, int allowedError) {
        assertTrue(message, isInRange(expected, actual, allowedError));
    }

    /**
     * Asserts that the actual value is within a given allowed range of the expected value
     *
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static void assertInRange(int expected, int actual, int allowedError) {
        assertInRange(null, expected, actual, allowedError);
    }

    /**
     * Asserts that the actual value is within a given allowed range of the expected value
     * 
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static void assertInRange(String message, double expected, double actual, double allowedError) {
        assertTrue(message, isInRange(expected, actual, allowedError));
    }

    /**
     * Asserts that the actual value is within a given allowed range of the expected value
     *
     * @param expected expected value
     * @param actual actual value
     * @param allowedError allowed error between expected and actual values
     */
    public static void assertInRange(double expected, double actual, double allowedError) {
        assertInRange(null, expected, actual, allowedError);
    }
}
