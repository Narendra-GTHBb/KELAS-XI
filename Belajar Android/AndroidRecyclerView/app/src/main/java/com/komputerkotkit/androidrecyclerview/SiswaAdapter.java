package com.komputerkotkit.androidrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ViewHolder> {

    // Deklarasi variabel Context dan List<Siswa>
    private Context context;
    private List<Siswa> siswaList;

    // Konstruktor yang menerima Context dan List<Siswa> sebagai parameter
    public SiswaAdapter(Context context, List<Siswa> siswaList) {
        this.context = context;
        this.siswaList = siswaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_siswa.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_siswa, parent, false);
        // Kembalikan objek ViewHolder yang baru dengan View yang telah di-inflate
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Dapatkan objek Siswa dari siswaList berdasarkan position
        Siswa siswa = siswaList.get(position);

        // Set teks tvNama dan tvAlamat di ViewHolder dengan data dari objek Siswa
        holder.tvNama.setText(siswa.getNama());
        holder.tvAlamat.setText(siswa.getAlamat());

        // TAMBAHAN: Click listener untuk mengambil data dan menampilkan Toast message
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil data dari objek siswa yang diklik
                String nama = siswa.getNama();
                String alamat = siswa.getAlamat();

                // Format message dan tampilkan dengan Toast default
                String message = "Nama : " + nama + " Alamat : " + alamat;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });

        // TAMBAHAN BARU: Click listener untuk menu PopupMenu
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat PopupMenu dengan anchor view tv_menu
                PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);

                // Inflate menu dari menu_option.xml
                popupMenu.getMenuInflater().inflate(R.menu.menu_option, popupMenu.getMenu());

                // Set OnMenuItemClickListener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.menu_simpan) {
                            // Tampilkan Toast untuk menyimpan data
                            Toast.makeText(context, "Simpan Data " + siswa.getNama(),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.menu_hapus) {
                            // Simpan nama siswa sebelum dihapus untuk Toast
                            String namaSiswa = siswa.getNama();

                            // Hapus data dari list
                            siswaList.remove(holder.getAdapterPosition());

                            // Notify adapter bahwa data telah berubah
                            notifyDataSetChanged();

                            // Tampilkan Toast konfirmasi penghapusan
                            Toast.makeText(context, namaSiswa + " Sudah di Hapus",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

                // Tampilkan PopupMenu
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan ukuran dari siswaList
        return siswaList.size();
    }

    // Inner Class ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Deklarasi TextView untuk tvNama dan tvAlamat
        TextView tvNama;
        TextView tvAlamat;
        TextView tvMenu; // TAMBAHAN BARU: TextView untuk menu

        // Konstruktor ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi TextView menggunakan itemView.findViewById()
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tv_menu); // TAMBAHAN BARU: Inisialisasi tvMenu
        }
    }
}