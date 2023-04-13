package per.chowhound.plugin.mirai.util

import cn.hutool.core.bean.BeanUtil

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