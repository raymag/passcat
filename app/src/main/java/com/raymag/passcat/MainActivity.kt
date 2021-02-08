package com.raymag.passcat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gen_pass.isEnabled = false

        minus_btn.setOnClickListener {decreaseSize()}
        plus_btn.setOnClickListener {increaseSize()}

        get_password_btn.setOnClickListener {genPassword()}
        copy_btn.setOnClickListener {copy()}
        help_btn.setOnClickListener {help()}
    }

    private fun decreaseSize() {
        val currentSize: Int = size_label.text.toString().toInt()
        if (currentSize > 1) {
            size_label.text = (currentSize - 1).toString()
        }
    }

    private  fun increaseSize() {
        val currentSize: Int = size_label.text.toString().toInt()
        if (currentSize < 32) {
            size_label.text = (currentSize + 1).toString()
        }
    }

    private fun hashString(root: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(root.toByteArray())
        return bytes.fold("", {str, it -> str + "%02x".format(it)})
    }

    private fun genPassword() {
        if (root_word.text.toString().isEmpty() || signature.text.toString().isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (root_word.text.toString().length < 3) {
            Toast.makeText(this, "Root Word must be at least 3 characters wide", Toast.LENGTH_SHORT).show()
            return
        }
        val hash: String = hashString(root_word.text.toString())
        val size: Int = size_label.text.toString().toInt()

        val hint: String =  root_word.text.toString().substring(0, 3)
        val sign: String = signature.text.toString()
        val hashpass: String = hash.substring(0, size) + hash.substring(hash.length - size, hash.length)

        val pass: String = hint + sign + hashpass

        gen_pass.setText(pass)
        Toast.makeText(this, "Password Generated", Toast.LENGTH_SHORT).show()
    }

    private fun copy() {
        val textToCopy = gen_pass.text.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Passcat Password", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun help() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }
}