package com.navigation.foxizz.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.navigation.foxizz.R

class TitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        initialise(context, attrs)
    }

    private lateinit var backButton: ImageButton //返回
    private lateinit var titleText: TextView //标题
    private lateinit var menuButton: ImageButton //菜单

    /**
     * 设置返回按钮是否启用
     * @param enable 是否启用
     */
    fun setBackButtonEnable(enable: Boolean) {
        if (enable) backButton.visibility = VISIBLE else backButton.visibility = GONE
    }

    /**
     * 设置标题内容
     * @param content 内容
     */
    private fun setTitleTextContent(content: String?) {
        titleText.text = content
    }

    /**
     * 设置菜单按钮是否启用
     * @param enable 是否启用
     */
    fun setMenuButtonEnable(enable: Boolean) {
        if (enable) menuButton.visibility = VISIBLE
        else menuButton.visibility = GONE
    }

    /**
     * 设置返回按钮点击事件
     * @param backOnClickListener 返回按钮点击事件
     */
    fun setBackOnClickListener(backOnClickListener: OnClickListener) {
        backButton.setOnClickListener(backOnClickListener)
    }

    /**
     * 设置菜单按钮点击事件
     * @param menuOnClickListener 菜单按钮点击事件
     */
    fun setMenuOnClickListener(menuOnClickListener: OnClickListener) {
        menuButton.setOnClickListener(menuOnClickListener)
    }

    //初始化标题
    private fun initialise(context: Context, attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.view_title_layout, this)

        backButton = findViewById(R.id.ib_back)
        titleText = findViewById(R.id.tv_title)
        menuButton = findViewById(R.id.ib_menu)

        backButton.setOnClickListener {
            (getContext() as Activity).finish()
        }

        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout)
        setBackButtonEnable(typedArray.getBoolean( //默认启用返回按钮
                R.styleable.TitleLayout_back_enable, true))
        setTitleTextContent(typedArray.getString(
                R.styleable.TitleLayout_title))
        setMenuButtonEnable(typedArray.getBoolean( //默认不启用菜单按钮
                R.styleable.TitleLayout_menu_enable, false))
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}