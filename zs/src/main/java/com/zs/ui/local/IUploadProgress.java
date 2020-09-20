package com.zs.ui.local;

import com.zs.ui.local.bean.FileUpload;

public interface IUploadProgress {
    public void onProgress(FileUpload bean, String from);
}
