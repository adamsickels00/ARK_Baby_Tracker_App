package com.example.arkbabytracker.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.luminance
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.example.arkbabytracker.R
import kotlin.math.pow

object DinoColorUtils {
    // From https://ark.fandom.com/wiki/Color_IDs
    private val idToColorIdMap = mapOf(
        1 to R.color.Red,
        2 to R.color.Blue,
        3 to R.color.Green,
        4 to R.color.Yellow,
        5 to R.color.Cyan,
        6 to R.color.Magenta,
        7 to R.color.Light_Green,
        8 to R.color.Light_Grey,
        9 to R.color.Light_Brown,
        10 to R.color.Light_Orange,
        11 to R.color.Light_Yellow,
        12 to R.color.Light_Red,
        13 to R.color.Dark_Grey,
        14 to R.color.Black,
        15 to R.color.Brown,
        16 to R.color.Dark_Green,
        17 to R.color.Dark_Red,
        18 to R.color.White,
        19 to R.color.Dino_Light_Red,
        20 to R.color.Dino_Dark_Red,
        21 to R.color.Dino_Light_Orange,
        22 to R.color.Dino_Dark_Orange,
        23 to R.color.Dino_Light_Yellow,
        24 to R.color.Dino_Dark_Yellow,
        25 to R.color.Dino_Light_Green,
        26 to R.color.Dino_Medium_Green,
        27 to R.color.Dino_Dark_Green,
        28 to R.color.Dino_Light_Blue,
        29 to R.color.Dino_Dark_Blue,
        30 to R.color.Dino_Light_Purple,
        31 to R.color.Dino_Dark_Purple,
        32 to R.color.Dino_Light_Brown,
        33 to R.color.Dino_Medium_Brown,
        34 to R.color.Dino_Dark_Brown,
        35 to R.color.Dino_Darker_Grey,
        36 to R.color.Dino_Albino,
        37 to R.color.BigFoot0,
        38 to R.color.BigFoot4,
        39 to R.color.BigFoot5,
        40 to R.color.WolfFur,
        41 to R.color.DarkWolfFur,
        42 to R.color.DragonBase0,
        43 to R.color.DragonBase1,
        44 to R.color.DragonFire,
        45 to R.color.DragonGreen0,
        46 to R.color.DragonGreen1,
        47 to R.color.DragonGreen2,
        48 to R.color.DragonGreen3,
        49 to R.color.WyvernPurple0,
        50 to R.color.WyvernPurple1,
        51 to R.color.WyvernBlue0,
        52 to R.color.WyvernBlue1,
        53 to R.color.Dino_Medium_Blue,
        54 to R.color.Dino_Deep_Blue,
        55 to R.color.NearWhite,
        56 to R.color.NearBlack,
        57 to R.color.DarkTurquoise,
        58 to R.color.MediumTurquoise,
        59 to R.color.Turquoise,
        60 to R.color.GreenSlate,
        61 to R.color.Sage,
        62 to R.color.DarkWarmGray,
        63 to R.color.MediumWarmGray,
        64 to R.color.LightWarmGray,
        65 to R.color.DarkCement,
        66 to R.color.LightCement,
        67 to R.color.LightPink,
        68 to R.color.DeepPink,
        69 to R.color.DarkViolet,
        70 to R.color.DarkMagenta,
        71 to R.color.BurntSienna,
        72 to R.color.MediumAutumn,
        73 to R.color.Vermillion,
        74 to R.color.Coral,
        75 to R.color.Orange,
        76 to R.color.Peach,
        77 to R.color.LightAutumn,
        78 to R.color.Mustard,
        79 to R.color.ActualBlack,
        80 to R.color.MidnightBlue,
        81 to R.color.DarkBlue,
        82 to R.color.BlackSands,
        83 to R.color.LemonLime,
        84 to R.color.Mint,
        85 to R.color.Jade,
        86 to R.color.PineGreen,
        87 to R.color.SpruceGreen,
        88 to R.color.LeafGreen,
        89 to R.color.DarkLavender,
        90 to R.color.MediumLavender,
        91 to R.color.Lavender,
        92 to R.color.DarkTeal,
        93 to R.color.MediumTeal,
        94 to R.color.Teal,
        95 to R.color.PowderBlue,
        96 to R.color.Glacial,
        97 to R.color.Cammo,
        98 to R.color.DryMoss,
        99 to R.color.Custard,
        100 to R.color.Cream,
        201 to R.color.Black_Dye,
        202 to R.color.Blue_Dye,
        203 to R.color.Brown_Dye,
        204 to R.color.Cyan_Dye,
        205 to R.color.Forest_Dye,
        206 to R.color.Green_Dye,
        207 to R.color.Purple_Dye,
        208 to R.color.Orange_Dye,
        209 to R.color.Parchment_Dye,
        210 to R.color.Pink_Dye,
        211 to R.color.Uncraftable_Purple_Dye,
        212 to R.color.Red_Dye,
        213 to R.color.Royalty_Dye,
        214 to R.color.Silver_Dye,
        215 to R.color.Sky_Dye,
        216 to R.color.Tan_Dye,
        217 to R.color.Tangerine_Dye,
        218 to R.color.White_Dye,
        219 to R.color.Yellow_Dye,
        220 to R.color.Magenta_Dye,
        221 to R.color.Brick_Dye,
        222 to R.color.Cantaloupe_Dye,
        223 to R.color.Mud_Dye,
        224 to R.color.Navy_Dye,
        225 to R.color.Olive_Dye,
        226 to R.color.Slate_Dye,
    )

    fun getColorOfIdIfExists(id:Int): Int? {
        return idToColorIdMap[id]
    }

    fun getTextColorForBackgroundHex(hex:Int,context: Context):Int{
        var blackContrast = (context.getColor(R.color.text_on_light).luminance + .05)/(hex.luminance+.05)
        var whiteContrast = (context.getColor(R.color.text_on_dark).luminance + .05)/(hex.luminance+.05)
        if(blackContrast < 1) blackContrast = blackContrast.pow(-1)
        if(whiteContrast < 1) whiteContrast = whiteContrast.pow(-1)
        return if(blackContrast>=whiteContrast) context.getColor(R.color.text_on_light)
        else context.getColor(R.color.text_on_dark)
    }

}
@BindingAdapter("dinoColor")
fun setDinoColor(view:TextView,id:Int){
    view.text = id.toString()
    val colorResId = DinoColorUtils.getColorOfIdIfExists(id)
    if(colorResId != null) {
        view.setBackgroundResource(colorResId)
        view.setTextColor(DinoColorUtils.getTextColorForBackgroundHex(view.context.getColor(colorResId),view.context))
    } else{
        view.setBackgroundColor(Color.TRANSPARENT)
        view.context?.let {
            val x = TypedValue()
            it.theme.resolveAttribute(com.google.android.material.R.attr.colorOnBackground, x, true)
            view.setTextColor(x.data)
        }
    }
}