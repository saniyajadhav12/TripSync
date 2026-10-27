# Demo data

TripSync can seed a demo account with sample trips so you can try the app without entering data by hand.
The seeder only runs when the Spring profile `demo` is active. It never runs in the default profile or in tests.

## Run the backend with demo data

You need a local MySQL database and a JWT secret of at least 32 bytes. From the repository root:

```sh
# Create an empty database once (adjust the user if you don't use root)
mysql -u root -e "CREATE DATABASE IF NOT EXISTS tripsync_dev"

SPRING_PROFILES_ACTIVE=demo \
DB_URL=jdbc:mysql://localhost:3306/tripsync_dev \
JWT_SECRET="$(openssl rand -base64 48)" \
./mvnw spring-boot:run
```

`DB_USERNAME` and `DB_PASSWORD` default to `root` and an empty password; set them if your MySQL user differs.

Then start the frontend in a second terminal:

```sh
cd frontend
npm ci
npm run dev
```

Open http://localhost:5173 and log in with:

| Username | Password |
|---|---|
| `demo` | `tripsync-demo` |

These credentials are public and meant for local demos only. Never enable the `demo` profile on a shared or real deployment.

## What gets created

- User **Demo Traveler** (`demo@example.com`), password stored as a BCrypt hash.
- Three trips with dates in the near future, relative to the day you start the app:
  - **Kyoto, Japan**: 3 activities, 2 accommodations (hotel, hostel)
  - **Moab, Utah**: 3 activities, 2 accommodations (camping, resort)
  - **Lisbon, Portugal**: 3 activities, 1 accommodation (guesthouse)

  They are created in this order because the Home page picks its sample photos by position,
  so the cards show a pagoda for Kyoto, a red-rock arch for Moab and a city skyline for Lisbon.

Seeding is idempotent: if the `demo` user already exists, the seeder logs a message and does nothing,
so restarting with the profile never duplicates data. To start over, drop and recreate the database.
