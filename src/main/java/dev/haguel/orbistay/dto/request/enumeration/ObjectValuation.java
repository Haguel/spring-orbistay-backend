package dev.haguel.orbistay.dto.request.enumeration;

public enum ObjectValuation {
    EXCELLENT("9,10"),
    VERY_GOOD("8,9"),
    GOOD("7,8");

    public final String range;

    ObjectValuation(String range) {
        this.range = range;
    }

    public static ObjectValuation fromRate(double rate) {
        if (rate >= 9) {
            return EXCELLENT;
        } else if (rate >= 8 && rate < 9) {
            return VERY_GOOD;
        } else if (rate >= 7 && rate < 8) {
            return GOOD;
        } else {
            return null;
        }
    }
}
