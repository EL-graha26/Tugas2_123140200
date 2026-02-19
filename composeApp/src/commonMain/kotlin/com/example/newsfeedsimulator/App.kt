package com.example.newsfeedsimulator

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

val dummyData = mapOf(
    "TECH" to listOf(
        "Apple Vision Pro 2: Lebih Ringan, Baterai Tahan Lama" to "Bocoran terbaru menyebutkan generasi kedua headset AR Apple akan fokus pada kenyamanan. Analis memprediksi rilis pada akhir 2026 dengan harga yang lebih terjangkau.",
        "Google Integrasikan Gemini Nano ke Semua HP Android" to "Update besar Android 15 akan membawa AI on-device ke miliaran perangkat. Pengguna bisa meringkas email dan mengedit foto secara instan tanpa internet.",
        "NVIDIA Rilis GPU RTX 5090: Monster AI untuk Konsumen" to "Kartu grafis terbaru ini diklaim 3x lebih cepat untuk beban kerja AI lokal. Para gamer dan kreator konten menyambut antusias meski konsumsi dayanya tinggi."
    ),
    "SPORTS" to listOf(
        "Timnas Indonesia Lolos ke Putaran Final Piala Dunia!" to "Sejarah tercipta! Gol tunggal di menit akhir memastikan Indonesia melaju ke turnamen sepak bola terbesar dunia. PSSI menjanjikan persiapan maksimal.",
        "Rekor Dunia Baru di Formula 1: Verstappen Tak Terbendung" to "Pembalap Red Bull Racing memecahkan rekor jumlah kemenangan beruntun. Dominasinya membuat persaingan di sisa musim menjadi tantangan besar bagi rival.",
        "NBA: Golden State Warriors Juarai Musim Ini" to "Stephen Curry kembali memimpin Warriors meraih gelar juara. Performa gemilangnya di final menyabet penghargaan MVP."
    ),
    "BUSINESS" to listOf(
        "Inflasi Global Mulai Turun, Pasar Saham Menggeliat" to "Data ekonomi terbaru menunjukkan tanda-tanda pemulihan. Investor mulai kembali optimis, Indeks Harga Saham Gabungan (IHSG) ditutup menguat.",
        "Startup AI Indonesia Raih Pendanaan Seri B Rp 500 Miliar" to "Suntikan dana segar akan digunakan untuk ekspansi pasar dan pengembangan produk. Investor yakin pada potensi pasar AI lokal.",
        "Tesla Buka Pabrik Baterai Baru di Jawa Tengah" to "Kerja sama dengan pemerintah Indonesia, Tesla berkomitmen memproduksi baterai mobil listrik untuk pasar Asia Tenggara. Diharapkan menyerap ribuan tenaga kerja."
    ),
    "DESIGN" to listOf(
        "Tren Desain UI 2026: Minimalism Bertemu 3D Interaktif" to "Gaya 'Skeuomorphism' modern kembali populer. Tombol dan elemen UI dibuat lebih nyata namun tetap clean dan responsif.",
        "Figma Rilis Fitur AI untuk Desain Otomatis" to "Desainer kini bisa membuat prototipe aplikasi dalam hitungan detik dengan perintah teks. Fitur ini memicu perdebatan tentang masa depan profesi desainer.",
        "Warna 'Neo Mint' Jadi Tren Palet Tahun Ini" to "Warna hijau muda segar ini mendominasi berbagai industri, dari fashion hingga desain interior. Psikologi warna mengaitkannya dengan optimisme dan teknologi bersih."
    )
)

fun getCategoryPrimaryColor(category: String): Color {
    return when(category) {
        "TECH" -> Color(0xFF0284C7)
        "SPORTS" -> Color(0xFF16A34A)
        "BUSINESS" -> Color(0xFF7C3AED)
        "DESIGN" -> Color(0xFFD97706)
        else -> Color.Gray
    }
}

fun getCategorySurfaceColor(category: String): Color {
    return when(category) {
        "TECH" -> Color(0xFFF0F9FF)
        "SPORTS" -> Color(0xFFF0FDF4)
        "BUSINESS" -> Color(0xFFFAF5FF)
        "DESIGN" -> Color(0xFFFFFBEB)
        else -> Color.White
    }
}

data class NewsItem(
    val id: Int,
    val title: String,
    val category: String,
    val isRead: Boolean = false,
    val detail: String? = null,
    val isLoadingDetail: Boolean = false,
    val isError: Boolean = false
)

class NewsViewModel(private val scope: CoroutineScope) {
    private val _readCount = MutableStateFlow(0)
    val readCount = _readCount.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _newsList = MutableStateFlow<List<NewsItem>>(emptyList())

    val displayNews = combine(_newsList, _selectedCategory) { list, category ->
        if (category == "ALL") list else list.filter { it.category == category }
    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    private val categories = listOf("TECH", "SPORTS", "BUSINESS", "DESIGN")

    init {
        startNewsGenerator()
    }

    private fun startNewsGenerator() {
        scope.launch {
            val newsFlow = flow {
                var id = 1
                while (true) {
                    delay(2000L)
                    val randomCategory = categories.random()
                    val randomContent = dummyData[randomCategory]!!.random()
                    emit(NewsItem(id, randomContent.first, randomCategory))
                    id++
                }
            }

            newsFlow
                .map { news -> news.copy(title = "📰 ${news.title}") }
                .onEach { println("Log System: Berita baru digenerate -> ID: ${it.id}") }
                .catch { e -> println("Terjadi kesalahan pada stream: ${e.message}") }
                .collect { newNews ->
                    _newsList.update { currentList -> listOf(newNews) + currentList }
                }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun readNewsDetail(news: NewsItem) {
        if (news.detail != null) return

        scope.launch {
            updateNewsState(news.id) { it.copy(isLoadingDetail = true, isRead = true) }
            if (!news.isRead) _readCount.value += 1

            try {
                coroutineScope {
                    val detailDeferred = async(Dispatchers.Default) {
                        delay(1200L)
                        if (news.id % 8 == 0) throw Exception("Network Timeout")

                        val originalTitle = news.title.removePrefix("📰 ")

                        dummyData[news.category]?.find { it.first == originalTitle }?.second
                            ?: throw Exception("Detail berita tidak ditemukan")
                    }

                    val result = detailDeferred.await()
                    updateNewsState(news.id) { it.copy(detail = result, isLoadingDetail = false) }
                }
            } catch (e: Exception) {
                updateNewsState(news.id) {
                    it.copy(detail = "Gagal memuat detail berita. Silakan coba lagi.", isLoadingDetail = false, isError = true)
                }
            }
        }
    }

    private fun updateNewsState(id: Int, update: (NewsItem) -> NewsItem) {
        _newsList.update { list ->
            list.map { if (it.id == id) update(it) else it }
        }
    }
}

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1E293B), secondary = Color(0xFF3B82F6), background = Color(0xFFF8FAFC)
        )
    ) {
        val scope = rememberCoroutineScope()
        val viewModel = remember { NewsViewModel(scope) }
        val displayNews by viewModel.displayNews.collectAsState()
        val readCount by viewModel.readCount.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()

        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            ModernHeader(readCount)
            CategoryFilters(selectedCategory) { viewModel.setCategory(it) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayNews, key = { it.id }) { news ->
                    NewsCard(news) { viewModel.readNewsDetail(news) }
                }
            }
        }
    }
}

@Composable
fun ModernHeader(readCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0E7FF), Color(0xFFF8FAFC))
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("The Daily News", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text("Berita & Update Terkini", fontSize = 14.sp, color = Color.Gray)
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text("Dibaca: ", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(readCount.toString(), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun CategoryFilters(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf("ALL", "TECH", "SPORTS", "BUSINESS", "DESIGN")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            val isSelected = selected == category
            val primaryColor = if (category == "ALL") MaterialTheme.colorScheme.primary else getCategoryPrimaryColor(category)
            val surfaceColor = if (category == "ALL") Color(0xFFF1F5F9) else getCategorySurfaceColor(category)

            Surface(
                modifier = Modifier.clickable { onSelect(category) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) primaryColor else surfaceColor,
                border = BorderStroke(1.dp, if (isSelected) primaryColor else primaryColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = category, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    color = if (isSelected) Color.White else primaryColor,
                    fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    val primaryColor = getCategoryPrimaryColor(news.category)
    val surfaceColor = getCategorySurfaceColor(news.category)

    val targetBackgroundColor = if (news.isRead) Color(0xFFF8FAFC) else surfaceColor
    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        border = BorderStroke(1.dp, if (news.isRead) Color(0xFFE2E8F0) else primaryColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (news.isRead) 0.dp else 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = primaryColor,
                    contentColor = Color.White
                ) {
                    Text(
                        text = news.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                if (!news.isRead) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Baru", color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(primaryColor))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = news.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (news.isRead) Color.DarkGray else Color.Black,
                maxLines = if (news.detail != null) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = news.isLoadingDetail || news.detail != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = primaryColor.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    if (news.isLoadingDetail) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                            strokeWidth = 2.dp,
                            color = primaryColor
                        )
                    } else if (news.detail != null) {
                        Text(
                            text = news.detail,
                            fontSize = 14.sp,
                            color = if (news.isError) Color(0xFFEF4444) else Color(0xFF475569),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}