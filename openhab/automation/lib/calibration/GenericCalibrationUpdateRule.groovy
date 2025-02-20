package fi.smolbo.openhab.automation.lib.calibration

import fi.smolbo.openhab.automation.lib.calibration.impl.Calibrator
import fi.smolbo.openhab.automation.lib.calibration.impl.SensorCalibrationItems
import org.openhab.core.automation.Action
import org.openhab.core.automation.module.script.rulesupport.shared.simple.SimpleRule
import org.openhab.core.items.ItemRegistry
import org.openhab.core.library.items.NumberItem
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

class GenericCalibrationUpdateRule extends SimpleRule {
    private final ItemRegistry itemRegistry

    GenericCalibrationUpdateRule(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry
    }

    Object execute(Action module, Map<String, ?> inputs) {
        def logS = LoggerFactory.getLogger("org.openhab.core.automation.calibration.generic")
        try {

            //logi.warn("***********")
            //logi.info("Module: {}", module.dump())
            //logi.info("Inputs list: {}", inputs.keySet())

            def triggeringItem = inputs["triggeringItem"] as NumberItem

            def sensorItems = new SensorCalibrationItems(itemRegistry, triggeringItem)

            def usefulCalibrationPoints = sensorItems.getUsefulPoints()

            def calibrator = new Calibrator(usefulCalibrationPoints)
            calibrator.setNoPointsBehavior(Calibrator.NoPointsBehavior.RETURN_INPUT)

            def calibratedVal = sensorItems.getRawVal()
                    .map { calibrator.calibratedValue(it) }

            sensorItems.setCalibratedVal(calibratedVal)

            def actualPoints = calibrator.getActualPoints()

            logS.info(" {}: {} -> {}, (CalPoints: {})",
                    sensorItems.getSensorGroup().getName(),
                    sensorItems.rawValue.getState(),
                    sensorItems.calibratedValue.getState(),
                    actualPoints.stream().map {
                        "{${it.inVal.orElse(Double.NaN)}-> ${it.outVal.orElse(Double.NaN)}}"
                    }.collect(Collectors.joining(", "))
            )
            return null
        } catch (t) {
            logS.warn("Script RUN error: ", t)
            throw t
        }
    }

}


