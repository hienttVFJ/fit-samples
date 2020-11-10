package com.google.android.gms.fit.samples.stepcounterkotlin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activity_body_fat.*
import kotlinx.android.synthetic.main.activity_body_fat.tvTitle
import kotlinx.android.synthetic.main.activity_step_count.*
import kotlinx.android.synthetic.main.activity_step_count.btnMonth
import kotlinx.android.synthetic.main.activity_step_count.btnThreeMonth
import kotlinx.android.synthetic.main.activity_step_count.btnYear
import kotlinx.android.synthetic.main.activity_step_count.tvResult
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Tran The Hien on 17,July,2020
 */
class BodyFatActivity : BaseFitnessActivity() {
    companion object {
        const val TAG = "BodyFatActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_fat)
        tvTitle.text = "体脂肪率 - Body fat percentage"
        btnMonth.setOnClickListener {
            getBodyFat(Type.MONTH)
        }
        btnThreeMonth.setOnClickListener {
            getBodyFat(Type.THREE_MONTH)
        }
        btnYear.setOnClickListener {
            getBodyFat(Type.YEAR)
        }
        btnAdd.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(this, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                showInputDialog(newDate)

            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }
    }

    private fun showInputDialog(calendar: Calendar) {
        val editText = EditText(this)
        editText.hint = "input body fat percent"
        editText.setInputType(InputType.TYPE_CLASS_NUMBER)
        AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
                    val value = editText.text.toString().toFloat()
                    pushBodyFat(calendar, value)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton -> })
                .show()
    }

    private fun pushBodyFat(calendar: Calendar, value: Float) {
        // Create a data source
        val dataSource: DataSource = DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setType(DataSource.TYPE_RAW)
                .build()
        val dataPoint = DataPoint.builder(dataSource).setFloatValues(value).setTimestamp(calendar.timeInMillis, TimeUnit.MILLISECONDS).build()
        val dataSet = DataSet.builder(dataSource).add(dataPoint).build()

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .insertData(dataSet)
                .addOnSuccessListener { response ->
                    Toast.makeText(this, "insert success", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "insert success")
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "insert fail", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, Log.getStackTraceString(exception))
                }
    }


    private fun getBodyFat(type: Type) {
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

        val dataSource = DataSource.Builder()
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setAppPackageName(this)
                .setType(DataSource.TYPE_RAW)
                .build()

        val dataReadRequest = DataReadRequest.Builder()
                .read(dataSource)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
//
//        val dataReadRequest = DataReadRequest.Builder()
//                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
//                .bucketByTime(1, TimeUnit.DAYS)
//                .enableServerQueries()
//                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                .build()
        var start = System.currentTimeMillis()
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .readData(dataReadRequest)
                .addOnSuccessListener { response ->
                    var time = System.currentTimeMillis() - start
                    printValueFloat(response, tvResult, time, "body fat ")
                }.addOnFailureListener { exception ->
                    Log.e(TAG, Log.getStackTraceString(exception))
                }
    }

}

