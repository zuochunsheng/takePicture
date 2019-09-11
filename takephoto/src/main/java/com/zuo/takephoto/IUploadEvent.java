package com.zuo.takephoto;

/**
 * anther: created by zcs on 2019/9/11 18 : 14
 * description :
 */
public interface IUploadEvent {


    //拍照裁剪成功
    void takephotoSuccessEvent(String uri, String origin);
    //拍照裁剪失败
    void takephotoErrorEvent(String error);

}
