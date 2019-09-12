package com.zuo.takepicture;

/**
 * anther: created by zcs on 2019/9/11 18 : 14
 * description :
 */
public interface IUploadEvent {


    //拍照成功
    /*
     * originUri : 原始 uri
     *
     */
    void takepictureSuccessEvent(String originUri);

}
