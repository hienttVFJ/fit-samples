package com.google.android.gms.fit.samples.stepcounterkotlin

import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activity_step_count.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Tran The Hien on 17,July,2020
 */
class WeightActivity : BaseFitnessActivity() {
    companion object {
        const val TAG = "WeightActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_count)
        tvTitle.text = "体重 - Weight"
        btnMonth.setOnClickListener {
            getWeights(Type.MONTH)
        }
        btnThreeMonth.setOnClickListener {
            getWeights(Type.THREE_MONTH)
        }
        btnYear.setOnClickListener {
            getWeights(Type.YEAR)
        }
    }


    private fun getWeights(type: Type) {
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

        val dataReadRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        var start = System.currentTimeMillis()
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .readData(dataReadRequest)
                .addOnSuccessListener { response ->
                    var time = System.currentTimeMillis() - start
                    printValueFloat(response, tvResult, time, "weight")
                }.addOnFailureListener { exception ->
                    Log.e(TAG, Log.getStackTraceString(exception))
                }
    }

}

