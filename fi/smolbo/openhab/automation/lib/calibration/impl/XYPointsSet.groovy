package fi.smolbo.openhab.automation.lib.calibration.impl

class XYPointsSet {
    final double [] x
    final double [] y
    final int size
    XYPointsSet(List<CalibrationPoint> points) {
        def normalized = normalizePoints(points)
        x = normalized.stream().mapToDouble {p -> p.inVal.get().doubleValue()}.toArray()
        y = normalized.stream().mapToDouble{p -> p.outVal.get().doubleValue()}.toArray()
        size = normalized.size()
    }

    int size() {
        return size
    }

    static List<CalibrationPoint> normalizePoints(List<CalibrationPoint> points) {
        return points.findAll{it.isUseful()}.sort {it.inVal.get()}
    }
}