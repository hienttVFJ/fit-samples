package com.google.android.gms.fit.samples.stepcounterkotlin

import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activity_step_count.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Tran The Hien on 17,July,2020
 */
class StepActivity : BaseFitnessActivity() {
    companion object {
        const val TAG = "StepActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_count)
        tvTitle.text = "歩数 - Steps"
        btnMonth.setOnClickListener {
            getSteps(Type.MONTH)
        }
        btnThreeMonth.setOnClickListener {
            getSteps(Type.THREE_MONTH)
        }
        btnYear.setOnClickListener {
            getSteps(Type.YEAR)
        }
        btnInsert1.setOnClickListener {
            val cal = Calendar.getInstance()
            insertStep(cal)
        }
        btnInsert2.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -900)
            insertStep(cal)
        }
    }

    private fun insertStep(cal: Calendar) {
        for (x in 1..900) {
            cal.add(Calendar.DATE, -1)
            val calendar = cal.clone() as Calendar
//            calendar.set(Calendar.HOUR_OF_DAY, -2);
            val endTime = calendar.timeInMillis
            Log.e(TAG,"endTime " + calendar.time.toString())
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            val startTime = calendar.timeInMillis
            Log.e(TAG,"startTime " + calendar.time.toString())
            val dataSource = DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setStreamName("vf" + " - step count")
                    .setType(DataSource.TYPE_RAW)
                    .build()

            val stepCountDelta = (0..10000).random()
            val dataPoint = DataPoint.builder(dataSource)
                    .setField(Field.FIELD_STEPS, stepCountDelta)
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build()

            val dataSet = DataSet.builder(dataSource)
                    .add(dataPoint)
                    .build()

            Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .insertData(dataSet).addOnSuccessListener {
                        Log.e(TAG, "insert success")
                    }.addOnFailureListener {
                        Log.e(TAG, "insert failure")
                    }
        }
    }


    private fun getSteps(type: Type) {
        tvResult.text = ""
        val cal = Calendar.getInstance()
        cal.time = Date()

        val endTime = cal.timeInMillis
        when (type) {
            Type.MONTH -> cal.add(Calendar.DATE, -30)
            Type.THREE_MONTH -> cal.add(Calendar.DATE, -90)
            Type.YEAR -> cal.add(Calendar.DATE, -365)
        }
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        val startTime = cal.timeInMillis
        Log.e(TAG, "startTime $startTime")
        Log.e(TAG, "endTime $endTime")
        val dataSource = DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build()

        val dataReadRequest = DataReadRequest.Builder()
                .aggregate(dataSource, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        var start = System.currentTimeMillis()
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .readData(dataReadRequest)
                .addOnSuccessListener { response ->
                    var time = System.currentTimeMillis() - start
                    printValue(response, tvResult, time, "step")
                }.addOnFailureListener { exception ->
                    Log.e(TAG, Log.getStackTraceString(exception))
                }
    }

}

enum class Type {
    MONTH, THREE_MONTH, YEAR
}