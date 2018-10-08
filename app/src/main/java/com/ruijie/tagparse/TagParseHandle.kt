package com.ruijie.tagparse

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import org.xml.sax.XMLReader
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by panruijie on 2018/9/25.
 * Html的tag解析
 */
const val SIZE = "size"
const val TYPEFACE = "typeface"
const val COLOR = "color"

const val TAG_FONT = "extendfont"
const val TAG = "TagParseHandle"
const val DP = "dp"

class TagParseHandle(val context: Context) : Html.TagHandler {

    val tagList: ArrayList<String>
    val attributesKeyList: ArrayList<String>
    val attributesMap: HashMap<String, Any>
    var preIndex = 0
    var curIndex = 0

    init {
        tagList = ArrayList()
        tagList.add(TAG_FONT)
        attributesKeyList = ArrayList()
        attributesKeyList.add(SIZE)
        attributesKeyList.add(TYPEFACE)
        attributesMap = HashMap()
    }

    override fun handleTag(isOpen: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (output == null || output.isEmpty()) {
            return
        }
        for (type in tagList) {
            if (tag!!.toLowerCase().equals(type)) {
                parseAttributes(xmlReader)
                if (isOpen) {
                    handleStartTag(tag, output, xmlReader)
                } else {
                    handleEndTag(tag, output, xmlReader)
                }
            }
        }
    }

    private fun parseAttributes(xmlReader: XMLReader?) {
        try {
            val elementField = xmlReader!!.javaClass.getDeclaredField("theNewElement")
            elementField.setAccessible(true)
            val element = elementField.get(xmlReader)
            val attsField = element.javaClass.getDeclaredField("theAtts")
            attsField.setAccessible(true)
            val atts = attsField.get(element)
            val dataField = atts.javaClass.getDeclaredField("data")
            dataField.setAccessible(true)
            val data = dataField.get(atts) as Array<String>
            val lengthField = atts.javaClass.getDeclaredField("length")
            lengthField.setAccessible(true)
            val len = lengthField.get(atts) as Int
            for (i in 0 until len - 1) {
                attributesMap.put(data[i * 5 + 1], data[i * 5 + 4])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleStartTag(tag: String?, output: Editable, xmlReader: XMLReader?) {
        curIndex = output.length
    }

    private fun handleEndTag(tag: String?, output: Editable, xmlReader: XMLReader?) {
        for ((attribute, value) in attributesMap) {
            when (attribute.toLowerCase()) {
                SIZE -> {
                    Log.w(TAG, "size, output = " + output + ", value = " + (value.toString()) + ", start = " + preIndex + ", end = " + curIndex)
                    output.setSpan(AbsoluteSizeSpan(dip2px(value.toString().toInt())), preIndex, curIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                TYPEFACE -> {
                    Log.w(TAG, "typeface, output = " + output + ", value = " + value.toString() + ", start = " + preIndex + ", end = " + curIndex)
                    output.setSpan(ExtendTypefaceSpan(Typeface.createFromAsset(context.assets, value.toString())), preIndex, curIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                COLOR -> {
                    /*value.toString().also {
                        it -> output.setSpan(ForegroundColorSpan(Integer.parseInt(it.split("#")[1])), preIndex, curIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)}*/
                }
            }
        }
        preIndex = curIndex
        curIndex = output.length
        attributesMap.clear()
    }

    private fun dip2px(dpValue: Int): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

}
