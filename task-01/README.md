Docker image is available at:
https://hub.docker.com/r/pawelduraj/android-t1

---

Build, run, start, stop:

```bash
docker build -t pawelduraj/android-t1:1.0.0 .
docker run -it --name android-t1 pawelduraj/android-t1:1.0.0
docker start -i android-t1
docker stop android-t1
```

---

Compose up, open shell:

```bash
docker compose -p android-t1 up
docker exec -it android-t1-main bash
```
