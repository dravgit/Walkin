package com.cyp.walkin.cyp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.centerm.smartpos.aidl.printer.AidlPrinter
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener
import com.centerm.smartpos.aidl.printer.PrinterParams
import com.cyp.walkin.R
import com.cyp.walkin.cyp.app.WalkinApplication
import com.cyp.walkin.cyp.app.WalkinApplication.Companion.sunmiPrinterService
import com.cyp.walkin.cyp.models.*
import com.cyp.walkin.cyp.utils.BitmapUtils
import com.cyp.walkin.cyp.utils.GlobalConstants
import com.cyp.walkin.cyp.utils.NetworkUtil.Companion.NetworkLisener
import com.cyp.walkin.cyp.utils.NetworkUtil.Companion.searchByOrder
import com.cyp.walkin.cyp.utils.PreferenceUtils
import com.cyp.walkin.cyp.utils.Util
import com.cyp.walkin.cyp.utils.Util.Companion.createImageFromQRCode
import com.cyp.walkin.cyp.utils.Util.Companion.toDateFormat
import com.sunmi.peripheral.printer.InnerResultCallbcak
import com.vanstone.appsdk.client.ISdkStatue
import com.vanstone.l2.Common
import com.vanstone.trans.api.MagCardApi
import com.vanstone.trans.api.PrinterApi
import com.vanstone.trans.api.SystemApi
import com.vanstone.utils.CommonConvert
import sunmi.sunmiui.utils.LogUtil

class DetailAdapter     // RecyclerView recyclerView;
    (var context: Context) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    private var listdata: List<PartialVisitorResponseModel> = ArrayList()
    private var printDev: AidlPrinter? = null
    fun setListdata(listdata: List<PartialVisitorResponseModel>) {
        this.listdata = listdata
        notifyDataSetChanged()
    }

    fun setPrinter(printDev: AidlPrinter?) {
        this.printDev = printDev
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detailListData = listdata[position]
        holder.tvName.text = detailListData.name
        holder.tvObjective.text = context.getString(R.string.contact_s, detailListData.department)
        holder.tvIn.text = toDateFormat(detailListData.checkin_time)
        if (detailListData.checkout_time.isEmpty()) {
            holder.tvOut.text = context.getString(R.string.stay_in)
            holder.btnReprint.visibility = View.VISIBLE
            holder.btnReprint.tag = detailListData.contact_code
            holder.btnReprint.setOnClickListener { view ->
                val code = view.tag.toString()
                search(code)
            }
        } else {
            holder.btnReprint.visibility = View.GONE
            holder.tvOut.text = toDateFormat(detailListData.checkout_time)
        }
        holder.imageView1.setImageResource(0)
        holder.imageView2.setImageResource(0)
        for (imageModel: ImageModel in detailListData.images) {
            val type = imageModel.type
            if (("1" == type) && !imageModel.url.isEmpty()) {
                Glide.with(context) //1
                    .load(imageModel.url)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .skipMemoryCache(true) //2
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                    .into(holder.imageView1)
            } else if (("2" == type) && !imageModel.url.isEmpty()) {
                Glide.with(context) //1
                    .load(imageModel.url)
                    .placeholder(R.drawable.ic_car)
                    .error(R.drawable.ic_car)
                    .skipMemoryCache(true) //2
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                    .into(holder.imageView2)
            } else if (("4" == type) && !imageModel.url.isEmpty()) {
                Glide.with(context) //1
                    .load(imageModel.url)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .skipMemoryCache(true) //2
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                    .into(holder.imageView1)
            }
        }
        holder.relativeLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
//                Toast.makeText(view.getContext(),"click on item: "+ detailListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        })
    }

    private fun search(code: String) {
        searchByOrder(code, object : NetworkLisener<VisitorResponseModel> {
            override fun onResponse(response: VisitorResponseModel) {
//                print(response)

                if ("A75".equals(Build.MODEL, true)) {
                    printA75(response)
                } else {
                    printP2(response)
                }
            }

            override fun onError(errorModel: WalkInErrorModel) {
                Log.e("error", errorModel.msg)
            }

            override fun onExpired() {
                search(code)
            }
        }, VisitorResponseModel::class.java)
    }

    @Throws(RemoteException::class)
    fun setHeight(height: Int) {
        val returnText = ByteArray(3)
        returnText[0] = 0x1B
        returnText[1] = 0x33
        returnText[2] = height.toByte()

        if (sunmiPrinterService == null) {
            sunmiPrinterService = WalkinApplication.sunmiPrinterService
        }
        sunmiPrinterService?.sendRAWData(returnText, null)
    }

    private fun resizeBitmap(cacheBitmap: Bitmap): Bitmap {
        val currentWidth = cacheBitmap.width
        val currentHeight = cacheBitmap.height

        val desiredWidth = 350
        val scaleRatio = desiredWidth.toDouble() / currentWidth
        val desiredHeight = (currentHeight * scaleRatio).toInt()

        var resizedBitmap = BitmapUtils.scale(cacheBitmap, desiredWidth, desiredHeight)
        resizedBitmap =
            BitmapUtils.replaceBitmapColor(resizedBitmap, Color.TRANSPARENT, Color.WHITE)

        return resizedBitmap
    }

    private fun printA75(data: VisitorResponseModel) {
        val signature = PreferenceUtils.getSignature()
        PrinterApi.PrnClrBuff_Api()
        PrinterApi.PrnSetGray_Api(15)
        PrinterApi.PrnLineSpaceSet_Api(5.toShort(), 0)
        PrinterApi.PrnLogo_Api(resizeBitmap(PreferenceUtils.getBitmapLogo()))
        PrinterApi.PrnFontSet_Api(24, 24, 0)
        PrinterApi.PrnStr_Api(
            "\n\nบริษัท : " + PreferenceUtils.getCompanyName() +
                    "\nชื่อ-นามสกุล : " + data.name.replace(" ", " ") +
                    "\nเลขบัตรประขาชน : " + data.idcard +
                    "\nทะเบียนรถ : " + data.vehicle_id +
                    "\nจากบริษัท : " + data.from +
                    "\nผู้ที่ขอพบ : " + data.person_contact +
                    "\nติดต่อแผนก : " + data.department +
                    "\nวัตถุประสงค์ : " + data.objective_type.replace(" ", " ") +
                    "\nรายละเอียด : " + data.objective_note.replace(" ", " ") +
                    "\nอุณหภูมิ : " + data.temperature +
                    "\nเวลาเข้า : " + data.checkin_time
        )

        PrinterApi.PrnStr_Api("\n           ")
        PrinterApi.printAddQrCode_Api(1, 250, data.contact_code)
//		PrinterApi.PrnLogo_Api(bitmap)
        PrinterApi.PrnStr_Api("\n       " + data.contact_code + "\n\n")
        for (i in signature.indices) {
            PrinterApi.PrnStr_Api(
                "\n\n\n\n____________________________" + "\n       " + signature.get(
                    i
                ).getname()
            )
        }

        PrinterApi.PrnStr_Api("\n\n" + PreferenceUtils.getCompanyNote() + "\n\n\n\n\n")

        Util.printData()
    }

    private fun printP2(data: VisitorResponseModel) {
        setHeight(0x11)
        val bitmap = createImageFromQRCode(data.contact_code)
        val signature = PreferenceUtils.getSignature()

        sunmiPrinterService!!.clearBuffer()
        sunmiPrinterService!!.enterPrinterBuffer(true)
        //Set Center
        sunmiPrinterService!!.setAlignment(1, innerResultCallbcak)
        //Logo
        sunmiPrinterService!!.printBitmap(
            resizeBitmap(PreferenceUtils.getBitmapLogo()),
            innerResultCallbcak
        )
        sunmiPrinterService!!.printText("\n", innerResultCallbcak)
        //Set Left
        sunmiPrinterService!!.setAlignment(0, innerResultCallbcak)
        sunmiPrinterService!!.printText(
            "บริษัท : " + PreferenceUtils.getCompanyName() +
                    "\nชื่อ-นามสกุล : " + (data.name ?: " ").replace(" ", " ") +
                    "\nเลขบัตรประขาชน : " + (data.idcard ?: " ") +
                    "\nทะเบียนรถ : " + (data.vehicle_id ?: " ") +
                    "\nจากบริษัท : " + (data.from ?: " ") +
                    "\nผู้ที่ขอพบ : " + (data.person_contact ?: " ") +
                    "\nติดต่อแผนก : " + (data.department ?: " ") +
                    "\nวัตถุประสงค์ : " + (data.objective_type ?: " ").replace(" ", " ") +
                    "\nรายละเอียด : " + (data.objective_note ?: " ").replace(" ", " ") +
                    "\nอุณหภูมิ : " + (data.temperature ?: " ") +
                    "\nเวลาเข้า : " + (data.checkin_time ?: " ") + "\n", innerResultCallbcak
        )
        //Set Center
        sunmiPrinterService!!.setAlignment(1, innerResultCallbcak)
        //QRCode
        sunmiPrinterService!!.printBitmap(bitmap, innerResultCallbcak)
        sunmiPrinterService!!.printText("\n" + data.contact_code + "\n", innerResultCallbcak)
        //Signature
        for (i in signature.indices) {
            sunmiPrinterService!!.printText(
                "\n\n\n\n\n____________________________\n",
                innerResultCallbcak
            )
            sunmiPrinterService!!.printText(signature[i].getname(), innerResultCallbcak)
        }
        sunmiPrinterService!!.printText("\n\n", innerResultCallbcak)
        sunmiPrinterService!!.setAlignment(0, innerResultCallbcak)
        sunmiPrinterService!!.printText(
            PreferenceUtils.getCompanyNote() + "\n\n\n\n\n\n\n\n\n",
            innerResultCallbcak
        )
        //Print
        sunmiPrinterService!!.commitPrinterBufferWithCallback(innerResultCallbcak)
    }

    private fun print(data: VisitorResponseModel) {
        try {
            val bitmap = createImageFromQRCode(data.contact_code)
            val signature = PreferenceUtils.getSignature()
            val textList: MutableList<PrinterParams> = ArrayList()
            var printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.CENTER
            printerParams.dataType = PrinterParams.DATATYPE.IMAGE
            printerParams.lineHeight = 200
            printerParams.bitmap = PreferenceUtils.getBitmapLogo()
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text =
                "\n\nบริษัท : " + PreferenceUtils.getCompanyName().replace(" ", " ")
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nชื่อ-นามสกุล : " + data.name().replace(" ", " ")
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nเลขบัตรประขาชน : " + data.idcard
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nทะเบียนรถ : " + data.vehicle_id
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nจากบริษัท : " + data.from
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nผู้ที่ขอพบ : " + data.person_contact
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nต่อต่อแผนก : " + data.department
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nวัตถุประสงค์ : " + data.objective_type.replace(" ", " ")
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nอุณหภูมิ : " + data.temperature
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.LEFT
            printerParams.textSize = 24
            printerParams.text = "\nเวลาเข้า : " + data.checkin_time
            printerParams.isBold = true
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.CENTER
            printerParams.dataType = PrinterParams.DATATYPE.IMAGE
            printerParams.lineHeight = 200
            printerParams.bitmap = bitmap
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.CENTER
            printerParams.textSize = 24
            printerParams.text = data.contact_code
            textList.add(printerParams)
            for (i in signature.indices) {
                printerParams = PrinterParams()
                printerParams.align = PrinterParams.ALIGN.CENTER
                printerParams.textSize = 24
                printerParams.text = "\n\n\n____________________________"
                textList.add(printerParams)
                printerParams = PrinterParams()
                printerParams.align = PrinterParams.ALIGN.CENTER
                printerParams.textSize = 24
                printerParams.text = "\n" + signature.get(i).getname()
                textList.add(printerParams)
            }
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.CENTER
            printerParams.textSize = 24
            printerParams.text = "\n\n" + PreferenceUtils.getCompanyNote()
            textList.add(printerParams)
            printerParams = PrinterParams()
            printerParams.align = PrinterParams.ALIGN.CENTER
            printerParams.textSize = 24
            printerParams.text = "\n\n\n\n\n"
            textList.add(printerParams)
            printDev!!.printDatas(textList, object : AidlPrinterStateChangeListener.Stub() {
                @Throws(RemoteException::class)
                override fun onPrintFinish() {
                    Log.e("panya", "onPrintFinish")
                }

                @Throws(RemoteException::class)
                override fun onPrintError(i: Int) {
                    Log.e("panya", "onPrintError")
                }

                @Throws(RemoteException::class)
                override fun onPrintOutOfPaper() {
                    Log.e("panya", "onPrintOutOfPaper")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView1: ImageView
        var imageView2: ImageView
        var tvName: TextView
        var tvObjective: TextView
        var tvIn: TextView
        var tvOut: TextView
        var relativeLayout: RelativeLayout
        var btnReprint: Button

        init {
            imageView1 = itemView.findViewById<View>(R.id.iv_1) as ImageView
            imageView2 = itemView.findViewById<View>(R.id.iv_2) as ImageView
            tvName = itemView.findViewById<View>(R.id.tv_name) as TextView
            tvObjective = itemView.findViewById<View>(R.id.tv_objective) as TextView
            tvIn = itemView.findViewById<View>(R.id.tv_in) as TextView
            tvOut = itemView.findViewById<View>(R.id.tv_out) as TextView
            btnReprint = itemView.findViewById<View>(R.id.btnReprint) as Button
            relativeLayout = itemView.findViewById<View>(R.id.relativeLayout) as RelativeLayout
        }
    }

    private var `is` = true
    private val innerResultCallbcak: InnerResultCallbcak = object : InnerResultCallbcak() {
        override fun onRunResult(isSuccess: Boolean) {
            LogUtil.e("lxy", "isSuccess:$isSuccess")
            if (`is`) {
                try {
                    sunmiPrinterService!!.lineWrap(6, this)
                    `is` = false
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onReturnString(result: String) {
            LogUtil.e("lxy", "result:$result")
        }

        override fun onRaiseException(code: Int, msg: String) {
            LogUtil.e("lxy", "code:$code,msg:$msg")
        }

        override fun onPrintResult(code: Int, msg: String) {
            LogUtil.e("lxy", "code:$code,msg:$msg")
        }
    }

}