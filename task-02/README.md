### Info

---

Hosts enabled for CORS: `localhost:3000` and `127.0.0.1:3000`.
Serve `www` folder to test CORS.

Docker image: `https://hub.docker.com/r/pawelduraj/android-t2`

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
  "price": "number",
  "categoryId": "number"
}
```

Category:

```json
{
  "name": "string",
  "description": "string",
  "keywords": "string"
}
```

### Build, run, start, stop

---

```bash
docker build -t pawelduraj/android-t2:1.0.0 .
docker run -it --name android-t2 -p 8080:8080 pawelduraj/android-t2:1.0.0
docker start -i android-t2
docker stop android-t2
```
