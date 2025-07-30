package com.komputerkotkit.sqlitedatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    Context context;
    List<Barang> barangList;
    MainActivity mainActivity;

    public BarangAdapter(Context context, List<Barang> barangList, MainActivity mainActivity) {
        this.context = context;
        this.barangList = barangList;
        this.mainActivity = mainActivity;
        System.out.println("BarangAdapter created with " + barangList.size() + " items");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_barang, parent, false);
        System.out.println("ViewHolder created");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = barangList.get(position);

        System.out.println("Binding data at position " + position +
                ": " + barang.getBarang() +
                ", Stok: " + barang.getStok() +
                ", Harga: " + barang.getHarga());

        holder.tvBarang.setText(barang.getBarang());
        holder.tvStok.setText("Stok: " + barang.getStok());
        holder.tvHarga.setText("Rp. " + barang.getHarga());
        holder.tvMenu.setText("⋮");

        // Simple click untuk menu - menampilkan dialog pilihan
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog(barang);
            }
        });

        // Click pada item untuk edit
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.editData(barang);
                }
            }
        });
    }

    private void showActionDialog(Barang barang) {
        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(context)
                .setTitle("Pilih Aksi")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            if (mainActivity != null) {
                                mainActivity.editData(barang);
                            }
                            break;
                        case 1: // Delete
                            showDeleteConfirmation(barang);
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(Barang barang) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus " + barang.getBarang() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (mainActivity != null) {
                        mainActivity.deleteData(barang);
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    public int getItemCount() {
        int count = barangList != null ? barangList.size() : 0;
        System.out.println("getItemCount: " + count);
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvBarang, tvStok, tvHarga, tvMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBarang = itemView.findViewById(R.id.tvBarang);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvMenu = itemView.findViewById(R.id.tvMenu);

            // Debug: Check if views are found
            if (tvBarang == null) System.out.println("ERROR: tvBarang not found!");
            if (tvStok == null) System.out.println("ERROR: tvStok not found!");
            if (tvHarga == null) System.out.println("ERROR: tvHarga not found!");
            if (tvMenu == null) System.out.println("ERROR: tvMenu not found!");
        }
    }

    // Method untuk update data
    public void updateData(List<Barang> newData) {
        this.barangList = newData;
        notifyDataSetChanged();
        System.out.println("Adapter data updated with " + newData.size() + " items");
    }
}