# felippp-p5-pbo2026-springboot-cashflow

Aplikasi web **Cashflow (Catatan Keuangan)** berbasis **Spring Boot 4.1.2**, **Maven**, **Vaadin 25.3.4**, dan **JDBC (PostgreSQL)**, dengan arsitektur **Clean Architecture** (domain, usecase, adapter, framework). Studi kasus Praktikum 5 PBO 2026 (JDBC).

> Ganti `felippp` (folder, artifact, package `org.delcom.felipppspringbootcashflow`) dengan username Anda — lihat `rename-username.sh`.

## Fitur

- Register, login, logout; sesi bertahan setelah refresh (`AuthSession` + cookie `cashflow_uid` + `LOGGED_OUT_KEY`); password SHA-256
- Role **ADMIN** / **USER**; admin di-seed dari `ROOT_USERNAME` / `ROOT_PASSWORD`
- Transaksi **Pemasukan (INCOME)** / **Pengeluaran (EXPENSE)** per pengguna, bukti gambar opsional (`proofPath`), klik bukti → modal zoom
- Cari keterangan (case-insensitive), urutkan (header kolom + 4 `SortOption`: jumlah naik/turun, pemasukan dulu, pengeluaran dulu)
- Kartu ringkasan: total transaksi, pemasukan, pengeluaran, saldo (format `Rp 1,000,000.00`)
- Tambah / ubah (klik 2x baris) / hapus (dengan konfirmasi)
- Profil: nama, password, foto
- ADMIN: daftar pengguna + transaksi pengguna lain (read-only); admin juga punya cashflow sendiri

## Rute

| Kategori | Route | View | Akses |
|---|---|---|---|
| Public | `/login`, `/register` | LoginView, RegisterView | Anonim (sudah login → `/`) |
| Public | `/uploads/**`, `/icons/**`, `styles.css` | static | Semua |
| Protected | `/` | FinanceView | USER & ADMIN |
| Protected | `/profile` | ProfileView | USER & ADMIN |
| Admin | `/users`, `/user-transactions/{id}` | UsersView, UserTransactionsView | ADMIN (USER → `/`) |

## Menjalankan

1. Buat database sebelum run:
   ```sql
   CREATE DATABASE pbo2026_cashflow;
   ```
2. Salin `.env.example` menjadi `.env` lalu sesuaikan `DB_*`, `ROOT_*`, `UPLOAD_DIR`.
3. Development:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
4. Build JAR (wajib profil production Vaadin):
   ```powershell
   .\mvnw.cmd clean package -Pproduction
   java -jar target\felippp-p5-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
   ```
5. Uji + coverage Jacoco 100% (view/auth dikecualikan): `.\mvnw.cmd clean verify`

Login admin default: `admin` / `admin` (dari `.env`).

## Struktur

```
src/main/java/org/delcom/felipppspringbootcashflow/
├── App.java, DotEnv.java
├── config/AppConfig.java               # composition root (@Bean)
├── domain/
│   ├── PasswordHasher.java
│   ├── entity/       Transaction, TransactionType, Balance, User, Role, SortOption
│   ├── exception/    DomainException, EntityNotFoundException, ValidationException
│   └── repository/   IRepository<T,ID>, ITransactionRepository, IUserRepository
├── usecase/          AuthUseCase, FinanceUseCase, UserUseCase
├── adapter/
│   ├── presenter/    FinancePresenter
│   ├── repository/   InMemory*, Jdbc*, SchemaInitializer
│   └── storage/      FileStorage
└── framework/
    ├── auth/         AuthSession
    └── view/         Login, Register, MainLayout, Finance, Profile, Users, UserTransactions, ...
```

Dependency rule: `domain ← usecase ← adapter / framework`.
