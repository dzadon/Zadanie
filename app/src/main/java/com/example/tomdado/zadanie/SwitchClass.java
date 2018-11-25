package com.example.tomdado.zadanie;

public class SwitchClass {

    public Class getActivity(int id){
       Class cls;
        switch (id){
            case R.id.navProfile:
                cls = ProfileActivity.class;
                break;
            case R.id.navUploadFile:
                cls = UploadFileActivity.class;
                break;
            default:
                cls = ProfileActivity.class;
                break;
        }
        return cls;
    }
}
