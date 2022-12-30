### Info

---

This is a simple shop app for android. To proper work you need to run the server app. You can find
it in `/server` directory in this repository. How to configure and run the server app is described
in `/server/README.md` file. Server is filled with some data. To login you can use one of the
following accounts:

- email: `admin@example.com`, password: `admin`
- email: `user@example.com`, password: `user`

To view list of products you need to be logged in.

### Config

---

To configure app you need to generate keystore file. To do this you can use `keytool` command which
is included in JDK bin directory. To generate keystore file run the following command:

```bash
keytool -genkey -v -keystore "C:\dev.keystore" -alias key -keyalg RSA -keysize 2048 -validity 10000
```

Use password `keykey` and alias `key`. If another data is used then you need to change it in
`/app/build.gradle` file.

It is also necessary to config app in `/app/src/main/java/com/example/app/dao/Config.kt` file.
API_URL should be set to the server url.

To use sign in with github you need to create an oauth app in github settings. After that you need
to set GITHUB_CLIENT_ID. To create github oauth app go to `https://github.com/settings/developers`.

To use sign in with google you need to create an oauth app in google console. To create google oauth
app go to `https://console.cloud.google.com/apis/credentials` and create 2 oauth id clients (one for
android and one for web). Use SHA1 fingerprint of your keystore file to configure android client.
After that you need to set GOOGLE_CLIENT_ID with web client id.
