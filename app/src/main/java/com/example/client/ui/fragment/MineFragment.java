package com.example.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.base.BaseFragment;
import com.example.client.ui.activity.AchieveDataActivity;
import com.example.client.ui.activity.CostQueryActivity;
import com.example.client.ui.activity.PersonIntroduce;

public class MineFragment extends BaseFragment implements View.OnClickListener{

    private TextView achieve_data;
    private LinearLayout person_introduce_show;
    private LinearLayout cost_query_show;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);
        achieve_data = (TextView) getViewById(R.id.tv_achieve_data);
        person_introduce_show=(LinearLayout)getViewById(R.id.person_introduce_show);
        cost_query_show=(LinearLayout)getViewById(R.id.cost_query_show);
    }

    @Override
    protected void initClick() {
        super.initClick();
        achieve_data.setOnClickListener(this);
        person_introduce_show.setOnClickListener(this);
        cost_query_show.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_achieve_data:
                Intent intent_mine_achieve_data=new Intent(getActivity(), AchieveDataActivity.class);
                getActivity().startActivity(intent_mine_achieve_data);
                break;

            case R.id.person_introduce_show:
                Intent intent_mine_person_introduce=new Intent(getActivity(), PersonIntroduce.class);
                getActivity().startActivity(intent_mine_person_introduce);
                break;

            case R.id.cost_query_show:
                Intent intent_cost_query_show=new Intent(getActivity(), CostQueryActivity.class);
                getActivity().startActivity(intent_cost_query_show);
                break;

            default:
                break;
        }
    }
}
