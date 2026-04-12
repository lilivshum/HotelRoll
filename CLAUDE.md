# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## About the Project

HotelRoll is an Android hotel property management app for room booking and reservation tracking, with a calendar-style "roll" view for visualizing daily occupancy. The main goal of the app is help manage how to assign rooms to guests, and it includes an interface to be able to make changes and see room statuses in a easy sleek and simple way. 

## Build Commands

```bash
# Build
./gradlew build
./gradlew assembleRelease

# Run tests (instrumentation — requires connected device/emulator)
./gradlew androidTest

# Run unit tests
./gradlew test

# Lint
./gradlew lint

# Install on connected device
./gradlew installDebug

# Clean
./gradlew clean
```

## Architecture

**Stack:** Kotlin, Jetpack Compose, Room, Navigation Compose, Material3, Coroutines + StateFlow, MVVM + Repository pattern.

**Layers (top to bottom):**

1. **UI (`ui/`)** — Compose screens + ViewModels. Each feature folder has a `*Screen.kt` and `*ViewModel.kt`. ViewModels use factory pattern and receive `HotelRepository` via `HotelApplication`.
2. **Repository (`repository/HotelRepository.kt`)** — Single source of truth. All database reads/writes go through here. Uses `withTransaction` for atomic operations (e.g., `createReservationWithStay`).
3. **Domain (`domain/HotelManager.kt`)** — Pure business rule validation (date ranges, guest counts, overlap detection). Called by the repository before writes.
4. **Data (`data/`)** — Room database (`hotel.db`, version 8), 3 entities + 3 DAOs.

**Core data model:**
- `Reservation` (1) → (many) `Stay` (cascade delete on reservation)
- `Stay` → `RoomEntity` (via `roomId`)
- `Stay` tracks: check-in/out dates, occupancy, tariff, currency (CRC/USD), status (PENDING/CONFIRMED), and tariff type (NET/WITH_TAX)

**Database notes:**
- Uses `fallbackToDestructiveMigration()` — schema changes wipe data.
- Type converters in `Converters.kt` handle `LocalDate` and enums.
- 45 rooms are seeded on first launch via `DatabaseCallback.kt` + `DefaultRooms.kt`.
- Core Library Desugaring is enabled for `LocalDate` on API < 26.

## Navigation

Routes are defined as a sealed class in `ui/navigation/HotelRoute.kt` and wired in `HotelNavGraph.kt`. The app uses a navigation drawer (phone) or permanent sidebar at 320dp (tablet/medium+ window size class).

Key routes:
- `roll` — main calendar screen
- `reservation/{reservationId}` — reservation detail
- `stay/{stayId}/roomNumber/{roomNumber}/reservationName/{reservationName}` — stay detail
- `roomId/{roomId}/roomNumber/{roomNumber}/date/{date}/mode/{mode}?stayId={stayId}` — create/edit stay
- `reservation/create` — create reservation

## Key Behaviors to Know

- **Overlap detection** happens in `HotelManager` before any room assignment. A checkout date equal to another stay's check-in date is allowed (back-to-back bookings).
- **Room capacity validation is disabled** (commented out in `HotelManager`).
- **`HotelApplication`** is the singleton entry point — it creates the database instance and repository, which are injected into ViewModels via their factories.
- The roll/calendar query (`getRoomRoll`) is a complex join across rooms and stays for a given date — it lives in `StayDao`.
