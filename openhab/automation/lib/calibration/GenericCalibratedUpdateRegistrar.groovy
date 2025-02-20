package fi.smolbo.openhab.automation.lib.calibration

import org.slf4j.LoggerFactory
import org.openhab.core.automation.module.script.rulesupport.shared.ScriptedAutomationManager
import org.openhab.core.config.core.Configuration
import org.openhab.core.items.ItemRegistry
import org.openhab.core.automation.util.TriggerBuilder

// keep below imports to have SimpleRule
//import org.openhab.core.automation.*
//import org.openhab.core.automation.module.script.rulesupport.shared.simple.*

class GenericCalibratedUpdateRegistrar {
    private static def log = LoggerFactory.getLogger("org.openhab.core.automation.examples.greener.calibration")

    private static register(ItemRegistry itemRegistry, ScriptedAutomationManager automationManager) {

        try {
            def sRule = new GenericCalibrationUpdateRule(itemRegistry as ItemRegistry)
            sRule.setTriggers([
                    TriggerBuilder.create()
                            .withId("rule_GenericUpdateCalibratedScript")
                            .withTypeUID("core.GroupStateUpdateTrigger")
                            .withConfiguration(new Configuration([groupName: "calibration_generic_inValChange_cb"]))
                            .build()
            ])

            log.info("Adding rule     : {}", sRule.getClass().getSimpleName())
            def res = automationManager.addRule(sRule)
            log.info("Adding rule res : {}", res.dump())
            for (t in res.triggers) {
                log.info("Adding rule trig : {}", t.dump())
            }
        } catch (t) {
            log.error("Script INIT error: ", t)
            throw t
        }
    }
}

