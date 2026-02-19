# Tugas PAM 2 News Feed Simulator

Aplikasi Android berbasis **Kotlin Multiplatform (KMP) & Jetpack Compose** yang mensimulasikan aliran berita secara *real-time*. Proyek ini dikembangkan untuk memenuhi Tugas Praktikum Mata Kuliah **Pengembangan Aplikasi Mobile (PAM)**.

**Dikerjakan Oleh:**
* **Nama:** Muhammad Piela Nugraha
* **NIM:** 123140200
* **Program Studi:** Teknik Informatika

---

##  Pemenuhan Kriteria Tugas 

Aplikasi ini telah memenuhi seluruh kriteria wajib dan bonus yang diminta pada modul praktikum:

1. **Implementasi Flow (25%):** Menggunakan `flow { }` builder untuk mensimulasikan data berita baru yang di-`emit` secara konsisten setiap 2 detik.
2. **Penggunaan Operators (20%):** * `.filter` untuk menyaring berita berdasarkan kategori.
   * `.map` untuk mentransformasi data (menambahkan ikon 📰 pada judul).
   * `.onEach` untuk mencatat *log system* di *console*.
3. **StateFlow Implementation (20%):** Menggunakan `MutableStateFlow` untuk melacak dan menyimpan *state* jumlah berita yang sudah dibaca secara *real-time*.
4. **Coroutines Usage (20%):** Menggunakan `scope.launch`, `async(Dispatchers.Default)`, dan `.await()` untuk mengambil detail berita secara *asynchronous* tanpa memblokir UI.
5. **Bonus - Error Handling (+10%):**
   * Menggunakan operator `.catch` pada aliran *Flow* utama.
   * Menerapkan pembungkus `coroutineScope` dan `try-catch` pada proses *async* untuk mencegah *Force Close* saat terjadi simulasi *Network Timeout*.

---

##  Fitur Unggulan & UI/UX

Selain memenuhi syarat teknis, aplikasi ini mengedepankan pengalaman pengguna (UX) yang menarik:
* **Thematic Color Cards:** Setiap kartu berita memiliki rona warna latar (*soft pastel*) dan *badge* warna solid yang menyesuaikan dengan kategorinya (Biru untuk Tech, Hijau untuk Sports, dll).
* **Smooth Animations:** Dilengkapi dengan `animateColorAsState` dan `AnimatedVisibility` agar transisi perubahan warna kartu (saat berita dibaca) dan pemunculan detail berita terasa sangat mulus.
* **Bulletproof Architecture:** Tahan terhadap *NullPointerException*. Jika data gagal dimuat, aplikasi tidak akan *crash*, melainkan menampilkan pesan *error* visual berwarna merah.

---

## Langkah Singkat Pembuatan

1. **Setup Environment:** Inisialisasi proyek Kotlin Multiplatform dan menambahkan *dependency* `kotlinx-coroutines-core`.
2. **Data & State Management:** Membuat `NewsViewModel` yang menampung *dummy data* dan mengelola *state* menggunakan `StateFlow`.
3. **Flow Generator:** Membangun coroutine *Flow* yang secara konstan menghasilkan data berita acak setiap 2 detik dan memasukkannya ke dalam *list*.
4. **UI Construction:** Membangun antarmuka menggunakan Jetpack Compose (Modern Header, Category Filters, dan News Card).
5. **Async & Exception Handling:** Mengintegrasikan logika klik untuk memuat detail berita dengan `async`, simulasi *delay* jaringan, dan penanganan *error*.

---

##  Dokumentasi Layar (Screenshots)


* **Tampilan Utama (All News):** <img width="421" height="796" alt="image" src="https://github.com/user-attachments/assets/468470fd-7c4a-42e3-923d-911dca6b6dfd" />

<img width="1915" height="879" alt="image" src="https://github.com/user-attachments/assets/cba2e426-99da-4a49-a795-e16037b7ca31" />

* **Fitur Filter Kategori:** <img width="374" height="141" alt="image" src="https://github.com/user-attachments/assets/69cb372c-651e-4df7-abf2-09e9b09f687e" />

* **Detail Berita & Indikator Loading:** `<img width="359" height="822" alt="image" src="https://github.com/user-attachments/assets/b2257f6b-1c87-4eb2-b568-10d33c66201d" />

<img width="1808" height="761" alt="image" src="https://github.com/user-attachments/assets/8b057a33-d32e-4421-ab7d-70ea89bd5ee5" />


* **Simulasi Error Handling (Network Timeout):** <img width="372" height="191" alt="image" src="https://github.com/user-attachments/assets/b4f75c59-0540-4044-af5f-683b46ad3907" />


---
