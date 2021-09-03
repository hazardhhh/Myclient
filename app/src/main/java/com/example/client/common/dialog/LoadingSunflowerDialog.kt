package com.example.client.common.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.example.client.R
import com.example.client.databinding.LoadingSunflowerBinding


/**
 * @author: chenjiayou
 * @createBy: 2019/11/29
 * @descript: 加载loading
 */
class LoadingSunflowerDialog(var context: Context) : IDialogService<LoadingSunflowerBinding> {

    lateinit var animationSet: AnimationSet

    override fun getLayoutId(): Int {
        return R.layout.loading_sunflower
    }

    override fun getDialog(): Dialog {
        return Dialog(context)
    }

    override fun init(title: String?, viewDataBinding: LoadingSunflowerBinding, dialog: Dialog) {
        /**设置对话框背景透明*/
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        viewDataBinding.tvTitle.text = title ?: ""
        dialog.setContentView(viewDataBinding.root)
        loading()
        viewDataBinding.ivLoading.startAnimation(animationSet)
    }

    //加载动画
    private fun loading() {
        animationSet = AnimationSet(true)
        val animationRotate = RotateAnimation(
            0f, (+359).toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        //第一个参数fromDegrees为动画起始时的旋转角度 //第二个参数toDegrees为动画旋转到的角度
        //第三个参数pivotXType为动画在X轴相对于物件位置类型 //第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
        //第五个参数pivotXType为动画在Y轴相对于物件位置类型 //第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
        animationRotate.repeatCount = -1
        animationRotate.startOffset = 0
        animationRotate.duration = 1000
        val lir = LinearInterpolator()
        animationSet.interpolator = lir
        animationSet.addAnimation(animationRotate)
    }


    override fun show(dialog: Dialog, viewDataBinding: LoadingSunflowerBinding) {
        dialog.show()
    }

}