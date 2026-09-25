package com.example.hotelroll.repository

/**
 * Abstraction over the sync backend (Drive today, Firebase later).
 *
 * Master devices call [scheduleSync] after every DB write — it debounces and
 * runs [push] in the background.  Slave devices call [pull] on app open and
 * via the manual button in Settings.
 */
interface SyncService {
    /** Upload current local state to the cloud. */
    suspend fun push(): Result<Unit>

    /** Download cloud state and overwrite local data. */
    suspend fun pull(): Result<Unit>

    /**
     * Debounced push — safe to call after every DB write.
     * Rapid consecutive calls collapse into a single upload.
     */
    fun scheduleSync()

    /** Human-readable timestamp of the last successful push, or null. */
    fun getLastSync(): String?
}
