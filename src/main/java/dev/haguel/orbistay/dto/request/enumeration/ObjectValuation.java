package dev.haguel.orbistay.dto.request.enumeration;

public enum ObjectValuation {
    EXCELLENT("9,10"),
    VERY_GOOD("8,9"),
    GOOD("7,8");

    public final String range;

    ObjectValuation(String range) {
        this.range = range;
    }
}
