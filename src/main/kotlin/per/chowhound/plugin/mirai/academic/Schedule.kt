package per.chowhound.plugin.mirai.academic

import java.sql.Date


/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 20:04
 * @Description: 课表实体类, 公共父类
 */
@Suppress("unused")
open class Schedule(
    /**
     * 课程名称
     */
    open var name: String = "课程",
    /**
     * 教师
     */
    open var teacher: String = "教师",
    /**
     * 上课地点
     */
    open var room: String = "教室",
    /**
     * 周次
     */
    open var week: String = "周次",
    /**
     * 每周的第几天, 1-7
     */
    open var day: Int = 1,

    open var date: Date = Date(System.currentTimeMillis()),

    /**
     * 开始时间, 800 即为 8:00， 1300 即为 13:00
     */
    open var start: Int = 800,

    /**
     * 结束时间, 950 即为 9:50， 1300 即为 13:00
     */
    open var end: Int = 950
){
    fun toMessage(): String{
        return let{
            val startTime = String.format("%02d", start / 100) + ":" + String.format("%02d", start % 100) // 开始时间
            val endTime = String.format("%02d", end / 100) + ":" + String.format("%02d", end % 100)// 结束时间
//                "${DateUtil.format(date, "MM-dd")} 周${WEEK_DAY[i]}: ${startTime} - ${endTime} ${name} ${teacher} ${room} ${week})}"
            "\n * $startTime - $endTime $room $teacher $name $teacher"
        }
    }
}
