/*
* JOB IT - Job Email Creator
* By: Mahmoud Aly
* engma7moud3ly@gmail.com
*
*/
package com.ma7moud3ly.jobit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.ma7moud3ly.jobit.databinding.MainLayoutBinding
import com.ma7moud3ly.jobit.databinding.WelcomeLayoutBinding
import com.ma7moud3ly.ustore.UPref
import kotlinx.android.synthetic.main.welcome_layout.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private val PICK_FILE = 2
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
        applicant = JobApplicant(
            pref.get("name", ""),
            pref.get("phone", ""),
            pref.get("job", ""),
            pref.get("resume", "")
        )
        binding.applicant = applicant

        binding.applyBtn.setOnClickListener(apply_now)
        binding.applyTextView.setOnClickListener(apply_now)
        binding.attachResumeBtn.setOnClickListener(attach_resume)
        binding.attachResumeTextView.setOnClickListener(attach_resume)
        binding.resumeName.setOnClickListener { renameResume(this) }

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
        val resume = applicant.resume.get()
        if (name.isNullOrEmpty() || phone.isNullOrEmpty() || job.isNullOrEmpty() || resume == null) {
            Toast.makeText(applicationContext, "Please Provide all you data!", Toast.LENGTH_SHORT)
                .show()
            return@OnClickListener
        }
        sendActivityIntent.putExtra("name", name)
        sendActivityIntent.putExtra("phone", phone)
        sendActivityIntent.putExtra("job", job)
        sendActivityIntent.putExtra("resume", resume)
        startActivity(sendActivityIntent)
    }


    //pickup a resume file
    private val attach_resume = View.OnClickListener {
        openFile()
    }

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        // if (scripts != null) intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, scripts)
        startActivityForResult(intent, PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            val old_resume = File(App.resume_path, applicant.resume.get())
            if (old_resume.exists()) old_resume.delete()
            applicant.resume.set(null);
            val uri = data?.getData()
            val file = copyResume(uri)
            if (file != null) {
                applicant.resume.set(file.name);

            }
        }
    }

    fun renameResume(context: Context) {
        if (applicant.resume == null || applicant.resume.get() == "") return
        val builder = AlertDialog.Builder(context)
        val title = EditText(context)
        title.setText(applicant.resume.get())
        title.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_TEXT
        title.hint = "resume name.."
        title.setSelectAllOnFocus(true)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(title)
        builder.setView(layout)
        builder.setMessage("rename your resume..")
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builder.setPositiveButton("Ok", null)
        val dialog = builder.create()
        dialog.show()
        val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positive.setOnClickListener {
            try {
                val new_name = File(App.resume_path, title.text.toString())
                val old_name = File(App.resume_path, applicant.resume.get())
                old_name.renameTo(new_name)
                applicant.resume.set(title.text.toString())
                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(context, "invalid name", Toast.LENGTH_LONG).show()
                title.selectAll()
                e.printStackTrace()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        val pref = UPref(this)
        pref.put("name", applicant.name.get())
        pref.put("phone", applicant.phone.get())
        pref.put("job", applicant.job.get())
        pref.put("resume", applicant.resume.get())
    }

    private fun copyResume(uri: Uri?): File? {
        try {
            val inputPFD = contentResolver.openFileDescriptor(uri!!, "r")
            val fd = inputPFD?.fileDescriptor
            var name = File(uri.path).name
            if (!name.endsWith(".pdf")) name += ".pdf"
            val dest = File(App.resume_path, name)
            if (!dest.parentFile.exists()) dest.parentFile.mkdirs()
            FileInputStream(fd).use { inp ->
                FileOutputStream(dest).use { out ->
                    val buf = ByteArray(1024)
                    var len: Int = 0
                    while (inp.read(buf).also({ len = it }) > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
            return dest
        } catch (e: Exception) {
            e.printStackTrace()
            return null;
        }
    }
}