package pt.joseluisvf.glucopath.util

import java.util.regex.{Matcher, Pattern}

object TextUtil {
  def countOcurrencesTextIn(pattern: String, text: String): Int = {
    val findDays: Pattern = Pattern.compile(pattern)
    val matcher: Matcher = findDays.matcher(text)
    var count: Int = 0
    while (matcher.find()) count += 1
    count
  }
}
