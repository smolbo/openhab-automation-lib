package fi.smolbo.openhab.automation.lib.calibration.impl

import fi.smolbo.openhab.automation.lib.calibration.impl.NoOpInterpolator
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator

class Calibrator {
    private UnivariateFunction interpolationFunction

    private final UnivariateFunction noOpProxy = new UnivariateFunction() {
        private NoPointsBehavior behavior = NoPointsBehavior.RETURN_NAN
        @Override
        double value(double v) {
            return behavior.noOpFunction.value(v)
        }
    }

    private final Extrapolator extrapolator

    private boolean isDefaultPoints = false

    private final List<CalibrationPoint> actualPoints

    Calibrator(List<CalibrationPoint> points) {
        this(points, Optional.<List<CalibrationPoint>>empty())
    }

    Calibrator(List<CalibrationPoint> points, List<CalibrationPoint> defaultPoints) {
        this(points, Optional.of(defaultPoints))
    }

    Calibrator(List<CalibrationPoint> points, Optional<List<CalibrationPoint>> defaultPoints) {
        XYPointsSet calibration = new XYPointsSet(points)
        if(calibration.size() < 2 && defaultPoints.isPresent()) {
            calibration = new XYPointsSet(defaultPoints.get())
            isDefaultPoints = true
            actualPoints = defaultPoints.get()
        } else {
            isDefaultPoints = false
            actualPoints = points
        }
        interpolationFunction = createInterpolator(calibration)
        extrapolator = new Extrapolator(calibration, noOpProxy)
    }

    void setNoPointsBehavior(NoPointsBehavior behavior) {
        noOpProxy.behavior = behavior
    }

    double calibratedValue(double inValue) {
        if(extrapolator.isOutOfInterpolationRange(inValue)) {
            return extrapolator.extrapolate(inValue)
        }
        return interpolationFunction.value(inValue)
    }

    boolean isDefaultCalibration() {
        return isDefaultPoints
    }

    boolean isNoOp() {
        return interpolationFunction == noOpProxy
    }

    List<CalibrationPoint> getActualPoints() {
        return actualPoints
    }

    private UnivariateFunction createInterpolator(XYPointsSet points) {

        UnivariateInterpolator interpolator = switch (points.size()) {
            case {it < 2} -> new NoOpInterpolator(noOpProxy)
            case 2 -> new LinearInterpolator()
            default -> new SplineInterpolator()
        }

        return interpolator.interpolate(points.x, points.y)
    }

    static enum NoPointsBehavior {
        RETURN_NAN(NoOpInterpolator.NAN_FUNCTION),
        RETURN_ZERO(NoOpInterpolator.ZERO_FUNCTION),
        RETURN_INPUT(NoOpInterpolator.RET_INPUT_FUNCTION)

        final UnivariateFunction noOpFunction
        NoPointsBehavior(UnivariateFunction fn) {
            noOpFunction = fn
        }
    }
}