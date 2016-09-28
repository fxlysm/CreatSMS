package com.fxly.creatsms.language;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class StaticFunction {

	public static final String TAG = "setting_data";
	public static boolean SYSTEM_CHANGE_LANGUAGE_FLAG = false;
	public static final String SYSTEM_LOCALE_LANGUAGUE_STRING = "system_locale_languague_string";
	public static final String SYSTEM_LOCALE_COUNTRY_STRING = "system_locale_country_string";
	public static final String ENAME[] = { "zh", "en", };

	public static Locale getSystemLacate(Context context) {
		SharedPreferences sharedPreferences = getCurrentSharedPreferences(context);
		String str = sharedPreferences.getString(
				SYSTEM_LOCALE_LANGUAGUE_STRING, "no_languague");
		String strc = sharedPreferences.getString(SYSTEM_LOCALE_COUNTRY_STRING,
				"");
		if ("no_languague".equals(str)) {
			Locale l = Locale.getDefault();
			String def = "en";
			for (int i = 0; i < ENAME.length; i++) {
				if (ENAME[i].equals(l.getLanguage())) {
					def = ENAME[i];
					break;
				}
			}
			Locale nLocale = null;
			if ("zh".equals(def)) {
				if ("CN".equals(l.getCountry())) {
					nLocale = Locale.SIMPLIFIED_CHINESE;
				} else {
					nLocale = Locale.TRADITIONAL_CHINESE;
				}
			} else {
				nLocale = new Locale(def);
			}
			setSystemLacate(context, nLocale);
			return nLocale;
		}
		return new Locale(str, strc);
	}
	public static void setSystemLacate(Context context, Locale locale) {
		SharedPreferences sharedPreferences = getCurrentSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(SYSTEM_LOCALE_LANGUAGUE_STRING, locale.getLanguage());
		editor.putString(SYSTEM_LOCALE_COUNTRY_STRING, locale.getCountry());
		editor.commit();

	}

	public static SharedPreferences getCurrentSharedPreferences(Context context) {
		return context.getSharedPreferences(TAG, context.MODE_PRIVATE);
	}



}
