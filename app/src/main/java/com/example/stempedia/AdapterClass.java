package com.example.stempedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewholder>
{
    Context context;
    RecyclerView recycle;
    ArrayList<String> list;
    ArrayList<String> urls;
public void update(String fileName,String url)
{

    list.add(fileName);
    urls.add(url);
    notifyDataSetChanged();
}
    public AdapterClass(RecyclerView recycle, Context context, ArrayList<String> list,ArrayList<String> urls)
    {
        this.recycle=recycle;
        this.context=context;
        this.list=list;
        this.urls=urls;
    }
    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType  ) {
         return new MyViewholder(LayoutInflater.from(context).inflate(R.layout.individual_files,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        holder.tvFileName.setText(list.get(position));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder
    {
        TextView tvFileName;


        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            tvFileName=itemView.findViewById(R.id.tvFileName);
            tvFileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View rootView = (View) view.getParent();
                    int pos=recycle.getChildLayoutPosition(rootView);
                    Intent a=new Intent(Intent.ACTION_VIEW);
                    a.setData(Uri.parse(urls.get(pos)));
                    context.startActivity(a);
                    /*Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setDataAndType(Uri.parse( "http://docs.google.com/viewer?url=" + urls.get(pos)), "text/html");

                    context.startActivity(intent);
*/
                }
            });

        }
    }

}

