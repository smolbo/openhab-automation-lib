package fi.smolbo.openhab.automation.lib.calibration.impl

class CalibrationPoint {
    final Optional<Double> inVal
    final Optional<Double> outVal
    final boolean enabled

    CalibrationPoint(Optional<Double> inVal, Optional<Double> outVal, boolean enabled) {
        this.inVal = inVal
        this.outVal = outVal
        this.enabled = enabled
    }

    CalibrationPoint(double inVal, double outVal) {
        this(Optional.of(inVal), Optional.of(outVal), true)
    }

    boolean isUseful() {
        return enabled && inVal.isPresent() && outVal.isPresent()
    }


    String toString() {
        return "CalibrationPoint{" +
                "inVal=" + inVal +
                ", outVal=" + outVal +
                ", enabled=" + enabled +
                '}'
    }
}