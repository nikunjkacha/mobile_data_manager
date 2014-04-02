package com.example.mobiledatamanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.mobiledatamanager.R.id;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	protected String TAG = "MobileDataManager";
	protected static SharedPreferences preference;
	protected SharedPreferences.Editor editor;
	protected static boolean mobileDataManagerEnabled = false;
	
	private static Method setMobileDataMethod = null;
	private static Method getMobileDataMethod = null;
	private static ConnectivityManager connectivityManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		preference = getSharedPreferences("mobile_data_preference",
										  Activity.MODE_PRIVATE);
		editor = preference.edit();
	
		findMobileDataEnabledMethod();

		setContentView(R.layout.activity_main);

		/*
		 * Mobile data enable/disable
		 */
		Switch switchMobileData = (Switch)findViewById(R.id.switch_mobile_data);
		switchMobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton b, boolean isChecked) {
				Toast.makeText(MainActivity.this, "set mobile data " + isChecked,
							   Toast.LENGTH_SHORT).show();
				Log.d(TAG, "set mobile data " + isChecked);
				setMobileDataEnabled(isChecked);
			}
		});
		
		
		/*
		 * Set TimePicker for Calendar
		 */
		Button buttonSetStartTime = (Button) findViewById(R.id.button_set_start_time);
		setTimeToTextView(R.id.text_start_time,
						  preference.getInt("START_HOUR_OF_DAY", 0),
						  preference.getInt("START_MINUTE", 0));
		buttonSetStartTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						editor.putInt("START_HOUR_OF_DAY", hourOfDay);
						editor.putInt("START_MINUTE", minute);
						editor.commit();
						setTimeToTextView(R.id.text_start_time, hourOfDay, minute);
					}
				},
				preference.getInt("START_HOUR_OF_DAY", 0),
				preference.getInt("START_MINUTE", 0), true).show();
			}
		});
		
		Button buttonSetEndTime = (Button)findViewById(R.id.button_set_end_time);
		setTimeToTextView(R.id.text_end_time,
						  preference.getInt("END_HOUR_OF_DAY", 0),
						  preference.getInt("END_MINUTE", 0));
		buttonSetEndTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						editor.putInt("END_HOUR_OF_DAY", hourOfDay);
						editor.putInt("END_MINUTE", minute);
						editor.commit();
						setTimeToTextView(R.id.text_end_time, hourOfDay, minute);
					}
				},
				preference.getInt("END_HOUR_OF_DAY", 0),
				preference.getInt("END_MINUTE", 0), true).show();
			}
		});

		
		/*
		 * Mobile data manager start/stop
		 */
		Switch switchMobileDataManager =
				(Switch)findViewById(R.id.switch_mobile_data_manager);
		switchMobileDataManager.setChecked(mobileDataManagerEnabled);
		switchMobileDataManager.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton b, boolean isChecked) {
				if (isChecked) {
					scheduleService();
				} else {
					cancelService();
				}
				Toast.makeText(MainActivity.this, "mobile data manager " +
							   isChecked, Toast.LENGTH_SHORT).show();
				Log.d(TAG, "mobile data manager " + isChecked);
				mobileDataManagerEnabled = isChecked;
			}
		});
		
		
		/* 
		 * CheckBoxes for day of week
		 */		
		CheckBox checkBoxMonday = (CheckBox)findViewById(id.checkBoxMonday);
		checkBoxMonday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("MONDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
		
		CheckBox checkBoxTuesday = (CheckBox)findViewById(id.checkBoxTuesday);
		checkBoxTuesday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("TUESDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
		
		CheckBox checkBoxWednesday = (CheckBox)findViewById(id.checkBoxWednesday);
		checkBoxWednesday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("WEDNESDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
		
		CheckBox checkBoxThursday = (CheckBox)findViewById(id.checkBoxThursday);
		checkBoxThursday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("THURSDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});

		CheckBox checkBoxFriday = (CheckBox)findViewById(id.checkBoxFriday);
		checkBoxFriday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("FRIDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
		
		CheckBox checkBoxSaturday = (CheckBox)findViewById(id.checkBoxSaturday);
		checkBoxSaturday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("SATURDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
		
		CheckBox checkBoxSunday = (CheckBox)findViewById(id.checkBoxSunday);
		checkBoxSunday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cbox = (CheckBox)v;
				editor.putBoolean("SUNDAY", cbox.isChecked());
				editor.commit();
				Log.d(TAG, cbox.getText() + " set to " + cbox.isChecked());
			}
		});
	}


	@Override
	protected void onResume()
	{
		super.onResume();
		Switch switchMobileData = (Switch)findViewById(R.id.switch_mobile_data);
		switchMobileData.setChecked(getMobileDataEnabled());

		Switch switchMobileDataManager =
				(Switch)findViewById(R.id.switch_mobile_data_manager);
		switchMobileDataManager.setChecked(mobileDataManagerEnabled);

		CheckBox checkBox = (CheckBox)findViewById(id.checkBoxMonday);
		checkBox.setChecked(preference.getBoolean("MONDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxTuesday);
		checkBox.setChecked(preference.getBoolean("TUESDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxWednesday);
		checkBox.setChecked(preference.getBoolean("WEDNESDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxThursday);
		checkBox.setChecked(preference.getBoolean("THURSDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxFriday);
		checkBox.setChecked(preference.getBoolean("FRIDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxSaturday);
		checkBox.setChecked(preference.getBoolean("SATURDAY", false));
		checkBox = (CheckBox)findViewById(id.checkBoxSunday);
		checkBox.setChecked(preference.getBoolean("SUNDAY", false));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setTimeToTextView(int textViewId, int hour, int min)
	{
		TextView textView = (TextView)findViewById(textViewId);
		textView.setText(String.format("%02d:%02d", hour, min));
	}

	protected void scheduleService()
	{
		Log.d(TAG, "service scheduled");
		Context context = getBaseContext();
		Intent intent = new Intent(context, MobileDataIntentService.class);
		PendingIntent pendingIntent = 
				PendingIntent.getService(context, -1, intent,
										 PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager =
				(AlarmManager)context.getSystemService(ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
										 System.currentTimeMillis(),
										 15000, pendingIntent);
	}
	
	protected void cancelService()
	{
		Log.d(TAG, "service cancelled");
		Context context = getBaseContext();
		Intent intent = new Intent(context, MobileDataIntentService.class);
		PendingIntent pendingIntent =
				PendingIntent.getService(context, -1, intent,
										 PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager =
				(AlarmManager)context.getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	protected void findMobileDataEnabledMethod()
	{
		connectivityManager =
				(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

	    Class<?> clazz = null;
	    try {
	        clazz = Class.forName(connectivityManager.getClass().getName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        return;
	    }
	    try {
	        Method[] available_methods = clazz.getDeclaredMethods();
	        for (Method m : available_methods) {
	            if (m.getName().contains("setMobileDataEnabled")) {
	            	setMobileDataMethod = m;
	            }
	            if (m.getName().contains("getMobileDataEnabled")) {
	            	getMobileDataMethod = m;
	            }
	        }
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    }
	}

	protected static void setMobileDataEnabled(boolean toBeEnabled)
	{
		try {
			setMobileDataMethod.invoke(connectivityManager, toBeEnabled);
		} catch (InvocationTargetException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    }
	}

	protected static void setMobileDataEnabledWithUpdateView(boolean toBeEnabled)
	{
		setMobileDataEnabled(toBeEnabled);
	}

	protected static Boolean getMobileDataEnabled()
	{
	    Boolean state = false;
		try {
	        state = (Boolean)getMobileDataMethod.invoke(connectivityManager);
		} catch (InvocationTargetException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    }
		return state;
	}
}
