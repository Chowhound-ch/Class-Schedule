package per.chowhound.plugin.mirai.util

import cn.hutool.core.bean.BeanUtil
import per.chowhound.plugin.mirai.ClassSchedule

/**
 * @Author: Chowhound
 * @Date: 2023/4/14 - 16:58
 * @Description:
 */
object WeekUtil {
    val WEEK_DAY = arrayOf("一", "二", "三", "四", "五", "六", "日")
}



/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 22:16
 * @Description:
 */
object BeanUtil {
    fun <T> copyProperties(source: Any, target: T): T{
        BeanUtil.copyProperties(source, target)
        return target
    }
}


/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 20:51
 * @Description: 日志工具类
 */
@Suppress("unused")
object Logger{
    fun  logDebug(msg : String) = ClassSchedule.logger.debug(msg)

    fun  logInfo(msg: String) = ClassSchedule.logger.info(msg)

    fun  logWarn(msg : String) = ClassSchedule.logger.warning(msg)

    fun  logError(msg : String) = ClassSchedule.logger.error(msg)
}
