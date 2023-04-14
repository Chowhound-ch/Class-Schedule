package per.chowhound.plugin.mirai.util

import per.chowhound.plugin.mirai.ClassSchedule

/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 20:51
 * @Description: 日志工具类
 */


fun  logDebug(msg : String) = ClassSchedule.logger.debug(msg)

fun  logInfo(msg: String) = ClassSchedule.logger.info(msg)

fun  logWarn(msg : String) = ClassSchedule.logger.warning(msg)

fun  logError(msg : String) = ClassSchedule.logger.error(msg)
