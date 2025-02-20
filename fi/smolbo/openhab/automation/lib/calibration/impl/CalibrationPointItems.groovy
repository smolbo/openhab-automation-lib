package fi.smolbo.openhab.automation.lib.calibration.impl

import org.slf4j.LoggerFactory
import org.openhab.core.items.GroupItem
import org.openhab.core.library.items.NumberItem
import org.openhab.core.library.items.SwitchItem
import org.openhab.core.library.types.OnOffType
import org.openhab.core.types.State
import org.openhab.core.types.UnDefType
import fi.smolbo.openhab.automation.lib.Utils

class CalibrationPointItems {
    private static log = LoggerFactory.getLogger(CalibrationPointItems.class)

    private NumberItem inVal
    private NumberItem outVal
    private SwitchItem isEnabled

    CalibrationPointItems(GroupItem point) {
        inVal = Utils.<NumberItem> getMemberOrFail(point, "_inVal")
        outVal = Utils.<NumberItem> getMemberOrFail(point, "_outVal")
        isEnabled = Utils.<SwitchItem> getMemberOrFail(point, "_enabled")
    }


    CalibrationPoint toCalibrationPoint() {
        boolean enabled = switch (isEnabled.getState()) {
            case OnOffType.ON -> true
            case OnOffType.OFF -> false
            case UnDefType -> false
            default -> {
                log.warn("Unknown type for expected boolean: {}", isEnabled); false
            }

        }

        return new CalibrationPoint(
                Utils.getOptDouble(inVal.getState() as State),
                Utils.getOptDouble(outVal.getState() as State),
                enabled
        )
    }
}