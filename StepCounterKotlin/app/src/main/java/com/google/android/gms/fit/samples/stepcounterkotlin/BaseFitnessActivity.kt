package com.google.android.gms.fit.samples.stepcounterkotlin

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.fitness.result.DataReadResponse
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * Created by Tran The Hien on 17,July,2020
 */
abstract class BaseFitnessActivity : AppCompatActivity() {
    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val DATE_FORMAT_DETAIL = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun printValue(dataReadResult: DataReadResponse, textView: TextView, time: Long, type: String) {
        textView.text = ""
        textView.append("time $time ms \n")
        Log.e("buckets",dataReadResult.buckets.toString())
        if (dataReadResult.buckets.size > 0) {
            for (bucket in dataReadResult.buckets) {
                val date: String = DATE_FORMAT.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
                val dataSets = bucket.dataSets
                var steps = 0
                for (dataSet in dataSets) {
                    for (dp in dataSet.dataPoints) {
                        for (field in dp.dataType.fields) {
                            steps += dp.getValue(field).asInt()
                        }
                    }
                }
                textView.append("date $date     $type $steps \n")
            }
        }
    }


    fun printValueFloat(dataReadResult: DataReadResponse, textView: TextView, time: Long, type: String) {
        textView.text = ""
        textView.append("time $time ms \n")
        if (dataReadResult.buckets.size > 0) {
            for (bucket in dataReadResult.buckets) {
                val date: String = DATE_FORMAT.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
                val dateDetail: String = DATE_FORMAT_DETAIL.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
                val dataSets = bucket.dataSets
                var weight = 0f
                for (dataSet in dataSets) {
                    Log.e("xxx", "dateTime $dateDetail")
                    for (dp in dataSet.dataPoints) {
                        for (field in dp.dataType.fields) {
                            weight = dp.getValue(field).asFloat()
                            Log.e("xxx", "value $weight")
                        }
                    }
                }
                textView.append("date $date     $type $weight \n")
            }
        }
    }
}