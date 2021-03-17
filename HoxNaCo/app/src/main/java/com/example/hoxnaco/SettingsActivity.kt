package com.example.hoxnaco

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settingsToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left)
        }
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {   // 앱 바 이벤트 처리
        when (item.itemId) {
            R.id.backSettings -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        mail.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.setType("plain/text")
            val address = arrayOf<String>("jhaenim2002@daum.net")
            email.putExtra(Intent.EXTRA_EMAIL, address)
            email.putExtra(Intent.EXTRA_SUBJECT, "'혹나코' 어플 건의사항");
            email.putExtra(Intent.EXTRA_TEXT, "앱 서비스에 관련하여 건의 할 사항이 있으시면 언제든지 문의 주세요!");
            startActivity(email);
        }

        api_inform.setOnClickListener {
            val fragmentTransaction = supportFragmentManager?.beginTransaction()
            val fragment = APIInformFragment()
            fragmentTransaction?.replace(R.id.settingMenus, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit();
        }

        my_inform.setOnClickListener {
            val fragmentTransaction = supportFragmentManager?.beginTransaction()
            val fragment = MyInformFragment()
            fragmentTransaction?.replace(R.id.settingMenus, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit();
        }
    }
}