package com.example.appbanhang.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbanhang.R;
import com.example.appbanhang.adapter.DienThoaiAdapter;
import com.example.appbanhang.model.SanPhamMoi;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.example.appbanhang.untils.Untils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DienThoaiActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;

    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int page = 1;
    int loai;

    DienThoaiAdapter adapterDt;

    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dien_thoai);
        apiBanHang = RetrofitClient.getInstance(Untils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai", 1);
        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    isLoading = true;
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        // Thêm mục tạm thời "đang tải"
        sanPhamMoiList.add(null);
        adapterDt.notifyItemInserted(sanPhamMoiList.size() - 1);

        // Tải dữ liệu sau một thời gian trễ
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.remove(sanPhamMoiList.size() - 1); // Xóa mục tạm thời "đang tải"
                adapterDt.notifyItemRemoved(sanPhamMoiList.size());

                page++;
                getData(page);
                isLoading = false;
            }
        }, 1500); // Giảm thời gian trễ
    }

    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterDt == null) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    adapterDt = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterDt);
                                } else {
                                    // Xóa mục tạm thời "đang tải" ngay khi có dữ liệu mới
                                    sanPhamMoiList.remove(sanPhamMoiList.size() - 1);
                                    adapterDt.notifyItemRemoved(sanPhamMoiList.size());

                                    int vitri = sanPhamMoiList.size();
                                    int soluongadd = sanPhamMoiModel.getResult().size();
                                    sanPhamMoiList.addAll(sanPhamMoiModel.getResult());
                                    adapterDt.notifyItemRangeInserted(vitri, soluongadd);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Hết sản phẩm rồi!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server", Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleview_dt);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
