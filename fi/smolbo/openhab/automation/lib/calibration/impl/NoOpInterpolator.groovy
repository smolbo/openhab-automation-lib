package fi.smolbo.openhab.automation.lib.calibration.impl

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
import org.apache.commons.math3.exception.DimensionMismatchException
import org.apache.commons.math3.exception.MathIllegalArgumentException

class NoOpInterpolator implements UnivariateInterpolator {

    private final UnivariateFunction interpolationFun

    NoOpInterpolator(UnivariateFunction retFun) {
        interpolationFun = retFun
    }

    @Override
    UnivariateFunction interpolate(double[] doubles, double[] doubles1) throws MathIllegalArgumentException, DimensionMismatchException {
        return interpolationFun
    }

    static final UnivariateFunction ZERO_FUNCTION = new UnivariateFunction() {
        @Override
        double value(double v) {
            return 0
        }
    }

    static final UnivariateFunction NAN_FUNCTION = new UnivariateFunction() {
        @Override
        double value(double v) {
            return Double.NaN
        }
    }

    static final UnivariateFunction RET_INPUT_FUNCTION = new UnivariateFunction() {
        @Override
        double value(double v) {
            return v
        }
    }

    static final Set<UnivariateFunction> known = new HashSet<>(Arrays.asList(
            ZERO_FUNCTION, NAN_FUNCTION, RET_INPUT_FUNCTION
    ))

    static boolean isStaticFun(UnivariateFunction other) {
        return known.contains(other)
    }
}