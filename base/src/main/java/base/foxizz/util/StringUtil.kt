package base.foxizz.util

/**
 * 字符串工具类
 */

val REGEX_DIGITS = Regex("[0-9]+") //数字
val REGEX_LETTERS = Regex("[A-Za-z]+") //字母
val REGEX_CHINESE = Regex("[\u4e00-\u9fa5]+") //中文
val REGEX_HEXADECIMAL = Regex("[0-9A-Fa-f]+") //十六进制数

/**
 * 统计字符串中包含多少个子串
 *
 * @param string 子串
 * @return 子串个数
 */
fun String.containsCount(string: String): Int {
    var count = 0
    for (i in 0..length - string.length) {
        if (substring(i, i + string.length) == string) {
            ++count
        }
    }
    return count
}

/**
 * 判断字符串是否全为数字
 */
fun String.isDigits() = REGEX_DIGITS.matches(this)

/**
 * 判断字符串是否全为字母
 */
fun String.isLetters() = REGEX_LETTERS.matches(this)

/**
 * 判断字符串是否全为中文
 */
fun String.isChinese() = REGEX_CHINESE.matches(this)

/**
 * 判断字符串是否是十六进制数
 */
fun String.isHexadecimal() = REGEX_HEXADECIMAL.matches(this)

/**
 * 判断字符串是否包含数字
 */
fun String.hasDigits() = contains(REGEX_DIGITS)

/**
 * 判断字符串是否包含字母
 */
fun String.hasLetters() = contains(REGEX_LETTERS)

/**
 * 判断字符串是否包含中文
 */
fun String.hasChinese() = contains(REGEX_CHINESE)

/**
 * 移除字符串中的子串
 */
fun String.remove(string: String) = replace(string, "")

/**
 * 移除字符串中符合正则的子串
 */
fun String.remove(regex: Regex) = replace(regex, "")

/**
 * 移除字符串中第一个子串
 */
fun String.removeFirst(string: String) = replaceFirst(string, "")

/**
 * 移除字符串中第一个子串符合正则的子串
 */
fun String.removeFirst(regex: Regex) = replaceFirst(regex, "")

/**
 * 移除字符串中的数字
 */
fun String.removeDigits() = remove(REGEX_DIGITS)

/**
 * 移除字符串中的字母
 */
fun String.removeLetters() = remove(REGEX_LETTERS)

/**
 * 移除字符串中的中文
 */
fun String.removeChinese() = remove(REGEX_CHINESE)

/**
 * 附加字符串
 *
 * @param time   次数
 * @param strings 任意个字符串
 */
fun StringBuilder.appends(time: Int, vararg strings: Any) =
    apply { for (i in 1..time) strings.forEach { append(it) } }

/**
 * 替换字符串中的子串
 *
 * @param oldValue 旧字符串
 * @param newValue 新字符串
 */
fun StringBuilder.replace(oldValue: String, newValue: String) =
    toString().replace(oldValue, newValue)

/**
 * 替换字符串中第一个子串
 *
 * @param oldValue 旧字符串
 * @param newValue 新字符串
 */
fun StringBuilder.replaceFirst(oldValue: String, newValue: String) =
    toString().replaceFirst(oldValue, newValue)

/**
 * 移除字符串中的子串
 *
 * @param string 子串
 */
fun StringBuilder.remove(string: String) = replace(string, "")

/**
 * 移除字符串中符合正则的子串
 *
 * @param regex 正则
 */
fun StringBuilder.remove(regex: Regex) = replace(regex, "")

/**
 * 移除字符串中第一个子串
 *
 * @param string 子串
 */
fun StringBuilder.removeFirst(string: String) = replaceFirst(string, "")

/**
 * 移除字符串中第一个符合正则的子串
 *
 * @param regex 正则
 */
fun StringBuilder.removeFirst(regex: Regex) = replaceFirst(regex, "")
