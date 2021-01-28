package com.hy.mydivapplication;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListDialogWithIconAdpater extends BaseAdapter {
    private int[] iconsResources;
    private String[] titles;
    private LayoutInflater inflater;
    private int textAligin = 0;//1,居左,2,居右,0居中
    private int pos;
    private boolean is;

    public ListDialogWithIconAdpater(int[] iconsResources, String[] titles,
                                     Context context, int textAligin) {
        super();
        this.iconsResources = iconsResources;
        this.titles = titles;
        this.textAligin = textAligin;
        inflater = LayoutInflater.from(context);
    }

    public ListDialogWithIconAdpater(int[] iconsResources, String[] titles,
                                     Context context, int textAligin, boolean is, int pos) {
        super();
        this.iconsResources = iconsResources;
        this.titles = titles;
        this.textAligin = textAligin;
        this.is = is;
        this.pos = pos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int ponsition, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.div_dialog_list_item, null);
            holder = new ViewHolder();
            holder.image = convertView
                    .findViewById(R.id.div_dialog_item_img);
            holder.image1 = convertView.findViewById(R.id.div_image);
            holder.text = convertView
                    .findViewById(R.id.div_dialog_item_text);
            switch (textAligin) {
                case 0:
                    break;
                case 1:
                    holder.text.setGravity(Gravity.LEFT);
                    break;
                case 2:
                    holder.text.setGravity(Gravity.RIGHT);
                    break;
                default:
                    break;
            }
            if (is) {
                if (ponsition == pos) {
                    holder.image1.setVisibility(View.VISIBLE);
                } else {
                    holder.image1.setVisibility(View.GONE);
                }
            } else {
                holder.image1.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (iconsResources == null) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageResource(iconsResources[ponsition]);
        }
        holder.text.setText(titles[ponsition]);
        return convertView;
    }

    class ViewHolder {
        private TextView text;
        private ImageView image;
        private ImageView image1;

        public TextView getText() {
            return text;
        }

        public void setText(TextView text) {
            this.text = text;
        }

        public ImageView getImage() {
            return image;
        }

        public void setImage(ImageView image) {
            this.image = image;
        }

    }
}
