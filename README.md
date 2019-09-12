# takephoto
拍照和相册选择

        //引用(最新)
        implementation 'com.github.zuochunsheng:takephoto:1.1'

        // 使用方法
        //isNeedCrop 是否裁剪 默认false
        TakephotoUtil.getInstance(this)
                .setIsNeedCrop(isNeedCrop)
                .checkPermissions(new IUploadEvent() {
                    @Override
                    public void takephotoSuccessEvent(String originUri, String cropUri) {
                        Log.e("tag", "原始路径:" + originUri);
                        //Log.e("zuo", "裁剪后缓存的路径 :" + cropUri);
                        Glide.with(MainActivity.this)
                                .load(originUri)
                                .placeholder(R.mipmap.ic_launcher)
                                .centerCrop()
                                .into(imageView);
                    }

                    @Override
                    public void takephotoErrorEvent(String error) {
                        Log.e("zuo", "takephotoErrorEvent =" + error);
                    }
                });


   1.1 修复从相册选择照片 文件大小为0的问题
