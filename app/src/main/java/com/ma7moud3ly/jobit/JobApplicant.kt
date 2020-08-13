/*
* JOB IT - Job Email Creator
* By: Mahmoud Aly
* engma7moud3ly@gmail.com
*
*/
package com.ma7moud3ly.jobit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import java.io.File

class JobApplicant {
    //observe input fields for two-way data binding
    var name = ObservableField<String>()
    var phone = ObservableField<String>()
    var job = ObservableField<String>()
    var resume_path = ObservableField<String>()
    var resume = ObservableField<String>()
    val company = ObservableField<String>()
    val recruiter = ObservableField<String>()
    val body = ObservableField<String>()

    constructor(
        name: String?,
        phone: String?,
        job: String?,
        resume_path: String?
    ) {
        this.name.set(name);
        this.phone.set(phone)
        this.job.set(job)
        this.resume_path.set(resume_path)
        this.resume.set(File(resume_path)?.name)
    }

    //take all job applicant data to mailing application to send
    fun applyJob(activity: Activity) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this.company.get()))
        intent.putExtra(Intent.EXTRA_SUBJECT, this.job.get())
        intent.putExtra(Intent.EXTRA_TEXT, this.body.get())
        intent.putExtra("body", this.body.get())
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + this.resume_path.get()))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(Intent.createChooser(intent, "Send email via..."))
            activity.finish()
        }


    }


}