package io.github.mzdluo123.mirai.android.utils

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import io.github.mzdluo123.mirai.android.IdleResources
import kotlinx.coroutines.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

fun Context.shareText(text: String, scope: LifecycleCoroutineScope) {
    scope.launch {
        val waitingDialog = indeterminateProgressDialog("请稍候", "正在上传") {
            //setCancelable(false)
        }
        waitingDialog.show()
        IdleResources.logUploadDialogIdleResources.increment()
        val errorHandle = CoroutineExceptionHandler { _, _ ->
            waitingDialog.dismiss()
            toast("上传失败！")
        }
        val url = async(errorHandle) { paste(text) }
        withContext(Dispatchers.Main) {
            val urlResult = url.await()
            waitingDialog.dismiss()
            IdleResources.logUploadDialogIdleResources.decrement()
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "MiraiAndroid日志分享")
                putExtra(Intent.EXTRA_TEXT, urlResult)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, "分享到"))
        }
    }
}