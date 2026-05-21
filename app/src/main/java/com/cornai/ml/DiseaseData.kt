package com.cornai.ml

object DiseaseData {
    data class DiseaseInfo(
        val className: String,
        val displayName: String,
        val isHealthy: Boolean,
        val symptoms: List<String>,
        val treatment: String,
        val prevention: List<String>,
        val severity: String,
        val recoveryTime: String
    )

    val diseaseInfoMap = mapOf(
        "Healthy_Daun" to DiseaseInfo(
            className = "Healthy_Daun",
            displayName = "Healthy Leaf",
            isHealthy = true,
            symptoms = emptyList(),
            treatment = "Tanaman sehat! Lanjutkan perawatan normal dengan penyiraman teratur dan pemupukan berkala.",
            prevention = listOf(
                "Penyiraman teratur 2x sehari",
                "Pupuk NPK berkala",
                "Pastikan sinar matahari cukup",
                "Periksa tanaman secara rutin"
            ),
            severity = "N/A",
            recoveryTime = "N/A"
        ),
        "Healthy_Tongkol" to DiseaseInfo(
            className = "Healthy_Tongkol",
            displayName = "Healthy Cob",
            isHealthy = true,
            symptoms = emptyList(),
            treatment = "Tongkol jagung sehat! Panen pada waktu yang tepat saat kulit tongkol sudah kering.",
            prevention = listOf(
                "Lindungi dari hama鸟类",
                "Jaga kelembaban tanah",
                "Panen di waktu yang tepat"
            ),
            severity = "N/A",
            recoveryTime = "N/A"
        ),
        "Common_Rust" to DiseaseInfo(
            className = "Common_Rust",
            displayName = "Common Rust",
            isHealthy = false,
            symptoms = listOf(
                "Bintik-bintik coklat/orange kecil di kedua permukaan daun",
                "Bintik membesar dan berubah warna coklat gelap",
                "Daun menguning lebih awal",
                "Tanaman terlihat kerdil"
            ),
            treatment = "Gunakan fungisida sistemik seperti propiconazole 250 EC dengan dosis 0.5-1 ml/liter air. Semprotkan setiap 7-14 hari.",
            prevention = listOf(
                "Gunakan varietas tahan karat",
                "Hindari penanaman terlalu rapat",
                "Buang daun yang terinfeksi berat",
                "Semprot fungisida preventif saat gejala pertama"
            ),
            severity = "Sedang",
            recoveryTime = "2-3 Minggu"
        ),
        "Gray_Leaf_Spot" to DiseaseInfo(
            className = "Gray_Leaf_Spot",
            displayName = "Gray Leaf Spot",
            isHealthy = false,
            symptoms = listOf(
                "Bercak kecil berwarna coklat kemerahan",
                "Bercak berkembang menjadi abu-abu rectangular",
                "Pelepah daun membusuk",
                "Penurunan hasil yang signifikan"
            ),
            treatment = "Gunakan fungisida golongan strobilurin seperti azoxystrobin. Kurangi residu tanaman dan atur pengairan.",
            prevention = listOf(
                "Gunakan varietas tahan penyakit",
                "Praktikkan crop rotation 2-3 musim",
                "Bakar atau kubur sisa tanaman",
                "Hindari penanaman berulang di lahan yang sama"
            ),
            severity = "Tinggi",
            recoveryTime = "3-4 Minggu"
        ),
        "Blight" to DiseaseInfo(
            className = "Blight",
            displayName = "Northern Leaf Blight",
            isHealthy = false,
            symptoms = listOf(
                "Bercak berbentuk oval memanjang berwarna hijau-abu",
                "Lesi dimulai dari daun bawah",
                "Bercak dapat mencapai 2-15 cm",
                "Daun menguning dan mati prematur"
            ),
            treatment = "Semprotkan fungisida berbasis mancozeb 80% dengan dosis 2-3 gram/liter. Buang dan musnahkan sisa tanaman yang terinfeksi.",
            prevention = listOf(
                "Pilih varietas tahan hawar daun",
                "Terapkan jarak tanam 75x25 cm",
                "Buang sisa tanaman setelah panen",
                "Pastikan drainase tanah baik"
            ),
            severity = "Tinggi",
            recoveryTime = "2-4 Minggu"
        ),
        "Bipolaris" to DiseaseInfo(
            className = "Bipolaris",
            displayName = "Bipolaris",
            isHealthy = false,
            symptoms = listOf(
                "Bercak coklat dengan halo kuning",
                "Lesi berbentuk elips atau V",
                "Daun mengering dari ujung",
                "Penurunan fotosintesis"
            ),
            treatment = "Aplikasi fungisida efektif seperti mancozeb, chlorothalonil, atau strobilurin. Sanitasi lahan segera setelah panen.",
            prevention = listOf(
                "Gunakan varietas tahan",
                "Rotasi tanaman dengan kacang-kacangan",
                "Hindari stres air",
                "Buang sisa tanaman terinfeksi"
            ),
            severity = "Sedang",
            recoveryTime = "2-3 Minggu"
        ),
        "Stenocarpella" to DiseaseInfo(
            className = "Stenocarpella",
            displayName = "Stenocarpella",
            isHealthy = false,
            symptoms = listOf(
                "Busuk pada pangkal tongkol",
                "Benjolan hitam pada batang",
                "Batang mudah patah",
                "Tongkol menjadi hitam dan busuk"
            ),
            treatment = "Cabut dan musnahkan tanaman yang terinfeksi. Gunakan fungisida seed treatment saat tanam.",
            prevention = listOf(
                "Gunakan benih sehat bersertifikat",
                "Rotasi tanaman 2-3 musim",
                "Buang sisa tanaman sakit",
                "Tanam di musim yang tepat"
            ),
            severity = "Tinggi",
            recoveryTime = "Tidak dapat disembuhkan"
        ),
        "Bacterial_Leaf_Streak" to DiseaseInfo(
            className = "Bacterial_Leaf_Streak",
            displayName = "Bacterial Leaf Streak",
            isHealthy = false,
            symptoms = listOf(
                "Garis-garis coklat pada daun",
                "Lesi panjang mengikuti tulang daun",
                "Daun terlihat seperti terbakar",
                "Sekresi bakteri berwarna kuning"
            ),
            treatment = "Tidak ada fungisida yang efektif untuk bakteri. Fokus pada pencegahan dan sanitasi. Cabut tanaman yang parah.",
            prevention = listOf(
                "Gunakan varietas tahan",
                "Hindari kerja di sawah saat daun basah",
                "Sanitasi peralatan pertanian",
                "Buang tanaman yang terinfeksi berat"
            ),
            severity = "Sedang",
            recoveryTime = "Tidak dapat disembuhkan"
        ),
        "Asphalt_stain" to DiseaseInfo(
            className = "Asphalt_stain",
            displayName = "Asphalt Stain",
            isHealthy = false,
            symptoms = listOf(
                "Bercak hitam seperti aspal di permukaan daun",
                "Biasanya muncul setelah hujan deras/banjir",
                "Tidak menyebar seperti penyakit fungal",
                "Mostly cosmetic, tidak fatal"
            ),
            treatment = "Tidak memerlukan treatment khusus. Bercak bersifat kosmetik dan tidak mempengaruhi pertumbuhan tanaman.",
            prevention = listOf(
                "Tidak ada pencegahan spesifik yang diperlukan",
                "Ini bukan penyakit, hanya noda dari air banjir",
                "Tanaman tetap sehat dan produktif"
            ),
            severity = "Rendah",
            recoveryTime = "Tidak ada"
        ),
        "Unhealthy_Tongkol" to DiseaseInfo(
            className = "Unhealthy_Tongkol",
            displayName = "Unhealthy Cob",
            isHealthy = false,
            symptoms = listOf(
                "Tongkol berwarna kecoklatan",
                "Butir tidak terbentuk sempurna",
                "Serat-serat tongkol membusuk",
                "Bau tidak sedap"
            ),
            treatment = "Cabut tongkol yang terinfeksi dan musnahkan. Jangan gunakan untuk benih.",
            prevention = listOf(
                "Gunakan benih unggul bersertifikat",
                "Jaga kebersihan lahan",
                "Panen di waktu yang tepat",
                "Simpan hasil panen dengan baik"
            ),
            severity = "Tinggi",
            recoveryTime = "Tidak dapat disembuhkan"
        )
    )

    fun getDiseaseInfo(className: String): DiseaseInfo {
        return diseaseInfoMap[className] ?: DiseaseInfo(
            className = className,
            displayName = className,
            isHealthy = false,
            symptoms = listOf("Data tidak tersedia"),
            treatment = "Konsultasikan dengan penyuluh pertanian.",
            prevention = listOf("Monitor tanaman secara rutin"),
            severity = "Tidak diketahui",
            recoveryTime = "Tidak diketahui"
        )
    }

    fun getAllClassNames(): List<String> = diseaseInfoMap.keys.toList()

    fun getDisplayName(className: String): String {
        return diseaseInfoMap[className]?.displayName ?: className
    }

    fun isHealthy(className: String): Boolean {
        return diseaseInfoMap[className]?.isHealthy ?: false
    }
}