package com.example.data

enum class PuzzleCategory(val label: String, val description: String) {
    WARMUP("Pemanasan", "Pengenalan pola, koordinasi, dan pemikiran awal"),
    LOGIC("Logika", "Urutan, deduksi, dan relasi sebab-akibat"),
    MEMORY("Memori", "Mengingat urutan, posisi warna, dan pola visual"),
    SPATIAL("Spasial", "Rotasi ruang 3D, sudut pandang, dan navigasi labirin"),
    PROBLEM_SOLVING("Problem Solving", "Strategi multi-langkah, keseimbangan, dan sirkuit"),
    MASTER("Master Puzzle", "Kombinasi kompleks dari seluruh keahlian kognitif")
}

enum class PuzzleType {
    PATTERN_LOCK,
    MAZE_3D,
    MEMORY_CUBE,
    LOGIC_DOOR,
    BALANCE,
    ROTATE_CUBE,
    LIGHT_CIRCUIT,
    OBJECT_SEQUENCE,
    HIDDEN_OBJECT,
    GRAVITY
}

data class LevelDefinition(
    val id: Int,
    val title: String,
    val category: PuzzleCategory,
    val difficulty: String, // "Mudah", "Sedang", "Sulit", "Master"
    val timeLimitSeconds: Int,
    val puzzleType: PuzzleType,
    val objective: String,
    val rewardXP: Int,
    val rewardCoin: Int,
    val hint1: String, // Petunjuk awal (5 coin)
    val hint2: String, // Petunjuk pertengahan (10 coin)
    val hint3: String  // Petunjuk jelas (20 coin)
)

object LevelCatalog {
    val levels: List<LevelDefinition> = listOf(
        // Level 1-5: Pemanasan
        LevelDefinition(
            id = 1,
            title = "Inisiasi Pola",
            category = PuzzleCategory.WARMUP,
            difficulty = "Mudah",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.PATTERN_LOCK,
            objective = "Kenali urutan simbol geometris dan pilih simbol berikutnya.",
            rewardXP = 100,
            rewardCoin = 25,
            hint1 = "Perhatikan pergantian bentuk lingkaran dan segitiga.",
            hint2 = "Pola berulang: Lingkaran, Lingkaran, Segitiga.",
            hint3 = "Setelah Segitiga, pola dimulai kembali dengan Lingkaran."
        ),
        LevelDefinition(
            id = 2,
            title = "Rotasi Kristal",
            category = PuzzleCategory.WARMUP,
            difficulty = "Mudah",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.ROTATE_CUBE,
            objective = "Putar kubus energi 3D agar simbol target sejajar ke depan.",
            rewardXP = 110,
            rewardCoin = 25,
            hint1 = "Gunakan geseran jari (swipe) untuk memutar kubus.",
            hint2 = "Cari simbol bintang emas di salah satu sisi samping.",
            hint3 = "Lakukan 2x swipe ke kiri untuk menghadapkannya ke depan."
        ),
        LevelDefinition(
            id = 3,
            title = "Jejak Memori Awal",
            category = PuzzleCategory.WARMUP,
            difficulty = "Mudah",
            timeLimitSeconds = 80,
            puzzleType = PuzzleType.MEMORY_CUBE,
            objective = "Ingat urutan panel kubus yang menyala dan ulangi ketukannya.",
            rewardXP = 120,
            rewardCoin = 30,
            hint1 = "Fokus pada warna cahaya saat menyala secara berurutan.",
            hint2 = "Ada 3 langkah pola yang perlu diingat.",
            hint3 = "Urutan: Biru -> Hijau -> Kuning."
        ),
        LevelDefinition(
            id = 4,
            title = "Navigasi Labirin Mini",
            category = PuzzleCategory.WARMUP,
            difficulty = "Mudah",
            timeLimitSeconds = 100,
            puzzleType = PuzzleType.MAZE_3D,
            objective = "Gerakkan drone eksplorasi dari Start (Biru) menuju Exit (Hijau).",
            rewardXP = 130,
            rewardCoin = 30,
            hint1 = "Hindari dinding merah dan cari koridor terbuka.",
            hint2 = "Jalur tercepat mengarah ke belokan kanan lalu lurus.",
            hint3 = "Ikuti jalur sepanjang dinding kanan hingga menemukan portal hijau."
        ),
        LevelDefinition(
            id = 5,
            title = "Sandi Pintu Lab",
            category = PuzzleCategory.WARMUP,
            difficulty = "Mudah",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.LOGIC_DOOR,
            objective = "Pecahkan relasi variabel angka untuk membuka pintu laboratorium.",
            rewardXP = 150,
            rewardCoin = 35,
            hint1 = "Ingat urutan operasi matematika: perkalian didahulukan daripada penjumlahan.",
            hint2 = "Hitung B × C terlebih dahulu, lalu tambahkan A.",
            hint3 = "Substitusi nilai: A=2, B=3, C=4. Hasilnya adalah 2 + (3 × 4) = 14."
        ),

        // Level 6-10: Logika
        LevelDefinition(
            id = 6,
            title = "Deduksi Simbolik",
            category = PuzzleCategory.LOGIC,
            difficulty = "Sedang",
            timeLimitSeconds = 85,
            puzzleType = PuzzleType.PATTERN_LOCK,
            objective = "Temukan aturan tersembunyi pada deret simbol berputar.",
            rewardXP = 160,
            rewardCoin = 35,
            hint1 = "Perhatikan jumlah sudut pada setiap bentuk yang muncul.",
            hint2 = "Jumlah sudut bertambah 1 pada setiap langkah deret.",
            hint3 = "Setelah Segiempat (4 sudut), bentuk berikutnya adalah Segilima (5 sudut)."
        ),
        LevelDefinition(
            id = 7,
            title = "Aktivasi Arus Cahaya",
            category = PuzzleCategory.LOGIC,
            difficulty = "Sedang",
            timeLimitSeconds = 95,
            puzzleType = PuzzleType.LIGHT_CIRCUIT,
            objective = "Putar konektor kabel daya agar lampu lab menyala sempurna.",
            rewardXP = 170,
            rewardCoin = 40,
            hint1 = "Mulailah menyambungkan dari sel baterai utama di sudut kiri.",
            hint2 = "Setiap konektor harus membentuk jalur tanpa celah.",
            hint3 = "Putar sambungan tengah sebanyak 1 kali searah jarum jam."
        ),
        LevelDefinition(
            id = 8,
            title = "Pintu Kuantum",
            category = PuzzleCategory.LOGIC,
            difficulty = "Sedang",
            timeLimitSeconds = 80,
            puzzleType = PuzzleType.LOGIC_DOOR,
            objective = "Selesaikan persamaan logika biner untuk membuka pintu portal.",
            rewardXP = 180,
            rewardCoin = 40,
            hint1 = "Cari pola selisih antara angka input dan output.",
            hint2 = "Rumus mengalikan angka pertama dengan angka kedua ditambah konstanta.",
            hint3 = "Jika X=5 dan Y=3 dengan formula (X*2)-Y, jawabannya adalah 7."
        ),
        LevelDefinition(
            id = 9,
            title = "Urutan Benda Mengambang",
            category = PuzzleCategory.LOGIC,
            difficulty = "Sedang",
            timeLimitSeconds = 85,
            puzzleType = PuzzleType.OBJECT_SEQUENCE,
            objective = "Amati orbit objek 3D dan tebak objek yang hilang di slot akhir.",
            rewardXP = 190,
            rewardCoin = 45,
            hint1 = "Perhatikan warna dan ukuran objek yang berputar.",
            hint2 = "Ukuran membesar secara bertahap dengan warna yang berulang.",
            hint3 = "Objek berikutnya adalah Prisma berwarna Ungu dengan ukuran Sedang."
        ),
        LevelDefinition(
            id = 10,
            title = "Timbangan Keseimbangan 3D",
            category = PuzzleCategory.LOGIC,
            difficulty = "Sedang",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.BALANCE,
            objective = "Seimbangkan platform 3D dengan menempatkan beban pada posisi yang tepat.",
            rewardXP = 200,
            rewardCoin = 50,
            hint1 = "Prinsip tuas: Beban berat di dekat poros seimbang dengan beban ringan di ujung.",
            hint2 = "Massa kiri berjarak 2 kotak dengan berat 4 (Momen = 8).",
            hint3 = "Letakkan beban seberat 2 di kotak berjarak 4 dari poros kanan."
        ),

        // Level 11-15: Memori
        LevelDefinition(
            id = 11,
            title = "Kubus Memori 4-Fase",
            category = PuzzleCategory.MEMORY,
            difficulty = "Sedang",
            timeLimitSeconds = 80,
            puzzleType = PuzzleType.MEMORY_CUBE,
            objective = "Hafalkan urutan 4 kilatan neon pada kubus holografik.",
            rewardXP = 210,
            rewardCoin = 45,
            hint1 = "Ucapkan nama warna dalam hati saat lampu menyala.",
            hint2 = "Pola memiliki satu warna yang berulang dua kali.",
            hint3 = "Urutan: Merah -> Cyan -> Merah -> Emas."
        ),
        LevelDefinition(
            id = 12,
            title = "Objek Misterius Tersembunyi",
            category = PuzzleCategory.MEMORY,
            difficulty = "Sedang",
            timeLimitSeconds = 85,
            puzzleType = PuzzleType.HIDDEN_OBJECT,
            objective = "Jelajahi Brain Lab 3D dan temukan Core Kristal yang tersembunyi.",
            rewardXP = 220,
            rewardCoin = 50,
            hint1 = "Putar kamera dengan sentuhan geser untuk melihat ke belakang pilar.",
            hint2 = "Kristal memancarkan kilau partikel biru muda yang berkedip.",
            hint3 = "Kristal terletak di atas rak platform sebelah kanan di balik silinder."
        ),
        LevelDefinition(
            id = 13,
            title = "Urutan Matriks 5-Langkah",
            category = PuzzleCategory.MEMORY,
            difficulty = "Sedang",
            timeLimitSeconds = 85,
            puzzleType = PuzzleType.MEMORY_CUBE,
            objective = "Tirukan 5 urutan koordinat kubus berdenyut tanpa salah.",
            rewardXP = 230,
            rewardCoin = 50,
            hint1 = "Bagi urutan menjadi 2 bagian (3 langkah pertama, lalu 2 langkah akhir).",
            hint2 = "Langkah dimulai dari panel atas, lalu berputar searah jarum jam.",
            hint3 = "Urutan: Atas -> Kanan -> Bawah -> Kiri -> Tengah."
        ),
        LevelDefinition(
            id = 14,
            title = "Rekonstruksi Pola",
            category = PuzzleCategory.MEMORY,
            difficulty = "Sedang",
            timeLimitSeconds = 75,
            puzzleType = PuzzleType.PATTERN_LOCK,
            objective = "Ingat pola geometris yang ditampilkan sejenak lalu pilih urutannya.",
            rewardXP = 240,
            rewardCoin = 55,
            hint1 = "Hubungkan bentuk-bentuk tersebut seperti sebuah kalimat visual.",
            hint2 = "Terdapat pergantian bentuk ganjil dan genap sisi.",
            hint3 = "Pola yang tepat: Belah ketupat -> Hexagon -> Lingkaran."
        ),
        LevelDefinition(
            id = 15,
            title = "Sensor Spasial Tersembunyi",
            category = PuzzleCategory.MEMORY,
            difficulty = "Sulit",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.HIDDEN_OBJECT,
            objective = "Temukan 3 Micro-Chip di sekitar ruangan Brain Lab 3D sebelum timer habis.",
            rewardXP = 260,
            rewardCoin = 60,
            hint1 = "Zoom in menggunakan cubitan jari untuk melihat detail sudut ruangan.",
            hint2 = "Satu chip berada di bawah meja konsol, satu di dinding belakang.",
            hint3 = "Chip ketiga menempel di pilar kiri atas."
        ),

        // Level 16-20: Spasial
        LevelDefinition(
            id = 16,
            title = "Rotasi Kubus Multi-Sumbu",
            category = PuzzleCategory.SPATIAL,
            difficulty = "Sulit",
            timeLimitSeconds = 95,
            puzzleType = PuzzleType.ROTATE_CUBE,
            objective = "Putar kubus 3D dalam sumbu X dan Y hingga simbol rune cocok.",
            rewardXP = 270,
            rewardCoin = 60,
            hint1 = "Rotasi atas-bawah mengubah bidang vertikal, kiri-kanan bidang horizontal.",
            hint2 = "Simbol target berada di sisi bawah kubus saat ini.",
            hint3 = "Lakukan swipe ke bawah sekali, lalu swipe ke kanan sekali."
        ),
        LevelDefinition(
            id = 17,
            title = "Labirin Lab 3D Bertingkat",
            category = PuzzleCategory.SPATIAL,
            difficulty = "Sulit",
            timeLimitSeconds = 110,
            puzzleType = PuzzleType.MAZE_3D,
            objective = "Pandu bola energi melewati koridor labirin 3D yang berliku.",
            rewardXP = 280,
            rewardCoin = 65,
            hint1 = "Gunakan sudut pandang kamera dari atas untuk melihat jalan buntu.",
            hint2 = "Jangan ambil jalur kiri di persimpangan kedua.",
            hint3 = "Ambil jalur lurus, lalu belok kanan di persimpangan terakhir."
        ),
        LevelDefinition(
            id = 18,
            title = "Gravitasi Turunan Ramp",
            category = PuzzleCategory.SPATIAL,
            difficulty = "Sulit",
            timeLimitSeconds = 100,
            puzzleType = PuzzleType.GRAVITY,
            objective = "Atur kemiringan ramp 3D agar bola menggelinding masuk ke lubang target.",
            rewardXP = 290,
            rewardCoin = 70,
            hint1 = "Pastikan sudut ramp tidak terlalu curam agar bola tidak terpental.",
            hint2 = "Posisikan ramp kedua miring ke kiri sebesar 30 derajat.",
            hint3 = "Sejajarkan ramp akhir tepat di atas lingkaran target penampung."
        ),
        LevelDefinition(
            id = 19,
            title = "Penjajaran Sinar Spektrum",
            category = PuzzleCategory.SPATIAL,
            difficulty = "Sulit",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.LIGHT_CIRCUIT,
            objective = "Arahkan konektor optik 3D agar sinar laser memantul ke prisma tujuan.",
            rewardXP = 300,
            rewardCoin = 70,
            hint1 = "Sinar laser selalu memantul 90 derajat pada konektor cermin.",
            hint2 = "Dua rotator pertama harus saling berhadapan.",
            hint3 = "Konektor ketiga harus mengarah ke atas menuju prisma merah."
        ),
        LevelDefinition(
            id = 20,
            title = "Rekonstruksi Kubus Simetri",
            category = PuzzleCategory.SPATIAL,
            difficulty = "Sulit",
            timeLimitSeconds = 85,
            puzzleType = PuzzleType.ROTATE_CUBE,
            objective = "Samakan orientasi warna kubus sesuai dengan blueprint di layar.",
            rewardXP = 320,
            rewardCoin = 75,
            hint1 = "Bandingkan letak muka warna Kuning terhadap muka warna Cyan.",
            hint2 = "Muka Kuning harus berada di atas, muka Cyan di depan.",
            hint3 = "Swipe ke atas lalu putar searah jarum jam."
        ),

        // Level 21-25: Problem Solving
        LevelDefinition(
            id = 21,
            title = "Jaringan Sirkuit Multi-Node",
            category = PuzzleCategory.PROBLEM_SOLVING,
            difficulty = "Sulit",
            timeLimitSeconds = 105,
            puzzleType = PuzzleType.LIGHT_CIRCUIT,
            objective = "Hubungkan 4 lampu secara paralel tanpa menyebabkan korsleting.",
            rewardXP = 330,
            rewardCoin = 80,
            hint1 = "Aliran listrik harus bercabang di node tengah.",
            hint2 = "Pastikan tidak ada ujung kabel yang buntu menghadap dinding.",
            hint3 = "Putar percabangan T di tengah sehingga mengalirkan daya ke atas dan bawah."
        ),
        LevelDefinition(
            id = 22,
            title = "Keseimbangan Massa Bertingkat",
            category = PuzzleCategory.PROBLEM_SOLVING,
            difficulty = "Sulit",
            timeLimitSeconds = 95,
            puzzleType = PuzzleType.BALANCE,
            objective = "Tentukan distribusi 3 beban berbeda pada platform jungkat-jungkit 3D.",
            rewardXP = 350,
            rewardCoin = 80,
            hint1 = "Total momen gaya kiri harus sama persis dengan total momen gaya kanan.",
            hint2 = "Beban 3 kg letakkan pada jarak 2 dari titik tumpu kiri.",
            hint3 = "Di sisi kanan, letakkan beban 2 kg pada jarak 3 dari titik tumpu."
        ),
        LevelDefinition(
            id = 23,
            title = "Pintu Aljabar Matriks",
            category = PuzzleCategory.PROBLEM_SOLVING,
            difficulty = "Sulit",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.LOGIC_DOOR,
            objective = "Pecahkan teka-teki logika persamaan bertingkat untuk membuka sekat baja.",
            rewardXP = 360,
            rewardCoin = 85,
            hint1 = "Gunakan metode eliminasi variabel pertama.",
            hint2 = "Dari persamaan 1: X + Y = 10, dan 2X - Y = 5.",
            hint3 = "Nilai X = 5, Y = 5. Nilai dari X × Y adalah 25."
        ),
        LevelDefinition(
            id = 24,
            title = "Lintasan Gravitasi Berpenghalang",
            category = PuzzleCategory.PROBLEM_SOLVING,
            difficulty = "Sulit",
            timeLimitSeconds = 110,
            puzzleType = PuzzleType.GRAVITY,
            objective = "Arahkan bola melewati platform miring menghindari lubang jebakan merah.",
            rewardXP = 380,
            rewardCoin = 90,
            hint1 = "Gunakan pembatas samping untuk membelokkan arah bola sebelum jebakan.",
            hint2 = "Beri sudut kemiringan lembut ke arah kanan.",
            hint3 = "Ramp tengah harus sedikit mendatar untuk mengurangi laju kecepatan bola."
        ),
        LevelDefinition(
            id = 25,
            title = "Rantai Transformasi Objek",
            category = PuzzleCategory.PROBLEM_SOLVING,
            difficulty = "Sulit",
            timeLimitSeconds = 90,
            puzzleType = PuzzleType.OBJECT_SEQUENCE,
            objective = "Pecahkan formula kombinasi bentuk, warna, dan orientasi benda 3D.",
            rewardXP = 400,
            rewardCoin = 95,
            hint1 = "Setiap langkah mengalami rotasi 45 derajat dan perubahan warna spektral.",
            hint2 = "Bentuk berganti dari Prisma -> Piramida -> Silinder -> Kubus.",
            hint3 = "Jawaban yang benar adalah Silinder Emas dengan orientasi horizontal."
        ),

        // Level 26-30: Master Puzzle
        LevelDefinition(
            id = 26,
            title = "Labirin Spasial Kuantum",
            category = PuzzleCategory.MASTER,
            difficulty = "Master",
            timeLimitSeconds = 120,
            puzzleType = PuzzleType.MAZE_3D,
            objective = "Taklukkan labirin 3D kompleks dengan saklar pembuka gerbang.",
            rewardXP = 450,
            rewardCoin = 100,
            hint1 = "Anda harus menyentuh saklar biru terlebih dahulu untuk membuka gerbang merah.",
            hint2 = "Saklar biru terletak di ujung lorong utara.",
            hint3 = "Setelah saklar ditekan, kembali ke jalur tengah menuju portal exit hijau."
        ),
        LevelDefinition(
            id = 27,
            title = "Kubus Memori Grandmaster",
            category = PuzzleCategory.MASTER,
            difficulty = "Master",
            timeLimitSeconds = 100,
            puzzleType = PuzzleType.MEMORY_CUBE,
            objective = "Ingat 7 urutan kilatan cahaya cepat pada kubus dodecahedron.",
            rewardXP = 480,
            rewardCoin = 110,
            hint1 = "Kelompokkan menjadi irama ritmik (3 nada - 4 nada).",
            hint2 = "Tiga nada pertama: Merah, Biru, Hijau.",
            hint3 = "Empat nada berikutnya: Kuning, Merah, Ungu, Cyan."
        ),
        LevelDefinition(
            id = 28,
            title = "Sirkuit Energi Total",
            category = PuzzleCategory.MASTER,
            difficulty = "Master",
            timeLimitSeconds = 120,
            puzzleType = PuzzleType.LIGHT_CIRCUIT,
            objective = "Nyalakan seluruh 6 lampu Brain Lab dalam konfigurasi grid 3D.",
            rewardXP = 500,
            rewardCoin = 120,
            hint1 = "Dua sumber energi di sudut harus dialirkan secara simetris.",
            hint2 = "Hindari menghubungkan kedua kutub baterai secara langsung.",
            hint3 = "Konektor silang di pusat harus menyalurkan daya ke 4 kuadran."
        ),
        LevelDefinition(
            id = 29,
            title = "Pintu Logika Singularitas",
            category = PuzzleCategory.MASTER,
            difficulty = "Master",
            timeLimitSeconds = 105,
            puzzleType = PuzzleType.LOGIC_DOOR,
            objective = "Selesaikan kode kriptografi matematika multi-variabel ruang waktu.",
            rewardXP = 550,
            rewardCoin = 130,
            hint1 = "Cari hubungan deret bilangan prima dan pangkat dua.",
            hint2 = "Formula: (P^2) - (Q * 3) = Kunci.",
            hint3 = "Dengan P=7 dan Q=5: (49) - (15) = 34."
        ),
        LevelDefinition(
            id = 30,
            title = "Inti Otak Terakhir (Mastermind)",
            category = PuzzleCategory.MASTER,
            difficulty = "Master",
            timeLimitSeconds = 150,
            puzzleType = PuzzleType.GRAVITY,
            objective = "Uji puncak spasial dan logika: Bimbing inti energi melintasi labirin gravitasi dinamis.",
            rewardXP = 700,
            rewardCoin = 200,
            hint1 = "Kombinasikan kontrol sudut platform dan momentum lompatan bola.",
            hint2 = "Gunakan ramp melengkung untuk menambah daya dorong melewati celah.",
            hint3 = "Turunkan kemiringan di detik akhir agar bola mendarat mulus di reaktor inti."
        )
    )

    fun getLevelById(id: Int): LevelDefinition {
        return levels.find { it.id == id } ?: levels.first()
    }

    fun getDailyLevel(dateStr: String): LevelDefinition {
        val hash = dateStr.hashCode()
        val index = kotlin.math.abs(hash) % levels.size
        val base = levels[index]
        return base.copy(
            title = "Tantangan Harian: ${base.title}",
            rewardXP = base.rewardXP + 150,
            rewardCoin = base.rewardCoin + 50
        )
    }
}
