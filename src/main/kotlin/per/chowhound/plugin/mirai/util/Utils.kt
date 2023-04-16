package per.chowhound.plugin.mirai.util

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.lang.ClassScanner
import cn.hutool.core.lang.Filter
import per.chowhound.plugin.mirai.ClassSchedule
import java.sql.Date
import java.time.LocalDate

/**
 * @Author: Chowhound
 * @Date: 2023/4/16 - 15:56 
 * @Description: [Date]的工具类
 */
object SqlDateUtil{
    val TODAY: Date
        get() {return Date.valueOf(LocalDate.now())}
    
    val YESTERDAY: Date
        get() {return Date.valueOf(LocalDate.now().apply { this.minusDays(1) })}

    val TOMORROW: Date
        get() {return Date.valueOf(LocalDate.now().apply { this.plusDays(1) }) }


    // 获取当天加上int天的日期
    fun getDateReact(int: Int? =null): Date = Date.valueOf(LocalDate.now().apply { this.minusDays(int?.toLong() ?: 1L) })
}

/**
 * @Author: Chowhound
 * @Date: 2023/4/15 - 22:55
 * @Description:
 */

@Suppress("unused")
object HutoolClassUtil{
    /**
     * 扫描包下的所有类
     * 由于mirai的console插件加载机制，Hutool的ClassUtil.scanPackage()中使用Thread.currentThread().getContextClassLoader()
     * 获取的类加载器并不是实际加载该插件的加载器，不能在packages中找到该插件的class
     * 故使用ClassSchedule::class.java.classLoader获取的加载器
     * @param packageName 包名
     * @return 类集合
     */
    fun scanPackageBySuper(packageName: String, superClass: Class<*>): Set<Class<*>> {

        val filter = Filter { clazz: Class<*> -> superClass.isAssignableFrom(clazz) && superClass != clazz }
        return ClassScanner(packageName, filter).apply { this.setClassLoader(ClassSchedule::class.java.classLoader) }.scan()
    }
}

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
