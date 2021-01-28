package com.hy.mydivapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class InsureHelperListDialog extends Dialog implements OnItemClickListener {
    private ItemClickListener itemClickListener;
    private int[] iconsResources;
    private String[] titles;
    private ListView dialog_content;
    private ListDialogWithIconAdpater dialogAdapter;
    private Context context;
    private int textAlign = 0;
    private int kind = 0;
    private RelativeLayout relative;
    private String title;
    private TextView tv_title;

    public InsureHelperListDialog(Context context, int[] iconsResources,
                                  String[] titles, int textAligin, int kind) {
        super(context, R.style.dialog);
        this.iconsResources = iconsResources;
        this.titles = titles;
        this.context = context;
        this.textAlign = textAligin;
        this.kind = kind;
    }

    public InsureHelperListDialog(Context context, String[] titles, int textAligin, int kind) {
        super(context);
        this.titles = titles;
        this.context = context;
        this.textAlign = textAligin;
        this.kind = kind;
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void setTeitlText(String title){
        this.title=title;
    }

    private void initData() {
        dialogAdapter = new ListDialogWithIconAdpater(iconsResources, titles, this.getContext(), textAlign);
        dialog_content.setAdapter(dialogAdapter);
        dialog_content.setOnItemClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.div_custom_dialog_list);
        initViews();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.95
//		if(this.kind == 1){
//			p.height = (int) (d.getHeight() * 0.5); // 宽度设置为屏幕的0.95
//		}
        p.gravity = Gravity.BOTTOM; // 紧贴底部
        p.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        this.getWindow().setAttributes(p);
        initData();
    }

    private void initViews() {
        this.dialog_content = this.findViewById(R.id.div_dialog_content_list);
        relative=this.findViewById(R.id.div_relative);
        tv_title=this.findViewById(R.id.div_tv_title);
        if(!TextUtils.isEmpty(title)){
            relative.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        itemClickListener.onItemClick(arg2);
        this.cancel();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
