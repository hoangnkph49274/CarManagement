package com.example.asm_api;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private String BASE_URL = "http://192.168.16.124:3000/";
    private List<Car> carList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CarAdapter(List<Car> carList, Context context, OnItemClickListener listener) {
        this.carList = carList;
        this.context = context;
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.tvName.setText("Tên xe: " + car.getName());
        holder.tvYear.setText("Năm sản xuất: " + car.getYear());
        holder.tvPrice.setText("Giá: " + car.getPrice());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        // Set click listeners for the buttons
        var ref = new Object() {
            String id;
        };
        holder.btnEdit.setOnClickListener(v -> {
            showEditDialog(car, position);
        });
        Log.d("xxxxxxxxxxxx", "onBindViewHolder: " + car.getId());
        holder.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Cảnh báo");
            builder.setIcon(R.drawable.iconwarning);
            builder.setMessage("Bạn có chắc chắn muốn xóa xe này không?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                Log.d("xxxxxxxxx", "Id"+car.getId());
                deleteCar(car.getId(), position);
                dialog.dismiss();
            });
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
            builder.create().show();

        });
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(car);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void setCarList(List<Car> carList) {
        this.carList = carList;
        notifyDataSetChanged();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvYear, tvPrice;
        ImageView btnEdit, btnDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void showEditDialog(Car car, int position) {
        // Tạo và hiển thị dialog chỉnh sửa ở đây
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.diaglog_update, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtManufacturer = dialogView.findViewById(R.id.edtBrand);
        EditText edtYear = dialogView.findViewById(R.id.edtYear);
        EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
        Button btnSua = dialogView.findViewById(R.id.btnSua);
        Button btnHuy = dialogView.findViewById(R.id.btnHuy);

        AlertDialog dialog = builder.create();


        // Điền dữ liệu hiện tại vào các trường
        edtName.setText(car.getName());
        edtManufacturer.setText(car.getManufacturer());
        edtYear.setText(String.valueOf(car.getYear()));
        edtPrice.setText(String.valueOf(car.getPrice()));
        edtDescription.setText(car.getDescription());


        btnSua.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String manufacturer = edtManufacturer.getText().toString();
            int year = Integer.parseInt(edtYear.getText().toString());
            int price = Integer.parseInt(edtPrice.getText().toString());
            String description = edtDescription.getText().toString();

            // Cập nhật thông tin xe
            updateCar(car.getId(), name,manufacturer, year, price, description,position);
            dialog.dismiss();
        });

        btnHuy.setOnClickListener(v -> {
            dialog.dismiss(); // Hủy dialog nếu người dùng bấm hủy
        });

        dialog.show();
    }

    // Hàm gọi API để cập nhật thông tin xe
    private void updateCar(String carId, String name, String manufacturer, int year, int price, String description, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Car updatedCar = new Car(name, manufacturer, year, price, description);
        updatedCar.setId(carId); // Đặt lại ID cho xe

        apiService.updateCar(carId, updatedCar).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if (response.isSuccessful()) {
                    // Đảm bảo giữ lại id của xe cũ
                    updatedCar.setId(carId);
                    carList.set(position, updatedCar);  // Cập nhật lại danh sách xe
                    notifyItemChanged(position);  // Cập nhật lại item trong RecyclerView
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm gọi API để xóa xe
    private void deleteCar(String carId, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        apiService.deleteCar(carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if (response.isSuccessful()) {
                    carList.remove(position);  // Xóa xe khỏi danh sách
                    notifyItemRemoved(position);  // Xóa item khỏi RecyclerView
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi xóa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.d("ZZZZZZZZZZ", "Lỗi: "+t.getMessage());
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClick(Car car);
    }

}
