package com.example.aop_part3_chapter03

import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: 뷰를 초기화 해주기
        initOnOffButton()
        initChangeAlarmTimeButton()

        //TODO: 데이터 가져오기
        val model = fetchDataFromSharedPreferences()
        renderView(model)

        //TODO: 가져온데이터 뷰에 그려주기
    }

    private fun initOnOffButton() {
        val onOffButton: Button = findViewById(R.id.onOffButton)
        onOffButton.setOnClickListener {
            //TODO: 저장한 데이터를 확인을 한다.

            //TODO: 온오프에 따라 작업을 처리한다.

            //off-> 알람제거 / on-> 알람 등록
        }
    }

    private fun initChangeAlarmTimeButton() {
        val changeAlarmtimeButton: Button = findViewById(R.id.changeAlarmtimeButton)
        changeAlarmtimeButton.setOnClickListener {
            //TODO: 현재시간을 가져온다.
            val calendar = Calendar.getInstance()

            //TODO: TimePickerDialog 이용해서 시간 설정하고, 시간을 가져와서 저장
            TimePickerDialog(this, { picker, hour, minute ->

                val model = saveAlarmModel(hour, minute, false)
                //TODO: 뷰를 업데이트 한다.
                renderView(model)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        //TODO: 기존 알람 삭제
    }

    private fun saveAlarmModel(
            hour: Int,
            minute : Int,
            onOff: Boolean
    ) : AlarmDisplayModel{
        val model = AlarmDisplayModel(
                hour = hour,
                minute = minute,
                onOff = onOff
        )

        val sharedPreferences = getSharedPreferences("time", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            //TODO: With 함수를 이용해서 같이 작업할 수 있는 영역을 묶어서 작업
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }
        return model
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        val timeDBValue = sharedPreferences.getString("ALARM_KEY", "9:30") ?:"9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
                hour = alarmData[0].toInt(),
                minute = alarmData[1].toInt(),
                onOff = onOffDBValue
        )

        //보정? 예외처리
//        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
//        if((pendingIntent == null) && alarmModel.onOff) {
//            //TODO: 알람은 꺼져 있는데, 데이터는 켜져있는 경우
//            alarmModel.onOff = false
//        } else if ((pendingIntent != null) && alarmModel.onOff.not()) {
//            //TODO: 알람은 켜져 있는데, 데이터는 꺼져 있는 경우 -> 알람 취소
//            pendingIntent.cancel()
//        }
        return alarmModel
    }

    private fun renderView(model : AlarmDisplayModel) {
        findViewById<TextView>(R.id.ampmTextView).apply{
            text = model.ampmText
        }
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model
        }
    }

    companion object {
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val SHARED_PREFERENCE_NAME = "time"
        private const val ALARM_REUEST_CODE = 1000
    }
}