package com.ealax.sdkcompressjpegktdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.ealax.compressjpeg.CompressJpeg
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val mPicDir = Environment.getExternalStorageDirectory().path + "/jpeg_picture/"
    private var mCompressedPic: File? = null
    private val mSourcePicPath = mPicDir + File.separator + "temp.jpg"//事先准备好的sd卡目录下的图片

    private var compressImgIv: ImageView? = null
    private var originalImgIv: ImageView? = null
    private var mBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        compressImgIv = findViewById(R.id.main_iv_img_compress)
        originalImgIv = findViewById(R.id.main_iv_img_original)
        findViewById<View>(R.id.main_tv_compress)!!.setOnClickListener(this)
        mCompressedPic = File("$mPicDir/jni_compress.jpg")
        if (!mCompressedPic!!.exists()) {
            mCompressedPic!!.createNewFile()
        }
        mBitmap = BitmapFactory.decodeFile(mSourcePicPath)
        originalImgIv!!.setImageBitmap(mBitmap)
    }

    override fun onClick(v: View?) {
        val viewId = v!!.id
        Observable.just(viewId).map { o ->
            compressBitmap(o)
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onNext(bitmap: String) {
                        Toast.makeText(this@MainActivity, "压缩成功", Toast.LENGTH_LONG).show()
                        compressImgIv!!.setImageBitmap(BitmapFactory.decodeFile(mCompressedPic!!.path))
                    }
                })
    }


    private fun compressBitmap(id: Int): String {
        var codeString = ""
        when (id) {
            R.id.main_tv_compress -> {
                codeString = CompressJpeg.compressBitmapNative(mBitmap, mBitmap!!.width, mBitmap!!.height, 40, mCompressedPic!!.absolutePath.toByteArray(), true)
            }
        }
        Thread.sleep(1000)
        return codeString
    }
}
