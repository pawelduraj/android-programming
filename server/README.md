### Info

---

Server is running on port 5000.

### Endpoints

---

| URL               | HTTP     | Payload    | Description           |
|-------------------|----------|------------|-----------------------|
| `/products`       | `POST`   | `product`  | Create a new product  |
| `/products`       | `GET`    |            | Get all products      |
| `/products/:id`   | `GET`    |            | Get a single product  |
| `/products/:id`   | `PUT`    | `product`  | Update a product      |
| `/products/:id`   | `DELETE` |            | Delete a product      |
| `/categories`     | `POST`   | `category` | Create a new category |
| `/categories`     | `GET`    |            | Get all categories    |
| `/categories/:id` | `GET`    |            | Get a single category |
| `/categories/:id` | `PUT`    | `category` | Update a category     |
| `/categories/:id` | `DELETE` |            | Delete a category     |

### Payloads

---

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

Category:

```json
{
  "name": "string"
}
```
