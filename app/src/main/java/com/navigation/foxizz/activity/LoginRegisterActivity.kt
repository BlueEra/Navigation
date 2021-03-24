package com.navigation.foxizz.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.zerokirby.api.data.AvatarDataHelper
import cn.zerokirby.api.data.User
import cn.zerokirby.api.data.UserDataHelper
import cn.zerokirby.api.util.CodeUtil
import com.navigation.foxizz.R
import com.navigation.foxizz.data.Constants
import com.navigation.foxizz.data.SPHelper
import com.navigation.foxizz.util.SettingUtil
import com.navigation.foxizz.util.ThreadUtil
import com.navigation.foxizz.util.showToast

/**
 * 登录页
 */
class LoginRegisterActivity : AppCompatActivity() {
    companion object {
        private lateinit var mLocalBroadcastManager: LocalBroadcastManager//本地广播管理器

        /**
         * 启动登录页
         *
         * @param context 上下文
         */
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginRegisterActivity::class.java))
        }
    }

    private lateinit var rlLoginRegister: RelativeLayout //登录注册页
    private lateinit var ibBack: ImageButton //返回按钮
    private lateinit var tvPageTitle: TextView //标题，登录或注册
    private lateinit var etUsername: EditText //用户名输入框
    private lateinit var etPassword: EditText //密码输入框
    private lateinit var ibWatchPassword: ImageButton //显示密码按钮
    private lateinit var etVerify: EditText //验证码输入框
    private lateinit var tvVerify: ImageView //验证码图片
    private lateinit var cbRememberUsername: CheckBox //记住用户名
    private lateinit var cbRememberPassword: CheckBox //记住密码
    private lateinit var appCompatBtLoginRegister: AppCompatButton //登录或注册按钮
    private lateinit var tvRegisterLoginHint: TextView //注册或登录提示
    private lateinit var tvRegisterLoginLink: TextView //注册或登录按钮
    private lateinit var pbLoadingProgress: ProgressBar //加载进度条

    private var isLogin = true //是否是登录页
    private var isSending = false //是否正在登录或注册
    private var isWatchPassword = false //是否显示密码
    private var username = "" //用户名
    private var password = "" //密码
    private var verify = "" //输入框里的验证码
    private lateinit var verifyCode: String //验证码
    private lateinit var mCodeUtil: CodeUtil //验证码生成工具

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        mCodeUtil = CodeUtil()
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        initView() //初始化控件

        //恢复输入框中的信息
        if (savedInstanceState != null) {
            username = savedInstanceState.getString("username")!!
            password = savedInstanceState.getString("password")!!
            verify = savedInstanceState.getString("verify")!!
        }
    }

    override fun onResume() {
        super.onResume()
        resetEdit() //重设输入框填入的信息
    }

    override fun onPause() {
        super.onPause()
        rememberUsernameAndPassword() //重设是否记住账号密码
    }

    //活动被回收时保存输入框中的信息
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("username", username)
        outState.putString("password", password)
        outState.putString("verify", verify)
    }

    //初始化控件
    private fun initView() {
        rlLoginRegister = findViewById(R.id.rl_activity_login_register)
        ibBack = findViewById(R.id.ib_back)
        tvPageTitle = findViewById(R.id.tv_page_title)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        ibWatchPassword = findViewById(R.id.ib_watch_password)
        etVerify = findViewById(R.id.et_verify)
        tvVerify = findViewById(R.id.ib_verify_image)
        cbRememberUsername = findViewById(R.id.cb_remember_username)
        cbRememberPassword = findViewById(R.id.cb_remember_password)
        appCompatBtLoginRegister = findViewById(R.id.app_compat_bt_login_register)
        tvRegisterLoginHint = findViewById(R.id.tv_register_login_hint)
        tvRegisterLoginLink = findViewById(R.id.tv_register_login_link)
        pbLoadingProgress = findViewById(R.id.pb_loading)

        //手机模式只允许竖屏，平板模式只允许横屏，且根据不同的模式设置不同的背景图
        if (SettingUtil.isMobile) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            rlLoginRegister.setBackgroundResource(R.drawable.beach)
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            rlLoginRegister.setBackgroundResource(R.drawable.foxizz_on_the_beach)
        }

        //设置密码输入框的类型
        etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        setUsernameAndPassword() //设置账号密码

        //返回按钮的点击事件
        ibBack.setOnClickListener {
            if (!isSending && !showReturnHintDialog()) finish()
        }

        //监听用户输入变化，账号密码都不为空且验证码正确时，登录或注册按钮可点击
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                username = etUsername.text.toString()
                password = etPassword.text.toString()
                verify = etVerify.text.toString()
                appCompatBtLoginRegister.isEnabled =
                        (username.isNotEmpty() && password.isNotEmpty()
                                && verify.isNotEmpty()
                                && etVerify.text.toString().equals(verifyCode, ignoreCase = true))
            }
        }
        etUsername.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)
        etVerify.addTextChangedListener(textWatcher)

        //显示密码按钮的点击事件
        ibWatchPassword.setOnClickListener {
            if (isWatchPassword) {
                isWatchPassword = false
                ibWatchPassword.setImageResource(R.drawable.ic_eye_black_30dp)
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                isWatchPassword = true
                ibWatchPassword.setImageResource(R.drawable.ic_eye_blue_30dp)
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            etPassword.setSelection(password.length) //移动焦点到末尾
        }
        resetVerify() //生成新验证码
        tvVerify.setOnClickListener {
            resetVerify() //生成新验证码
        }

        //记住账号按钮的点击事件
        cbRememberUsername.setOnClickListener {
            if (cbRememberUsername.isChecked) {
                cbRememberPassword.isEnabled = true
            } else {
                cbRememberPassword.isEnabled = false
                cbRememberPassword.isChecked = false
            }
        }

        //登录或注册按钮的点击事件
        appCompatBtLoginRegister.setOnClickListener {
            pbLoadingProgress.visibility = View.VISIBLE //显示进度条
            appCompatBtLoginRegister.isEnabled = false //登录或注册时不可点击
            ThreadUtil.execute {
                isSending = true

                //提交账号信息，获取返回结果。该操作会阻塞线程，需在子线程进行
                val jsonObject = UserDataHelper.loginRegisterSendRequest(username, password, isLogin)

                //获取登录或注册结果
                var status = 2
                try {
                    status = jsonObject.getInt("Status")
                } catch (ignored: Exception) {
                }

                //生成登录或注册结果提示信息
                var toastMessage = ""
                if (isLogin) { //登录页
                    when (status) {
                        2 -> toastMessage = resources.getString(R.string.server_error)
                        1 -> toastMessage = getString(R.string.login_successfully)
                        0 -> toastMessage = getString(R.string.wrong_username_or_password)
                        -1 -> toastMessage = getString(R.string.username_banned)
                        -2 -> toastMessage = getString(R.string.username_not_exists)
                    }
                } else { //注册页
                    toastMessage = when (status) {
                        2 -> resources.getString(R.string.server_error)
                        1 -> resources.getString(R.string.register_successfully)
                        else -> resources.getString(R.string.username_already_exists)
                    }
                }
                val finalToastMessage = toastMessage
                isSending = false
                finalToastMessage.showToast()//弹出提示信息

                //返回UI线程进行UI操作（主线程）
                runOnUiThread {
                    pbLoadingProgress.visibility = View.GONE //隐藏进度条
                    appCompatBtLoginRegister.isEnabled = true //登录或注册完毕后可点击
                }

                //若登录成功
                if (status == 1) {
                    //初始化用户信息
                    UserDataHelper.loginRegisterInitUserInfo(jsonObject, username, password, isLogin)

                    //发送本地广播通知更新用户名
                    mLocalBroadcastManager.sendBroadcast(Intent(Constants.LOGIN_BROADCAST)
                            .putExtra(Constants.LOGIN_TYPE, Constants.SET_USERNAME))
                    finish() //关闭页面
                    AvatarDataHelper.downloadAvatar(UserDataHelper.loginUserId) //下载头像

                    //发送本地广播通知更新头像
                    mLocalBroadcastManager.sendBroadcast(Intent(Constants.LOGIN_BROADCAST)
                            .putExtra(Constants.LOGIN_TYPE, Constants.SET_AVATAR))
                }
            }
        }

        //注册或登录链接的点击事件
        tvRegisterLoginLink.setOnClickListener {
            if (!isSending) {
                if (isLogin) {
                    isLogin = false
                    tvPageTitle.setText(R.string.register) //设置页面标题为注册
                    appCompatBtLoginRegister.setText(R.string.register) //设置登录或注册按钮为注册
                    tvRegisterLoginHint.setText(R.string.login_hint) //设置登录提示
                    tvRegisterLoginLink.setText(R.string.login_link) //设置登录链接
                } else {
                    isLogin = true
                    tvPageTitle.setText(R.string.login) //设置页面标题为登录
                    appCompatBtLoginRegister.setText(R.string.login) //设置登录或注册按钮为登录
                    tvRegisterLoginHint.setText(R.string.register_hint) //设置注册提示
                    tvRegisterLoginLink.setText(R.string.register_link) //设置注册链接
                }
            }
        }
    }

    //设置账号密码
    private fun setUsernameAndPassword() {
        val userId = UserDataHelper.loginUserId
        val user: User = UserDataHelper.getUser(userId)
        if (SPHelper.getBoolean(Constants.REMEMBER_USERNAME, false)) {
            username = user.username
            etUsername.setText(username)
            cbRememberUsername.isChecked = true
            cbRememberPassword.isEnabled = true
        } else {
            cbRememberPassword.isEnabled = false
        }
        if (SPHelper.getBoolean(Constants.REMEMBER_PASSWORD, false)) {
            password = user.password
            etPassword.setText(password)
            cbRememberPassword.isChecked = true
        }
    }

    //重设输入框填入的信息
    private fun resetEdit() {
        etUsername.setText(username)
        etPassword.setText(password)
        etVerify.setText(verify)
    }

    //重新生成验证码
    private fun resetVerify() {
        val verifyBit = mCodeUtil.createBitmap() //生成新验证码
        tvVerify.setImageBitmap(verifyBit) //设置新生成的验证码图片
        verifyCode = mCodeUtil.code //获取新生成的验证码
        appCompatBtLoginRegister.isEnabled = false //重新生成后不可直接登录
    }

    //重设是否记住账号密码
    private fun rememberUsernameAndPassword() {
        val userId = UserDataHelper.loginUserId
        SPHelper.putBoolean(Constants.REMEMBER_USERNAME, cbRememberUsername.isChecked)
        SPHelper.putBoolean(Constants.REMEMBER_PASSWORD, cbRememberPassword.isChecked)
        if (!cbRememberUsername.isChecked) {
            UserDataHelper.updateUser("username", "", userId)
        }
        if (!cbRememberPassword.isChecked) {
            UserDataHelper.updateUser("password", "", userId)
        }
    }

    //显示返回提示对话框
    private fun showReturnHintDialog(): Boolean {
        if (isLogin) { //如果是登录页
            val user = UserDataHelper.getUser(UserDataHelper.loginUserId)
            //如果账号或密码修改过
            if (!username.equals(user.username, ignoreCase = true) || password != user.password) {
                showReturnHintDialog(getString(R.string.login_page_return_hint))
                return true //显示
            }
        } else { //如果是注册页
            //如果账号或密码不为空
            if (username.isNotEmpty() || password.isNotEmpty()) {
                showReturnHintDialog(getString(R.string.register_page_return_hint))
                return true //显示
            }
        }
        return false //不显示
    }

    //设置返回提示对话框并弹出
    private fun showReturnHintDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.hint)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            finish() //退出登录页
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            //do nothing
        }
        builder.show()
    }

    //监听按键抬起事件
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        //如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //信息有修改或正在连接服务器时不可返回
            if (isSending || showReturnHintDialog()) return false
        }
        return super.onKeyUp(keyCode, event)
    }
}