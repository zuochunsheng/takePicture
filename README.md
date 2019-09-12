# takephoto
拍照和相册选择工具

        // 使用方法
        //isNeedCrop 默认false
        TakephotoUtil.getInstance(this)
                .setIsNeedCrop(isNeedCrop)
                .checkPermissions(new IUploadEvent() {
                    @Override
                    public void takephotoSuccessEvent(String originUri, String cropUri) {

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
    }

