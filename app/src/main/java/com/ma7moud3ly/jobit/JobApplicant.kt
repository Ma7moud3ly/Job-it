/*
* JOB IT - Job Email Creator
* By: Mahmoud Aly
* engma7moud3ly@gmail.com
*
*/
package com.ma7moud3ly.jobit

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.ObservableField
import java.io.File


class JobApplicant {
    //observe input fields for two-way data binding
    var name = ObservableField<String>()
    var phone = ObservableField<String>()
    var job = ObservableField<String>()
    var resume = ObservableField<String>()
    val company = ObservableField<String>()
    val recruiter = ObservableField<String>()
    val body = ObservableField<String>()

    constructor(
        name: String?,
        phone: String?,
        job: String?,
        resume: String?
    ) {
        this.name.set(name);
        this.phone.set(phone)
        this.job.set(job)
        this.resume.set(resume)
    }

   /* //take all job applicant data to mailing application to send
    fun applyJob(activity: Activity, resume_name: String?) {
        val file = File(App.resume_path,resume_name)
        val uri = FileProvider.getUriForFile(activity, "com.ma7moud3ly.jobit.fileprovider", file)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this.company.get()))
        intent.putExtra(Intent.EXTRA_SUBJECT, this.job.get())
        intent.putExtra(Intent.EXTRA_TEXT, this.body.get())
        intent.putExtra("body", this.body.get())
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(Intent.createChooser(intent, "Send email via..."))
        }
    }*/

    fun applyJob(activity: Activity, resume_name: String?) {
        val file = File(App.resume_path,resume_name)
        val uri = FileProvider.getUriForFile(activity, "com.ma7moud3ly.jobit.fileprovider", file)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(this.company.get()))
        intent.putExtra(Intent.EXTRA_SUBJECT, this.job.get())
        intent.putExtra(Intent.EXTRA_TEXT, this.body.get())
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            activity.startActivity(
                Intent.createChooser(
                    intent,
                    activity.getResources().getString(R.string.app_name)
                )
            )
        } catch (e: Exception) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }


}