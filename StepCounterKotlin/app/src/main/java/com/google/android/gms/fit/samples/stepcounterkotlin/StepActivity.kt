package com.google.android.gms.fit.samples.stepcounterkotlin

import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
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