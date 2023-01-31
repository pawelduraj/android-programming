### Info

---

Server is running on port 5000. Server should be configured in the `/src/main/resources/application.conf` file.
Look at the `application.conf` file for more information. Remember to not commit sensitive information to the
repository.

### Endpoints

---

| URL                  | HTTP     | Payload    | Description              |
|----------------------|----------|------------|--------------------------|
| `/auth/github`       | `POST`   | `token`    | Authenticate with GitHub |
| `/auth/google`       | `POST`   | `token`    | Authenticate with Google |
| `/auth/login`        | `POST`   | `login`    | Login a user             |
| `/auth/register`     | `POST`   | `register` | Register a new user      |
| `/categories`        | `POST`   | `category` | Create a new category    |
| `/categories`        | `GET`    |            | Get all categories       |
| `/categories/:id`    | `GET`    |            | Get a single category    |
| `/categories/:id`    | `PUT`    | `category` | Update a category        |
| `/categories/:id`    | `DELETE` |            | Delete a category        |
| `/contact`           | `POST`   | `message`  | Send message             |
| `/me/orders`         | `GET`    |            | Get my orders            |
| `/me/order-details`  | `GET`    |            | Get my order details     |
| `/payments/buy`      | `POST`   | `buy`      | Creates new order        |
| `/payments/payu`     | `POST`   | `pay`      | Pay using PayU           |
| `/payments/stripe`   | `POST`   | `pay`      | Pay using Stripe         |
| `/payments/finalize` | `POST`   | `pay`      | Finalize payment         |
| `/products`          | `POST`   | `product`  | Create a new product     |
| `/products`          | `GET`    |            | Get all products         |
| `/products/:id`      | `GET`    |            | Get a single product     |
| `/products/:id`      | `PUT`    | `product`  | Update a product         |
| `/products/:id`      | `DELETE` |            | Delete a product         |
| `/users`             | `POST`   | `user`     | Create a new user        |
| `/users`             | `GET`    |            | Get all users            |
| `/users/:id`         | `GET`    |            | Get a single user        |
| `/users/:id`         | `PUT`    | `user`     | Update a user            |
| `/users/:id`         | `DELETE` |            | Delete a user            |

### Payloads

---

Token:

```json
{
  "token": "string"
}
```

Login:

```json
{
  "email": "string",
  "password": "string"
}
```

Register:

```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

Category:

```json
{
  "name": "string"
}
```

Message:

```json
{
  "message": "string"
}
```

Buy:

```json
[
  {
    "productId": "number",
    "quantity": "number"
  }
]
```

Pay:

```json
{
  "orderId": "number"
}
```

Product:

```json
{
  "name": "string",
  "categoryId": "number",
  "price": "number",
  "inStock": "number",
  "description": "string"
}
```

User:

```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "admin": "boolean"
}
```

### Authentication

---

All endpoints except

- `/auth/github`
- `/auth/google`
- `/auth/login`
- `/auth/register`

require a valid JWT token in the `Authorization` header. The token can be obtained by logging in.
`Authorization` header should be in the following format: `Bearer <token>`.

> Some endpoints require the user to be an admin.
