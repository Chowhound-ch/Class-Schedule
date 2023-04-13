package per.chowhound.plugin.mirai.util

import per.chowhound.plugin.mirai.ClassSchedule

/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 20:51
 * @Description: 日志工具类
 */

    fun  Any.logDebug(msg : String) = ClassSchedule.logger.debug(msg)

    fun  Any.logInfo(msg : String) = ClassSchedule.logger.info(msg)

    fun  Any.logWarn(msg : String) = ClassSchedule.logger.warning(msg)

    fun  Any.logError(msg : String) = ClassSchedule.logger.error(msg)
}

