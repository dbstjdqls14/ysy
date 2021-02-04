package com.example.ysy.mypage

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updateLayoutParams
import com.example.ysy.R
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.net.URL
import android.view.View.VISIBLE as VISIBLE1


class MypageActivity : AppCompatActivity() {

    var check = 0;
    var testData = "dbstjdqls14";
    var login_id = "a";
    var login_pw = "b";

//    lateinit var Att_area: TextView;
//    lateinit var question_log: TextView;
//    lateinit var notice: TextView;
//    lateinit var app_version: TextView;
//    lateinit var service: TextView;
//    lateinit var logoutText: TextView;
//    lateinit var user1_nameText: TextView;
//    lateinit var user1_dateText: TextView;
//    lateinit var user2: LinearLayout;
//    lateinit var logout_btn: LinearLayout;
//    lateinit var Att_area_linear: LinearLayout;

    var thiss = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        var Att_area = findViewById<TextView>(R.id.Att_area);
        var question_log = findViewById<TextView>(R.id.question_log);
        var notice = findViewById<TextView>(R.id.notice);
        var app_version = findViewById<TextView>(R.id.app_version);
        var service = findViewById<TextView>(R.id.service);
        var logoutText = findViewById<TextView>(R.id.user1_logoutText);
        var user1_nameText = findViewById<TextView>(R.id.user1_nameText);
        var user1_dateText = findViewById<TextView>(R.id.user1_dateText);
        var user2 = findViewById<LinearLayout>(R.id.userBox2);
        var logout_btn = findViewById<LinearLayout>(R.id.logout_btn);
        var Att_area_linear = findViewById<LinearLayout>(R.id.linearLayout4);

        Att_area.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Att_area.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    }
                    MotionEvent.ACTION_UP -> {
                        Att_area.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                return true //or false
            }
        })
        question_log.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        question_log.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    }
                    MotionEvent.ACTION_UP -> {
                        question_log.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                return true //or false
            }
        })
        notice.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        notice.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    }
                    MotionEvent.ACTION_UP -> {
                        notice.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                return true //or false
            }
        })
        app_version.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        app_version.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    }
                    MotionEvent.ACTION_UP -> {
                        app_version.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                return true //or false
            }
        })
        service.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        service.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    }
                    MotionEvent.ACTION_UP -> {
                        service.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                return true //or false
            }
        })
        logoutText.setOnClickListener{

            var builder = AlertDialog.Builder(this)
            builder.setTitle("커스텀 다이얼로그")
            builder.setIcon(R.mipmap.ic_launcher)

            var v1 = layoutInflater.inflate(R.layout.dialog, null)
            builder.setView(v1)
            // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
            var listener = DialogInterface.OnClickListener { p0, p1 ->
                var alert = p0 as AlertDialog
                var edit1: EditText? = alert.findViewById<EditText>(R.id.editText)
                var edit2: EditText? = alert.findViewById<EditText>(R.id.editText2)
                val warnText: TextView? = alert.findViewById<TextView>(R.id.warnText);

                login_id = "${edit1?.text}"
                login_pw = "${edit2?.text}"

                if (warnText != null) {
                    connectServer(warnText)
                };

            } // alert dialog listener

            builder.setPositiveButton("로그인", listener)
            builder.setNegativeButton("취소", null)

            builder.show()

        } // logoutText Clicked

        logout_btn.setOnClickListener{
            if(check >= 1){
                check = 0;
                checkVisible();
            }
        }

    } // oncreate
    fun checkVisible(){
        runOnUiThread(Runnable() {
            if(check >= 1){

                findViewById<TextView>(R.id.user1_dateText).visibility = VISIBLE1;
                findViewById<TextView>(R.id.user1_nameText).visibility = VISIBLE1;
                findViewById<LinearLayout>(R.id.userBox2).visibility = VISIBLE1;
                findViewById<TextView>(R.id.user1_logoutText).visibility = View.INVISIBLE;
                findViewById<LinearLayout>(R.id.logout_btn).visibility = VISIBLE1;
            }
            else{
                findViewById<TextView>(R.id.user1_dateText).visibility = View.INVISIBLE;
                findViewById<TextView>(R.id.user1_nameText).visibility = View.INVISIBLE;
                findViewById<LinearLayout>(R.id.userBox2).visibility = View.GONE;
                findViewById<TextView>(R.id.user1_logoutText).visibility = VISIBLE1;
                findViewById<LinearLayout>(R.id.logout_btn).visibility = View.INVISIBLE;
            }
        })
    } // checkVisible

    fun connectServer(warnText:TextView){
        val url = URL("http://10.0.2.2:80/ysy.php");
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build();

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("연결실패");
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val body = response.body!!.string()

                try {
                    val userInfo = JSONObject(body)
                    val jsonArray = userInfo.optJSONArray("results");

                    var i = 0;
                    while (i < jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val passwd = jsonObject.getString("passwd")
                        val userid = jsonObject.getString("userid")
                        val test = jsonObject.getInt("test")
                        println("userid : $userid "+"passwd : $passwd "+"test : $test")
                        if("$userid" == login_id){
                            if("$passwd" == login_pw) {
                                check = 1;
                                checkVisible();
                            }
                            else{
                                warnText.text = "pw가 일치하지 않습니다."
                            }
                        }
                        else{
                            warnText.text = "id가 존재하지 않습니다."
                        }
                        i++;

                    }

                } catch (e: JSONException) {
                    println(e);
                }
            }
        }) // client enqueue
    } // connectServer

} // Mypage


//val builder = AlertDialog.Builder(this)
//            builder.setTitle("타이틀 입니다.")
//            builder.setMessage("메시지를 입력합니다.")
//            builder.setPositiveButton(
//                "선택 1",
//                { dialogInterface: DialogInterface?, i: Int ->
//                    //원하는 명령어
//                })
//
//            builder.setNegativeButton(
//                "선택 2",
//                { dialogInterface: DialogInterface?, i: Int ->
//                    //원하는 명령어
//                })
//
//            builder.setNeutralButton(
//                "선택 3",
//                { dialogInterface: DialogInterface?, i: Int ->
//                    //원하는 명령어
//                })
//            builder.show()
/*
* client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: okhttp3.Call, response: Response) {
                    println("난 살았어");

                    val body = response?.body?.string()

                    //Gson으로 파싱
                    val gson = GsonBuilder().create()
                    val list = gson.fromJson(body, JsonObj::class.java)

                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    println(e)
                }


            })
* */

/*
*
        val handler = Handler {
            when(it.what){
                user1_dateText.visibility = VISIBLE1;
                    user1_nameText.visibility = VISIBLE1;
                user2.visibility = VISIBLE1;
                    logoutText.visibility = View.INVISIBLE;
                logout_btn.visibility = VISIBLE1;
            }
        }
* */

//            if(check >= 1){
//                user1_dateText.visibility = VISIBLE1;
//                user1_nameText.visibility = VISIBLE1;
//                user2.visibility = VISIBLE1;
//                logoutText.visibility = View.INVISIBLE;
//                logout_btn.visibility = VISIBLE1;
//            }