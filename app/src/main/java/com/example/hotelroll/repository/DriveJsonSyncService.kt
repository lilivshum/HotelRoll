package com.example.hotelroll.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.room.withTransaction
import com.example.hotelroll.data.dao.ReservationDao
import com.example.hotelroll.data.dao.RoomDao
import com.example.hotelroll.data.dao.StayDao
import com.example.hotelroll.data.database.HotelDatabase
import com.example.hotelroll.data.model.Currency
import com.example.hotelroll.data.model.Reservation
import com.example.hotelroll.data.model.RoomStatus
import com.example.hotelroll.data.model.Stay
import com.example.hotelroll.data.model.StayStatus
import com.example.hotelroll.data.model.TariffType
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val SCOPE = "oauth2:https://www.googleapis.com/auth/drive.file"
private const val FILE_NAME = "hotelroll_data.json"
private const val BOUNDARY = "hotelroll_json_boundary"
private const val KEY_DRIVE_FILE_ID = "drive_sync_file_id"
private const val KEY_LAST_SYNC = "last_sync"
private const val DEBOUNCE_MS = 3000L

class DriveJsonSyncService(
    private val appContext: Context,
    private val db: HotelDatabase,
    private val reservationDao: ReservationDao,
    private val stayDao: StayDao,
    private val roomDao: RoomDao,
    private val prefs: SharedPreferences,
    private val scope: CoroutineScope
) : SyncService {

    private var debounceJob: Job? = null

    override fun scheduleSync() {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(DEBOUNCE_MS)
            push()  // failures are silent — user can check status in Settings
        }
    }

    override suspend fun push(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(appContext)
                ?: return@withContext Result.failure(Exception("Not signed in to Drive"))
            val token = GoogleAuthUtil.getToken(appContext, account.account!!, SCOPE)

            val json = exportJson()
            val existingId = prefs.getString(KEY_DRIVE_FILE_ID, null)
                ?: findBackupFile(token)
            val fileId = uploadJson(token, json, existingId)

            val timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            prefs.edit()
                .putString(KEY_DRIVE_FILE_ID, fileId)
                .putString(KEY_LAST_SYNC, timestamp)
                .apply()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pull(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(appContext)
                ?: return@withContext Result.failure(Exception("Not signed in to Drive"))
            val token = GoogleAuthUtil.getToken(appContext, account.account!!, SCOPE)

            val fileId = prefs.getString(KEY_DRIVE_FILE_ID, null)
                ?: findBackupFile(token)
                ?: return@withContext Result.failure(Exception("No backup found on Drive"))

            val json = downloadJson(token, fileId)
            importJson(json)

            prefs.edit().putString(KEY_DRIVE_FILE_ID, fileId).apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLastSync(): String? = prefs.getString(KEY_LAST_SYNC, null)

    // ── JSON export ──────────────────────────────────────────────────────────

    private suspend fun exportJson(): String {
        val reservations = reservationDao.getAll()
        val stays = stayDao.getAll()
        val rooms = roomDao.getAll()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", LocalDateTime.now().toString())

        val resArray = JSONArray()
        reservations.forEach { r ->
            resArray.put(JSONObject().apply {
                put("id", r.id)
                put("resName", r.resName)
                put("checkInDate", r.checkInDate.toString())
                put("nights", r.nights)
                put("noGuests", r.noGuests)
                put("noKids", r.noKids)
                put("notes", r.notes ?: JSONObject.NULL)
                put("isActive", r.isActive)
            })
        }
        root.put("reservations", resArray)

        val stayArray = JSONArray()
        stays.forEach { s ->
            stayArray.put(JSONObject().apply {
                put("stayId", s.stayId)
                put("stayName", s.stayName ?: JSONObject.NULL)
                put("reservationId", s.reservationId)
                put("roomId", s.roomId)
                put("peopleInRoom", s.peopleInRoom)
                put("kidsInRoom", s.kidsInRoom)
                put("checkInDate", s.checkInDate.toString())
                put("checkOutDate", s.checkOutDate.toString())
                put("status", s.status.name)
                put("tariff", s.tariff)
                put("tariffType", s.tariffType.name)
                put("notes", s.notes ?: JSONObject.NULL)
                put("currency", s.currency.name)
            })
        }
        root.put("stays", stayArray)

        // Only sync non-default room statuses (rooms are seeded identically on all devices)
        val roomStatusObj = JSONObject()
        rooms.filter { it.status != RoomStatus.AVAILABLE }.forEach { r ->
            roomStatusObj.put(r.roomId.toString(), r.status.name)
        }
        root.put("roomStatuses", roomStatusObj)

        return root.toString()
    }

    // ── JSON import ──────────────────────────────────────────────────────────

    private suspend fun importJson(jsonStr: String) {
        val root = JSONObject(jsonStr)
        val resArray = root.getJSONArray("reservations")
        val stayArray = root.getJSONArray("stays")
        val roomStatusObj = root.getJSONObject("roomStatuses")

        val reservations = (0 until resArray.length()).map { i ->
            val o = resArray.getJSONObject(i)
            Reservation(
                id = o.getLong("id"),
                resName = o.getString("resName"),
                checkInDate = LocalDate.parse(o.getString("checkInDate")),
                nights = o.getInt("nights"),
                noGuests = o.getInt("noGuests"),
                noKids = o.getInt("noKids"),
                notes = if (o.isNull("notes")) null else o.getString("notes"),
                isActive = o.getBoolean("isActive")
            )
        }

        val stays = (0 until stayArray.length()).map { i ->
            val o = stayArray.getJSONObject(i)
            Stay(
                stayId = o.getLong("stayId"),
                stayName = if (o.isNull("stayName")) null else o.getString("stayName"),
                reservationId = o.getLong("reservationId"),
                roomId = o.getLong("roomId"),
                peopleInRoom = o.getInt("peopleInRoom"),
                kidsInRoom = o.getInt("kidsInRoom"),
                checkInDate = LocalDate.parse(o.getString("checkInDate")),
                checkOutDate = LocalDate.parse(o.getString("checkOutDate")),
                status = StayStatus.valueOf(o.getString("status")),
                tariff = o.getDouble("tariff"),
                tariffType = TariffType.valueOf(o.getString("tariffType")),
                notes = if (o.isNull("notes")) null else o.getString("notes"),
                currency = Currency.valueOf(o.getString("currency"))
            )
        }

        // Atomic replace: delete everything then reinsert with original IDs
        db.withTransaction {
            reservationDao.deleteAll()  // stays cascade-delete via FK
            reservationDao.insertAll(reservations)
            stayDao.insertAll(stays)
            roomDao.resetAllStatuses()
            roomStatusObj.keys().forEach { key ->
                roomDao.updateStatus(key.toLong(), RoomStatus.valueOf(roomStatusObj.getString(key)))
            }
        }
    }

    // ── Drive REST API ────────────────────────────────────────────────────────

    private fun uploadJson(token: String, json: String, fileId: String?): String {
        val urlStr = if (fileId != null)
            "https://www.googleapis.com/upload/drive/v3/files/$fileId?uploadType=multipart"
        else
            "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart"

        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = if (fileId != null) "PATCH" else "POST"
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Content-Type", "multipart/related; boundary=$BOUNDARY")
            doOutput = true
        }

        conn.outputStream.use { out ->
            val dos = DataOutputStream(out)
            dos.writeBytes("--$BOUNDARY\r\n")
            dos.writeBytes("Content-Type: application/json; charset=UTF-8\r\n\r\n")
            dos.write("""{"name":"$FILE_NAME","mimeType":"application/json"}""".toByteArray())
            dos.writeBytes("\r\n--$BOUNDARY\r\n")
            dos.writeBytes("Content-Type: application/json; charset=UTF-8\r\n\r\n")
            dos.write(json.toByteArray(Charsets.UTF_8))
            dos.writeBytes("\r\n--$BOUNDARY--\r\n")
        }

        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "no body"
            throw Exception("Drive upload failed ($code): $err")
        }
        return JSONObject(conn.inputStream.bufferedReader().readText()).getString("id")
    }

    private fun findBackupFile(token: String): String? {
        val q = URLEncoder.encode("name='$FILE_NAME' and trashed=false", "UTF-8")
        val conn = (URL("https://www.googleapis.com/drive/v3/files?q=$q&spaces=drive&fields=files(id)")
            .openConnection() as HttpURLConnection).apply {
            setRequestProperty("Authorization", "Bearer $token")
        }
        val body = conn.inputStream.bufferedReader().readText()
        val files = JSONObject(body).getJSONArray("files")
        return if (files.length() > 0) files.getJSONObject(0).getString("id") else null
    }

    private fun downloadJson(token: String, fileId: String): String {
        val conn = (URL("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
            .openConnection() as HttpURLConnection).apply {
            setRequestProperty("Authorization", "Bearer $token")
        }
        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "no body"
            throw Exception("Drive download failed ($code): $err")
        }
        return conn.inputStream.bufferedReader().readText()
    }
}
