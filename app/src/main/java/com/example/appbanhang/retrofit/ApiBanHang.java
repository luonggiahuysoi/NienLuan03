package com.example.appbanhang.retrofit;

import io.reactivex.rxjava3.core.Observable;
import com.example.appbanhang.model.LoaiSpModel;
import com.example.appbanhang.model.SanPhamMoiModel;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiBanHang {
    @POST("getloaisp.php")
    Observable<LoaiSpModel> getLoaisp();

    @POST("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @FormUrlEncoded
    @POST("chitiet.php")
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );
}
