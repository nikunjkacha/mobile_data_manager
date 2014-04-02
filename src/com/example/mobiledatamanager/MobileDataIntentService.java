package com.example.mobiledatamanager;

import java.util.Calendar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MobileDataIntentService extends IntentService
{

	static String TAG = "MobileDataIntentService";
	private Calendar start;
	private Calendar end;
	private static Boolean disabledByThis = false;
	
	public MobileDataIntentService()
	{
		super(TAG);
		start = Calendar.getInstance();
		int startHourOfDay = MainActivity.preference.getInt("START_HOUR_OF_DAY", 0);
		int startMinute = MainActivity.preference.getInt("START_MINUTE", 0);
		int endHourOfDay = MainActivity.preference.getInt("END_HOUR_OF_DAY", 0);
		int endMinute = MainActivity.preference.getInt("END_MINUTE", 0);
		Log.d(TAG, "start " + startHourOfDay + ":" + startMinute);
		Log.d(TAG, "end " + endHourOfDay + ":" + endMinute);
		start.set(Calendar.HOUR_OF_DAY, startHourOfDay);
		start.set(Calendar.MINUTE, startMinute);
		start.set(Calendar.SECOND, 0);
		end = Calendar.getInstance();
		end.set(Calendar.HOUR_OF_DAY, endHourOfDay);
		end.set(Calendar.MINUTE, endMinute);
		end.set(Calendar.SECOND, 0);
	}

	@Override
	protected void onHandleIntent(Intent arg0)
	{
		Boolean b = MainActivity.getMobileDataEnabled();
		Calendar now = Calendar.getInstance();
		Log.d(TAG, "service handled. Mobile data " + b.toString());
		Log.d(TAG, "disabled by This: " + disabledByThis);
		if (b && now.after(start) && now.before(end) &&
		    isDayOfWeekChecked(now.get(Calendar.DAY_OF_WEEK))) {
			MainActivity.setMobileDataEnabled(false);
			disabledByThis = true;
			Log.d(TAG, "Mobile data disabled by Service");
		} else if (!b && disabledByThis && (now.before(start) ||
				   now.after(end))) {
			MainActivity.setMobileDataEnabled(true);
			disabledByThis = false;
			Log.d(TAG, "Mobile data Enabled by Service");
		}
	}
	
	private boolean isDayOfWeekChecked(int d)
	{
		String dow = "";
		
		switch (d) {
		case Calendar.SUNDAY:
			dow = "SUNDAY";
			break;
		case Calendar.MONDAY:
			dow = "MONDAY";
			break;
		case Calendar.TUESDAY:
			dow = "TUESDAY";
			break;
		case Calendar.WEDNESDAY:
			dow = "WEDNESDAY";
			break;
		case Calendar.THURSDAY:
			dow = "THURSDAY";
			break;
		case Calendar.FRIDAY:
			dow = "FRIDAY";
			break;
		case Calendar.SATURDAY:
			dow = "SATURDAY";
			break;
		default:
			break;
		}
		return MainActivity.preference.getBoolean(dow, false);
	}
}
