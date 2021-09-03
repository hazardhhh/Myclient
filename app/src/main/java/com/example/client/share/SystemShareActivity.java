package com.example.client.share;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.ProviderInfo;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.alibaba.android.arouter.facade.annotation.Autowired;
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.alibaba.android.arouter.launcher.ARouter;
//import com.shenhua.sdk.uikit.PreferencesUcStar;
//import com.shenhua.sdk.uikit.R;
//import com.shenhua.sdk.uikit.common.activity.UI;
//import com.shenhua.sdk.uikit.model.ToolBarOptions;
//import com.shenhua.sdk.uikit.recent.RecentContactsCallback;
//import com.shenhua.sdk.uikit.recent.RecentContactsFragmentAddMessage;
//import com.shenhua.sdk.uikit.session.constant.RouterConstant;
//import com.shenhua.sdk.uikit.utils.GlobalToastUtils;
//import com.shenhua.shanghui.login.LoginActivity;
//import com.shenhua.shanghui.session.SessionHelper;
//import com.ucstar.android.SDKSharedPreferences;
//import com.ucstar.android.sdk.UcSTARSDKClient;
//import com.ucstar.android.sdk.msg.MsgService;
//import com.ucstar.android.sdk.msg.attachment.MsgAttachment;
//import com.ucstar.android.sdk.msg.model.IMMessage;
//import com.ucstar.android.sdk.msg.model.RecentContact;
//
//import java.io.File;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//

public class SystemShareActivity{
    public static final int REQUEST_CODE_LOGIN=520;
}




///**
// * @author: qingqi
// * @date: 2019/12/10
// */
//@Route(path = RouterConstant.SystemShareAcivity)
//public class SystemShareActivity extends UI {
//
//    ArrayList<String> fileAddress = new ArrayList<>();
//
//    @Autowired
//    String editAddress;
//
//    public static final int REQUEST_CODE_LOGIN=520;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ARouter.getInstance().inject(this);
//        setContentView(R.layout.system_share_activity);
//
//        if(isUnLogin()){
//            LoginActivity.startShareLogin(this,REQUEST_CODE_LOGIN);
//        }else {
//            addRecentContactsFragment();
//        }
//        getUrl();
//        ToolBarOptions options = new ToolBarOptions();
//        options.titleId = com.shenhua.shanghui.R.string.share;
//        if(!TextUtils.isEmpty(editAddress)){
//            fileAddress.add(editAddress);
//            options.titleId = com.shenhua.shanghui.R.string.send;
//        }
//        setToolBar(com.shenhua.shanghui.R.id.toolbar, options);
//    }
//
//    private boolean isUnLogin(){
//        String account = PreferencesUcStar.getUserAccount();
//        String token = SDKSharedPreferences.getInstance().getAccessToken();
//
//        return TextUtils.isEmpty(account) || TextUtils.isEmpty(token);
//    }
//
//    private void getUrl() {
//        fileAddress.clear();
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        if(Intent.ACTION_SEND_MULTIPLE.equals(action)){
//
//            ArrayList<Parcelable> temp = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//            if(temp!=null){
//                if(temp.size()>9){
//                    GlobalToastUtils.showHintShort("一次性最多分享9张");
//                }
//               for(int i=0;i<temp.size()&&i<9;i++) {
//                   Uri uri = (Uri)(temp.get(i));
//                   getRealPath(uri);
//               }
//
//            }
//        }else if (Intent.ACTION_SEND.equals(action)) {
//            Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
////            Uri uri = intent.getData();
//            getRealPath(uri);
//
//        }
//    }
//
//      private void getRealPath(Uri uri){
//        String filename = uri.getPath();
//        if (String.valueOf(uri) != null
//                && String.valueOf(uri).contains("content")) {
//            boolean kkk = false;
//            try {
//                filename = getFilePathFromContentUri(uri,getContentResolver());
//                if (TextUtils.isEmpty(filename)) {
//                    kkk = true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                kkk = true;
//            }
//            if (kkk) {
//                filename = getFPUriToPath(this, uri);
//            }
//        }
//        fileAddress.add(filename);
//    }
//
//    public static String getFPUriToPath(Context context, Uri uri) {
//        try {
//            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
//            if (packs != null) {
//                String fileProviderClassName = FileProvider.class.getName();
//                for (PackageInfo pack : packs) {
//                    ProviderInfo[] providers = pack.providers;
//                    if (providers != null) {
//                        for (ProviderInfo provider : providers) {
//                            if (uri.getAuthority().equals(provider.authority)) {
//                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
//                                    Class<FileProvider> fileProviderClass = FileProvider.class;
//                                    try {
//                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
//                                        getPathStrategy.setAccessible(true);
//                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
//                                        if (invoke != null) {
//                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
//                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
//                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
//                                            getFileForUri.setAccessible(true);
//                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
//                                            if (invoke1 instanceof File) {
//                                                String filePath = ((File) invoke1).getAbsolutePath();
//                                                return filePath;
//                                            }
//                                        }
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                    } catch (InvocationTargetException e) {
//                                        e.printStackTrace();
//                                    } catch (IllegalAccessException e) {
//                                        e.printStackTrace();
//                                    } catch (ClassNotFoundException e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
////        private Uri getContentUri(FileCategory cat) {
////            Uri uri;
////            String volumeName = "external";
////            switch(cat) {
////                case Theme:
////                case Doc:
////                case Zip:
////                case Apk:
////                    uri = MediaStore.Files.getContentUri(volumeName);
////                    break;
////                case Music:
////                    uri = MediaStore.Audio.Media.getContentUri(volumeName);
////                    break;
////                case Video:
////                    uri = MediaStore.Video.Media.getContentUri(volumeName);
////                    break;
////                case Picture:
////                    uri = MediaStore.Images.Media.getContentUri(volumeName);
////                    break;
////                default:
////                    uri = null;
////            }
//////            Log.e(LOG_CURSOR, "getContentUri");
////            return uri;
////        }
//
//
//    // 将最近联系人列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
//    private void addRecentContactsFragment() {
//        RecentContactsFragmentAddMessage fragment = new RecentContactsFragmentAddMessage();
//        fragment.setContainerId(R.id.content_fragment);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.content_fragment, fragment);
//        transaction.commit();
////        final UI activity = (UI) getActivity();
//
//        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
////        fragment = (RecentContactsFragment) addFragment(fragment);
//
//        fragment.setCallback(new RecentContactsCallback() {
//            @Override
//            public void onRecentContactsLoaded() {
//                // 最近联系人列表加载完毕
//            }
//
//            @Override
//            public void onUnreadCountChange(int unreadCount) {
////                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
//            }
//
//            @Override
//            public void onItemClick(RecentContact recent) {
//                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
////                File file=new File(fileAddress);
//                switch (recent.getSessionType()) {
//                    case P2P:
//                        if (recent.getContactId().equals("视频会议")) {
////                            VidyoMainActivity.start(getActivity());
//                        } else {
//                            SessionHelper.startP2PSessionAddMessage(SystemShareActivity.this, recent.getContactId(), fileAddress);
//                        }
//                        break;
//                    case Team:
//                        SessionHelper.startTeamSessionAddMessage(SystemShareActivity.this, recent.getContactId(),fileAddress);
//                        break;
//                    case Broadcast:
////                        SessionHelper.startBroadcastSession(SystemShareActivity.this, recent.getContactId());
//                        break;
//                    default:
//                        break;
//                }
//                finish();
//            }
//
//            @Override
//            public String getDigestOfAttachment(MsgAttachment attachment) {
////                // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
////                // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
////                if (attachment instanceof GuessAttachment) {
////                    GuessAttachment guess = (GuessAttachment) attachment;
////                    return guess.getValue().getDesc();
////                } else if (attachment instanceof RTSAttachment) {
////                    return "[白板]";
////                } else if (attachment instanceof StickerAttachment) {
////                    return "[贴图]";
////                } else if (attachment instanceof SnapChatAttachment) {
////                    return "[阅后即焚]";
////                }
//
//                return null;
//            }
//
//            @Override
//            public String getDigestOfTipMsg(RecentContact recent) {
//                String msgId = recent.getRecentMessageId();
//                List<String> uuids = new ArrayList<>(1);
//                uuids.add(msgId);
//                List<IMMessage> msgs = UcSTARSDKClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
//                if (msgs != null && !msgs.isEmpty()) {
//                    IMMessage msg = msgs.get(0);
//                    Map<String, Object> content = msg.getRemoteExtension();
//                    if (content != null && !content.isEmpty()) {
//                        return (String) content.get("content");
//                    }
//                }
//
//                return null;
//            }
//        });
//    }
//
//    /**
//     * 将uri转换成真实路径
//     *
//     * @param selectedVideoUri
//     * @param contentResolver
//     * @return
//     */
//    public static String getFilePathFromContentUri(Uri selectedVideoUri,
//                                                   ContentResolver contentResolver) {
//        String filePath = "";
//        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
//
//        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn,
//                null, null, null);
//        // 也可用下面的方法拿到cursor
//        // Cursor cursor = this.context.managedQuery(selectedVideoUri,
//        // filePathColumn, null, null, null);
//
////        cursor.moveToFirst();
////
////        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////        filePath = cursor.getString(columnIndex);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int id = cursor.getColumnIndex(filePathColumn[0]);
//                if (id > -1)
//                    filePath = cursor.getString(id);
//            }
//            cursor.close();
//        }
//
//        return filePath;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_OK){
//            addRecentContactsFragment();
//        }else{
//            finish();
//        }
//    }
//}