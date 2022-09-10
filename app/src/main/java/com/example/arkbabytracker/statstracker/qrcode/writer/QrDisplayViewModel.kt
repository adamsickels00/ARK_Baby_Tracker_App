package com.example.arkbabytracker.statstracker.qrcode.writer

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class QrDisplayViewModel:ViewModel(){

    fun createQrBitmapList(dinoList:List<DinoStats>,width:Int,height:Int):List<ImageBitmap>{
        val list = mutableListOf<ImageBitmap>()
        for(d in dinoList){
            list.add(createQrBitmap(d,width,height))
        }
        return list
    }

    fun createQrBitmap(dino:DinoStats,width:Int,height:Int):ImageBitmap{
        val string = Gson().toJson(dino)
        val uri = Uri.Builder()
            .scheme("https")
            .authority("arkbabytracker")
            .path("dinoList")
            .appendPath(string)
            .build()
            .toString()
        val bitMatrix = QRCodeWriter().encode(uri, BarcodeFormat.QR_CODE,width,height)
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)

        // USE SET PIXELS? SHOULD BE FASTER!
        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap.setPixel(i, j, if (bitMatrix.get(i, j)) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap.asImageBitmap()

    }


}