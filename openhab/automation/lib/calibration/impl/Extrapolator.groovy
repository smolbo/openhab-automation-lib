package fi.smolbo.openhab.automation.lib.calibration.impl

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.NevilleInterpolator

class Extrapolator {
    private final UnivariateFunction extrapolateDown
    private final UnivariateFunction extrapolateUp
    private final double xLow
    private final double xHigh

    Extrapolator(XYPointsSet points, UnivariateFunction noOpFunction) {
        def interpolator = switch (points.size()) {
            case {it < 2} -> new NoOpInterpolator(noOpFunction)
            default -> new NevilleInterpolator()
        }
        xLow = points.size() > 1 ? points.x[0] : 0.0
        xHigh = points.size() > 1 ? points.x[points.size() - 1] : 0.0
        def fun = interpolator.interpolate(points.x, points.y)
        extrapolateUp = fun
        extrapolateDown = fun
    }

    boolean isOutOfInterpolationRange(double x) {
        return x < xLow || x > xHigh
    }

    double extrapolate(double x) {
        if(x < xLow)
            return extrapolateDown.value(x)
        if(x > xHigh)
            return extrapolateUp.value(x)
        throw new RuntimeException("Should be interpolated: x = $x, range: [$xLow..$xHigh]")
    }
}