package com.example.client.share;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({ShareType.TEXT, ShareType.IMAGE,
        ShareType.AUDIO, ShareType.VIDEO, ShareType.File})
@Retention(RetentionPolicy.SOURCE)
@interface ShareType {
    /**
     * Share Text
     */
    final String TEXT = "text/plain";

    /**
     * Share Image
     */
    final String IMAGE = "image/*";

    /**
     * Share Audio
     */
    final String AUDIO = "audio/*";

    /**
     * Share Video
     */
    final String VIDEO = "video/*";

    /**
     * Share File
     */
    final String File = "*/*";
}