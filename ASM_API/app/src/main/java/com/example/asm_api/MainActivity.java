package com.example.asm_api;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private String BASE_URL = "http://192.168.16.124:3000/";
    private RecyclerView recyclerView;
    private CarAdapter adapter;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.rv);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        apiService.getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if(response.isSuccessful()){
                    List<Car> carList = response.body();
                    adapter = new CarAdapter(carList, MainActivity.this, car -> {
                        // Hiển thị dialog khi nhấn vào item
                        showCarDetailsDialog(car);
                    });
                    recyclerView.setAdapter(adapter);

                    recyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(MainActivity.this, "Lỗi load", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                Log.d("zzzzzzzzzzz", "Lỗi: "+t.getMessage());
            }
        });
        btnAdd.setOnClickListener(v -> {
            // Tạo dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // Thiết lập Layout cho Dialog
            View dialogView = getLayoutInflater().inflate(R.layout.diaglog_add, null);
            builder.setView(dialogView);

            // Tạo Dialog từ Builder
            AlertDialog dialog = builder.create();

            EditText edtName = dialogView.findViewById(R.id.edtName);
            EditText edtManufacturer = dialogView.findViewById(R.id.edtBrand);
            EditText edtYear = dialogView.findViewById(R.id.edtYear);
            EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
            EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
            Button btnThem = dialogView.findViewById(R.id.btnThem);
            Button btnCancel = dialogView.findViewById(R.id.btnHuy);

            // Thiết lập nút xác nhận (Thêm)
            btnThem.setOnClickListener(v1 -> {
                String name = edtName.getText().toString();
                String manufacturer = edtManufacturer.getText().toString();
                String yearStr = edtYear.getText().toString();
                String priceStr = edtPrice.getText().toString();
                String description = edtDescription.getText().toString();

                if (name.isEmpty() || manufacturer.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int year = Integer.parseInt(yearStr);
                    int price = Integer.parseInt(priceStr);
                    if (year < 1900 || year > 2024) {
                        Toast.makeText(MainActivity.this, "Năm không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (price <= 0) {
                        Toast.makeText(MainActivity.this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Gọi API để thêm xe
                    addCar(name, manufacturer, year, price, description);

                    // Tắt dialog khi thêm thành công
                    dialog.dismiss();  // Tắt dialog

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập giá trị hợp lệ cho năm và giá", Toast.LENGTH_SHORT).show();
                }
            });

            // Thiết lập nút hủy
            btnCancel.setOnClickListener(v1 -> {
                dialog.dismiss();  // Tắt dialog khi bấm hủy
            });

            // Kiểm tra xem dialog có thể hiển thị không
            Log.d("DialogTest", "Dialog should be showing now");

            // Hiển thị dialog
            dialog.show();
        });

    }
    private void addCar(String name, String manufacturer, int year, int price, String description) {
        Gson gson = new GsonBuilder()
                .setLenient()  // Kích hoạt chế độ lenient
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Car newCar = new Car(name, manufacturer, year, price, description);
        apiService.addCar(newCar).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if (response.isSuccessful()) {
                    // Trực tiếp sử dụng response.body() mà không cần chuyển thành chuỗi
                    Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    fetchCars();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi load", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                Log.d("zzzzzzzzzzz", "Lỗi: " + t.getMessage());
            }
        });
    }
    public void fetchCars(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        apiService.getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if(response.isSuccessful()){
                    recyclerView.removeAllViews();
                    List<Car> carList = response.body();
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter = new CarAdapter(carList, MainActivity.this, car -> {
                        // Hiển thị dialog khi nhấn vào item
                        showCarDetailsDialog(car);
                    });
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(MainActivity.this, "Load thành công", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Lỗi load 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                Log.d("zzzzzzzzzzz", "Lỗi 2: "+t.getMessage());
            }
        });
    }
    private void showCarDetailsDialog(Car car) {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_car_details, null);
        builder.setView(dialogView);

        // Ánh xạ các thành phần trong dialog
        TextView tvName = dialogView.findViewById(R.id.tvCarName);
        TextView tvManufacturer = dialogView.findViewById(R.id.tvCarManufacturer);
        TextView tvYear = dialogView.findViewById(R.id.tvCarYear);
        TextView tvPrice = dialogView.findViewById(R.id.tvCarPrice);
        TextView tvDescription = dialogView.findViewById(R.id.tvCarDescription);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        // Gán dữ liệu cho các thành phần
        tvName.setText("Tên xe: " + car.getName());
        tvManufacturer.setText("Hãng: " + car.getManufacturer());
        tvYear.setText("Năm: " + car.getYear());
        tvPrice.setText("Giá: " + car.getPrice());
        tvDescription.setText("Mô tả: " + car.getDescription());
        AlertDialog dialog = builder.create();
        // Đóng dialog khi nhấn nút
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Hiển thị dialog

        dialog.show();
    }

}