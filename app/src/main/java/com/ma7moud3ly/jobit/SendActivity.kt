/*
* JOB IT - Job Email Creator
* By: Mahmoud Aly
* engma7moud3ly@gmail.com
*
*/
package com.ma7moud3ly.jobit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ma7moud3ly.jobit.databinding.SendLayoutBinding


class SendActivity : AppCompatActivity() {
    private lateinit var applicant: JobApplicant
    private lateinit var binding: SendLayoutBinding
    private lateinit var templates: Array<String>
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SendLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        //read user data passed from main activity
        var bundle: Bundle? = intent.extras
        applicant = JobApplicant(
            bundle?.getString("name"), bundle?.getString("phone"),
            bundle?.getString("job"), bundle?.getString("resume_path")
        )
        binding.applicant = applicant

        //hide keyboard when activity launches..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //read emial boy templates from the resources
        templates = resources.getStringArray(R.array.email_templates)

        init_list(binding.templates, templates.size)
        //when
        binding.templates.onItemSelectedListener = template_select

        binding.send.setOnClickListener {

            if (applicant.company.get().isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please Provide Company Email",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            applicant.applyJob(activity)
        }

        binding.refresh.setOnClickListener {
            val i: Int = binding.templates.selectedItemPosition
            var template: String = edit_template(templates[i], applicant)
            applicant.body.set(template)
        }

    }

    private val template_select = object : OnItemSelectedListener {
        override fun onItemSelected(
            adapterView: AdapterView<*>?,
            view: View,
            i: Int,
            l: Long
        ) {
            var template: String = edit_template(templates[i], applicant)
            applicant.body.set(template)
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {
        }
    }

    private fun init_list(spinner: Spinner, n: Int) {
        val spinner = spinner;
        val templates = arrayOfNulls<String>(n)
        for (i in 0 until n) templates[i] = "template " + (i + 1)
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, templates)
        spinner.adapter = adapter
    }

    private fun edit_template(template: String, applicant: JobApplicant): String {
        var recruiter = applicant.recruiter.get()
        if (recruiter.equals(null)) recruiter = "Hiring Manager"

        return template.replace("[recruiter]", recruiter.toString())
            .replace("[name]", applicant.name.get().toString())
            .replace("[phone]", applicant.phone.get().toString())
            .replace("[job]", applicant.job.get().toString())
    }

    public fun my_resume(view: View) {
        Toast.makeText(applicationContext, applicant.resume.get(), Toast.LENGTH_SHORT).show()
    }


}