package com.example.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.base.BaseFragment;

public class MessageFragment extends BaseFragment {
    // 声明组件
    private ListView listView;

    private String[] titles = {"测试1", "测试2", "测试3", "测试4", "测试5"};
    private int[] icons = {R.drawable.message_text_img, R.drawable.message_text_img, R.drawable.message_text_img,
            R.drawable.message_text_img, R.drawable.message_text_img};
    private String[] contents = {"u? ff", "u? a", "ha", "u? df", "hazard"};
    private String[] times = {"13:01", "18:23", "02:24", "11:55", "23:51"};

    @Override
    protected int getLayoutId() {
        return R.layout.message_fragement;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);
        //初始化组件
        listView = (ListView) getViewById(R.id.listView);
        //创建自定义的适配器，用于把数据显示在组件上
        MessageAdapter adapter = new MessageAdapter();
        //设置适配器
        listView.setAdapter(adapter);
    }

    class MessageAdapter extends BaseAdapter {
        // 声明内部类对象
        private ViewHolder viewHolder;

        // 返回的总个数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return titles.length;
        }

        // 返回每个条目对应的数据
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return titles[position];
        }

        // 返回的id
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        // 返回这个条目对应的控件对象
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 判断当前条目是否为null
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.message_item, null);
                viewHolder.message_text_img = (ImageView) convertView.findViewById(R.id.img1);
                viewHolder.message_text_title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.message_text_content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.message_text_time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.message_text_code = (ImageView) convertView.findViewById(R.id.code);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.message_text_img.setBackgroundResource(icons[position]);
            viewHolder.message_text_title.setText(titles[position]);
            viewHolder.message_text_content.setText(contents[position]);
            viewHolder.message_text_time.setText(times[position]);

            return convertView;
        }

        /**
         * 内部类 记录单个条目中所有属性
         */
        class ViewHolder {
            public ImageView message_text_img;
            public TextView message_text_title;
            public TextView message_text_content;
            public TextView message_text_time;
            public ImageView message_text_code;
        }
    }
}
