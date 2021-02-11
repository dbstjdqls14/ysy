package com.example.ysy.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.ysy.R
import com.example.ysy.util.APIHelper
import com.example.ysy.util.LoginEvent
import com.example.ysy.signup.SnsSignup
import com.example.ysy.util.CloseEvent
import com.example.ysy.util.SaveSharedPreference
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import okhttp3.ConnectionPool
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    // 싱글톤
    private val helper = APIHelper.instance
    private val conPool: ConnectionPool = ConnectionPool()

    lateinit var closeBtn: ImageView
    lateinit var kakaoBtn: ImageView
    lateinit var naverBtn: ImageView
    lateinit var facebookBtn: ImageView
    lateinit var loginBtn: LinearLayout

    // 카카오 콜백 함수
    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            kakaoLoginErrorCode(error)
        } else if (token != null) {
            // 사용자 정보 요청
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(localClassName, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    // 유저 정보
                    var userInfo = HashMap<String, String?>()
                    userInfo["userId"] = user.id.toString()
                    userInfo["snsId"] = "0001"
                    userInfo["name"] = user.kakaoAccount?.profile?.nickname.toString()
                    userInfo["email"] = user.kakaoAccount?.email
                    userInfo["birthday"] = user.kakaoAccount?.birthday

                    signup(userInfo)
                }
            }
        }
    }

    val facebookCallback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            val request = GraphRequest.newMeRequest(result!!.accessToken) { `object`, _ ->
                try {
                    // 11/26/2000
                    var birthday: String = `object`.getString("birthday")
                    var splitBirthday = birthday.split("/")
                    birthday = splitBirthday[2] + splitBirthday[0] + splitBirthday[1]

                    var userInfo = HashMap<String, String?>()
                    userInfo["userId"] = `object`.getString("id")
                    userInfo["snsId"] = "0002"
                    userInfo["name"] = `object`.getString("name")
                    userInfo["email"] = `object`.getString("email")
                    userInfo["birthday"] = birthday

                    signup(userInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val parameters = Bundle()
            parameters.putString("fields", "name,email,id,birthday")

            request.parameters = parameters
            request.executeAsync()
        }

        override fun onCancel() {
            Log.v(localClassName, "CANCEL!")
        }

        override fun onError(error: FacebookException?) {
            Log.v(localClassName, error.toString())
        }
    }
    // 페이스북 콜백 관리자
    val facebookCallBackManger = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        EventBus.getDefault().register(this)
        addClickEvent()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Facebook CallBack 호출
        facebookCallBackManger.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    // EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseEvent(evt: CloseEvent) {
        EventBus.getDefault().post(LoginEvent(null))
        finish()
    }

    private fun addClickEvent() {
        closeBtn = findViewById(R.id.close_btn)
        loginBtn = findViewById(R.id.login_btn)
        kakaoBtn = findViewById(R.id.kakao_login)
        naverBtn = findViewById(R.id.naver_login)
        facebookBtn = findViewById(R.id.facebook_login)

        loginBtn.setOnClickListener(this)
        closeBtn.setOnClickListener(this)
        kakaoBtn.setOnClickListener(this)
        naverBtn.setOnClickListener(this)
        facebookBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // 로그인 팝업창 닫기
            R.id.close_btn -> finish()
            // 로그인
//            R.id.login_btn -> callUserInfo()
            R.id.kakao_login -> kakaoLogin()
            R.id.facebook_login -> facebookLogin()
        }
    }

    private fun kakaoLogin() {
        // 카카오톡 설치 여부 확인
        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            LoginClient.instance.loginWithKakaoTalk(this, callback = kakaoCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    private fun kakaoLoginErrorCode(error: Throwable) {
        when {
            error.toString() == ClientErrorCause.Cancelled.toString() -> {
                Toast.makeText(this, "캔슬!~!~~!~!", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.ServerError.toString() -> {
                Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
            }
            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun facebookLogin() {
        // 페이스북 콜백
        val loginManager = LoginManager.getInstance()

        loginManager.logInWithReadPermissions(
                this,
                listOf("public_profile", "email", "user_birthday")
        )
        loginManager.registerCallback(facebookCallBackManger, facebookCallback)
    }

    fun signup(data: HashMap<String, String?>) {
        val response = findUser(data["userId"].toString())
        val statusCode = response.get("statusCode")

        if (statusCode == 204) {
            var intent = Intent(this, SnsSignup::class.java)
            intent.putExtra("userId", data["userId"].toString())
            intent.putExtra("name", data["name"].toString())
            intent.putExtra("snsId", data["snsId"].toString())

            if (data["email"] != null) intent.putExtra("email", data["email"].toString())
            if (data["birthday"] != null) intent.putExtra("birthday", data["birthday"].toString())

            startActivity(intent)
        } else if (statusCode == 200) {
            // API 서버에서 token 가져와 창 닫기
            val token: String = getUserToken(data["userId"].toString())

            if (token != "NOT_TOKEN") {
                // 유저 토큰 저장
                val sharedPreference = SaveSharedPreference()
                sharedPreference.setToken(this, token)

                // Event Bus 보냄
                EventBus.getDefault().post(LoginEvent(null))
                finish()
            } else if (token == "NOT_TOKEN") {
                Log.v(localClassName, "NO USER")
            }
        }
    }

    private fun findUser(userId: String): JSONObject {
        val url = "http://10.0.2.2:80/FindUser.php"
        val req: HashMap<String, String> = HashMap()
        req.put("userId", userId)

        val data = helper.API_CALL_POST(url, req, conPool)

        return data
    }

    private fun getUserToken(userId: String): String {
        val url = "http://10.0.2.2:80/GetUserToken.php"
        val req: HashMap<String, String> = HashMap()
        req.put("userId", userId)

        val data = helper.API_CALL_POST(url, req, conPool)
        val statusCode = data.get("statusCode")
        var token = "NOT_TOKEN"

        if (statusCode == 200) {
            token = data.getJSONObject("results").getString("token")
        }

        return token
    }
}