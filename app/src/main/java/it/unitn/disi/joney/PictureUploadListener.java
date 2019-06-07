package it.unitn.disi.joney;

import android.widget.ImageView;

public interface PictureUploadListener {
    public void onRemovePicture(int imgViewIndex);

    public void onChangePicture(int index);
}
