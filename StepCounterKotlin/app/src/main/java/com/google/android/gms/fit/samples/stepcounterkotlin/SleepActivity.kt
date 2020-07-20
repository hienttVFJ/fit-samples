package com.google.android.gms.fit.samples.stepcounterkotlin

import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_step_count.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Tran The Hien on 17,July,2020
 */
class SleepActivity : BaseFitnessActivity() {
    companion object {
        const val TAG = "SleepActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_count)
        tvTitle.text = "睡眠時間 - Time of sleeping"
        btnMonth.setOnClickListener {
            getSleep(Type.MONTH)
        }
        btnThreeMonth.setOnClickListener {
            getSleep(Type.THREE_MONTH)
        }
        btnYear.setOnClickListener {
            getSleep(Type.YEAR)
        }
    }


    private fun getSleep(type: Type) {
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
        val request = SessionReadRequest.Builder()
                .readSessionsFromAllApps() // Activity segment data is required for details of the fine-
                // granularity sleep, if it is present.
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build()


        val sessionClient = Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
        val task: Task<SessionReadResponse> = sessionClient.readSession(request)

        task.addOnSuccessListener { response: SessionReadResponse ->
            // Filter the resulting list of sessions to just those that are sleep.
            val sleepSessions = response.sessions
                    .filter { s: Session -> s.activity == FitnessActivities.SLEEP }
            for (session in sleepSessions) {
                val result = java.lang.String.format("Sleep between %s and %s",
                        DATE_FORMAT_DETAIL.format(session.getStartTime(TimeUnit.MILLISECONDS)),
                        DATE_FORMAT_DETAIL.format(session.getEndTime(TimeUnit.MILLISECONDS)))
                Log.d("AppName", result)
                tvResult.append(result + "\n")

                // If the sleep session has finer granularity sub-components, extract them:
                val dataSets = response.getDataSet(session)
                for (dataSet in dataSets) {
                    for (point in dataSet.dataPoints) {
                        // The Activity defines whether this segment is light, deep, REM or awake.
                        val sleepStage = point.getValue(Field.FIELD_ACTIVITY).asActivity()
                        val start = DATE_FORMAT_DETAIL.format(point.getStartTime(TimeUnit.MILLISECONDS))
                        val end = DATE_FORMAT_DETAIL.format(point.getEndTime(TimeUnit.MILLISECONDS))
                        val result = String.format("\t* %s between %s and %s", sleepStage, start, end)
                        Log.d("AppName", result)
                        tvResult.append(result + "\n")
                    }
                }
                tvResult.append("\n")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, Log.getStackTraceString(exception))
        }

    }

}

