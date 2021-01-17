package com.example.jingbin.cloudreader.viewmodel.wan

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.jingbin.cloudreader.http.HttpClient
import com.example.jingbin.cloudreader.utils.ToastUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jingbin.bymvvm.base.BaseViewModel

/**
 * Created by jingbin on 2021/1/17.
 */
class PublishViewModel(application: Application) : BaseViewModel(application) {

    val title: ObservableField<String> = ObservableField<String>()
    val link: ObservableField<String> = ObservableField<String>()


    /**
     * 分享文章
     */
    fun pushArticle(): MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        if (!verifyData()) {
            data.value = false
            return data
        }
        val subscribe = HttpClient.Builder.getWanAndroidServer().pushArticle(title.get(), link.get())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bean ->
                    if (bean != null && bean.errorCode == 200) {
                        data.setValue(true)
                    } else {
                        bean?.let {
                            ToastUtil.showToast(bean.errorMsg)
                        }
                        data.setValue(false)
                    }
                }) { throwable: Throwable? -> data.setValue(false) }
        addDisposable(subscribe)
        return data
    }

    private fun verifyData(): Boolean {
        if (TextUtils.isEmpty(title.get())) {
            ToastUtil.showToast("请输入标题")
            return false
        }
        if (TextUtils.isEmpty(link.get())) {
            ToastUtil.showToast("请输入链接")
            return false
        }
        return true
    }

}