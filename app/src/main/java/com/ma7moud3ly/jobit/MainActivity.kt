/*
* JOB IT - Job Email Creator
* By: Mahmoud Aly
* engma7moud3ly@gmail.com
*
*/
package com.ma7moud3ly.jobit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.ma7moud3ly.jobit.databinding.MainLayoutBinding
import com.ma7moud3ly.jobit.databinding.WelcomeLayoutBinding
import com.ma7moud3ly.ustore.UPref
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    private val PICKFILE_RESULT_CODE = 1001
    private lateinit var applicant: JobApplicant
    private lateinit var binding: MainLayoutBinding
    private lateinit var sendActivityIntent: Intent
    private lateinit var pref: UPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //attach send_layout
        binding = MainLayoutBinding.inflate(layoutInflater)
        //shared preferences management library
        pref = UPref(this)

        //check for welcome screen
        if (pref.get("welcome", true))
            welcome_screen();
        else setContentView(binding.root)


        //recover stored user data
        pref.get("resume_path", "")
        applicant = JobApplicant(
            pref.get("name", ""),
            pref.get("phone", ""),
            pref.get("job", ""),
            pref.get("resume_path", "")
        )
        binding.applicant = applicant

        binding.applyBtn.setOnClickListener(apply_now)
        binding.applyTextView.setOnClickListener(apply_now)
        binding.attachResumeBtn.setOnClickListener(attach_resume)
        binding.attachResumeTextView.setOnClickListener(attach_resume)

        sendActivityIntent = Intent(this, SendActivity::class.java)

    }

    //welcome screen binding and events
    private fun welcome_screen() {
        val welcome = WelcomeLayoutBinding.inflate(layoutInflater)
        welcome.goBtn.setOnClickListener(View.OnClickListener {
            setContentView(binding.root)
        })
        welcome.hideBtn.setOnClickListener(View.OnClickListener {
            pref.put("welcome", false)
            setContentView(binding.root)
        })
        setContentView(welcome.root)
    }

    //apply
    private val apply_now = View.OnClickListener {
        val name = applicant.name.get()
        val phone = applicant.phone.get()
        val job = applicant.job.get()
        val resume_path = applicant.resume_path.get()
        if (name.isNullOrEmpty() || phone.isNullOrEmpty() || job.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "Please Provide all you data!", Toast.LENGTH_SHORT)
                .show()
            return@OnClickListener
        }
        sendActivityIntent.putExtra("name", name)
        sendActivityIntent.putExtra("phone", phone)
        sendActivityIntent.putExtra("job", job)
        sendActivityIntent.putExtra("resume_path", resume_path)
        startActivity(sendActivityIntent)
    }

    //pickup a resume file
    private val attach_resume = View.OnClickListener {
        if (isStoragePermissionGranted()) {
            val intent = Intent(this, FilePickerActivity::class.java)
            intent.putExtra(
                FilePickerActivity.CONFIGS, Configurations.Builder()
                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowAudios(false)
                    .setShowVideos(false)
                    .setSingleClickSelection(true)
                    .setSuffixes("pdf")
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true)
                    .setSkipZeroSizeFiles(true)
                    .build()
            )
            startActivityForResult(intent, PICKFILE_RESULT_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val files: ArrayList<MediaFile> =
                data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
            var path = files[0].path
            val name: String = File(path).name
            applicant.resume.set(name)
            applicant.resume_path.set(path)
        }
    }

    //check if storage permission is granted
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
                false
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    //restore user data
    override fun onPause() {
        super.onPause()
        val pref = UPref(this)
        pref.put("name", applicant.name.get())
        pref.put("phone", applicant.phone.get())
        pref.put("job", applicant.job.get())
        pref.put("resume_path", applicant.resume_path.get())
    }


}