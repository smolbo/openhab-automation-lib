package fi.smolbo.openhab.automation.lib

import org.slf4j.LoggerFactory
import org.openhab.core.items.GroupItem
import org.openhab.core.items.Item
import org.openhab.core.library.types.DecimalType
import org.openhab.core.library.types.QuantityType
import org.openhab.core.types.State
import org.openhab.core.types.UnDefType

class Utils {
    static def log = LoggerFactory.getLogger(Utils.class)

    static Optional<Double> getOptDouble(State state) {
        return switch (state) {
            case UnDefType -> Optional.empty()
            case DecimalType -> Optional.of((state as DecimalType).doubleValue())
            case QuantityType -> Optional.of((state as QuantityType).doubleValue())
            default ->
                {
                    log.warn("getOptionalDouble(): unsupported State: {}", state)
                    Optional.empty()
                }
        }
    }

    static <T extends Item> T getMemberOrFail(GroupItem group, String memberEndsWith) {
        return Utils.<T> getOptMember(group, memberEndsWith).get()
    }

    static <T extends Item> Optional<T> getOptMember(GroupItem group, String memberEndsWith) {
        return Optional.ofNullable(
                group.getMembers { it.getName().endsWith(memberEndsWith) }
                        .first() as T
        )
    }

    static String safeNumericStateFormat(State state, String format) {
        return switch (state) {
            case UnDefType -> state.toString()
            case DecimalType -> (state as DecimalType).format(format)
            case QuantityType -> (state as QuantityType).format(format)
            default ->
                {
                    log.warn("safeNumericStateFormat(): unsupported State: {}", state)
                    state.toString()
                }
        }
    }
}
