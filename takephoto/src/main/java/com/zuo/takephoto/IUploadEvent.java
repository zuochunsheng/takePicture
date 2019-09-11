package com.zuo.takephoto;

/**
 * anther: created by zcs on 2019/9/11 18 : 14
 * description :
 */
public interface IUploadEvent {


    //拍照裁剪成功
    /*
     * originUri : 原始 uri
     * cropUri : 裁剪后缓存的路径 uri
     *
     */
    void takephotoSuccessEvent(String originUri,String cropUri);
    //拍照裁剪失败
    void takephotoErrorEvent(String error);

}
