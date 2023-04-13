package per.chowhound.plugin.mirai.academic

import java.util.Date


/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 20:04
 * @Description: 课表实体类, 公共父类
 */
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

    open var date: Date = java.sql.Date(System.currentTimeMillis()),

    /**
     * 开始时间, 800 即为 8:00， 1300 即为 13:00
     */
    open var start: Int = 800,

    /**
     * 结束时间, 950 即为 9:50， 1300 即为 13:00
     */
    open var end: Int = 950
)

//data class HFUTSchedule(
//
//    override var name: String,
//
//    override var teacher: String,
//
//    override var room: String,
//
//    override var week: String,
//
//    override var day: Int,
//
//    override var start: Int,
//
//    override var end: Int
//
//) : Schedule(name, teacher, room, week, day, start, end)