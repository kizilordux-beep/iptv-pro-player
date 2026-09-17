package com.iptvpro.player.viewmodel
import android.app.Application
import androidx.lifecycle.*
import com.iptvpro.player.model.*
import com.iptvpro.player.player.PlayerManager
import com.iptvpro.player.repository.ChannelRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val pm = PlayerManager(app)
    private val repo = ChannelRepository()
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()
    private val _current = MutableStateFlow<Channel?>(null)
    val current = _current.asStateFlow()
    private val _idx = MutableStateFlow(0)
    val idx = _idx.asStateFlow()
    private val _groups = MutableStateFlow(listOf("Tümü"))
    val groups = _groups.asStateFlow()
    private val _group = MutableStateFlow("Tümü")
    val group = _group.asStateFlow()
    private val _filtered = MutableStateFlow<List<Channel>>(emptyList())
    val filtered = _filtered.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _showList = MutableStateFlow(false)
    val showList = _showList.asStateFlow()
    private val _showBottom = MutableStateFlow(false)
    val showBottom = _showBottom.asStateFlow()
    private val _showEpg = MutableStateFlow(false)
    val showEpg = _showEpg.asStateFlow()
    private val _showMenu = MutableStateFlow(false)
    val showMenu = _showMenu.asStateFlow()
    private val _now = MutableStateFlow<EpgProgram?>(null)
    val now = _now.asStateFlow()
    private val _next = MutableStateFlow<EpgProgram?>(null)
    val next = _next.asStateFlow()
    private val _epgList = MutableStateFlow<List<EpgProgram>>(emptyList())
    val epgList = _epgList.asStateFlow()
    private val _state = MutableStateFlow(PState.IDLE)
    val state = _state.asStateFlow()
    private val _err = MutableStateFlow<String?>(null)
    val err = _err.asStateFlow()
    private var hideJob: Job? = null

    init { load() }

    private fun load() = viewModelScope.launch {
        _loading.value = true
        try {
            val all = repo.loadAll()
            _channels.value = all; _groups.value = repo.getGroups(); _filtered.value = all
            if (all.isNotEmpty()) select(0)
        } catch (e: Exception) { _err.value = e.message }
        finally { _loading.value = false }
    }

    fun select(i: Int) {
        val list = _filtered.value
        if (i < 0 || i >= list.size) return
        _current.value = list[i]; _idx.value = i
        viewModelScope.launch {
            _state.value = PState.LOADING
            try {
                val url = if (list[i].url.isEmpty() && list[i].isAddon) repo.resolveUrl(list[i]) else list[i].url
                pm.play(list[i].copy(url = url)); _state.value = PState.PLAYING
                val eid = list[i].epgId.ifEmpty { list[i].id }
                _now.value = repo.getNow(eid); _next.value = repo.getNext(eid); _epgList.value = repo.getEpg(eid)
                showBottomTemp()
            } catch (e: Exception) { _state.value = PState.ERROR; _err.value = e.message }
        }
    }
    fun chUp() { val s = _filtered.value.size; if (s>0) select((_idx.value+1)%s) }
    fun chDown() { val s = _filtered.value.size; if (s>0) select(if (_idx.value-1<0) s-1 else _idx.value-1) }
    fun setGroup(g: String) { _group.value = g; _filtered.value = repo.getByGroup(g) }
    fun toggleList() { _showList.value = !_showList.value; _showMenu.value = false; _showEpg.value = false }
    fun toggleBottom() { _showBottom.value = !_showBottom.value; showBottomTemp() }
    fun toggleEpg() { _showEpg.value = !_showEpg.value; _showList.value = false; _showMenu.value = false }
    fun toggleMenu() { _showMenu.value = !_showMenu.value; _showList.value = false; _showEpg.value = false }
    fun hideAll() { _showList.value = false; _showBottom.value = false; _showEpg.value = false; _showMenu.value = false }
    fun showBottomTemp() { _showBottom.value = true; hideJob?.cancel(); hideJob = viewModelScope.launch { delay(5000); _showBottom.value = false } }
    fun refresh() = load()
    fun clearErr() { _err.value = null }
    override fun onCleared() { super.onCleared(); pm.release() }
}
enum class PState { IDLE, LOADING, PLAYING, PAUSED, ERROR }
