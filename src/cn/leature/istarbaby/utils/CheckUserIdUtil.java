package cn.leature.istarbaby.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUserIdUtil {
	public static boolean isValidUserId(String userid) {
		Pattern p = Pattern
				.compile("^((14[5,7])|(13[0-9])|(15[^4,\\D])|(18[0-3,5-9]))\\d{8}$");
		Matcher matcher = p.matcher(userid);

		return matcher.matches();
	}
}
