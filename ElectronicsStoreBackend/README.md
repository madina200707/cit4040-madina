# Electronics Store Backend

CIT4040 Backend Development course project, KIMEP University.

A Spring Boot REST API for an electronics shop. Customers browse the catalogue, write reviews,
fill a cart, place orders and pay for them through a simulated payment gateway. Staff manage
the catalogue, promo codes and move orders through their statuses. There is no front end:
the API is the product, and the Postman collection in `postman/` demonstrates it.

**Stack:** Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security with JWT (jjwt),
Bean Validation, PostgreSQL 15 in Docker, Flyway migrations, Lombok.

---

## 1. Run it from a clean clone

You need Docker Desktop, JDK 21 and Maven (or IntelliJ IDEA, which has Maven built in).

```bash
# 1. secrets: copy the example file and, if you like, change the values
cp .env.example .env          # Windows PowerShell: copy .env.example .env

# 2. start PostgreSQL (data is kept in the postgres_data volume)
docker compose up -d

# 3. build and run the tests (the tests need no database)
mvn clean install

# 4. start the API on http://localhost:8080
mvn spring-boot:run
```

In IntelliJ: open the `ElectronicsStoreBackend` folder, wait for Maven to import, then run
`ShopApplication`. The working directory must be the `ElectronicsStoreBackend` folder, because
that is where the application reads `.env` from.

On start-up Flyway creates the schema (`V1`), adds indexes and constraints (`V2`) and loads the
seed data (`V3`). Hibernate then checks every entity against the schema (`ddl-auto: validate`).

**Start again from an empty database:**

```bash
docker compose down -v        # -v deletes the volume and every row in it
docker compose up -d
```

### Secrets

No password or key is written in a committed file. `application.yml` and `docker-compose.yml`
read `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET` from the environment or from `.env`, and `.env`
is in `.gitignore`. `JWT_SECRET` must be at least 32 characters, otherwise the application
refuses to start.

---

## 2. Seeded accounts and data

| Email | Password | Role |
|---|---|---|
| admin@shop.local | Admin123! | ADMIN |
| manager@shop.local | Manager123! | MANAGER |
| customer@shop.local | Customer123! | CUSTOMER |
| customer2@shop.local | Customer123! | CUSTOMER |

Passwords are stored only as BCrypt hashes.

The seed also contains 6 categories (a tree under Electronics), 3 brands, 20 products with
images, variants and specifications, 4 delivery options, 5 promo codes, 6 orders in different
statuses (PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED), payments, 10 reviews and a
wishlist.

| Promo code | Rule |
|---|---|
| WELCOME10 | 10% off, at most 30 000 |
| SAVE5000 | 5 000 off orders from 50 000 |
| FREESHIP | free shipping on orders from 20 000 |
| AUDIO15 | 15% off products in the Audio category only |
| EXPIRED2025 | expired, used to show that it is refused |

---

## 3. Postman

1. Import `postman/ElectronicsStore.postman_collection.json` and
   `postman/ElectronicsStore.postman_environment.json`.
2. Select the environment **Electronics Store - local**. Only `baseUrl` may need changing.
3. Run the folder **01 Auth** first. It logs in the seeded accounts and stores `accessToken`,
   `refreshToken`, `adminToken`, `managerToken` and `customer2Token` in the environment.
4. Then run any other folder, or the whole collection.

Every request has a test on the status code. There are deliberate failure cases for
validation (400), not found (404), forbidden (403), no token (401), conflicts (409) and
declined payments (402). The payment folder is slower because the simulated gateway waits
1 to 3 seconds per payment, as the brief asks.

---

## 4. Endpoints

All lists are paginated: `?page=0&size=20&sort=field,asc`. A list response looks like
`{ "content": [...], "page": 0, "size": 20, "totalElements": 42, "totalPages": 3, "first": true, "last": false }`.

| Area | Endpoints | Access |
|---|---|---|
| Auth `/api/auth` | `POST /register`, `POST /login`, `POST /refresh`, `POST /logout`, `POST /change-password` | public, change-password needs a token |
| Users `/api/users` | `GET /me`, `PUT /me`, `PUT /me/password`, `DELETE /me`, `GET POST /me/addresses`, `GET PUT DELETE /me/addresses/{id}`, `PATCH /me/addresses/{id}/default` | customer |
| Products `/api/products` | `GET /` (filters `category`, `brand`, `minPrice`, `maxPrice`, `inStock`, `rating`, `search`), `GET /{id}`, `GET /{id}/variants`, `GET /{id}/related`, `GET /featured`, `GET /new-arrivals`, `GET /best-sellers`, `GET /search?q=` | public |
| Categories `/api/categories` | `GET /` (tree), `GET /{id}`, `GET /{id}/products`, `GET /{slug}` | public |
| Brands `/api/brands` | `GET /`, `GET /{id}`, `GET /{id}/products` | public |
| Reviews `/api/products/{productId}/reviews` | `GET /`, `GET /summary`, `POST /`, `PUT /{id}`, `DELETE /{id}` | reading public, writing customer |
| Cart `/api/cart` | `GET /`, `POST /items`, `PUT /items/{itemId}`, `DELETE /items/{itemId}`, `DELETE /`, `POST /apply-promo`, `DELETE /promo`, `POST /merge` | customer, or a guest with an `X-Session-Id` header |
| Wishlist `/api/wishlist` | `GET /`, `POST /{productId}`, `DELETE /{productId}`, `POST /{productId}/move-to-cart` | customer |
| Orders `/api/orders` | `POST /`, `GET /`, `GET /{id}`, `GET /{orderNumber}`, `PUT /{id}/cancel`, `POST /{id}/reorder` | customer, own orders only |
| Payments `/api/payments` | `POST /process`, `GET /{orderId}/status`, `GET /methods` | customer, methods public |
| Delivery `/api/delivery` | `GET /methods`, `POST /calculate` | public |
| Promo `/api/promo` | `POST /validate` | customer |
| Admin products `/api/admin/products` | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` (soft), `PATCH /{id}/stock`, `PATCH /{id}/activate`, `PATCH /{id}/deactivate`, `POST /{id}/variants`, `PUT DELETE /{id}/variants/{variantId}` | ADMIN |
| Admin categories `/api/admin/categories` | `POST /`, `PUT /{id}`, `DELETE /{id}`, `PUT /reorder` | ADMIN |
| Admin brands `/api/admin/brands` | `POST /`, `PUT /{id}`, `DELETE /{id}` | ADMIN |
| Admin orders `/api/admin/orders` | `GET /` (filters `status`, `customerEmail`, `from`, `to`), `GET /{id}`, `PUT /{id}/status`, `PUT /{id}/tracking`, `PUT /{id}/note` | ADMIN, MANAGER |
| Admin promo codes `/api/admin/promo-codes` | `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}` | ADMIN |
| Admin delivery options `/api/admin/delivery-options` | `GET /`, `POST /`, `PUT /{id}`, `DELETE /{id}` | ADMIN |
| Admin reviews `/api/admin/reviews` | `GET /?status=`, `PUT /{id}` (approve or reject, reply) | ADMIN, MANAGER |

`POST` answers `201 Created` with a `Location` header, `DELETE` answers `204 No Content`.

---

## 5. Business rules

### Order total (`OrderTotalsCalculator`)

```
subtotal      = sum of priceAtPurchase * quantity
discount      = promo code discount, capped at maxDiscountAmount
shippingCost  = 0 if subtotal >= freeThreshold (or a FREE_SHIPPING code), else the option's basePrice
bonusUsed     = requested points, capped at 50% of (subtotal - discount)
totalAmount   = subtotal - discount + shippingCost - bonusUsed * 0.01
```

The bonus system is an optional module that is not built, so `bonusUsed` is always 0 here;
the cap is still implemented and unit tested.

### Order statuses (`OrderStatusTransitions`, `OrderStatusManager`)

```
PENDING    -> CONFIRMED, CANCELLED
CONFIRMED  -> PAID, CANCELLED
PAID       -> PROCESSING, REFUNDED
PROCESSING -> SHIPPED, CANCELLED
SHIPPED    -> IN_TRANSIT
IN_TRANSIT -> DELIVERED
DELIVERED  -> REFUNDED   (only through a return, so the admin endpoint refuses it)
CANCELLED, REFUNDED -> nothing
```

Any other transition answers `409`. Stock follows the status:

* placing an order reserves stock (`available = stockQuantity - reservedQuantity`) and empties
  the cart, in one transaction;
* cancelling releases the reservation, and refunds a completed payment;
* shipping turns the reservation into a sale: stock and reserved stock both go down.

### Payment simulation (`PaymentGatewaySimulator`)

| Card number | Result |
|---|---|
| 4111111111111111 | always succeeds |
| 4000000000000002 | declined: insufficient funds |
| 4000000000000069 | network error |
| 4000000000000077 | suspected fraud |
| anything else | 90% success, 10% random failure |

The card number is checked with the Luhn algorithm before anything else. The gateway waits 1 to
3 seconds. Only the last four digits are stored. A payment moves the order from PENDING to
CONFIRMED; a successful payment moves it to PAID, a failed one leaves it CONFIRMED and answers
`402` with the error code, so the customer can try again. Cash on delivery leaves the payment
PENDING until staff set the order to PAID.

### Other rules

* Prices: the current price is always charged. A discount price counts only between its start
  and end dates. The cart shows `priceAtAdd`, `currentPrice` and `priceChanged`.
* Promo codes: active, inside their dates, not used up, per-user limit, minimum order amount,
  and limited to products or categories (a category also covers its subcategories).
* Reviews: one per customer per product. `isVerifiedPurchase` is worked out by the server from a
  DELIVERED order. `averageRating` and `reviewCount` are recalculated after every change.
* Guest cart: without a token the cart is identified by the `X-Session-Id` header.
  `POST /api/cart/merge` moves it into the user's cart; duplicate lines are merged and their
  quantities added, within the stock.
* Soft delete for products, variants, brands, categories, promo codes and delivery options:
  old orders still point at them.

---

## 6. Security

* Access tokens are JWTs signed with HMAC-SHA (15 minutes). Refresh tokens are random strings
  stored in the `refresh_tokens` table (7 days); a refresh token is used once and replaced,
  and logout revokes it.
* Public: catalogue reads, delivery methods and calculation, payment methods, register, login,
  refresh and logout. `/api/admin/**` needs ADMIN or MANAGER; catalogue and promo code admin
  endpoints need ADMIN (`@PreAuthorize`).
* No token or an invalid one: `401`. A valid token with the wrong role: `403`.
* Ownership: every customer query filters by the logged-in user, and another customer's order
  or payment answers `403`.
* No entity is used in a controller; requests and responses are DTOs (Java records), and no
  response contains a password, a hash, a full card number or the internal admin note.

### Error format

Every error has the same shape (`GlobalExceptionHandler`, `JsonSecurityErrorHandler`):

```json
{
  "timestamp": "2026-11-18T09:30:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "No product with id 9999",
  "path": "/api/products/9999",
  "details": {}
}
```

For a validation failure `details` has one entry per failing field. No stack trace, SQL or
table name is ever returned.

---

## 7. Database

* `src/main/resources/db/migration`: `V1__create_tables.sql`, `V2__add_indexes_and_constraints.sql`,
  `V3__seed_data.sql`. The schema is reproducible from an empty database.
* Every foreign key and every column used for filtering or sorting has an index.
* ER diagram: `docs/er-diagram.png` (also `docs/er-diagram.pdf`, and the Graphviz source
  `docs/er-diagram.dot`).

---

## 8. Project structure

```
src/main/java/com/example/shop/
├── ShopApplication.java
├── config/            SecurityConfig
├── controller/
│   ├── admin/         staff endpoints
│   └── customer/      public and customer endpoints
├── dto/
│   ├── request/       request records with validation
│   └── response/      response records, PageResponse, ErrorResponse
├── exception/         one exception per error status, GlobalExceptionHandler
├── mapper/            hand-written entity -> DTO mappers
├── model/
│   ├── entity/        16 entities (+ the embedded ShippingAddress)
│   └── enums/
├── repository/        Spring Data repositories
├── security/          JWT provider and filter, user details, 401/403 handler
├── service/           service interfaces, order rules, totals calculator
│   └── impl/          service implementations, payment simulator
├── util/              prices, Luhn check, slugs, JSON columns
└── validator/         @UniqueEmail, @ValidPassword
```

Unit tests (`src/test`) cover the status transition table, the order total formula, the Luhn
check and the slug generator.

---

## 9. Optional modules

None of the ten bonus modules is claimed as finished. Review moderation
(`/api/admin/reviews`) is included as a small extra.
