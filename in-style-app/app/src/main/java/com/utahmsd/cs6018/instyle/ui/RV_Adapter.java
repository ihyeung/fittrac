package com.utahmsd.cs6018.instyle.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.utahmsd.cs6018.instyle.R;

import java.util.List;

public class RV_Adapter extends RecyclerView.Adapter<RV_Adapter.ViewHolder> {
    private List<DashButton> m_btn_img_ListItems;
    private Context m_Context;

    private OnAdapterDataChannel m_dataListener;

    public RV_Adapter(List<DashButton> inputList) {
        m_btn_img_ListItems = inputList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        protected View itemLayout;
        protected ImageButton btn_image_itemData;
        protected TextView txtv_btn_lbl;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemLayout = view;
            btn_image_itemData = view.findViewById(R.id.btn_img_dashboard_item);
            txtv_btn_lbl = view.findViewById(R.id.txtv_btn_label);
        }
    }

    @NonNull
    @Override
    public RV_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        m_Context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(m_Context);
        View view = layoutInflater.inflate(R.layout.dashboard_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        try {
            m_dataListener = (OnAdapterDataChannel) m_Context;
        } catch (ClassCastException cce) {
            throw new ClassCastException(m_Context.toString() + " must implement OnAdapterDataPass");
        }

        viewHolder.btn_image_itemData.setImageDrawable(m_btn_img_ListItems.get(position).getImage());
        viewHolder.txtv_btn_lbl.setText(m_btn_img_ListItems.get(position).getText());

        if (m_Context.getResources().getBoolean(R.bool.isWideDisplay)) { //Scale up button sizes if on tablet
            viewHolder.btn_image_itemData.setScaleX(Float.valueOf("1.5"));
            viewHolder.btn_image_itemData.setScaleY(Float.valueOf("1.5"));
            viewHolder.btn_image_itemData.setPadding(0, 24, 0, 24);
            viewHolder.txtv_btn_lbl.setTextSize(25);
        }
        viewHolder.btn_image_itemData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                m_dataListener.onAdapterDataPass(position);
            }
        });
    }

    public interface OnAdapterDataChannel {
        void onAdapterDataPass(int position);
    }

    @Override
    public int getItemCount() {
        return m_btn_img_ListItems.size();
    }
}