package com.example.appbanhang.retrofit;

import io.reactivex.rxjava3.core.Observable; // Sử dụng Observable từ RxJava

import com.example.appbanhang.model.LoaiSpModel;

import retrofit2.http.GET;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaisp();
}
