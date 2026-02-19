# News Feed Simulator_Praktikum Tugas 2 PAM ITERA
**Oleh:** Muhammad Piela Nugraha| **NIM:** 123140200

Aplikasi ini adalah simulasi aliran berita *asynchronous* menggunakan Kotlin Multiplatform (KMP), Coroutines, dan Flow.

## Fitur & Pemenuhan Rubrik Penilaian
1. **Implementasi Flow (25%):** Menggunakan `flow {}` builder untuk meng-*generate* berita baru setiap 2 detik. Ditangkap menggunakan `.collect()`.
2. **Penggunaan Operators (20%):** Menggunakan `.map` untuk memanipulasi teks judul, `.filter` (via `combine`) untuk sorting kategori berita, dan `.onEach` untuk *logging* sistem.
3. **StateFlow (20%):** Menggunakan `MutableStateFlow` untuk melacak indikator "*Berita Dibaca*" secara *real-time*.
4. **Coroutines Usage (20%):** Menggunakan blok `async` dengan `Dispatchers.Default` dan `.await()` untuk mensimulasikan penarikan *detail* berita.
5. **Bonus (+10%):** Mengimplementasikan block `try-catch` pada async Coroutines dan operator `.catch` pada aliran Flow untuk penanganan error/timeout jaringan.

## Cara Menjalankan
1. Buka project di **Android Studio**.
2. Pastikan *Gradle Sync* sudah selesai.
3. Pilih konfigurasi *Run* untuk **composeApp**.
4. Klik tombol panah hijau (**Run**) di menu navigasi atas.