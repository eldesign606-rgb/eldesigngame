# BrainQuest 3D – Asah Otak & Lab Logika

**BrainQuest 3D** adalah game Android 3D asah otak modern, edukatif, interaktif, dan ringan yang dirancang untuk menguji ketajaman logika, analisis, memori, spasial, dan pemecahan masalah dalam lingkungan laboratorium futuristik **Brain Lab 3D**.

---

## 🌟 Fitur Utama

- **Lingkungan 3D Brain Lab Interaktif**:
  - Grafis *low-poly* 3D dengan rendering 60 FPS yang ringan.
  - Kontrol kamera orbit penuh: sentuhan geser (*drag/swipe*), cubit (*pinch zoom*), dan *tap* langsung pada objek 3D.
- **30 Level Menantang Berjenjang**:
  - **Level 1–5 (Pemanasan)**: Pengenalan pola, koordinasi, dan pemikiran awal.
  - **Level 6–10 (Logika)**: Deduksi deret, sebab-akibat, dan relasi logika pintu.
  - **Level 11–15 (Memori)**: Hafalan kilatan kubus memori, posisi objek tersembunyi, dan pola visual.
  - **Level 16–20 (Spasial)**: Rotasi kubus multi-sumbu, navigasi labirin 3D, dan manipulasi sudut pandang.
  - **Level 21–25 (Problem Solving)**: Rangkaian sirkuit cahaya multi-node, keseimbangan platform momen gaya, dan aljabar matriks.
  - **Level 26–30 (Master Puzzle)**: Tantangan puncak mengombinasikan logika kuantum, ritme memori grandmaster, dan lintasan gravitasi dinamis.
- **10 Mekanisme Puzzle Unik**:
  1. *Pattern Lock*: Deret geometri pola berkelanjutan.
  2. *3D Maze*: Labirin koridor kubus dengan start, dinding merah, dan exit hijau.
  3. *Memory Cube*: Mengingat urutan kilatan cahaya warna pada panel kubus.
  4. *Logic Door*: Memecahkan relasi aljabar dan sandi pintu laboratorium.
  5. *Balance Seesaw*: Menyeimbangkan momen gaya beban pada platform 3D.
  6. *Rotate Cube*: Memutar orientasi sumbu kubus 3D hingga simbol target cocok.
  7. *Light Circuit*: Memutar konektor kabel energi hingga seluruh lampu menyala.
  8. *Object Sequence*: Menganalisis siklus orbit dan transformasi benda 3D.
  9. *Hidden Object*: Menjelajahi ruang Brain Lab 3D untuk menemukan artefak inti kristal.
  10. *Gravity Ramp*: Mengatur sudut kemiringan ramp agar bola gravitasi meluncur ke target.
- **Sistem Progres & Game Loop Lengkap**:
  - Sistem Nyawa (❤️❤️❤️) & Batas Waktu dinamis.
  - Sistem Skor transparan (Skor Dasar + Bonus Waktu + Bonus Nyawa - Penalti).
  - Peringkat Bintang (⭐ 1-3 Bintang) per level.
  - Sistem Koin & 3 Tingkatan Petunjuk (*Hint 1, 2, 3*).
  - Level XP & Peningkatan Level Pemain.
- **Tantangan Harian (*Daily Challenge*)**:
  - Puzzle khusus harian dengan sistem *Streak* berturut-turut dan bonus koin milestone.
- **Statistik Kognitif Otak**:
  - Akurasi persentase, total puzzle selesai, total waktu bermain, dan grafik keahlian kognitif.
- **Sistem Audio Sintesis Mandiri**:
  - Efek suara klik, sukses, salah, koin, dan kemenangan level.
  - Musik ambient synthesizer futuristik yang menenangkan.
- **100% Offline & Privasi Terjaga**:
  - Tidak memerlukan koneksi internet untuk bermain dan tidak menyimpan data sensitif pengguna.

---

## 🛠 Teknologi & Arsitektur

- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Database Lokal**: Room Database + Coroutines & Flow
- **3D Engine**: Pure Kotlin Projection & Vector Math 3D Engine (Painter's Algorithm, Depth Sorting, Directional Lighting, Hit Testing)
- **Audio**: Android AudioTrack Real-time Synthesizer

---

## 🚀 Cara Menjalankan Project

1. Buka project di **Android Studio Ladybug (2024.2.1)** atau versi yang lebih baru.
2. Pastikan JDK 17 atau JDK 21 terpasang.
3. Jalankan Gradle Sync.
4. Pilih perangkat emulator Android atau perangkat fisik (Android 7.0 / API 24 ke atas).
5. Klik tombol **Run** (`Shift + F10`) untuk mengompilasi dan menjalankan aplikasi.
