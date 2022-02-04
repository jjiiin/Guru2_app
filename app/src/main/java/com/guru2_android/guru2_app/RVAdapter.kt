package com.guru2_android.guru2_app


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guru2_android.guru2_app.dataModel.eggModel
import com.guru2_android.guru2_app.dataModel.jobModel

//퀘스트 목록을 리싸이클러뷰에 출력하는 어댑터 클래스
class RVAdapter(val items:MutableList<jobModel>):RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.listview_item,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.title.text=item.title
        holder.time.text=item.time
        holder.sub.text=item.sub
        if(item.done == "1"){
            holder.itemView.setBackgroundColor(Color.parseColor("#FCEE8F"))// 완료한 건 색상 변경하기
        }
        if(item.image==""){
            holder.image.visibility=View.INVISIBLE //안보이게 하기
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener)  {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title:TextView=itemView.findViewById(R.id.title)
        val time:TextView=itemView.findViewById(R.id.time)
        val sub:TextView=itemView.findViewById(R.id.sub)
        val image:ImageView=itemView.findViewById(R.id.cam)

    }

}