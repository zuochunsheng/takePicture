# takepicture
拍照和相册选择

        //引用(最新)
        implementation 'com.github.zuochunsheng:takepicture:v1.3'

        // 使用方法
       TakepictureUtil.getInstance(this)
       .checkPermissions(new IUploadEvent() {
           @Override
           public void takepictureSuccessEvent(String originUri) {
               Log.e("zuo", "原始路径:" + originUri);
               //Log.e("zuo", "原始.getAbsolutePath<> " + new File(originUri).getAbsolutePath() + " ,大小<> " + new File(originUri).length());

               Glide.with(MainActivity.this)
                       .load(originUri)
                       .placeholder(R.mipmap.ic_launcher)
                       .centerCrop()
                       .into(iv);
           }

       });



