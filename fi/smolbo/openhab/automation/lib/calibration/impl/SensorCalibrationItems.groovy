package fi.smolbo.openhab.automation.lib.calibration.impl

import org.openhab.core.items.GroupItem
import org.openhab.core.items.ItemRegistry
import org.openhab.core.library.items.NumberItem
import org.openhab.core.library.types.DecimalType
import org.openhab.core.types.State
import org.openhab.core.types.UnDefType
import fi.smolbo.openhab.automation.lib.Utils

class SensorCalibrationItems {
    final GroupItem sensorGroup
    final GroupItem calibrationGroup
    final NumberItem rawValue
    final NumberItem calibratedValue
    final List<CalibrationPointItems> calibrationPointsItems
    final List<CalibrationPoint> usefulPoints
    final ItemRegistry itemRegistry

    SensorCalibrationItems(ItemRegistry itemRegistry, NumberItem rawValueItemTriggering) {
        this.itemRegistry = itemRegistry
        rawValue = rawValueItemTriggering
        sensorGroup = getSensorGroupFromRawItemGroup(rawValue as NumberItem)
        calibrationGroup = getCalibrationGroup(sensorGroup)
        calibratedValue = Utils.<NumberItem> getMemberOrFail(sensorGroup, "_calibratedVal")
        def calibrationPointsGroups = calibrationGroup.getMembers()
        calibrationPointsItems = calibrationPointsGroups.collect {
            it as GroupItem
            new CalibrationPointItems(it as GroupItem)
        }
        usefulPoints = calibrationPointsItems
                .collect { it.toCalibrationPoint() }
                .findAll { it.isUseful() }
                .sort { it.inVal.get() }
    }


    GroupItem getSensorGroupFromRawItemGroup(NumberItem triggeredRawValueItem) {
        def groupName = triggeredRawValueItem.getGroupNames()
                .find { it -> triggeredRawValueItem.getName().startsWith(it) }
        return itemRegistry.get(groupName) as GroupItem
    }

    GroupItem getCalibrationGroup(GroupItem sensorGroup) {
        return Utils.<GroupItem> getMemberOrFail(sensorGroup, "_calibration")
    }

    List<CalibrationPoint> getUsefulPoints() {
        return usefulPoints
    }

    Optional<Double> getRawVal() {
        return Utils.getOptDouble(rawValue.getState())
    }

    void setCalibratedVal(Optional<Double> calValOpt) {
        State state = calValOpt.filter {
            !Double.isNaN(it)
        }.<State> map {
            new DecimalType(it)
        }.orElse(UnDefType.UNDEF)

        calibratedValue.setState(state)
    }

}
