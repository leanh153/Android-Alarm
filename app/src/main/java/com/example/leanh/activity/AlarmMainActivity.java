package com.example.leanh.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.leanh.database.DataBaseManager;
import com.example.leanh.model.Alarm;
import com.example.leanh.receiver.AlarmReceiver;
import com.example.leanh.ultil.Constants;
import com.example.leanh.view.AlarmAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlarmMainActivity extends AppCompatActivity implements AlarmAdapter.CallBack {


    @BindView(R.id.openAdd)
    Button button;
    @BindView(R.id.alarmView)
    RecyclerView recyclerView;
    // this to manage data base
    private DataBaseManager dataBaseManager;
    // this to manage Alarm adapter like ArrayList
    private AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    // TODO: this initialize view for activity
    private void initView() {
        // set layout for recycle view
        //hasFixedSize true if adapter changes cannot affect the size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // this layout can be vertical or horizontal by change the second param
        // of LinearLayoutManager, and display up to down by set the third param false
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        importData();
        // set adapter for recycle view
        recyclerView.setAdapter(alarmAdapter);
    }

    @OnClick(R.id.openAdd)
    public void onOpenAddAlarm(View view) {
        //TODO: processing when user click on "+" button start new intent with request code
            Intent intent = new Intent(getApplicationContext(), AddAlarmActivity.class);
            intent.putExtra("screenType", "add");
            startActivityForResult(intent, Constants.REQUEST_ADD);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: Receive return data form Add or Edit activity to process add or edit alarm
        Alarm alarm;
        // check if request code and result code the same sent and return result
        if (requestCode == Constants.REQUEST_ADD && resultCode == RESULT_OK) {
            // get data and set a new alarm by function setAlarm
            alarm = (Alarm) data.getSerializableExtra("Alarm");
            // this check if the setting time already exist
            boolean containAlarm = checkAlarm(alarm);

            if (!containAlarm) {
                // add alarm to adapter
                alarmAdapter.add(alarm);
                // refresh adapter
                alarmAdapter.notifyDataSetChanged();
                // add it to database
                dataBaseManager.insert(alarm);
                // set new PendingIntent
                setAlarm(alarm, 0);
            }


        } else if (requestCode == Constants.REQUEST_EDIT && resultCode == RESULT_OK) {
            // get alarm object from AddAlarmActivity
            alarm = (Alarm) data.getSerializableExtra("Alarm");
            // this check if the setting time already exist
            boolean containAlarm = checkAlarm(alarm);

            if (!containAlarm) {
                // get alarm's position
                int position = data.getExtras().getInt("position");
                // update alarm at position
                alarmAdapter.updateAlarm(alarm, position);
                // this help refresh display
                alarmAdapter.notifyDataSetChanged();
                // update alarm to database
                dataBaseManager.update(alarm);
                // if alarm.getOnOff ==1 set alarm else not
                if (alarm.getOnOff() == 1) {
                    // get data and set a new alarm by function setAlarm with flag update current because
                    // this PendingIntent has already existed
                    setAlarm(alarm, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
            
        }
    }

    


    @Override
    public void onMenuAction(Alarm alarm, MenuItem item, int position) {
        // TODO: this function is a function of callBack interface which was created in alarm adapter
        //TODO: process edit or delete based on user option
        switch (item.getItemId()) {
            case R.id.edit:
                // send edit intent to AddAlarmActivity to edit this would return alarm and position
                Intent intent = new Intent(this, AddAlarmActivity.class);
                // put screenType to set display for AddAlarmActivity
                intent.putExtra("screenType", "edit");
                // put alarm need to edit
                intent.putExtra("AlarmEdit", alarm);
                // put alarm's position
                intent.putExtra("position", position);
                // this start AddAlarmActivity and change the screen and change detail return
                // one result on "onActivityResult" about
                startActivityForResult(intent, Constants.REQUEST_EDIT);
                break;

            case R.id.delete:
                // when user click edit remove alarm
                alarmAdapter.removeAlarm(position);
                // refresh
                alarmAdapter.notifyDataSetChanged();
                // get alarm id to delete alarm in database
                int alarmId = (int) alarm.getId();
                // delete alarm from database
                dataBaseManager.delete(alarmId);
                // cancel pendingIntent
                deleteCancel(alarm);
                break;
        }

    }


    @Override
    public void startAlarm(Alarm alarm) {
        //TODO: Xử lý truyền thông tin giờ hẹn cho AlarmReceiver
        // when toggle button click on set alarm on
        alarm.setOnOff(1);
        // update database
        dataBaseManager.update(alarm);
        // set PendingIntent for this alarm
        setAlarm(alarm, 0);

    }


    @Override
    public void cancelAlarm(Alarm timeItem) {
        //TODO: Gửi thông tin giờ hẹn cần hủy sang cho AlarmReceiver
        // when user click cancel toggle button
        // set alarm off
        timeItem.setOnOff(0);
        // update database
        dataBaseManager.update(timeItem);
        // cancel this Alarm PendingIntent
        deleteCancel(timeItem);
        // if alarm is triggered and ringing, send this alarm detail to AlarmReceiver
        // then AlarmReceiver send detail to service to stop music
        sendIntent(timeItem, Constants.OFF_INTENT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // TODO: this check if Alarm have already existed 
    private boolean checkAlarm(Alarm alarm) {
        boolean contain = false;
        for (Alarm alar : alarmAdapter.getmAlarms()) {
            if (alar.getHour_x() == alarm.getHour_x() && alar.getMinute_x() == alarm.getMinute_x())
                contain = true;
        }
        if (contain) {
            Toast.makeText(this, "You have already set this Alarm", Toast.LENGTH_SHORT).show();
        }
        return contain;
    }

    // TODO: import data from dataBase and create AlarmAdapter
    private void importData() {
        // if alarmAdapter null it's means data have not imported, yet or database is empty
        if (alarmAdapter == null) {
            // initialize database manager
            dataBaseManager = new DataBaseManager(this);
            // get Alarm ArrayList from database
            ArrayList<Alarm> arrayList = dataBaseManager.getAlarmList();
            // create Alarm adapter to display detail through RecyclerView
            alarmAdapter = new AlarmAdapter(arrayList, this);

        }
    }

    // TODO: this sends intent to AlarmReceiver
    private void sendIntent(Alarm alarm, String intentType) {
        // intent1 to send to AlarmReceiver
        Intent intent1 = new Intent(AlarmMainActivity.this, AlarmReceiver.class);
        // put intent type Constants.ADD_INTENT or Constants.OFF_INTENT
        intent1.putExtra("intentType", intentType);
        // put alarm'id to compare with pendingIntent'id in AlarmService
        intent1.putExtra("AlarmId", (int) alarm.getId());
        // this sent broadCast right a way
        sendBroadcast(intent1);
    }

    // TODO: this sets pendingIntent for alarm
    private void setAlarm(Alarm alarm, int flags) {
        // this set alarm based on TimePicker so we need to set Calendar like the
        // trigger time
        // get instant of Calendar
        Calendar myCalendar = Calendar.getInstance();
        Calendar calendar = (Calendar) myCalendar.clone();
        // set current hour for calendar
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour_x());
        // set current minute
        calendar.set(Calendar.MINUTE, alarm.getMinute_x());
        // set current second for calendar
        calendar.set(Calendar.SECOND, 0);
        // plus one day if the time set less than the the Calendar current time
        if (calendar.compareTo(myCalendar) <= 0) {
            calendar.add(Calendar.DATE, 1);
        }
        // get id of alarm and set for PendingIntent to multiply multiple PendingIntent for cancel
        // time, this also put into PendingIntent to compare with the cancel Alarm's id=
        int alarmId = (int) alarm.getId();
        // make intent to broadCast
        Intent intent = new Intent(AlarmMainActivity.this, AlarmReceiver.class);
        // put intent type to check which intent trigger add or cancel
        intent.putExtra("intentType", Constants.ADD_INTENT);
        // put id to intent
        intent.putExtra("PendingId", alarmId);
        // this pendingIntent include alarm id  to manage
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, alarmId,
                intent, flags);
        // create alarm manager ALARM_SERVICE
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Set alarm, at the trigger time "calandar.getTimeInMillis" this pendingIntent will be
        // sent to AlarmReceiver and then sent to alarm service to play music
        // this "AlarmManager.INTERVAL_DAY" mean this will set one new alarm at the trigger time
        // setInExactRepeating this may set alarm again and again also this may be not
        // trigger at the right time( at the first second start) but this will save the battery.
        // "AlarmManager.RTC_WAKEUP" allow this app wake device from idle time and the time
        // based on device time
        
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    // TODO:  this cancel pendingIntent of the alarm
    private void deleteCancel(Alarm alarm) {
        // if user click delete or cancel alarm the pendingIntent also to be canceled by AlarmManager
        // this PendingIntent is canceled based on alarm's ID was set for it, the pendingIntent is
        // going to be canceled must be same with the one was made based on it'id and intent also
        // where the context is.
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // get alarm id
        int alarmId = (int) alarm.getId();
        // create intent
        Intent intent = new Intent(AlarmMainActivity.this, AlarmReceiver.class);
        // this retrieve the pendingIntent was set
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, alarmId, intent, 0);
        // cancel this pendingIntent
        alarmManager.cancel(alarmIntent);
    }
}
