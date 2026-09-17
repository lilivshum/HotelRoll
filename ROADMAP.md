# HotelRoll — Roadmap

## Completed

| Feature | Commit | Notes |
|---|---|---|
| Room Move | — | Tap-to-select room move on roll screen |
| Split-Move | 29ddcfc | Long-press action picker: move whole stay or split from current date |
| Users | — | Seeded users, active user in SharedPreferences, switcher in nav drawer |
| Action Confirmation | 4f82c48 | Reusable `ConfirmActionDialog` with active user on all tracked actions |
| History Log | 3c00110 | Per-reservation event log, history screen, status-aware delete dialogs |
| Gantt / Room Calendar | — | Monthly Gantt view, toggle from roll header |
| Save Button | a4e797c | Animates white→blue when there are unsaved changes in edit mode |
| Room Block | dd2ad10 | Long-press empty cell to block/unblock; amber highlight in roll and Gantt |
| Google Sign-In | 1ef5bd5 | Auth wired up in Settings for Drive backup |
| Drive JSON Sync | — | Master pushes JSON on every write (3s debounce); slave pulls on app open; manual buttons in Settings — **needs tablet testing** |

---

## Planned

### Firebase Sync
Replace the current Google Drive JSON sync with Firebase Realtime Database or Firestore.
- Real-time push/pull instead of polling
- `SyncService` interface already in place — swap in `FirebaseSyncService`, nothing else changes
- Adds proper conflict resolution and delta sync

### Guest Arrival Flag
Add `hasArrived: Boolean` to the `Stay` entity.
- Receptionists mark when guests physically arrive at the front desk
- Visual indicator on roll/Gantt (e.g. checkmark or dot)

### Slave Read-Only UI
Disable all write actions in the UI when device is not master.
- Currently only auto-sync is blocked on slave; UI still shows write buttons
- Needs `isMaster` state propagated to all screens

### Edit Users
Allow editing existing user names from the nav drawer.
- Currently users are seeded and read-only

---

## Future Ideas

- **Allo-Algo** — A* algorithm that proposes minimum-move reallocation sequences when the hotel is fully booked (needs `roomType` + `capacity` fields on `RoomEntity` first)
- **Tariff Management** — Seasonal/weekend rates screen instead of hardcoded defaults
- **Reporting / Export** — Daily occupancy, revenue summary, CSV or PDF export
