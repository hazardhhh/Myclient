package com.example.client.common.ui.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.example.client.utils.ImageLoaderKit;
import  com.example.client.R;
import  com.example.client.utils.UcstarUIKit;
import  com.example.client.utils.cache.ConfigureCache;
import com.ucstar.android.sdk.nos.model.NosThumbParam;
import com.ucstar.android.sdk.nos.util.NosThumbImageUtil;
import com.ucstar.android.sdk.team.constant.TeamTypeEnum;
import com.ucstar.android.sdk.team.model.Team;
import com.ucstar.android.sdk.uinfo.UserInfoProvider;

/**
 * Created by ucstar on 2015/11/13.
 */
public class HeadImageView extends RectImageView {

    public static final int DEFAULT_AVATAR_THUMB_SIZE = (int) UcstarUIKit.getContext().getResources().getDimension(R.dimen.avatar_max_size);
    public static final int DEFAULT_AVATAR_NOTIFICATION_ICON_SIZE = (int) UcstarUIKit.getContext().getResources().getDimension(R.dimen.avatar_notification_size);

    private DisplayImageOptions options = createImageOptions();

    private static final DisplayImageOptions createImageOptions() {
        int defaultIcon = UcstarUIKit.getUserInfoProvider().getDefaultIconResId();
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIcon)
                .showImageOnFail(defaultIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public HeadImageView(Context context) {
        super(context);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void loadSystemIcon(String id) {
        setImageResource(R.drawable.ic_notice_system);
    }
    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param account
     */
    public void loadBuddyAvatar(String account) {
        loadBuddyAvatar(account, DEFAULT_AVATAR_THUMB_SIZE);
    }

    public void loadBuddyAvatarWithNotTag(String account) {
        // 先显示默认头像
        setImageResource(UcstarUIKit.getUserInfoProvider().getDefaultIconResId());
        doLoadImageNotTag(true, account, ConfigureCache.getInstance().getAvatarUrl(), DEFAULT_AVATAR_THUMB_SIZE);
    }

    /**
     * 加载用户头像（原图）
     *
     * @param account
     */
    public void loadBuddyOriginalAvatar(String account) {
        loadBuddyAvatar(account, 0);
    }

    /**
     * 加载用户头像（指定缩略大小）
     *
     * @param account
     * @param thumbSize 缩略图的宽、高
     */
    private void loadBuddyAvatar(final String account, final int thumbSize) {

        // 先显示默认头像
        setImageResource(UcstarUIKit.getUserInfoProvider().getDefaultIconResId());
        // 判断是否需要ImageLoader加载
        final UserInfoProvider.UserInfo userInfo = UcstarUIKit.getUserInfoProvider().getUserInfo(account);
        boolean needLoad = userInfo != null && ImageLoaderKit.isImageUriValid(ConfigureCache.getInstance().getAvatarUrl());
        doLoadImage(needLoad, account, userInfo != null ? ConfigureCache.getInstance().getAvatarUrl() : null, thumbSize);
    }

    public void loadServiceOnLineAvatar(final String account) {

        // 先显示默认头像
        setImageResource(UcstarUIKit.getUserInfoProvider().getDefaultIconResId());
        // 判断是否需要ImageLoader加载
//        final UserInfoProvider.UserInfo userInfo = UcstarUIKit.getUserInfoProvider().getUserInfo(account);
//        boolean needLoad = userInfo != null && ImageLoaderKit.isImageUriValid(ConfigureCache.getInstance().getAvatarUrl());
        doLoadImage(true, account, ConfigureCache.getInstance().getAvatarUrl(), DEFAULT_AVATAR_THUMB_SIZE);
    }


    public void loadTeamIcon(String tid) {
        Bitmap bitmap = UcstarUIKit.getUserInfoProvider().getTeamIcon(tid);
        setImageBitmap(bitmap);
    }

    public void loadTeamIconByTeam(final Team team) {
        // 先显示默认头像
        if (team != null) {
            TeamTypeEnum teamType = team.getType();
            if (TeamTypeEnum.Normal == teamType) {
                setImageResource(R.drawable.nim_avatar_normal_group);
            } else if (TeamTypeEnum.Advanced == teamType) {
                setImageResource(R.drawable.nim_avatar_group);
            } else {
                setImageResource(R.drawable.nim_avatar_group);
            }
        }else {
            setImageResource(R.drawable.nim_avatar_group);
        }

        // 判断是否需要ImageLoader加载
        //boolean needLoad = team != null && ImageLoaderKit.isImageUriValid(team.getIcon());
        String tag = team != null ? team.getId() : null;
        String url = team != null ? team.getIcon() : null;
        //doLoadImage(needLoad, tag, url, DEFAULT_AVATAR_THUMB_SIZE);
        boolean needLoad = ImageLoaderKit.isImageUriValid(ConfigureCache.getInstance().getAvatarUrl());
        if(team==null){
            return;
        }
        doLoadImage(needLoad, team.getId(), team != null ? ConfigureCache.getInstance().getAvatarUrl() : null, DEFAULT_AVATAR_THUMB_SIZE);
    }

    public void loadBroadcast(String id) {
        // 广播默认头像
        int resId = R.drawable.broadcast;
        if ("notice_broadcast".equals(id))
            resId = R.drawable.notice_broadcast;
        setImageResource(resId);
    }

    /**
     * ImageLoader异步加载
     */
    private void doLoadImage(final boolean needLoad, final String tag, final String url, final int thumbSize) {
        if (needLoad) {
            setTag(tag); // 解决ViewHolder复用问题
            /**
             * 若使用UcSTAR云存储，这里可以设置下载图片的压缩尺寸，生成下载URL
             * 如果图片来源是非UcSTAR云存储，请不要使用NosThumbImageUtil
             */
//            final String thumbUrl = makeAvatarThumbNosUrl(url, thumbSize);
            final String thumbUrl = url + "?imgfile=" + tag + ".jpeg";//拼接头像的url地址

            //加载图片之前 先清理图片缓存 避免新的图片没有及时更新
//            ImageLoader.getInstance().clearMemoryCache();
//            ImageLoader.getInstance().clearDiskCache();
            // 异步从cache or NOS加载图片
            ImageLoader.getInstance().displayImage(thumbUrl, new NonViewAware(new ImageSize(thumbSize, thumbSize),
                    ViewScaleType.CROP), options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (getTag() != null && getTag().equals(tag)) {
                        setImageBitmap(loadedImage);
                    }
                }
            });
        } else {
            setTag(null);
        }
    }



    /**
     * ImageLoader异步加载
     */
    private void doLoadImageNotTag(final boolean needLoad, final String tag, final String url, final int thumbSize) {
        if (needLoad) {
            /**
             * 若使用UcSTAR云存储，这里可以设置下载图片的压缩尺寸，生成下载URL
             * 如果图片来源是非UcSTAR云存储，请不要使用NosThumbImageUtil
             */
//            final String thumbUrl = makeAvatarThumbNosUrl(url, thumbSize);
            final String thumbUrl = url + "?imgfile=" + tag + ".jpeg";//拼接头像的url地址

            // 异步从cache or NOS加载图片
            ImageLoader.getInstance().displayImage(thumbUrl, new NonViewAware(new ImageSize(thumbSize, thumbSize),
                    ViewScaleType.CROP), options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        setImageBitmap(loadedImage);
                }
            });
        }
    }

    /**
     * 解决ViewHolder复用问题
     */
    public void resetImageView() {
        setImageBitmap(null);
    }

    /**
     * 生成头像缩略图NOS URL地址（用作ImageLoader缓存的key）
     */
    private static String makeAvatarThumbNosUrl(final String url, final int thumbSize) {
        return thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url, NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : url;
    }

    public static String getAvatarCacheKey(final String url) {
        return makeAvatarThumbNosUrl(url, DEFAULT_AVATAR_THUMB_SIZE);
    }
}
