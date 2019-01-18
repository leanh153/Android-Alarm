package com.example.leanh.view;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.leanh.activity.R;
import com.example.leanh.model.Alarm;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmAdapter extends RecyclerView.Adapter {

    private ArrayList<Alarm> mAlarms;
    private CallBack mCallBack;

    public AlarmAdapter(ArrayList<Alarm> mAlarms, CallBack mCallBack) {
        this.mAlarms = mAlarms;
        this.mCallBack = mCallBack;
    }


    public interface CallBack {
        //Callback xử lý logic cho menu edit và delete
        void onMenuAction(Alarm object, MenuItem item, int position);

        //Callback xử lý logic start alarm
        void startAlarm(Alarm timeItem);

        //Callback xử lý logic cancel alarm
        void cancelAlarm(Alarm timeItem);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO: Khởi tạo alarm_item thông qua inflate.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.alarm_item, parent, false);
        return new TimeViewHolder(view, mCallBack);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TimeViewHolder) {
            TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
            timeViewHolder.bindView(mAlarms.get(position));

        }

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            PopupMenu.OnMenuItemClickListener {

        @BindView(R.id.time_Alarm)
        TextView time;  // this displays time alarm
        @BindView(R.id.alarm_Name)
        TextView title; // this displays alarm title
        @BindView(R.id.toggle_Alarm)
        ToggleButton toggleButton;  // toggle button to set and cancel alarm

        private TimeViewHolder(View itemView, CallBack CallBack) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // register callback
            mCallBack = CallBack;
            // setting long click, display menu for each item
            itemView.setOnCreateContextMenuListener(this);

        }

        // TODO: 6/15/2018 process onclick toggle button
        @OnClick(R.id.toggle_Alarm)
        public void onToggleClicked(View v) {
            boolean isChecked = toggleButton.isChecked();
            if (isChecked) {
                // change time' text color if toggle state have changed
                time.setTextColor(Color.rgb(155, 231, 174));
                // start alarm when toggle on, getPosition method support by RecyclerView
                mCallBack.startAlarm(mAlarms.get(getPosition()));
            } else {
                time.setTextColor(Color.rgb(155, 155, 155));
                // cancel alarm when toggle off
                mCallBack.cancelAlarm(mAlarms.get(getPosition()));
            }
        }


        // TODO: 6/15/2018  this bind view when from mAlarms arrayList
        private void bindView(Alarm alarm) {
            // set time's text
            time.setText(getStringFromTime(alarm));
            // set title's text
            title.setText(alarm.getAlarm_Name());
            // set color state for toggle button  and time's text
            int onOff = alarm.getOnOff();
            switch (onOff) {
                case 1:
                    toggleButton.setChecked(true);
                    // this set color for time' text green if toggle on on the bind view time
                    time.setTextColor(Color.rgb(155, 231, 174));
                    break;
                case 0:
                    toggleButton.setChecked(false);
                    // else set text color little black
                    time.setTextColor(Color.rgb(155, 155, 155));
                    break;
            }
        }

        // TODO: 6/15/2018 return string from Alarm
        private String getStringFromTime(Alarm alarm) {

            int minute = alarm.getMinute_x(); // minute of alarm
            int hourSource = alarm.getHour_x(); // hour of alarm
            int hour; // this hold time for time at AM, PM format
            String hour_x; // append 0 in front of hour if hour less than 10
            String minute_x; // append 0 in front of minute if minute less than 10
            String format;      // format for alarm


            if (hourSource == 0) {
                hour = hourSource + 12;
                format = "AM";
            } else if (hourSource == 12) {
                hour = hourSource;
                format = "PM";
            } else if (hourSource > 12) {
                hour = hourSource - 12;
                format = "PM";
            } else {
                hour = hourSource;
                format = "AM";
            }


            if (hour < 10) {
                hour_x = "0" + hour;
            } else {
                hour_x = "" + hour;
            }

            if (minute < 10) {
                minute_x = "0" + minute;
            } else {
                minute_x = "" + minute;
            }


            return hour_x + " : " + minute_x + "    " + format;
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();


        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            // on item clicked use this callback to check which function is called
            // delete or edit
            int position = getPosition();
            mCallBack.onMenuAction(mAlarms.get(position), menuItem, position);
            return false;
        }
    }


    // TODO: this add alarm to adapter
    public void add(Alarm alarm) {
        mAlarms.add(alarm);
    }


    // TODO:   add alarm to the position remove one at position

    public void updateAlarm(Alarm alarm, int position) {
        mAlarms.remove(position);
        mAlarms.add(position, alarm);
    }

    // TODO:  remove alarm from mAlarms ArrayList
    public void removeAlarm(int position) {
        //TODO: Xử lý logic xóa alarm khỏi adapter
        try {
            mAlarms.remove(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: get Alarm arraylist


    public ArrayList<Alarm> getmAlarms() {
        return mAlarms;
    }
}

