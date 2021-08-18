package com.example.client.ui.adapter;

import com.example.client.ui.fragment.ContactsFragment;
import com.example.client.ui.fragment.FindFragment;
import com.example.client.ui.fragment.MessageFragment;
import com.example.client.ui.fragment.MineFragment;

public class HomeFragmentFactory {
    static HomeFragmentFactory mInstance;
    private MessageFragment mMessageFragment;
    private ContactsFragment mContactsFragment;
    private FindFragment mFindFragment;
    private MineFragment mMineFragment;

    public HomeFragmentFactory() {
    }

    public static HomeFragmentFactory getInstance() {
        if (mInstance == null) {
            synchronized (HomeFragmentFactory.class) {
                if (mInstance == null) {
                    mInstance = new HomeFragmentFactory();
                }
            }
        }
        return mInstance;
    }

    /**
     * 消息
     *
     * @return
     */
    public MessageFragment getMessageFragment() {
        if (mMessageFragment == null) {
            synchronized (MessageFragment.class) {
                if (mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                }
            }
        }
        return mMessageFragment;
    }

    /**
     * 通讯录
     *
     * @return
     */
    public ContactsFragment getContactsFragment() {
        if (mContactsFragment == null) {
            synchronized (ContactsFragment.class) {
                if (mContactsFragment == null) {
                    mContactsFragment = new ContactsFragment();
                }
            }
        }
        return mContactsFragment;
    }

    /**
     * 发现
     *
     * @return
     */
    public FindFragment getFindFragment() {
        if (mFindFragment == null) {
            synchronized (FindFragment.class) {
                if (mFindFragment == null) {
                    mFindFragment = new FindFragment();
                }
            }
        }
        return mFindFragment;
    }

    /**
     * 我
     *
     * @return
     */
    public MineFragment getMineFragment() {
        if (mMineFragment == null) {
            synchronized (MineFragment.class) {
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                }
            }
        }
        return mMineFragment;
    }
}
