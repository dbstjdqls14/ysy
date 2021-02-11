package com.example.ysy.signup

import android.graphics.Rect
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ysy.R
import com.example.ysy.util.APIHelper
import com.example.ysy.util.CloseEvent
import com.example.ysy.util.SaveSharedPreference
import okhttp3.ConnectionPool
import org.greenrobot.eventbus.EventBus


class SnsSignup : AppCompatActivity() {
    private val helper = APIHelper.instance
    private val conPool: ConnectionPool = ConnectionPool()

    var userId: String = ""
    var snsId: String = ""
    var name: String = ""
    var email: String = ""
    var birthday: String = ""
    var originHeight: Int = -1
    var keyboardHeight: Int = 0
    var phoneValidation = false
    var birthdayValidation = false
    lateinit var phoneInput: EditText
    lateinit var phoneError: TextView
    lateinit var birthInput: EditText
    lateinit var birthError: TextView
    lateinit var signBtn: ConstraintLayout

    private val phoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            var text = phoneInput.text.toString()
            var regExp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$".toRegex()

            if (text == "") {
                phoneError.text = ""
                phoneValidation = false
            } else if (text.matches(regExp)) {
                phoneError.text = ""
                phoneValidation = true

                if (phoneValidation && birthdayValidation) slideUp(signBtn)
            } else {
                if (phoneValidation && birthdayValidation) slideDown(signBtn)

                phoneError.text = "올바른 휴대폰 번호를 입력해주세요."
                phoneValidation = false
            }
        }
    }

    private val birthTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            var text = birthInput.text.toString()
            var regExp = "^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()

            if (text == "") {
                birthError.text = ""
                birthdayValidation = false
            } else if (text.matches(regExp)) {
                birthError.text = ""
                birthdayValidation = true

                if (phoneValidation && birthdayValidation) slideUp(signBtn)
            } else {
                if (phoneValidation && birthdayValidation) slideDown(signBtn)

                birthError.text = "올바른 생년월일를 입력해주세요."
                birthdayValidation = false
            }
        }
    }

    private val signupBtnClickEvent = View.OnClickListener {
        var datas = HashMap<String, String>()
        var birthday: String = findViewById<EditText>(R.id.birthday_input).text.toString()
        var phone: String = findViewById<EditText>(R.id.phone_input).text.toString()

        datas["userId"] = userId
        datas["snsId"] = snsId
        datas["name"] = name
        datas["email"] = email
        datas["phone"] = phone.replace("-", "")
        datas["birthday"] = birthday

        val url = "http://10.0.2.2:80/CreateSnsUser.php"
        val data = helper.API_CALL_POST(url, datas, conPool)
        val statusCode = data.getInt("statusCode")

        if (statusCode == 200) {
            val token = data.getJSONObject("results").getString("token")
            val sharedPreference = SaveSharedPreference()
            sharedPreference.setToken(this, token)

            EventBus.getDefault().post(CloseEvent(null))

            finish()
        } else if (statusCode == 402) {
            // 회원가입 된 ID
            Log.v(localClassName, "Already USER")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_signup)

        signBtn = findViewById(R.id.sign_btn)
        signBtn.setOnClickListener(signupBtnClickEvent)

        var rootView: LinearLayout = findViewById<LinearLayout>(R.id.root_view)
        rootView.viewTreeObserver.addOnGlobalLayoutListener { getKeyboardHeight(rootView) }

        initUserInfo()
        addPhoneEventListener()
        addBirthdayListener()
    }

    private fun initUserInfo() {
        if (intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId").toString()
        }
        if (intent.hasExtra("snsId")) {
            snsId = intent.getStringExtra("snsId").toString()
        }
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name").toString()

            var nameInput = findViewById<EditText>(R.id.name_input)
            nameInput.setText(name)
            nameInput.isEnabled = false
        }
        if (intent.hasExtra("birthday")) {
            birthday = intent.getStringExtra("birthday").toString()

            if (birthday != null) {
                var birthdayInput = findViewById<EditText>(R.id.birthday_input)
                birthdayInput.setText(birthday)
                birthdayInput.isEnabled = false
                birthdayValidation = true
            }
        }
    }

    private fun addPhoneEventListener() {
        phoneInput = findViewById(R.id.phone_input)
        phoneError = findViewById(R.id.phone_error)

        phoneInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        phoneInput.addTextChangedListener(phoneTextWatcher)
    }

    private fun addBirthdayListener() {
        birthInput = findViewById(R.id.birthday_input)
        birthError = findViewById(R.id.birthday_error)

        birthInput.addTextChangedListener(birthTextWatcher)
    }
    
    // 회원가입 버튼 올라오는 애니메이션
    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE

        var animate = TranslateAnimation(
                0F,
                0F,
                view.height.toFloat() + keyboardHeight,
                0F
        )

        animate.duration = 700
        animate.fillAfter = true

        view.startAnimation(animate)
    }

    // 회원가입 버튼 내려가는 애니메이션
    private fun slideDown(view: View) {
        val animate = TranslateAnimation(
                0F,
                0F,
                0F,
                view.height.toFloat() + keyboardHeight
        )

        animate.duration = 700
        animate.fillAfter = true

        view.startAnimation(animate)

        view.visibility = View.GONE
    }
    
    // 키보드 높이 가져오기
    private fun getKeyboardHeight(view: View) {
        if (view.height > originHeight) {
            originHeight = view.height
        }

        var visibleFrameSize: Rect = Rect()
        view.getWindowVisibleDisplayFrame(visibleFrameSize)

        var visibleFrameHeight: Int = visibleFrameSize.bottom - visibleFrameSize.top
        keyboardHeight = originHeight - visibleFrameHeight
    }
}
